package goblinbob.mobends.standard.data;

import goblinbob.mobends.core.animation.controller.IAnimationController;
import goblinbob.mobends.core.client.model.ModelPartTransform;
import goblinbob.mobends.core.data.LivingEntityData;
import goblinbob.mobends.standard.animation.controller.WolfController;
import net.minecraft.entity.passive.EntityWolf;

public class WolfData extends LivingEntityData<EntityWolf> {
   public ModelPartTransform head;
   public ModelPartTransform nose;
   public ModelPartTransform mouth;
   public ModelPartTransform tongue;
   public ModelPartTransform leftEar;
   public ModelPartTransform rightEar;
   public ModelPartTransform body;
   public ModelPartTransform leg1;
   public ModelPartTransform leg2;
   public ModelPartTransform leg3;
   public ModelPartTransform leg4;
   public ModelPartTransform tail;
   public ModelPartTransform mane;
   public ModelPartTransform foreLeg1;
   public ModelPartTransform foreLeg2;
   public ModelPartTransform foreLeg3;
   public ModelPartTransform foreLeg4;
   private final WolfController controller = new WolfController();

   public WolfData(EntityWolf entity) {
      super(entity);
   }

   public IAnimationController<?> getController() {
      return this.controller;
   }

   public void onTicksRestart() {
   }

   public void initModelPose() {
      super.initModelPose();
      this.nameToPartMap.put("head", this.head = new ModelPartTransform());
      this.nameToPartMap.put("body", this.body = new ModelPartTransform());
      this.nameToPartMap.put("leg1", this.leg1 = new ModelPartTransform());
      this.nameToPartMap.put("leg2", this.leg2 = new ModelPartTransform());
      this.nameToPartMap.put("leg3", this.leg3 = new ModelPartTransform());
      this.nameToPartMap.put("leg4", this.leg4 = new ModelPartTransform());
      this.nameToPartMap.put("tail", this.tail = new ModelPartTransform());
      this.nameToPartMap.put("mane", this.mane = new ModelPartTransform());
      this.nameToPartMap.put("nose", this.nose = new ModelPartTransform());
      this.nameToPartMap.put("mouth", this.mouth = new ModelPartTransform());
      this.nameToPartMap.put("tongue", this.tongue = new ModelPartTransform());
      this.nameToPartMap.put("leftEar", this.leftEar = new ModelPartTransform());
      this.nameToPartMap.put("rightEar", this.rightEar = new ModelPartTransform());
      this.nameToPartMap.put("foreLeg1", this.foreLeg1 = new ModelPartTransform());
      this.nameToPartMap.put("foreLeg2", this.foreLeg2 = new ModelPartTransform());
      this.nameToPartMap.put("foreLeg3", this.foreLeg3 = new ModelPartTransform());
      this.nameToPartMap.put("foreLeg4", this.foreLeg4 = new ModelPartTransform());
      this.head.position.set(0.0F, -0.5F, -13.0F);
      this.body.position.set(0.0F, 14.0F, 8.0F);
      this.mane.position.set(0.0F, -0.5F, -12.0F);
      this.nose.position.set(0.0F, 1.0F, -3.0F);
      this.mouth.position.set(0.0F, 2.0F, -3.0F);
      this.tongue.position.set(0.0F, 2.0F, -3.0F);
      this.leftEar.position.set(-2.0F, -3.0F, -1.0F);
      this.rightEar.position.set(2.0F, -3.0F, -1.0F);
      this.leg1.position.set(-2.0F, 3.0F, -1.0F);
      this.leg2.position.set(2.0F, 3.0F, -1.0F);
      this.leg3.position.set(-2.0F, 3.0F, -12.0F);
      this.leg4.position.set(2.0F, 3.0F, -12.0F);
      this.tail.position.set(0.0F, -3.0F, 0.0F);
      this.foreLeg1.position.set(0.0F, 4.0F, -1.0F);
      this.foreLeg2.position.set(0.0F, 4.0F, -1.0F);
      this.foreLeg3.position.set(0.0F, 4.0F, 1.0F);
      this.foreLeg4.position.set(0.0F, 4.0F, 1.0F);
   }

   public void updateParts(float ticksPerFrame) {
      super.updateParts(ticksPerFrame);
      this.head.update(ticksPerFrame);
      this.body.update(ticksPerFrame);
      this.leg1.update(ticksPerFrame);
      this.leg2.update(ticksPerFrame);
      this.leg3.update(ticksPerFrame);
      this.leg4.update(ticksPerFrame);
      this.tail.update(ticksPerFrame);
      this.mane.update(ticksPerFrame);
      this.nose.update(ticksPerFrame);
      this.mouth.update(ticksPerFrame);
      this.tongue.update(ticksPerFrame);
      this.leftEar.update(ticksPerFrame);
      this.rightEar.update(ticksPerFrame);
      this.foreLeg1.update(ticksPerFrame);
      this.foreLeg2.update(ticksPerFrame);
      this.foreLeg3.update(ticksPerFrame);
      this.foreLeg4.update(ticksPerFrame);
   }

   public boolean isSitting() {
      return ((EntityWolf)this.entity).isSitting();
   }
}
