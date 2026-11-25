package reborncore.api.praescriptum.ingredients.input;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictionaryInputIngredient extends InputIngredient<String> {
   public final int amount;
   public final Integer meta;
   private List<ItemStack> equivalents;

   public static OreDictionaryInputIngredient of(String ingredient) {
      return of(ingredient, 1);
   }

   public static OreDictionaryInputIngredient of(String ingredient, int amount) {
      return of(ingredient, amount, (Integer)null);
   }

   public static OreDictionaryInputIngredient of(String ingredient, int amount, Integer meta) {
      return new OreDictionaryInputIngredient(ingredient, amount, meta);
   }

   protected OreDictionaryInputIngredient(String ingredient) {
      this(ingredient, 1);
   }

   protected OreDictionaryInputIngredient(String ingredient, int amount) {
      this(ingredient, amount, (Integer)null);
   }

   protected OreDictionaryInputIngredient(String ingredient, int amount, Integer meta) {
      super(ingredient);
      this.amount = amount;
      this.meta = meta;
   }

   public Object getUnspecific() {
      throw new UnsupportedOperationException("Operation not supported for this ingredient.");
   }

   public InputIngredient<String> copy() {
      throw new UnsupportedOperationException("Operation not supported for this ingredient.");
   }

   public boolean isEmpty() {
      return this.amount <= 0;
   }

   public int getCount() {
      return this.amount;
   }

   public void shrink(int amount) {
      throw new UnsupportedOperationException("Operation not supported for this ingredient.");
   }

   public boolean matches(Object other) {
      if (!(other instanceof ItemStack)) {
         return false;
      } else {
         List<ItemStack> inputs = this.getEquivalents();
         boolean useOreStackMeta = this.meta == null;
         Item subjectItem = ((ItemStack)other).getItem();
         int subjectMeta = ((ItemStack)other).getItemDamage();
         return inputs.stream().anyMatch((entry) -> {
            Item oreItem = entry.getItem();
            int metaRequired = useOreStackMeta ? entry.getItemDamage() : this.meta.intValue();
            return subjectItem == oreItem && (subjectMeta == metaRequired || metaRequired == 32767);
         });
      }
   }

   public boolean matchesStrict(Object other) {
      return other instanceof String && ((String)this.ingredient).equals(other);
   }

   public String toFormattedString() {
      return (String)this.ingredient;
   }

   private List<ItemStack> getEquivalents() {
      if (this.equivalents != null) {
         return this.equivalents;
      } else {
         List<ItemStack> ret = OreDictionary.getOres((String)this.ingredient);
         if (ret != OreDictionary.EMPTY_LIST) {
            this.equivalents = ret;
         }

         return ret;
      }
   }
}
