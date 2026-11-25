package goblinbob.mobends.standard.animation.controller;

import goblinbob.mobends.core.animation.controller.IAnimationController;
import goblinbob.mobends.core.client.event.DataUpdateHandler;
import goblinbob.mobends.standard.data.SquidData;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.util.math.MathHelper;

public class SquidController implements IAnimationController<SquidData> {
   public SquidController() {
   }

   @Nullable
   public Collection<String> perform(SquidData data) {
      EntitySquid squid = (EntitySquid)data.getEntity();
      float squidRotation = squid.prevSquidRotation + (squid.squidRotation - squid.prevSquidRotation) * DataUpdateHandler.partialTicks + 1.1F;
      float f = squidRotation / 3.1415927F;
      f = Math.max(0.0F, f);
      float baseTentacleAngle = 0.0F;
      if (squid.prevSquidRotation < 3.1415927F) {
         baseTentacleAngle = MathHelper.sin(f * f * 3.1415927F) * 60.0F;
      }

      for(int i = 0; i < data.squidTentacles.length; ++i) {
         double d0 = (double)i * -360.0D / (double)data.squidTentacles.length + 90.0D;
         data.squidTentacles[i][0].rotation.setSmoothness(0.1F).orientX(baseTentacleAngle).rotateY((float)d0);
         float f2 = squidRotation / 6.2831855F;
         f2 = Math.max(0.0F, f2);

         for(int j = 1; j < 9; ++j) {
            float tentacleAngle = 0.0F;
            if (squid.squidRotation < 3.1415927F) {
               tentacleAngle = MathHelper.sin(f2 * 3.1415927F * 2.0F + (float)j * 0.1F) * 10.0F;
            }

            data.squidTentacles[i][j].rotation.setSmoothness(0.1F).orientX(-tentacleAngle);
         }
      }

      return null;
   }
}
