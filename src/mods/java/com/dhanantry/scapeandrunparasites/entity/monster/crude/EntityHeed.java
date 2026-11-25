package com.dhanantry.scapeandrunparasites.entity.monster.crude;

import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIWaterLeapAtTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPCrude;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.google.common.base.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class EntityHeed extends EntityPCrude implements EntityBodyParts {
   private EntityBody head;
   private int border;
   private boolean skillThrow;

   public EntityHeed(World worldIn) {
      super(worldIn);
      this.setSize(0.9F, 1.9F);
      this.stepHeight = 1.0F;
      this.ignoreFrustumCheck = true;
      this.type = 31;
      this.skillThrow = false;
      if (SRPConfig.mobattacking) {
         this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus(this, EntityLiving.class, 0, !SRPConfigSystems.useOneMind, false, new Predicate<EntityLiving>() {
            public boolean apply(@Nullable EntityLiving entity) {
               return !(entity instanceof EntityWaterMob) && !(entity instanceof EntityAnimal) && !(entity instanceof EntityVillager) && !ParasiteEventEntity.checkEntity(entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
            }
         }, SRPConfig.primitiveSneakPen, SRPConfig.primitiveInviPen));
      }

      this.head = new EntityBody(this, 1.8F, 1.8F, 1.0F, 2.1F, 0.3F, 1, 1, false, 0.2F);
   }

   public int getParasiteIDRegister() {
      return 63;
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(0, new EntityAISwimmingDiving(this, 0.095D));
      this.tasks.addTask(2, new EntityAIWaterLeapAtTargetStatus(this, 0.7F, 1.5D, 3, 20, 0));
      this.tasks.addTask(3, new EntityAIAttackMeleeStatus(this, 1.3D, false, 8.0D, 10));
      this.tasks.addTask(2, new EntityAISkill(this, 200, 20, false, 1));
      this.tasks.addTask(6, new EntityAIGetFollowers(this, 2, 16));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.HEED_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.HEED_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.32D);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(SRPAttributes.HEED_KD_RESISTANCE);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.HEED_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.primitiveFollow);
   }

   protected void entityInit() {
      super.entityInit();
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      this.head.onUpdate();
   }

   public boolean attackEntityBodyFrom(DamageSource source, float amount, int id, boolean notify) {
      if (this.rand.nextBoolean()) {
         SRPPotions.applyStackPotion(SRPPotions.BLEED_E, this, 80, 0);
      }

      return this.attackEntityFrom(source, amount * 3.0F);
   }

   public void setBodyPartDead(int id) {
   }

   public void setDead() {
      if (this.head != null) {
         this.world.removeEntityDangerously(this.head);
      }

      super.setDead();
   }

   public float getEyeHeight() {
      return 1.5F;
   }

   public static void registerFixesBano(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityHeed.class);
   }

   public void onKillEntity(EntityLivingBase entityLivingIn) {
      super.onKillEntity(entityLivingIn);
      this.particleStatus((byte)5);
   }

   public boolean getFinished(byte attID) {
      switch(attID) {
      case 1:
         return this.skillThrow;
      default:
         return super.getFinished(attID);
      }
   }

   public void setFinished(byte attID, boolean in) {
      switch(attID) {
      case 1:
         this.skillThrow = in;
         return;
      default:
         super.setFinished(attID, in);
      }
   }

   public void doSpecialSkill(byte id) {
      switch(id) {
      case 1:
         this.throwBlock();
         return;
      default:
         super.doSpecialSkill(id);
      }
   }

   private void throwBlock() {
      AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D)).grow(1.5D);

      for(EntityParasiteBase entityIn : this.world.getEntitiesWithinAABB(EntityParasiteBase.class, axisalignedbb)) {
         if (entityIn != this && SRPConfigSystems.rageEnable) {
            entityIn.addPotionEffect(new PotionEffect(SRPPotions.RAGE_E, 1200, 0, false, false));
         }
      }

      this.skillThrow = true;
      this.setParasiteStatus(0);
      this.border = 0;
   }
}
