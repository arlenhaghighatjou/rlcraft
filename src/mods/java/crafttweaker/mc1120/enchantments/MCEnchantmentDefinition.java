package crafttweaker.mc1120.enchantments;

import crafttweaker.api.enchantments.IEnchantment;
import crafttweaker.api.enchantments.IEnchantmentDefinition;
import crafttweaker.api.item.IItemStack;
import java.util.Objects;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class MCEnchantmentDefinition implements IEnchantmentDefinition {
   public final Enchantment enchantment;

   public MCEnchantmentDefinition(Enchantment enchantment) {
      this.enchantment = enchantment;
   }

   public MCEnchantmentDefinition(int id) {
      this(Enchantment.getEnchantmentByID(id));
   }

   public int getID() {
      return Enchantment.getEnchantmentID(this.enchantment);
   }

   public String getRegistryName() {
      ResourceLocation registryName = this.enchantment.getRegistryName();
      return registryName != null ? registryName.toString() : null;
   }

   public String getName() {
      return this.enchantment.getName();
   }

   public void setName(String name) {
      this.enchantment.setName(name);
   }

   public boolean canApply(IItemStack itemStack) {
      return this.enchantment.canApply((ItemStack)itemStack.getInternal());
   }

   public boolean canApplyAtEnchantmentTable(IItemStack itemStack) {
      return this.enchantment.canApplyAtEnchantingTable((ItemStack)itemStack.getInternal());
   }

   public int getMaxLevel() {
      return this.enchantment.getMaxLevel();
   }

   public int getMinLevel() {
      return this.enchantment.getMinLevel();
   }

   public int getMaxEnchantability(int enchantmentLevel) {
      return this.enchantment.getMaxEnchantability(enchantmentLevel);
   }

   public int getMinEnchantability(int enchantmentLevel) {
      return this.enchantment.getMinEnchantability(enchantmentLevel);
   }

   public String getTranslatedName(int enchantmentLevel) {
      return this.enchantment.getTranslatedName(enchantmentLevel);
   }

   public boolean isAllowedOnBooks() {
      return this.enchantment.isAllowedOnBooks();
   }

   public boolean isCompatibleWith(IEnchantmentDefinition other) {
      return this.enchantment.isCompatibleWith((Enchantment)other.getInternal());
   }

   public boolean isCurse() {
      return this.enchantment.isCurse();
   }

   public boolean isTreasureEnchantment() {
      return this.enchantment.isTreasureEnchantment();
   }

   public IEnchantment makeEnchantment(int level) {
      return new MCEnchantment(this.enchantment, level);
   }

   public int compare(IEnchantmentDefinition other) {
      return Integer.compare(this.getID(), other.getID());
   }

   public Object getInternal() {
      return this.enchantment;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MCEnchantmentDefinition that = (MCEnchantmentDefinition)o;
         return Objects.equals(this.enchantment, that.enchantment);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.enchantment});
   }
}
