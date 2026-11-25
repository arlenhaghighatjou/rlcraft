package goblinbob.mobends.standard.animation.bit.spider;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.core.client.event.DataUpdateHandler;
import goblinbob.mobends.core.util.GUtil;
import goblinbob.mobends.standard.data.SpiderData;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.util.math.MathHelper;

public class SpiderIdleAnimationBit extends AnimationBit<SpiderData> {
   protected static final float KNEEL_DURATION = 10.0F;

   public SpiderIdleAnimationBit() {
   }

   public String[] getActions(SpiderData entityData) {
      return new String[]{"idle"};
   }

   public void onPlay(SpiderData data) {
      super.onPlay(data);
   }

   public void perform(SpiderData data) {
      float ticks = DataUpdateHandler.getTicks();
      float pt = DataUpdateHandler.partialTicks;
      EntitySpider spider = (EntitySpider)data.getEntity();
      float headYaw = ((Float)data.headYaw.get()).floatValue();
      float headPitch = ((Float)data.headPitch.get()).floatValue();
      double groundLevel = Math.sin((double)(ticks * 0.1F)) * 0.5D;
      float touchdown = Math.min(data.getTicksAfterTouchdown() / 10.0F, 1.0F);
      if (touchdown < 1.0F) {
         float preBounce = 0.0F;
         float touchdownInv = 1.0F - touchdown;
         groundLevel += Math.sin((double)(touchdown * 1.0F - 0.0F) * 3.141592653589793D * 2.0D) * 4.0D * (double)touchdownInv;
      }

      data.spiderHead.rotation.orientInstantX(headPitch);
      data.spiderHead.rotation.rotateY(headYaw).finish();
      double bodyX = Math.sin((double)(ticks * 0.2F)) * 0.4D;
      double bodyZ = Math.cos((double)(ticks * 0.2F)) * 0.4D;

      for(SpiderData.Limb limb : data.limbs) {
         SpiderData.IKResult ikResult = limb.solveIK(bodyX, bodyZ, pt);
         double deviation = GUtil.getRadianDifference(limb.getNeutralYaw(), ikResult.xzAngle + 1.5707963267948966D);
         if (deviation > 0.9D || ikResult.xzDistance * 0.0625D > 1.2D) {
            limb.adjustToNeutralPosition();
         }

         limb.applyIK(ikResult, groundLevel, 4.0D, pt);
      }

      if (spider.ticksExisted % 100 < 10) {
         data.limbs[6].adjustToLocalPosition(0.0D, 1.5D, 0.2F);
         data.limbs[7].adjustToLocalPosition(0.0D, 1.5D, 0.2F);
      }

      float climbingRotation = 0.0F;
      MathHelper.wrapDegrees(spider.rotationYaw - ((Float)data.headYaw.get()).floatValue() - climbingRotation);
      data.localOffset.slideToZero();
      data.globalOffset.set((float)bodyX, (float)(-groundLevel), (float)(-bodyZ));
      data.centerRotation.orientZero();
      data.renderRotation.orientZero();
   }
}
