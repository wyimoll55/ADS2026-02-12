package by.it.group510901.dremluk.lesson11;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class MyHashSet<E> implements Set<E> {
    private static class Node<E> {                     // узел цепочки
        E value;                                       // значение
        Node<E> next;

        Node(E value, Node<E> next) {                  // конструктор узла
            this.value = value;
            this.next = next;
        }
    }

    private Node<E>[] table;
    private int size;
    private static final int DEFAULT_CAPACITY = 16;

    @SuppressWarnings("unchecked")
    public MyHashSet() {
        this.table = (Node<E>[]) new Node[DEFAULT_CAPACITY]; // массив на 16 корзин
        this.size = 0;
    }

    private int getIndex(Object key) {                 // номер корзины для ключа
        if (key == null) return 0;
        int hash = key.hashCode();
        hash = hash ^ (hash >>> 16);                   // берёми перемешиваем старшие и младшие биты
        return (hash & 0x7FFFFFFF) % table.length;     // убираем знак, берём по модулю
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        for (int i = 0; i < table.length; i++) {       // по всем корзинам
            table[i] = null;                           // обнуляем голову
        }
        size = 0;                                      // элементов ноль
    }

    @Override
    // пусто ли
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean add(E e) {
        if (contains(e)) return false;                 // уже есть — не добавляем
        int index = getIndex(e);                       // номер корзины
        table[index] = new Node<>(e, table[index]);    // вставка в голову цепочки
        size++;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        int index = getIndex(o);                       // номер корзины голова цепочки
        Node<E> current = table[index];
        Node<E> prev = null;                           // предыдущий узел

        while (current != null) {                      // идём по цепочке
            boolean isMatch = (o == null) ? (current.value == null) : o.equals(current.value); // сравнение с учётом
            if (isMatch) {
                if (prev == null) {                    // нашли если голова
                    table[index] = current.next;       // новой головой становится next
                } else {
                    prev.next = current.next;          // перепрыгиваем через current
                }
                size--;
                return true;
            }
            prev = current;                            // сдвигаем prev
            current = current.next;                    // шаг к следующему
        }
        return false;
    }

    @Override
    public boolean contains(Object o) {                // есть ли элемент
        int index = getIndex(o);                       // номер корзины
        Node<E> current = table[index];                // голова цепочки

        while (current != null) {                      // идём по цепочке
            boolean isMatch = (o == null) ? (current.value == null) : o.equals(current.value); // сравнение с учётом null
            if (isMatch) return true;
            current = current.next;                    // шаг вперёд
        }
        return false;                                  // не нашли
    }

    @Override
    public String toString() {
        if (isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        boolean first = true;
        for (int i = 0; i < table.length; i++) {
            Node<E> current = table[i];                // голова цепочки
            while (current != null) {                  // по цепочке
                if (!first) sb.append(", ");
                sb.append(current.value);
                first = false;                         // уже не первый
                current = current.next;
            }
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public Iterator<E> iterator() {                    // обход элементов
        throw new UnsupportedOperationException();     // не реализован
    }

    @Override
    public Object[] toArray() {                        // массив элементов
        throw new UnsupportedOperationException();     // не реализован
    }

    @Override
    public <T> T[] toArray(T[] a) {                    // типизированный массив
        throw new UnsupportedOperationException();     // не реализован
    }

    @Override
    public boolean addAll(Collection<? extends E> c) { // добавить все из c
        throw new UnsupportedOperationException();     // не реализован
    }

    @Override
    public boolean retainAll(Collection<?> c) {        // оставить только из c
        throw new UnsupportedOperationException();     // не реализован
    }

    @Override
    public boolean removeAll(Collection<?> c) {        // удалить все из c
        throw new UnsupportedOperationException();     // не реализован
    }

    @Override
    public boolean containsAll(Collection<?> c) {      // все ли из c есть
        throw new UnsupportedOperationException();     // не реализован
    }
}
