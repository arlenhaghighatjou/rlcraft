package noppes.vc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import noppes.vc.constants.GuiType;
import noppes.vc.constants.PacketClient;

public class CommonUtils {
   public CommonUtils() {
   }

   public static void ConsumeItemStack(int i, EntityPlayer player) {
      ItemStack item = player.inventory.getCurrentItem();
      if (!player.capabilities.isCreativeMode && item != null && !item.isEmpty()) {
         item.shrink(1);
         if (item.getCount() <= 0) {
            player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
         }

      }
   }

   public static void OpenGui(EntityPlayer player, GuiType gui, int x, int y, int z) {
      if (VariedCommodities.proxy.getServerGuiElement(gui.ordinal(), player, player.world, x, y, z) != null) {
         player.openGui(VariedCommodities.Instance, gui.ordinal(), player.world, x, y, z);
      } else {
         Server.sendDataChecked((EntityPlayerMP)player, PacketClient.GUI, gui.ordinal(), x, y, z);
      }

   }

   public static ItemStack ChangeItemStack(ItemStack is, Item item) {
      NBTTagCompound comp = is.writeToNBT(new NBTTagCompound());
      ResourceLocation resourcelocation = (ResourceLocation)Item.REGISTRY.getNameForObject(item);
      comp.setString("id", resourcelocation == null ? "minecraft:air" : resourcelocation.toString());
      return new ItemStack(comp);
   }
}
