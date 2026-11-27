package com.charles445.simpledifficulty.register;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.charles445.simpledifficulty.api.SDPotions;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.potion.PotionHyperthermia;
import com.charles445.simpledifficulty.potion.PotionHypothermia;
import com.charles445.simpledifficulty.potion.PotionParasites;
import com.charles445.simpledifficulty.potion.PotionResistCold;
import com.charles445.simpledifficulty.potion.PotionResistHeat;
import com.charles445.simpledifficulty.potion.PotionThirsty;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.registries.IForgeRegistry;

public class RegisterPotions {
   public RegisterPotions() {
   }

   public static class Registrar {
      public Registrar() {
      }

      public static void register(EventBus bus) {
         bus.registerGeneric(Register.class, Potion.class, Registrar::register);
         bus.registerGeneric(Register.class, PotionType.class, Registrar::registerTypes);
      }

      public static void register(Register<Potion> event) {
         IForgeRegistry<Potion> registry = event.getRegistry();
         SDPotions.hyperthermia = registerAs("hyperthermia", new PotionHyperthermia(), registry);
         SDPotions.hypothermia = registerAs("hypothermia", new PotionHypothermia(), registry);
         SDPotions.thirsty = registerAs("thirsty", new PotionThirsty(), registry);
         SDPotions.parasites = registerAs("parasites", new PotionParasites(), registry);
         SDPotions.cold_resist = registerAs("cold_resist", new PotionResistCold(), registry);
         SDPotions.heat_resist = registerAs("heat_resist", new PotionResistHeat(), registry);
      }

      public static void registerTypes(Register<PotionType> event) {
         IForgeRegistry<PotionType> registry = event.getRegistry();
         SDPotions.cold_resist_type = registerTypeAs("cold_resist_type", SDPotions.cold_resist, ModConfig.server.miscellaneous.resistancePotionDurationShort, registry);
         SDPotions.long_cold_resist_type = registerTypeAs("long_cold_resist_type", SDPotions.cold_resist, ModConfig.server.miscellaneous.resistancePotionDurationLong, registry);
         SDPotions.heat_resist_type = registerTypeAs("heat_resist_type", SDPotions.heat_resist, ModConfig.server.miscellaneous.resistancePotionDurationShort, registry);
         SDPotions.long_heat_resist_type = registerTypeAs("long_heat_resist_type", SDPotions.heat_resist, ModConfig.server.miscellaneous.resistancePotionDurationLong, registry);
      }

      private static PotionType registerTypeAs(String name, Potion potion, int duration, IForgeRegistry<PotionType> registry) {
         PotionType potionType = new PotionType(new PotionEffect[]{new PotionEffect(potion, duration)});
         potionType.setRegistryName("simpledifficulty", name);
         registry.register(potionType);
         SDPotions.potionTypes.put(name, potionType);
         return potionType;
      }

      private static Potion registerAs(String name, Potion potion, IForgeRegistry<Potion> registry) {
         potion.setRegistryName("simpledifficulty", name);
         potion.setPotionName("effect." + name);
         registry.register(potion);
         SDPotions.potions.put(name, potion);
         return potion;
      }
   }
}
