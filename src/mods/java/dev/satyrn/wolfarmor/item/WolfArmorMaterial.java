package dev.satyrn.wolfarmor.item;

import dev.satyrn.wolfarmor.api.item.IWolfArmorMaterial;
import dev.satyrn.wolfarmor.api.item.WolfArmorMaterials;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;

@Immutable
public class WolfArmorMaterial implements IWolfArmorMaterial {
   private final String name;
   private final int durability;
   private final double damageReductionAmount;
   private final int enchantability;
   private final boolean canBeDyed;
   private final int defaultColor;
   private final SoundEvent equipSound;
   private final boolean hasOverlay;
   private final Item repairItem;
   private final float toughness;

   WolfArmorMaterial(@Nonnull String name, @Nonnegative int durability, @Nonnegative double damageReductionAmount, @Nonnegative int enchantability, @Nonnull SoundEvent equipSound, @Nullable Item repairItem, @Nonnegative float toughness) {
      this(name, durability, damageReductionAmount, enchantability, false, 16777215, equipSound, false, repairItem, toughness);
   }

   WolfArmorMaterial(@Nonnull String name, @Nonnegative int durability, @Nonnegative double damageReductionAmount, @Nonnegative int enchantability, boolean canBeDyed, int defaultColor, @Nonnull SoundEvent equipSound, boolean hasOverlay, @Nullable Item repairItem, @Nonnegative float toughness) {
      this.name = name;
      this.durability = durability;
      this.damageReductionAmount = damageReductionAmount;
      this.enchantability = enchantability;
      this.canBeDyed = canBeDyed;
      this.defaultColor = defaultColor;
      this.equipSound = equipSound;
      this.hasOverlay = hasOverlay;
      this.repairItem = repairItem;
      this.toughness = toughness;
   }

   public static void initializeMaterials() {
      WolfArmorMaterials.CLOTH = new WolfArmorMaterial("leather", 80, 6.0D, 15, true, 10511680, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, true, Items.LEATHER, 0.0F);
      WolfArmorMaterials.CHAINMAIL = new WolfArmorMaterial("chainmail", 180, 12.0D, 12, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, Items.IRON_NUGGET, 0.0F);
      WolfArmorMaterials.IRON = new WolfArmorMaterial("iron", 240, 15.0D, 9, SoundEvents.ITEM_ARMOR_EQUIP_IRON, Items.IRON_INGOT, 0.0F);
      WolfArmorMaterials.GOLD = new WolfArmorMaterial("gold", 112, 12.0D, 25, SoundEvents.ITEM_ARMOR_EQUIP_GOLD, Items.GOLD_INGOT, 0.0F);
      WolfArmorMaterials.DIAMOND = new WolfArmorMaterial("diamond", 528, 20.0D, 10, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, Items.DIAMOND, 2.0F);
   }

   @Nonnull
   public String getName() {
      return this.name;
   }

   @Nonnegative
   public int getDurability() {
      return this.durability;
   }

   @Nonnegative
   public double getDamageReductionAmount() {
      return this.damageReductionAmount;
   }

   @Nonnegative
   public int getEnchantability() {
      return this.enchantability;
   }

   public boolean getCanBeDyed() {
      return this.canBeDyed;
   }

   public int getDefaultColor() {
      return this.defaultColor;
   }

   @Nonnull
   public SoundEvent getEquipSound() {
      return this.equipSound;
   }

   public boolean getHasOverlay() {
      return this.hasOverlay;
   }

   @Nullable
   public Item getRepairItem() {
      return this.repairItem;
   }

   @Nonnegative
   public float getToughness() {
      return this.toughness;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         boolean var10000;
         label53: {
            WolfArmorMaterial implementation = (WolfArmorMaterial)o;
            if (this.durability == implementation.durability && Double.compare(implementation.damageReductionAmount, this.damageReductionAmount) == 0 && this.enchantability == implementation.enchantability && this.canBeDyed == implementation.canBeDyed && this.defaultColor == implementation.defaultColor && this.hasOverlay == implementation.hasOverlay && Float.compare(implementation.toughness, this.toughness) == 0 && this.name.equals(implementation.name) && this.equipSound.equals(implementation.equipSound)) {
               if (this.repairItem != null) {
                  if (this.repairItem.equals(implementation.repairItem)) {
                     break label53;
                  }
               } else if (implementation.repairItem == null) {
                  break label53;
               }
            }

            var10000 = false;
            return var10000;
         }

         var10000 = true;
         return var10000;
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.name.hashCode();
      result = 31 * result + this.durability;
      long temp = Double.doubleToLongBits(this.damageReductionAmount);
      result = 31 * result + (int)(temp ^ temp >>> 32);
      result = 31 * result + this.enchantability;
      result = 31 * result + (this.canBeDyed ? 1 : 0);
      result = 31 * result + this.defaultColor;
      result = 31 * result + this.equipSound.hashCode();
      result = 31 * result + (this.hasOverlay ? 1 : 0);
      result = 31 * result + (this.repairItem != null ? this.repairItem.hashCode() : 0);
      result = 31 * result + (this.toughness != 0.0F ? Float.floatToIntBits(this.toughness) : 0);
      return result;
   }
}
