package com.dhanantry.scapeandrunparasites.entity.monster.ancient;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class EntityOroncoTen extends EntityParasiteBase {
   private int ticksGround;
   private int maxMobs;

   public EntityOroncoTen(World worldIn) {
      super(worldIn);
      this.setSize(1.0F, 0.7F);
      this.tasks.removeTask(this.aiWander);
      this.tasks.removeTask(this.folow);
      this.ignoreFrustumCheck = true;
      this.canD = SRPConfig.ancientdespawn;
      this.killcount = -10.0D;
      this.type = 62;
   }

   public int getParasiteIDRegister() {
      return 35;
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.ORONCO_HEALTH * 0.25D);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.ORONCO_ARMOR * 0.25D);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(2.0D);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.onGround) {
         ++this.ticksGround;
         if (this.ticksGround > 200 && !this.world.isRemote) {
            if (this.ticksGround % 20 == 0 && this.nearbydangerous() && this.maxMobs < 10) {
               if (ParasiteEventEntity.SummonM(this, new String[]{"srparasites:lodo;1;1"}, 2, this.getAttackTarget())) {
                  ++this.maxMobs;
               } else if (this.maxMobs >= 10) {
                  this.setDead();
               }
            }
         } else if (this.world.isRemote && this.ticksGround > 200) {
            this.spawnParticles(SRPEnumParticle.GCLOUD, 91, 81, 75);
            this.spawnParticles(SRPEnumParticle.GCLOUD, 164, 174, 180);
            this.spawnParticles(SRPEnumParticle.GCLOUD, 91, 81, 75);
            this.spawnParticles(SRPEnumParticle.GCLOUD, 164, 174, 180);
         }
      }

   }

   private boolean nearbydangerous() {
      int k = 0;
      AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D)).grow(16.0D);

      for(EntityLivingBase mob : this.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb)) {
         if (mob != this && mob instanceof EntityParasiteBase && mob.isEntityAlive()) {
            --k;
         } else if (mob.isEntityAlive()) {
            ++k;
         }
      }

      return k > 0;
   }

   public void fall(float distance, float damageMultiplier) {
   }

   public float getEyeHeight() {
      return 0.5F;
   }
}
