package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanSummon;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationaryArchitect;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrol;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrolSII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrolSIII;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.AxisAlignedBB;

public class EntityAIVenkrolSummon extends EntityAIBase {
   private final EntityPStationaryArchitect parentEntity;
   private int attackTimer = 0;
   private int limit = 0;
   private int sLimit;
   private int sCooldown;
   private int sStage;
   private boolean locked;
   private boolean counted;
   private boolean flag;
   private int extraV;
   private double extraM;
   private int totalV;
   private double totalM;

   public EntityAIVenkrolSummon(EntityPStationaryArchitect venkrol, int limit, int cooldown, int stage, int CAV, double CAM) {
      this.parentEntity = venkrol;
      this.sCooldown = cooldown;
      this.sLimit = limit;
      this.sStage = stage;
      this.locked = false;
      this.flag = false;
      this.counted = false;
      if (SRPConfigSystems.useEvolution) {
         if (SRPWorldData.get(venkrol.world).getEvolutionPhase() < SRPConfigSystems.evolutionBeckonIgnoreCooldown) {
            this.attackTimer = -this.sCooldown;
         }
      } else if (!SRPConfigSystems.rsIgnoreCooldownAtSpawn) {
         this.attackTimer = -this.sCooldown;
      }

      this.totalV = CAV;
      this.totalM = CAM;
   }

   public void startExecuting() {
      this.parentEntity.setParasiteStatus(1);
   }

   public boolean shouldExecute() {
      return this.parentEntity.getAttackTarget() != null;
   }

   public void resetTask() {
      this.limit = 0;
      this.parentEntity.setParasiteStatus(0);
   }

   public void updateTask() {
      if (this.locked) {
         --this.attackTimer;
         if (this.attackTimer <= -200) {
            this.locked = false;
            this.attackTimer = -this.sCooldown;
         }
      } else if (this.parentEntity.getAttackTarget() == null) {
         if (this.attackTimer > 0) {
            --this.attackTimer;
         }
      } else if (this.parentEntity.getAttackTarget().isDead) {
         if (this.attackTimer > 0) {
            --this.attackTimer;
         }
      } else {
         EntityLivingBase target = this.parentEntity.getAttackTarget();
         if (this.parentEntity.canEntityBeSeen(target)) {
            ++this.attackTimer;
            this.parentEntity.world.setEntityState(this.parentEntity, (byte)12);
            if (this.attackTimer % 20 == 0 && this.attackTimer > 0) {
               if (this.attackTimer == 20) {
                  this.extraV = this.countNeraby(this.parentEntity.getEntityId());
                  this.extraM = Math.floor((double)this.extraV * this.totalM);
                  this.flag = this.extraV >= this.totalV;
                  this.setNearbyNonConted(this.parentEntity.getEntityId());
                  EntityCanSummon parent = this.parentEntity;
                  parent.checkID();
               }

               EntityCanSummon parent = this.parentEntity;
               parent.checkID();
               if (parent.getActualParasites() < parent.getTotalParasites() && this.limit < this.sLimit) {
                  if (this.sStage == 4) {
                     if (ParasiteEventEntity.SummonM(this.parentEntity, SRPAttributes.VENKROLSIV_MOBTABLEG, 7, this.parentEntity.posX, this.parentEntity.posY, this.parentEntity.posZ, this.parentEntity.getAttackTarget(), false)) {
                        if (this.flag) {
                           this.setNerabyLocked(this.parentEntity.getEntityId());
                        }

                        ++this.limit;
                     }
                  } else {
                     String[] ground = SRPAttributes.VENKROL_MOBTABLEG;
                     String[] air = null;
                     switch(this.sStage) {
                     case 2:
                        ground = SRPAttributes.VENKROLSII_MOBTABLEG;
                        air = SRPAttributes.VENKROLSII_MOBTABLEA;
                        break;
                     case 3:
                        ground = SRPAttributes.VENKROLSIII_MOBTABLEG;
                        air = SRPAttributes.VENKROLSIII_MOBTABLEA;
                     }

                     if (ParasiteEventEntity.spawnBiomassFromBeckon(this.parentEntity, this.sStage, target, true, ground, air)) {
                        if (this.flag) {
                           this.setNerabyLocked(this.parentEntity.getEntityId());
                        }

                        ++this.limit;
                        int j = this.parentEntity.getActualT();
                        ++j;
                        this.parentEntity.setActualT(j);
                     }
                  }
               }

               if (this.limit >= this.sLimit) {
                  if (this.flag) {
                     if (this.extraM > 0.0D && this.attackTimer > this.sLimit * 20) {
                        if (this.sStage == 4) {
                           if (ParasiteEventEntity.SummonM(this.parentEntity, SRPAttributes.VENKROLSIV_MOBTABLEG, 5, this.parentEntity.posX, this.parentEntity.posY, this.parentEntity.posZ, this.parentEntity.getAttackTarget(), false)) {
                              --this.extraM;
                           }
                        } else {
                           String[] ground = SRPAttributes.VENKROL_MOBTABLEG;
                           String[] air = null;
                           switch(this.sStage) {
                           case 2:
                              ground = SRPAttributes.VENKROLSII_MOBTABLEG;
                              air = SRPAttributes.VENKROLSII_MOBTABLEA;
                              break;
                           case 3:
                              ground = SRPAttributes.VENKROLSIII_MOBTABLEG;
                              air = SRPAttributes.VENKROLSIII_MOBTABLEA;
                           }

                           if (ParasiteEventEntity.spawnBiomassFromBeckon(this.parentEntity, this.sStage, target, true, ground, air)) {
                              --this.extraM;
                           }
                        }
                     } else if (this.extraM <= 0.0D) {
                        this.extraM = 0.0D;
                        this.flag = false;
                        this.attackTimer = -this.sCooldown;
                        this.setNearbyFree(this.parentEntity.getEntityId());
                        this.limit = 0;
                     }
                  } else {
                     this.attackTimer = -this.sCooldown;
                     this.setNearbyFree(this.parentEntity.getEntityId());
                     this.limit = 0;
                  }
               }
            }
         } else if (this.attackTimer > 0) {
            --this.attackTimer;
         }
      }

   }

