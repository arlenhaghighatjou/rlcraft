package com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent;

import com.dhanantry.scapeandrunparasites.block.IMetaName;
import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.EntityDamage;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeRangeSwitch;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatusAOE;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackProjectile;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackRangedStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIEvadeDash;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanShoot;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPreeminent;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileHomming;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.BossInfo.Color;
import net.minecraft.world.BossInfo.Overlay;
import net.minecraftforge.event.ForgeEventFactory;

public class EntityPheon extends EntityPPreeminent implements EntityBodyParts, IRangedAttackMob, EntityCutomAttack, EntityCanShoot {
   private EntityBody head;
   private EntityBody middle;
   private final BossInfoServer bossInfo = (BossInfoServer)(new BossInfoServer(this.getDisplayName(), Color.RED, Overlay.PROGRESS)).setDarkenSky(false);

   public EntityPheon(World worldIn) {
      super(worldIn);
      this.setSize(2.0F, 3.6F);
      this.ignoreFrustumCheck = true;
      this.type = 63;
      this.borderOrb = -1;
      this.stepHeight = 1.0F;
      this.head = new EntityBody(this, 2.0F, 7.3F, 1.0F, -2.5F, 0.0F, -1, 1, false, 0.2F);
      this.middle = new EntityBody(this, 1.9F, 3.5F, 1.0F, 0.0F, 3.7F, 1, 2, false, 0.2F);
   }

