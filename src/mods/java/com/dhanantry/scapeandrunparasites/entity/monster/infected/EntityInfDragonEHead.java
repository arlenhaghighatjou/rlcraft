package com.dhanantry.scapeandrunparasites.entity.monster.infected;

import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileDragonE;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityInfDragonEHead extends EntityPInfected {
   public EntityInfDragonEHead(World worldIn) {
      super(worldIn);
      this.setSize(1.75F, 1.95F);
      this.killcount = -10.0D;
   }

   public int getParasiteIDRegister() {
      return 70;
   }

   protected void initEntityAI() {
      this.tasks.addTask(6, new EntityInfDragonEHead.AIFireballAttack(this));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, new Class[0]));
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.tasks.addTask(3, new EntityAIAttackMeleeStatus(this, 1.3D, false, -1.0D));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.INFDRAGONE_HEADHEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.INFDRAGONE_HEADDAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
   }

   public void setInWeb() {
   }

   public float getEyeHeight() {
      return 0.8F;
   }

   public static void registerFixesInfSheepHead(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityInfDragonEHead.class);
   }

   public void fall(float distance, float damageMultiplier) {
      super.fall(distance, damageMultiplier * 0.3F);
   }

   static class AIFireballAttack extends EntityAIBase {
      private final EntityInfDragonEHead parentEntity;
      public int attackTimer;

      public AIFireballAttack(EntityInfDragonEHead ghast) {
         this.parentEntity = ghast;
      }

      public boolean shouldExecute() {
         return this.parentEntity.getAttackTarget() != null;
      }

      public void startExecuting() {
         this.attackTimer = 0;
      }

      public void resetTask() {
      }

      public void updateTask() {
         EntityLivingBase entitylivingbase = this.parentEntity.getAttackTarget();
         double d0 = 64.0D;
         if (entitylivingbase != null) {
            if (entitylivingbase.getDistanceSq(this.parentEntity) < 4096.0D && this.parentEntity.canEntityBeSeen(entitylivingbase)) {
               World world = this.parentEntity.world;
               ++this.attackTimer;
               if (this.parentEntity.isPotionActive(SRPPotions.RAGE_E)) {
                  ++this.attackTimer;
               }

               if (this.attackTimer == 15) {
                  double d1 = 4.0D;
                  Vec3d vec3d = this.parentEntity.getLook(1.0F);
                  double d2 = entitylivingbase.posX - (this.parentEntity.posX + vec3d.x * 4.0D);
                  double d3 = entitylivingbase.getEntityBoundingBox().minY + (double)(entitylivingbase.height / 2.0F) - (0.5D + this.parentEntity.posY + (double)(this.parentEntity.height / 2.0F));
                  double d4 = entitylivingbase.posZ - (this.parentEntity.posZ + vec3d.z * 4.0D);
                  world.playEvent((EntityPlayer)null, 1016, new BlockPos(this.parentEntity), 0);
                  EntityProjectileDragonE entitylargefireball = new EntityProjectileDragonE(world, this.parentEntity, d2, d3, d4);
                  entitylargefireball.posX = this.parentEntity.posX + vec3d.x;
                  entitylargefireball.posY = this.parentEntity.posY + (double)(this.parentEntity.height / 2.0F) + 0.5D;
                  entitylargefireball.posZ = this.parentEntity.posZ + vec3d.z;
                  world.spawnEntity(entitylargefireball);
                  this.attackTimer = 0;

                  for(int i = 0; i <= 2; ++i) {
                     this.parentEntity.world.spawnParticle(EnumParticleTypes.FLAME, this.parentEntity.posX + vec3d.x * 4.0D, this.parentEntity.posY + (double)(this.parentEntity.height / 2.0F) + 0.5D, this.parentEntity.posZ + vec3d.z * 4.0D, 0.0D, -1.0D, 0.0D, new int[0]);
                  }
               }
            } else if (this.attackTimer > 0) {
               --this.attackTimer;
            }

         }
      }
   }
}
