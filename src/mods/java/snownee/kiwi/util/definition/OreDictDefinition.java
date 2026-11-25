package snownee.kiwi.util.definition;

import java.util.Collections;
import java.util.List;
import net.minecraft.item.ItemStack;
import snownee.kiwi.crafting.input.ProcessingInput;
import snownee.kiwi.util.OreUtil;

public class OreDictDefinition implements ProcessingInput {
   public static final OreDictDefinition EMPTY = of("", 0);
   String ore;
   int size;

   private OreDictDefinition(String ore) {
      this(ore, 1);
   }

   private OreDictDefinition(String ore, int size) {
      this.ore = ore;
      this.size = size;
   }

   public static OreDictDefinition of(String ore) {
      return new OreDictDefinition(ore);
   }

   public static OreDictDefinition of(String ore, int size) {
      return new OreDictDefinition(ore, size);
   }

   public List<ItemStack> examples() {
      return (List<ItemStack>)(this.isEmpty() ? Collections.emptyList() : OreUtil.getItemsFromOre(this.ore, this.size));
   }

   public ItemStack getItemStack() {
      return OreUtil.getPreferredItemFromOre(this.ore);
   }

   public boolean matches(ItemStack stack) {
      return stack.getCount() >= this.size && OreUtil.doesItemHaveOreName(stack, this.ore);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         OreDictDefinition that = (OreDictDefinition)o;
         return this.size == that.size && this.ore.equals(that.ore);
      } else {
         return false;
      }
   }

   public int count() {
      return this.size;
   }

   public String toString() {
      return "ore:" + (this.isEmpty() ? "null" : this.ore) + " * " + this.size;
   }

   public int hashCode() {
      return this.ore.hashCode() * 31 + this.size;
   }

   public boolean isEmpty() {
      return this.ore.isEmpty() || this.size == 0;
   }
}
