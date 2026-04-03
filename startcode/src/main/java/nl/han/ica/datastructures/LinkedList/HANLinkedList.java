package nl.han.ica.datastructures.LinkedList;

import java.util.Iterator;

public class HANLinkedList<T> implements IHANLinkedList<T> {
  private HANLinkedListNode<T> firstNode;

  public HANLinkedList() {}

  public HANLinkedList(T initialValue) {
    insert(initialValue);
  }

  public HANLinkedList(T... initialValues) {
    for (T value : initialValues) {
      insert(value);
    }
  }

  @Override
  public void addFirst(T value) {
    firstNode = new HANLinkedListNode<>(firstNode, value);
  }

  @Override
  public void clear() {
    firstNode = null;
  }

  private HANLinkedListNode<T> getNext(int targetIndex, HANLinkedListNode<T> currentNode, int currentIndex) {
    HANLinkedListNode<T> nextNode = currentNode.getNext();
    if (nextNode == null) {
      return null;
    }

    if (targetIndex == currentIndex+1) {
      return nextNode;
    }

    return getNext(targetIndex, nextNode, currentIndex+1);
  }

  private HANLinkedListNode<T> getNodeAtPosition(int targetIndex) {
    if (targetIndex == 0) {
      return firstNode;
    }

    return getNext(targetIndex, firstNode, 0);
  }

  @Override
  public void insert(int index, T value) {
    if (index == 0) {
      addFirst(value);
      return;
    }

    HANLinkedListNode<T> prev = getNodeAtPosition(index-1);
    HANLinkedListNode<T> cur = getNodeAtPosition(index);

    HANLinkedListNode<T> newNode = new HANLinkedListNode<>(cur, value);
    prev.setNext(newNode);
  }

  @Override
  public void insert(T value) {
    insert(getSize(), value);
  }

  @Override
  public T delete(int pos) {
    if (pos == 0) {
      HANLinkedListNode<T> cur = getNodeAtPosition(pos);

      firstNode = cur.getNext();

      return cur.getValue();
    }

    HANLinkedListNode<T> prev = getNodeAtPosition(pos-1);
    HANLinkedListNode<T> cur = getNodeAtPosition(pos);

    if (cur == null) {
      throw new IndexOutOfBoundsException();
    }

    prev.setNext(cur.getNext());

    return cur.getValue();
  }

  @Override
  public T get(int pos) {
    HANLinkedListNode<T> node = getNodeAtPosition(pos);

    if (node == null) {
      throw new IndexOutOfBoundsException();
    }

    return node.getValue();
  }

  @Override
  public boolean has(T value) {
    // Use the Iterable interface
    for (T t : this) {
      if (t == value) {
        return true;
      }
    }

    return false;
  }

  @Override
  public void removeFirst() {
    firstNode = firstNode.getNext();
  }

  @Override
  public T getFirst() {
    return firstNode.getValue();
  }

  private int countNodes(HANLinkedListNode<T> currentNode) {
    if (currentNode == null) {
      return 0;
    }

    if (currentNode.getNext() == null) {
      return 1;
    }

    return countNodes(currentNode.getNext()) + 1;
  }

  @Override
  public int getSize() {
    return countNodes(firstNode);
  }

  @Override
  public Iterator<T> iterator() {
    return new HANLinkedListIterator<>(firstNode);
  }

  @Override
  public String toString(String sep) {
    StringBuilder v = new StringBuilder();

    for (int i = 0; i < getSize() - 1; i++) {
      T t = get(i);

      v.append(t).append(sep);
    }

    v.append(get(getSize()-1));

    return v.toString();
  }
}
