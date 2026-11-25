package gigaherz.toolbelt.common;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
   public static final int BELT = 0;
   public static final int BELT_SLOT = 1;

   public GuiHandler() {
   }

   @Nullable
   public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
      switch(id) {
      case 0:
         ItemStack heldItem = player.getHeldItem(EnumHand.values()[x]);
         if (heldItem.getCount() > 0) {
            int blockedSlot = -1;
            if (player.getHeldItemMainhand() == heldItem) {
               blockedSlot = player.inventory.currentItem;
            }

            return new ContainerBelt(player.inventory, blockedSlot, heldItem);
         }
      default:
         return null;
      case 1:
         return new ContainerBeltSlot(player.inventory, !player.world.isRemote, player);
      }
   }

   @Nullable
   public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
      switch(id) {
      case 0:
         ItemStack heldItem = player.getHeldItem(EnumHand.values()[x]);
         if (heldItem.getCount() > 0) {
            int blockedSlot = -1;
            if (player.getHeldItemMainhand() == heldItem) {
               blockedSlot = player.inventory.currentItem;
            }

            return new GuiBelt(player.inventory, blockedSlot, heldItem);
         }
      default:
         return null;
      case 1:
         return new GuiBeltSlot(player);
      }
   }
}
