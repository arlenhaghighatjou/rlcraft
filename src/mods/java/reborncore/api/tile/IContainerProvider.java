package reborncore.api.tile;

import reborncore.common.container.RebornContainer;

public interface IContainerProvider extends IInventoryProvider {
   RebornContainer getContainer();
}
