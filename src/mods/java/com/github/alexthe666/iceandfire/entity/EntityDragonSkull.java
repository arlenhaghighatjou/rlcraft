package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.core.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityDragonSkull extends EntityAnimal implements IBlacklistedFromStatues, IDeadMob {
   private static final DataParameter<Integer> DRAGON_TYPE = EntityDataManager.createKey(EntityDragonSkull.class, DataSerializers.VARINT);
   private static final DataParameter<Integer> DRAGON_AGE = EntityDataManager.createKey(EntityDragonSkull.class, DataSerializers.VARINT);
   private static final DataParameter<Integer> DRAGON_STAGE = EntityDataManager.createKey(EntityDragonSkull.class, DataSerializers.VARINT);
   private static final DataParameter<Float> DRAGON_DIRECTION = EntityDataManager.createKey(EntityDragonSkull.class, DataSerializers.FLOAT);
   public final float minSize = 0.3F;
   public final float maxSize = 8.58F;

   public EntityDragonSkull(World worldIn) {
      super(worldIn);
      this.setSize(0.9F, 0.65F);
      this.ignoreFrustumCheck = true;
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
   }

   public boolean isEntityInvulnerable(DamageSource i) {
      return i.getTrueSource() != null;
   }

   public boolean isAIDisabled() {
      return true;
   }

   public boolean isOnWall() {
      return this.world.isAirBlock(this.getPosition().down());
   }

   public void onUpdate() {
      this.prevRenderYawOffset = 0.0F;
      this.prevRotationYawHead = 0.0F;
      this.renderYawOffset = 0.0F;
      this.rotationYawHead = 0.0F;
   }

   protected void entityInit() {
      super.entityInit();
      this.getDataManager().register(DRAGON_TYPE, Integer.valueOf(0));
      this.getDataManager().register(DRAGON_AGE, Integer.valueOf(0));
      this.getDataManager().register(DRAGON_STAGE, Integer.valueOf(0));
      this.getDataManager().register(DRAGON_DIRECTION, Float.valueOf(0.0F));
   }

   public float getYaw() {
      return ((Float)this.getDataManager().get(DRAGON_DIRECTION)).floatValue();
   }

   public void setYaw(float var1) {
      this.getDataManager().set(DRAGON_DIRECTION, Float.valueOf(var1));
   }

   public int getType() {
      return ((Integer)this.getDataManager().get(DRAGON_TYPE)).intValue();
   }

   public void setType(int var1) {
      this.getDataManager().set(DRAGON_TYPE, Integer.valueOf(var1));
   }

   public int getStage() {
      return ((Integer)this.getDataManager().get(DRAGON_STAGE)).intValue();
   }

   public void setStage(int var1) {
      this.getDataManager().set(DRAGON_STAGE, Integer.valueOf(var1));
   }

   public int getDragonAge() {
      return ((Integer)this.getDataManager().get(DRAGON_AGE)).intValue();
   }

   public void setDragonAge(int var1) {
      this.getDataManager().set(DRAGON_AGE, Integer.valueOf(var1));
   }

   public SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return null;
   }

   public boolean attackEntityFrom(DamageSource var1, float var2) {
      this.turnIntoItem();
      return super.attackEntityFrom(var1, var2);
   }

   public void turnIntoItem() {
      if (!this.isDead) {
         this.setDead();
         ItemStack stack = new ItemStack(ModItems.dragon_skull, 1, this.getType());
         stack.setTagCompound(new NBTTagCompound());
         stack.getTagCompound().setInteger("Stage", this.getStage());
         stack.getTagCompound().setInteger("DragonAge", this.getDragonAge());
         if (!this.world.isRemote) {
            this.entityDropItem(stack, 0.0F);
         }

      }
   }

   public boolean processInteract(EntityPlayer player, EnumHand hand) {
      if (player.isSneaking()) {
         this.setYaw(player.rotationYaw);
      }

      return super.processInteract(player, hand);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      this.setType(compound.getInteger("Type"));
      this.setStage(compound.getInteger("Stage"));
      this.setDragonAge(compound.getInteger("DragonAge"));
      this.setYaw(compound.getFloat("DragonYaw"));
      super.readEntityFromNBT(compound);
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      compound.setInteger("Type", this.getType());
      compound.setInteger("Stage", this.getStage());
      compound.setInteger("DragonAge", this.getDragonAge());
      compound.setFloat("DragonYaw", this.getYaw());
      super.writeEntityToNBT(compound);
   }

   public float getDragonSize() {
      float step = -0.06624F;
      if (this.getDragonAge() > 125) {
         this.getClass();
         return 0.3F + step * 125.0F;
      } else {
         this.getClass();
         return 0.3F + step * (float)this.getDragonAge();
      }
   }

   protected boolean canDespawn() {
      return false;
   }

   public boolean canBePushed() {
      return false;
   }

   protected void collideWithEntity(Entity entity) {
   }

   public EntityAgeable createChild(EntityAgeable ageable) {
      return null;
   }

   public boolean canBeTurnedToStone() {
      return false;
   }

   public boolean isMobDead() {
      return true;
   }
}
