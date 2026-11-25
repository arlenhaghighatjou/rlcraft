package gigaherz.toolbelt.shadow.commons.state;

import gigaherz.toolbelt.shadow.commons.ItemRegistered;

public abstract class ItemStateful extends ItemRegistered implements StatefulItem {
   private IItemStateManager stateManager = this.createStateManager();

   public ItemStateful(String name) {
      super(name);
   }

   public IItemStateManager getStateManager() {
      return this.stateManager;
   }

   public IItemState getDefaultState() {
      return this.stateManager.getDefaultState();
   }

   public void setDefaultState(IItemState defaultState) {
      this.stateManager.setDefaultState(defaultState);
   }

   public abstract IItemStateManager createStateManager();
}
