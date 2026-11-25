package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.core.entity.goals.actions.PlayerControlGoal;
import com.lycanitesmobs.core.entity.goals.targeting.CopyRiderAttackTargetGoal;
import com.lycanitesmobs.core.entity.goals.targeting.RevengeRiderGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import java.util.HashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class RideableCreatureEntity extends TameableCreatureEntity {
   public Entity lastRiddenByEntity = null;
   public boolean mountJumping = false;
   public float jumpPower = 0.0F;
   public boolean abilityToggled = false;
   public boolean inventoryToggled = false;

   public RideableCreatureEntity(World world) {
      super(world);
      this.hasJumpSound = true;
   }

   protected void entityInit() {
      super.entityInit();
   }

   protected void initEntityAI() {
      this.tasks.addTask(this.nextPriorityGoalIndex++, new PlayerControlGoal(this));
      super.initEntityAI();
      this.targetTasks.addTask(this.nextReactTargetIndex++, new RevengeRiderGoal(this));
      this.targetTasks.addTask(this.nextReactTargetIndex++, new CopyRiderAttackTargetGoal(this));
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.lastRiddenByEntity != this.getControllingPassenger()) {
         if (this.lastRiddenByEntity != null) {
            this.onDismounted(this.lastRiddenByEntity);
         }

         this.lastRiddenByEntity = this.getControllingPassenger();
      }

      if (this.hasRiderTarget()) {
         if (this.getControllingPassenger() instanceof EntityLivingBase) {
            EntityLivingBase riderLiving = (EntityLivingBase)this.getControllingPassenger();
            this.riderEffects(riderLiving);
            if (!this.canBurn()) {
               riderLiving.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 105, 1));
            }

            for(Object possibleEffect : riderLiving.getActivePotionEffects().toArray(new Object[riderLiving.getActivePotionEffects().size()])) {
               if (possibleEffect instanceof PotionEffect) {
                  PotionEffect potionEffect = (PotionEffect)possibleEffect;
                  if (!this.isPotionApplicable(potionEffect)) {
                     riderLiving.removePotionEffect(potionEffect.getPotion());
                  }
               }
            }
         }

         if (!this.getEntityWorld().isRemote && this.hasAttackTarget() && this.updateTick % 20L == 0L) {
            EntityLivingBase mountedAttackTarget = this.getAttackTarget();
            if (mountedAttackTarget != null && this.canAttackEntity(mountedAttackTarget) && this.getDistanceSq(mountedAttackTarget.posX, mountedAttackTarget.getEntityBoundingBox().minY, mountedAttackTarget.posZ) <= this.getMeleeAttackRange(mountedAttackTarget, 1.0D)) {
               this.attackMelee(this.getAttackTarget(), 1.0D);
            }
         }

         if (!this.getEntityWorld().isRemote && this.getControllingPassenger() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)this.getControllingPassenger();
            ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
            if (playerExt != null && playerExt.isControlActive(ExtendedPlayer.CONTROL_ID.MOUNT_DISMOUNT)) {
               player.dismountRidingEntity();
            }
         }
      } else {
         this.abilityToggled = false;
         this.inventoryToggled = false;
      }

   }

   public void riderEffects(EntityLivingBase rider) {
      if (!rider.canBreatheUnderwater() && this.canBreatheUnderwater() && rider.isInWater()) {
         rider.setAir(300);
      }

   }

   public void mountAbility(Entity rider) {
   }

   public void onDismounted(Entity entity) {
      if (this.isSitting()) {
         int homeY = MathHelper.floor(this.posY);
         if (!this.isCurrentlyFlying()) {
            homeY = this.getGroundY(this.getPosition());
         }

         this.setHomePosition(MathHelper.floor(this.posX), homeY, MathHelper.floor(this.posZ));
      }

   }

   public boolean canBePushed() {
      return this.getControllingPassenger() != null ? false : super.canBePushed();
   }

   public boolean canBeSteered() {
      if (this.getEntityWorld().isRemote) {
         return true;
      } else {
         Entity entity = this.getControllingPassenger();
         return entity == this.getOwner();
      }
   }

   protected boolean isMovementBlocked() {
      return super.isMovementBlocked();
   }

   public void updatePassenger(Entity passenger) {
      if (this.isPassenger(passenger)) {
         double zOffset = this.getMountedZOffset();
         if (zOffset == 0.0D) {
            zOffset = 1.0E-5D;
         }

         Vec3d mountOffset = this.getFacingPositionDouble(0.0D, 0.0D, 0.0D, zOffset, (double)this.rotationYaw);
         this.getControllingPassenger().setPosition(this.posX + mountOffset.x, this.posY + this.getMountedYOffset() + passenger.getYOffset(), this.posZ + mountOffset.z);
      }

   }

   private void mount(Entity entity) {
      entity.rotationYaw = this.rotationYaw;
      entity.rotationPitch = this.rotationPitch;
      if (!this.getEntityWorld().isRemote) {
         entity.startRiding(this);
      }

   }

   public float getStafeSpeed() {
      return 0.5F;
   }

   public void travel(float strafe, float up, float forward) {
      if (this.isTamed() && this.hasSaddle() && this.hasRiderTarget() && this.getControllingPassenger() instanceof EntityLivingBase && this.riderControl()) {
         this.moveMountedWithHeading(strafe, up, forward);
      } else {
         super.travel(strafe, up, forward);
      }
   }

   public void moveMountedWithHeading(float strafe, float up, float forward) {
      if (this.getControllingPassenger() instanceof EntityLivingBase) {
         EntityLivingBase rider = (EntityLivingBase)this.getControllingPassenger();
         this.prevRotationYaw = this.rotationYaw = rider.rotationYaw;
         this.rotationPitch = rider.rotationPitch * 0.5F;
         this.setRotation(this.rotationYaw, this.rotationPitch);
         this.rotationYawHead = this.renderYawOffset = this.rotationYaw;
         strafe = rider.moveStrafing * this.getStafeSpeed();
         forward = rider.moveForward;
      }

      double verticalMotion = 0.0D;
      if (!this.isInWater() && !this.isInLava() && !this.isFlying()) {
         if (!this.isMountJumping() && this.getControllingPassenger() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)this.getControllingPassenger();
            ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
            if (playerExt != null && playerExt.isControlActive(ExtendedPlayer.CONTROL_ID.JUMP)) {
               this.startJumping();
            }
         }

         if (this.getJumpPower() > 0.0F && !this.isMountJumping() && this.canPassengerSteer()) {
            this.motionY = this.getMountJumpHeight() * (double)this.getJumpPower();
            if (this.isPotionActive(MobEffects.JUMP_BOOST)) {
               this.motionY += (double)((float)(this.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1F);
            }

            this.setMountJumping(true);
            this.isAirBorne = true;
            if (forward > 0.0F) {
               float f2 = MathHelper.sin(this.rotationYaw * 3.1415927F / 180.0F);
               float f3 = MathHelper.cos(this.rotationYaw * 3.1415927F / 180.0F);
               this.motionX += (double)(-0.4F * f2 * this.jumpPower);
               this.motionZ += (double)(0.4F * f3 * this.jumpPower);
            }

            if (!this.getEntityWorld().isRemote) {
               this.playJumpSound();
            }

            this.setJumpPower(0);
            ForgeHooks.onLivingJump(this);
         }

         this.jumpMovementFactor = (float)((double)this.getAIMoveSpeed() * this.getGlideScale());
      } else if (this.getControllingPassenger() instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)this.getControllingPassenger();
         ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
         if (playerExt != null && playerExt.isControlActive(ExtendedPlayer.CONTROL_ID.JUMP)) {
            verticalMotion = this.creatureStats.getSpeed() * 20.0D;
         } else if (playerExt != null && playerExt.isControlActive(ExtendedPlayer.CONTROL_ID.DESCEND)) {
            verticalMotion = -this.creatureStats.getSpeed() * 20.0D;
         } else {
            verticalMotion = 0.0D;
         }
      }

      if (this.getControllingPassenger() instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)this.getControllingPassenger();
         ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
         if (playerExt != null) {
            if (playerExt.isControlActive(ExtendedPlayer.CONTROL_ID.MOUNT_ABILITY)) {
               this.mountAbility(player);
               this.abilityToggled = true;
            } else {
               this.abilityToggled = false;
            }

            if (playerExt.isControlActive(ExtendedPlayer.CONTROL_ID.MOUNT_INVENTORY)) {
               if (!this.inventoryToggled) {
                  this.openGUI(player);
               }

               this.inventoryToggled = true;
            } else {
               this.inventoryToggled = false;
            }
         }
      }

      if (this.canPassengerSteer()) {
         this.setAIMoveSpeed((float)this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
         if (!this.useDirectNavigator()) {
            if (this.isFlying() && !this.isInWater() && !this.isInLava()) {
               this.moveRelative(strafe, 0.0F, forward, 0.1F);
               this.move(MoverType.SELF, this.motionX, verticalMotion / 16.0D, this.motionZ);
               this.motionX *= 0.8999999761581421D;
               this.motionY *= 0.8999999761581421D;
               this.motionZ *= 0.8999999761581421D;
            } else if (this.isInWater()) {
               if (!this.canBreatheUnderwater()) {
                  verticalMotion *= 0.25D;
                  strafe *= 0.25F;
                  forward *= 0.25F;
               }

               this.moveRelative(strafe, 0.0F, forward, 0.1F);
               this.move(MoverType.SELF, this.motionX, verticalMotion / 16.0D, this.motionZ);
               this.motionX *= 0.8999999761581421D;
               this.motionY *= 0.8999999761581421D;
               this.motionZ *= 0.8999999761581421D;
            } else if (this.isInLava()) {
               if (!this.isStrongSwimmer()) {
                  verticalMotion *= 0.25D;
                  strafe *= 0.25F;
                  forward *= 0.25F;
               }

               this.moveRelative(strafe, 0.0F, forward, 0.1F);
               this.move(MoverType.SELF, this.motionX, verticalMotion / 16.0D, this.motionZ);
               this.motionX *= 0.8999999761581421D;
               this.motionY *= 0.8999999761581421D;
               this.motionZ *= 0.8999999761581421D;
            } else {
               super.travel(strafe, up, forward);
            }
         } else {
            this.directNavigator.flightMovement(strafe, forward);
         }
      }

      if (this.onGround || this.isInWater() || this.isInLava()) {
         this.setJumpPower(0);
         this.setMountJumping(false);
      }

      this.prevLimbSwingAmount = this.limbSwingAmount;
      double d0 = this.posX - this.prevPosX;
      double d1 = this.posZ - this.prevPosZ;
      float f4 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;
      if (f4 > 1.0F) {
         f4 = 1.0F;
      }

      this.limbSwingAmount += (f4 - this.limbSwingAmount) * 0.4F;
      this.limbSwing += this.limbSwingAmount;
   }

   public void startJumping() {
      this.setJumpPower();
   }

   public double getMountJumpHeight() {
      return 0.75D;
   }

   public boolean isMountJumping() {
      return this.mountJumping;
   }

   public void setMountJumping(boolean set) {
      this.mountJumping = set;
   }

   public void setJumpPower(int power) {
      if (power < 0) {
         power = 0;
      }

      if (power > 99) {
         power = 99;
      }

      if (power < 90) {
         this.jumpPower = 1.0F * ((float)power / 89.0F);
      } else {
         this.jumpPower = 1.0F + 1.0F * ((float)(power - 89) / 10.0F);
      }

   }

   public void setJumpPower() {
      this.setJumpPower(89);
   }

   public float getJumpPower() {
      return this.jumpPower;
   }

   public double getGlideScale() {
      return 0.10000000149011612D;
   }

   public boolean riderControl() {
      return true;
   }

   public HashMap<Integer, String> getInteractCommands(EntityPlayer player, EnumHand hand, ItemStack itemStack) {
      HashMap<Integer, String> commands = new HashMap();
      commands.putAll(super.getInteractCommands(player, hand, itemStack));
      boolean mountingAllowed = CreatureManager.getInstance().config.mountingEnabled;
      if (mountingAllowed && this.isFlying()) {
         mountingAllowed = CreatureManager.getInstance().config.mountingFlightEnabled;
      }

      if (this.canBeMounted(player) && !player.isSneaking() && !this.getEntityWorld().isRemote && mountingAllowed) {
         commands.put(Integer.valueOf(BaseCreatureEntity.COMMAND_PIORITIES.MAIN.id), "Mount");
      }

      return commands;
   }

   public boolean performCommand(String command, EntityPlayer player, EnumHand hand, ItemStack itemStack) {
      if (command.equals("Mount")) {
         this.playMountSound();
         this.clearMovement();
         this.setAttackTarget((EntityLivingBase)null);
         this.mount(player);
         return true;
      } else {
         return super.performCommand(command, player, hand, itemStack);
      }
   }

   public Team getTeam() {
      if (this.hasRiderTarget()) {
         EntityLivingBase rider = this.getRider();
         if (rider != null) {
            return rider.getTeam();
         }
      }

      return super.getTeam();
   }

   public boolean isOnSameTeam(Entity target) {
      if (this.hasRiderTarget()) {
         EntityLivingBase rider = this.getRider();
         if (target == rider) {
            return true;
         }

         if (rider != null && target != null) {
            return rider.isOnSameTeam(target);
         }
      }

      return super.isOnSameTeam(target);
   }

   public boolean canBeMounted(Entity entity) {
      if (this.getControllingPassenger() != null) {
         return false;
      } else {
         if (this.isTamed() && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            if (player == this.getOwner()) {
               return this.hasSaddle() && !this.isChild();
            }
         } else if (!this.isTamed() && !(entity instanceof EntityPlayer)) {
            return !this.isChild();
         }

         return false;
      }
   }

   public boolean hasSaddle() {
      ItemStack saddleStack = this.inventory.getEquipmentStack("saddle");
      return !saddleStack.isEmpty() && saddleStack.getItem() == this.creatureInfo.creatureType.saddle;
   }

   public boolean attackEntityFrom(DamageSource damageSource, float damageAmount) {
      Entity entity = damageSource.getTrueSource();
      return this.getControllingPassenger() != null && this.isRidingOrBeingRiddenBy(entity) ? false : super.attackEntityFrom(damageSource, damageAmount);
   }

   public float getFallResistance() {
      return 2.0F;
   }

   public void playMountSound() {
      this.playSound(AssetManager.getSound(this.creatureInfo.getName() + "_mount"), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
   }
}
