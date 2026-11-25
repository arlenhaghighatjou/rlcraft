package goblinbob.mobends.core.kumo.variable;

import goblinbob.mobends.core.client.event.DataUpdateHandler;
import goblinbob.mobends.core.data.EntityData;
import goblinbob.mobends.core.data.LivingEntityData;
import java.util.HashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class KumoVariableRegistry {
   public static final KumoVariableRegistry instance = new KumoVariableRegistry();
   private EntityData<?> tempData;
   private HashMap<String, KumoVariableEntry> variables = new HashMap();

   public KumoVariableRegistry() {
   }

   public void provideTemporaryData(EntityData<?> tempData) {
      this.tempData = tempData;
   }

   private static void registerVariable(String key, IKumoVariable variable) {
      instance.variables.put(key, new KumoVariableEntry(variable, key));
   }

   static {
      registerVariable("ticks", DataUpdateHandler::getTicks);
      registerVariable("random", Math::random);
      registerVariable("ticksAfterPunch", () -> {
         return instance.tempData instanceof LivingEntityData ? (double)((LivingEntityData)instance.tempData).getTicksAfterAttack() : 0.0D;
      });
      registerVariable("health", () -> {
         Entity entity = instance.tempData.getEntity();
         return entity instanceof EntityLivingBase ? (double)((EntityLivingBase)entity).getHealth() : 0.0D;
      });
   }
}
