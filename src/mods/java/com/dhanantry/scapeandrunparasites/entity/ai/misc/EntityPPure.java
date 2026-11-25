package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import com.dhanantry.scapeandrunparasites.block.BlockGore;
import com.dhanantry.scapeandrunparasites.entity.EntityRemain;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityDodT;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityNak;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityGore;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.google.common.base.Predicate;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class EntityPPure extends EntityPMalleable implements EntityCanSummon {
   protected int totalP;
   protected int actualP;
   protected int[] mobID;
   protected int[] mobPT;
   private int ggg;

   public EntityPPure(World worldIn) {
      super(worldIn);
      this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus(this, EntityPlayer.class, 0, SRPConfig.pureWalls, false, (Predicate)null, SRPConfig.pureSneakPen, SRPConfig.pureInviPen));
      if (SRPConfig.canOrbAttack) {
         this.tasks.addTask(2, new EntityAISkill(this, 80, 5, false, 21));
      }

      if (SRPConfig.mobattacking) {
         this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus(this, EntityLiving.class, 0, SRPConfig.pureWalls, false, new Predicate<EntityLiving>() {
            public boolean apply(@Nullable EntityLiving entity) {
               return !(entity instanceof EntityWaterMob) && !(entity instanceof EntityAnimal) && !(entity instanceof EntityVillager) && !ParasiteEventEntity.checkEntity(entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
            }
         }, SRPConfig.pureSneakPen, SRPConfig.pureInviPen));
      }

      this.tasks.removeTask(this.folow);
      this.experienceValue = SRPAttributes.XP_ADAPTED;
      this.canD = SRPConfig.puredespawn;
      this.damageCap = SRPConfig.pureCap;
      this.canModRender = 1;
      this.fuseTime = 70;
      this.type = 51;
      this.killcount = 10.0D;
      this.fuseOrb = 20;
      this.orbStartTimer = 5;
      this.foodSteal = SRPConfig.pureFoodSteal;
      this.orbItemCool = SRPConfig.pureItemOrbCooldown * 20;
      this.pointCap = SRPConfig.purePointCap;
      this.pointReduction = SRPConfig.purePointRed;
      this.chanceLearn = SRPConfig.pureChanceLe;
      this.chanceLearnFire = SRPConfig.pureChanceLeFire;
      this.DamageTypeCap = SRPConfig.purePointDamCap;
      this.MiniDamage = SRPConfig.pureMinDamage;
      this.regen = SRPConfig.pureRegen * SRPConfig.globalHealthMultiplier;
      this.oneMindDeathValue = SRPConfig.pureOneMindDeathV;
      this.regenEff = 3;
      this.foodRott = SRPConfig.pureFoodChance;
      this.foodRootNumber = SRPConfig.pureFoodAmount;
      this.totalP = SRPConfig.pureSeiZ;
      this.mobID = new int[this.totalP];
      this.mobPT = new int[this.totalP];

      for(int i = 0; i < this.mobID.length; ++i) {
         this.mobID[i] = -777;
      }

      this.valueEvDeath = SRPConfig.pureLoosingEPValue;
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      ++this.ggg;
      if (this.ggg > 60) {
         this.ggg = 0;
      }

      if (this.getAttackTarget() != null) {
         this.summonTentacles(this.getAttackTarget());
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
      if (flag && source.getTrueSource() instanceof EntityLivingBase) {
         this.summonTentacles((EntityLivingBase)source.getTrueSource());
      }

      return flag;
   }

   public void fall(float distance, float damageMultiplier) {
      if (distance >= 80.0F) {
         super.fall(distance, damageMultiplier);
      }

   }

   protected void summonTentacles(EntityLivingBase in) {
      if (this.ggg == 20) {
         double dis = this.getDistanceSq(in);
         boolean flag = !this.canEntityBeSeen(in);
         if ((dis <= 64.0D || !flag || this.rand.nextInt(3) != 0) && this.rand.nextInt(10) != 0) {
            if (in.isPotionActive(MobEffects.SLOWNESS) && in.getActivePotionEffect(MobEffects.SLOWNESS).getAmplifier() == 2) {
               return;
            }

            this.checkID();
            if (this.getActualParasites() < this.getTotalParasites() && this.spawnT(in, 3, 1, 16, 1)) {
               ;
            }
         } else {
            this.checkID();
            if (this.getActualParasites() < this.getTotalParasites() && this.spawnT(in, 5, 3, 2, 2)) {
               this.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 150, 120, false, false));
            }
         }

      }
   }

   protected boolean spawnT(EntityLivingBase in, int range2, int mini2, int check, int type) {
      int range = range2;
      int mini = mini2;
      Random rand = new Random();
      double x = in.posX;
      double y = in.posY;
      double z = in.posZ;
      double randomx = (double)(rand.nextInt(range2) + mini2);
      double randomz = (double)(rand.nextInt(range2) + mini2);
      double negative = (double)rand.nextInt(2);
      if (negative == 0.0D) {
         randomx *= -1.0D;
      }

      negative = (double)rand.nextInt(2);
      if (negative == 0.0D) {
         randomz *= -1.0D;
      }

      int limit = 0;
      boolean flag = true;
      EntityPStationary entityout = new EntityDodT(this.world);
      ((EntityDodT)entityout).setTele(this);
      if (type == 1) {
         entityout = new EntityNak(this.world);
      }

      while(flag) {
         if (limit >= 5) {
            entityout.setDead();
            return false;
         }

         BlockPos pos = new BlockPos(x + randomx, y, z + randomz);
         pos = ParasiteEventEntity.getFloor(this.world, pos, 5);
         if (pos != null) {
            this.world.getBlockState(pos);
            if (this.world.getBlockState(pos.down()).isFullCube()) {
               AxisAlignedBB axisalignedbb = (new AxisAlignedBB((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1))).grow((double)check);
               List<EntityLivingBase> moblist = this.world.getEntitiesWithinAABB(EntityNak.class, axisalignedbb);
               if (moblist.size() > 10) {
                  return false;
               }

               axisalignedbb = (new AxisAlignedBB((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1))).grow(2.0D);
               moblist = this.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);
               if (moblist.isEmpty()) {
                  entityout.setLocationAndAngles(x + randomx, y, z + randomz, this.rotationYaw, this.rotationPitch);
                  if (this.world.getCollisionBoxes(entityout, entityout.getEntityBoundingBox()).isEmpty()) {
                     entityout.onInitialSpawn(this.world.getDifficultyForLocation(new BlockPos(entityout)), (IEntityLivingData)null);
                     entityout.setAttackTarget(in);
                     this.setActualParasites(1);
                     this.addID(entityout.getEntityId(), 1);
                     entityout.setBuried();
                     this.world.spawnEntity(entityout);
                     this.world.setEntityState(entityout, (byte)50);
                     flag = false;
                     return true;
                  }

                  randomx = (double)(rand.nextInt(range) + mini);
                  randomz = (double)(rand.nextInt(range) + mini);
                  negative = (double)rand.nextInt(2);
                  if (negative == 0.0D) {
                     randomx *= -1.0D;
                  }

                  negative = (double)rand.nextInt(2);
                  if (negative == 0.0D) {
                     randomz *= -1.0D;
                  }

                  ++limit;
                  continue;
               }
            }
         }

         randomx = (double)(rand.nextInt(range) + mini);
         randomz = (double)(rand.nextInt(range) + mini);
         negative = (double)rand.nextInt(2);
         if (negative == 0.0D) {
            randomx *= -1.0D;
         }

         negative = (double)rand.nextInt(2);
         if (negative == 0.0D) {
            randomz *= -1.0D;
         }

         ++limit;
      }

      return false;
   }

   private EntityDodT getTentacle() {
      for(int i = 0; i < this.mobID.length; ++i) {
         if (this.mobID[i] > 0 && this.world.getEntityByID(this.mobID[i]) instanceof EntityDodT) {
            EntityDodT flag = (EntityDodT)this.world.getEntityByID(this.mobID[i]);
            if (flag == null) {
               this.mobID[i] = -777;
               int negative = this.mobPT[i] * -1;
               this.setActualParasites(negative);
            } else if (flag.isEntityAlive() && flag.getTele() == null && !flag.buried()) {
               return flag;
            }
         }
      }

      return null;
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
            playerIn.addExperience(-SRPConfig.pureExpSteal);
            if (playerIn.experience < 0.0F) {
               --playerIn.experienceLevel;
               playerIn.experience = 1.0F;
            }
         } else {
            playerIn.addExperience(-SRPConfig.pureExpSteal);
            if (playerIn.experience < 0.0F) {
               playerIn.experience = 0.0F;
            }
         }
      }

      return flag;
   }
}
