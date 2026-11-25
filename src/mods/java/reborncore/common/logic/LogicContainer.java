package reborncore.common.logic;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import reborncore.common.container.RebornContainer;

public class LogicContainer extends RebornContainer {
   @Nonnull
   LogicController logicController;
   @Nonnull
   EntityPlayer player;

   public LogicContainer(EntityPlayer player, LogicController logicController) {
      this.player = player;
      this.logicController = logicController;
      if (logicController.getSlots() != null) {
         for(Slot s : logicController.getSlots()) {
            this.addSlotToContainer(s);
         }
      }

      this.drawPlayersInv(player, logicController.inventoryOffsetX(), logicController.inventoryOffsetY());
      this.drawPlayersHotBar(player, logicController.inventoryOffsetX(), logicController.inventoryOffsetY() + 58);
   }

   public boolean canInteractWith(EntityPlayer playerIn) {
      return true;
   }
}
