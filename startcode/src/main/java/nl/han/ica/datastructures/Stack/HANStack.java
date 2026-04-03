package nl.han.ica.datastructures.Stack;

import nl.han.ica.datastructures.LinkedList.HANLinkedList;
import nl.han.ica.datastructures.LinkedList.IHANLinkedList;

import java.util.ArrayList;
import java.util.List;

public class HANStack<T> implements IHANStack<T> {
  IHANLinkedList<T> list;

  public HANStack() {
    list = new HANLinkedList<>();
  }

  @Override
  public void push(T value) {
    list.insert(value);
  }

  @Override
  public T pop() {
    return list.delete(list.getSize()-1);
  }

  @Override
  public T peek() {
    return list.get(list.getSize()-1);
  }
}
