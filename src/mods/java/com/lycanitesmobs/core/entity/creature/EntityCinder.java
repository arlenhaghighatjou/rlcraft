package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IFusable;
import com.lycanitesmobs.core.entity.EntityItemCustom;
import com.lycanitesmobs.core.entity.RapidFireProjectileEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityCinder extends TameableCreatureEntity implements IMob, IFusable {
   public float inWallDamageAbsorbed = 0.0F;
   protected IFusable fusionTarget;

   public EntityCinder(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.spawnsInBlock = false;
      this.hasAttackSound = false;
      this.setupMob();
      this.stepHeight = 1.0F;
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackRangedGoal(this)).setSpeed(0.75D).setStaminaTime(100).setRange(5.0F).setMinChaseDistance(2.0F));
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.getEntityWorld().isRemote && this.inWallDamageAbsorbed >= 10.0F) {
         this.transform(CreatureManager.getInstance().getEntityClass("volcan"), (Entity)null, false);
      }

      if (this.getEntityWorld().isRemote) {
         for(int i = 0; i < 2; ++i) {
            this.getEntityWorld().spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D, new int[0]);
            this.getEntityWorld().spawnParticle(EnumParticleTypes.FLAME, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D, new int[0]);
         }
      }

   }

   public void attackRanged(Entity target, float range) {
      List<RapidFireProjectileEntity> projectiles = new ArrayList();
      ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile("ember");
      if (projectileInfo != null) {
         RapidFireProjectileEntity projectileEntry = new RapidFireProjectileEntity(projectileInfo, this.getEntityWorld(), this, 15, 3);
         projectiles.add(projectileEntry);
         RapidFireProjectileEntity projectileEntry2 = new RapidFireProjectileEntity(projectileInfo, this.getEntityWorld(), this, 15, 3);
         ++projectileEntry2.offsetX;
         projectiles.add(projectileEntry2);
         RapidFireProjectileEntity projectileEntry3 = new RapidFireProjectileEntity(projectileInfo, this.getEntityWorld(), this, 15, 3);
         --projectileEntry3.offsetX;
         projectiles.add(projectileEntry3);
         RapidFireProjectileEntity projectileEntry4 = new RapidFireProjectileEntity(projectileInfo, this.getEntityWorld(), this, 15, 3);
         ++projectileEntry4.offsetZ;
         projectiles.add(projectileEntry4);
         RapidFireProjectileEntity projectileEntry5 = new RapidFireProjectileEntity(projectileInfo, this.getEntityWorld(), this, 15, 3);
         --projectileEntry5.offsetZ;
         projectiles.add(projectileEntry5);
         RapidFireProjectileEntity projectileEntry6 = new RapidFireProjectileEntity(projectileInfo, this.getEntityWorld(), this, 15, 3);
         ++projectileEntry6.offsetY;
         projectiles.add(projectileEntry6);
         RapidFireProjectileEntity projectileEntry7 = new RapidFireProjectileEntity(projectileInfo, this.getEntityWorld(), this, 15, 3);
         --projectileEntry7.offsetY;
         projectiles.add(projectileEntry7);

         for(RapidFireProjectileEntity projectile : projectiles) {
            projectile.setProjectileScale(1.0F);
            projectile.posY -= (double)(this.height / 4.0F);
            float accuracy = 1.0F * (this.getRNG().nextFloat() - 0.5F);
            double d0 = target.posX - this.posX + (double)accuracy;
            double d1 = target.posY + (double)target.getEyeHeight() - 1.100000023841858D - projectile.posY + (double)accuracy;
            double d2 = target.posZ - this.posZ + (double)accuracy;
            float f1 = MathHelper.sqrt(d0 * d0 + d2 * d2) * 0.2F;
            float velocity = 0.6F;
            projectile.shoot(d0, d1 + (double)f1, d2, velocity, 6.0F);
            this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
            this.getEntityWorld().spawnEntity(projectile);
         }

         super.attackRanged(target, range);
      }
   }

   public boolean isFlying() {
      return true;
   }

   public boolean petControlsEnabled() {
      return true;
   }

   public int getNoBagSize() {
      return 0;
   }

   public int getBagSize() {
      return this.creatureInfo.bagSize;
   }

   public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
      return super.isDamageTypeApplicable(type, source, damage);
   }

   public boolean canBurn() {
      return false;
   }

   public boolean waterDamage() {
      return true;
   }

   public float getDamageModifier(DamageSource damageSrc) {
      return damageSrc.isFireDamage() ? 0.0F : super.getDamageModifier(damageSrc);
   }

   public void applyDropEffects(EntityItemCustom entityitem) {
      entityitem.setCanBurn(false);
   }

   public float getBrightness() {
      return 1.0F;
   }

   @SideOnly(Side.CLIENT)
   public int getBrightnessForRender() {
      return 15728880;
   }

   public IFusable getFusionTarget() {
      return this.fusionTarget;
   }

   public void setFusionTarget(IFusable fusionTarget) {
      this.fusionTarget = fusionTarget;
   }

   public Class getFusionClass(IFusable fusable) {
      if (fusable instanceof EntityJengu) {
         return CreatureManager.getInstance().getEntityClass("xaphan");
      } else if (fusable instanceof EntityGeonach) {
         return CreatureManager.getInstance().getEntityClass("volcan");
      } else if (fusable instanceof EntityDjinn) {
         return CreatureManager.getInstance().getEntityClass("zephyr");
      } else if (fusable instanceof EntityAegis) {
         return CreatureManager.getInstance().getEntityClass("wisp");
      } else {
         return fusable instanceof EntityArgus ? CreatureManager.getInstance().getEntityClass("grue") : null;
      }
   }
}
