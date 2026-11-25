package com.oblivioussp.spartanweaponry.entity.projectile;

import com.google.common.collect.ImmutableMap;
import com.oblivioussp.spartanweaponry.api.IWeaponPropertyContainer;
import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import com.oblivioussp.spartanweaponry.api.weaponproperty.IPropertyCallback;
import com.oblivioussp.spartanweaponry.api.weaponproperty.WeaponProperty;
import com.oblivioussp.spartanweaponry.init.EnchantmentRegistrySW;
import com.oblivioussp.spartanweaponry.init.SoundRegistry;
import com.oblivioussp.spartanweaponry.item.ItemThrowingWeapon;
import com.oblivioussp.spartanweaponry.util.DamageSourcesSW;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityArrow.PickupStatus;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.common.registry.IThrowableEntity;

public class EntityThrownWeapon extends EntityArrow implements IThrowableEntity, IEntityAdditionalSpawnData {
   public static final String NBT_WEAPON = "weapon";
   public static final String NBT_LIFE = "life";
   protected static final DataParameter<ItemStack> DATA_WEAPON = EntityDataManager.createKey(EntityThrownWeapon.class, DataSerializers.ITEM_STACK);
   protected static final DataParameter<Byte> DATA_RETURN = EntityDataManager.createKey(EntityThrownWeapon.class, DataSerializers.BYTE);
   protected boolean isReturning = false;
   protected boolean playedReturnSound = false;

   public EntityThrownWeapon(World world) {
      super(world);
   }

   public EntityThrownWeapon(World world, double x, double y, double z) {
      super(world, x, y, z);
   }

   public EntityThrownWeapon(World world, EntityLivingBase shooter) {
      super(world, shooter);
   }

   protected void entityInit() {
      super.entityInit();
      this.getDataManager().register(DATA_WEAPON, ItemStack.EMPTY);
      this.getDataManager().register(DATA_RETURN, Byte.valueOf((byte)0));
   }

   public void setWeapon(ItemStack weaponStack) {
      ItemStack stack = weaponStack.copy();
      if (stack.hasTagCompound() && stack.getItem() instanceof ItemThrowingWeapon) {
         int maxAmmo = ((ItemThrowingWeapon)stack.getItem()).getMaxAmmoBase();
         stack.getTagCompound().setInteger("AmmoUsed", maxAmmo - 1);
         stack.getTagCompound().setBoolean("Original", false);
      }

      this.getDataManager().set(DATA_WEAPON, stack);
      this.getDataManager().setDirty(DATA_WEAPON);
      this.getDataManager().set(DATA_RETURN, Byte.valueOf((byte)EnchantmentHelper.getEnchantmentLevel(EnchantmentRegistrySW.THROWING_RETURN, stack)));
      this.getDataManager().setDirty(DATA_RETURN);
   }

   protected ItemStack getArrowStack() {
      return (ItemStack)this.getDataManager().get(DATA_WEAPON);
   }

   public ItemStack getWeaponStack() {
      return this.getArrowStack();
   }

   public boolean isReturning() {
      return this.isReturning;
   }

   public boolean isValidThrowingWeapon() {
      return !this.getWeaponStack().isEmpty();
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setShort("life", (short)this.ticksInGround);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      this.ticksInGround = compound.getShort("life");
   }

