package by.it.group510901.dremluk.lesson10;

import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class MyArrayDeque<E> implements Deque<E> {


    private E[] elements = (E[]) new Object[16];

    private int head = 0;
    private int tail = 0;
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
        ensureCapacity();            
        head = (head - 1 + elements.length) % elements.length; // влево
        elements[head] = element;
        size++;
    }

    @Override
    public void addLast(E element) {
        ensureCapacity();
        elements[tail] = element;
        tail = (tail + 1) % elements.length;    //  вправо
        size++;
    }

    @Override
    public E element() {
        return getFirst();
    }

    // посмотреть первую вещь. Пусто — ошибка
    @Override
    public E getFirst() {
        if (size == 0) throw new NoSuchElementException();
        return elements[head];
    }

    // посмотреть последнюю вещь (не забирая). Пусто — ошибка
    @Override
    public E getLast() {
        if (size == 0) throw new NoSuchElementException();
        return elements[(tail - 1 + elements.length) % elements.length];
    }

    // Забрать первую вещь
    @Override
    public E poll() {
        return pollFirst();
    }

    // Забрать первую вещь
    @Override
    public E pollFirst() {
        if (size == 0) return null;
        E item = elements[head];                // взяли вещь
        elements[head] = null;                  // коробку очистили
        head = (head + 1) % elements.length;    //  вправо
        size--;
        return item;
    }

    // Забрать последнюю вещь
    @Override
    public E pollLast() {
        if (size == 0) return null;
        tail = (tail - 1 + elements.length) % elements.length; // шаг влевотеперь на последней вещи
        E item = elements[tail];                // взяли вещь
        elements[tail] = null;                  // коробку очистили
        size--;
        return item;
    }


    @Override
    public String toString() {
        if (size == 0) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append(elements[(head + i) % elements.length]); // i-я вещь после head (по кругу)
            if (i < size - 1) sb.append(", ");                 // запятая, если не последняя
        }
        return sb.append("]").toString();
    }

    // Если все коробки заняты — делаем круг в 2 раза больше
    @SuppressWarnings("unchecked")
    private void ensureCapacity() {
        if (size == elements.length) {          // места нет?
            E[] newElements = (E[]) new Object[elements.length * 2];
            for (int i = 0; i < size; i++) {
                // перекладываем ПО ПОРЯДКУ, начиная с первой вещи,
                // в новом массиве они лягут подряд с коробки 0
                newElements[i] = elements[(head + i) % elements.length];
            }
            elements = newElements;
            head = 0;                           // первая вещь теперь в коробке 0
            tail = size;                        // свободное место сразу после последней
        }
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
    @Override public void push(E e) {}                           // как стопка тарелок: положить сверху
    @Override public E pop() { return null; }                    // как стопка тарелок: снять сверху
    @Override public boolean remove(Object o) { return false; }  // удалить вещь o
    @Override public boolean containsAll(Collection<?> c) { return false; }
    @Override public boolean addAll(Collection<? extends E> c) { return false; }
    @Override public boolean removeAll(Collection<?> c) { return false; }
    @Override public boolean retainAll(Collection<?> c) { return false; }
    @Override public void clear() {}                             // выкинуть всё

    // ОСТОРОЖНО: эти две пустышки ломают сравнение — очередь не равна даже сама себе.
    // Если их делать не нужно — лучше просто удалить эти 2 строки.
    @Override public boolean equals(Object o) { return false; }
    @Override public int hashCode() { return 0; }

    @Override public boolean isEmpty() { return size == 0; }     // пусто? (этот сделан по-настоящему)
    @Override public boolean contains(Object o) { return false; }       // есть ли o
    @Override public Iterator<E> iterator() { return null; }            // для цикла for (x : deque)
    @Override public Iterator<E> descendingIterator() { return null; }  // обход с конца
    @Override public Object[] toArray() { return new Object[0]; }
    @Override public <T> T[] toArray(T[] a) { return a; }
}