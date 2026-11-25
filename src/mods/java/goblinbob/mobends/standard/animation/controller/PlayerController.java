package goblinbob.mobends.standard.animation.controller;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.core.animation.controller.IAnimationController;
import goblinbob.mobends.core.animation.keyframe.ArmatureMask;
import goblinbob.mobends.core.animation.layer.HardAnimationLayer;
import goblinbob.mobends.standard.animation.bit.biped.FallingAnimationBit;
import goblinbob.mobends.standard.animation.bit.biped.JumpAnimationBit;
import goblinbob.mobends.standard.animation.bit.biped.LadderClimbAnimationBit;
import goblinbob.mobends.standard.animation.bit.biped.RidingAnimationBit;
import goblinbob.mobends.standard.animation.bit.biped.SittingAnimationBit;
import goblinbob.mobends.standard.animation.bit.biped.SneakAnimationBit;
import goblinbob.mobends.standard.animation.bit.biped.StandAnimationBit;
import goblinbob.mobends.standard.animation.bit.biped.SwimmingAnimationBit;
import goblinbob.mobends.standard.animation.bit.biped.TorchHoldingAnimationBit;
import goblinbob.mobends.standard.animation.bit.biped.item.BipedActionController;
import goblinbob.mobends.standard.animation.bit.player.CapeAnimationBit;
import goblinbob.mobends.standard.animation.bit.player.ElytraAnimationBit;
import goblinbob.mobends.standard.animation.bit.player.FlyingAnimationBit;
import goblinbob.mobends.standard.animation.bit.player.SleepingAnimationBit;
import goblinbob.mobends.standard.animation.bit.player.SprintAnimationBit;
import goblinbob.mobends.standard.animation.bit.player.SprintJumpAnimationBit;
import goblinbob.mobends.standard.animation.bit.player.WalkAnimationBit;
import goblinbob.mobends.standard.data.BipedEntityData;
import goblinbob.mobends.standard.data.PlayerData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;

public class PlayerController implements IAnimationController<PlayerData> {
   protected HardAnimationLayer<BipedEntityData<?>> layerBase = new HardAnimationLayer<BipedEntityData<?>>();
   protected HardAnimationLayer<BipedEntityData<?>> layerTorch = new HardAnimationLayer<BipedEntityData<?>>();
   protected HardAnimationLayer<BipedEntityData<?>> layerSneak = new HardAnimationLayer<BipedEntityData<?>>();
   protected HardAnimationLayer<BipedEntityData<?>> layerCape = new HardAnimationLayer<BipedEntityData<?>>();
   protected AnimationBit<BipedEntityData<?>> bitStand = new StandAnimationBit<BipedEntityData<?>>();
   protected AnimationBit<BipedEntityData<?>> bitJump = new JumpAnimationBit<BipedEntityData<?>>();
   protected AnimationBit<BipedEntityData<?>> bitSneak = new SneakAnimationBit();
   protected AnimationBit<BipedEntityData<?>> bitLadderClimb = new LadderClimbAnimationBit();
   protected AnimationBit<BipedEntityData<?>> bitSwimming = new SwimmingAnimationBit();
   protected AnimationBit<BipedEntityData<?>> bitRiding = new RidingAnimationBit();
   protected AnimationBit<BipedEntityData<?>> bitSitting = new SittingAnimationBit();
   protected AnimationBit<BipedEntityData<?>> bitFalling = new FallingAnimationBit();
   protected AnimationBit<PlayerData> bitWalk = new WalkAnimationBit();
   protected AnimationBit<PlayerData> bitSprint = new SprintAnimationBit();
   protected AnimationBit<PlayerData> bitSprintJump = new SprintJumpAnimationBit();
   protected AnimationBit<BipedEntityData<?>> bitTorchHolding = new TorchHoldingAnimationBit();
   protected FlyingAnimationBit bitFlying = new FlyingAnimationBit();
   protected ElytraAnimationBit bitElytra = new ElytraAnimationBit();
   protected CapeAnimationBit bitCape = new CapeAnimationBit();
   protected SleepingAnimationBit bitSleeping = new SleepingAnimationBit();
   protected final BipedActionController actionController = new BipedActionController();
   protected ArmatureMask upperBodyOnlyMask = new ArmatureMask(ArmatureMask.Mode.EXCLUDE_ONLY);

