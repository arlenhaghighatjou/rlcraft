package noppes.vc.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.MathHelper;

public class ContainerBasic extends Container {
   private int posX;
   private int posZ;

   public ContainerBasic(EntityPlayer player) {
      this.posX = MathHelper.floor(player.posX);
      this.posZ = MathHelper.floor(player.posZ);
      player.motionX = 0.0D;
      player.motionZ = 0.0D;
   }

   public boolean canInteractWith(EntityPlayer player) {
      return !player.isDead && this.posX == MathHelper.floor(player.posX) && this.posZ == MathHelper.floor(player.posZ);
   }
}
