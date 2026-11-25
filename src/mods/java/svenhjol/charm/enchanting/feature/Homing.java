package svenhjol.charm.enchanting.feature;

import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import svenhjol.charm.enchanting.enchantment.EnchantmentHoming;
import svenhjol.meson.Feature;

public class Homing extends Feature {
   public static EnchantmentHoming enchantment;
   public static int minEnchantability;
   public static int range;
   public static int damage;

   public Homing() {
   }

   public String getDescription() {
      return "A hoe with the Homing enchantment is attracted to ore of the same ingots that make up the head of the hoe.\nRight click underground.  If you hear a sound, you can follow it to the ore.";
   }

   public void configure() {
      super.configure();
      minEnchantability = 15;
      damage = 200;
      range = 6;
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      enchantment = new EnchantmentHoming();
   }

   @SubscribeEvent
   public void onBlockInteract(RightClickBlock event) {
      enchantment.onBlockInteract(event);
   }

   public boolean hasSubscriptions() {
      return true;
   }
}
