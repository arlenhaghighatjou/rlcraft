package knightminer.inspirations.plugins;

import com.google.common.eventbus.Subscribe;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.recipes.recipe.ArmorClearRecipe;
import knightminer.inspirations.recipes.recipe.ArmorDyeingCauldronRecipe;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import slimeknights.mantle.pulsar.pulse.Pulse;

@Pulse(
   id = "TwilightForestPlugin",
   description = "Supports dyeing Twilight Forest armor in the Inspirations cauldron.",
   modsRequired = "twilightforest",
   pulsesRequired = "InspirationsRecipes"
)
public class TwilightForestPlugin {
   public static final String pulseID = "TwilightForestPlugin";
   public static final String requiredModID = "twilightforest";

   public TwilightForestPlugin() {
   }

   @Subscribe
   public void init(FMLInitializationEvent event) {
      if (Config.enableCauldronRecipes) {
         try {
            ArmorMaterial material = ArmorMaterial.valueOf("ARCTIC");
            InspirationsRegistry.addCauldronRecipe(new ArmorClearRecipe(material));
            if (Config.enableCauldronDyeing) {
               InspirationsRegistry.addCauldronRecipe(new ArmorDyeingCauldronRecipe(material));
            }
         } catch (IllegalArgumentException var3) {
            Inspirations.log.error("Skipping registering Twilight Forest plugin, cannot find arctic armor material");
         }

      }
   }
}
