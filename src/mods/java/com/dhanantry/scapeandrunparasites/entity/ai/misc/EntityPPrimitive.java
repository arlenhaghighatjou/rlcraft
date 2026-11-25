package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import com.dhanantry.scapeandrunparasites.block.BlockGore;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.EntityRemain;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class EntityPPrimitive extends EntityPMalleable {
   public EntityPPrimitive(World worldIn) {
      super(worldIn);
      this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus(this, EntityPlayer.class, 0, SRPConfig.primitiveWalls, false, (Predicate)null, SRPConfig.primitiveSneakPen, SRPConfig.primitiveInviPen));
      if (SRPConfig.canOrbAttack) {
         this.tasks.addTask(2, new EntityAISkill(this, 100, 4, false, 21));
      }

      if (SRPConfig.mobattacking) {
         this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus(this, EntityLiving.class, 0, SRPConfig.primitiveWalls, false, new Predicate<EntityLiving>() {
            public boolean apply(@Nullable EntityLiving entity) {
               return !(entity instanceof EntityWaterMob) && !(entity instanceof EntityAnimal) && !(entity instanceof EntityVillager) && !ParasiteEventEntity.checkEntity(entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
            }
         }, SRPConfig.primitiveSneakPen, SRPConfig.primitiveInviPen));
      }

      this.experienceValue = SRPAttributes.XP_PRIMITIVE;
      this.damageCap = SRPConfig.primitiveCap;
      this.canD = SRPConfig.primitivedespawn;
      this.canModRender = 1;
      this.type = 31;
      this.fuseOrb = 8;
      this.orbStartTimer = 30;
      this.foodSteal = SRPConfig.primitiveFoodSteal;
      this.orbItemCool = SRPConfig.primitiveItemOrbCooldown * 20;
      this.pointCap = SRPConfig.primitivePointCap;
      this.pointReduction = SRPConfig.primitivePointRed;
      this.chanceLearn = SRPConfig.primitiveChanceLe;
      this.chanceLearnFire = SRPConfig.primitiveChanceLeFire;
      this.DamageTypeCap = SRPConfig.primitivePointDamCap;
      this.MiniDamage = SRPConfig.primitiveMinDamage;
      this.regen = SRPConfig.primitiveRegen * SRPConfig.globalHealthMultiplier;
      this.oneMindDeathValue = SRPConfig.primitiveOneMindDeathV;
      this.regenEff = 1;
      this.foodRott = SRPConfig.primitiveFoodChance;
      this.foodRootNumber = SRPConfig.primitiveFoodAmount;
      this.valueEvDeath = SRPConfig.primitiveLoosingEPValue;
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
            player.addPotionEffect(new PotionEffect(SRPPotions.FEAR_E, 300, 1, false, false));
         } else if (player.getActivePotionEffect(SRPPotions.FEAR_E).getAmplifier() < 1) {
            player.addPotionEffect(new PotionEffect(SRPPotions.FEAR_E, 300, 1, false, false));
         }
      } catch (Exception var3) {
         ;
      }

   }

   public boolean attackEntityAsMob(Entity entityIn) {
      boolean flag = super.attackEntityAsMob(entityIn);
      if (flag && entityIn instanceof EntityLivingBase && this.rand.nextDouble() < (double)SRPConfigSystems.cothPrimitive) {
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

   public void fall(float distance, float damageMultiplier) {
      if (distance >= 40.0F) {
         super.fall(distance, damageMultiplier);
      }

   }

   protected void spawnGore() {
      int range = 2;
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
            if (block == Blocks.AIR && blockDown != Blocks.AIR && this.world.getBlockState(blockpos.down()).isFullBlock() && blockDown != SRPBlocks.InfestedStain && this.world.rand.nextInt(3) == 0) {
               this.world.setBlockState(blockpos, SRPBlocks.Gore.getDefaultState().withProperty(BlockGore.VARIANT, BlockGore.EnumType.PRI_FLAT));
            }
         }
      }

      if (this.world.getBlockState(this.getPosition().down()).isFullBlock() && (this.world.getBlockState(this.getPosition()).getBlock() instanceof BlockBush || this.world.getBlockState(this.getPosition()).getBlock() == Blocks.AIR)) {
         this.world.setBlockState(this.getPosition(), SRPBlocks.Gore.getDefaultState().withProperty(BlockGore.VARIANT, BlockGore.EnumType.PRI_BIG));
         EntityRemain nnn = new EntityRemain(this.world);
         nnn.setLocationAndAngles((double)this.getPosition().getX() + 0.5D, (double)this.getPosition().getY(), (double)this.getPosition().getZ() + 0.5D, 0.0F, 0.0F);
         nnn.setParasite(EntityList.getKey(this).toString());
         nnn.setSkin((byte)this.getSkin());
         nnn.setGoal(20 * SRPConfig.primitiveRemainValue);
         this.world.spawnEntity(nnn);
      }

      for(int i = 0; i < 4 && SRPConfig.paraGore; ++i) {
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
         bomb.setType((byte)2);
         bomb.copyLocationAndAnglesFrom(this);
         bomb.setMotion(d3, d4, d5, 0.15D, 0.55D);
         this.world.spawnEntity(bomb);
      }

   }

   @SideOnly(Side.CLIENT)
   public void spawnEffectsGore() {
      for(int i = 0; i <= 60; ++i) {
         if (i % 4 == 0) {
            this.spawnParticles(SRPEnumParticle.GCLOUD, 150, 0, 0);
         }

         if (i % 5 == 0) {
            this.spawnParticles(SRPEnumParticle.GSPLASH, 2, -1, -1);
         }
      }

   }

   public boolean scaryOrbEffect(EntityLivingBase in, int mobs) {
      boolean flag = super.scaryOrbEffect(in, mobs);
      if (flag) {
         ;
      }

      return flag;
   }
}
