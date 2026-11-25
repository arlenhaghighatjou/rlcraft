package com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus;

import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIVenkrolSummon;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPrimitive;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationaryArchitect;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityTendril;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityBano;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityCanra;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityEmana;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityHull;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityNogla;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityRanrac;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityShyco;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import java.util.List;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.World;
import net.minecraft.world.BossInfo.Color;
import net.minecraft.world.BossInfo.Overlay;

public class EntityVenkrolSIV extends EntityPStationaryArchitect implements EntityBodyParts {
   public EntityAIVenkrolSummon summonV = new EntityAIVenkrolSummon(this, SRPConfigMobs.venkrolsivlimit, 20 * SRPConfigMobs.venkrolsivCooldown, 4, SRPConfigMobs.venkrolsivCAMinimumV, (double)SRPConfigMobs.venkrolsivCAExtraM);
   private int count;
   private int tCount;
   private int ticksss;
   private EntityBody head;
   private final BossInfoServer bossInfo = (BossInfoServer)(new BossInfoServer(this.getDisplayName(), Color.RED, Overlay.PROGRESS)).setDarkenSky(false);

   public EntityVenkrolSIV(World worldIn) {
      super(worldIn);
      this.setSize(0.8F, 6.9F);
      this.ignoreFrustumCheck = true;
      this.head = new EntityBody(this, 1.9F, 1.9F, 1.0F, 0.0F, 7.0F, -1, 1, false, 0.2F);
      this.totalP = SRPConfigMobs.venkrolsivTotalActiveMobs;
      this.mobID = new int[this.totalP + SRPConfigMobs.venkrolsivlimit];
      this.mobPT = new int[this.totalP + SRPConfigMobs.venkrolsivlimit];
      this.stage = 4;
      this.experienceValue = SRPAttributes.XP_ADAPTED * 4;

      for(int i = 0; i < this.mobID.length; ++i) {
         this.mobID[i] = -777;
      }

      this.setBODY(1.0F);
      this.tasks.addTask(2, this.summonV);
      this.damageCap = SRPConfig.nexussivCap;
      this.count = 200;
      this.tCount = 300;
      this.ticksss = 0;
      this.pointCap = SRPConfig.nexussivPointCap;
      this.pointReduction = SRPConfig.nexussivPointRed;
      this.chanceLearn = SRPConfig.nexussivChanceLe;
      this.chanceLearnFire = SRPConfig.nexussivChanceLeFire;
      this.DamageTypeCap = SRPConfig.nexussivPointDamCap;
      this.valueEvDeath = SRPConfig.nexussivLoosingEPValue;
   }

   public int getParasiteIDRegister() {
      return 41;
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.VENKROLSIV_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.VENKROLSIV_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.VENKROLSIV_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.nexussivFollow);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      this.head.onUpdate();
      --this.count;
      --this.tCount;
      if (this.getParasiteStatus() == 0) {
         this.setBODY(0.04F);
      } else {
         this.setBODY(-0.04F);
      }

