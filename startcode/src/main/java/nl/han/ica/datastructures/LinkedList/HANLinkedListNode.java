package nl.han.ica.datastructures.LinkedList;

public class HANLinkedListNode<T> {
  private HANLinkedListNode<T> next;
  private T value;

  public HANLinkedListNode(HANLinkedListNode<T> next, T value) {
    this.next = next;
    this.value = value;
  }

  public HANLinkedListNode<T> getNext() {
    return next;
  }

  public void setNext(HANLinkedListNode<T> next) {
    this.next = next;
  }

  public T getValue() {
    return value;
  }
}
