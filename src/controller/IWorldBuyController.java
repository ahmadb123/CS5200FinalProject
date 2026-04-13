package controller;

/**
 * Top-level controller contract for WorldBuy.
 * the main class picks one of the implementations (GuiLauncher or CliLauncher)
 * based on command-line arguments and calls run() to start the application.
 * this is the entry point into the controller + view layers.
 */
public interface IWorldBuyController {

  /**
   * starts the application. wires models, view, and any controllers together,
   * then hands control to the chosen UI.
   */
  void run();
}
