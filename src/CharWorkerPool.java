import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CharWorkerPool {
  private final List<Character> source;
  private final ThreadSafeList destination;
  private final int numberOfThreads;
  private final int length;
  private final long threadCheckIntervalMilliseconds;

  public CharWorkerPool(List<Character> source, ThreadSafeList destination, int numberOfThreads) {
    this(source, destination, numberOfThreads, 10);
  }

  public CharWorkerPool(
      List<Character> source,
      ThreadSafeList destination,
      int numberOfThreads,
      long threadCheckIntervalMilliseconds) {
    this.source = source;
    this.length = source.size();
    this.destination = destination;
    this.numberOfThreads = numberOfThreads;
    this.threadCheckIntervalMilliseconds = threadCheckIntervalMilliseconds;
  }

  public List<Character> runAndReturn() {
    int lastQueuedIndex = Math.min(length, numberOfThreads) - 1;
    List<CharWorker> workers = IntStream
        .rangeClosed(0, lastQueuedIndex)
        .mapToObj(CharWorker::new)
        .collect(Collectors.toList());
    workers.forEach(CharWorker::start);

    while (lastQueuedIndex + 1 < length) {
      workers = workers.stream().filter(CharWorker::isAlive).collect(Collectors.toList());
      int activeWorkersCount = workers.size();
      int newWorkersToAddCount = Math.min(
          length - lastQueuedIndex - 1, numberOfThreads - activeWorkersCount);
      List<CharWorker> newWorkers = IntStream
          .rangeClosed(lastQueuedIndex + 1, lastQueuedIndex + newWorkersToAddCount)
          .mapToObj(CharWorker::new)
          .collect(Collectors.toUnmodifiableList());
      newWorkers.forEach(CharWorker::start);
      workers.addAll(newWorkers);
      lastQueuedIndex += newWorkers.size();
      try {
        Thread.sleep(threadCheckIntervalMilliseconds);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    workers.forEach(w -> {
      try {
        w.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    });
    return destination.getList();
  }

  private class CharWorker extends Thread {
    private final int index;

    CharWorker(int index) {
      this.index = index;
    }

    @Override
    public void run() {
      Character input = source.get(index);
      Character output = Character.toUpperCase(input);
      destination.setElement(index, output);
    }
  }
}
