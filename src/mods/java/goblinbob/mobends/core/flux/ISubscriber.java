package goblinbob.mobends.core.flux;

import java.util.List;

public interface ISubscriber {
   List<Subscription<?>> getSubscriptions();

   default void trackSubscription(Subscription<?> observer) {
      this.getSubscriptions().add(observer);
   }

   default void removeSubscriptions() {
      for(Subscription subscription : this.getSubscriptions()) {
         subscription.unsubscribe();
      }

      this.getSubscriptions().clear();
   }
}
