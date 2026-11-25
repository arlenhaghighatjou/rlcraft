package vazkii.quark.building.feature;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.block.BlockMod;
import vazkii.arl.block.BlockModSlab;
import vazkii.arl.block.BlockModStairs;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.building.block.BlockNewSandstone;
import vazkii.quark.building.block.slab.BlockVanillaSlab;
import vazkii.quark.building.block.stairs.BlockVanillaStairs;

public class MoreSandstone extends Feature {
   public static BlockMod sandstone_new;
   public static boolean enableStairsAndSlabs;

   public MoreSandstone() {
   }

   public void setupConfig() {
      enableStairsAndSlabs = this.loadPropBool("Enable stairs and slabs", "", true) && GlobalConfig.enableVariants;
   }

   public void preInit(FMLPreInitializationEvent event) {
      sandstone_new = new BlockNewSandstone();
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(sandstone_new, 8, 0), new Object[]{"SSS", "S S", "SSS", 'S', ProxyRegistry.newStack(Blocks.SANDSTONE)});
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(sandstone_new, 4, 1), new Object[]{"SS", "SS", 'S', ProxyRegistry.newStack(sandstone_new, 1, 0)});
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(sandstone_new, 8, 2), new Object[]{"SSS", "S S", "SSS", 'S', ProxyRegistry.newStack(Blocks.RED_SANDSTONE)});
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(sandstone_new, 4, 3), new Object[]{"SS", "SS", 'S', ProxyRegistry.newStack(sandstone_new, 1, 2)});
      if (enableStairsAndSlabs) {
         boolean soulSandstone = ModuleLoader.isFeatureEnabled(SoulSandstone.class);

         for(BlockNewSandstone.Variants variant : BlockNewSandstone.Variants.values()) {
            if (variant.stairs) {
               if (variant.ordinal() > 3 && !soulSandstone) {
                  break;
               }

               IBlockState state = sandstone_new.getDefaultState().withProperty(sandstone_new.getVariantProp(), variant);
               String name = variant.getName() + "_stairs";
               BlockModStairs.initStairs(sandstone_new, variant.ordinal(), new BlockVanillaStairs(name, state));
            }
         }

         for(BlockNewSandstone.Variants variant : BlockNewSandstone.Variants.values()) {
            if (variant.slabs) {
               if (variant.ordinal() > 3 && !soulSandstone) {
                  break;
               }

               IBlockState state = sandstone_new.getDefaultState().withProperty(sandstone_new.getVariantProp(), variant);
               String name = variant.getName() + "_slab";
               BlockModSlab.initSlab(sandstone_new, variant.ordinal(), new BlockVanillaSlab(name, state, false), new BlockVanillaSlab(name, state, true));
            }
         }
      }

   }

   public void postPreInit() {
      if (ModuleLoader.isFeatureEnabled(SoulSandstone.class)) {
         RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(sandstone_new, 8, 4), new Object[]{"SSS", "S S", "SSS", 'S', ProxyRegistry.newStack(SoulSandstone.soul_sandstone)});
         RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(sandstone_new, 4, 5), new Object[]{"SS", "SS", 'S', ProxyRegistry.newStack(sandstone_new, 1, 4)});
      }

   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
