package vazkii.quark.tweaks.feature;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.world.feature.RevampStoneGen;

public class ImprovedStoneToolCrafting extends Feature {
   public static final String mat = "materialStoneTool";

   public ImprovedStoneToolCrafting() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      String[][] patterns = new String[][]{{"XXX", " # ", " # "}, {"X", "#", "#"}, {"XX", "X#", " #"}, {"XX", " #", " #"}, {"X", "X", "#"}};
      Item[] items = new Item[]{Items.STONE_PICKAXE, Items.STONE_SHOVEL, Items.STONE_AXE, Items.STONE_HOE, Items.STONE_SWORD};

      for(int i = 0; i < patterns.length; ++i) {
         RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(items[i]), new Object[]{patterns[i][0], patterns[i][1], patterns[i][2], 'X', "materialStoneTool", '#', ProxyRegistry.newStack(Items.STICK)});
      }

      this.addOreDict();
   }

   private void addOreDict() {
      addOreDict("materialStoneTool", ProxyRegistry.newStack(Items.FLINT));
      addOreDict("materialStoneTool", ProxyRegistry.newStack(Blocks.STONE));
      addOreDict("materialStoneTool", ProxyRegistry.newStack(Blocks.STONE, 1, 1));
      addOreDict("materialStoneTool", ProxyRegistry.newStack(Blocks.STONE, 1, 3));
      addOreDict("materialStoneTool", ProxyRegistry.newStack(Blocks.STONE, 1, 5));
      addOreDict("materialStoneTool", ProxyRegistry.newStack(Blocks.COBBLESTONE));
      if (RevampStoneGen.limestone != null) {
         addOreDict("materialStoneTool", ProxyRegistry.newStack(RevampStoneGen.limestone));
      }

      if (RevampStoneGen.marble != null) {
         addOreDict("materialStoneTool", ProxyRegistry.newStack(RevampStoneGen.marble));
      }

   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }

   public String getFeatureIngameConfigName() {
      return "Better Stone Tool Crafting";
   }
}
