package knightminer.inspirations.recipes.recipe;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.recipe.cauldron.CauldronFluidRecipe;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import slimeknights.mantle.util.RecipeMatch;

public class SpongeEmptyCauldron extends CauldronFluidRecipe {
   public static final SpongeEmptyCauldron INSTANCE = new SpongeEmptyCauldron();

   private SpongeEmptyCauldron() {
      super(RecipeMatch.of(new ItemStack(Blocks.SPONGE, 1, 0)), (Fluid)null, new ItemStack(Blocks.SPONGE, 1, 1), (Boolean)null, Config.spongeCauldronFull ? 3 : 1);
   }

   protected boolean matches(ICauldronRecipe.CauldronState state) {
      return state.isWater() || state.getColor() > -1 || state.getPotion() != null;
   }

   public int getLevel(int level) {
      return 0;
   }

   public ItemStack getContainer(ItemStack stack) {
      return ItemStack.EMPTY;
   }
}
