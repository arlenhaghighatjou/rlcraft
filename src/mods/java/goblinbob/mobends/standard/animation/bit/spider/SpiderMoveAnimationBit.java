package goblinbob.mobends.standard.animation.bit.spider;

import goblinbob.mobends.core.client.event.DataUpdateHandler;
import goblinbob.mobends.standard.data.SpiderData;
import net.minecraft.util.math.MathHelper;

public class SpiderMoveAnimationBit extends SpiderAnimationBitBase {
   protected static final String[] ACTIONS = new String[]{"move"};
   protected static final float KNEEL_DURATION = 10.0F;

   public SpiderMoveAnimationBit() {
   }

   public String[] getActions(SpiderData data) {
      return ACTIONS;
   }

   public void perform(SpiderData data) {
      float ticks = DataUpdateHandler.getTicks();
      float headYaw = ((Float)data.headYaw.get()).floatValue();
      float headPitch = ((Float)data.headPitch.get()).floatValue();
      float limbSwing = ((Float)data.limbSwing.get()).floatValue() * 0.6662F;
      float groundLevel = MathHelper.sin(ticks * 0.6F) * 1.2F;
      float touchdown = Math.min(data.getTicksAfterTouchdown() / 10.0F, 1.0F);
      if (this.startTransition < 1.0F) {
         this.startTransition += DataUpdateHandler.ticksPerFrame * 0.1F;
      }

      if (touchdown < 1.0F) {
         float touchdownInv = 1.0F - touchdown;
         groundLevel = (float)((double)groundLevel + Math.sin((double)(touchdown * 1.2F - 0.2F) * 3.141592653589793D * 2.0D) * 3.0D * (double)touchdownInv);
      }

      data.spiderHead.rotation.orientInstantX(headPitch);
      data.spiderHead.rotation.rotateY(headYaw).finish();
      float bodyX = MathHelper.sin(ticks * 0.2F) * 0.4F;
      float bodyZ = MathHelper.cos(ticks * 0.2F) * 0.4F;
      this.animateMovingLimb(data, groundLevel, limbSwing + 0.0F, 0, 20.0F, 10.0F, -80.0F, -50.0F);
      this.animateMovingLimb(data, groundLevel, limbSwing + 0.3F, 1, 20.0F, 10.0F, -80.0F, -50.0F);
      this.animateMovingLimb(data, groundLevel, limbSwing + 0.3F, 2, 15.0F, 15.0F, -30.0F, 10.0F);
      this.animateMovingLimb(data, groundLevel, limbSwing + 0.0F, 3, 15.0F, 15.0F, -30.0F, 10.0F);
      this.animateMovingLimb(data, groundLevel, limbSwing + 0.4F, 4, 7.0F, 15.0F, 20.0F, 50.0F);
      this.animateMovingLimb(data, groundLevel, limbSwing + 0.7F, 5, 7.0F, 15.0F, 20.0F, 50.0F);
      this.animateMovingLimb(data, groundLevel, limbSwing + 0.7F, 6, 10.0F, 20.0F, 60.0F, 80.0F);
      this.animateMovingLimb(data, groundLevel, limbSwing + 0.4F, 7, 10.0F, 20.0F, 60.0F, 80.0F);
      data.localOffset.slideToZero();
      data.globalOffset.set(bodyX, -groundLevel, -bodyZ);
      data.renderRotation.orientZero();
      data.centerRotation.orientZero();
   }
}
