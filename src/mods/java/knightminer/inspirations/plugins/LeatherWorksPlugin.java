package knightminer.inspirations.plugins;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ObjectHolderRegistry;
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
import slimeknights.mantle.util.RecipeMatch;

public class LeatherWorksPlugin extends PulseBase {
   public void handlePulseEvent(Object evt) {
      if (evt instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)evt);
      }

   }

   public static final String pulseID = "LeatherWorks";
   public static final String requiredModID = "leatherworks";
   public static Item tanninBall = null;
   public static Item tanninBottle = null;
   public static Item preparedHide = null;
   public static Item soakedHide = null;
   public static Item washedHide = null;

   public LeatherWorksPlugin() {
   }

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

   public static void registerHolders() {
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("leatherworks:tannin_ball"), v -> tanninBall = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("leatherworks:tannin_bottle"), v -> tanninBottle = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("leatherworks:crafting_leather_scraped"), v -> preparedHide = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("leatherworks:crafting_leather_soaked"), v -> soakedHide = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("leatherworks:crafting_leather_washed"), v -> washedHide = v);
   }
}
