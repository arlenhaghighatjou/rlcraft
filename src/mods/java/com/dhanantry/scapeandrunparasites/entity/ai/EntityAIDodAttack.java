package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPDispatcher;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPreeminent;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationary;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityDodT;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityNak;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityUnvo;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import java.util.ArrayList;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class EntityAIDodAttack extends EntityAIBase {
   private final EntityPDispatcher parent;
   private byte stage;
   private double tickss;
   private byte maxID;
   private int maxDodT;
   private ArrayList<Integer> resistanceI = new ArrayList();
   float maxHardness;

   public EntityAIDodAttack(EntityPDispatcher dod, int CURRENTstage, byte limit, float hardness) {
      this.parent = dod;
      this.tickss = 20.0D;
      this.stage = (byte)CURRENTstage;
      this.maxID = limit;
      this.maxDodT = CURRENTstage * 3;
      this.maxHardness = hardness;
   }

   public boolean shouldExecute() {
      --this.tickss;
      if (this.tickss < 0.0D) {
         this.tickss = 80.0D;
         if (this.parent.getAttackTarget() != null) {
            this.tickss = 10.0D;
         }

         return true;
      } else {
         return false;
      }
   }

   public void resetTask() {
   }

   public void updateTask() {
      double follow = 0.0D;
      AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.parent.posX, this.parent.posY, this.parent.posZ, this.parent.posX + 1.0D, this.parent.posY + 1.0D, this.parent.posZ + 1.0D)).grow(this.parent.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue());
      EntityLivingBase target = this.parent.getAttackTarget();
      this.unhide(axisalignedbb);
      this.storePara(axisalignedbb);
      if (target == null) {
         this.findT(follow, axisalignedbb);
      } else {
         if (!this.parent.getMobList().isEmpty() && this.parent.world.rand.nextInt(3) != 0) {
            int sel = this.parent.world.rand.nextInt(this.stage * 2);
            if (sel < this.parent.getMobList().size()) {
               for(int i = 0; i <= sel; ++i) {
                  this.summonTentacles(target, follow, axisalignedbb, true);
               }
            }
         } else {
            switch(this.stage) {
            case 1:
               this.parent.checkID();
               if (this.parent.getActualParasites() < this.parent.getTotalParasites() && this.checkNak() && this.spawnDet(target, new EntityNak(this.parent.world), 3, 1, true)) {
                  ;
               }
               break;
            case 2:
               this.parent.checkID();
               if (this.parent.getActualParasites() < this.parent.getTotalParasites() && this.checkNak() && this.spawnDet(target, new EntityNak(this.parent.world), 3, 1, true)) {
                  ;
               }
               break;
            case 3:
               if (this.parent.world.rand.nextBoolean()) {
                  this.parent.checkID();
                  if (this.parent.getActualParasites() < this.parent.getTotalParasites() && this.spawnDet(target, new EntityUnvo(this.parent.world), 6, 4, true)) {
                     ;
                  }
               } else {
                  this.parent.checkID();
                  if (this.parent.getActualParasites() < this.parent.getTotalParasites() && this.checkNak() && this.spawnDet(target, new EntityNak(this.parent.world), 3, 1, true)) {
                     ;
                  }
               }
               break;
            case 4:
               if (this.parent.world.rand.nextBoolean()) {
                  this.parent.checkID();
                  if (this.parent.getActualParasites() < this.parent.getTotalParasites() && this.spawnDet(target, new EntityUnvo(this.parent.world), 6, 4, true)) {
                     ;
                  }
               } else {
                  this.parent.checkID();
                  if (this.parent.getActualParasites() < this.parent.getTotalParasites() && this.checkNak() && this.spawnDet(target, new EntityNak(this.parent.world), 3, 1, true)) {
                     ;
                  }
               }
            }
         }

         if (!target.isEntityAlive()) {
            this.parent.setAttackTarget((EntityLivingBase)null);
         }
      }

   }

   private void findT(double follow, AxisAlignedBB axisalignedbb) {
      for(EntityLivingBase mob : this.parent.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb)) {
         if (mob != null) {
            if (mob instanceof EntityPlayer) {
               if (this.canAttackPlayer((EntityPlayer)mob)) {
                  this.parent.setAttackTarget(mob);
                  return;
               }
            } else if (mob instanceof EntityLiving && !ParasiteEventEntity.checkEntity((EntityLiving)mob, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite) && mob != this.parent && this.canTargetEntity(mob) && mob.getDistanceSq(this.parent) < 1024.0D && mob.isEntityAlive() && this.parent.canAttackClass(mob.getClass())) {
               this.parent.setAttackTarget(mob);
               return;
            }
         }
      }

   }

   private boolean canTargetEntity(EntityLivingBase in) {
      return !(in instanceof EntityParasiteBase) && !(in instanceof EntityAnimal) && !(in instanceof EntityCreeper) && !(in instanceof EntityWaterMob);
   }

   private boolean canAttackPlayer(EntityPlayer in) {
      return in.capabilities.disableDamage ? false : EntityAITarget.isSuitableTarget(this.parent, in, false, true);
   }

   private void summonTentacles(EntityLivingBase in, double follow, AxisAlignedBB axisalignedbb, boolean dod) {
      if (this.parent.world.rand.nextInt(2) == 0) {
         this.parent.checkID();
         this.checkdodts();
         if (this.resistanceI.size() < this.maxDodT || !dod) {
            if ((this.parent.getActualParasites() < this.parent.getTotalParasites() || this.parent.getMobList().size() > 0) && this.spawnDet(in, new EntityDodT(this.parent.world), 5, 3, dod)) {
               ;
            }

         }
      }
   }

   private void checkdodts() {
      for(int i = 0; i < this.resistanceI.size(); ++i) {
         Entity flag = this.parent.world.getEntityByID(((Integer)this.resistanceI.get(i)).intValue());
         if (flag == null) {
            this.resistanceI.remove(i);
            i = 0;
         } else if (flag instanceof EntityDodT) {
            EntityDodT flagg = (EntityDodT)flag;
            if (!flagg.isEntityAlive()) {
               this.resistanceI.remove(i);
               i = 0;
            }
         } else {
            this.resistanceI.remove(i);
            i = 0;
         }
      }

   }

   private boolean spawnDet(EntityLivingBase in, EntityPStationary entityout, int max, int mini, boolean addId) {
      boolean tele = entityout.getParasiteIDRegister() == 74;
      if (this.parent.getMobList().size() <= 0 && tele) {
         entityout.setDead();
         return false;
      } else {
         EaglercraftRandom rand = new EaglercraftRandom();
         double x = in.posX;
         double y = in.posY;
         double z = in.posZ;
         double randomx = (double)(rand.nextInt(max) + mini);
         double randomz = (double)(rand.nextInt(max) + mini);
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

         while(flag) {
            if (limit >= 5) {
               entityout.setDead();
               return false;
            }

            BlockPos pos = new BlockPos(x + randomx, y, z + randomz);
            pos = ParasiteEventEntity.getFloor(this.parent.world, pos, 5);
            if (pos != null) {
               this.parent.world.getBlockState(pos);
               if (this.parent.world.getBlockState(pos.down()).isFullCube()) {
                  float bHard = 0.0F;
                  IBlockState state2 = this.parent.world.getBlockState(pos.down());
                  float atm = state2.getBlockHardness(this.parent.world, pos.down());
                  if (atm <= 0.0F) {
                     randomx = (double)(rand.nextInt(max) + mini);
                     randomz = (double)(rand.nextInt(max) + mini);
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

                  bHard = bHard + atm;
                  state2 = this.parent.world.getBlockState(pos.down(2));
                  atm = state2.getBlockHardness(this.parent.world, pos.down(2));
                  if (atm <= 0.0F) {
                     randomx = (double)(rand.nextInt(max) + mini);
                     randomz = (double)(rand.nextInt(max) + mini);
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

                  bHard = bHard + atm;
                  state2 = this.parent.world.getBlockState(pos.down(3));
                  atm = state2.getBlockHardness(this.parent.world, pos.down(3));
                  if (atm <= 0.0F) {
                     randomx = (double)(rand.nextInt(max) + mini);
                     randomz = (double)(rand.nextInt(max) + mini);
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

                  bHard = bHard + atm;
                  if (bHard >= this.maxHardness) {
                     randomx = (double)(rand.nextInt(max) + mini);
                     randomz = (double)(rand.nextInt(max) + mini);
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

                  AxisAlignedBB axisalignedbb = (new AxisAlignedBB((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1))).grow(2.0D);
                  List<EntityLivingBase> moblist = this.parent.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);
                  if (moblist.isEmpty()) {
                     entityout.setLocationAndAngles(x + randomx, y, z + randomz, this.parent.rotationYaw, this.parent.rotationPitch);
                     if (this.parent.world.getCollisionBoxes(entityout, entityout.getEntityBoundingBox()).isEmpty()) {
                        entityout.onInitialSpawn(this.parent.world.getDifficultyForLocation(new BlockPos(entityout)), (IEntityLivingData)null);
                        entityout.setAttackTarget(in);
                        if (tele) {
                           this.resistanceI.add(Integer.valueOf(entityout.getEntityId()));
                           String name = this.parent.getParasiteStored();
                           if (name == null) {
                              entityout.setDead();
                              return false;
                           }

                           ((EntityDodT)entityout).setTele(name, this.stage);
                        } else {
                           this.parent.setActualParasites(1);
                           this.parent.addID(entityout.getEntityId(), 1);
                        }

                        entityout.setPeek(true);
                        entityout.setBuried();
                        if (this.parent.isBurning()) {
                           entityout.setFire(8);
                        }

                        this.parent.world.spawnEntity(entityout);
                        this.tickss = 10.0D;
                        this.parent.world.setEntityState(entityout, (byte)50);
                        flag = false;
                        return true;
                     }

                     randomx = (double)(rand.nextInt(max) + mini);
                     randomz = (double)(rand.nextInt(max) + mini);
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

            randomx = (double)(rand.nextInt(max) + mini);
            randomz = (double)(rand.nextInt(max) + mini);
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

         entityout.setDead();
         return false;
      }
   }

   private void unhide(AxisAlignedBB axisalignedbb) {
      List<EntityLivingBase> moblist = this.parent.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);

      for(EntityLivingBase mob : moblist) {
         if (mob != this.parent && !(mob instanceof EntityParasiteBase) && mob.onGround && !mob.isInWater() && !(mob instanceof EntityPlayer)) {
            if (!(mob instanceof EntityWaterMob) && this.parent.world.rand.nextInt(1) == 0 && moblist.size() < this.parent.getMobList().size()) {
               this.summonTentacles(mob, 0.0D, axisalignedbb, true);
            }

            if (mob.isPotionActive(SRPPotions.COTH_E)) {
               NBTTagCompound tags = mob.getEntityData();
               mob.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 600, 5));
               if (tags.hasKey("srpcothimmunity")) {
                  int key = tags.getInteger("srpcothimmunity");
                  if (key >= 1) {
                     key = key + 10;
                     tags.setInteger("srpcothimmunity", key);
                  }
               }
            }
         }
      }

   }

   private void storePara(AxisAlignedBB axisalignedbb) {
      for(EntityParasiteBase mob : this.parent.world.getEntitiesWithinAABB(EntityParasiteBase.class, axisalignedbb)) {
         if (mob.isEntityAlive() && !(mob instanceof EntityPStationary) && !(mob instanceof EntityPPreeminent) && mob.getAttackTarget() == null && mob.onGround && mob.ticksExisted > 100 && mob.canBeStored() == 0) {
            this.parent.storeParasite(mob);
         }
      }

   }

   private boolean checkNak() {
      AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.parent.posX, this.parent.posY, this.parent.posZ, this.parent.posX + 1.0D, this.parent.posY + 1.0D, this.parent.posZ + 1.0D)).grow(7.0D, 5.0D, 7.0D);
      List<EntityNak> moblist = this.parent.world.getEntitiesWithinAABB(EntityNak.class, axisalignedbb);
      return moblist.isEmpty();
   }
}
