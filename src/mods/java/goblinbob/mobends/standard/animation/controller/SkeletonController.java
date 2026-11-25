package goblinbob.mobends.standard.animation.controller;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.core.animation.controller.IAnimationController;
import goblinbob.mobends.core.animation.layer.HardAnimationLayer;
import goblinbob.mobends.standard.animation.bit.biped.JumpAnimationBit;
import goblinbob.mobends.standard.animation.bit.biped.item.BipedActionController;
import goblinbob.mobends.standard.animation.bit.skeleton.StandAnimationBit;
import goblinbob.mobends.standard.animation.bit.skeleton.WalkAnimationBit;
import goblinbob.mobends.standard.data.BipedEntityData;
import goblinbob.mobends.standard.data.SkeletonData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;

public class SkeletonController implements IAnimationController<SkeletonData> {
   protected HardAnimationLayer<BipedEntityData<EntitySkeleton>> layerBase = new HardAnimationLayer<BipedEntityData<EntitySkeleton>>();
   protected AnimationBit<? extends BipedEntityData<EntitySkeleton>> bitStand = new StandAnimationBit();
   protected AnimationBit<? extends BipedEntityData<EntitySkeleton>> bitWalk = new WalkAnimationBit();
   protected AnimationBit<? extends BipedEntityData<EntitySkeleton>> bitJump = new JumpAnimationBit<BipedEntityData<EntitySkeleton>>();
   protected final BipedActionController actionController = new BipedActionController();

   public SkeletonController() {
   }

   public void performActionAnimations(SkeletonData data, EntitySkeleton skeleton) {
      EnumHandSide primaryHand = skeleton.getPrimaryHand();
      ItemStack heldItemMainhand = skeleton.getHeldItemMainhand();
      ItemStack heldItemOffhand = skeleton.getHeldItemOffhand();
      Item activeItem = skeleton.getActiveItemStack().getItem();
      this.actionController.perform(data, primaryHand, heldItemMainhand, heldItemOffhand, activeItem);
   }

   public Collection<String> perform(SkeletonData skeletonData) {
      EntitySkeleton skeleton = (EntitySkeleton)skeletonData.getEntity();
      if (skeletonData.isOnGround() && skeletonData.getTicksAfterTouchdown() >= 1.0F) {
         if (skeletonData.isStillHorizontally()) {
            this.layerBase.playOrContinueBit(this.bitStand, skeletonData);
         } else {
            this.layerBase.playOrContinueBit(this.bitWalk, skeletonData);
         }
      } else {
         this.layerBase.playOrContinueBit(this.bitJump, skeletonData);
      }

      List<String> actions = new ArrayList();
      this.layerBase.perform(skeletonData, actions);
      this.performActionAnimations(skeletonData, skeleton);
      return actions;
   }
}
