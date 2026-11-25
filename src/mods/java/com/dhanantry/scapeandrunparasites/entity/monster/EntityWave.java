package com.dhanantry.scapeandrunparasites.entity.monster;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.init.Blocks;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class EntityWave extends EntityParasiteBase {
   private int raaa;
   private EntityLivingBase target;
   private int duration;

   public EntityWave(World worldIn) {
      super(worldIn);
      this.setSize(1.5F, 0.2F);
      this.tasks.removeTask(this.aiWander);
      this.tasks.removeTask(this.folow);
      this.killcount = -10.0D;
      this.MiniDamage = 0.1F;
      this.duration = 1;
   }

   protected void initEntityAI() {
      this.tasks.addTask(3, new EntityAIAttackMelee(this, 1.0D, false));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(1.0D);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.45D);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20.0D);
   }

   public void setDamages(double baseDamage, float min, int range, int durationF) {
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(baseDamage);
      this.MiniDamage = min;
      this.raaa = range;
      this.duration = durationF;
   }

   public int getParasiteIDRegister() {
      return 211;
   }

   public void onUpdate() {
      super.onUpdate();
      if (this.world.isRemote) {
         IBlockState state = this.world.getBlockState(this.getPosition().down());
         if (state.getBlock() != Blocks.AIR) {
            int id = Block.getStateId(state);

            for(int i = 0; i < 15; ++i) {
               this.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY, this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.rand.nextGaussian() * 0.02D, this.rand.nextGaussian() + 20.0D, this.rand.nextGaussian() * 0.02D, new int[]{id});
            }
         }
      } else {
         if (this.target != null && !this.target.isEntityAlive()) {
            this.setDead();
         }

         if (this.ticksExisted > 40) {
            if (this.posX == this.prevPosX || this.posZ == this.prevPosZ) {
               this.setDead();
            }

            if (this.ticksExisted > 20 * this.duration) {
               this.setDead();
            }
         }

         float f = this.width / 2.0F;
         float f1 = this.height;
         AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.posX - (double)f, this.posY, this.posZ - (double)f, this.posX + (double)f, this.posY + (double)f1, this.posZ + (double)f)).grow(0.4D, 0.2D, 0.4D);

         for(EntityLivingBase mob : this.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb)) {
            if (!(mob instanceof EntityParasiteBase)) {
               this.attackEntityAsMobMinimum(mob);
            }
         }
      }

   }

   protected void jump() {
      this.setDead();
   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      return false;
   }

   public boolean isPotionApplicable(PotionEffect potioneffectIn) {
      return false;
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      return false;
   }

   public AxisAlignedBB getCollisionBoundingBox() {
      return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
   }

   public void onKillEntity(EntityLivingBase entityLivingIn) {
      super.onKillEntity(entityLivingIn);
      if (!this.world.isRemote) {
         if (this.target == entityLivingIn) {
            this.setDead();
         }

      }
   }

   public void setAttackTarget(EntityLivingBase entitylivingbaseIn) {
      if (this.ticksExisted > 40) {
         this.setDead();
      }

      super.setAttackTarget(entitylivingbaseIn);
      this.target = entitylivingbaseIn;
   }
}
