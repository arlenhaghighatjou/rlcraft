package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.CreatureBuildTask;
import com.lycanitesmobs.core.entity.CreatureRelationshipEntry;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.PlaceBlockGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindMasterGoal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityVespid extends AgeableCreatureEntity implements IMob {
   public PlaceBlockGoal aiPlaceBlock;
   public CreatureBuildTask creatureBuildTask;
   private boolean hiveBuilding = true;

   public EntityVespid(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.ARTHROPOD;
      this.hasAttackSound = true;
      this.setupMob();
      this.canGrow = true;
      this.babySpawnChance = 0.1D;
      this.stepHeight = 1.0F;
      this.setAttackCooldownMax(10);
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setLongMemory(true));
      this.aiPlaceBlock = (new PlaceBlockGoal(this)).setMaxDistance(128.0D).setSpeed(3.0D).setReplaceLiquid(true).setReplaceSolid(true);
      this.tasks.addTask(this.nextIdleGoalIndex++, this.aiPlaceBlock);
      this.targetTasks.addTask(this.nextFindTargetIndex++, (new FindMasterGoal(this)).setTargetClass(EntityVespidQueen.class).setRange(64.0D));
   }

   public void loadCreatureFlags() {
      this.hiveBuilding = this.creatureInfo.getFlag("hiveBuilding", this.hiveBuilding);
   }

   public boolean isPersistant() {
      return this.getMasterTarget() instanceof BaseCreatureEntity ? ((BaseCreatureEntity)this.getMasterTarget()).isPersistant() : super.isPersistant();
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.getEntityWorld().isRemote && this.hiveBuilding) {
         if (this.getMasterTarget() instanceof EntityVespidQueen) {
            if (this.getMasterTarget().isDead && this.aiPlaceBlock.blockState != null) {
               this.aiPlaceBlock.resetTask();
            } else if (this.aiPlaceBlock.blockState == null) {
               EntityVespidQueen queen = (EntityVespidQueen)this.getMasterTarget();
               this.creatureBuildTask = queen.creatureStructure.takeBuildTask(this);
               if (this.creatureBuildTask != null) {
                  this.aiPlaceBlock.setBlockPlacement(this.creatureBuildTask.blockState, this.creatureBuildTask.pos);
               }
            }
         } else if (this.aiPlaceBlock.blockState != null) {
            this.aiPlaceBlock.resetTask();
         }
      }

      if (!this.getEntityWorld().isRemote && this.getAttackTarget() instanceof EntityConba && ((EntityConba)this.getAttackTarget()).vespidInfection) {
         this.setAttackTarget((EntityLivingBase)null);
      }

   }

   public void onBlockPlaced(BlockPos blockPos, IBlockState blockState) {
      if (this.getMasterTarget() instanceof EntityVespidQueen) {
         EntityVespidQueen queen = (EntityVespidQueen)this.getMasterTarget();
         if (this.creatureBuildTask != null) {
            queen.creatureStructure.completeBuildTask(this.creatureBuildTask);
            this.creatureBuildTask = null;
         }
      }

   }

   public boolean canAttackEntity(EntityLivingBase targetEntity) {
      if (targetEntity == this.getMasterTarget()) {
         return false;
      } else if (targetEntity instanceof EntityConba) {
         return false;
      } else if (!(targetEntity instanceof EntityVespid) || ((EntityVespid)targetEntity).hasMaster() && ((EntityVespid)targetEntity).getMasterTarget() != this.getMasterTarget()) {
         if (!(targetEntity instanceof EntityVespidQueen) || this.hasMaster() && this.getMasterTarget() != targetEntity) {
            if (this.hasMaster() && this.getMasterTarget() instanceof EntityVespidQueen) {
               EntityVespidQueen entityVespidQueen = (EntityVespidQueen)this.getMasterTarget();
               CreatureRelationshipEntry creatureRelationshipEntry = entityVespidQueen.relationships.getEntry(targetEntity);
               if (creatureRelationshipEntry != null && !creatureRelationshipEntry.canAttack()) {
                  return false;
               }
            }

            return super.canAttackEntity(targetEntity);
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean isFlying() {
      return true;
   }

   public float getDamageModifier(DamageSource damageSrc) {
      return damageSrc.isFireDamage() ? 4.0F : 1.0F;
   }

   public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
      return type.equals("inWall") ? false : super.isDamageTypeApplicable(type, source, damage);
   }

   public boolean canEntityBeSeen(Entity entity) {
      return entity instanceof EntityVespidQueen ? true : super.canEntityBeSeen(entity);
   }
}
