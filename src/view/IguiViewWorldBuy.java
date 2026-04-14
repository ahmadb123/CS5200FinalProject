package view;

import controller.uiFeatures.UiFeatures;

/**
 * GUI view interface. Extends IWorldBuyView with two methods that only
 * make sense for the Swing UI: addFeatures (attaches button listeners to
 * features) and refreshView (forces a repaint after programmatic state
 * changes). The controller keeps an instance of this as its view
 * reference.
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
