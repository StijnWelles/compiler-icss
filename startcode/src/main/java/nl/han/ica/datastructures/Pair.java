package nl.han.ica.datastructures;

public class Pair<K, V> {
  K key;
  V value;

  public Pair() {}

  public Pair(K key, V value) {
    this.key = key;
    this.value = value;
  }
}
