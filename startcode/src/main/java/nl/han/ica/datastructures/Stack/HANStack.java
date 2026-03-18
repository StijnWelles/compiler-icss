package nl.han.ica.datastructures.Stack;

import nl.han.ica.datastructures.IHANStack;

import java.util.ArrayList;
import java.util.List;

public class HANStack<T> implements IHANStack<T> {
  List<T> list;

  public HANStack() {
    list = new ArrayList<>();
  }

  @Override
  public void push(T value) {
    list.add(value);
  }

  @Override
  public T pop() {
    return list.removeLast();
  }

  @Override
  public T peek() {
    return list.getLast();
  }
}
