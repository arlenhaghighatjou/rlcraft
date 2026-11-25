package codersafterdark.reskillable.advancement;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.ICriterionTrigger.Listener;

public class CriterionListeners<T extends ICriterionInstance> {
   private final PlayerAdvancements playerAdvancements;
   private final Set<Listener<T>> listeners = Sets.newHashSet();

   public CriterionListeners(PlayerAdvancements playerAdvancements) {
      this.playerAdvancements = playerAdvancements;
   }

   public boolean isEmpty() {
      return this.listeners.isEmpty();
   }

   public void add(Listener<T> listener) {
      this.listeners.add(listener);
   }

   public void remove(Listener<T> listener) {
      this.listeners.remove(listener);
   }

   public void trigger(Predicate<T> test) {
      List<Listener<T>> toGrant = new ArrayList();

      for(Listener<T> listener : this.listeners) {
         if (test.test(listener.getCriterionInstance())) {
            toGrant.add(listener);
         }
      }

      toGrant.forEach((listenerx) -> {
         listener.grantCriterion(this.playerAdvancements);
      });
   }
}
