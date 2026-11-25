package svenhjol.charm.tweaks.feature;

import net.minecraft.init.Items;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import svenhjol.meson.Feature;

public class StackablePotions extends Feature {
   public static int stackSize;

   public StackablePotions() {
   }

   public String getDescription() {
      return "Potions and water bottles can stack (up to 16).";
   }

   public void configure() {
      super.configure();
      stackSize = 16;
   }

   public void init(FMLInitializationEvent event) {
      Items.POTIONITEM.setMaxStackSize(stackSize);
      Items.SPLASH_POTION.setMaxStackSize(stackSize);
      Items.LINGERING_POTION.setMaxStackSize(stackSize);
   }
}
