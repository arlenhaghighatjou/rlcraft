package boni.dummy;

import boni.dummy.network.DamageMessage;
import boni.dummy.network.SyncEquipmentMessage;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.EntityEquipmentSlot.Type;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityDummy extends EntityLiving implements IEntityAdditionalSpawnData {
   private float customRotation;
   public float shake;
   public float shakeAnimation;
   public float lastDamage;
   public int lastDamageTick;
   public int firstDamageTick;
   public float damageTaken;
   public EntityFloatingNumber myLittleNumber;

   public EntityDummy(World world) {
      super(world);

      for(int i = 0; i < this.inventoryArmorDropChances.length; ++i) {
         this.inventoryArmorDropChances[i] = 1.1F;
      }

   }

   public void setPlacementRotation(Vec3d lookVector, int side) {
      int r = 0;
      if (side != 0 && side != 1) {
         if (side == 2) {
            r = 180;
         } else if (side == 3) {
            r = 0;
         } else if (side == 4) {
            r = 90;
         } else if (side == 5) {
            r = 270;
         }
      } else {
         r = (int)(Math.atan2(lookVector.z, lookVector.x) * 360.0D / 6.283185307179586D);
         r = r + 90;
      }

      this.customRotation = (float)r;
      this.setCustomRotationStuff();
   }

   private void setCustomRotationStuff() {
      float r = this.customRotation;
      this.prevRotationYawHead = this.rotationYawHead = r;
      this.prevRotationYaw = this.rotationYaw = r;
      this.rotationYaw = r;
      this.prevRenderYawOffset = this.renderYawOffset = r;
      this.randomYawVelocity = 0.0F;
   }

   protected boolean processInteract(EntityPlayer player, EnumHand hand) {
      ItemStack itemStack = player.getHeldItem(hand);
      if (itemStack.isEmpty()) {
         return this.removeArmor(player);
      } else {
         return this.equipArmor(player, itemStack);
      }
   }

   private boolean equipArmor(EntityPlayer player, ItemStack itemStack) {
      Item item = itemStack.getItem();

      for(EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
         if (slot.getSlotType() == Type.ARMOR && item.isValidArmor(itemStack, slot, player)) {
            ItemStack armor = this.getItemStackFromSlot(slot);
            if (!armor.isEmpty() && !this.getEntityWorld().isRemote) {
               this.entityDropItem(armor, 1.0F);
            }

            armor = itemStack.copy();
            armor.setCount(1);
            if (!this.getEntityWorld().isRemote) {
               TestDummyMod.proxy.network.sendToAllAround(new SyncEquipmentMessage(this.getEntityId(), slot.ordinal(), armor), new TargetPoint(this.dimension, this.posX, this.posY, this.posZ, 20.0D));
            }

            this.setItemStackToSlot(slot, armor);
            this.getAttributeMap().applyAttributeModifiers(armor.getAttributeModifiers(slot));
            itemStack.shrink(1);
            return true;
         }
      }

      return false;
   }

   private boolean removeArmor(EntityPlayer player) {
      if (!player.isSneaking()) {
         return false;
      } else {
         for(EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            if (slot.getSlotType() == Type.ARMOR) {
               ItemStack armor = this.getItemStackFromSlot(slot);
               if (!armor.isEmpty()) {
                  if (!this.getEntityWorld().isRemote) {
                     if (!player.capabilities.isCreativeMode) {
                        this.entityDropItem(armor, 1.0F);
                     }

                     TestDummyMod.proxy.network.sendToAllAround(new SyncEquipmentMessage(this.getEntityId(), slot.ordinal(), ItemStack.EMPTY), new TargetPoint(this.dimension, this.posX, this.posY, this.posZ, 20.0D));
                  }

                  this.setItemStackToSlot(slot, ItemStack.EMPTY);
                  this.getAttributeMap().removeAttributeModifiers(armor.getAttributeModifiers(slot));
                  return true;
               }
            }
         }

         return false;
      }
   }

   public void dismantle() {
      if (!this.getEntityWorld().isRemote) {
         this.dropEquipment(true, 999);
         this.dropItem(TestDummyMod.itemDummy, 1);
      }

      this.setDead();
   }

   public boolean attackEntityFrom(DamageSource source, float damage) {
      if (source.damageType.equals("player") && source.getTrueSource() != null) {
         EntityPlayer player = (EntityPlayer)source.getTrueSource();
         if (player.isSneaking() && player.getHeldItemMainhand().isEmpty()) {
            this.dismantle();
            return false;
         }
      }

      if ((float)this.hurtResistantTime > (float)this.maxHurtResistantTime / 2.0F) {
         if (damage <= this.lastDamage) {
            return false;
         }

         this.lastDamage = damage;
      } else {
         this.lastDamage = damage;
         this.hurtResistantTime = this.maxHurtResistantTime;
      }

      if (!this.isEntityInvulnerable(source) && !this.world.isRemote) {
         damage = ForgeHooks.onLivingHurt(this, source, damage);
         if (damage > 0.0F) {
            float var4 = this.applyArmorCalculations(source, damage);
            float var5 = this.applyPotionDamageCalculations(source, var4);
            float f1 = var5;
            damage = Math.max(var5 - this.getAbsorptionAmount(), 0.0F);
            this.setAbsorptionAmount(this.getAbsorptionAmount() - (f1 - damage));
         }
      }

      if (this.lastDamageTick == this.ticksExisted) {
         this.lastDamage += damage;
         this.shake += damage;
         this.shake = Math.min(this.shake, 30.0F);
      } else {
         this.shake = Math.min(damage, 30.0F);
         this.lastDamage = damage;
         this.lastDamageTick = this.ticksExisted;
      }

      this.hurtTime = this.maxHurtTime = 10;
      if (!this.getEntityWorld().isRemote) {
         if (this.myLittleNumber != null && !this.myLittleNumber.isDead) {
            this.myLittleNumber.setDead();
         }

         EntityFloatingNumber number = new EntityFloatingNumber(this.getEntityWorld(), damage, this.posX, this.posY + 2.0D, this.posZ);
         this.myLittleNumber = number;
         this.getEntityWorld().spawnEntity(number);
         TestDummyMod.proxy.network.sendToAllAround(new DamageMessage(this.lastDamage, this.shake, this, this.myLittleNumber), new TargetPoint(this.dimension, this.posX, this.posY, this.posZ, 20.0D));
         this.damageTaken += damage;
         if (this.firstDamageTick == 0) {
            this.firstDamageTick = this.ticksExisted;
         }
      }

      return true;
   }

   protected void updateAITasks() {
   }

   public void onUpdate() {
      if (this.shake > 0.0F) {
         ++this.shakeAnimation;
         this.shake -= 0.8F;
         if (this.shake <= 0.0F) {
            this.shakeAnimation = 0.0F;
            this.shake = 0.0F;
         }
      }

      if (this.hurtTime > 0) {
         --this.hurtTime;
      }

      if (this.hurtResistantTime > 0) {
         --this.hurtResistantTime;
      }

      if (this.getEntityWorld().isRemote) {
         this.extinguish();
      }

      if (!this.getEntityWorld().isRemote && this.damageTaken > 0.0F && this.ticksExisted - this.lastDamageTick > 30) {
         if (this.firstDamageTick < this.lastDamageTick) {
            float seconds = (float)(this.lastDamageTick - this.firstDamageTick) / 20.0F + 1.0F;
            float dps = this.damageTaken / seconds;
            EntityFloatingNumber number = new EntityDpsFloatingNumber(this.getEntityWorld(), dps, this.posX, this.posY + 3.0D, this.posZ);
            this.getEntityWorld().spawnEntity(number);
         }

         this.damageTaken = 0.0F;
         this.firstDamageTick = 0;
      }

   }

   public void onEntityUpdate() {
   }

   protected boolean isMovementBlocked() {
      return true;
   }

   public void onLivingUpdate() {
   }

   protected boolean canDespawn() {
      return false;
   }

   public boolean isEntityAlive() {
      return false;
   }

   public boolean canBePushed() {
      return false;
   }

   public boolean canBeCollidedWith() {
      return true;
   }

   public void writeSpawnData(ByteBuf buffer) {
      buffer.writeFloat(this.customRotation);
   }

   public void readSpawnData(ByteBuf additionalData) {
      this.customRotation = additionalData.readFloat();
      this.setCustomRotationStuff();
   }

   public void writeEntityToNBT(NBTTagCompound tag) {
      super.writeEntityToNBT(tag);
      tag.setFloat("customRotation", this.customRotation);
      tag.setFloat("shake", this.shake);
   }

   public void readEntityFromNBT(NBTTagCompound tag) {
      super.readEntityFromNBT(tag);
      this.customRotation = tag.getFloat("customRotation");
      this.shake = tag.getFloat("shake");
   }
}
