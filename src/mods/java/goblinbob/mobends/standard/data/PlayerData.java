package goblinbob.mobends.standard.data;

import goblinbob.mobends.core.client.event.DataUpdateHandler;
import goblinbob.mobends.core.client.model.ModelPartTransform;
import goblinbob.mobends.standard.animation.controller.PlayerController;
import goblinbob.mobends.standard.main.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.EnumHand;

public class PlayerData extends BipedEntityData<AbstractClientPlayer> {
   protected boolean sprintJumpLeg = false;
   protected boolean sprintJumpLegSwitched = false;
   protected boolean fistPunchArm = false;
   protected int currentAttack = 0;
   protected float capeWavePhase = 0.0F;
   protected float capeWaveSpeed = 0.0F;
   private Boolean flyingStateOverride = null;
   public ModelPartTransform cape;
   private final PlayerController controller = new PlayerController();

   public PlayerData(AbstractClientPlayer entity) {
      super(entity);
   }

   public PlayerController getController() {
      return this.controller;
   }

   public void setCapeWaveSpeed(float value) {
      this.capeWaveSpeed = value;
   }

   public float getCapeWavePhase() {
      return this.capeWavePhase;
   }

   public void overrideFlyingState(boolean flying) {
      this.flyingStateOverride = flying;
   }

   public void unsetFlyingStateOverride() {
      this.flyingStateOverride = null;
   }

   public void onTicksRestart() {
   }

   public void initModelPose() {
      super.initModelPose();
      Render<AbstractClientPlayer> render = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(this.entity);
      this.cape = new ModelPartTransform(this.body);
      this.nameToPartMap.put("cape", this.cape);
      this.cape.position.set(0.0F, 0.0F, 0.0F);
      if (((RenderPlayer)render).smallArms) {
         this.rightArm.position.set(-5.0F, -9.5F, 0.0F);
         this.leftArm.position.set(5.0F, -9.5F, 0.0F);
      }

   }

   public void updateParts(float ticksPerFrame) {
      super.updateParts(ticksPerFrame);
      this.cape.update(ticksPerFrame);
   }

   public void update(float partialTicks) {
      super.update(partialTicks);
      if (this.getTicksAfterAttack() > 20.0F) {
         this.currentAttack = 0;
      }

      if (this.motionY < 0.0D) {
         this.sprintJumpLegSwitched = false;
      }

      if (!this.sprintJumpLegSwitched && this.motionY > 0.0D) {
         this.sprintJumpLeg = !this.sprintJumpLeg;
         this.sprintJumpLegSwitched = true;
      }

      this.capeWavePhase += this.capeWaveSpeed * DataUpdateHandler.ticksPerFrame;
      if (this.capeWavePhase > 380.0F) {
         this.capeWavePhase -= 380.0F;
      }

   }

   public void onLiftoff() {
      super.onLiftoff();
      if (!this.sprintJumpLegSwitched) {
         this.sprintJumpLeg = !this.sprintJumpLeg;
         this.sprintJumpLegSwitched = true;
      }

   }

   public void onAttack() {
      if (((AbstractClientPlayer)this.entity).getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.AIR) {
         this.fistPunchArm = !this.fistPunchArm;
         this.ticksAfterAttack = 0.0F;
      } else if (this.ticksAfterAttack > 6.0F) {
         switch(this.currentAttack) {
         case 1:
            this.currentAttack = 2;
            break;
         case 2:
            this.currentAttack = 3;
            break;
         case 3:
            this.currentAttack = 4;
            break;
         case 4:
            this.currentAttack = ModConfig.performSpinAttack && !((AbstractClientPlayer)this.getEntity()).isRiding() ? 5 : 1;
            break;
         default:
            this.currentAttack = 1;
         }

         this.ticksAfterAttack = 0.0F;
      }
   }

   public int getCurrentAttack() {
      return this.currentAttack;
   }

   public boolean getFistPunchArm() {
      return this.fistPunchArm;
   }

   public boolean getSprintJumpLeg() {
      return this.sprintJumpLeg;
   }

   public boolean isFlying() {
      return this.flyingStateOverride != null ? this.flyingStateOverride.booleanValue() : ((AbstractClientPlayer)this.entity).capabilities.isFlying;
   }
}
