package codersafterdark.compatskills.common.compats.minecraft.item.weapon;

import codersafterdark.compatskills.common.compats.tinkersconstruct.TinkersCompatHandler;
import codersafterdark.reskillable.api.data.FuzzyLockKey;
import codersafterdark.reskillable.api.data.LockKey;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.tools.ranged.IProjectile;

public class AttackDamageLockKey implements FuzzyLockKey {
   private final double attackDamage;

   public AttackDamageLockKey(double attackDamage) {
      this.attackDamage = attackDamage;
   }

   public AttackDamageLockKey(ItemStack stack) {
      if (stack.isEmpty()) {
         this.attackDamage = 0.0D;
      } else {
         Item item = stack.getItem();
         Multimap<String, AttributeModifier> attributeModifiers = item.getAttributeModifiers(EntityEquipmentSlot.MAINHAND, stack);
         Collection<AttributeModifier> damage = attributeModifiers.get(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
         if (damage.isEmpty() && TinkersCompatHandler.ENABLED && item instanceof IProjectile) {
            attributeModifiers = ((IProjectile)item).getProjectileAttributeModifier(stack);
            damage = attributeModifiers.get(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
         }

         if (damage.isEmpty()) {
            this.attackDamage = 0.0D;
         } else {
            this.attackDamage = ((Double)damage.stream().findFirst().map(AttributeModifier::getAmount).orElse(Double.valueOf(0.0D))).doubleValue();
         }
      }
   }

   public boolean fuzzyEquals(FuzzyLockKey o) {
      return o == this || o instanceof AttackDamageLockKey && this.attackDamage >= ((AttackDamageLockKey)o).attackDamage;
   }

   public boolean isNotFuzzy() {
      return false;
   }

   public LockKey getNotFuzzy() {
      return null;
   }

   public boolean equals(Object o) {
      return o == this || o instanceof AttackDamageLockKey && this.attackDamage == ((AttackDamageLockKey)o).attackDamage;
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.attackDamage});
   }
}
