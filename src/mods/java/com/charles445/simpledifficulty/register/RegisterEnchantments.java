package com.charles445.simpledifficulty.register;

import com.charles445.simpledifficulty.api.SDEnchantments;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.enchantment.EnchantmentArmorTemperature;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class RegisterEnchantments {
   public RegisterEnchantments() {
   }

   @EventBusSubscriber(
      modid = "simpledifficulty"
   )
   public static class Registrar {
      public Registrar() {
      }

      @SubscribeEvent
      public static void registerEnchantments(Register<Enchantment> event) {
         IForgeRegistry<Enchantment> registry = event.getRegistry();
         SDEnchantments.chilling = registerAs("chilling", new EnchantmentArmorTemperature(), registry);
         SDEnchantments.heating = registerAs("heating", new EnchantmentArmorTemperature(), registry);
      }

      private static Enchantment registerAs(String name, Enchantment newEnchantment, IForgeRegistry<Enchantment> registry) {
         newEnchantment.setName(name);
         newEnchantment.setRegistryName("simpledifficulty", name);
         if (ModConfig.server.miscellaneous.registerEnchantments) {
            registry.register(newEnchantment);
         }

         SDEnchantments.enchantments.put(name, newEnchantment);
         return newEnchantment;
      }
   }
}
