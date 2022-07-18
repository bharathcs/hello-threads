import java.util.ArrayList;
import java.util.List;

public class HelloThreads {
  public static String capitalize(String input) {
    char[] chars = input.toCharArray();
    List<Character> src = new ArrayList<>();
    for (char c : chars) {
      src.add(c);
    }

    ThreadSafeList dest = new ThreadSafeList(new ArrayList<>(src));
    CharWorkerPool pool = new CharWorkerPool(src, dest, 5);
    return pool.runAndReturn().stream().reduce(
        "", (prevStr, currChar) -> prevStr + currChar, (prevStr, currStr) -> prevStr + currStr);
  }
}
