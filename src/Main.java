import controller.CliLauncher;
import controller.GuiLauncher;
import controller.IWorldBuyController;

/**
 * Entry point for WorldBuy. parses command-line arguments to pick
 * between GUI and CLI modes, then hands control to the matching launcher.
 * usage:
 *   java Main              -> launches GUI (default)
 *   java Main --mode gui   -> launches GUI
 *   java Main --mode cli   -> launches text CLI
 */
public class Main {

  /**
   * program entry point. reads --mode argument and starts the right launcher.
   *
   * @param args command-line arguments. expects optional --mode gui|cli.
   */
  public static void main(String[] args) {
    String mode = parseMode(args);

    IWorldBuyController launcher;
    if ("cli".equalsIgnoreCase(mode)) {
      launcher = new CliLauncher();
    } else {
      launcher = new GuiLauncher();
    }
    launcher.run();
  }

  /**
   * extracts the value of the --mode flag from the argument array.
   * defaults to "gui" if no --mode is passed.
   *
   * @param args the raw argument array from main.
   * @return the mode value ("gui" or "cli"), or "gui" by default.
   */
  private static String parseMode(String[] args) {
    for (int i = 0; i < args.length - 1; i++) {
      if ("--mode".equalsIgnoreCase(args[i])) {
        return args[i + 1];
      }
    }
    return "gui";
  }
}