   protected void onHit(RayTraceResult raytraceResultIn) {
      Entity entity = raytraceResultIn.entityHit;
      if (entity != null) {
         ItemStack weapon = this.getWeaponStack();
         float i = (float)this.getDamage();
         float damage = i;
         if (this.noClip) {
            return;
         }

         DamageSource damagesource;
         if (this.shootingEntity == null) {
            damagesource = DamageSourcesSW.causeThrownWeaponMobDamage(this, this);
         } else if (this.shootingEntity instanceof EntityPlayer) {
            damagesource = DamageSourcesSW.causeThrownWeaponPlayerDamage(this, (EntityPlayer)this.shootingEntity);
         } else {
            damagesource = DamageSourcesSW.causeThrownWeaponMobDamage(this, this.shootingEntity);
         }

         if (weapon.getItem() instanceof IWeaponPropertyContainer && entity instanceof EntityLivingBase && this.shootingEntity instanceof EntityLivingBase && !entity.isEntityEqual(this.shootingEntity)) {
            IWeaponPropertyContainer<Item> container = (IWeaponPropertyContainer)weapon.getItem();
            float damageDirect = container.getDirectAttackDamage();
            ToolMaterialEx materialEx = container.getMaterialEx();

            for(WeaponProperty prop : container.getAllWeaponProperties()) {
               IPropertyCallback callback = prop.getCallback();
               if (callback != null) {
                  damage = callback.modifyDamageDealt(materialEx, damage, damageDirect + 1.0F, damagesource, (EntityLivingBase)this.shootingEntity, (EntityLivingBase)entity);
                  callback.onHitEntity(container.getMaterialEx(), weapon, (EntityLivingBase)entity, (EntityLivingBase)this.shootingEntity, this);
               }
            }

            for(WeaponProperty prop : materialEx.getAllWeaponProperties()) {
               IPropertyCallback callback = prop.getCallback();
               if (callback != null) {
                  damage = callback.modifyDamageDealt(materialEx, damage, damageDirect + 1.0F, damagesource, (EntityLivingBase)this.shootingEntity, (EntityLivingBase)entity);
                  callback.onHitEntity(materialEx, weapon, (EntityLivingBase)entity, (EntityLivingBase)this.shootingEntity, this);
               }
            }
         }

         if (this.isBurning() && !(entity instanceof EntityEnderman)) {
            entity.setFire(5);
         }

         if (entity.attackEntityFrom(damagesource, damage)) {
            if (!this.world.isRemote) {
               if (damage > i) {
                  ((WorldServer)this.world).spawnParticle(EnumParticleTypes.CRIT_MAGIC, entity.posX, entity.posY + (double)(entity.height / 2.0F), entity.posZ, 16, 0.2D, 0.2D, 0.2D, 0.0D, new int[0]);
               } else if (damage < i) {
                  ((WorldServer)this.world).spawnParticle(EnumParticleTypes.SMOKE_NORMAL, entity.posX, entity.posY + (double)(entity.height / 2.0F), entity.posZ, 16, 0.2D, 0.2D, 0.2D, 0.0D, new int[0]);
               }
            }

            if (weapon.getMaxStackSize() == 1 && weapon.attemptDamageItem(1, this.rand, (EntityPlayerMP)null)) {
               this.world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_ITEM_BREAK, this.getSoundCategory(), 0.8F, 0.8F + this.world.rand.nextFloat() * 0.4F);

               for(int j = 0; j < 5; ++j) {
                  Vec3d vec3d = new Vec3d(((double)this.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
                  vec3d = vec3d.rotatePitch(-this.rotationPitch * 0.017453292F);
                  vec3d = vec3d.rotateYaw(-this.rotationYaw * 0.017453292F);
                  double d0 = (double)(-this.rand.nextFloat()) * 0.6D - 0.3D;
                  Vec3d vec3d1 = new Vec3d(((double)this.rand.nextFloat() - 0.5D) * 0.3D, d0, 0.6D);
                  vec3d1 = vec3d1.rotatePitch(-this.rotationPitch * 0.017453292F);
                  vec3d1 = vec3d1.rotateYaw(-this.rotationYaw * 0.017453292F);
                  vec3d1 = vec3d1.addVector(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ);
                  if (this.world instanceof WorldServer) {
                     ((WorldServer)this.world).spawnParticle(EnumParticleTypes.ITEM_CRACK, vec3d1.x, vec3d1.y, vec3d1.z, 0, vec3d.x, vec3d.y + 0.05D, vec3d.z, 0.0D, new int[]{Item.getIdFromItem(weapon.getItem()), weapon.getMetadata()});
                  } else {
                     this.world.spawnParticle(EnumParticleTypes.ITEM_CRACK, vec3d1.x, vec3d1.y, vec3d1.z, vec3d.x, vec3d.y + 0.05D, vec3d.z, new int[]{Item.getIdFromItem(weapon.getItem()), weapon.getMetadata()});
                  }
               }

               this.setDead();
            }

            if (entity instanceof EntityLivingBase) {
               EntityLivingBase entitylivingbase = (EntityLivingBase)entity;
               if (this.knockbackStrength > 0) {
                  float f1 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
                  if (f1 > 0.0F) {
                     entitylivingbase.addVelocity(this.motionX * (double)this.knockbackStrength * 0.6000000238418579D / (double)f1, 0.1D, this.motionZ * (double)this.knockbackStrength * 0.6000000238418579D / (double)f1);
                  }
               }

               if (this.shootingEntity instanceof EntityLivingBase) {
                  EnchantmentHelper.applyThornEnchantments(entitylivingbase, this.shootingEntity);
                  EnchantmentHelper.applyArthropodEnchantments((EntityLivingBase)this.shootingEntity, entitylivingbase);
               }

               this.arrowHit(entitylivingbase);
               if (this.shootingEntity != null && entitylivingbase != this.shootingEntity && entitylivingbase instanceof EntityPlayer && this.shootingEntity instanceof EntityPlayerMP) {
                  ((EntityPlayerMP)this.shootingEntity).connection.sendPacket(new SPacketChangeGameState(6, 0.0F));
               }
            }

            this.playSound(SoundRegistry.THROWING_WEAPON_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
            if (!(entity instanceof EntityEnderman)) {
               this.motionX *= -0.1D;
               this.motionY *= -0.1D;
               this.motionZ *= -0.1D;
               this.rotationYaw += 180.0F;
               this.prevRotationYaw += 180.0F;
            }
         } else {
            this.motionX *= -0.10000000149011612D;
            this.motionY *= -0.10000000149011612D;
            this.motionZ *= -0.10000000149011612D;
            this.rotationYaw += 180.0F;
            this.prevRotationYaw += 180.0F;
            this.ticksInAir = 0;
            if (!this.world.isRemote && this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ < 0.0010000000474974513D) {
               if (((Byte)this.getDataManager().get(DATA_RETURN)).byteValue() > 0 && !this.noClip) {
                  this.noClip = true;
               }

               if (this.pickupStatus == PickupStatus.ALLOWED) {
                  this.dropAsItem();
               }

               this.setDead();
            }
         }
      } else if (!this.noClip) {
         if (raytraceResultIn.typeOfHit == Type.BLOCK) {
            if (!this.world.isRemote) {
               ((WorldServer)this.world).spawnParticle(EnumParticleTypes.BLOCK_DUST, this.posX, this.posY, this.posZ, 5, 0.1D, 0.1D, 0.1D, 0.05D, new int[]{Block.getIdFromBlock(this.world.getBlockState(raytraceResultIn.getBlockPos()).getBlock())});
            }

            ItemStack stack = this.getArrowStack();
            this.removeEnchantments(stack);
         }

         super.onHit(raytraceResultIn);
      }

   }

   protected void removeEnchantments(ItemStack stack) {
      if (stack.isItemEnchanted() && stack.hasTagCompound() && stack.getTagCompound().hasKey("AmmoUsed")) {
         EnchantmentHelper.setEnchantments(ImmutableMap.of(), stack);
         if (!this.world.isRemote) {
            ((WorldServer)this.world).spawnParticle(EnumParticleTypes.SPELL_WITCH, this.posX, this.posY, this.posZ, 10, 0.1D, 0.1D, 0.1D, 0.2D, new int[0]);
         }
      }

   }

   protected void dropAsItem() {
      ItemStack stack = this.getArrowStack();
      this.removeEnchantments(stack);
      this.entityDropItem(stack, 0.1F);
   }

   public void onCollideWithPlayer(EntityPlayer entityIn) {
      if (this.inGround) {
         this.attemptCatch(entityIn);
      }

   }

   protected boolean attemptCatch(EntityPlayer player) {
      if (!this.world.isRemote && this.arrowShake <= 0) {
         boolean flag = this.pickupStatus == PickupStatus.ALLOWED || this.pickupStatus == PickupStatus.CREATIVE_ONLY && player.capabilities.isCreativeMode;
         if (this.pickupStatus == PickupStatus.ALLOWED) {
            boolean pickUpAsNewItem = true;
            ItemStack weapon = this.getWeaponStack();

            for(int i = 0; i < player.inventory.getSizeInventory(); ++i) {
               ItemStack slotStack = player.inventory.getStackInSlot(i);
               if (ItemStack.areItemsEqualIgnoreDurability(slotStack, weapon) && weapon.hasTagCompound() && slotStack.hasTagCompound() && weapon.getTagCompound().getUniqueId("UUID").equals(slotStack.getTagCompound().getUniqueId("UUID")) && weapon.getItem() instanceof ItemThrowingWeapon) {
                  int maxAmmo = ((ItemThrowingWeapon)slotStack.getItem()).getMaxAmmo(slotStack);
                  int currentAmmo = maxAmmo - slotStack.getTagCompound().getInteger("AmmoUsed");
                  if (currentAmmo < maxAmmo) {
                     int itemDamage = slotStack.getItemDamage() + weapon.getItemDamage();
                     if (itemDamage > slotStack.getMaxDamage()) {
                        player.renderBrokenItemStack(weapon);
                        itemDamage -= slotStack.getMaxDamage() + 1;
                     } else {
                        ++currentAmmo;
                        slotStack.getTagCompound().setInteger("AmmoUsed", maxAmmo - currentAmmo);
                     }

                     slotStack.setItemDamage(itemDamage);
                     pickUpAsNewItem = false;
                     flag = true;
                     break;
                  }
               }
            }

            if (pickUpAsNewItem) {
               ItemStack pickUpStack = this.getArrowStack().copy();
               this.removeEnchantments(pickUpStack);
               flag = player.inventory.addItemStackToInventory(pickUpStack);
            }
         }

         if (flag) {
            player.onItemPickup(this, 1);
            this.setDead();
         }

         return flag;
      } else {
         return false;
      }
   }

   protected boolean canBeCaughtInMidair(Entity shooter, Entity entityHit) {
      return shooter == entityHit && this.noClip;
   }

   public int getTicksInAir() {
      return this.ticksInAir;
   }

   public void onUpdate() {
      if (this.inGround) {
         ++this.ticksInGround;
      }

      if (this.timeInGround > 4 || this.isReturning && this.shootingEntity != null) {
         int returnLevel = ((Byte)this.getDataManager().get(DATA_RETURN)).byteValue();
         if (returnLevel > 0 && this.shootingEntity.isEntityAlive() && (!(this.shootingEntity instanceof EntityPlayerMP) || this.shootingEntity instanceof EntityPlayer && !((EntityPlayer)this.shootingEntity).isSpectator())) {
            if (!this.isReturning) {
               this.noClip = true;
               this.inGround = false;
               this.isReturning = true;
               this.setNoGravity(true);
            }

            Vec3d distance = this.shootingEntity.getPositionEyes(1.0F).subtract(this.getPositionVector());
            this.posY += distance.y * 0.015D * (double)returnLevel;
            if (this.world.isRemote) {
               this.prevPosY = this.posY;
            }

            double velocity = 1.0D + 0.25D * (double)(returnLevel - 1);
            Vec3d motionVec = distance.normalize().scale(velocity);
            this.motionX = motionVec.x;
            this.motionY = motionVec.y;
            this.motionZ = motionVec.z;
            if (!this.playedReturnSound) {
               this.world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, SoundRegistry.THROWING_WEAPON_RETURN, SoundCategory.NEUTRAL, 10.0F, 0.1F);
               this.playedReturnSound = true;
            }
         } else if (returnLevel > 0 && !this.shootingEntity.isEntityAlive()) {
            this.noClip = false;
            this.isReturning = false;
            this.setNoGravity(false);
            this.getDataManager().set(DATA_RETURN, Byte.valueOf((byte)0));
         }
      }

      if (this.ticksInGround >= 1200) {
         if (this.pickupStatus == PickupStatus.ALLOWED) {
            this.dropAsItem();
         }

         this.setDead();
      }

      if (!this.inGround) {
         ++this.ticksInAir;
      } else if (this.ticksInAir != 0) {
         this.ticksInAir = 0;
      }

      if (!this.world.isRemote) {
         this.setFlag(6, this.isGlowing());
      }

      this.onEntityUpdate();
      if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
         float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * 57.29577951308232D);
         this.rotationPitch = (float)(MathHelper.atan2(this.motionY, (double)f) * 57.29577951308232D);
         this.prevRotationYaw = this.rotationYaw;
         this.prevRotationPitch = this.rotationPitch;
      }

