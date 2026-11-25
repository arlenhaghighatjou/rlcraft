package com.lycanitesmobs.core.item.equipment.features;

import com.google.gson.JsonObject;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.CustomProjectileEntity;
import com.lycanitesmobs.core.entity.ExtendedEntity;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ProjectileEquipmentFeature extends EquipmentFeature {
   public String projectileName;
   public String projectileTrigger = "secondary";
   public String projectilePattern = "simple";
   public double hitChance = 0.05D;
   public int cooldown = 2;
   public int count = 1;
   public double spreadX = 0.0D;
   public double spreadY = 0.0D;
   public double ringRange = 0.0D;
   public int bonusDamage = 0;
   private BaseProjectileEntity channeledProjectile;

   public ProjectileEquipmentFeature() {
   }

   public void loadFromJSON(JsonObject json) {
      super.loadFromJSON(json);
      this.projectileName = json.get("projectileName").getAsString();
      if (json.has("projectileTrigger")) {
         this.projectileTrigger = json.get("projectileTrigger").getAsString();
      }

      if (json.has("projectilePattern")) {
         this.projectilePattern = json.get("projectilePattern").getAsString();
      }

      if (json.has("hitChance")) {
         this.hitChance = json.get("hitChance").getAsDouble();
      }

      if (json.has("cooldown")) {
         this.cooldown = json.get("cooldown").getAsInt();
      }

      if (json.has("count")) {
         this.count = json.get("count").getAsInt();
      }

      if (json.has("spreadX")) {
         this.spreadX = json.get("spreadX").getAsDouble();
      }

      if (json.has("spreadY")) {
         this.spreadY = json.get("spreadY").getAsDouble();
      }

      if (json.has("ringRange")) {
         this.ringRange = json.get("ringRange").getAsDouble();
      }

      if (json.has("bonusDamage")) {
         this.bonusDamage = json.get("bonusDamage").getAsInt();
      }

   }

   public boolean isActive(ItemStack itemStack, int level) {
      if (!super.isActive(itemStack, level)) {
         return false;
      } else {
         return ProjectileManager.getInstance().getProjectile(this.projectileName) != null;
      }
   }

   public String getDescription(ItemStack itemStack, int level) {
      if (!this.isActive(itemStack, level)) {
         return null;
      } else {
         ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile(this.projectileName);
         String description = LanguageManager.translate("equipment.feature." + this.featureType) + " " + projectileInfo.getTitle();
         if (!"simple".equals(this.projectilePattern)) {
            description = description + " " + LanguageManager.translate("equipment.feature.projectile.pattern." + this.projectilePattern);
         }

         description = description + " " + LanguageManager.translate("equipment.feature.projectile.trigger." + this.projectileTrigger);
         if ("hit".equals(this.projectileTrigger)) {
            description = description + " " + String.format("%.0f", this.hitChance * 100.0D) + "%";
         } else {
            description = description + " " + String.format("%.1f", (float)this.cooldown / 20.0F) + "s";
         }

         return description;
      }
   }

   public String getSummary(ItemStack itemStack, int level) {
      if (!this.isActive(itemStack, level)) {
         return null;
      } else {
         ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile(this.projectileName);
         String summary = projectileInfo.getTitle();
         if (this.bonusDamage != 0) {
            summary = summary + " +" + this.bonusDamage;
         }

         return summary;
      }
   }

   public boolean onUsePrimary(World world, EntityPlayer shooter, EnumHand hand) {
      if (!"primary".equalsIgnoreCase(this.projectileTrigger)) {
         return false;
      } else {
         ExtendedEntity shooterExt = ExtendedEntity.getForEntity(shooter);
         if (shooterExt == null) {
            return false;
         } else if (shooterExt.getProjectileCooldown(1, this.projectileName) > 0) {
            return false;
         } else {
            shooterExt.setProjectileCooldown(1, this.projectileName, this.cooldown);
            this.fireProjectile(shooter);
            return true;
         }
      }
   }

   public boolean onUseSecondary(World world, EntityPlayer shooter, EnumHand hand) {
      return "secondary".equalsIgnoreCase(this.projectileTrigger);
   }

   public boolean onHoldSecondary(EntityLivingBase shooter, int count) {
      if (!"secondary".equalsIgnoreCase(this.projectileTrigger)) {
         return false;
      } else {
         ExtendedEntity shooterExt = ExtendedEntity.getForEntity(shooter);
         if (shooterExt == null) {
            return false;
         } else if (shooterExt.getProjectileCooldown(2, this.projectileName) > 0) {
            return false;
         } else {
            shooterExt.setProjectileCooldown(2, this.projectileName, this.cooldown);
            this.fireProjectile(shooter);
            return true;
         }
      }
   }

   public boolean onHitEntity(ItemStack itemStack, EntityLivingBase target, EntityLivingBase attacker) {
      if (target != null && attacker != null && !attacker.getEntityWorld().isRemote && !attacker.isSneaking() && "hit".equals(this.projectileTrigger)) {
         if (attacker.getRNG().nextDouble() <= this.hitChance) {
            this.fireProjectile(attacker);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void fireProjectile(EntityLivingBase shooter) {
      if (shooter != null && !shooter.getEntityWorld().isRemote && this.count > 0) {
         if (this.channeledProjectile != null) {
            if (this.channeledProjectile.isEntityAlive()) {
               this.channeledProjectile.projectileLife = 20;
               return;
            }

            this.channeledProjectile = null;
         }

         World world = shooter.getEntityWorld();
         BaseProjectileEntity mainProjectile = null;
         Vec3d firePos = new Vec3d(shooter.posX, shooter.posY + (double)shooter.height * 0.65D, shooter.posZ);
         double offsetX = 0.0D;
         if ("spread".equals(this.projectilePattern)) {
            this.count = 10;
            this.spreadX = 45.0D;
            this.spreadY = 10.0D;

            for(int i = 0; i < this.count; ++i) {
               double yaw = (double)shooter.rotationYaw + this.spreadX * shooter.getRNG().nextDouble() - this.spreadX / 2.0D;
               double pitch = (double)shooter.rotationPitch + this.spreadY * shooter.getRNG().nextDouble() - this.spreadY / 2.0D;
               ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile(this.projectileName);
               BaseProjectileEntity projectile = projectileInfo.createProjectile(world, shooter);
               projectile.setPosition(firePos.x, firePos.y, firePos.z);
               projectile.shoot(shooter, (float)pitch, (float)yaw - (float)offsetX, 0.0F, (float)projectileInfo.velocity, 0.0F);
               projectile.setBonusDamage(this.bonusDamage);
               world.spawnEntity(projectile);
               mainProjectile = projectile;
            }
         } else if ("ring".equals(this.projectilePattern)) {
            double angle = this.ringRange / (double)this.count;

            for(int i = 0; i < this.count; ++i) {
               double yaw = (double)shooter.rotationYaw + angle * (double)i - this.ringRange / 2.0D;
               ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile(this.projectileName);
               BaseProjectileEntity projectile = projectileInfo.createProjectile(world, shooter);
               projectile.setPosition(firePos.x, firePos.y, firePos.z);
               projectile.setBonusDamage(this.bonusDamage);
               world.spawnEntity(projectile);
               projectile.shoot(shooter, shooter.rotationPitch, (float)yaw - (float)offsetX, 0.0F, (float)projectileInfo.velocity, 0.0F);
               mainProjectile = projectile;
            }
         } else {
            ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile(this.projectileName);
            mainProjectile = projectileInfo.createProjectile(world, shooter);
            mainProjectile.setPosition(firePos.x, firePos.y, firePos.z);
            mainProjectile.shoot(shooter, shooter.rotationPitch, shooter.rotationYaw - (float)offsetX, 0.0F, (float)projectileInfo.velocity, 0.0F);
            mainProjectile.setBonusDamage(this.bonusDamage);
            world.spawnEntity(mainProjectile);
         }

         if (this.count == 1 && mainProjectile instanceof CustomProjectileEntity) {
            CustomProjectileEntity customProjectileEntity = (CustomProjectileEntity)mainProjectile;
            if (customProjectileEntity.shouldChannel()) {
               this.channeledProjectile = customProjectileEntity;
            }
         }

         if (shooter instanceof EntityPlayer && mainProjectile != null) {
            world.playSound((EntityPlayer)null, shooter.getPosition(), mainProjectile.getLaunchSound(), SoundCategory.NEUTRAL, 0.5F, 0.4F / (shooter.getRNG().nextFloat() * 0.4F + 0.8F));
         }

      }
   }

   public Vec3d getFacingPosition(EntityLivingBase entity, double distance, double angle) {
      angle = Math.toRadians(angle);
      double xAmount = -Math.sin(angle);
      double zAmount = Math.cos(angle);
      return new Vec3d(entity.posX + distance * xAmount, entity.posY, entity.posZ + distance * zAmount);
   }
}
