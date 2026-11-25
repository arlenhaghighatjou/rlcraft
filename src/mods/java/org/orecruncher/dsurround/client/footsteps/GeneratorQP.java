package org.orecruncher.dsurround.client.footsteps;

import javax.annotation.Nonnull;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.registry.acoustics.EventType;
import org.orecruncher.dsurround.registry.footstep.Variator;

@SideOnly(Side.CLIENT)
public class GeneratorQP extends Generator {
   private int hoof = 0;
   private float nextWalkDistanceMultiplier = 0.05F;

   public GeneratorQP(@Nonnull Variator var) {
      super(var);
   }

   protected void stepped(@Nonnull EntityLivingBase ply, @Nonnull EventType event) {
      if (this.hoof == 0 || this.hoof == 2) {
         this.nextWalkDistanceMultiplier = RANDOM.nextFloat();
      }

      if (this.hoof >= 3) {
         this.hoof = 0;
      } else {
         ++this.hoof;
      }

      if (this.hoof == 3 && event == EventType.RUN) {
         this.produceStep(ply, event);
         this.hoof = 0;
      }

      if (event == EventType.WALK) {
         this.produceStep(ply, event);
      }

   }

   protected float walkFunction2(float distance) {
      float overallMultiplier = this.VAR.QUADRUPED_MULTIPLIER;
      float ndm = 0.2F;
      float pond = this.nextWalkDistanceMultiplier;
      pond = pond * pond;
      pond = pond * 0.2F;
      return this.hoof != 1 && this.hoof != 3 ? distance * (1.0F - pond) * overallMultiplier : distance * pond * overallMultiplier;
   }

   protected float reevaluateDistance(@Nonnull EventType event, float distance) {
      if (event == EventType.WALK) {
         return this.walkFunction2(distance);
      } else if (event == EventType.RUN && this.hoof == 0) {
         return distance * 0.8F;
      } else {
         return event == EventType.RUN ? distance * 0.3F : distance;
      }
   }
}
