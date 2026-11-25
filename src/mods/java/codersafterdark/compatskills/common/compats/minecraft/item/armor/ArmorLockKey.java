package codersafterdark.compatskills.common.compats.minecraft.item.armor;

import codersafterdark.reskillable.api.data.FuzzyLockKey;
import codersafterdark.reskillable.api.data.LockKey;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class ArmorLockKey implements FuzzyLockKey {
   private final double armor;

   public ArmorLockKey(double armor) {
      this.armor = armor;
   }

   public ArmorLockKey(ItemStack stack) {
      if (stack.isEmpty()) {
         this.armor = 0.0D;
      } else {
         Item item = stack.getItem();
         if (item instanceof ItemArmor) {
            ItemArmor itemArmor = (ItemArmor)item;
            Multimap<String, AttributeModifier> attributeModifiers = itemArmor.getAttributeModifiers(itemArmor.armorType, stack);
            Collection<AttributeModifier> protection = attributeModifiers.get(SharedMonsterAttributes.ARMOR.getName());
            if (protection.isEmpty()) {
               this.armor = (double)itemArmor.damageReduceAmount;
            } else {
               this.armor = (double)itemArmor.damageReduceAmount + ((Double)protection.stream().findFirst().map(AttributeModifier::getAmount).orElse(Double.valueOf(0.0D))).doubleValue();
            }
         } else {
            this.armor = 0.0D;
         }

      }
   }

   public boolean fuzzyEquals(FuzzyLockKey o) {
      return o == this || o instanceof ArmorLockKey && this.armor >= ((ArmorLockKey)o).armor;
   }

   public boolean isNotFuzzy() {
      return false;
   }

   public LockKey getNotFuzzy() {
      return null;
   }

   public boolean equals(Object o) {
      return o == this || o instanceof ArmorLockKey && this.armor == ((ArmorLockKey)o).armor;
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.armor});
   }
}
