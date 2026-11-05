package by.it.group510901.dremluk.lesson11;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

public class MyTreeSet<E extends Comparable<? super E>> implements Set<E> {

    private static final int DEFAULT_CAPACITY = 10;
    private Object[] elements;
    private int size;
    public MyTreeSet() {
        this.elements = new Object[DEFAULT_CAPACITY];
        this.size = 0;
    }

    // Расширение массива
    private void grow() {
        int oldCapacity = elements.length;             // старая ёмкость
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        Object[] newElements = new Object[newCapacity]; // новый массив
        System.arraycopy(elements, 0, newElements, 0, size); // копируем только реальные элементы
        elements = newElements;                        // подменяем ссылку
    }

    // Бинарный поиск если нет
    private int binarySearch(E key) {
        int low = 0;
        int high = size - 1;                           // правая граница
        while (low <= high) {                          // пока границы не сошлись
            int mid = (low + high) >>> 1;
            E midVal = (E) elements[mid];              // значение в середине
            int cmp = midVal.compareTo(key);           // сравниваем
            if (cmp < 0) low = mid + 1;
            else if (cmp > 0) high = mid - 1;
            else return mid;                           // нашли — возвращаем индекс
        }
        return -(low + 1);                             // не нашли — кодируем точку вставки
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
    public boolean contains(Object o) {                // есть ли элемент
        if (o == null) return false;
        try {
            return binarySearch((E) o) >= 0;           // ищем бинарно
        } catch (ClassCastException e) {               // если тип не тот  считаем, что нет
            return false;
        }
    }

    @Override
    public boolean add(E e) {
        if (e == null) throw new NullPointerException();
        int pos = binarySearch(e);                     // ищем
        if (pos >= 0) return false;                    // уже есть — не добавляем
        int insertPos = -(pos + 1);
        if (size == elements.length) grow();           // места нет — расширяем
        System.arraycopy(elements, insertPos, elements, insertPos + 1, size - insertPos); // вправо
        elements[insertPos] = e;                       // вставляем на место
        size++;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (o == null) return false;
        try {
            int pos = binarySearch((E) o);             // ищем
            if (pos < 0) return false;                 // нет — ничего не делаем
            System.arraycopy(elements, pos + 1, elements, pos, size - pos - 1); //влево
            size--;
            elements[size] = null;                     // хвост обнуляем
            return true;
        } catch (ClassCastException e) {               // если тип не тот считаем, что нет
            return false;
        }
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            elements[i] = null;
        }
        size = 0;
    }

    @Override
    public String toString() {
        if (isEmpty()) return "[]";
        String result = "[";
        boolean first = true;
        for (int i = 0; i < size; i++) {
            if (!first) result += ", ";
            result += elements[i];
            first = false;
        }
        result += "]";
        return result;
    }

    @Override
    public boolean containsAll(Collection<?> c) {      // все ли из c есть
        if (c == null) throw new NullPointerException();
        for (Object item : c) {                        // по всем элементам c
            if (!contains(item)) return false;         // хоть одного нет — false
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (c == null) throw new NullPointerException();
        boolean modified = false;                      // флаг «что-то добавили»
        for (E item : c) {
            if (add(item)) modified = true;            // добавили — ставим флаг
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c == null) throw new NullPointerException();
        boolean modified = false;
        Iterator<E> it = iterator();                   // обходчик
        while (it.hasNext()) {
            if (c.contains(it.next())) {               // этот есть в c — удаляем
                it.remove();                           // удаление через итератор
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {        // оставить только из c
        if (c == null) throw new NullPointerException();
        boolean modified = false;
        Iterator<E> it = iterator();
        while (it.hasNext()) {
            if (!c.contains(it.next())) {              // этого НЕТ в c — удаляем
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public Iterator<E> iterator() {                    // обходчик (анонимный класс)
        return new Iterator<E>() {
            private int cursor = 0;                    // текущая позиция в массиве
            private int lastRet = -1;                  // последний возвращённый (для remove)

            @Override
            public boolean hasNext() {                 // есть ли ещё
                return cursor < size;
            }

            @Override
            public E next() {                          // взять следующий
                if (cursor >= size) throw new NoSuchElementException();
                lastRet = cursor;                      // запоминаем позицию
                return (E) elements[cursor++];         // отдаём и сдвигаемся
            }

            @Override
            public void remove() {                     // удалить последний возвращённый
                if (lastRet < 0) throw new IllegalStateException(); // next не вызывали
                MyTreeSet.this.remove(elements[lastRet]); // удаляем из множества
                cursor = lastRet;                      // курсор назад (массив сжался)
                lastRet = -1;                          // сбрасываем
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