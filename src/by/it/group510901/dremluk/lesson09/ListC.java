package by.it.group510901.dremluk.lesson09;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ListC<E> implements List<E> {

    //Создайте аналог списка БЕЗ использования других классов СТАНДАРТНОЙ БИБЛИОТЕКИ

    // тут храним сами элементы, начальная вместимость 10
    private Object[] elements = new Object[10];
    // сколько элементов реально лежит в списке
    private int size = 0;

    // если массив забился, делаем новый в два раза больше и переносим всё туда
    private void grow() {
        if (size == elements.length) {
            Object[] bigger = new Object[elements.length * 2];
            for (int i = 0; i < size; i++) {
                bigger[i] = elements[i];
            }
            elements = bigger;
        }
    }

    // проверка индекса для get, set, remove (тут index должен быть строго < size)
    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index: " + index + ", size: " + size);
        }
    }

    // проверка индекса для вставки (можно вставлять и в самый конец, то есть index == size)
    private void checkIndexForAdd(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("index: " + index + ", size: " + size);
        }
    }

    // сравнение двух объектов с учетом null, чтобы не ловить NullPointerException
    private boolean same(Object a, Object b) {
        return a == null ? b == null : a.equals(b);
    }

    /////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////
    //////               Обязательные к реализации методы             ///////
    /////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////
    @Override
    public String toString() {
        // собираем строку вида [1, 2, 3]
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append(elements[i]);
            if (i < size - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean add(E e) {
        // сначала убеждаемся что есть место, потом кладем в конец
        grow();
        elements[size] = e;
        size++;
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E remove(int index) {
        checkIndex(index);
        E removed = (E) elements[index];
        // сдвигаем все элементы после удаленного на одну позицию влево
        for (int i = index; i < size - 1; i++) {
            elements[i] = elements[i + 1];
        }
        // последнюю ячейку зануляем, чтобы не держать лишнюю ссылку
        size--;
        elements[size] = null;
        return removed;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void add(int index, E element) {
        checkIndexForAdd(index);
        grow();
        // раздвигаем элементы вправо, начиная с конца, чтобы освободить место
        for (int i = size; i > index; i--) {
            elements[i] = elements[i - 1];
        }
        elements[index] = element;
        size++;
    }

    @Override
    public boolean remove(Object o) {
        // ищем первое совпадение и удаляем его по индексу
        int index = indexOf(o);
        if (index < 0) {
            return false;
        }
        remove(index);
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E set(int index, E element) {
        checkIndex(index);
        E old = (E) elements[index];
        elements[index] = element;
        return old;
    }


    @Override
    public boolean isEmpty() {
        return size == 0;
    }


    @Override
    public void clear() {
        // зануляем ячейки, чтобы сборщик мусора мог забрать объекты
        for (int i = 0; i < size; i++) {
            elements[i] = null;
        }
        size = 0;
    }

    @Override
    public int indexOf(Object o) {
        for (int i = 0; i < size; i++) {
            if (same(o, elements[i])) {
                return i;
            }
        }
        return -1;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E get(int index) {
        checkIndex(index);
        return (E) elements[index];
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    @Override
    public int lastIndexOf(Object o) {
        // идем с конца, первое найденное совпадение и будет последним
        for (int i = size - 1; i >= 0; i--) {
            if (same(o, elements[i])) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        // если хоть одного элемента из c у нас нет, сразу false
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean addAll(Collection<? extends E> c) {
        // сначала делаем копию в массив, вдруг передали сам этот же список
        Object[] arr = c.toArray();
        for (int i = 0; i < arr.length; i++) {
            add((E) arr[i]);
        }
        return arr.length > 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean addAll(int index, Collection<? extends E> c) {
        checkIndexForAdd(index);
        Object[] arr = c.toArray();
        if (arr.length == 0) {
            return false;
        }
        // вставляем по одному, каждый следующий идет сразу за предыдущим
        for (int i = 0; i < arr.length; i++) {
            add(index + i, (E) arr[i]);
        }
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        // идем по списку и оставляем только то, чего нет в коллекции c
        int write = 0;
        for (int read = 0; read < size; read++) {
            if (!c.contains(elements[read])) {
                elements[write] = elements[read];
                write++;
            }
        }
        boolean changed = write != size;
        // хвост зануляем
        for (int i = write; i < size; i++) {
            elements[i] = null;
        }
        size = write;
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        // то же самое что removeAll, только условие наоборот: оставляем то, что есть в c
        int write = 0;
        for (int read = 0; read < size; read++) {
            if (c.contains(elements[read])) {
                elements[write] = elements[read];
                write++;
            }
        }
        boolean changed = write != size;
        for (int i = write; i < size; i++) {
            elements[i] = null;
        }
        size = write;
        return changed;
    }

    /////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////
    //////               Опциональные к реализации методы             ///////
    /////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////

    @Override
    @SuppressWarnings("unchecked")
    public List<E> subList(int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex > size || fromIndex > toIndex) {
            throw new IndexOutOfBoundsException("from: " + fromIndex + ", to: " + toIndex + ", size: " + size);
        }
        // делаем просто копию куска, а не "окно" в исходный список, для лабы этого хватит
        ListC<E> result = new ListC<>();
        for (int i = fromIndex; i < toIndex; i++) {
            result.add((E) elements[i]);
        }
        return result;
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        checkIndexForAdd(index);
        return new ListItr(index);
    }

    @Override
    public ListIterator<E> listIterator() {
        return new ListItr(0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        // без рефлексии новый массив типа T не создать, поэтому работаем только с переданным
        if (a.length < size) {
            throw new UnsupportedOperationException("передайте массив длиной не меньше size()");
        }
        for (int i = 0; i < size; i++) {
            a[i] = (T) elements[i];
        }
        // по контракту после последнего элемента ставим null, если место осталось
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];
        for (int i = 0; i < size; i++) {
            result[i] = elements[i];
        }
        return result;
    }

    /////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////
    ////////        Эти методы имплементировать необязательно    ////////////
    ////////        но они будут нужны для корректной отладки    ////////////
    /////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////
    @Override
    public Iterator<E> iterator() {
        // ListIterator это тоже Iterator, поэтому отдаем ту же реализацию
        return new ListItr(0);
    }

    // внутренний класс итератора, чтобы работал for-each и отладчик
    private class ListItr implements ListIterator<E> {
        // позиция курсора между элементами
        private int cursor;
        // индекс элемента, который вернули последним (нужен для remove и set)
        private int lastRet = -1;

        ListItr(int start) {
            cursor = start;
        }

        @Override
        public boolean hasNext() {
            return cursor < size;
        }

        @Override
        @SuppressWarnings("unchecked")
        public E next() {
            // NoSuchElementException не берем, чтобы не тянуть лишний импорт
            if (cursor >= size) {
                throw new IndexOutOfBoundsException("больше элементов нет");
            }
            lastRet = cursor;
            cursor++;
            return (E) elements[lastRet];
        }

        @Override
        public boolean hasPrevious() {
            return cursor > 0;
        }

        @Override
        @SuppressWarnings("unchecked")
        public E previous() {
            if (cursor <= 0) {
                throw new IndexOutOfBoundsException("предыдущих элементов нет");
            }
            cursor--;
            lastRet = cursor;
            return (E) elements[lastRet];
        }

        @Override
        public int nextIndex() {
            return cursor;
        }

        @Override
        public int previousIndex() {
            return cursor - 1;
        }

        @Override
        public void remove() {
            if (lastRet < 0) {
                throw new IllegalStateException();
            }
            // вызываем именно метод внешнего списка, а не этот
            ListC.this.remove(lastRet);
            cursor = lastRet;
            lastRet = -1;
        }

        @Override
        public void set(E e) {
            if (lastRet < 0) {
                throw new IllegalStateException();
            }
            ListC.this.set(lastRet, e);
        }

        @Override
        public void add(E e) {
            ListC.this.add(cursor, e);
            cursor++;
            lastRet = -1;
        }
    }

}
