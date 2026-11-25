package goblinbob.mobends.core.pack;

import goblinbob.mobends.core.client.event.DataUpdateHandler;
import goblinbob.mobends.core.data.EntityData;
import goblinbob.mobends.core.kumo.state.template.MalformedKumoTemplateException;
import java.util.Collection;
import javax.annotation.Nullable;

public class BendsPackPerformer {
   public static final BendsPackPerformer INSTANCE = new BendsPackPerformer();

   public BendsPackPerformer() {
   }

   public void performCurrentPack(EntityData<?> entityData, String animatedEntityKey, @Nullable Collection<String> actions) {
      BendsPackData packData = PackDataProvider.INSTANCE.getAppliedData();
      if (packData != null) {
         try {
            entityData.packAnimationState.update(entityData, packData, animatedEntityKey, DataUpdateHandler.ticksPerFrame);
         } catch (MalformedKumoTemplateException var6) {
            var6.printStackTrace();
            PackManager.INSTANCE.resetAppliedPacks(true);
         }

      }
   }
}
