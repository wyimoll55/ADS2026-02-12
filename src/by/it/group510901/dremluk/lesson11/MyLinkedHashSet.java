package by.it.group510901.dremluk.lesson11;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

public class MyLinkedHashSet<E> implements Set<E> {

    private static class Node<E> {
        E value;                                       // значение
        Node<E> next;
        Node<E> insertionNext;
        Node<E> insertionPrev;                         // предыдущий ПО ПОРЯДКУ вставки

        Node(E value) {                                // конструктор: только значение
            this.value = value;
        }
    }

    private Node<E>[] table;
    private Node<E> head;                              // первый по порядку вставки
    private Node<E> tail;
    private int size;                                  // число элементов
    private static final int DEFAULT_CAPACITY = 16;

    @SuppressWarnings("unchecked")
    public MyLinkedHashSet() {                         // конструктор
        this.table = (Node<E>[]) new Node[DEFAULT_CAPACITY];
        this.size = 0;                                 // пусто
        this.head = null;                              // порядка ещё нет
        this.tail = null;
    }

    private int getIndex(Object key) {
        if (key == null) return 0;
        int hash = key.hashCode();                     // берём hashCode
        hash = hash ^ (hash >>> 16);                   // перемешиваем старшие и младшие биты
        return (hash & 0x7FFFFFFF) % table.length;     // убираем знак, берём по модулю
    }

    @Override
    public boolean add(E e) {
        if (contains(e)) return false;                 // уже есть — не добавляем
        int index = getIndex(e);                       // номер корзины
        Node<E> newNode = new Node<>(e);               // создаём узел

        newNode.next = table[index];                   // новый указывает на старую голову корзины
        table[index] = newNode;

        if (tail == null) {                            // порядок пуст?
            head = newNode;
            tail = newNode;
        } else {                                       // порядок уже есть
            tail.insertionNext = newNode;              // старый хвост → новый
            newNode.insertionPrev = tail;
            tail = newNode;                            // новый стал хвостом
        }

        size++;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        int index = getIndex(o);
        Node<E> current = table[index];                // голова цепочки корзины
        Node<E> prevInBucket = null;                   // предыдущий В КОРЗИНЕ

        while (current != null) {                      // идём по цепочке корзины
            boolean isMatch = (o == null) ? (current.value == null) : o.equals(current.value);
            if (isMatch) {                             // нашли
                // убираем из корзины
                if (prevInBucket == null) {            // был головой корзины
                    table[index] = current.next;       // новой головой становится next
                } else {                               // был в середине/хвосте
                    prevInBucket.next = current.next;  // перепрыгиваем
                }

                // убираем из списка порядка
                if (current.insertionPrev != null) {   // был не первым в порядке
                    current.insertionPrev.insertionNext = current.insertionNext;
                } else {                               // был первым в порядке
                    head = current.insertionNext;      // новым первым становится следующий
                }

                if (current.insertionNext != null) {   // был не последним в порядке
                    current.insertionNext.insertionPrev = current.insertionPrev;
                } else {                               // был последним в порядке
                    tail = current.insertionPrev;      // новым последним становится предыдущий
                }

                // обнуляем ссылки у удалённого
                current.next = null;
                current.insertionNext = null;
                current.insertionPrev = null;

                size--;
                return true;
            }
            prevInBucket = current;                    // сдвигаем prev в корзине
            current = current.next;                    // шаг вперёд по корзине
        }
        return false;
    }

    @Override
    public boolean contains(Object o) {
        int index = getIndex(o);                       // номер корзины
        Node<E> current = table[index];                // голова цепочки

        while (current != null) {                      // идём по цепочке
            boolean isMatch = (o == null) ? (current.value == null) : o.equals(current.value);
            if (isMatch) return true;                  // нашли
            current = current.next;                    // шаг вперёд
        }
        return false;                                  // не нашли
    }

    @Override
    public int size() {                                // вернуть количество
        return size;
    }

    @Override
    public boolean isEmpty() {                         // пусто ли
        return size == 0;
    }

    @Override
    public void clear() {                              // очистить всё
        for (int i = 0; i < table.length; i++) {       // по всем корзинам
            table[i] = null;                           // обнуляем головы
        }
        head = null;                                   // порядок тоже пуст
        tail = null;
        size = 0;
    }

    @Override
    public String toString() {                         // печать В ПОРЯДКЕ ВСТАВКИ
        if (isEmpty()) return "[]";                    // пусто
        String result = "[";                           // начинаем строку
        Node<E> current = head;                        // идём от первого по порядку
        boolean first = true;                          // флаг первого
        while (current != null) {                      // пока есть узлы
            if (!first) result += ", ";                // запятая, если не первый
            result += current.value;                   // дописываем значение
            first = false;                             // уже не первый
            current = current.insertionNext;           // шаг по порядку вставки
        }
        result += "]";                                 // закрываем
        return result;
    }

    @Override
    public boolean containsAll(Collection<?> c) {      // все ли элементы c есть
        if (c == null) throw new NullPointerException(); // c не передали
        for (Object item : c) {                        // по всем элементам c
            if (!contains(item)) return false;         // хоть одного нет — false
        }
        return true;                                   // все есть
    }

    @Override
    public boolean addAll(Collection<? extends E> c) { // добавить все из c
        if (c == null) throw new NullPointerException();
        boolean modified = false;                      // флаг «что-то добавили»
        for (E item : c) {                             // по всем элементам c
            if (add(item)) modified = true;            // добавили — ставим флаг
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {        // удалить все из c
        if (c == null) throw new NullPointerException();
        boolean modified = false;
        Node<E> current = head;                        // идём по порядку вставки
        while (current != null) {
            Node<E> nextNode = current.insertionNext;  // запоминаем следующий ДО удаления
            if (c.contains(current.value)) {           // этот есть в c — удаляем
                remove(current.value);
                modified = true;
            }
            current = nextNode;                        // идём дальше
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {        // оставить только из c
        if (c == null) throw new NullPointerException();
        boolean modified = false;
        Node<E> current = head;                        // идём по порядку вставки
        while (current != null) {
            Node<E> nextNode = current.insertionNext;  // запоминаем следующий ДО удаления
            if (!c.contains(current.value)) {          // этого НЕТ в c — удаляем
                remove(current.value);
                modified = true;
            }
            current = nextNode;                        // идём дальше
        }
        return modified;
    }

    @Override
    public Iterator<E> iterator() {                    // обходчик (анонимный класс)
        return new Iterator<E>() {
            private Node<E> currentNode = head;        // начинаем с первого по порядку

            @Override
            public boolean hasNext() {                 // есть ли ещё
                return currentNode != null;
            }

            @Override
            public E next() {                          // взять следующий
                if (!hasNext()) {                      // кончились
                    throw new NoSuchElementException();
                }
                E value = currentNode.value;           // запомнили значение
                currentNode = currentNode.insertionNext; // сдвинулись по порядку
                return value;
            }

            @Override
            public void remove() {                     // удалять через iterator нельзя
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public Object[] toArray() {                        // массив элементов
        throw new UnsupportedOperationException();     // не реализован
    }

    @Override
    public <T> T[] toArray(T[] a) {                    // типизированный массив
        throw new UnsupportedOperationException();     // не реализован
    }
}