   public PlayerController() {
      this.upperBodyOnlyMask.exclude("root");
      this.upperBodyOnlyMask.exclude("head");
      this.upperBodyOnlyMask.exclude("leftLeg");
      this.upperBodyOnlyMask.exclude("leftForeLeg");
      this.upperBodyOnlyMask.exclude("rightLeg");
      this.upperBodyOnlyMask.exclude("rightForeLeg");
   }

   public void performActionAnimations(PlayerData data, AbstractClientPlayer player) {
      if (player.isEntityAlive() && player.isPlayerSleeping()) {
         this.actionController.clearAction();
      } else {
         EnumHandSide primaryHand = player.getPrimaryHand();
         ItemStack heldItemMainhand = player.getHeldItemMainhand();
         ItemStack heldItemOffhand = player.getHeldItemOffhand();
         Item activeItem = player.getActiveItemStack().getItem();
         this.actionController.perform(data, primaryHand, heldItemMainhand, heldItemOffhand, activeItem);
      }
   }

   public Collection<String> perform(PlayerData data) {
      AbstractClientPlayer player = (AbstractClientPlayer)data.getEntity();
      this.layerCape.playOrContinueBit(this.bitCape, data);
      if (player.isEntityAlive() && player.isPlayerSleeping()) {
         this.layerBase.playOrContinueBit(this.bitSleeping, data);
         this.layerSneak.clearAnimation();
      } else if (player.isRiding()) {
         if (player.getRidingEntity() instanceof EntityLivingBase) {
            this.layerBase.playOrContinueBit(this.bitRiding, data);
         } else {
            this.layerBase.playOrContinueBit(this.bitSitting, data);
         }

         this.layerSneak.clearAnimation();
      } else if (player.getTicksElytraFlying() > 4) {
         this.layerBase.playOrContinueBit(this.bitElytra, data);
         this.layerSneak.clearAnimation();
         this.layerTorch.clearAnimation();
      } else if (data.isClimbing()) {
         this.layerBase.playOrContinueBit(this.bitLadderClimb, data);
         this.layerSneak.clearAnimation();
         this.layerTorch.clearAnimation();
      } else if (player.isInWater()) {
         this.layerBase.playOrContinueBit(this.bitSwimming, data);
         this.layerSneak.clearAnimation();
         this.layerTorch.clearAnimation();
      } else if (data.isOnGround() && data.getTicksAfterTouchdown() >= 1.0F) {
         if (data.isStillHorizontally()) {
            this.layerBase.playOrContinueBit(this.bitStand, data);
            this.layerTorch.playOrContinueBit(this.bitTorchHolding, data);
         } else if (player.isSprinting()) {
            this.layerBase.playOrContinueBit(this.bitSprint, data);
            this.layerTorch.clearAnimation();
         } else {
            this.layerBase.playOrContinueBit(this.bitWalk, data);
            this.layerTorch.playOrContinueBit(this.bitTorchHolding, data);
         }

         if (player.isSneaking()) {
            this.layerSneak.playOrContinueBit(this.bitSneak, data);
         } else {
            this.layerSneak.clearAnimation();
         }
      } else {
         if (data.isFlying()) {
            this.layerBase.playOrContinueBit(this.bitFlying, data);
         } else if (data.getTicksFalling() > 10.0F) {
            this.layerBase.playOrContinueBit(this.bitFalling, data);
         } else if (player.isSprinting()) {
            this.layerBase.playOrContinueBit(this.bitSprintJump, data);
         } else {
            this.layerBase.playOrContinueBit(this.bitJump, data);
         }

         this.layerSneak.clearAnimation();
         this.layerTorch.clearAnimation();
      }

      data.renderLeftItemRotation.orientZero();
      data.renderRightItemRotation.orientZero();
      List<String> actions = new ArrayList();
      this.layerBase.perform(data, actions);
      this.layerSneak.perform(data, actions);
      this.layerTorch.perform(data, actions);
      this.performActionAnimations(data, player);
      this.layerCape.perform(data, actions);
      return actions;
   }
}
