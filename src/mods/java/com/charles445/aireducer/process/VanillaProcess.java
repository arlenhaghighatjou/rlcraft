package com.charles445.aireducer.process;

import com.charles445.aireducer.config.ModConfig;
import com.charles445.aireducer.reflect.ReflectorMinecraft;
import com.charles445.aireducer.routine.AvoidRoutine;
import com.charles445.aireducer.routine.RabbitRoutine;
import com.charles445.aireducer.routine.Routine;
import net.minecraft.entity.EntityLiving;

public class VanillaProcess extends ModProcess {
   private Routine rabbitRoutine = new RabbitRoutine();
   private Routine avoidRoutine = new AvoidRoutine();

   public VanillaProcess() {
   }

   public boolean canUse() {
      return ModConfig.vanilla.ENABLED && ReflectorMinecraft.reflector != null;
   }

   public void handle(EntityLiving entity, String domain, String path) {
      this.avoidRoutine.runRoutine(entity, domain, path);
      byte var5 = -1;
      switch(path.hashCode()) {
      case -938645478:
         if (path.equals("rabbit")) {
            var5 = 0;
         }
      default:
         switch(var5) {
         case 0:
            this.rabbitRoutine.runRoutine(entity, domain, path);
         default:
         }
      }
   }
}
