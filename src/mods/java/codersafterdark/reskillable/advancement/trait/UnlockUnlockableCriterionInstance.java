package codersafterdark.reskillable.advancement.trait;

import codersafterdark.reskillable.api.unlockable.Unlockable;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.util.ResourceLocation;

public class UnlockUnlockableCriterionInstance extends AbstractCriterionInstance {
   private final Unlockable unlockable;

   public UnlockUnlockableCriterionInstance(Unlockable unlockable) {
      super(new ResourceLocation("reskillable", "unlockable"));
      this.unlockable = unlockable;
   }

   public boolean test(Unlockable other) {
      return this.unlockable == other;
   }
}
