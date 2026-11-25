package goblinbob.mobends.standard.data;

import goblinbob.mobends.standard.animation.controller.SkeletonController;
import net.minecraft.entity.monster.EntitySkeleton;

public class SkeletonData extends BipedEntityData<EntitySkeleton> {
   private final SkeletonController controller = new SkeletonController();

   public SkeletonData(EntitySkeleton entity) {
      super(entity);
   }

   public SkeletonController getController() {
      return this.controller;
   }

   public void onTicksRestart() {
   }

   public void initModelPose() {
      super.initModelPose();
      this.rightArm.position.set(-5.0F, -10.0F, 0.0F);
      this.leftArm.position.set(5.0F, -10.0F, 0.0F);
      this.rightLeg.position.set(-2.0F, 12.0F, 0.0F);
      this.leftLeg.position.set(2.0F, 12.0F, 0.0F);
      this.rightForeArm.position.set(0.0F, 4.0F, 1.0F);
      this.leftForeArm.position.set(0.0F, 4.0F, 1.0F);
      this.leftForeLeg.position.set(0.0F, 6.0F, -1.0F);
      this.rightForeLeg.position.set(0.0F, 6.0F, -1.0F);
   }
}
