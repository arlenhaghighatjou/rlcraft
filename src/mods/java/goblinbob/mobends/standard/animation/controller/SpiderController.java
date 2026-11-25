package goblinbob.mobends.standard.animation.controller;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.core.animation.controller.IAnimationController;
import goblinbob.mobends.core.animation.layer.HardAnimationLayer;
import goblinbob.mobends.core.math.SmoothOrientation;
import goblinbob.mobends.standard.animation.bit.spider.SpiderCrawlAnimationBit;
import goblinbob.mobends.standard.animation.bit.spider.SpiderDeathAnimationBit;
import goblinbob.mobends.standard.animation.bit.spider.SpiderIdleAnimationBit;
import goblinbob.mobends.standard.animation.bit.spider.SpiderJumpAnimationBit;
import goblinbob.mobends.standard.animation.bit.spider.SpiderMoveAnimationBit;
import goblinbob.mobends.standard.data.SpiderData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.entity.monster.EntitySpider;

public class SpiderController implements IAnimationController<SpiderData> {
   protected HardAnimationLayer<SpiderData> layerBase = new HardAnimationLayer<SpiderData>();
   protected AnimationBit<SpiderData> bitIdle = new SpiderIdleAnimationBit();
   protected AnimationBit<SpiderData> bitMove = new SpiderMoveAnimationBit();
   protected AnimationBit<SpiderData> bitJump = new SpiderJumpAnimationBit();
   protected AnimationBit<SpiderData> bitDeath = new SpiderDeathAnimationBit();
   protected AnimationBit<SpiderData> bitClimb = new SpiderCrawlAnimationBit();
   protected boolean resetAfterJumped = false;

   public SpiderController() {
   }

   public Collection<String> perform(SpiderData spiderData) {
      EntitySpider spider = (EntitySpider)spiderData.getEntity();
      if (spider.getHealth() <= 0.0F) {
         this.layerBase.playOrContinueBit(this.bitDeath, spiderData);
      } else if (spider.isBesideClimbableBlock()) {
         this.layerBase.playOrContinueBit(this.bitClimb, spiderData);
      } else if (spiderData.isOnGround() && spiderData.getTicksAfterTouchdown() >= 1.0F) {
         if (!this.resetAfterJumped) {
            for(SpiderData.Limb limb : spiderData.limbs) {
               limb.resetPosition();
            }

            this.resetAfterJumped = true;
         }

         if (spiderData.isStillHorizontally()) {
            this.layerBase.playOrContinueBit(this.bitIdle, spiderData);
         } else {
            this.layerBase.playOrContinueBit(this.bitMove, spiderData);
         }
      } else {
         this.layerBase.playOrContinueBit(this.bitJump, spiderData);
         if (this.resetAfterJumped) {
            this.resetAfterJumped = false;
         }
      }

      List<String> actions = new ArrayList();
      this.layerBase.perform(spiderData, actions);
      return actions;
   }

   public static void putLimbOnGround(SmoothOrientation upperLimb, SmoothOrientation lowerLimb, boolean odd, double stretchDistance, double groundLevel) {
      putLimbOnGround(upperLimb, lowerLimb, odd, stretchDistance, groundLevel, 1.0F);
      upperLimb.finish();
      lowerLimb.finish();
   }

   public static void putLimbOnGround(SmoothOrientation upperLimb, SmoothOrientation lowerLimb, boolean odd, double stretchDistance, double groundLevel, float smoothness) {
      float limbSegmentLength = 12.0F;
      float maxStretch = 24.0F;
      double c = groundLevel == 0.0D ? stretchDistance : Math.sqrt(stretchDistance * stretchDistance + groundLevel * groundLevel);
      if (c > 24.0D) {
         c = 24.0D;
      }

      double alpha = c > 24.0D ? 0.0D : Math.acos(c / 2.0D / 12.0D);
      double beta = Math.atan2(stretchDistance, -groundLevel);
      double lowerAngle = Math.max(-2.3D, -2.0D * alpha);
      double upperAngle = Math.min(1.0D, alpha + beta - 1.5707963267948966D);
      upperLimb.setSmoothness(smoothness).localRotateZ((float)(upperAngle / 3.141592653589793D * 180.0D) * (float)(odd ? -1 : 1));
      lowerLimb.setSmoothness(smoothness).orientZ((float)(lowerAngle / 3.141592653589793D * 180.0D) * (float)(odd ? -1 : 1));
   }
}
