import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ThreadSafeList<T> {
  private final List<T> list;
  private int size;

  public ThreadSafeList(int size) {
    this.size = size;
    this.list = new ArrayList<>(size);
  }

  public ThreadSafeList(Collection<? extends T> collection) {
    this.list = new ArrayList<>(collection);
    this.size = list.size();
  }

  public void setElement(int index, T element) {
    checkIndex(index);
    synchronized (list) {
      list.set(index, element);
    }
  }

  public boolean isEmpty() {
    synchronized (list) {
      return list.isEmpty();
    }
  }

  public T getElement(int i) {
    checkIndex(i);
    Character result;
    synchronized (list) {
      return list.get(i);
    }
  }

  public Optional<T> pop() {
    if (size > 0) {
      synchronized (list) {
        if (list.size() > 0) {
          size--;
          return Optional.of(list.remove(0));
        }
      }
    }
    return Optional.empty();
  }

  public List<T> getList() {
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
