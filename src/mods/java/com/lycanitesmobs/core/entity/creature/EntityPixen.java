package com.lycanitesmobs.core.entity.creature;

import com.google.common.base.Predicate;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.goals.actions.TemptGoal;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class EntityPixen extends TameableCreatureEntity implements IMob {
   protected boolean wantsToLand;
   protected boolean isLanded;
   public int auraRate = 60;
   public List<String> auraEffects = new ArrayList();
   public int auraDuration = 100;
   public int auraAmplifier = 0;

   public EntityPixen(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.spawnsOnLand = true;
      this.spawnsInWater = true;
      this.hasAttackSound = false;
      this.flySoundSpeed = 5;
      this.stepHeight = 1.0F;
      this.setupMob();
      this.auraEffects.add("minecraft:speed");
      this.auraEffects.add("minecraft:haste");
      this.auraEffects.add("minecraft:jump_boost");
      this.auraEffects.add("lycanitesmobs:fallresist");
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextDistractionGoalIndex++, (new TemptGoal(this)).setAlwaysTempted(true));
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackRangedGoal(this)).setSpeed(1.0D).setRange(14.0F).setMinChaseDistance(5.0F).setCheckSight(false));
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.getEntityWorld().isRemote) {
         if (this.isLanded) {
            this.wantsToLand = false;
            if (!this.isSitting() && this.updateTick % 100L == 0L && this.getRNG().nextBoolean()) {
               this.leap(1.0D, 1.0D);
               this.isLanded = false;
            }
         } else if (this.wantsToLand) {
            if (this.isSafeToLand()) {
               this.isLanded = true;
            }
         } else if (this.updateTick % 100L == 0L && this.getRNG().nextBoolean()) {
            this.wantsToLand = true;
         }
      }

      if (!this.getEntityWorld().isRemote && this.auraRate > 0 && !this.isPetType("familiar") && this.updateTick % (long)this.auraRate == 0L) {
         for(Object entityObj : this.getNearbyEntities(EntityLivingBase.class, (Predicate)null, 4.0D)) {
            EntityLivingBase target = (EntityLivingBase)entityObj;
            if (target != this && !(target instanceof EntityPixen) && target != this.getAttackTarget() && target != this.getAvoidTarget()) {
               int randomIndex = this.getRNG().nextInt(this.auraEffects.size());
               Potion effect = (Potion)GameRegistry.findRegistry(Potion.class).getValue(new ResourceLocation((String)this.auraEffects.get(randomIndex)));
               if (effect != null) {
                  target.addPotionEffect(new PotionEffect(effect, this.auraDuration, this.auraAmplifier));
               }
            }
         }
      }

   }

   public BlockPos getWanderPosition(BlockPos wanderPosition) {
      if (this.wantsToLand || !this.isLanded) {
         BlockPos groundPos;
         for(groundPos = wanderPosition.down(); groundPos.getY() > 0 && this.getEntityWorld().getBlockState(groundPos).getBlock() == Blocks.AIR; groundPos = groundPos.down()) {
            ;
         }

         if (this.getEntityWorld().getBlockState(groundPos).getMaterial().isSolid()) {
            return groundPos.up();
         }
      }

      return super.getWanderPosition(wanderPosition);
   }

   public double getFlightOffset() {
      if (!this.wantsToLand) {
         super.getFlightOffset();
      }

      return 0.0D;
   }

   public void attackRanged(Entity target, float range) {
      this.fireProjectile("tricksterflare", target, range, 0.0F, new Vec3d(0.0D, 0.0D, 0.0D), 0.75F, 1.0F, 1.0F);
      super.attackRanged(target, range);
   }

   public boolean isFlying() {
      return !this.isLanded;
   }

   public boolean isStrongSwimmer() {
      return false;
   }

   public boolean canBreatheUnderwater() {
      return false;
   }

   public boolean canBeTempted() {
      return !this.isInPack() && this.getRevengeTarget() == null;
   }

   public boolean isAggressive() {
      return !this.isInPack() && this.getRevengeTarget() == null ? false : super.isAggressive();
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

   public float getFallResistance() {
      return 100.0F;
   }
}
