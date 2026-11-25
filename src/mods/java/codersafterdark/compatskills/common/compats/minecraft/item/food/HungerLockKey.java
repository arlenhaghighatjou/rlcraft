package codersafterdark.compatskills.common.compats.minecraft.item.food;

import codersafterdark.reskillable.api.data.FuzzyLockKey;
import codersafterdark.reskillable.api.data.LockKey;
import java.util.Objects;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;

public class HungerLockKey implements FuzzyLockKey {
   private final int hunger;

   public HungerLockKey(int hunger) {
      this.hunger = hunger;
   }

   public HungerLockKey(ItemStack stack) {
      this(stack.getItem() instanceof ItemFood ? ((ItemFood)stack.getItem()).getHealAmount(stack) : -1);
   }

   public boolean fuzzyEquals(FuzzyLockKey o) {
      return o == this || o instanceof HungerLockKey && this.hunger >= ((HungerLockKey)o).hunger;
   }

   public boolean isNotFuzzy() {
      return false;
   }

   public LockKey getNotFuzzy() {
      return null;
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.hunger});
   }

   public boolean equals(Object o) {
      return o == this || o instanceof HungerLockKey && this.hunger == ((HungerLockKey)o).hunger;
   }
}
