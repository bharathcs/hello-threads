import java.util.ArrayList;
import java.util.List;

public class CharWorkerPool {
  private final List<Character> source;
  private final ThreadSafeList destination;

  public CharWorkerPool(List<Character> source, ThreadSafeList destination) {
      this.source = source;
      this.destination = destination;
  }

  public List<Character> runAndReturn() {
    List<CharWorker> workers = new ArrayList<>();
    for (int i = 0; i < source.size(); i++) {
      CharWorker worker = new CharWorker(i);
      worker.start();
      workers.add(worker);
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

    CharWorker (int index) {
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
