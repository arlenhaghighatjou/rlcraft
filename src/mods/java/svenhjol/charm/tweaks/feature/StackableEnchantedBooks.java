package svenhjol.charm.tweaks.feature;

import net.minecraft.init.Items;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import svenhjol.meson.Feature;

public class StackableEnchantedBooks extends Feature {
   public static int stackSize;

   public StackableEnchantedBooks() {
   }

   public String getDescription() {
      return "Enchanted Books can stack (up to 16).";
   }

   public void configure() {
      super.configure();
      stackSize = 16;
   }

   public void init(FMLInitializationEvent event) {
      Items.ENCHANTED_BOOK.setMaxStackSize(stackSize);
   }
}
