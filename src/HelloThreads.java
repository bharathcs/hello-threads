import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HelloThreads {
  public static String capitalize(String input) {
    char[] chars = input.toCharArray();
    List<Character> src = new ArrayList<>();
    for (char c : chars) {
      src.add(c);
    }

    ThreadSafeList<Character> dest = new ThreadSafeList<>(new ArrayList<>(src));
    Function<Integer,  WorkerPool.Task> taskGenerator = index -> {
      return () -> {
        dest.setElement(index, Character.toUpperCase(src.get(index)));
      };
    };

    ThreadSafeList<WorkerPool.Task> tasks = new ThreadSafeList<WorkerPool.Task>(
        IntStream
            .rangeClosed(0, src.size() - 1)
            .mapToObj(taskGenerator::apply)
            .collect(Collectors.toList()));
    WorkerPool pool = new WorkerPool(tasks, 3);
    pool.runAndBlock();
    return dest.getList().stream().reduce(
        "", (prevStr, currChar) -> prevStr + currChar, (prevStr, currStr) -> prevStr + currStr);
  }
}