      BlockPos blockpos = new BlockPos(this.xTile, this.yTile, this.zTile);
      IBlockState iblockstate = this.world.getBlockState(blockpos);
      Block block = iblockstate.getBlock();
      if (iblockstate.getMaterial() != Material.AIR && !this.noClip) {
         AxisAlignedBB axisalignedbb = iblockstate.getCollisionBoundingBox(this.world, blockpos);
         if (axisalignedbb != Block.NULL_AABB && axisalignedbb.offset(blockpos).contains(new Vec3d(this.posX, this.posY, this.posZ))) {
            this.inGround = true;
         }
      }

      if (this.arrowShake > 0) {
         --this.arrowShake;
      }

      if (this.inGround && !this.noClip) {
         int j = block.getMetaFromState(iblockstate);
         if ((block != this.inTile || j != this.inData) && !this.world.collidesWithAnyBlock(this.getEntityBoundingBox().grow(0.05D))) {
            this.inGround = false;
            this.motionX *= (double)(this.rand.nextFloat() * 0.2F);
            this.motionY *= (double)(this.rand.nextFloat() * 0.2F);
            this.motionZ *= (double)(this.rand.nextFloat() * 0.2F);
            this.ticksInGround = 0;
            this.ticksInAir = 0;
         } else {
            ++this.ticksInGround;
            if (this.ticksInGround >= 1200) {
               if (this.pickupStatus == PickupStatus.ALLOWED) {
                  this.dropAsItem();
               }

               this.setDead();
            }
         }

         ++this.timeInGround;
      } else {
         this.timeInGround = 0;
         ++this.ticksInAir;
         Vec3d vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
         Vec3d vec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
         RayTraceResult raytraceresult = this.world.rayTraceBlocks(vec3d1, vec3d, false, true, false);
         vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
         vec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
         boolean caught = false;
         if (raytraceresult != null) {
            vec3d = new Vec3d(raytraceresult.hitVec.x, raytraceresult.hitVec.y, raytraceresult.hitVec.z);
         }

         Entity entity = this.findEntityOnPath(vec3d1, vec3d);
         if (entity != null) {
            raytraceresult = new RayTraceResult(entity);
         }

         if (raytraceresult != null && raytraceresult.entityHit instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer)raytraceresult.entityHit;
            if (this.shootingEntity instanceof EntityPlayer) {
               if (this.canBeCaughtInMidair(this.shootingEntity, entityplayer)) {
                  caught = this.attemptCatch(entityplayer);
               }

               if (!((EntityPlayer)this.shootingEntity).canAttackPlayer(entityplayer)) {
                  raytraceresult = null;
               }
            }
         }

