package reborncore.common.recipes;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.Validate;

public class OreRecipeInput implements IRecipeInput {
   String oreDictName;
   int size = 1;

   public OreRecipeInput(String oreDictName, int size) {
      this.oreDictName = oreDictName;
      this.size = size;
      Validate.notEmpty(this.getAllStacks());
   }

   public OreRecipeInput(String oreDictName) {
      this.oreDictName = oreDictName;
      Validate.notEmpty(this.getAllStacks());
   }

   public ItemStack getItemStack() {
      List<ItemStack> list = this.getAllStacks();
      return list.isEmpty() ? ItemStack.EMPTY : (ItemStack)list.get(0);
   }

   public List<ItemStack> getAllStacks() {
      if (!OreDictionary.doesOreNameExist(this.oreDictName)) {
         return NonNullList.create();
      } else {
         NonNullList<ItemStack> list = OreDictionary.getOres(this.oreDictName);
         return (List)list.stream().filter(Objects::nonNull).map((stack) -> {
            ItemStack clone = stack.copy();
            clone.setCount(this.size);
            return clone;
         }).filter((stack) -> {
            return !stack.isEmpty();
         }).collect(Collectors.toList());
      }
   }
}
