package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.dungeon.DungeonManager;
import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.CreatureBuildTask;
import com.lycanitesmobs.core.entity.CreatureStructure;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.StayByHomeGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindAttackTargetGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.item.consumable.CreatureTreatItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityVespidQueen extends TameableCreatureEntity implements IMob {
   public final CreatureStructure creatureStructure;
   protected int swarmLimit = 10;

   public EntityVespidQueen(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.ARTHROPOD;
      this.hasAttackSound = true;
      this.solidCollision = true;
      this.setupMob();
      this.canGrow = true;
      this.babySpawnChance = 0.0D;
      this.stepHeight = 1.0F;
      this.setAttackCooldownMax(10);
      this.creatureStructure = new CreatureStructure(this, DungeonManager.getInstance().getTheme("vespid_hive"));
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setLongMemory(true));
      this.tasks.addTask(this.nextTravelGoalIndex++, new StayByHomeGoal(this));
      this.targetTasks.addTask(this.nextFindTargetIndex++, (new FindAttackTargetGoal(this)).addTargets(this.getClass()));
      Class<? extends Entity> conbaType = CreatureManager.getInstance().getEntityClass("conba");
      if (conbaType != null) {
         this.targetTasks.addTask(this.nextFindTargetIndex++, (new FindAttackTargetGoal(this)).addTargets(conbaType));
      }

   }

   public void loadCreatureFlags() {
      this.swarmLimit = this.creatureInfo.getFlag("swarmLimit", this.swarmLimit);
   }

   public boolean isPersistant() {
      return this.hasHome() && this.getEntityWorld().getDifficulty() != EnumDifficulty.PEACEFUL ? true : super.isPersistant();
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.getEntityWorld().isRemote) {
         if (this.updateTick > 0L && this.updateTick % 20L == 0L) {
            if (!this.hasHome()) {
               this.creatureStructure.setOrigin(this.getPosition());
            }

            if (!this.creatureStructure.isPhaseComplete(0) || this.updateTick % 200L == 0L) {
               this.creatureStructure.refreshBuildTasks();
            }

            if (this.hasHome() && this.creatureStructure.getFinalPhaseBuildTaskSize() <= 10 && this.updateTick % 60L == 0L) {
               this.allyUpdate();
            }
         }

         if (!this.getEntityWorld().isRemote && this.getAttackTarget() instanceof EntityConba && ((EntityConba)this.getAttackTarget()).vespidInfection) {
            this.setAttackTarget((EntityLivingBase)null);
         }

      }
   }

   public void onBuildTaskComplete(CreatureBuildTask buildTask) {
      super.onBuildTaskComplete(buildTask);
      if (!this.hasHome()) {
         this.setHome(this.creatureStructure.getOrigin().getX(), this.creatureStructure.getOrigin().getY(), this.creatureStructure.getOrigin().getZ(), 8.0F);
      }

   }

   public void setHomePosition(int x, int y, int z) {
      super.setHomePosition(x, y, z);
      this.creatureStructure.setOrigin(new BlockPos(x, y, z));
   }

   public boolean rollWanderChance() {
      if (this.hasHome()) {
         return false;
      } else {
         return this.getRNG().nextDouble() <= 8.0E-4D;
      }
   }

   public void allyUpdate() {
      if (!this.getEntityWorld().isRemote) {
         if (this.swarmLimit > 0 && this.nearbyCreatureCount(CreatureManager.getInstance().getCreature("vespid").getEntityClass(), 32.0D) < this.swarmLimit) {
            float random = this.rand.nextFloat();
            if (random <= 0.05F) {
               EntityLivingBase minion = this.spawnAlly(this.posX - 2.0D + (double)(random * 4.0F), this.posY, this.posZ - 2.0D + (double)(random * 4.0F));
               if (minion instanceof AgeableCreatureEntity) {
                  ((AgeableCreatureEntity)minion).setGrowingAge(((AgeableCreatureEntity)minion).growthTime);
               }
            }
         }

      }
   }

   public EntityLivingBase spawnAlly(double x, double y, double z) {
      EntityLivingBase minion = CreatureManager.getInstance().getCreature("vespid").createEntity(this.getEntityWorld());
      minion.setLocationAndAngles(x, y, z, this.rand.nextFloat() * 360.0F, 0.0F);
      if (minion instanceof BaseCreatureEntity) {
         ((BaseCreatureEntity)minion).applyVariant(this.getVariantIndex());
      }

      this.getEntityWorld().spawnEntity(minion);
      if (this.getAttackTarget() != null) {
         minion.setRevengeTarget(this.getAttackTarget());
      }

      return minion;
   }

   public boolean attackMelee(Entity target, double damageScale) {
      if (!super.attackMelee(target, damageScale)) {
         return false;
      } else if (target instanceof EntityConba) {
         ((EntityConba)target).vespidInfection = true;
         return true;
      } else {
         return true;
      }
   }

   public boolean canAttackEntity(EntityLivingBase targetEntity) {
      if (targetEntity instanceof EntityConba && ((EntityConba)targetEntity).vespidInfection) {
         return false;
      } else {
         return !(targetEntity instanceof EntityVespid) || ((EntityVespid)targetEntity).hasMaster() && ((EntityVespid)targetEntity).getMasterTarget() != this ? super.canAttackEntity(targetEntity) : false;
      }
   }

   public boolean canAttackOwnSpecies() {
      return true;
   }

   public boolean canBeTempted() {
      return true;
   }

   public boolean isTamingItem(ItemStack itemStack) {
      if (!itemStack.isEmpty() && this.creatureInfo.creatureType != null) {
         if (itemStack.getItem() instanceof CreatureTreatItem) {
            CreatureTreatItem itemTreat = (CreatureTreatItem)itemStack.getItem();
            if (itemTreat.getCreatureType() == this.creatureInfo.creatureType) {
               return true;
            }
         }

         return super.isTamingItem(itemStack);
      } else {
         return false;
      }
   }

   public boolean isFlying() {
      return true;
   }

   public boolean canBePushed() {
      return false;
   }

   public float getDamageModifier(DamageSource damageSrc) {
      return damageSrc.isFireDamage() ? 4.0F : 1.0F;
   }

   public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
      return type.equals("inWall") ? false : super.isDamageTypeApplicable(type, source, damage);
   }

   public void setDead() {
      if (this.creatureStructure != null) {
         this.creatureStructure.removePitBLocks();
      }

      super.setDead();
   }
}
