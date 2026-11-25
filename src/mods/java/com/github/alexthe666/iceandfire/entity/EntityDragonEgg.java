package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.block.BlockEggInIce;
import com.github.alexthe666.iceandfire.core.ModBlocks;
import com.github.alexthe666.iceandfire.core.ModItems;
import com.github.alexthe666.iceandfire.core.ModSounds;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityEggInIce;
import com.github.alexthe666.iceandfire.enums.EnumDragonEgg;
import com.google.common.base.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityDragonEgg extends EntityLiving implements IBlacklistedFromStatues, IDeadMob {
   private static final DataParameter<Integer> DRAGON_TYPE = EntityDataManager.createKey(EntityDragonEgg.class, DataSerializers.VARINT);
   private static final DataParameter<Integer> DRAGON_AGE = EntityDataManager.createKey(EntityDragonEgg.class, DataSerializers.VARINT);
   protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.createKey(EntityDragonEgg.class, DataSerializers.OPTIONAL_UNIQUE_ID);

   public EntityDragonEgg(World worldIn) {
      super(worldIn);
      this.isImmuneToFire = true;
      this.setSize(0.45F, 0.55F);
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
   }

   public void writeEntityToNBT(NBTTagCompound tag) {
      super.writeEntityToNBT(tag);
      tag.setInteger("Color", (byte)this.getType().ordinal());
      tag.setByte("DragonAge", (byte)this.getDragonAge());
      if (this.getOwnerId() == null) {
         tag.setString("OwnerUUID", "");
      } else {
         tag.setString("OwnerUUID", this.getOwnerId().toString());
      }

   }

   public void readEntityFromNBT(NBTTagCompound tag) {
      super.readEntityFromNBT(tag);
      this.setType(EnumDragonEgg.values()[tag.getInteger("Color")]);
      this.setDragonAge(tag.getByte("DragonAge"));
      String s;
      if (tag.hasKey("OwnerUUID", 8)) {
         s = tag.getString("OwnerUUID");
      } else {
         String s1 = tag.getString("Owner");
         s = PreYggdrasilConverter.convertMobOwnerIfNeeded(this.getServer(), s1);
      }

      if (!s.isEmpty()) {
         this.setOwnerId(UUID.fromString(s));
      }

   }

   protected void entityInit() {
      super.entityInit();
      this.getDataManager().register(DRAGON_TYPE, Integer.valueOf(0));
      this.getDataManager().register(DRAGON_AGE, Integer.valueOf(0));
      this.dataManager.register(OWNER_UNIQUE_ID, Optional.absent());
   }

   @Nullable
   public UUID getOwnerId() {
      return (UUID)((Optional)this.dataManager.get(OWNER_UNIQUE_ID)).orNull();
   }

   public void setOwnerId(@Nullable UUID p_184754_1_) {
      this.dataManager.set(OWNER_UNIQUE_ID, Optional.fromNullable(p_184754_1_));
   }

   public EnumDragonEgg getType() {
      return EnumDragonEgg.values()[((Integer)this.getDataManager().get(DRAGON_TYPE)).intValue()];
   }

   public void setType(EnumDragonEgg newtype) {
      this.getDataManager().set(DRAGON_TYPE, Integer.valueOf(newtype.ordinal()));
   }

   public boolean isEntityInvulnerable(DamageSource i) {
      return i.getTrueSource() != null;
   }

   public int getDragonAge() {
      return ((Integer)this.getDataManager().get(DRAGON_AGE)).intValue();
   }

   public void setDragonAge(int i) {
      this.getDataManager().set(DRAGON_AGE, Integer.valueOf(i));
   }

   public void onUpdate() {
      super.onUpdate();
      this.setAir(200);
      BlockPos pos = new BlockPos(this);
      if (this.world.getBlockState(pos).getMaterial() == Material.FIRE && this.getType().isFire) {
         this.setDragonAge(this.getDragonAge() + 1);
      }

      if (this.world.getBlockState(pos).getMaterial() == Material.WATER && !this.getType().isFire && this.getRNG().nextInt(500) == 0) {
         this.setDead();
         this.world.setBlockState(pos, ModBlocks.eggInIce.getDefaultState());
         this.world.playSound(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ, SoundEvents.BLOCK_GLASS_BREAK, this.getSoundCategory(), 2.5F, 1.0F, false);
         if (this.world.getBlockState(pos).getBlock() instanceof BlockEggInIce) {
            ((TileEntityEggInIce)this.world.getTileEntity(pos)).type = this.getType();
            ((TileEntityEggInIce)this.world.getTileEntity(pos)).ownerUUID = this.getOwnerId();
         }
      }

      if (this.getDragonAge() > IceAndFire.CONFIG.dragonEggTime && this.world.getBlockState(pos).getMaterial() == Material.FIRE && this.getType().isFire) {
         this.world.setBlockToAir(pos);
         EntityFireDragon dragon = new EntityFireDragon(this.world);
         dragon.setVariant(this.getType().ordinal());
         dragon.setGender(this.getRNG().nextBoolean());
         dragon.setPosition((double)pos.getX() + 0.5D, (double)(pos.getY() + 1), (double)pos.getZ() + 0.5D);
         dragon.setHunger(50);
         if (!this.world.isRemote) {
            this.world.spawnEntity(dragon);
         }

         dragon.setTamed(true);
         dragon.setOwnerId(this.getOwnerId());
         this.world.playSound(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ, SoundEvents.BLOCK_FIRE_EXTINGUISH, this.getSoundCategory(), 2.5F, 1.0F, false);
         this.world.playSound(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ, ModSounds.DRAGON_HATCH, this.getSoundCategory(), 2.5F, 1.0F, false);
         this.setDead();
      }

   }

   public boolean isAIDisabled() {
      return false;
   }

   public SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return null;
   }

   public boolean attackEntityFrom(DamageSource var1, float var2) {
      if (!this.world.isRemote && !var1.canHarmInCreative() && !this.isDead) {
         this.dropItem(this.getItem().getItem(), 1);
      }

      this.setDead();
      return super.attackEntityFrom(var1, var2);
   }

   private ItemStack getItem() {
      switch(this.getType().ordinal()) {
      case 1:
         return new ItemStack(ModItems.dragonegg_green);
      case 2:
         return new ItemStack(ModItems.dragonegg_bronze);
      case 3:
         return new ItemStack(ModItems.dragonegg_gray);
      case 4:
         return new ItemStack(ModItems.dragonegg_blue);
      case 5:
         return new ItemStack(ModItems.dragonegg_white);
      case 6:
         return new ItemStack(ModItems.dragonegg_sapphire);
      case 7:
         return new ItemStack(ModItems.dragonegg_silver);
      default:
         return new ItemStack(ModItems.dragonegg_red);
      }
   }

   public boolean canBePushed() {
      return false;
   }

   protected void collideWithEntity(Entity entity) {
   }

   public boolean canBeTurnedToStone() {
      return false;
   }

   public void onPlayerPlace(EntityPlayer player) {
      this.setOwnerId(player.getUniqueID());
   }

   public boolean isMobDead() {
      return true;
   }
}
