package view;

import controller.uiFeatures.UiFeatures;

/**
 * GUI-specific view contract. extends IWorldBuyView with methods that only
 * make sense for the Swing UI: addFeatures (wires button listeners to features)
 * and refreshView (forces a repaint after programmatic state changes).
 * the controller holds this as its view reference.
 */
public interface IguiViewWorldBuy extends IWorldBuyView {

  /**
   * receives the features reference from the controller and wires up
   * button action listeners so each click calls the right feature method.
   *
   * @param features the full feature set to wire into the UI.
   */
  void addFeatures(UiFeatures features);

  /**
   * forces the view to repaint. called by the controller after state changes
   * that the view wouldn't otherwise know about.
   */
  void refreshView();
}
