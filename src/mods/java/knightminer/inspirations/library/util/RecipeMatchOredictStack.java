package knightminer.inspirations.library.util;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import slimeknights.mantle.util.RecipeMatch.Oredict;

public class RecipeMatchOredictStack extends Oredict {
   private final List<ItemStack> oredictEntry;

   public RecipeMatchOredictStack(String oredictEntry, int amountNeeded) {
      this(oredictEntry, amountNeeded, 1);
   }

   public RecipeMatchOredictStack(String oredictEntry, int amountNeeded, int amountMatched) {
      super(oredictEntry, amountNeeded, amountMatched);
      this.oredictEntry = OreDictionary.getOres(oredictEntry);
   }

   public List<ItemStack> getInputs() {
      return (List)this.oredictEntry.stream().map((s) -> {
         s = s.copy();
         s.setCount(this.amountNeeded);
         return s;
      }).collect(Collectors.toList());
   }
}