      this.spawnThunder();
      this.placeBiome();
   }

   private void spawnThunder() {
      if (!this.world.isRemote && this.rand.nextDouble() < 0.015D) {
         if (this.tCount > 0) {
            return;
         }

         if (this.count < 0 && this.upgradeParasites()) {
            this.tCount = 300;
            this.count = 300;
            return;
         }

         int range = 64;
         double randomx = (double)this.rand.nextInt(range);
         double randomz = (double)this.rand.nextInt(range);
         double negative = (double)this.rand.nextInt(2);
         if (negative == 0.0D) {
            randomx *= -1.0D;
         }

         negative = (double)this.rand.nextInt(2);
         if (negative == 0.0D) {
            randomz *= -1.0D;
         }

         BlockPos ray = ParasiteEventEntity.getFloor(this.world, new BlockPos(randomx + this.posX, this.posY, randomz + this.posZ), 5);
         if (ray != null) {
            if (SRPConfig.thunderEnable) {
               this.world.addWeatherEffect(new EntityLightningBolt(this.world, (double)ray.getX(), (double)ray.getY(), (double)ray.getZ(), true));
            }

            this.tCount = 200;
         }
      }

   }

   private boolean upgradeParasites() {
      int count = 3;
      int current = 0;
      AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D)).grow(48.0D, 34.0D, 48.0D);
      List<EntityParasiteBase> moblist = this.world.getEntitiesWithinAABB(EntityPInfected.class, axisalignedbb);

      for(EntityParasiteBase mob : moblist) {
         if (mob instanceof EntityPStationaryArchitect && (mob.getParasiteIDRegister() == 16 || mob.getParasiteIDRegister() == 18 || mob.getParasiteIDRegister() == 19)) {
            ((EntityPStationaryArchitect)mob).setActualT(0);
         }
      }

      if (moblist.size() > SRPConfig.nexussivCapUpgrade) {
         return true;
      } else {
         for(EntityParasiteBase mob : moblist) {
            if (mob.getParasiteIDRegister() != 64) {
               ++current;
               this.thunderParasite(mob, 1);
               if (current >= count) {
                  return true;
               }
            }
         }

         if (current >= count) {
            return true;
         } else {
            for(EntityParasiteBase mob : this.world.getEntitiesWithinAABB(EntityPPrimitive.class, axisalignedbb)) {
               ++current;
               this.thunderParasite(mob, 2);
               if (current >= count) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   private void thunderParasite(EntityParasiteBase in, int type) {
      if (ParasiteEventEntity.canSpawnNext) {
         switch(type) {
         case 1:
            EntityParasiteBase out = new EntityTendril(this.world);
            switch(this.rand.nextInt(7)) {
            case 0:
               if (SRPConfigMobs.emanaEnabled) {
                  out = new EntityEmana(in.world);
               }
               break;
            case 1:
               if (SRPConfigMobs.canraEnabled) {
                  out = new EntityCanra(in.world);
               }
               break;
            case 2:
               if (SRPConfigMobs.zetmoEnabled) {
                  out = new EntityBano(in.world);
               }
               break;
            case 3:
               if (SRPConfigMobs.shycoEnabled) {
                  out = new EntityShyco(in.world);
               }
               break;
            case 4:
               if (SRPConfigMobs.arachnidaEnabled) {
                  out = new EntityRanrac(in.world);
               }
               break;
            case 5:
               if (SRPConfigMobs.noglaEnabled) {
                  out = new EntityNogla(in.world);
               }
               break;
            case 6:
               if (SRPConfigMobs.hullEnabled) {
                  out = new EntityHull(in.world);
               }
            }

            if (SRPConfig.thunderEnable) {
               in.world.addWeatherEffect(new EntityLightningBolt(in.world, in.posX, in.posY, in.posZ, true));
            }

            ParasiteEventEntity.spawnNext(in, out, true, true);
            break;
         case 2:
            if (SRPConfig.thunderEnable) {
               in.world.addWeatherEffect(new EntityLightningBolt(in.world, in.posX, in.posY, in.posZ, true));
            }

            in.setKillC(in.getKillC() + 1000.0D);
         }

      }
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

   public boolean attackEntityFrom(DamageSource source, float amount) {
      boolean flag = super.attackEntityFrom(source, amount);
      if (flag && source.getTrueSource() != null && source.getTrueSource() instanceof EntityPlayerMP) {
         this.bossInfo.addPlayer((EntityPlayerMP)source.getTrueSource());
      }

      return flag;
   }

   public boolean attackEntityBodyFrom(DamageSource source, float amount, int id, boolean notify) {
      if (this.rand.nextBoolean()) {
         SRPPotions.applyStackPotion(SRPPotions.BLEED_E, this, 80, 0);
      }

      return this.attackEntityFrom(source, amount * 3.0F);
   }

   public void setDead() {
      if (this.head != null) {
         this.world.removeEntityDangerously(this.head);
      }

      super.setDead();
   }

   public float getEyeHeight() {
      return 8.1F;
   }

   public void setBODY(float in) {
      in = in + this.getBODY();
      if (in > 0.5F) {
         in = 0.5F;
      }

      if (in < 0.0F) {
         in = 0.0F;
      }

      this.body = in;
   }

   private void placeBiome() {
      if (this.ticksExisted >= 1200) {
         if (!this.world.isRemote) {
            ++this.ticksss;
            if (this.ticksss >= 20) {
               this.ticksss = 0;
               if (ParasiteEventWorld.canBiomeStillExist(this.world, this.getPosition(), true) >= 1) {
                  this.ticksss = -1000;
               } else {
                  int range = 7;
                  int attemp = 3;
                  int mini = 5;

                  while(attemp > 0) {
                     --attemp;
                     double randomx = (double)this.rand.nextInt(range);
                     double randomz = (double)this.rand.nextInt(range);
                     double negative = (double)this.rand.nextInt(2);
                     if (negative == 0.0D) {
                        randomx = randomx * -1.0D - (double)mini;
                     } else {
                        randomx = randomx + (double)mini;
                     }

                     negative = (double)this.rand.nextInt(2);
                     if (negative == 0.0D) {
                        randomz = randomz * -1.0D - (double)mini;
                     } else {
                        randomz = randomz + (double)mini;
                     }

                     BlockPos pos = new BlockPos(this.posX + randomx, this.posY, this.posZ + randomz);
                     pos = ParasiteEventEntity.getFloor(this.world, pos, 5);
                     if (pos != null && ParasiteEventWorld.placeHeartInWorld(this.world, pos) == 1) {
                        this.ticksss = -1000;
                        return;
                     }
                  }

                  this.ticksss = -100;
               }
            }
         }
      }
   }

   public boolean canDespawn() {
      return SRPConfig.rsDespawn;
   }

   public void setBodyPartDead(int id) {
   }

   public float getBombDamage() {
      return (float)SRPAttributes.VENKROLSIV_ATTACK_DAMAGE;
   }
}
