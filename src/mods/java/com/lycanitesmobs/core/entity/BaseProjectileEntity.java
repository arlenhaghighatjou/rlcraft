package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ModInfo;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

public class BaseProjectileEntity extends EntityThrowable {
   public String entityName = "projectile";
   public ModInfo modInfo;
   public long updateTick;
   public boolean movement = true;
   public float projectileScale = 1.0F;
   public int projectileLife = 200;
   public int damage = 1;
   public int pierce = 1;
   public double weight = 1.0D;
   public double knockbackChance = 1.0D;
   public int bonusDamage = 0;
   public boolean waterProof = false;
   public boolean lavaProof = false;
   public boolean cutsGrass = false;
   public boolean ripper = false;
   public boolean pierceBlocks = false;
   public int animationFrame = 0;
   public int animationFrameMax = 0;
   public int textureTiling = 1;
   public float textureScale = 1.0F;
   public float textureOffsetY = 0.0F;
   public boolean clientOnly = false;
   public float rollSpeed = 0.0F;
   protected static final DataParameter<Float> SCALE = EntityDataManager.createKey(BaseProjectileEntity.class, DataSerializers.FLOAT);

   public BaseProjectileEntity(World world) {
      super(world);
      this.dataManager.register(SCALE, Float.valueOf(this.projectileScale));
      this.setProjectileScale(this.projectileScale);
      this.setup();
   }

   public BaseProjectileEntity(World world, EntityLivingBase entityLiving) {
      super(world, entityLiving);
      this.shoot(entityLiving, entityLiving.rotationPitch, entityLiving.rotationYaw, 0.0F, 1.1F, 1.0F);
      this.dataManager.register(SCALE, Float.valueOf(this.projectileScale));
      this.setProjectileScale(this.projectileScale);
      this.setup();
   }

   public BaseProjectileEntity(World world, double x, double y, double z) {
      super(world, x, y, z);
      this.dataManager.register(SCALE, Float.valueOf(this.projectileScale));
      this.setProjectileScale(this.projectileScale);
      this.setup();
   }

   public void setup() {
   }

   public String getStringFromDataManager(DataParameter<String> key) {
      try {
         return (String)this.getDataManager().get(key);
      } catch (Exception var3) {
         return null;
      }
   }

   public void onUpdate() {
      ++this.updateTick;
      if (!this.movement) {
         this.inGround = false;
         this.timeUntilPortal = this.getPortalCooldown();
      }

      double initX = this.posX;
      double initY = this.posY;
      double initZ = this.posZ;
      super.onUpdate();
      if (!this.movement) {
         this.posX = initX;
         this.posY = initY;
         this.posZ = initZ;
         this.motionX = 0.0D;
         this.motionY = 0.0D;
         this.motionZ = 0.0D;
         this.setPosition(this.posX, this.posY, this.posZ);
      }

      this.isInWeb = false;
      if (!this.getEntityWorld().isRemote) {
         if (!this.waterProof && this.isInWater()) {
            this.setDead();
         } else if (!this.lavaProof && this.isInLava()) {
            this.setDead();
         }
      }

      if ((!this.getEntityWorld().isRemote || this.clientOnly) && this.projectileLife-- <= 0) {
         this.setDead();
      }

      if (this.getEntityWorld().isRemote) {
         this.projectileScale = ((Float)this.dataManager.get(SCALE)).floatValue();
      }

      if (this.animationFrameMax > 0) {
         if (this.animationFrame != this.animationFrameMax && this.animationFrame >= 0) {
            ++this.animationFrame;
         } else {
            this.animationFrame = 0;
         }
      }

   }

   public boolean isInLava() {
      return false;
   }

   protected float getGravityVelocity() {
      return (float)this.weight * 0.03F;
   }

   public void spawnRunningParticles() {
   }

   public boolean handleWaterMovement() {
      return this.inWater;
   }