   public void setNerabyLocked(int id) {
      switch(this.sStage) {
      case 1:
         AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.parentEntity.posX, this.parentEntity.posY, this.parentEntity.posZ, this.parentEntity.posX + 1.0D, this.parentEntity.posY + 1.0D, this.parentEntity.posZ + 1.0D)).grow(this.parentEntity.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getBaseValue() + 5.0D);

         for(EntityVenkrol mob : this.parentEntity.world.getEntitiesWithinAABB(EntityVenkrol.class, axisalignedbb)) {
            if (mob.getEntityId() != id && mob.isEntityAlive() && !mob.summonV.getLocker() && mob.canEntityBeSeen(this.parentEntity)) {
               mob.summonV.setlocker(true);
               mob.summonV.setNerabyLocked(id);
            }
         }

         return;
      case 2:
         AxisAlignedBB axisalignedbbsii = (new AxisAlignedBB(this.parentEntity.posX, this.parentEntity.posY, this.parentEntity.posZ, this.parentEntity.posX + 1.0D, this.parentEntity.posY + 1.0D, this.parentEntity.posZ + 1.0D)).grow(this.parentEntity.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getBaseValue() + 5.0D);

         for(EntityVenkrolSII mob : this.parentEntity.world.getEntitiesWithinAABB(EntityVenkrolSII.class, axisalignedbbsii)) {
            if (mob.getEntityId() != id && mob.isEntityAlive() && !mob.summonV.getLocker() && mob.canEntityBeSeen(this.parentEntity)) {
               mob.summonV.setlocker(true);
               mob.summonV.setNerabyLocked(id);
            }
         }

         return;
      case 3:
         AxisAlignedBB axisalignedbbsiii = (new AxisAlignedBB(this.parentEntity.posX, this.parentEntity.posY, this.parentEntity.posZ, this.parentEntity.posX + 1.0D, this.parentEntity.posY + 1.0D, this.parentEntity.posZ + 1.0D)).grow(this.parentEntity.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getBaseValue() + 5.0D);

