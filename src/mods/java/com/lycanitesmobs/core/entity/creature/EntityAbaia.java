package com.lycanitesmobs.core.entity.creature;

import com.google.common.base.Predicate;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.damagesources.ElementDamageSource;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.info.ElementManager;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityAbaia extends TameableCreatureEntity implements IMob {
   protected short aoeAttackTick = 0;

   public EntityAbaia(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.spawnsOnLand = false;
      this.spawnsInWater = true;
      this.hasAttackSound = true;
      this.babySpawnChance = 0.05D;
      this.canGrow = true;
      this.setupMob();
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setLongMemory(false));
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.getEntityWorld().isRemote && this.hasAttackTarget() && ++this.aoeAttackTick == (this.isPetType("familiar") ? 100 : 40)) {
         this.aoeAttackTick = 0;

         for(Object entityObj : this.getNearbyEntities(EntityLivingBase.class, (Predicate)null, 4.0D)) {
            EntityLivingBase target = (EntityLivingBase)entityObj;
            if (target != this && this.canAttackClass(target.getClass()) && this.canAttackEntity(target) && this.getEntitySenses().canSee(target) && (target.isInWater() || this.isRareVariant())) {
               target.attackEntityFrom(ElementDamageSource.causeElementDamage(this, ElementManager.getInstance().getElement("lightning")), this.getAttackDamage(1.0D));
            }
         }
      }

      if (this.getEntityWorld().isRemote && this.hasAttackTarget()) {
         this.getEntityWorld().spawnParticle(EnumParticleTypes.CRIT, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D, new int[0]);

         for(Object entityObj : this.getNearbyEntities(EntityLivingBase.class, (Predicate)null, 4.0D)) {
            EntityLivingBase target = (EntityLivingBase)entityObj;
            if (this.canAttackClass(target.getClass()) && this.canAttackEntity(target) && this.getEntitySenses().canSee(target)) {
               this.getEntityWorld().spawnParticle(EnumParticleTypes.CRIT, target.posX + (this.rand.nextDouble() - 0.5D) * (double)target.width, target.posY + this.rand.nextDouble() * (double)target.height, target.posZ + (this.rand.nextDouble() - 0.5D) * (double)target.width, 0.0D, 0.0D, 0.0D, new int[0]);
            }
         }
      }

   }

   public float getBlockPathWeight(int x, int y, int z) {
      int waterWeight = 10;
      Block block = this.getEntityWorld().getBlockState(new BlockPos(x, y, z)).getBlock();
      if (block == Blocks.WATER) {
         return (super.getBlockPathWeight(x, y, z) + 1.0F) * (float)(waterWeight + 1);
      } else if (this.getEntityWorld().isRaining() && this.getEntityWorld().canBlockSeeSky(new BlockPos(x, y, z))) {
         return (super.getBlockPathWeight(x, y, z) + 1.0F) * (float)(waterWeight + 1);
      } else if (this.getAttackTarget() != null) {
         return super.getBlockPathWeight(x, y, z);
      } else {
         return this.waterContact() ? -999999.0F : super.getBlockPathWeight(x, y, z);
      }
   }

   public boolean isStrongSwimmer() {
      return true;
   }

   public boolean canWalk() {
      return false;
   }

   public int getNoBagSize() {
      return 0;
   }

   public int getBagSize() {
      return this.creatureInfo.bagSize;
   }

   public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
      return "lightning".equalsIgnoreCase(type) ? false : super.isDamageTypeApplicable(type, source, damage);
   }

   public boolean canBreatheUnderwater() {
      return true;
   }

   public boolean canBreatheAir() {
      return false;
   }
}
