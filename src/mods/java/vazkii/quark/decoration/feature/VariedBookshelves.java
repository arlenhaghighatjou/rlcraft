package vazkii.quark.decoration.feature;

import java.util.function.Predicate;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.block.BlockMod;
import vazkii.arl.recipe.BlacklistOreIngredient;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.handler.RecipeProcessor;
import vazkii.quark.base.module.Feature;
import vazkii.quark.decoration.block.BlockCustomBookshelf;

public class VariedBookshelves extends Feature {
   public static BlockMod custom_bookshelf;
   public static boolean renameVanillaBookshelves;

   public VariedBookshelves() {
   }

   public void setupConfig() {
      renameVanillaBookshelves = this.loadPropBool("Rename vanilla bookshelves to Oak Bookshelf", "", true);
   }

   public void preInit(FMLPreInitializationEvent event) {
      if (renameVanillaBookshelves) {
         Blocks.BOOKSHELF.setUnlocalizedName("oak_bookshelf");
      }

      custom_bookshelf = new BlockCustomBookshelf();
      RecipeProcessor.addWoodReplacements(Blocks.BOOKSHELF);

      for(int i = 0; i < 5; ++i) {
         RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(custom_bookshelf, 1, i), new Object[]{"WWW", "BBB", "WWW", 'W', ProxyRegistry.newStack(Blocks.PLANKS, 1, i + 1), 'B', ProxyRegistry.newStack(Items.BOOK)});
      }

      Ingredient wood = new BlacklistOreIngredient("plankWood", (stack) -> {
         return stack.getItem() == Item.getItemFromBlock(Blocks.PLANKS);
      });
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(Blocks.BOOKSHELF), new Object[]{"WWW", "BBB", "WWW", 'W', wood, 'B', ProxyRegistry.newStack(Items.BOOK)});
      this.addOreDict();
   }

   private void addOreDict() {
      addOreDict("bookshelf", Blocks.BOOKSHELF);
      addOreDict("bookshelf", ProxyRegistry.newStack(custom_bookshelf, 1, 32767));
      addOreDict("bookshelfOak", Blocks.BOOKSHELF);
      addOreDict("bookshelfSpruce", ProxyRegistry.newStack(custom_bookshelf, 1, 0));
      addOreDict("bookshelfBirch", ProxyRegistry.newStack(custom_bookshelf, 1, 1));
      addOreDict("bookshelfJungle", ProxyRegistry.newStack(custom_bookshelf, 1, 2));
      addOreDict("bookshelfAcacia", ProxyRegistry.newStack(custom_bookshelf, 1, 3));
      addOreDict("bookshelfDarkOak", ProxyRegistry.newStack(custom_bookshelf, 1, 4));
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
