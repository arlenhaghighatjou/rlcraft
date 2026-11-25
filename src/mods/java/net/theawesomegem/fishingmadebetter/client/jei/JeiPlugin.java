package net.theawesomegem.fishingmadebetter.client.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import net.theawesomegem.fishingmadebetter.common.configuration.ConfigurationManager;

@JEIPlugin
public class JeiPlugin implements IModPlugin {
   public JeiPlugin() {
   }

   public void register(IModRegistry registry) {
      registry.addRecipes(FishScalingRecipeMaker.getFishScalingRecipes(), "minecraft.crafting");
      registry.addRecipes(FishSlicingRecipeMaker.getFishSlicingRecipes(), "minecraft.crafting");
      registry.addRecipes(FishingRodBaitRecipeMaker.getFishingRodBaitRecipes(), "minecraft.crafting");
      registry.addRecipes(FishingRodAttachmentRecipeMaker.getFishingRodAttachmentRecipes(), "minecraft.crafting");
      registry.addRecipes(BaitBucketRecipeMaker.getBaitBucketRecipes(), "minecraft.crafting");
      if (ConfigurationManager.server.enableFishBucket) {
         registry.addRecipes(FishBucketRecipeMaker.getFishBucketRecipes(), "minecraft.crafting");
      }

   }
}
