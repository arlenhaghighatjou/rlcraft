package net.silentchaos512.scalinghealth.loot;

import net.silentchaos512.lib.registry.SRegistry;
import net.silentchaos512.scalinghealth.loot.properties.PropertyBlight;
import net.silentchaos512.scalinghealth.loot.properties.PropertyDifficulty;

public class ModLootStuff {
   public ModLootStuff() {
   }

   public static void registerAll(SRegistry registry) {
      registry.registerLootEntityProperty(new PropertyBlight.Serializer());
      registry.registerLootEntityProperty(new PropertyDifficulty.Serializer());
   }
}
