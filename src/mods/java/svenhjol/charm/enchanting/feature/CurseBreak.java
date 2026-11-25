package svenhjol.charm.enchanting.feature;

import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import svenhjol.charm.enchanting.enchantment.EnchantmentCurseBreak;
import svenhjol.meson.Feature;

public class CurseBreak extends Feature {
   public static EnchantmentCurseBreak enchantment;
   public static int xpCost;

   public CurseBreak() {
   }

   public String getDescription() {
      return "Combine the Curse Break enchanted book with a cursed item on an anvil to remove the curse.";
   }

   public void configure() {
      super.configure();
      xpCost = this.propInt("XP cost", "Amount of XP (levels) to remove a curse from an item", 1);
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      enchantment = new EnchantmentCurseBreak();
   }

   @SubscribeEvent
   public void onAnvilUpdate(AnvilUpdateEvent event) {
      enchantment.onAnvilUpdate(event);
   }

   public boolean hasSubscriptions() {
      return true;
   }
}