         for(EntityVenkrolSIII mob : this.parentEntity.world.getEntitiesWithinAABB(EntityVenkrolSIII.class, axisalignedbbsiii)) {
            if (mob.getEntityId() != id && mob.isEntityAlive() && !mob.summonV.getLocker() && mob.canEntityBeSeen(this.parentEntity)) {
               mob.summonV.setlocker(true);
               mob.summonV.setNerabyLocked(id);
            }
         }
      }

   }

   public void setNearbyFree(int id) {
      switch(this.sStage) {
      case 1:
         AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.parentEntity.posX, this.parentEntity.posY, this.parentEntity.posZ, this.parentEntity.posX + 1.0D, this.parentEntity.posY + 1.0D, this.parentEntity.posZ + 1.0D)).grow(this.parentEntity.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getBaseValue() + 5.0D);

         for(EntityVenkrol mob : this.parentEntity.world.getEntitiesWithinAABB(EntityVenkrol.class, axisalignedbb)) {
            if (mob.getEntityId() != id && mob.isEntityAlive() && mob.summonV.getLocker() && mob.canEntityBeSeen(this.parentEntity)) {
               mob.summonV.setlocker(false);
               mob.summonV.setCooldownZero(-this.sCooldown);
               mob.summonV.setNearbyFree(id);
            }
         }

         return;
      case 2:
         AxisAlignedBB axisalignedbbsii = (new AxisAlignedBB(this.parentEntity.posX, this.parentEntity.posY, this.parentEntity.posZ, this.parentEntity.posX + 1.0D, this.parentEntity.posY + 1.0D, this.parentEntity.posZ + 1.0D)).grow(this.parentEntity.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getBaseValue() + 5.0D);

         for(EntityVenkrolSII mob : this.parentEntity.world.getEntitiesWithinAABB(EntityVenkrolSII.class, axisalignedbbsii)) {
            if (mob.getEntityId() != id && mob.isEntityAlive() && mob.summonV.getLocker() && mob.canEntityBeSeen(this.parentEntity)) {
               mob.summonV.setlocker(false);
               mob.summonV.setCooldownZero(-this.sCooldown);
               mob.summonV.setNearbyFree(id);
            }
         }

         return;
      case 3:
         AxisAlignedBB axisalignedbbsiii = (new AxisAlignedBB(this.parentEntity.posX, this.parentEntity.posY, this.parentEntity.posZ, this.parentEntity.posX + 1.0D, this.parentEntity.posY + 1.0D, this.parentEntity.posZ + 1.0D)).grow(this.parentEntity.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getBaseValue() + 5.0D);

         for(EntityVenkrolSIII mob : this.parentEntity.world.getEntitiesWithinAABB(EntityVenkrolSIII.class, axisalignedbbsiii)) {
            if (mob.getEntityId() != id && mob.isEntityAlive() && mob.summonV.getLocker() && mob.canEntityBeSeen(this.parentEntity)) {
               mob.summonV.setlocker(false);
               mob.summonV.setCooldownZero(-this.sCooldown);
               mob.summonV.setNearbyFree(id);
            }
         }
      }

   }

   public void setCooldownZero(int in) {
      this.attackTimer = in;
   }

   public void setlocker(boolean in) {
      if (in) {
         this.attackTimer = -1;
      }

      this.locked = in;
   }

   public boolean getLocker() {
      return this.locked;
   }

   public int countNeraby(int id) {
      int count = 1;
      switch(this.sStage) {
      case 1:
         AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.parentEntity.posX, this.parentEntity.posY, this.parentEntity.posZ, this.parentEntity.posX + 1.0D, this.parentEntity.posY + 1.0D, this.parentEntity.posZ + 1.0D)).grow(this.parentEntity.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getBaseValue() + 5.0D);

         for(EntityVenkrol mob : this.parentEntity.world.getEntitiesWithinAABB(EntityVenkrol.class, axisalignedbb)) {
            if (mob.getEntityId() != id && mob.isEntityAlive() && mob.canEntityBeSeen(this.parentEntity) && !mob.summonV.getCounted()) {
               mob.summonV.setCounted(true);
               count += mob.summonV.countNeraby(id);
            }
         }

         return count;
      case 2:
         AxisAlignedBB axisalignedbbsii = (new AxisAlignedBB(this.parentEntity.posX, this.parentEntity.posY, this.parentEntity.posZ, this.parentEntity.posX + 1.0D, this.parentEntity.posY + 1.0D, this.parentEntity.posZ + 1.0D)).grow(this.parentEntity.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getBaseValue() + 5.0D);

         for(EntityVenkrolSII mob : this.parentEntity.world.getEntitiesWithinAABB(EntityVenkrolSII.class, axisalignedbbsii)) {
            if (mob.getEntityId() != id && mob.isEntityAlive() && mob.canEntityBeSeen(this.parentEntity) && !mob.summonV.getCounted()) {
               mob.summonV.setCounted(true);
               count += mob.summonV.countNeraby(id);
            }
         }

         return count;
      case 3:
         AxisAlignedBB axisalignedbbsiii = (new AxisAlignedBB(this.parentEntity.posX, this.parentEntity.posY, this.parentEntity.posZ, this.parentEntity.posX + 1.0D, this.parentEntity.posY + 1.0D, this.parentEntity.posZ + 1.0D)).grow(this.parentEntity.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getBaseValue() + 5.0D);

         for(EntityVenkrolSIII mob : this.parentEntity.world.getEntitiesWithinAABB(EntityVenkrolSIII.class, axisalignedbbsiii)) {
            if (mob.getEntityId() != id && mob.isEntityAlive() && mob.canEntityBeSeen(this.parentEntity) && !mob.summonV.getCounted()) {
               mob.summonV.setCounted(true);
               count += mob.summonV.countNeraby(id);
            }
         }
      }

      return count;
   }

   public void setNearbyNonConted(int id) {
      switch(this.sStage) {
      case 1:
         AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.parentEntity.posX, this.parentEntity.posY, this.parentEntity.posZ, this.parentEntity.posX + 1.0D, this.parentEntity.posY + 1.0D, this.parentEntity.posZ + 1.0D)).grow(this.parentEntity.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getBaseValue() + 5.0D);

         for(EntityVenkrol mob : this.parentEntity.world.getEntitiesWithinAABB(EntityVenkrol.class, axisalignedbb)) {
            if (mob.getEntityId() != id && mob.isEntityAlive() && mob.summonV.getCounted() && mob.canEntityBeSeen(this.parentEntity)) {
               mob.summonV.setCounted(false);
               mob.summonV.setNearbyNonConted(id);
            }
         }

         return;
      case 2:
         AxisAlignedBB axisalignedbbsii = (new AxisAlignedBB(this.parentEntity.posX, this.parentEntity.posY, this.parentEntity.posZ, this.parentEntity.posX + 1.0D, this.parentEntity.posY + 1.0D, this.parentEntity.posZ + 1.0D)).grow(this.parentEntity.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getBaseValue() + 5.0D);

         for(EntityVenkrolSII mob : this.parentEntity.world.getEntitiesWithinAABB(EntityVenkrolSII.class, axisalignedbbsii)) {
            if (mob.getEntityId() != id && mob.isEntityAlive() && mob.summonV.getCounted() && mob.canEntityBeSeen(this.parentEntity)) {
               mob.summonV.setCounted(false);
               mob.summonV.setNearbyNonConted(id);
            }
         }

         return;
      case 3:
         AxisAlignedBB axisalignedbbsiii = (new AxisAlignedBB(this.parentEntity.posX, this.parentEntity.posY, this.parentEntity.posZ, this.parentEntity.posX + 1.0D, this.parentEntity.posY + 1.0D, this.parentEntity.posZ + 1.0D)).grow(this.parentEntity.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getBaseValue() + 5.0D);

         for(EntityVenkrolSIII mob : this.parentEntity.world.getEntitiesWithinAABB(EntityVenkrolSIII.class, axisalignedbbsiii)) {
            if (mob.getEntityId() != id && mob.isEntityAlive() && mob.summonV.getCounted() && mob.canEntityBeSeen(this.parentEntity)) {
               mob.summonV.setCounted(false);
               mob.summonV.setNearbyNonConted(id);
            }
         }
      }

   }

   public void setCounted(boolean in) {
      this.counted = in;
   }

   public boolean getCounted() {
      return this.counted;
   }
}
