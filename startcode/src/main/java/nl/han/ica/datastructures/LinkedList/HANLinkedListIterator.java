package nl.han.ica.datastructures.LinkedList;

import java.util.Iterator;

public class HANLinkedListIterator<T> implements Iterator<T> {
  private LinkedListNode<T> next;

  HANLinkedListIterator(LinkedListNode<T> first) {
    next = first;
  }

  @Override
  public boolean hasNext() {
    return next != null;
  }

  @Override
  public T next() {
    if (!hasNext()) {
      throw new java.util.NoSuchElementException();
    }

    T value = next.getValue();
    next = next.getNext();
    return value;
  }
}
