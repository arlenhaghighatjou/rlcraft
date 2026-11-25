package vazkii.quark.misc.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;

public class UtilityRecipes extends Feature {
   public static boolean enableDispenser;
   public static boolean enableRepeater;
   public static boolean enableMinecarts;

   public UtilityRecipes() {
   }

   public void setupConfig() {
      enableDispenser = this.loadPropBool("Dispenser Recipe", "", true);
      enableRepeater = this.loadPropBool("Repeater Recipe", "", true);
      enableMinecarts = this.loadPropBool("Enable Minecarts", "", true);
   }

   public void preInit(FMLPreInitializationEvent event) {
      if (enableDispenser) {
         RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(Blocks.DISPENSER), new Object[]{"ST ", "SDT", "ST ", 'S', "string", 'D', ProxyRegistry.newStack(Blocks.DROPPER), 'T', "stickWood"});
      }

      if (enableRepeater) {
         RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(Items.REPEATER), new Object[]{"R R", "TRT", "SSS", 'S', "stone", 'T', "stickWood", 'R', "dustRedstone"});
      }

      if (enableMinecarts) {
         this.addMinecartBlock(Blocks.CHEST, Items.CHEST_MINECART);
         this.addMinecartBlock(Blocks.FURNACE, Items.FURNACE_MINECART);
         this.addMinecartBlock(Blocks.HOPPER, Items.HOPPER_MINECART);
         this.addMinecartBlock(Blocks.TNT, Items.TNT_MINECART);
         this.addMinecart("chestWood", Items.CHEST_MINECART);
      }

   }

   private void addMinecartBlock(Block block, Item cart) {
      this.addMinecart(ProxyRegistry.newStack(block), cart);
   }

   private void addMinecart(Object block, Item cart) {
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(cart), new Object[]{"IBI", "III", 'I', "ingotIron", 'B', block});
   }
}
