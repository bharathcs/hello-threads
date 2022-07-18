import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WorkerPool {
  public final ThreadSafeList<Task> tasks;
  private final List<Worker> workers;
  private static final int POLLING_PERIOD_MILLISECONDS = 50;

  public WorkerPool(ThreadSafeList<Task> tasks, int numberOfThreads) {
    this.tasks = tasks;
    this.workers = IntStream
        .rangeClosed(1, numberOfThreads)
        .mapToObj(ignored -> new Worker())
        .collect(Collectors.toList());
  }

  public void runAndBlock() {
    runWithoutBlock();
    while (!tasks.isEmpty()) {
      try {
        Thread.sleep(POLLING_PERIOD_MILLISECONDS * 5);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public void runWithoutBlock() {
    workers.forEach(Worker::start);
  }

  private class Worker extends Thread {

    @Override
    public void run() {
      for (Optional<Task> maybeTask = tasks.pop(); maybeTask.isPresent(); maybeTask = tasks.pop()) {
        maybeTask.get().run();
        try {
          Thread.sleep(POLLING_PERIOD_MILLISECONDS);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /** Interface that describes a task */
  @FunctionalInterface
  public interface Task {
    void run();
  }
}
