package knightminer.inspirations.plugins.tan;

import com.google.common.collect.ImmutableList;
import java.util.List;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.ISimpleCauldronRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

public class FillCanteenRecipe implements ISimpleCauldronRecipe {
   private Item canteen;
   private int meta;
   private int current;
   private int type;
   private ICauldronRecipe.CauldronState fluid;
   private List<ItemStack> input;
   private ItemStack result;

   public FillCanteenRecipe(Item canteen, int current, int type, Fluid fluid) {
      this.canteen = canteen;
      this.current = current;
      this.type = type;
      this.meta = current == 0 ? 0 : (3 - current) * 4 + type;
      this.fluid = ICauldronRecipe.CauldronState.fluid(fluid);
      this.input = ImmutableList.of(new ItemStack(canteen, 1, this.meta));
      this.result = new ItemStack(canteen, 1, type);
   }

   public boolean matches(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      return level >= 1 && this.fluid.matches(state) && stack.getItem() == this.canteen && stack.getMetadata() == this.meta;
   }

   public ItemStack getResult(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      return level + this.current >= 3 ? new ItemStack(this.canteen, 1, this.type) : new ItemStack(this.canteen, 1, (3 - (level + this.current)) * 4 + this.type);
   }

   public int getInputLevel() {
      return 3 - this.current;
   }

   public int getLevel(int level) {
      int total = level + this.current;
      return total <= 3 ? 0 : total - 3;
   }

   public List<ItemStack> getInput() {
      return this.input;
   }

   public ItemStack getResult() {
      return this.result;
   }

   public Object getInputState() {
      return this.fluid.getFluid();
   }
}
