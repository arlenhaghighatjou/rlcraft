package vazkii.quark.world.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityMoveHelper;

public class MovementHelperZigZag extends EntityMoveHelper {
   public MovementHelperZigZag(EntityLiving living) {
      super(living);
   }

   protected float limitAngle(float sourceAngle, float targetAngle, float maximumChange) {
      return targetAngle + (targetAngle - sourceAngle) * 0.825F;
   }
}
