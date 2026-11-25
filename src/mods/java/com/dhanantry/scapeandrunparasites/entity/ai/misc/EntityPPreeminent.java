package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import com.dhanantry.scapeandrunparasites.block.BlockGore;
import com.dhanantry.scapeandrunparasites.entity.EntityRemain;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityFlam;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityGore;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.google.common.base.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class EntityPPreeminent extends EntityPMalleable implements EntityCanSummon {
   protected int totalP;
   protected int actualP;
   protected int[] mobID;
   protected int[] mobPT;
   private int ggg;
   protected boolean canTeleportToo;

   public EntityPPreeminent(World worldIn) {
      super(worldIn);
      this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus(this, EntityPlayer.class, 0, SRPConfig.preeminentWalls, false, (Predicate)null, SRPConfig.preeminentSneakPen, SRPConfig.preeminentInviPen));
      if (SRPConfig.mobattacking) {
         this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus(this, EntityLiving.class, 0, SRPConfig.preeminentWalls, false, new Predicate<EntityLiving>() {
            public boolean apply(@Nullable EntityLiving entity) {
               return !(entity instanceof EntityWaterMob) && !(entity instanceof EntityAnimal) && !(entity instanceof EntityVillager) && !ParasiteEventEntity.checkEntity(entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
            }
         }, SRPConfig.preeminentSneakPen, SRPConfig.preeminentInviPen));
      }

      this.tasks.removeTask(this.folow);
      this.borderOrb = -1;
      this.canModRender = 0;
      this.experienceValue = SRPAttributes.XP_PREE;
      this.canD = SRPConfig.preeminentdespawn;
      this.damageCap = SRPConfig.preeminentCap;
      this.fuseTime = 70;
      this.type = 51;
      this.killcount = 10.0D;
      this.fuseOrb = 13;
      this.orbStartTimer = 15;
      this.foodSteal = SRPConfig.preeminentFoodSteal;
      this.orbItemCool = SRPConfig.preeminentItemOrbCooldown * 20;
      this.pointCap = SRPConfig.preeminentPointCap;
      this.pointReduction = SRPConfig.preeminentPointRed;
      this.chanceLearn = SRPConfig.preeminentChanceLe;
      this.chanceLearnFire = SRPConfig.preeminentChanceLeFire;
      this.DamageTypeCap = SRPConfig.preeminentPointDamCap;
      this.MiniDamage = SRPConfig.preeminentMinDamage;
      this.regen = SRPConfig.preeminentRegen * SRPConfig.globalHealthMultiplier;
      this.oneMindDeathValue = SRPConfig.preeminentOneMindDeathV;
      this.regenEff = 3;
      this.foodRott = SRPConfig.preeminentFoodChance;
      this.foodRootNumber = SRPConfig.preeminentFoodAmount;
      this.totalP = SRPConfig.preeminentFlamTotal;
      this.mobID = new int[this.totalP];
      this.mobPT = new int[this.totalP];

      for(int i = 0; i < this.mobID.length; ++i) {
         this.mobID[i] = -777;
      }

      this.valueEvDeath = SRPConfig.preeminentLoosingEPValue;
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      ++this.ggg;
      if (this.ggg > 80) {
         this.ggg = 0;
      }

      if (this.getAttackTarget() != null) {
         this.summonFlam(this.getAttackTarget());
      }

   }

   protected void fearPlayer(EntityLivingBase player) {
      try {
         if (player == null) {
            return;
         }

         if (!this.canEntityBeSeen(player)) {
            return;
         }

         if (!player.isPotionActive(SRPPotions.FEAR_E)) {
            player.addPotionEffect(new PotionEffect(SRPPotions.FEAR_E, 300, 2, false, false));
         } else if (player.getActivePotionEffect(SRPPotions.FEAR_E).getAmplifier() < 2) {
            player.addPotionEffect(new PotionEffect(SRPPotions.FEAR_E, 300, 2, false, false));
         }
      } catch (Exception var3) {
         ;
      }

   }

   public boolean attackEntityAsMob(Entity entityIn) {
      boolean flag = super.attackEntityAsMob(entityIn);
      if (flag && entityIn instanceof EntityLivingBase && this.rand.nextDouble() < (double)SRPConfigSystems.cothPure) {
         EntityLivingBase target = (EntityLivingBase)entityIn;
         if (!target.isPotionActive(SRPPotions.COTH_E)) {
            if (target.isPotionActive(SRPPotions.EPEL_E)) {
               if (this.rand.nextDouble() >= SRPConfigSystems.epelChanceCOTH) {
                  target.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
               }
            } else {
               target.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
            }
         }
      }

      return flag;
   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      boolean flag = super.attackEntityFrom(source, amount);
      if (flag) {
         ;
      }

      return flag;
   }

   public void fall(float distance, float damageMultiplier) {
      if (distance >= 150.0F) {
         super.fall(distance, damageMultiplier);
      }

   }

   protected void summonFlam(EntityLivingBase in) {
      if (!this.world.isRemote) {
         if (this.ggg == 40) {
            this.getDistanceSq(in);
            this.checkID();
            if (this.getActualParasites() < this.getTotalParasites()) {
               this.resetIdleTime();
               EntityFlam wa = new EntityFlam(this.world);
               float f19 = MathHelper.sin(this.rotationYaw * 0.017453292F - this.randomYawVelocity * 0.01F);
               float f14 = 0.17453292F;
               float f16 = MathHelper.cos(f14);
               float f4 = MathHelper.cos(this.rotationYaw * 0.017453292F - this.randomYawVelocity * 0.01F);
               wa.setPositionAndUpdate(this.posX + -1.0D * (double)(f19 * 4.0F * f16), this.posY + (double)this.getEyeHeight(), this.posZ - -1.0D * (double)(f4 * 4.0F * f16));
               this.setActualParasites(1);
               this.addID(wa.getEntityId(), 1);
               this.world.spawnEntity(wa);
               wa.particleStatus((byte)8);
               wa.particleStatus((byte)8);
               wa.particleStatus((byte)8);
               wa.particleStatus((byte)8);
               wa.setAttackTarget(this.getAttackTarget());
               byte typeF = (byte)(this.rand.nextInt(3) + 1);
               if (typeF == 3) {
                  if (this.getAttackTarget().getDistanceSq(this) >= 100.0D && this.getAttackTarget().onGround && !this.canTeleportToo) {
                     this.canTeleportToo = true;
                  } else {
                     typeF = (byte)(this.rand.nextInt(2) + 1);
                  }
               }

               wa.setFatherTo(this, typeF);
               this.ggg -= 100;
            }

         }
      }
   }

   public void setTeleFlam(boolean in) {
      this.canTeleportToo = in;
   }

   public boolean getTeleFlam() {
      return this.canTeleportToo;
   }

   protected void spawnGore() {
      int range = 4;
      double i1 = (double)MathHelper.floor(this.posY + 0.1D);
      double l1 = this.posX;
      double i2 = this.posZ;

      for(int k2 = -1 * range; k2 <= 1 * range && SRPConfig.paraGore; ++k2) {
         for(int l2 = -1 * range; l2 <= 1 * range; ++l2) {
            double i3 = l1 + (double)k2;
            double l = i2 + (double)l2;
            BlockPos blockpos = new BlockPos(i3, i1, l);
            Block block = this.world.getBlockState(blockpos).getBlock();
            Block blockDown = this.world.getBlockState(blockpos.down()).getBlock();
            if (block == Blocks.AIR && blockDown != Blocks.AIR && this.world.getBlockState(blockpos.down()).isFullBlock() && blockDown != SRPBlocks.InfestedStain && this.world.rand.nextInt(4) == 0) {
               this.world.setBlockState(blockpos, SRPBlocks.Gore.getDefaultState().withProperty(BlockGore.VARIANT, BlockGore.EnumType.PURE_FLAT));
            }
         }
      }

      if (this.world.getBlockState(this.getPosition().down()).isFullBlock() && (this.world.getBlockState(this.getPosition()).getBlock() instanceof BlockBush || this.world.getBlockState(this.getPosition()).getBlock() == Blocks.AIR)) {
         this.world.setBlockState(this.getPosition(), SRPBlocks.Gore.getDefaultState().withProperty(BlockGore.VARIANT, BlockGore.EnumType.PURE_BIG));
         EntityRemain nnn = new EntityRemain(this.world);
         nnn.setLocationAndAngles((double)this.getPosition().getX() + 0.5D, (double)this.getPosition().getY(), (double)this.getPosition().getZ() + 0.5D, 0.0F, 0.0F);
         nnn.setParasite(EntityList.getKey(this).toString());
         nnn.setSkin((byte)this.getSkin());
         nnn.setGoal(20 * SRPConfig.pureRemainValue);
         this.world.spawnEntity(nnn);
      }

      for(int i = 0; i < 7 && SRPConfig.paraGore; ++i) {
         double d0 = (double)((float)this.posX + this.world.rand.nextFloat());
         double d1 = (double)((float)this.posY + this.world.rand.nextFloat());
         double d2 = (double)((float)this.posZ + this.world.rand.nextFloat());
         double d3 = d0 - this.posX;
         double d4 = d1 - this.posY;
         double d5 = d2 - this.posZ;
         double d6 = (double)MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
         d3 = d3 / d6;
         d4 = d4 / d6;
         d5 = d5 / d6;
         double d7 = 0.5D / (d6 / 4.0D + 0.1D);
         d7 = d7 * (double)(this.world.rand.nextFloat() * this.world.rand.nextFloat() + 0.3F);
         d3 = d3 * d7;
         d4 = d4 * d7 * 2.0D;
         d5 = d5 * d7;
         EntityGore bomb = new EntityGore(this.world);
         bomb.setType((byte)4);
         bomb.copyLocationAndAnglesFrom(this);
         bomb.setMotion(d3, d4, d5, 0.25D, 0.65D);
         this.world.spawnEntity(bomb);
      }

   }

   public int getTotalParasites() {
      return this.totalP;
   }

   public int getActualParasites() {
      return this.actualP;
   }

   public void setActualParasites(int i) {
      this.actualP += i;
   }

   public void addID(int id, int points) {
      for(int i = 0; i < this.mobID.length; ++i) {
         if (this.mobID[i] == -777) {
            this.mobID[i] = id;
            this.mobPT[i] = points;
            return;
         }
      }

   }

   public int IDable() {
      int flag = 0;

      for(int i = 0; i < this.mobID.length; ++i) {
         if (this.mobID[i] == -777) {
            ++flag;
         }
      }

      if (flag > this.totalP) {
         flag = this.totalP;
      }

      return flag;
   }

   public void checkID() {
      for(int i = 0; i < this.mobID.length; ++i) {
         if (this.mobID[i] > 0) {
            Entity flag = this.world.getEntityByID(this.mobID[i]);
            if (flag == null) {
               this.mobID[i] = -777;
               int negative = this.mobPT[i] * -1;
               this.setActualParasites(negative);
            }
         }
      }

   }

   public int[] getIDList() {
      return this.mobID;
   }

   public int[] getPointList() {
      return this.mobPT;
   }

   @SideOnly(Side.CLIENT)
   public void spawnEffectsGore() {
   }

   public boolean scaryOrbEffect(EntityLivingBase in, int mobs) {
      boolean flag = super.scaryOrbEffect(in, mobs);
      if (flag && in instanceof EntityPlayer) {
         EntityPlayer playerIn = (EntityPlayer)in;
         if (playerIn.experienceLevel > 0) {
            playerIn.addExperience(-SRPConfig.preeminentExpSteal);
            if (playerIn.experience < 0.0F) {
               --playerIn.experienceLevel;
               playerIn.experience = 1.0F;
            }
         } else {
            playerIn.addExperience(-SRPConfig.preeminentExpSteal);
            if (playerIn.experience < 0.0F) {
               playerIn.experience = 0.0F;
            }
         }
      }

      return flag;
   }
}
