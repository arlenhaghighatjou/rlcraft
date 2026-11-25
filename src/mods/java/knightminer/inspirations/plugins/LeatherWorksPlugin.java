package knightminer.inspirations.plugins;

import com.google.common.eventbus.Subscribe;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.recipe.cauldron.CauldronFluidRecipe;
import knightminer.inspirations.library.recipe.cauldron.CauldronFluidTransformRecipe;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.mantle.util.RecipeMatch;

@Pulse(
   id = "LeatherWorks",
   description = "Adds recipes for Leather Works items",
   modsRequired = "leatherworks",
   pulsesRequired = "InspirationsRecipes"
)
public class LeatherWorksPlugin extends PulseBase {
   public static final String pulseID = "LeatherWorks";
   public static final String requiredModID = "leatherworks";
   @ObjectHolder("leatherworks:tannin_ball")
   public static final Item tanninBall = null;
   @ObjectHolder("leatherworks:tannin_bottle")
   public static final Item tanninBottle = null;
   @ObjectHolder("leatherworks:crafting_leather_scraped")
   public static final Item preparedHide = null;
   @ObjectHolder("leatherworks:crafting_leather_soaked")
   public static final Item soakedHide = null;
   @ObjectHolder("leatherworks:crafting_leather_washed")
   public static final Item washedHide = null;

   public LeatherWorksPlugin() {
   }

   @Subscribe
   public void init(FMLInitializationEvent e) {
      if (Config.enableCauldronFluids) {
         Fluid tannin = FluidRegistry.getFluid("leatherworks:tannin");
         if (tannin != null) {
            if (tanninBall != null) {
               InspirationsRegistry.addCauldronRecipe(new CauldronFluidTransformRecipe(RecipeMatch.of(tanninBall), FluidRegistry.WATER, tannin, false));
            }

            if (tanninBottle != null) {
               InspirationsRegistry.addCauldronFluidItem(new ItemStack(tanninBottle), new ItemStack(Items.GLASS_BOTTLE), tannin, 1);
            }

            if (preparedHide != null && soakedHide != null && washedHide != null) {
               InspirationsRegistry.addCauldronRecipe(new CauldronFluidRecipe(RecipeMatch.of(preparedHide, 1), FluidRegistry.WATER, new ItemStack(washedHide), false, 1));
               InspirationsRegistry.addCauldronRecipe(new CauldronFluidRecipe(RecipeMatch.of(washedHide, 1), tannin, new ItemStack(soakedHide), false, 1));
            }
         }

      }
   }
}
