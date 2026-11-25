package codersafterdark.compatskills.common.compats.minecraft.item.food;

import codersafterdark.reskillable.api.data.FuzzyLockKey;
import codersafterdark.reskillable.api.data.LockKey;
import java.util.Objects;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;

public class SaturationLockKey implements FuzzyLockKey {
   private final float saturation;

   public SaturationLockKey(float saturation) {
      this.saturation = saturation;
   }

   public SaturationLockKey(ItemStack stack) {
      if (stack.getItem() instanceof ItemFood) {
         ItemFood food = (ItemFood)stack.getItem();
         this.saturation = (float)food.getHealAmount(stack) * food.getSaturationModifier(stack) * 2.0F;
      } else {
         this.saturation = -1.0F;
      }

   }

   public boolean fuzzyEquals(FuzzyLockKey o) {
      return o == this || o instanceof SaturationLockKey && this.saturation >= ((SaturationLockKey)o).saturation;
   }

   public boolean isNotFuzzy() {
      return false;
   }

   public LockKey getNotFuzzy() {
      return null;
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.saturation});
   }

   public boolean equals(Object o) {
      return o == this || o instanceof SaturationLockKey && this.saturation == ((SaturationLockKey)o).saturation;
   }
}
