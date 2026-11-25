package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityReaper extends TameableCreatureEntity implements IMob {
   public EntityReaper(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEAD;
      this.hasAttackSound = false;
      this.trueSight = true;
      this.setupMob();
      this.noClip = true;
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackRangedGoal(this)).setSpeed(0.75D).setRange(14.0F).setMinChaseDistance(0.75F).setCheckSight(false));
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setLongMemory(true));
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.getEntityWorld().isRemote) {
         for(int i = 0; i < 2; ++i) {
            this.getEntityWorld().spawnParticle(EnumParticleTypes.SPELL_WITCH, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D, new int[0]);
         }
      }

   }

   public void attackRanged(Entity target, float range) {
      this.fireProjectile("spectralbolt", target, range, 0.0F, new Vec3d(0.0D, 0.0D, 0.0D), 1.2F, 2.0F, 1.0F);
      super.attackRanged(target, range);
   }

   public boolean isFlying() {
      return true;
   }

   public boolean useDirectNavigator() {
      return true;
   }

   public boolean canEntityBeSeen(Entity target) {
      return true;
   }

   public boolean petControlsEnabled() {
      return true;
   }

   public int getNoBagSize() {
      return 0;
   }

   public int getBagSize() {
      return this.creatureInfo.bagSize;
   }

   public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
      return type.equals("inWall") ? false : super.isDamageTypeApplicable(type, source, damage);
   }

   public boolean daylightBurns() {
      return super.daylightBurns();
   }

   public boolean canBurn() {
      return true;
   }

   protected SoundEvent getAmbientSound() {
      if (this.hasAttackTarget() && this.getAttackTarget() instanceof EntityPlayer && "jbams".equalsIgnoreCase(this.getAttackTarget().getName())) {
         return AssetManager.getSound(this.creatureInfo.getName() + "_say_jon");
      } else {
         return this.isTamed() && this.getOwner() != null && "jbams".equalsIgnoreCase(this.getOwnerName()) ? AssetManager.getSound(this.creatureInfo.getName() + "_say_jon") : super.getAmbientSound();
      }
   }

   public ResourceLocation getTexture(String suffix) {
      if (!"Satan Claws".equals(this.getCustomNameTag())) {
         return super.getTexture(suffix);
      } else {
         String textureName = this.getTextureName() + "_satanclaws";
         if (!"".equals(suffix)) {
            textureName = textureName + "_" + suffix;
         }

         if (AssetManager.getTexture(textureName) == null) {
            AssetManager.addTexture(textureName, this.creatureInfo.modInfo, "textures/entity/" + textureName.toLowerCase() + ".png");
         }

         return AssetManager.getTexture(textureName);
      }
   }
}