   public int getParasiteIDRegister() {
      return 87;
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(0, new EntityAISwimmingDiving(this, 0.15D));
      this.tasks.addTask(6, new EntityAIAttackMeleeRangeSwitch(this, 10.0F));
      this.tasks.addTask(2, new EntityAIAttackMeleeStatusAOE(this, 1.0D, false, 100.0D, 5.0D));
      this.tasks.addTask(4, new EntityAIAttackRangedStatus(this, 1.0D, 20, 40, 40.0F));
      this.tasks.addTask(6, new EntityAIAttackProjectile(this, 60, 10, 3));
      this.tasks.addTask(3, new EntityAIFlightAttack(this, SRPConfig.preeminentFollow, true, 3));
      this.tasks.addTask(2, new EntityAIEvadeDash(this, 40, 2, 4, 5.0D, 100));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.PHEON_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.PHEON_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.283D);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.PHEON_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.preeminentFollow);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(2.0D);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      this.head.onUpdate();
      this.middle.onUpdate();
   }

   protected void updateAITasks() {
      super.updateAITasks();
      this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
   }

   public void setCustomNameTag(String name) {
      super.setCustomNameTag(name);
      this.bossInfo.setName(this.getDisplayName());
   }

   public void addTrackingPlayer(EntityPlayerMP player) {
   }

   public void removeTrackingPlayer(EntityPlayerMP player) {
      super.removeTrackingPlayer(player);
      this.bossInfo.removePlayer(player);
   }

   public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
   }

   public boolean attackEntityAsMobAOE(Entity entityIn) {
      AxisAlignedBB axisalignedbb = (new AxisAlignedBB(entityIn.posX, entityIn.posY, entityIn.posZ, entityIn.posX + 1.0D, entityIn.posY + 1.0D, entityIn.posZ + 1.0D)).grow(5.0D, 2.0D, 5.0D);
      List<EntityLivingBase> moblist = this.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);
      if (moblist.size() > 4) {
         axisalignedbb = (new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D)).grow(5.0D, 3.0D, 5.0D);
         moblist = this.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);
         float luck = (float)(SRPAttributes.TERLA_ATTACK_DAMAGE * 2.0D);

         for(EntityLivingBase mob : moblist) {
            if (mob != null && !(mob instanceof EntityParasiteBase) && mob != this) {
               EntityDamage damage = new EntityDamage(this.world, mob.posX, mob.posY, mob.posZ, 0.0F, this, luck, false, 3.0F);
               this.world.spawnEntity(damage);
            }
         }

         return true;
      } else {
         for(EntityLivingBase mob : moblist) {
            if (mob != null && !(mob instanceof EntityParasiteBase)) {
               this.attackEntityAsMob(mob);
            }
         }

         return !moblist.isEmpty();
      }
   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      boolean flag = super.attackEntityFrom(source, amount);
      if (flag && source.getTrueSource() instanceof EntityPlayerMP) {
         this.bossInfo.addPlayer((EntityPlayerMP)source.getTrueSource());
      }

      return flag;
   }

   public boolean attackEntityBodyFrom(DamageSource source, float amount, int id, boolean notify) {
      boolean flag = this.attackEntityFrom(source, amount);
      return flag;
   }

   public void setBodyPartDead(int id) {
   }

   public int getHorizontalFaceSpeed() {
      return 3;
   }

   public boolean isPotionApplicable(PotionEffect potioneffectIn) {
      return potioneffectIn.getPotion() == MobEffects.POISON ? false : super.isPotionApplicable(potioneffectIn);
   }

   public float getEyeHeight() {
      return 4.7F;
   }

   protected SoundEvent getAmbientSound() {
      return SRPSounds.MOBSILENCE;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return this.rand.nextBoolean() && this.getHitStatus() > 0 ? SRPSounds.MOBSILENCE : SRPSounds.MOBSILENCE;
   }

   protected SoundEvent getDeathSound() {
      return SRPSounds.MOBSILENCE;
   }

   public boolean scaryOrbEffect(EntityLivingBase in, int mobs) {
      boolean flag = super.scaryOrbEffect(in, mobs);
      if (flag) {
         ParasiteEventEntity.orbApplyEffects(in, this, SRPConfigMobs.pheonOrbEffects, mobs);
      }

      return flag;
   }

   protected float getSoundVolume() {
      return 5.0F;
   }

   public void setDead() {
      if (this.head != null) {
         this.world.removeEntityDangerously(this.head);
      }

      if (this.middle != null) {
         this.world.removeEntityDangerously(this.middle);
      }

      super.setDead();
   }

   public void setSwingingArms(boolean swingingArms) {
   }

   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
      if (this.rand.nextDouble() < SRPConfig.variantChance) {
         switch(this.rand.nextInt(1)) {
         case 0:
            this.setSkin(1);
            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.PHEON_HEALTH * 0.5D);
            this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.PHEON_ATTACK_DAMAGE * 1.5D);
            this.setHealth((float)this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue());
         }
      }

      return super.onInitialSpawn(difficulty, livingdata);
   }

   public Entity getProj(double accelX, double accelY, double accelZ) {
      this.playSound(SRPSounds.DORPA_RANGE, 2.0F, 1.0F);
      Vec3d vec3d = this.getLook(1.0F);
      EntityProjectileHomming entitylargefireball = new EntityProjectileHomming(this.world, this, this.getAttackTarget(), SRPAttributes.ORONCO_ATTACK_DAMAGE);
      entitylargefireball.posX = this.posX + vec3d.x;
      entitylargefireball.posY = this.posY + (double)this.getEyeHeight() - 0.2D;
      entitylargefireball.posZ = this.posZ + vec3d.z;
      return entitylargefireball;
   }

   public void playProjSound() {
   }

   public static void registerFixesTerla(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityPheon.class);
   }

   public void skillBreakBlocks() {
      if (this.blockH != 0.0F) {
         int blocksbroke = 0;
         int i1 = MathHelper.floor(this.posY + 0.1D);
         double l1 = this.posX;
         double i2 = this.posZ;
         boolean flag = false;
         int Brangeatm = this.BGrange;
         int offsetT = 0;
         if (this.getAttackTarget() != null) {
            EntityLivingBase target = this.getAttackTarget();
            if (target.getDistanceSq(this.posX, target.posY, this.posZ) < 9.0D) {
               if (target.posY - this.posY < -1.0D) {
                  offsetT -= 2;
                  if (!this.onGround) {
                     --offsetT;
                  }
               } else if (target.posY - this.posY > 2.0D) {
                  ++offsetT;
                  this.BGrange = 0;
               }
            }
         }

         for(int k2 = -1 * this.BGrange; k2 <= 1 * this.BGrange; ++k2) {
            for(int l2 = -1 * this.BGrange; l2 <= 1 * this.BGrange; ++l2) {
               for(int j = 1 + offsetT; j <= 8 + offsetT; ++j) {
                  double i3 = l1 + (double)k2;
                  double k = (double)(i1 + j);
                  double l = i2 + (double)l2;
                  BlockPos blockpos = new BlockPos(i3, k, l);
                  IBlockState iblockstate = this.world.getBlockState(blockpos);
                  Block block = iblockstate.getBlock();
                  float bHard = iblockstate.getBlockHardness(this.world, blockpos);
                  if (bHard <= this.blockH && bHard > 0.0F && (!(block instanceof IMetaName) || block == SRPBlocks.ParasiteCanister) && block != SRPBlocks.BiomeHeart && block != SRPBlocks.ColonyHeart && block != SRPBlocks.ParasiteRubbleDense && block != SRPBlocks.ParasiteCanisterActive) {
                     String name = block.getRegistryName().toString();
                     if (!this.blockException(name) && block != Blocks.AIR && block.canEntityDestroy(iblockstate, this.world, blockpos, this) && ForgeEventFactory.onEntityDestroyBlock(this, blockpos, iblockstate)) {
                        if (SRPConfig.cystActive) {
                           int meta = block.getMetaFromState(iblockstate);
                           flag = this.destroyBlockPos(blockpos, false) || flag;
                           if (SRPConfig.doTileDrops) {
                              this.addToBlockInv(name + ";" + meta);
                           }
                        } else {
                           this.destroyBlockPos(blockpos, SRPConfig.doTileDrops);
                        }

                        ++blocksbroke;
                     }
                  }
               }
            }
         }

         this.BGrange = Brangeatm;
         this.SkillBGflag = true;
      }
   }
}
