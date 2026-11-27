package knightminer.inspirations.plugins;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ObjectHolderRegistry;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import net.minecraft.block.Block;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

public class RatsPlugin extends PulseBase {
   public void handlePulseEvent(Object evt) {
      if (evt instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)evt);
      }

   }

   public static final String pulseID = "RatsPlugin";
   public static final String requiredModID = "rats";
   public static Block milkCauldron = null;

   public RatsPlugin() {
   }

   public void init(FMLInitializationEvent event) {
      if (Config.enableCauldronFluids) {
         Fluid milk = FluidRegistry.getFluid("milk");
         if (milkCauldron != null && milk != null) {
            InspirationsRegistry.registerFullCauldron(milkCauldron.getDefaultState(), ICauldronRecipe.CauldronState.fluid(milk));
         } else if (milkCauldron == null) {
            Inspirations.log.error("Skipping registering Rats plugin, failed to find Rats milk cauldron");
         } else {
            Inspirations.log.info("Skipping registering Rats plugin, milk not registered");
         }

      }
   }

   public static void registerHolders() {
      ObjectHolderRegistry.INSTANCE.register(Block.class, new ResourceLocation("rats:cauldron_milk"), v -> milkCauldron = v);
   }
}
