package goblinbob.mobends.standard.animation.bit.spider;

import goblinbob.mobends.core.client.event.DataUpdateHandler;
import goblinbob.mobends.standard.data.SpiderData;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.util.math.MathHelper;

public class SpiderCrawlAnimationBit extends SpiderAnimationBitBase {
   protected static final String[] ACTIONS = new String[]{"crawl"};

   public SpiderCrawlAnimationBit() {
   }

   public String[] getActions(SpiderData entityData) {
      return ACTIONS;
   }

   public void perform(SpiderData data) {
      float pt = DataUpdateHandler.partialTicks;
      EntitySpider spider = (EntitySpider)data.getEntity();
      float headYaw = ((Float)data.headYaw.get()).floatValue();
      float headPitch = ((Float)data.headPitch.get()).floatValue();
      float limbSwing = data.getInterpolatedCrawlProgress() * 5.0F;
      float groundLevel = MathHelper.sin(limbSwing * 0.6F) * 1.2F;
      if (this.startTransition < 1.0F) {
         this.startTransition += DataUpdateHandler.ticksPerFrame * 0.1F;
      }

      data.spiderHead.rotation.orientInstantX(headPitch);
      data.spiderHead.rotation.rotateY(headYaw).finish();
      this.animateMovingLimb(data, groundLevel, limbSwing + 0.0F, 0, 20.0F, 10.0F, -80.0F, -50.0F);
      this.animateMovingLimb(data, groundLevel, limbSwing + 0.3F, 1, 20.0F, 10.0F, -80.0F, -50.0F);
      this.animateMovingLimb(data, groundLevel, limbSwing + 0.3F, 2, 15.0F, 15.0F, -30.0F, 10.0F);
      this.animateMovingLimb(data, groundLevel, limbSwing + 0.0F, 3, 15.0F, 15.0F, -30.0F, 10.0F);
      this.animateMovingLimb(data, groundLevel, limbSwing + 0.4F, 4, 7.0F, 15.0F, 20.0F, 50.0F);
      this.animateMovingLimb(data, groundLevel, limbSwing + 0.7F, 5, 7.0F, 15.0F, 20.0F, 50.0F);
      this.animateMovingLimb(data, groundLevel, limbSwing + 0.7F, 6, 10.0F, 20.0F, 60.0F, 80.0F);
      this.animateMovingLimb(data, groundLevel, limbSwing + 0.4F, 7, 10.0F, 20.0F, 60.0F, 80.0F);
      float climbingRotation = data.getCrawlingRotation();
      float yaw = spider.prevRotationYaw + (spider.rotationYaw - spider.prevRotationYaw) * pt;
      float renderRotationY = MathHelper.wrapDegrees(yaw - climbingRotation);
      data.renderRotation.orientX(-90.0F);
      data.renderRotation.setSmoothness(0.6F).rotateY(renderRotationY);
      data.localOffset.slideTo(0.0F, -10.0F, 0.0F, 0.5F);
      data.centerRotation.orientZero();
   }
}
