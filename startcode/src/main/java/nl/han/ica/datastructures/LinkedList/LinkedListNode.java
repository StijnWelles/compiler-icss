package nl.han.ica.datastructures.LinkedList;

public class LinkedListNode<T> {
  private LinkedListNode<T> next;
  private T value;

  public LinkedListNode(LinkedListNode<T> next, T value) {
    this.next = next;
    this.value = value;
  }

  public LinkedListNode<T> getNext() {
    return next;
  }

  public void setNext(LinkedListNode<T> next) {
    this.next = next;
  }

  public T getValue() {
    return value;
  }
}
