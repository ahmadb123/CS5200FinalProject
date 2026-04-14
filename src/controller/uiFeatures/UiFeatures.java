package controller.uiFeatures;

/**
 * Top-level UI features interface that glues the view to the controller
 * and model layers. Forward path goes view -> features -> controller ->
 * model; updates flow back the other way, view &lt;- controller &lt;- model.
 * It extends four smaller sub-feature interfaces (auth, profile, orders,
 * admin) so each area has its own file instead of one huge interface.
 * GuiController is the single class that implements UiFeatures.
 */
public interface UiFeatures extends AuthFeatures,
    ProfileFeatures, OrderFeatures,
    AdminFeatures {
}
