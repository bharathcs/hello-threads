import java.util.Scanner;

public class Main {
  private static final String INSTRUCTIONS = "Enter any number of lines of texts. Type `EXIT` (case insensitive) to "
      + "close the program.";
  private static final String EXIT_KEYWORD = "EXIT";

  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    System.out.println(INSTRUCTIONS);
    while (sc.hasNext()) {
      String input = sc.nextLine();
      if (input.equalsIgnoreCase(EXIT_KEYWORD)) {
        break;
      }
      System.out.println(HelloThreads.capitalize(input));
    }
  }
}
