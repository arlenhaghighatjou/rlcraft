package codersafterdark.reskillable.advancement.trait;

import codersafterdark.reskillable.advancement.CriterionListeners;
import codersafterdark.reskillable.api.unlockable.Unlockable;
import java.util.function.Predicate;
import net.minecraft.advancements.PlayerAdvancements;

public class UnlockUnlockableListeners extends CriterionListeners<UnlockUnlockableCriterionInstance> {
   public UnlockUnlockableListeners(PlayerAdvancements playerAdvancements) {
      super(playerAdvancements);
   }

   public void trigger(Unlockable unlockable) {
      this.trigger((instance) -> {
         return instance.test(unlockable);
      });
   }
}
