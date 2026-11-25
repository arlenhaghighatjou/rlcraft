package com.dhanantry.scapeandrunparasites.entity.monster.derived;

import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPCosmical;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityKirin extends EntityPCosmical {
   public EntityKirin(World worldIn) {
      super(worldIn);
      this.setSize(1.713333F, 8.7F);
      this.stepHeight = 1.0F;
      this.tasks.removeTask(this.folow);
   }

   public int getParasiteIDRegister() {
      return 67;
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(0, new EntityAISwimmingDiving(this, 0.08D));
      this.tasks.addTask(3, new EntityAIAttackMeleeStatus(this, 1.0D, true, 0.0D));
      this.tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.CRUXA_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.CRUXA_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.14D);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(SRPAttributes.CRUXA_KD_RESISTANCE);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.CRUXA_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.adaptedFollow);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.world.isRemote) {
         for(int i = 0; i < 4; ++i) {
            this.world.spawnParticle(EnumParticleTypes.PORTAL, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width * 3.0D, this.posY + this.rand.nextDouble() * (double)this.height - 0.25D, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width * 3.0D, (this.rand.nextDouble() - 0.5D) * 2.0D, -this.rand.nextDouble(), (this.rand.nextDouble() - 0.5D) * 2.0D, new int[0]);
         }
      }

   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      boolean flag = super.attackEntityFrom(source, amount);
      if (flag) {
         ;
      }

      return flag;
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      boolean flag = super.attackEntityAsMob(entityIn);
      if (flag) {
         ;
      }

      return flag;
   }

   public float getEyeHeight() {
      return 3.3F;
   }

   public static void registerFixesShyco(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityKirin.class);
   }

   public boolean scaryOrbEffect(EntityLivingBase in, int mobs) {
      boolean flag = super.scaryOrbEffect(in, mobs);
      if (flag) {
         ;
      }

      return flag;
   }

   protected void playStepSound(BlockPos pos, Block blockIn) {
      this.playSound(SRPSounds.MONSTER_STEP, 0.15F, 1.0F);
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
   }
}