   protected void onImpact(RayTraceResult rayTraceResult) {
      boolean collided = false;
      boolean entityCollision = false;
      boolean doDamage = true;
      boolean blockCollision = false;
      BlockPos impactPos = this.getPosition();
      if (rayTraceResult.entityHit != null) {
         if (this.getThrower() != null && rayTraceResult.entityHit == this.getThrower()) {
            return;
         }

         if (rayTraceResult.entityHit instanceof EntityLivingBase) {
            doDamage = this.canDamage((EntityLivingBase)rayTraceResult.entityHit);
         }

         if (!this.getEntityWorld().isRemote && (this.getThrower() == null || rayTraceResult.entityHit != this.getThrower())) {
            this.onEntityCollision(rayTraceResult.entityHit);
         }

         if (doDamage && rayTraceResult.entityHit instanceof EntityLivingBase) {
            EntityLivingBase target = (EntityLivingBase)rayTraceResult.entityHit;
            float damage = this.getDamage(target);
            if (damage != 0.0F) {
               double targetKnockbackResistance = 0.0D;
               boolean stopKnockback = false;
               if (this.knockbackChance < 1.0D && (this.knockbackChance <= 0.0D || this.rand.nextDouble() <= this.knockbackChance)) {
                  targetKnockbackResistance = target.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).getAttributeValue();
                  target.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
                  stopKnockback = true;
               }

               boolean attackSuccess;
               if (this.getThrower() instanceof BaseCreatureEntity) {
                  BaseCreatureEntity creatureThrower = (BaseCreatureEntity)this.getThrower();
                  boolean blocked = this.isBlockedByEntity(target, rayTraceResult.hitVec);
                  attackSuccess = creatureThrower.doRangedDamage(target, this, damage, blocked);
               } else {
                  double pierceDamage = (double)this.pierce;
                  if ((double)damage <= pierceDamage) {
                     attackSuccess = target.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()).setDamageBypassesArmor().setDamageIsAbsolute(), damage);
                  } else {
                     int hurtResistantTimeBefore = target.hurtResistantTime;
                     target.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()).setDamageBypassesArmor().setDamageIsAbsolute(), (float)pierceDamage);
                     target.hurtResistantTime = hurtResistantTimeBefore;
                     damage = (float)((double)damage - pierceDamage);
                     attackSuccess = target.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), damage);
                  }
               }

               this.onEntityLivingDamage(target);
               this.onDamage(target, damage, attackSuccess);
               if (stopKnockback) {
                  target.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(targetKnockbackResistance);
               }
            }
         }

         collided = true;
         entityCollision = true;
         int i = (int)Math.floor(rayTraceResult.entityHit.posX);
         int j = (int)Math.floor(rayTraceResult.entityHit.posY);
         int k = (int)Math.floor(rayTraceResult.entityHit.posZ);
         impactPos = new BlockPos(i, j, k);
         if (!this.getEntityWorld().isRemote && this.canDestroyBlock(impactPos)) {
            try {
               this.placeBlock(this.getEntityWorld(), impactPos);
            } catch (Exception var18) {
               ;
            }
         }
      } else {
         int i = rayTraceResult.getBlockPos().getX();
         int j = rayTraceResult.getBlockPos().getY();
         int k = rayTraceResult.getBlockPos().getZ();
         BlockPos blockPos = new BlockPos(i, j, k);
         IBlockState blockState = this.getEntityWorld().getBlockState(blockPos);
         if (!(blockState.getBlock() instanceof BlockTallGrass) && blockState.getBlock() != Blocks.DOUBLE_PLANT) {
            collided = blockState.getMaterial().isSolid();
            if (!this.waterProof && blockState.getMaterial() == Material.WATER) {
               collided = true;
            }

            if (!this.lavaProof && blockState.getMaterial() == Material.LAVA) {
               collided = true;
            }
         } else if (this.cutsGrass) {
            this.world.destroyBlock(blockPos, false);
         }

         if (collided) {
            blockCollision = true;
            switch(rayTraceResult.sideHit) {
            case DOWN:
               --j;
               break;
            case UP:
               ++j;
               break;
            case SOUTH:
               --k;
               break;
            case NORTH:
               ++k;
               break;
            case WEST:
               --i;
               break;
            case EAST:
               ++i;
            }

            impactPos = new BlockPos(i, j, k);
            if (!this.getEntityWorld().isRemote && this.canDestroyBlock(impactPos)) {
               try {
                  this.placeBlock(this.getEntityWorld(), impactPos);
               } catch (Exception var17) {
                  ;
               }
            }
         }
      }

      if (collided && (!entityCollision || doDamage)) {
         if (!this.getEntityWorld().isRemote) {
            this.onImpactComplete(impactPos);
         } else {
            this.onImpactVisuals();
         }

         boolean entityPierced = this.ripper && entityCollision;
         boolean blockPierced = this.pierceBlocks && blockCollision;
         if (!this.getEntityWorld().isRemote && !entityPierced && !blockPierced) {
            this.setDead();
            if (this.getImpactSound() != null) {
               this.playSound(this.getImpactSound(), 1.0F, 1.0F / (this.getEntityWorld().rand.nextFloat() * 0.4F + 0.8F));
            }
         }
      }

   }

   public boolean isBlockedByEntity(Entity targetEntity, Vec3d damagePostition) {
      if (!(targetEntity instanceof EntityLivingBase)) {
         return false;
      } else {
         EntityLivingBase targetLiving = (EntityLivingBase)targetEntity;
         return targetLiving.isActiveItemStackBlocking();
      }
   }

   public boolean canDamage(EntityLivingBase targetEntity) {
      if (this.getEntityWorld().isRemote) {
         return false;
      } else {
         EntityLivingBase owner = this.getThrower();
         if (owner != null) {
            if (owner instanceof BaseCreatureEntity) {
               BaseCreatureEntity ownerCreature = (BaseCreatureEntity)owner;
               if (!ownerCreature.canAttackEntity(targetEntity)) {
                  return false;
               }
            }

            if (owner instanceof EntityPlayer) {
               if (MinecraftForge.EVENT_BUS.post(new AttackEntityEvent((EntityPlayer)owner, targetEntity))) {
                  return false;
               }

               if (targetEntity instanceof TameableCreatureEntity) {
                  TameableCreatureEntity targetCreature = (TameableCreatureEntity)targetEntity;
                  if (targetCreature.getPlayerOwner() == owner) {
                     return false;
                  }
               }
            }

            if (!this.getEntityWorld().getMinecraftServer().isPVPEnabled() && owner instanceof EntityPlayer) {
               if (targetEntity instanceof EntityPlayer) {
                  return false;
               }

               if (targetEntity instanceof TameableCreatureEntity) {
                  TameableCreatureEntity tamedTarget = (TameableCreatureEntity)targetEntity;
                  if (tamedTarget.isTamed()) {
                     return false;
                  }
               }
            }

            if (owner.isOnSameTeam(targetEntity) && CreatureManager.getInstance().config.friendlyFire) {
               return false;
            }
         }

         return true;
      }
   }

   public void onDamage(EntityLivingBase target, float damage, boolean attackSuccess) {
   }

   public void onEntityCollision(Entity entity) {
   }

   public boolean onEntityLivingDamage(EntityLivingBase entityLiving) {
      return true;
   }

   public boolean canDestroyBlock(BlockPos pos) {
      if (this.getEntityWorld().isAirBlock(pos)) {
         return true;
      } else {
         IBlockState blockState = this.getEntityWorld().getBlockState(pos);
         return !blockState.getMaterial().isSolid();
      }
   }

   public void placeBlock(World world, BlockPos pos) {
   }

   public void onImpactComplete(BlockPos impactPos) {
   }

   public void onImpactVisuals() {
   }

   public boolean canBeCollidedWith() {
      return false;
   }

   public boolean attackEntityFrom(DamageSource damageSource, float damage) {
      return false;
   }

   public void setProjectileScale(float scale) {
      this.projectileScale = scale;
      if (this.getEntityWorld().isRemote && !this.clientOnly) {
         this.setSize(this.projectileScale, this.projectileScale);
      } else {
         if (this.getThrower() != null && this.getThrower() instanceof BaseCreatureEntity) {
            this.projectileScale = (float)((double)this.projectileScale * ((BaseCreatureEntity)this.getThrower()).sizeScale);
         }

         this.setSize(this.projectileScale, this.projectileScale);
         this.dataManager.set(SCALE, Float.valueOf(this.projectileScale));
      }
   }

   public float getProjectileScale() {
      return this.projectileScale;
   }

   public float getTextureOffsetY() {
      return 0.0F;
   }

   public void setDamage(int damage) {
      this.damage = damage;
   }

   public float getDamage(Entity entity) {
      float damage = (float)this.damage + (float)this.bonusDamage;
      if (this.getThrower() != null && (this.getThrower() instanceof EntityPlayer || this.getThrower().getControllingPassenger() instanceof EntityPlayer) && !(entity instanceof EntityPlayer)) {
         damage *= 1.2F;
      }

      return damage;
   }

   public void setPierce(int pierce) {
      this.pierce = pierce;
   }

   public int getPierce() {
      return this.pierce;
   }

   public int getEffectDuration(int seconds) {
      return this.getThrower() != null && this.getThrower() instanceof BaseCreatureEntity ? Math.round((float)((BaseCreatureEntity)this.getThrower()).getEffectDuration(seconds) / 5.0F) : seconds * 20;
   }

   public float getEffectStrength(float value) {
      return this.getThrower() != null && this.getThrower() instanceof BaseCreatureEntity ? (float)((BaseCreatureEntity)this.getThrower()).getEffectStrength(value) : value;
   }

   public void setBonusDamage(int bonusDamage) {
      this.bonusDamage = bonusDamage;
   }

   public double[] getFacingPosition(double distance) {
      return this.getFacingPosition(this, distance, 0.0D);
   }

   public double[] getFacingPosition(Entity entity, double distance, double angleOffset) {
      double angle = Math.toRadians((double)entity.rotationYaw) + angleOffset;
      double xAmount = -Math.sin(angle);
      double zAmount = Math.cos(angle);
      double[] coords = new double[3];
      coords[0] = entity.posX + distance * xAmount;
      coords[1] = entity.posY;
      coords[2] = entity.posZ + distance * zAmount;
      return coords;
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setFloat("ProjectileScale", this.projectileScale);
      compound.setInteger("ProjectileLife", this.projectileLife);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      if (compound.hasKey("ProjectileScale")) {
         this.setProjectileScale(compound.getFloat("ProjectileScale"));
      }

      if (compound.hasKey("ProjectileLife")) {
         this.projectileLife = compound.getInteger("ProjectileLife");
      }

   }

   public String getTextureName() {
      return this.entityName.toLowerCase();
   }

   public ResourceLocation getTexture() {
      if (AssetManager.getTexture(this.getTextureName()) == null) {
         AssetManager.addTexture(this.getTextureName(), this.modInfo, "textures/items/" + this.getTextureName() + ".png");
      }

      return AssetManager.getTexture(this.getTextureName());
   }

   public SoundEvent getLaunchSound() {
      return AssetManager.getSound(this.entityName);
   }

   public SoundEvent getImpactSound() {
      return AssetManager.getSound(this.entityName + "_impact");
   }

   public SoundEvent getBeamSound() {
      return this.getLaunchSound();
   }
}