         if (raytraceresult != null && raytraceresult.typeOfHit != Type.MISS && !caught && !ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
            this.onHit(raytraceresult);
         }

         if (this.getIsCritical()) {
            for(int k = 0; k < 4; ++k) {
               this.world.spawnParticle(EnumParticleTypes.CRIT, this.posX + this.motionX * (double)k / 4.0D, this.posY + this.motionY * (double)k / 4.0D, this.posZ + this.motionZ * (double)k / 4.0D, -this.motionX, -this.motionY + 0.2D, -this.motionZ, new int[0]);
            }
         }

         this.posX += this.motionX;
         this.posY += this.motionY;
         this.posZ += this.motionZ;
         float f4 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         if (this.noClip) {
            this.rotationYaw = (float)(MathHelper.atan2(-this.motionX, -this.motionZ) * 57.29577951308232D);
         } else {
            this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * 57.29577951308232D);
         }

         for(this.rotationPitch = (float)(MathHelper.atan2(this.motionY, (double)f4) * 57.29577951308232D); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
            ;
         }

         while(this.rotationPitch - this.prevRotationPitch >= 180.0F) {
            this.prevRotationPitch += 360.0F;
         }

         while(this.rotationYaw - this.prevRotationYaw < -180.0F) {
            this.prevRotationYaw -= 360.0F;
         }

         while(this.rotationYaw - this.prevRotationYaw >= 180.0F) {
            this.prevRotationYaw += 360.0F;
         }

         this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
         this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
         float f1 = 0.99F;
         float f2 = 0.05F;
         if (this.isInWater()) {
            for(int i = 0; i < 4; ++i) {
               float f3 = 0.25F;
               this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * 0.25D, this.posY - this.motionY * 0.25D, this.posZ - this.motionZ * 0.25D, this.motionX, this.motionY, this.motionZ, new int[0]);
            }

            ItemStack weaponStack = this.getWeaponStack();
            f1 = EnchantmentHelper.getEnchantmentLevel(EnchantmentRegistrySW.THROWING_HYDRODYNAMIC, weaponStack) > 0 ? 0.99F : 0.6F;
         }

         if (this.isWet()) {
            this.extinguish();
         }

         if (!this.noClip) {
            this.motionX *= (double)f1;
            this.motionY *= (double)f1;
            this.motionZ *= (double)f1;
            if (!this.hasNoGravity()) {
               this.motionY -= 0.05000000074505806D;
            }
         }

         this.setPosition(this.posX, this.posY, this.posZ);
         this.doBlockCollisions();
      }

   }

   public void setKnockbackStrength(int knockback) {
      this.knockbackStrength = knockback;
   }

   public Entity getThrower() {
      return this.shootingEntity;
   }

   public void setThrower(Entity entity) {
      this.shootingEntity = entity;
   }

   public void onKillCommand() {
      if (this.pickupStatus == PickupStatus.ALLOWED) {
         this.dropAsItem();
      }

      super.onKillCommand();
   }

   public void writeSpawnData(ByteBuf buffer) {
   }

   public void readSpawnData(ByteBuf additionalData) {
   }
}
