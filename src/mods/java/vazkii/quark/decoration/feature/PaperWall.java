package vazkii.quark.decoration.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.decoration.block.BlockPaperWall;

public class PaperWall extends Feature {
   public static Block paper_wall;
   public static Block paper_wall_big;
   public static Block paper_wall_sakura;

   public PaperWall() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      paper_wall = new BlockPaperWall("paper_wall");
      paper_wall_big = new BlockPaperWall("paper_wall_big");
      paper_wall_sakura = new BlockPaperWall("paper_wall_sakura");
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(paper_wall, 6), new Object[]{"SSS", "PPP", "SSS", 'S', ProxyRegistry.newStack(Items.STICK), 'P', ProxyRegistry.newStack(Items.PAPER)});
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(paper_wall_big, 4), new Object[]{"PP", "PP", 'P', ProxyRegistry.newStack(paper_wall)});
      RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(paper_wall_sakura, 1), new Object[]{ProxyRegistry.newStack(paper_wall_big), "treeSapling"});
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
