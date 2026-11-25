package com.dhanantry.scapeandrunparasites.entity;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationary;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityBiomass;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class EntityParasiticScent extends Entity {
   private byte scentState;
   private int lifeTicks;
   private int currentL;
   private int dangerToUs;
   private byte active;
   private int delay;
   private int timerTick;
   private byte scentLevel;
   private byte scentReaction;
   private int minwave;
   private int maxwave;
   private int minmob;
   private int maxmob;
   private boolean followTargetScent;
   private byte loopLife;
   private byte failing;
   private EntityLivingBase targetScent;

   public EntityParasiticScent(World worldIn) {
      super(worldIn);
      this.lifeTicks = 200;
      this.followTargetScent = false;
      this.loopLife = 103;
   }

   public EntityParasiticScent(World worldIn, int status) {
      this(worldIn);
      this.scentState = (byte)status;
   }

   public EntityParasiticScent(World worldIn, int status, EntityLivingBase tar) {
      this(worldIn, status);
      this.targetScent = tar;
   }

   protected void entityInit() {
   }

   public SoundCategory getSoundCategory() {
      return SoundCategory.NEUTRAL;
   }

   public void onUpdate() {
      super.onUpdate();
      if (!this.world.isRemote) {
         ++this.currentL;
         if (this.currentL <= this.lifeTicks && this.world.getDifficulty() != EnumDifficulty.PEACEFUL && this.loopLife >= 0) {
            if (this.targetScent == null) {
               if (this.scentState > 1) {
                  this.scentState = 1;
                  return;
               }
            } else if (!this.targetScent.isEntityAlive() || this.targetScent.getDistanceSq(this) > 4096.0D) {
               this.targetScent = null;
               this.scentState = 1;
               return;
            }

            if (this.delay > 0) {
               --this.delay;
            } else {
               this.scentFollower();
               if (this.targetScent instanceof EntityPlayer) {
                  EntityPlayer pa = (EntityPlayer)this.targetScent;
                  if (pa.isSpectator() || pa.isCreative()) {
                     this.targetScent = null;
                     return;
                  }
               }

               switch(this.scentState) {
               case 0:
                  if (this.rand.nextInt(3) == 0) {
                     this.scentObserver();
                  }

                  this.scentListener();
                  break;
               case 1:
                  this.scentObserver();
                  this.scentListener();
               case 2:
               case 3:
               default:
                  break;
               case 4:
                  this.scentTactical();
                  break;
               case 5:
                  this.scentAttacker();
                  break;
               case 6:
                  this.scentBuilder();
                  break;
               case 7:
                  this.scentAggressive();
               }

            }
         } else {
            this.setDead();
         }
      }
   }

   private void scentListener() {
      if (this.active >= this.scentReaction && this.targetScent != null) {
         this.scentState = 4;
         this.active = 2;
         this.warnPlayers("Scent is active");
         this.lifeTicks = 20 * SRPConfigSystems.scentLifeTactical;
         this.currentL = 0;
      }
   }

   private void scentObserver() {
      if (this.ticksExisted % 20 == 0) {
         if (!this.followTargetScent) {
            if (this.targetScent != null) {
               ++this.active;
               AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D)).grow(64.0D);

               for(EntityParasiteBase mob : this.world.getEntitiesWithinAABB(EntityParasiteBase.class, axisalignedbb)) {
                  if (mob.isEntityAlive()) {
                     if (mob.getAttackTarget() == null) {
                        double ra = mob.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue();
                        if (mob.getDistanceSq(this.targetScent) < ra * ra) {
                           mob.setAttackTarget(this.targetScent);
                        }
                     } else if (!mob.getAttackTarget().isEntityAlive()) {
                        double ra = mob.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue();
                        if (mob.getDistanceSq(this.targetScent) < ra * ra) {
                           mob.setAttackTarget(this.targetScent);
                        }
                     }
                  }
               }

            } else {
               AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D)).grow(64.0D);
               List<EntityLivingBase> moblist = this.world.getEntitiesWithinAABB(EntityParasiteBase.class, axisalignedbb);
               int parasites = moblist.size();
               moblist = this.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);
               if (parasites < moblist.size()) {
                  EntityLivingBase target = null;
                  double dis = 40000.0D;
                  Iterator var7 = moblist.iterator();

                  while(true) {
                     EntityLivingBase mob;
                     double atm;
                     while(true) {
                        if (!var7.hasNext()) {
                           this.targetScent = target;
                           return;
                        }

                        mob = (EntityLivingBase)var7.next();
                        if (!(mob instanceof EntityParasiteBase) && !(mob instanceof EntityAnimal) && !(mob instanceof EntityWaterMob) && !(mob instanceof EntityCreeper)) {
                           atm = mob.getDistanceSq(this);
                           if (atm <= 4096.0D) {
                              if (!(mob instanceof EntityPlayer)) {
                                 break;
                              }

                              EntityPlayer qqq = (EntityPlayer)mob;
                              if (!qqq.isCreative() && !qqq.isSpectator()) {
                                 break;
                              }
                           }
                        }
                     }

                     if (atm < dis) {
                        dis = atm;
                        target = mob;
                     }
                  }
               } else {
                  this.currentL += 100;
               }
            }
         }
      }
   }

   private void scentTactical() {
      if (this.active >= SRPConfigSystems.scentSpawnWaves) {
         this.scentState = 5;
         this.active = (byte)(this.active - 5);
      } else {
         if (this.targetScent != null) {
            if (this.targetScent.isEntityAlive()) {
               ++this.active;
            } else {
               this.targetScent = null;
            }
         } else if (this.ticksExisted % 80 == 0) {
            --this.active;
            if (this.active <= 0) {
               this.lifeTicks = 20 * SRPConfigSystems.scentLifeObserver;
               this.currentL = 0;
               this.scentState = 1;
               return;
            }
         }

      }
   }

   private void scentAttacker() {
      if (this.checkNearby() <= 6) {
         this.scentState = 6;
      } else {
         this.delay = 100;
         this.lifeTicks += 100;
         this.scentState = 4;
      }

   }

   private void scentBuilder() {
      this.world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, SRPSounds.SCENTWAVE, this.getSoundCategory(), 10.0F, 1.0F);
      this.world.playSound((EntityPlayer)null, this.targetScent.posX, this.targetScent.posY, this.targetScent.posZ, SRPSounds.SCENTWAVE, this.targetScent.getSoundCategory(), 10.0F, 1.0F);
      --this.loopLife;
      int a = 0;
      int limit = 0;
      int horde = this.maxMinInt(this.minwave, this.maxwave);

      while(limit < 10) {
         a = a + this.checkNearby();
         a = a + this.placeWaves(SRPConfigSystems.scentMiniDis, SRPConfigSystems.scentMaxDis);
         ++limit;
         if (a >= horde) {
            this.delay = 100 + a * 20;
            this.lifeTicks = 20 * SRPConfigSystems.scentLifeTactical;
            this.currentL = 0;
            this.scentState = 4;
            return;
         }
      }

      this.delay = 200;
      if (SRPConfigSystems.oneMindDebug) {
         System.out.println(" aaa " + a + " h " + horde);
      }

      if (a <= 0) {
         this.delay = 100;
      }

   }

   private void scentAggressive() {
      int limit2 = 0;

      for(int enemies = this.maxMinInt(this.minmob, this.maxmob); limit2 < enemies; ++limit2) {
         this.spawnWaves();
      }

      this.setDead();
   }

   private void scentFollower() {
      if (this.followTargetScent) {
         if (this.targetScent != null) {
            if (!this.targetScent.isEntityAlive()) {
               this.targetScent = null;
               this.followTargetScent = false;
               return;
            }

            if (this.targetScent.getDistanceSq(this) > 144.0D && this.targetScent.isPotionActive(SRPPotions.PREY_E)) {
               this.copyLocationAndAnglesFrom(this.targetScent);
            }
         } else {
            this.setDead();
         }
      }

   }

   public boolean getCanFollow() {
      return this.followTargetScent;
   }

   public void setCanFollow(boolean in) {
      this.followTargetScent = in;
   }

   public void setScentState(int in) {
      this.scentState = (byte)in;
   }

   public byte getScentState() {
      return this.scentState;
   }

   public void setScentLife(int in) {
      this.lifeTicks = in;
   }

   public int getScentLife() {
      return this.lifeTicks;
   }

   public void setTargetToKill(EntityLivingBase in) {
      if (!this.followTargetScent) {
         if (in.isEntityAlive()) {
            this.targetScent = in;
         }
      }
   }

   public EntityLivingBase getTargetToKill() {
      if (this.targetScent == null) {
         return null;
      } else if (!this.targetScent.isEntityAlive()) {
         this.targetScent = null;
         return null;
      } else {
         return this.targetScent;
      }
   }

   public void increaseDanger(int in, boolean plus) {
      if (plus) {
         this.dangerToUs += in;
      } else {
         this.dangerToUs = in;
      }

      this.updateScentOLevel();
   }

   private void updateScentOLevel() {
      if (this.dangerToUs >= SRPConfigSystems.scentLevelPointsEight) {
         this.scentLevel = 8;
         this.maxmob = SRPConfigSystems.scentWaveMaxMobWaveEight;
         this.minmob = SRPConfigSystems.scentWaveMinMobWaveEight;
         this.maxwave = SRPConfigSystems.scentWaveMaximumEight;
         this.minwave = SRPConfigSystems.scentWaveMinimumEight;
      } else if (this.dangerToUs >= SRPConfigSystems.scentLevelPointsSeven) {
         this.scentLevel = 7;
         this.maxmob = SRPConfigSystems.scentWaveMaxMobWaveSeven;
         this.minmob = SRPConfigSystems.scentWaveMinMobWaveSeven;
         this.maxwave = SRPConfigSystems.scentWaveMaximumSeven;
         this.minwave = SRPConfigSystems.scentWaveMinimumSeven;
      } else if (this.dangerToUs >= SRPConfigSystems.scentLevelPointsSix) {
         this.scentLevel = 6;
         this.maxmob = SRPConfigSystems.scentWaveMaxMobWaveSix;
         this.minmob = SRPConfigSystems.scentWaveMinMobWaveSix;
         this.maxwave = SRPConfigSystems.scentWaveMaximumSix;
         this.minwave = SRPConfigSystems.scentWaveMinimumSix;
      } else if (this.dangerToUs >= SRPConfigSystems.scentLevelPointsFive) {
         this.scentLevel = 5;
         this.maxmob = SRPConfigSystems.scentWaveMaxMobWaveFive;
         this.minmob = SRPConfigSystems.scentWaveMinMobWaveFive;
         this.maxwave = SRPConfigSystems.scentWaveMaximumFive;
         this.minwave = SRPConfigSystems.scentWaveMinimumFive;
      } else if (this.dangerToUs >= SRPConfigSystems.scentLevelPointsFour) {
         this.scentLevel = 4;
         this.maxmob = SRPConfigSystems.scentWaveMaxMobWaveFour;
         this.minmob = SRPConfigSystems.scentWaveMinMobWaveFour;
         this.maxwave = SRPConfigSystems.scentWaveMaximumFour;
         this.minwave = SRPConfigSystems.scentWaveMinimumFour;
      } else if (this.dangerToUs >= SRPConfigSystems.scentLevelPointsThree) {
         this.scentLevel = 3;
         this.maxmob = SRPConfigSystems.scentWaveMaxMobWaveThree;
         this.minmob = SRPConfigSystems.scentWaveMinMobWaveThree;
         this.maxwave = SRPConfigSystems.scentWaveMaximumThree;
         this.minwave = SRPConfigSystems.scentWaveMinimumThree;
      } else if (this.dangerToUs >= SRPConfigSystems.scentLevelPointsTwo) {
         this.scentLevel = 2;
         this.maxmob = SRPConfigSystems.scentWaveMaxMobWaveTwo;
         this.minmob = SRPConfigSystems.scentWaveMinMobWaveTwo;
         this.maxwave = SRPConfigSystems.scentWaveMaximumTwo;
         this.minwave = SRPConfigSystems.scentWaveMinimumTwo;
      } else if (this.dangerToUs >= SRPConfigSystems.scentLevelPointsOne) {
         this.scentLevel = 1;
         this.maxmob = SRPConfigSystems.scentWaveMaxMobWaveOne;
         this.minmob = SRPConfigSystems.scentWaveMinMobWaveOne;
         this.maxwave = SRPConfigSystems.scentWaveMaximumOne;
         this.minwave = SRPConfigSystems.scentWaveMinimumOne;
      } else {
         this.maxmob = SRPConfigSystems.scentWaveMaxMobWaveZero;
         this.minmob = SRPConfigSystems.scentWaveMinMobWaveZero;
         this.maxwave = SRPConfigSystems.scentWaveMaximumZero;
         this.minwave = SRPConfigSystems.scentWaveMinimumZero;
      }

   }

   public int getDanger() {
      return this.dangerToUs;
   }

   public void increaseActivity(int in, boolean plus) {
      if (in <= 100) {
         if (plus) {
            this.active += (byte)in;
         } else {
            this.active = (byte)in;
         }

      }
   }

   private int checkNearby() {
      int i = 0;
      AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D)).grow(64.0D);
      List<EntityParasiteBase> moblist = this.world.getEntitiesWithinAABB(EntityParasiteBase.class, axisalignedbb);

      for(EntityParasiteBase mob : moblist) {
         if (mob.getAttackTarget() == null && mob.isEntityAlive() && this.world.getLightFor(EnumSkyBlock.BLOCK, this.getPosition()) < 5 && !(mob instanceof EntityPStationary) && !(mob instanceof EntityBiomass) && mob.hurtTime <= 0 && this.moveMobToLoc(mob)) {
            mob.setAttackTarget(this.targetScent);
            ++i;
         }
      }

      if (moblist.size() > 20) {
         return 20;
      } else {
         return i;
      }
   }

   private boolean moveMobToLoc(EntityParasiteBase in) {
      int minDist = SRPConfigSystems.scentMiniDis;
      int maxDist = SRPConfigSystems.scentMaxDis;
      int loop = 0;
      if (loop < 7) {
         ++loop;
         int range = maxDist * 2;
         int tryX = (int)Math.floor(this.targetScent.posX - (double)(range / 2) + (double)this.rand.nextInt(range));
         int tryY = (int)this.targetScent.posY;
         int tryZ = (int)Math.floor(this.targetScent.posZ - (double)(range / 2) + (double)this.rand.nextInt(range));
         BlockPos poss = new BlockPos(tryX, tryY, tryZ);
         poss = ParasiteEventEntity.getFloor(this.world, poss, 10);
         if (poss == null) {
            return false;
         } else if (this.world.getLightFor(EnumSkyBlock.BLOCK, poss) > 4) {
            return false;
         } else {
            AxisAlignedBB axisalignedbb = (new AxisAlignedBB((double)tryX, (double)tryY, (double)tryZ, (double)(tryX + 1), (double)(tryY + 1), (double)(tryZ + 1))).grow((double)minDist, 5.0D, (double)minDist);

            for(EntityLivingBase mob : this.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb)) {
               if (!(mob instanceof EntityParasiteBase)) {
                  return false;
               }
            }

            if (this.getDistance((double)poss.getX(), (double)poss.getY(), (double)poss.getZ()) >= (double)minDist && this.getDistance((double)poss.getX(), (double)poss.getY(), (double)poss.getZ()) <= (double)maxDist) {
               in.setPosition((double)poss.getX() + 0.5D, (double)poss.getY(), (double)poss.getZ() + 0.5D);
               return true;
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   public int placeWaves(int minDist, int maxDist) {
      if (this.targetScent == null) {
         return 0;
      } else {
         int range = maxDist * 2;
         int tryX = (int)Math.floor(this.targetScent.posX - (double)(range / 2) + (double)this.rand.nextInt(range));
         int tryY = (int)this.targetScent.posY;
         int tryZ = (int)Math.floor(this.targetScent.posZ - (double)(range / 2) + (double)this.rand.nextInt(range));
         BlockPos poss = new BlockPos(tryX, tryY, tryZ);
         poss = ParasiteEventEntity.getFloor(this.world, poss, 10);
         if (poss == null) {
            if (SRPConfigSystems.oneMindDebug) {
               System.out.println("wave floor null ");
            }

            return 0;
         } else if (this.getDistance((double)poss.getX(), (double)poss.getY(), (double)poss.getZ()) >= (double)minDist && this.getDistance((double)poss.getX(), (double)poss.getY(), (double)poss.getZ()) <= (double)SRPConfigSystems.oneMinRangeCap) {
            AxisAlignedBB axisalignedbb = (new AxisAlignedBB((double)poss.getX(), (double)poss.getY(), (double)poss.getZ(), (double)(poss.getX() + 1), (double)(poss.getY() + 1), (double)(poss.getZ() + 1))).grow((double)maxDist, 16.0D, (double)maxDist);
            List<EntityLivingBase> moblist = this.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);
            int living = moblist.size();
            moblist = this.world.getEntitiesWithinAABB(EntityParasiteBase.class, axisalignedbb);
            if (moblist.size() > SRPConfigSystems.scentMobLimit) {
               if (SRPConfigSystems.oneMindDebug) {
                  System.out.println("mob limit");
               }

               return -1;
            } else if (living == moblist.size()) {
               return 0;
            } else {
               axisalignedbb = (new AxisAlignedBB((double)poss.getX(), (double)poss.getY(), (double)poss.getZ(), (double)(poss.getX() + 1), (double)(poss.getY() + 1), (double)(poss.getZ() + 1))).grow((double)minDist, 5.0D, (double)minDist);

               for(EntityLivingBase mob : this.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb)) {
                  if (!(mob instanceof EntityParasiteBase)) {
                     if (SRPConfigSystems.oneMindDebug) {
                        System.out.println("too close to nonparasite mob");
                     }

                     return -1;
                  }
               }

               EntityParasiticScent entityliving = new EntityParasiticScent(this.world, 7);
               entityliving.increaseDanger(this.dangerToUs, false);
               entityliving.setPosition((double)poss.getX() + 0.5D, (double)poss.getY(), (double)poss.getZ() + 0.5D);
               if (this.world.getLightFor(EnumSkyBlock.BLOCK, poss) > 4) {
                  if (SRPConfigSystems.oneMindDebug) {
                     System.out.println("light block");
                  }

                  return 0;
               } else {
                  this.world.spawnEntity(entityliving);
                  entityliving.setTargetToKill(this.targetScent);
                  return 1;
               }
            }
         } else {
            if (SRPConfigSystems.oneMindDebug) {
               System.out.println("distance null " + this.getDistance((double)poss.getX(), (double)poss.getY(), (double)poss.getZ()));
            }

            return 0;
         }
      }
   }

   public int spawnWaves() {
      if (this.targetScent == null) {
         return 0;
      } else {
         String mob = this.getMob();
         EntityLiving entityliving = (EntityLiving)EntityList.createEntityByIDFromName(new ResourceLocation(mob), this.world);
         if (entityliving == null) {
            return 0;
         } else {
            entityliving.setPosition(this.posX, this.posY, this.posZ);
            entityliving.onInitialSpawn(this.world.getDifficultyForLocation(new BlockPos(entityliving)), (IEntityLivingData)null);
            this.world.spawnEntity(entityliving);
            if (SRPPotions.applySense(entityliving, 200, entityliving.getDistanceSq(this.targetScent), SRPConfigSystems.oneMinRangeCap)) {
               entityliving.setAttackTarget(this.targetScent);
               entityliving.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 200, 0, false, false));
            } else {
               entityliving.setDead();
            }

            return 1;
         }
      }
   }

   private byte getareaValue() {
      byte i = this.scentLevel;
      AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D)).grow(64.0D);

      for(EntityParasiticScent mob : this.world.getEntitiesWithinAABB(EntityParasiticScent.class, axisalignedbb)) {
         if (mob.getScentLevel() > i) {
            i = mob.getScentLevel();
         }
      }

      return i;
   }

   private String getMob() {
      String mob = "minecraft:zombie";
      byte area = this.getareaValue();
      switch(area) {
      case 0:
         mob = SRPConfigSystems.scentLevelZero[this.rand.nextInt(SRPConfigSystems.scentLevelZero.length)];
         break;
      case 1:
         mob = SRPConfigSystems.scentLevelOne[this.rand.nextInt(SRPConfigSystems.scentLevelOne.length)];
         break;
      case 2:
         mob = SRPConfigSystems.scentLevelTwo[this.rand.nextInt(SRPConfigSystems.scentLevelTwo.length)];
         break;
      case 3:
         mob = SRPConfigSystems.scentLevelThree[this.rand.nextInt(SRPConfigSystems.scentLevelThree.length)];
         break;
      case 4:
         mob = SRPConfigSystems.scentLevelFour[this.rand.nextInt(SRPConfigSystems.scentLevelFour.length)];
         break;
      case 5:
         mob = SRPConfigSystems.scentLevelFive[this.rand.nextInt(SRPConfigSystems.scentLevelFive.length)];
         break;
      case 6:
         mob = SRPConfigSystems.scentLevelSix[this.rand.nextInt(SRPConfigSystems.scentLevelSix.length)];
         break;
      case 7:
         mob = SRPConfigSystems.scentLevelSeven[this.rand.nextInt(SRPConfigSystems.scentLevelSeven.length)];
         break;
      case 8:
         mob = SRPConfigSystems.scentLevelEight[this.rand.nextInt(SRPConfigSystems.scentLevelEight.length)];
      }

      return mob;
   }

   public void setScentReaction(byte in, boolean override) {
      if (override) {
         this.scentReaction = in;
      } else if (in > this.scentReaction) {
         this.scentReaction = in;
      }

   }

   private byte getScentLevel() {
      return this.scentLevel;
   }

   public void warnPlayers(String in) {
      AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D)).grow(64.0D);

      for(EntityPlayer mob : this.world.getEntitiesWithinAABB(EntityPlayer.class, axisalignedbb)) {
         mob.sendStatusMessage(new TextComponentString(in), true);
      }

   }

   private int maxMinInt(int min, int max) {
      int atm = max - min + 1;
      return this.rand.nextInt(atm) + min;
   }

   public void applyEntityCollision(Entity entityIn) {
      super.applyEntityCollision(entityIn);
   }

   public void onCollideWithPlayer(EntityPlayer entityIn) {
      if (SRPConfigSystems.oneMindDebug) {
         if (this.ticksExisted % 20 == 0 && !entityIn.world.isRemote && entityIn instanceof EntityPlayer && SRPConfigSystems.oneMindDebug) {
            System.out.println("\n id " + this.getEntityId() + "\n scent state " + this.scentState + "\n active " + this.active + "\n danger " + this.dangerToUs + "\n delay " + this.delay + "\n total life " + this.lifeTicks + "\n current life " + this.currentL + "\n targetScent is null " + (this.targetScent == null) + "\n level " + this.scentLevel + "\n targetScent name " + (this.targetScent != null ? this.targetScent.getClass().toString() : " b ") + "\n min max mob " + this.minmob + " " + this.maxmob + "\n loop " + this.loopLife + "\n fail " + this.failing);
         }

         super.onCollideWithPlayer(entityIn);
      }
   }

   protected void readEntityFromNBT(NBTTagCompound compound) {
      if (compound.hasKey("parasitescenttype", 99)) {
         this.scentState = compound.getByte("parasitescenttype");
      }

      if (compound.hasKey("parasitescentactive", 99)) {
         this.active = compound.getByte("parasitescentactive");
      }

      if (compound.hasKey("parasitescentlevel", 99)) {
         this.scentLevel = compound.getByte("parasitescentlevel");
      }

      if (compound.hasKey("parasitelifespan", 99)) {
         this.lifeTicks = compound.getInteger("parasitelifespan");
      }

      if (compound.hasKey("parasitelifecurrent", 99)) {
         this.currentL = compound.getInteger("parasitelifecurrent");
      }

      if (compound.hasKey("parasitedangerous", 99)) {
         this.dangerToUs = compound.getInteger("parasitedangerous");
      }

      if (compound.hasKey("parasitedelay", 99)) {
         this.delay = compound.getInteger("parasitedelay");
      }

      if (compound.hasKey("parasitescentreaction", 99)) {
         this.scentReaction = compound.getByte("parasitescentreaction");
      }

      if (compound.hasKey("parasitescentloopf", 99)) {
         this.loopLife = compound.getByte("parasitescentloopf");
      }

      if (compound.hasKey("parasitescentfailing", 99)) {
         this.failing = compound.getByte("parasitescentfailing");
      }

   }

   protected void writeEntityToNBT(NBTTagCompound compound) {
      compound.setByte("parasitescenttype", this.scentState);
      compound.setByte("parasitescentactive", this.active);
      compound.setByte("parasitescentlevel", this.scentLevel);
      compound.setInteger("parasitelifespan", this.lifeTicks);
      compound.setInteger("parasitelifecurrent", this.currentL);
      compound.setInteger("parasitedangerous", this.dangerToUs);
      compound.setInteger("parasitedelay", this.delay);
      compound.setByte("parasitescentreaction", this.scentReaction);
      compound.setByte("parasitescentloopf", this.loopLife);
      compound.setByte("parasitescentfailing", this.failing);
   }
}
