package by.it.group510901.dremluk.lesson10;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class MyPriorityQueue<E> implements java.util.Queue<E> {

    private E[] queue = (E[]) new Object[11];

    private int size = 0;

    public MyPriorityQueue() {
    }

    /////////////////////////////////////////////////////////////////////////
    //////                     Основные методы                         //////
    /////////////////////////////////////////////////////////////////////////

    @Override
    public int size() {
        return size;
    }

    // Пусто?
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    // Выкинуть всё
    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            queue[i] = null;    // чистим каждую коробку
        }
        size = 0;               // счётчик на ноль
    }

    @Override
    public boolean add(E element) {
        return offer(element);
    }

    // Положить вещь
    @Override
    public boolean offer(E element) {
        if (element == null) {
            throw new NullPointerException(); // пустоту класть нельзя — её не сравнить
        }
        ensureCapacity();        // мало места? — расширяем
        siftUp(size, element);   // ставим в самый низ пирамиды и поднимаем, пока можно
        size++;
        return true;
    }

    // Посмотреть самую маленькую. Пусто — ошибка
    @Override
    public E element() {
        E x = peek();
        if (x == null) {
            throw new NoSuchElementException();
        }
        return x;
    }

    // Посмотреть самую маленькую.  Она всегда на вершине (коробка 0)
    @Override
    public E peek() {
        return size == 0 ? null : queue[0];
    }

    // ЗАБРАТЬ самую маленькую
    @Override
    public E poll() {
        if (size == 0) {
            return null;
        }
        int lastIdx = --size;           //  номер последней коробки
        E result = queue[0];            // вершина — её и отдадим
        E lastElem = queue[lastIdx];    // берём самую последнюю вещь
        queue[lastIdx] = null;          // её коробку очищаем

        if (lastIdx != 0) {             // если вещь была не одна
            siftDown(0, lastElem);      // ставим последнюю на вершину и опускаем вниз, пока надо
        }
        return result;
    }

    // ЗАБРАТЬ самую маленькую. Пусто — ошибка
    @Override
    public E remove() {
        E x = poll();
        if (x == null) {
            throw new NoSuchElementException();
        }
        return x;
    }

    // Есть ли вещь o?
    @Override
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    // Есть ли у нас ВСЕ вещи из c?
    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object item : c) {
            if (!contains(item)) {   // хоть одной нет —
                return false;        // сразу "нет"
            }
        }
        return true;
    }

    // Положить все вещи из c
    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (c == null) throw new NullPointerException();      // c не передали — ошибка
        if (c == this) throw new IllegalArgumentException();  // добавлять саму себя нельзя
        boolean modified = false;
        for (E e : c) {
            if (add(e)) {           // кладём каждую
                modified = true;
            }
        }
        return modified;
    }

    // Выкинуть все вещи, которые есть в c
    @Override
    public boolean removeAll(Collection<?> c) {
        if (c == null) throw new NullPointerException();
        int newSize = 0;                        // куда класть следующую оставшуюся вещь
        for (int i = 0; i < size; i++) {
            if (!c.contains(queue[i])) {        // этой вещи нет в c — оставляем
                queue[newSize++] = queue[i];    // переносим влево, без дырок
            }
        }
        if (newSize == size) {
            return false;                       // ничего не выкинули
        }
        for (int i = newSize; i < size; i++) {
            queue[i] = null;                    // чистим хвост
        }
        size = newSize;
        heapify();                              // пирамида поломалась — собираем заново
        return true;
    }

    // Оставить только вещи, которые есть в c (то же самое, но наоборот)
    @Override
    public boolean retainAll(Collection<?> c) {
        if (c == null) throw new NullPointerException();
        int newSize = 0;
        for (int i = 0; i < size; i++) {
            if (c.contains(queue[i])) {         // вещь есть в c — оставляем
                queue[newSize++] = queue[i];
            }
        }
        if (newSize == size) {
            return false;
        }
        for (int i = newSize; i < size; i++) {
            queue[i] = null;
        }
        size = newSize;
        heapify();                              // собираем пирамиду заново
        return true;
    }

    // Текст вида [a, b, c] — в порядке КОРОБОК, а не по возрастанию!
    // (так же печатает и настоящий PriorityQueue)
    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");  // удобная "склейка" строк
        for (int i = 0; i < size; i++) {
            sb.append(queue[i]);
            if (i < size - 1) {
                sb.append(", ");
            }
        }
        return sb.append("]").toString();
    }

    /////////////////////////////////////////////////////////////////////////
    //////                  Помощники для пирамиды                     //////
    /////////////////////////////////////////////////////////////////////////

    // Собрать пирамиду из перемешанных вещей.
    // Нижний ряд (у кого нет детей) трогать не надо.
    // Идём снизу вверх и каждую вещь с детьми опускаем на своё место.
    private void heapify() {
        for (int i = (size >>> 1) - 1; i >= 0; i--) {   // size >>> 1 — это size / 2
            siftDown(i, queue[i]);
        }
    }

    // ПОДНЯТЬ вещь x с места k: пока она меньше родителя — меняемся местами
    @SuppressWarnings("unchecked")
    private void siftUp(int k, E x) {
        Comparable<? super E> key = (Comparable<? super E>) x; // "x умеет сравниваться"
        while (k > 0) {                          // пока не дошли до вершины
            int parent = (k - 1) >>> 1;          // номер родителя
            E parentElem = queue[parent];
            if (key.compareTo(parentElem) >= 0) {
                break;                           // x не меньше родителя — стоп, место найдено
            }
            queue[k] = parentElem;               // родитель спускается вниз
            k = parent;                          // а мы поднимаемся на его место
        }
        queue[k] = (E) key;                      // кладём x на найденное место
    }

    // ОПУСТИТЬ вещь x с места k: пока она больше меньшего ребёнка — меняемся
    @SuppressWarnings("unchecked")
    private void siftDown(int k, E x) {
        Comparable<? super E> key = (Comparable<? super E>) x;
        int half = size >>> 1;                   // дальше этого номера детей уже нет
        while (k < half) {                       // пока у нас есть дети
            int child = (k << 1) + 1;            // левый ребёнок (k << 1 — это k * 2)
            E childElem = queue[child];
            int right = child + 1;               // правый ребёнок

            // правый есть и он меньше левого? — тогда смотрим на правого
            if (right < size && ((Comparable<? super E>) childElem).compareTo(queue[right]) > 0) {
                child = right;
                childElem = queue[right];
            }
            if (key.compareTo(childElem) <= 0) {
                break;                           // x не больше ребёнка — стоп, место найдено
            }
            queue[k] = childElem;                // ребёнок поднимается вверх
            k = child;                           // а мы спускаемся на его место
        }
        queue[k] = (E) key;                      // кладём x на найденное место
    }

    // В какой коробке лежит o? Нет — -1. Ищем просто по порядку
    private int indexOf(Object o) {
        if (o != null) {                         // пустоты в очереди не бывает
            for (int i = 0; i < size; i++) {
                if (o.equals(queue[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    // Все коробки заняты? — делаем ряд в 2 раза больше и перекладываем
    @SuppressWarnings("unchecked")
    private void ensureCapacity() {
        if (size == queue.length) {
            int newCap = queue.length * 2;
            E[] newQueue = (E[]) new Object[newCap];
            for (int i = 0; i < size; i++) {
                newQueue[i] = queue[i];
            }
            queue = newQueue;
        }
    }

    /////////////////////////////////////////////////////////////////////////
    //////      Пустышки — нужны только чтобы Java скомпилировала класс  ////
    /////////////////////////////////////////////////////////////////////////

    // Удалить конкретную вещь o — НЕ СДЕЛАНО, всегда "не удалила"
    @Override
    public boolean remove(Object o) {
        return false;
    }

    // Для цикла for (x : queue) — не сделано
    @Override
    public Iterator<E> iterator() {
        return null;
    }

    // Не сделано
    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    // Не сделано
    @Override
    public <T> T[] toArray(T[] a) {
        return a;
    }
}