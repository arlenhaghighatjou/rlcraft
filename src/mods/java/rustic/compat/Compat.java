package rustic.compat;

import forestry.api.fuels.EngineBronzeFuel;
import forestry.api.fuels.FuelManager;
import forestry.api.recipes.ICarpenterRecipe;
import forestry.api.recipes.IFermenterRecipe;
import forestry.api.recipes.RecipeManagers;
import forestry.core.fluids.Fluids;
import forestry.core.recipes.RecipeUtil;
import forestry.core.recipes.ShapedRecipeCustom;
import forestry.factory.recipes.CarpenterRecipe;
import forestry.factory.recipes.FermenterRecipe;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.common.blocks.ModBlocks;
import rustic.common.items.ModItems;
import rustic.compat.dynamictrees.DynamicTreesCompat;
import rustic.core.Rustic;

public class Compat {
   public static final String QUARK = "quark";

   public Compat() {
   }

   @Method(
      modid = "forestry"
   )
   public static void doForestryCompat() {
      if (FuelManager.bronzeEngineFuel != null) {
         Fluid fluid = FluidRegistry.getFluid("honey");
         FuelManager.bronzeEngineFuel.put(fluid, new EngineBronzeFuel(fluid, 20, 20, 1));
         fluid = FluidRegistry.getFluid("oliveoil");
         FuelManager.bronzeEngineFuel.put(fluid, new EngineBronzeFuel(fluid, 30, 30, 1));
         fluid = FluidRegistry.getFluid("applejuice");
         FuelManager.bronzeEngineFuel.put(fluid, new EngineBronzeFuel(fluid, 10, 10, 1));
         fluid = FluidRegistry.getFluid("grapejuice");
         FuelManager.bronzeEngineFuel.put(fluid, new EngineBronzeFuel(fluid, 10, 10, 1));
         fluid = FluidRegistry.getFluid("ironberryjuice");
         FuelManager.bronzeEngineFuel.put(fluid, new EngineBronzeFuel(fluid, 10, 10, 1));
         fluid = FluidRegistry.getFluid("wildberryjuice");
         FuelManager.bronzeEngineFuel.put(fluid, new EngineBronzeFuel(fluid, 10, 10, 1));
      }

      if (RecipeManagers.fermenterManager != null) {
         RecipeUtil.addFermenterRecipes(new ItemStack(ModBlocks.SAPLING, 1, 0), 250, Fluids.BIOMASS);
         RecipeUtil.addFermenterRecipes(new ItemStack(ModBlocks.SAPLING, 1, 1), 250, Fluids.BIOMASS);
         RecipeUtil.addFermenterRecipes(new ItemStack(ModBlocks.APPLE_SAPLING, 1, 0), 250, Fluids.BIOMASS);
      }

      if (RecipeManagers.carpenterManager != null) {
         List<ICarpenterRecipe> carpenterToAdd = new ArrayList();

         for(ICarpenterRecipe recipe : RecipeManagers.carpenterManager.recipes()) {
            if (recipe.getFluidResource() != null && recipe.getFluidResource().getFluid().getName().equals("for.honey")) {
               if (recipe.getCraftingGridRecipe() instanceof ShapedRecipeCustom) {
                  ShapedRecipeCustom shapedRecipe = (ShapedRecipeCustom)recipe.getCraftingGridRecipe();
                  FluidStack fluid = new FluidStack(FluidRegistry.getFluid("honey"), recipe.getFluidResource().amount);
                  int packingTime = recipe.getPackagingTime();
                  ItemStack box = recipe.getBox();
                  carpenterToAdd.add(new CarpenterRecipe(packingTime, fluid, box, shapedRecipe));
               }
            } else if (recipe.getFluidResource() != null && recipe.getFluidResource().getFluid().getName().equals("seed.oil") && recipe.getCraftingGridRecipe() instanceof ShapedRecipeCustom) {
               ShapedRecipeCustom shapedRecipe = (ShapedRecipeCustom)recipe.getCraftingGridRecipe();
               FluidStack fluid = new FluidStack(FluidRegistry.getFluid("oliveoil"), recipe.getFluidResource().amount);
               int packingTime = recipe.getPackagingTime();
               ItemStack box = recipe.getBox();
               carpenterToAdd.add(new CarpenterRecipe(packingTime, fluid, box, shapedRecipe));
            }
         }

         for(ICarpenterRecipe recipe : carpenterToAdd) {
            RecipeManagers.carpenterManager.addRecipe(recipe);
         }
      }

      if (RecipeManagers.fermenterManager != null) {
         List<IFermenterRecipe> fermenterToAdd = new ArrayList();

         for(IFermenterRecipe recipe : RecipeManagers.fermenterManager.recipes()) {
            if (recipe.getFluidResource() != null && recipe.getFluidResource().getFluid().getName().equals("for.honey")) {
               Fluid fluidOut = recipe.getOutput();
               FluidStack fluidIn = new FluidStack(FluidRegistry.getFluid("honey"), recipe.getFluidResource().amount);
               int value = recipe.getFermentationValue();
               float mod = recipe.getModifier();
               ItemStack itemIn = recipe.getResource();
               String oreItemIn = recipe.getResourceOreName();
               if (!itemIn.isEmpty()) {
                  fermenterToAdd.add(new FermenterRecipe(itemIn, value, mod, fluidOut, fluidIn));
               } else if (!oreItemIn.isEmpty()) {
                  fermenterToAdd.add(new FermenterRecipe(oreItemIn, value, mod, fluidOut, fluidIn));
               }
            }

            if (recipe.getFluidResource() != null && recipe.getFluidResource().getFluid().getName().equals("juice")) {
               Fluid fluidOut = recipe.getOutput();
               FluidStack fluidIn = new FluidStack(FluidRegistry.getFluid("applejuice"), recipe.getFluidResource().amount);
               int value = recipe.getFermentationValue();
               float mod = recipe.getModifier();
               ItemStack itemIn = recipe.getResource();
               String oreItemIn = recipe.getResourceOreName();
               if (!itemIn.isEmpty()) {
                  fermenterToAdd.add(new FermenterRecipe(itemIn, value, mod, fluidOut, fluidIn));
                  fluidIn = new FluidStack(FluidRegistry.getFluid("applejuice"), recipe.getFluidResource().amount);
                  fermenterToAdd.add(new FermenterRecipe(itemIn, value, mod, fluidOut, fluidIn));
                  fluidIn = new FluidStack(FluidRegistry.getFluid("grapejuice"), recipe.getFluidResource().amount);
                  fermenterToAdd.add(new FermenterRecipe(itemIn, value, mod, fluidOut, fluidIn));
                  fluidIn = new FluidStack(FluidRegistry.getFluid("ironberryjuice"), recipe.getFluidResource().amount);
                  fermenterToAdd.add(new FermenterRecipe(itemIn, value, 1.0F, fluidOut, fluidIn));
                  fluidIn = new FluidStack(FluidRegistry.getFluid("wildberryjuice"), recipe.getFluidResource().amount);
                  fermenterToAdd.add(new FermenterRecipe(itemIn, value, 1.25F, fluidOut, fluidIn));
               } else if (!oreItemIn.isEmpty()) {
                  fermenterToAdd.add(new FermenterRecipe(oreItemIn, value, mod, fluidOut, fluidIn));
                  fluidIn = new FluidStack(FluidRegistry.getFluid("applejuice"), recipe.getFluidResource().amount);
                  fermenterToAdd.add(new FermenterRecipe(oreItemIn, value, mod, fluidOut, fluidIn));
                  fluidIn = new FluidStack(FluidRegistry.getFluid("grapejuice"), recipe.getFluidResource().amount);
                  fermenterToAdd.add(new FermenterRecipe(oreItemIn, value, mod, fluidOut, fluidIn));
                  fluidIn = new FluidStack(FluidRegistry.getFluid("ironberryjuice"), recipe.getFluidResource().amount);
                  fermenterToAdd.add(new FermenterRecipe(oreItemIn, value, 1.0F, fluidOut, fluidIn));
                  fluidIn = new FluidStack(FluidRegistry.getFluid("wildberryjuice"), recipe.getFluidResource().amount);
                  fermenterToAdd.add(new FermenterRecipe(oreItemIn, value, 1.25F, fluidOut, fluidIn));
               }
            }
         }

         for(IFermenterRecipe recipe : fermenterToAdd) {
            RecipeManagers.fermenterManager.addRecipe(recipe);
         }
      }

      if (RecipeManagers.squeezerManager != null) {
         if (FluidRegistry.getFluid("seed.oil") != null) {
            RecipeManagers.squeezerManager.addRecipe(10, new ItemStack(ModItems.CHILI_PEPPER_SEEDS), new FluidStack(FluidRegistry.getFluid("seed.oil"), 10));
            RecipeManagers.squeezerManager.addRecipe(10, new ItemStack(ModItems.TOMATO_SEEDS), new FluidStack(FluidRegistry.getFluid("seed.oil"), 10));
            RecipeManagers.squeezerManager.addRecipe(10, new ItemStack(ModBlocks.APPLE_SEEDS), new FluidStack(FluidRegistry.getFluid("seed.oil"), 10));
            RecipeManagers.squeezerManager.addRecipe(10, new ItemStack(ModBlocks.GRAPE_STEM), new FluidStack(FluidRegistry.getFluid("seed.oil"), 10));
         }

         if (FluidRegistry.getFluid("juice") != null) {
            RecipeManagers.squeezerManager.addRecipe(10, new ItemStack(ModItems.TOMATO), new FluidStack(FluidRegistry.getFluid("juice"), 200));
            RecipeManagers.squeezerManager.addRecipe(10, new ItemStack(ModItems.CHILI_PEPPER), new FluidStack(FluidRegistry.getFluid("juice"), 100));
         }

         RecipeManagers.squeezerManager.addRecipe(10, new ItemStack(ModItems.OLIVES), new FluidStack(FluidRegistry.getFluid("oliveoil"), 250));
         RecipeManagers.squeezerManager.addRecipe(10, new ItemStack(ModItems.IRONBERRIES), new FluidStack(FluidRegistry.getFluid("ironberryjuice"), 250), new ItemStack(ModItems.IRON_DUST_TINY), 5);
         RecipeManagers.squeezerManager.addRecipe(10, new ItemStack(ModItems.WILDBERRIES), new FluidStack(FluidRegistry.getFluid("wildberryjuice"), 250));
         RecipeManagers.squeezerManager.addRecipe(10, new ItemStack(ModItems.GRAPES), new FluidStack(FluidRegistry.getFluid("grapejuice"), 250));
         RecipeManagers.squeezerManager.addRecipe(10, new ItemStack(ModItems.HONEYCOMB), new FluidStack(FluidRegistry.getFluid("honey"), 250));
      }

      Rustic.logger.info("Initialized Forestry compat module");
   }

   @Method(
      modid = "dynamictrees"
   )
   public static void preInitDynamicTreesCompat() {
      DynamicTreesCompat.preInit();
   }

   @Method(
      modid = "dynamictrees"
   )
   public static void initDynamicTreesCompat() {
      DynamicTreesCompat.init();
   }

   @Method(
      modid = "dynamictrees"
   )
   @SideOnly(Side.CLIENT)
   public static void preInitDynamicTreesClientCompat() {
      DynamicTreesCompat.clientPreInit();
   }

   @Method(
      modid = "dynamictrees"
   )
   @SideOnly(Side.CLIENT)
   public static void initDynamicTreesClientCompat() {
      DynamicTreesCompat.clientInit();
   }
}
