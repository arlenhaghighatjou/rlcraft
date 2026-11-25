package com.oblivioussp.spartanshields.init;

import com.oblivioussp.spartanshields.enchantment.EnchantmentSS;
import com.oblivioussp.spartanshields.enchantment.EnchantmentSpikes;
import com.oblivioussp.spartanshields.util.ConfigHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@EventBusSubscriber
public class EnchantmentRegistry {
   public static EnchantmentSS enchSpikes = new EnchantmentSpikes(Rarity.UNCOMMON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});

   public EnchantmentRegistry() {
   }

   @SubscribeEvent
   public static void register(Register<Enchantment> ev) {
      IForgeRegistry<Enchantment> reg = ev.getRegistry();
      if (ConfigHandler.enableSpikesEnchantment) {
         reg.register(enchSpikes);
      }

   }
}
