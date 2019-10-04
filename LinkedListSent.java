import java.lang.reflect.Array;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class LinkedListSent<E> implements List<E>, Deque<E>, Iterable<E> {
    private static class Node<E> {
        private E data;
        private Node<E> next;
        private Node<E> previous;

        public Node(E data, Node<E> next, Node<E> previous) {
            this.data = data;
            this.next = next;
            this.previous = previous;
        }

        public Node(E data) {
            this.data = data;
        }

        public E getData() {
            return data;
        }

        public void setData(E data) {
            this.data = data;
        }

        public Node<E> getNext() {
            return next;
        }

        public void setNext(Node<E> next) {
            this.next = next;
        }

        public Node<E> getPrevious() {
            return previous;
        }

        public void setPrevious(Node<E> previous) {
            this.previous = previous;
        }
    }

    private Node<E> sentinel;
    private int size;

    public LinkedListSent() {
        sentinel = new Node<>(null);
        size = 0;
        sentinel.next = sentinel;
        sentinel.previous = sentinel;
    }

    public LinkedListSent(Collection<? extends E> c) {
        this();
        addAll(c);
    }

    public void addFirst(E value) {
        Node<E> temp = new Node<>(value);

        temp.next = sentinel.next;
        temp.previous = sentinel;

        sentinel.next.previous = temp;
        sentinel.next = temp;

        size++;
    }

    public void addLast(E e) {
        //If it's empty, size - 1 will throw an exception, so call addFirst
        if (size() == 0) {
            addFirst(e);
            return;
        }

        Node<E> temp = new Node<>(e);
        temp.next = sentinel;
        sentinel.previous = temp;


        temp.previous = marchToIndex(size()-1);
        temp.previous.next = temp;

        size++;
    }

    public boolean add(E e) {
        addLast(e);

        return true;
    }

    public void add(int index, E e) {
        if (outOfBoundsCheck(index))
            throw new IndexOutOfBoundsException();

        if (index == 0)
            addFirst(e);
        else if (index == size()-1)
            addLast(e);
        else {
            Node<E> pointer = marchToIndex(index-1);

            Node<E> newNode = new Node<>(e, pointer.getNext(), pointer);

            pointer.next = newNode;
            newNode.next.previous = newNode;

            size++;
        }
    }

    public E removeLast() {
        if (size() == 0)
            throw new NoSuchElementException();

        E value = sentinel.previous.getData();

        //Change the Node before the last Node next to sentinel
        sentinel.previous.previous.next = sentinel;

        //Change sentinel's previous to the Node before the last Node
        sentinel.previous = sentinel.previous.previous;

        size--;

        return value;
    }

    public boolean removeLastOccurrence(Object o) {
        remove(lastIndexOf(o));
        return true;
    }

    public E removeFirst() {
        if (size() == 0)
            throw new NoSuchElementException();

        E value = sentinel.next.getData();

        //Change sentinel's next to the Node after the first Node
        sentinel.next.next.previous = sentinel;

        //Change the Node after the first Node previous to sentinel
        sentinel.next = sentinel.next.next;

        size--;

        return value;
    }

    public boolean removeFirstOccurrence(Object o) {
        remove(o);
        return true;
    }

    public boolean remove(Object o) {
        int index = indexOf(o);

        if (index == -1)
            return false;

        remove(index);
        return true;
    }

    public E remove(int index) {
        if (outOfBoundsCheck(index))
            throw new IndexOutOfBoundsException();

        if (index == 0)
            return removeFirst();
        else if (index == size()-1)
            return removeLast();
        else {
            Node<E> pointer = marchToIndex(index);
            E oldValue = pointer.getData();
            pointer.getPrevious().setNext(pointer.getNext());
            pointer.getNext().setPrevious(pointer.getPrevious());

            size--;

            return oldValue;
        }
    }

    public E remove() {
        return removeFirst();
    }

    public E get(int i) {
        return marchToIndex(i).getData();
    }

    public E set(int index, E e) {
        if(outOfBoundsCheck(index))
            throw new IndexOutOfBoundsException();

        Node<E> pointer = marchToIndex(index);
        E oldValue = pointer.getData();
        pointer.setData(e);

        return oldValue;
    }

    public int indexOf(Object o) {
        int index = 0;

        Node<E> pointer = sentinel.next;

        while (pointer != sentinel) {
            if (pointer.getData().equals(o))
                return index;

            index++;
            pointer = pointer.getNext();
        }

        return -1;
    }

    public int lastIndexOf(Object o) {
        int index = size()-1;

        Node<E> pointer = sentinel.previous;

        while (pointer != sentinel) {
            if (pointer.getData().equals(o))
                return index;

            index--;
            pointer = pointer.getPrevious();
        }

        return -1;
    }

    public Object[] toArray() {
        Object[] arr = new Object[size()];
        Node<E> pointer = sentinel.next;

        for (int i=0; i<size(); i++) {
            arr[i] = pointer;
            pointer = pointer.getNext();
        }

        return arr;
    }

    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            //this is how the actual LinkedList deals
            //with a smaller size than the List
            a = (T[]) Array.newInstance(a.getClass().getComponentType(),
                    size());
        }

        Node<E> pointer = sentinel.next;
        Object[] objArr = a;

        for (int i=0; i<size(); i++) {
            objArr[i] = pointer.data;
            pointer = pointer.next;
        }

        if (a.length > size)
            a[size] = null;

        return a;
    }

    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    public boolean containsAll(Collection<?> collection) {
        for (Object element: collection) {
            if (!(contains(element)))
                return false;
        }

        return true;
    }

    public boolean addAll(Collection<? extends E> collection) {
        for (E e: collection)
            add(e);
        return true;
    }

    public boolean addAll(int index, Collection<? extends E> collection) {
        if (outOfBoundsCheck(index))
            throw new IndexOutOfBoundsException();

        for (E e: collection) {
            add(index, e);
            index++;
        }

        return true;
    }

    public boolean removeAll(Collection<?> collection) {
        /* Verbose implementation */
//        if (size() == 0 || collection.isEmpty())
//            return false;
//
//        for (Object obj: collection) {
//            Node<E> pointer = sentinel.next;
//
//            while (pointer.next != sentinel) {
//                if (pointer.getData().equals(obj)) {
//                    remove(obj);
//                }
//                pointer = pointer.next;
//            }
//        }

        for (Object element: collection) {
            remove(element);
        }

        return true;
    }

    public boolean retainAll(Collection<?> collection) {
        if (size() == 0 || collection.isEmpty())
            return false;

        Node<E> pointer = sentinel.next;

        //For every element in our list, loop through the collection.
        //If an element in our list is not found in the collection, remove it.
        while (pointer != sentinel) {
            boolean found = false;
            for (Object element: collection) {
                if (element.equals(pointer.data))
                    found = true;
            }

            if (!found)
                remove(pointer.data);

            pointer = pointer.next;
        }

        return true;
    }
    
    public void replaceAll(UnaryOperator<E> unaryOperator) {
        Objects.requireNonNull(unaryOperator);
        final ListIterator<E> li = this.listIterator();
        while (li.hasNext()) {
            li.set(unaryOperator.apply(li.next()));
        }
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int size() {
        return size;
    }

    public void clear() {
        size = 0;
        sentinel.next = sentinel;
        sentinel.previous = sentinel;
    }

    public List<E> subList(int i, int i1) {
        if (outOfBoundsCheck(i) || outOfBoundsCheck(i1))
            throw new IndexOutOfBoundsException();

        List<E> sub = new LinkedList<>();

        Node<E> pointer = marchToIndex(i);

        while (i < i1) {
            sub.add(pointer.getData());
            pointer = pointer.getNext();
            i++;
        }

        return sub;
    }

    public void sort(Comparator<? super E> comparator) {
        Collections.sort(this, comparator);
    }

    public void forEach(Consumer<? super E> consumer) {
        Objects.requireNonNull(consumer);
        for (E e : this) {
            consumer.accept(e);
        }
    }

    public Spliterator<E> spliterator() {
        return Spliterators.spliterator(this, Spliterator.ORDERED);
    }

    public boolean removeIf(Predicate<? super E> predicate) {
        Objects.requireNonNull(predicate);
        boolean removed = false;
        final Iterator<E> each = iterator();
        while (each.hasNext()) {
            if (predicate.test(each.next())) {
                each.remove();
                removed = true;
            }
        }
        return removed;
    }
    
    public Stream<E> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
    
    public Stream<E> parallelStream() {
        return StreamSupport.stream(spliterator(), true);
    }

    public boolean equals(Object o) {
        if (!(o instanceof LinkedListSent))
            return false;

        LinkedListSent<E> other = (LinkedListSent<E>) o;

        if (size() != other.size())
            return false;

        Iterator<E> iterator = other.listIterator();

        for (E element: this) {
            if (!(element.equals(iterator.next())))
                return false;
        }

        return true;
    }

    public int hashCode() {
        return super.hashCode();
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("[");
        int count = 0;

        for (E e: this) {
            count++;
            str.append(e);

            if (count < size())
                str.append(", ");
        }

        str.append("]");
        return str.toString();
    }

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    protected void finalize() throws Throwable {
        super.finalize();
    }

    /* Deque methods */

    public E element() {
        if (size() == 0)
            throw new NoSuchElementException();

        return sentinel.next.getData();
    }

    public E getFirst() {
        return element();
    }

    public E getLast() {
        if (size() == 0)
            throw new NoSuchElementException();

        return sentinel.previous.getData();
    }

    public boolean offerFirst(E e) {
        addFirst(e);
        return true;
    }

    public boolean offerLast(E e) {
        addLast(e);
        return true;
    }

    public boolean offer(E e) {
        return offerLast(e);
    }

    public E peekFirst() {
        return getFirst();
    }

    public E peekLast() {
        return getLast();
    }

    public E peek() {
        return getFirst();
    }

    public E pollFirst() {
        return removeFirst();
    }

    public E pollLast() {
        return removeLast();
    }

    public E poll() {
        return pollFirst();
    }

    public E pop() {
        return removeFirst();
    }

    public void push(E e) {
        addFirst(e);
    }

    /* Private methods used for list */

    private Node<E> marchToIndex(int index) {
        if(outOfBoundsCheck(index))
            throw new IndexOutOfBoundsException();

        int count = 0;
        Node<E> pointer = sentinel.next;

        while(count < index) {
            count++;
            pointer = pointer.getNext();
        }
        return pointer;
    }

    private boolean outOfBoundsCheck(int index) {
        return index < 0 || index >= size();
    }

    /* Iterators: Normal iterator, descendingIterator, listIterator */

    public Iterator<E> iterator() {
        return new LinkedIterator();
    }

    private class LinkedIterator implements Iterator<E> {
        private Node<E> pointer;
        private Node<E> lastNode;
        private int index;

        public LinkedIterator() {
            index = 0;
            pointer = sentinel.next;
        }

        @Override
        public boolean hasNext() {
            return (index < size()) || (pointer.next == sentinel);
        }

        @Override
        public E next() {
            E value = pointer.getData();
            lastNode = pointer;
            pointer = pointer.next;
            index++;
            return value;
        }

        @Override
        public void remove() {
            if (lastNode == null)
                throw new IllegalStateException();

            LinkedListSent.this.remove(lastNode.data);
            index--;
        }
    }

    public Iterator<E> descendingIterator() {
        return new LinkedDescendingIterator();
    }

    private class LinkedDescendingIterator implements Iterator<E> {
        private LinkedListIterator iterator =
                new LinkedListIterator(size()-1);

        public boolean hasNext() {
            return iterator.hasPrevious();
        }

        public E next() {
            return iterator.previous();
        }

        public void remove() {
            iterator.remove();
        }
    }

    public ListIterator<E> listIterator() {
        return listIterator(0);
    }

    public ListIterator<E> listIterator(int i) {
        return new LinkedListIterator(i);
    }

    private class LinkedListIterator implements ListIterator<E> {
        private int index;
        private Node<E> pointer;
        private Node<E> lastNode;

        public LinkedListIterator(int index) {
            this.index = index;
            pointer = marchToIndex(index);
        }

        @Override
        public E next() {
            E value = pointer.getData();
            lastNode = pointer;
            pointer = pointer.next;
            index++;
            return value;
        }

        @Override
        public boolean hasNext() {
            return (index < size()) || (pointer.next == sentinel);
        }

        @Override
        public E previous() {
            E value = pointer.getData();
            lastNode = pointer;
            pointer = pointer.previous;
            index--;
            return value;
        }

        @Override
        public boolean hasPrevious() {
            return (index > 0) || (pointer.previous == sentinel);
        }

        @Override
        public int nextIndex() {
            return index;
        }

        @Override
        public int previousIndex() {
            return index - 1;
        }

        @Override
        public void remove() {
            if (lastNode == null)
                throw new IllegalStateException();

            LinkedListSent.this.remove(lastNode.data);
            index--;
        }

        @Override
        public void set(E e) {
            if (lastNode == null)
                throw new IllegalStateException();

            lastNode.data = e;
        }

        @Override
        public void add(E e) {
            lastNode = null;

            if (pointer == sentinel)
                LinkedListSent.this.add(e);
            else
                LinkedListSent.this.add(index, e);

            index++;
        }
    }
}
