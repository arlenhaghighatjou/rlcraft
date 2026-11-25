package com.dhanantry.scapeandrunparasites.entity.monster.inborn;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.google.common.base.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityLodo extends EntityParasiteBase {
   private int totalGrowtime = 10;
   private int actualGrowtime = 0;
   protected double buried;

   public EntityLodo(World worldIn) {
      super(worldIn);
      this.setSize(0.5F, 0.3F);
      this.experienceValue = SRPAttributes.XP_LiTTLE;
      this.totalGrowtime = this.rand.nextInt(60) + 60;
      this.killcount = -10.0D;
      this.type = 1;
      this.buried = -1.0D;
   }

   public int getParasiteIDRegister() {
      return 5;
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(3, new EntityAIAvoidEntity(this, EntityLivingBase.class, new Predicate<EntityLivingBase>() {
         public boolean apply(@Nullable EntityLivingBase entity) {
            return !(entity instanceof EntityWaterMob) && !(entity instanceof EntityCreeper) && !(entity instanceof EntityParasiteBase) && !(entity instanceof EntityAnimal);
         }
      }, 8.0F, 1.0D, 1.0D));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.LODO_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.LODO_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.LODO_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(SRPAttributes.LODO_KD_RESISTANCE);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      this.growTimer();
      this.growStage();
      this.buried();
   }

   public boolean buried() {
      if (this.buried < 0.0D) {
         if (this.getParasiteStatus() == 3) {
            this.setParasiteStatus(0);
         }

         return false;
      } else {
         this.getNavigator().clearPath();
         this.posX = this.prevPosX;
         this.posZ = this.prevPosZ;
         int id = Block.getStateId(this.world.getBlockState((new BlockPos(this.posX, this.posY, this.posZ)).down()));
         this.buried -= 0.02D;

         for(int i = 0; i < 2; ++i) {
            this.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY, this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.rand.nextGaussian() * 0.02D, this.rand.nextGaussian() * 0.02D, this.rand.nextGaussian() * 0.02D, new int[]{id});
         }

         return true;
      }
   }

   protected void collideWithEntity(Entity entityIn) {
      super.collideWithEntity(entityIn);
      if (entityIn instanceof EntityLivingBase) {
         SRPPotions.applyStackPotion(SRPPotions.COTH_E, (EntityLivingBase)entityIn, 100, 0);
      }

   }

   protected void growStage() {
      if (!this.world.isRemote && this.actualGrowtime > this.totalGrowtime && ParasiteEventEntity.canSpawnNext) {
         this.playSound(SRPSounds.LODO_MUDO, 1.0F, 1.0F);
         ParasiteEventEntity.spawnNext(this, new EntityMudo(this.world), true, false);
      }

   }

   protected void growTimer() {
      if (!this.world.isRemote && this.ticksExisted % 20 == 0) {
         ++this.actualGrowtime;
      }

   }

   public float getEyeHeight() {
      return 0.3F;
   }

   public static void registerFixesLodo(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityLodo.class);
   }

   protected SoundEvent getAmbientSound() {
      return SRPSounds.LODO_GROWL;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SRPSounds.LODO_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SRPSounds.LODO_DEATH;
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setInteger("ruptergrow", this.actualGrowtime);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      if (compound.hasKey("ruptergrow", 99)) {
         this.actualGrowtime = compound.getInteger("ruptergrow");
      }

   }

   public void setFloorTimer() {
      this.buried = 1.0D;
   }

   @SideOnly(Side.CLIENT)
   public double getFloorTimer() {
      return this.buried;
   }

   @SideOnly(Side.CLIENT)
   public void handleStatusUpdate(byte id) {
      if (id == 50) {
         this.buried = 1.0D;
      } else {
         super.handleStatusUpdate(id);
      }

   }
}
