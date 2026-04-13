package controller.uiFeatures;

/**
 * Master UI features contract. connects the view to the controller and model.
 * forward path: view -> features -> controller -> model.
 * update path: view <- (push updates) <- controller <- model.
 * this interface extends four parallel sub-feature interfaces so each concern
 * (auth, profile, orders, admin) has its own smaller contract, avoiding one
 * overly large interface. the single controller (GuiController) implements UiFeatures.
 */
public interface UiFeatures extends AuthFeatures,
    ProfileFeatures, OrderFeatures,
    AdminFeatures {
}
