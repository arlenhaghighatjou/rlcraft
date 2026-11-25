package goblinbob.mobends.standard.animation.bit.spider;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.core.client.event.DataUpdateHandler;
import goblinbob.mobends.standard.data.SpiderData;
import net.minecraft.util.math.MathHelper;

public class SpiderDeathAnimationBit extends AnimationBit<SpiderData> {
   protected static final float PI = 3.1415927F;
   protected static final String[] ACTIONS = new String[]{"death"};
   protected float wiggleSpeedMultiplier = 1.0F;
   protected float wigglePhase = 0.0F;

   public SpiderDeathAnimationBit() {
   }

   public String[] getActions(SpiderData entityData) {
      return ACTIONS;
   }

   public void onPlay(SpiderData entityData) {
      this.wiggleSpeedMultiplier = 1.0F;
      this.wigglePhase = 0.0F;
   }

   public void perform(SpiderData data) {
      data.globalOffset.slideY(10.0F, 0.3F);
      float headYaw = ((Float)data.headYaw.get()).floatValue();
      float headPitch = ((Float)data.headPitch.get()).floatValue();
      data.spiderHead.rotation.orientInstantX(headPitch);
      data.spiderHead.rotation.rotateY(headYaw);
      data.limbs[0].upperPart.rotation.orientInstantZ(-45.0F);
      data.limbs[1].upperPart.rotation.orientInstantZ(45.0F);
      data.limbs[2].upperPart.rotation.orientInstantZ(-33.3F);
      data.limbs[3].upperPart.rotation.orientInstantZ(33.3F);
      data.limbs[4].upperPart.rotation.orientInstantZ(-33.3F);
      data.limbs[5].upperPart.rotation.orientInstantZ(33.3F);
      data.limbs[6].upperPart.rotation.orientInstantZ(-45.0F);
      data.limbs[7].upperPart.rotation.orientInstantZ(45.0F);
      data.limbs[0].upperPart.rotation.rotateY(45.0F);
      data.limbs[1].upperPart.rotation.rotateY(-45.0F);
      data.limbs[2].upperPart.rotation.rotateY(22.5F);
      data.limbs[3].upperPart.rotation.rotateY(-22.5F);
      data.limbs[4].upperPart.rotation.rotateY(-22.5F);
      data.limbs[5].upperPart.rotation.rotateY(22.5F);
      data.limbs[6].upperPart.rotation.rotateY(-45.0F);
      data.limbs[7].upperPart.rotation.rotateY(45.0F);
      float foreBend = 89.0F;
      data.limbs[0].lowerPart.rotation.orientInstantZ(-89.0F);
      data.limbs[1].lowerPart.rotation.orientInstantZ(89.0F);
      data.limbs[2].lowerPart.rotation.orientInstantZ(-89.0F);
      data.limbs[3].lowerPart.rotation.orientInstantZ(89.0F);
      data.limbs[4].lowerPart.rotation.orientInstantZ(-89.0F);
      data.limbs[5].lowerPart.rotation.orientInstantZ(89.0F);
      data.limbs[6].lowerPart.rotation.orientInstantZ(-89.0F);
      data.limbs[7].lowerPart.rotation.orientInstantZ(89.0F);
      float limbSwing = ((Float)data.limbSwing.get()).floatValue() * 0.6662F;
      float limbSwingAmount = ((Float)data.limbSwingAmount.get()).floatValue() / 3.1415927F * 180.0F;
      float f3 = -(MathHelper.cos(limbSwing * 2.0F + 0.0F) * 0.4F) * limbSwingAmount;
      float f4 = -(MathHelper.cos(limbSwing * 2.0F + 3.1415927F) * 0.4F) * limbSwingAmount;
      float f5 = -(MathHelper.cos(limbSwing * 2.0F + 1.5707964F) * 0.4F) * limbSwingAmount;
      float f6 = -(MathHelper.cos(limbSwing * 2.0F + 4.712389F) * 0.4F) * limbSwingAmount;
      float f7 = Math.abs(MathHelper.sin(limbSwing + 0.0F) * 0.4F) * limbSwingAmount;
      float f8 = Math.abs(MathHelper.sin(limbSwing + 3.1415927F) * 0.4F) * limbSwingAmount;
      float f9 = Math.abs(MathHelper.sin(limbSwing + 1.5707964F) * 0.4F) * limbSwingAmount;
      float f10 = Math.abs(MathHelper.sin(limbSwing + 4.712389F) * 0.4F) * limbSwingAmount;
      data.limbs[0].upperPart.rotation.rotateY(f3);
      data.limbs[1].upperPart.rotation.rotateY(-f3);
      data.limbs[2].upperPart.rotation.rotateY(f4);
      data.limbs[3].upperPart.rotation.rotateY(-f4);
      data.limbs[4].upperPart.rotation.rotateY(f5);
      data.limbs[5].upperPart.rotation.rotateY(-f5);
      data.limbs[6].upperPart.rotation.rotateY(f6);
      data.limbs[7].upperPart.rotation.rotateY(-f6);
      if (this.wiggleSpeedMultiplier > 0.0F) {
         this.wiggleSpeedMultiplier -= DataUpdateHandler.ticksPerFrame * 0.1F;
         this.wiggleSpeedMultiplier = Math.max(0.0F, this.wiggleSpeedMultiplier);
      }

      this.wigglePhase += (0.3F + this.wiggleSpeedMultiplier * 2.0F) * DataUpdateHandler.ticksPerFrame;
      float wiggleAmount = 10.0F + this.wiggleSpeedMultiplier * 10.0F;
      float wiggle1 = MathHelper.cos(this.wigglePhase) * wiggleAmount;
      float wiggle2 = MathHelper.cos(this.wigglePhase + 0.7853982F) * wiggleAmount;
      float wiggle3 = MathHelper.cos(this.wigglePhase + 1.5707964F) * wiggleAmount;
      float wiggle4 = MathHelper.cos(this.wigglePhase + 2.3561945F) * wiggleAmount;
      data.limbs[0].upperPart.rotation.rotateZ(f7 + wiggle1);
      data.limbs[1].upperPart.rotation.rotateZ(-f7 + wiggle2);
      data.limbs[2].upperPart.rotation.rotateZ(f8 + wiggle3);
      data.limbs[3].upperPart.rotation.rotateZ(-f8 + wiggle4);
      data.limbs[4].upperPart.rotation.rotateZ(f9 + wiggle1);
      data.limbs[5].upperPart.rotation.rotateZ(-f9 + wiggle2);
      data.limbs[6].upperPart.rotation.rotateZ(f10 + wiggle3);
      data.limbs[7].upperPart.rotation.rotateZ(-f10 + wiggle4);
   }
}
