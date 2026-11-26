package c4.comforts.common.items;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ObjectHolderRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ComfortsItems {
   public static ItemSleepingBag SLEEPING_BAG;
   public static ItemHammock HAMMOCK;

   public ComfortsItems() {
   }

   @SideOnly(Side.CLIENT)
   public static void initModels() {
      SLEEPING_BAG.initModel();
      HAMMOCK.initModel();
   }

   public static void registerHolders() {
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("comforts:sleeping_bag"), v -> SLEEPING_BAG = (ItemSleepingBag)v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("comforts:hammock"), v -> HAMMOCK = (ItemHammock)v);
   }
}
