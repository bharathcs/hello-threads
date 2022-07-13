import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ThreadSafeList {
  private final List<Character> list;
  private final int size;

  public ThreadSafeList(int size) {
    this.size = size;
    this.list = new ArrayList<>(size);
  }

  public ThreadSafeList(List<Character> list) {
    this.list = list;
    this.size = list.size();
  }

  public void setElement(int i, Character c) {
    checkIndex(i);
    synchronized (list) {
      list.set(i, c);
    }
  }

  public Character getElement(int i) {
    checkIndex(i);
    Character result;
    synchronized (list) {
      return list.get(i);
    }
  }

  public List<Character> getList() {
    synchronized (list) {
      return list.stream().collect(Collectors.toUnmodifiableList());
    }
  }

  private void checkIndex(int i) {
    if (i >= 0 && i < size) {
      return;
    }
    throw new IllegalArgumentException(String.format("Index %d is invalid for list of size %d", i, size));
  }
}
