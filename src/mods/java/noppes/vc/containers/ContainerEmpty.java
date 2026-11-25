package noppes.vc.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class ContainerEmpty extends Container {
   public ContainerEmpty() {
   }

   public boolean canInteractWith(EntityPlayer var1) {
      return false;
   }
}
