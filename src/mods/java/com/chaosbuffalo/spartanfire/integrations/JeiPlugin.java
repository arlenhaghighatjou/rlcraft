package com.chaosbuffalo.spartanfire.integrations;

import com.chaosbuffalo.spartanfire.ForgeConfigHandler;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;

@JEIPlugin
public class JeiPlugin implements IModPlugin {
   public JeiPlugin() {
   }

   public void register(IModRegistry registry) {
      if (ForgeConfigHandler.general.registerFlameIceThrowingRecipes) {
         registry.addRecipes(FireIceThrowingRecipeMaker.getFireIceThrowingRecipes(), "minecraft.crafting");
      }

      if (ForgeConfigHandler.general.registerVenomThrowingRecipes) {
         registry.addRecipes(VenomThrowingRecipeMaker.getVenomThrowingRecipes(), "minecraft.crafting");
      }

   }
}
