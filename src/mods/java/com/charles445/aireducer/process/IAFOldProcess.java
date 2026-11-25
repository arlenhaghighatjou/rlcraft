package com.charles445.aireducer.process;

import com.charles445.aireducer.config.ModConfig;
import com.charles445.aireducer.reflect.ReflectorIAFOld;
import com.charles445.aireducer.reflect.ReflectorMinecraft;
import com.charles445.aireducer.routine.IAFOldDeathWormRoutine;
import com.charles445.aireducer.routine.IAFOldMyrmexRoutine;
import com.charles445.aireducer.routine.Routine;
import net.minecraft.entity.EntityLiving;

public class IAFOldProcess extends ModProcess {
   Routine deathWormRoutine = new IAFOldDeathWormRoutine();
   Routine myrmexRoutine = new IAFOldMyrmexRoutine();

   public IAFOldProcess() {
   }

   public boolean canUse() {
      return ModConfig.iceandfireold.ENABLED && ReflectorIAFOld.reflector != null && ReflectorMinecraft.reflector != null;
   }

   public void handle(EntityLiving entity, String domain, String path) {
      byte var5 = -1;
      switch(path.hashCode()) {
      case -1368556003:
         if (path.equals("myrmex_soldier")) {
            var5 = 2;
         }
         break;
      case -1112408185:
         if (path.equals("deathworm")) {
            var5 = 0;
         }
         break;
      case 14777989:
         if (path.equals("myrmex_queen")) {
            var5 = 5;
         }
         break;
      case 15541858:
         if (path.equals("myrmex_royal")) {
            var5 = 4;
         }
         break;
      case 311377641:
         if (path.equals("myrmex_sentinel")) {
            var5 = 3;
         }
         break;
      case 624744323:
         if (path.equals("myrmex_worker")) {
            var5 = 1;
         }
      }

      switch(var5) {
      case 0:
         this.deathWormRoutine.runRoutine(entity, domain, path);
         break;
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
         this.myrmexRoutine.runRoutine(entity, domain, path);
      }

   }
}
