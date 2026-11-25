package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class EntityDropPod extends EntityParasiteBase {
   private byte owner;

   public EntityDropPod(World worldIn) {
      super(worldIn);
      this.setSize(1.0F, 2.0F);
      this.tasks.removeTask(this.aiWander);
      this.tasks.removeTask(this.folow);
      this.fuseTime = 80;
      this.type = 61;
      this.setParasiteStatus(1);
      this.killcount = -10.0D;
   }

   public int getParasiteIDRegister() {
      return 34;
   }

   public EntityDropPod(World worldIn, int points) {
      this(worldIn);
   }

   public void applyEntityCollision(Entity entityIn) {
   }

   protected void collideWithEntity(Entity entityIn) {
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((SRPAttributes.ORONCO_HEALTH + SRPAttributes.TERLA_HEALTH) * 0.1D);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(5.0D);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(2.0D);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.onGround) {
         this.setParasiteStatus(0);
         this.setSelfeState(1);
         this.dyingBurst(false, 2);
      } else {
         this.particleStatus((byte)12);
      }

   }

   protected void selfExplode() {
      boolean flag = ForgeEventFactory.getMobGriefingEvent(this.world, this) && SRPConfigMobs.ratholGriefing;
      ParasiteEventEntity.createExplosion(this.world, this, this.posX, this.posY, this.posZ, 4.0F, flag);
      if (!this.world.isRemote) {
         this.playSound(SRPSounds.RATHOL_BOOM, 1.0F, 1.0F);
         this.dead = true;
         String[] here = new String[3];
         AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D)).grow(7.0D);
         List<EntityLivingBase> moblist = this.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);

         for(String i : SRPConfigMobs.pod1Effects) {
            here = i.split(";");
            Potion potionE = Potion.getPotionFromResourceLocation(here[2]);
            if (potionE != null) {
               int duration = Integer.parseInt(here[0]) * 20;
               int amp = Integer.parseInt(here[1]);

               for(EntityLivingBase mob : moblist) {
                  if (mob != this && !(mob instanceof EntityParasiteBase)) {
                     mob.addPotionEffect(new PotionEffect(potionE, duration, amp, false, false));
                  }
               }
            }
         }

         this.setDead();
         this.spawnLingeringCloud();
         switch(this.owner) {
         case 62:
            int limit = 0;
            int cap = 0;

            while(limit < SRPConfigMobs.oroncoMaxMobPod && cap < 5) {
               if (ParasiteEventEntity.SummonM(this, SRPConfigMobs.oroncoMobList, 1, this.posX, this.posY, this.posZ, this.getAttackTarget(), false)) {
                  ++limit;
               } else {
                  ++cap;
               }
            }

            return;
         case 63:
            ParasiteEventEntity.spawnM(this, SRPConfigMobs.oroncoMobList, 0, false, this.getCustomNameTag());
         }
      }

   }

   private void spawnLingeringCloud() {
      EntityAreaEffectCloud entityareaeffectcloud = new EntityAreaEffectCloud(this.world, this.posX, this.posY, this.posZ);
      entityareaeffectcloud.setRadius(this.width * 2.0F);
      entityareaeffectcloud.setWaitTime(5);
      entityareaeffectcloud.setDuration(entityareaeffectcloud.getDuration());
      entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
      entityareaeffectcloud.addEffect(new PotionEffect(MobEffects.POISON, 300, 0));
      entityareaeffectcloud.addEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
      this.world.spawnEntity(entityareaeffectcloud);
   }

   public void setOwner(byte in) {
      this.owner = in;
   }

   public void fall(float distance, float damageMultiplier) {
   }

   public float getEyeHeight() {
      return 2.1F;
   }
}
