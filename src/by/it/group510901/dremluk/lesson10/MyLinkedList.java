package by.it.group510901.dremluk.lesson10;

import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class MyLinkedList<E> implements Deque<E> {

    // вагончик
    private static class Node<E> {
        E item;         // вещь внутри
        Node<E> next;
        Node<E> prev;

        Node(Node<E> prev, E element, Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }

    private Node<E> first;
    private Node<E> last;
    private int size = 0;

    /////////////////////////////////////////////////////////////////////////
    //////               Обязательные к реализации методы             ///////
    /////////////////////////////////////////////////////////////////////////

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean add(E element) {
        addLast(element);
        return true;
    }
    @Override
    public void addFirst(E element) {
        Node<E> f = first;                              // запомнили старый первый вагон
        Node<E> newNode = new Node<>(null, element, f);  // новый: слева никого, справа старый первый
        first = newNode;                                // новый теперь первый
        if (f == null) {
            last = newNode;
        } else {
            f.prev = newNode;   // старый первый цепляется назад к новому
        }
        size++;
    }

    @Override
    public void addLast(E element) {
        Node<E> l = last;                               // запомнили старый последний
        Node<E> newNode = new Node<>(l, element, null); // новый: слева старый последний, справа никого
        last = newNode;                                 // новый теперь последний
        if (l == null) {
            first = newNode;
        } else {
            l.next = newNode;
        }
        size++;
    }

    // Отцепить вагон по номеру
    public E remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(); // такого номера нет — ошибка
        }
        return unlink(node(index));   // нашли вагон и отцепили
    }

    // Найти вещь o и отцепить её вагон
    @Override
    public boolean remove(Object o) {
        if (o == null) {
            // идём по поезду: начинаем с первого, переходим в следующий, пока вагоны не кончатся
            for (Node<E> x = first; x != null; x = x.next) {
                if (x.item == null) {
                    unlink(x);              // нашли — отцепили
                    return true;
                }
            }
        } else {                            // ищем настоящую вещь — сравниваем
            for (Node<E> x = first; x != null; x = x.next) {
                if (o.equals(x.item)) {
                    unlink(x);
                    return true;
                }
            }
        }
        return false;                       // не нашли
    }

    // Посмотреть первую вещь
    @Override
    public E element() {
        return getFirst();
    }

    // Посмотреть первую вещь. Пусто — ошибка
    @Override
    public E getFirst() {
        if (first == null) {
            throw new NoSuchElementException();
        }
        return first.item;
    }

    // Посмотреть последнюю вещь. Пусто — ошибка
    @Override
    public E getLast() {
        if (last == null) {
            throw new NoSuchElementException();
        }
        return last.item;
    }

    // Забрать первую вещь (то же, что pollFirst)
    @Override
    public E poll() {
        return pollFirst();
    }

    // Забрать первую вещь. Пусто — вернём null (без ошибки)
    @Override
    public E pollFirst() {
        if (first == null) {
            return null;
        }
        return unlink(first);   // отцепили первый вагон
    }

    // Забрать последнюю вещь. Пусто — вернём null
    @Override
    public E pollLast() {
        if (last == null) {
            return null;
        }
        return unlink(last);    // отцепили последний вагон
    }

    // Текст вида [a, b, c] — проходим поезд от первого вагона до последнего
    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("["); // удобная "склейка" строк
        Node<E> current = first;                   // встали в первый вагон
        while (current != null) {                  // пока вагоны не кончились
            sb.append(current.item);               // дописали вещь
            if (current.next != null) {            // дальше есть вагон —
                sb.append(", ");                   // ставим запятую
            }
            current = current.next;                // перешли в следующий вагон
        }
        return sb.append("]").toString();
    }

    // Дойти до вагона номер index
    private Node<E> node(int index) {
        if (index < (size >> 1)) {          // size >> 1 — это size / 2. Вагон в первой половине?
            Node<E> x = first;              // идём с головы поезда
            for (int i = 0; i < index; i++) {
                x = x.next;                 // шаг вперёд
            }
            return x;
        } else {                            // вагон во второй половине
            Node<E> x = last;               // идём с хвоста — так короче
            for (int i = size - 1; i > index; i--) {
                x = x.prev;                 // шаг назад
            }
            return x;
        }
    }

    // Отцепить вагон x
    private E unlink(Node<E> x) {
        E element = x.item;         // запомнили вещь, чтобы вернуть
        Node<E> next = x.next;      // сосед справа
        Node<E> prev = x.prev;      // сосед слева

        if (prev == null) {
            first = next;           // x был первым — первым станет сосед справа
        } else {
            prev.next = next;       // левый сосед цепляется к правому
            x.prev = null;          // убрали сцепку у x
        }

        if (next == null) {
            last = prev;            // x был последним — последним станет сосед слева
        } else {
            next.prev = prev;       // правый сосед цепляется к левому
            x.next = null;          // убрали сцепку у x
        }

        x.item = null;              // вагон пустой, его выкинет сборщик мусора
        size--;                     // вагонов -1
        return element;
    }

    /////////////////////////////////////////////////////////////////////////
    //////      Пустышки — нужны только чтобы Java скомпилировала класс  ////
    /////////////////////////////////////////////////////////////////////////
    // Ничего не делают. В комментарии — что они ДОЛЖНЫ делать:

    @Override public boolean offer(E e) { return false; }        // положить в конец
    @Override public boolean offerFirst(E e) { return false; }   // положить в начало
    @Override public boolean offerLast(E e) { return false; }    // положить в конец
    @Override public E remove() { return null; }                 // забрать первую (пусто — ошибка)
    @Override public E removeFirst() { return null; }            // забрать первую (пусто — ошибка)
    @Override public E removeLast() { return null; }             // забрать последнюю (пусто — ошибка)
    @Override public E peek() { return null; }                   // посмотреть первую (пусто — null)
    @Override public E peekFirst() { return null; }              // посмотреть первую (пусто — null)
    @Override public E peekLast() { return null; }               // посмотреть последнюю (пусто — null)
    @Override public boolean removeFirstOccurrence(Object o) { return false; } // удалить первую такую вещь
    @Override public boolean removeLastOccurrence(Object o) { return false; }  // удалить последнюю такую вещь
    @Override public void push(E e) {}                           // стопка тарелок: положить сверху
    @Override public E pop() { return null; }                    // стопка тарелок: снять сверху
    @Override public boolean containsAll(Collection<?> c) { return false; }
    @Override public boolean addAll(Collection<? extends E> c) { return false; }
    @Override public boolean removeAll(Collection<?> c) { return false; }
    @Override public boolean retainAll(Collection<?> c) { return false; }
    @Override public void clear() {}                             // выкинуть всё

    // ОСТОРОЖНО: эти две пустышки ломают сравнение — список не равен даже сам себе.
    // Если их делать не нужно — лучше просто удалить эти 2 строки.
    @Override public boolean equals(Object o) { return false; }
    @Override public int hashCode() { return 0; }

    @Override public boolean isEmpty() { return size == 0; }     // пусто? (этот сделан по-настоящему)
    @Override public boolean contains(Object o) { return false; }       // есть ли o
    @Override public Iterator<E> iterator() { return null; }            // для цикла for (x : list)
    @Override public Iterator<E> descendingIterator() { return null; }  // обход с конца
    @Override public Object[] toArray() { return new Object[0]; }
    @Override public <T> T[] toArray(T[] a) { return a; }
}