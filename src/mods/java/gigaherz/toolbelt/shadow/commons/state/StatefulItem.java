package gigaherz.toolbelt.shadow.commons.state;

public interface StatefulItem {
   IItemStateManager getStateManager();

   IItemState getDefaultState();
}
