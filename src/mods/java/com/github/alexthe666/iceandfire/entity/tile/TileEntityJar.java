package com.github.alexthe666.iceandfire.entity.tile;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.core.ModSounds;
import com.github.alexthe666.iceandfire.entity.EntityPixie;
import com.github.alexthe666.iceandfire.message.MessageUpdatePixieHouse;
import com.github.alexthe666.iceandfire.message.MessageUpdatePixieHouseModel;
import com.github.alexthe666.iceandfire.message.MessageUpdatePixieJar;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;

public class TileEntityJar extends TileEntity implements ITickable {
   private static final float PARTICLE_WIDTH = 0.3F;
   private static final float PARTICLE_HEIGHT = 0.6F;
   public boolean hasPixie;
   public boolean hasProduced;
   public boolean tamedPixie;
   public EaglercraftUUID pixieOwnerUUID;
   public int pixieType;
   public int ticksExisted;
   public NonNullList<ItemStack> pixieItems = NonNullList.withSize(1, ItemStack.EMPTY);
   public float rotationYaw;
   public float prevRotationYaw;
   private EaglercraftRandom rand = new EaglercraftRandom();

   public TileEntityJar() {
      this.hasPixie = true;
   }

   public TileEntityJar(boolean empty) {
      this.hasPixie = !empty;
   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      compound.setBoolean("HasPixie", this.hasPixie);
      compound.setInteger("PixieType", this.pixieType);
      compound.setBoolean("HasProduced", this.hasProduced);
      compound.setBoolean("TamedPixie", this.tamedPixie);
      if (this.pixieOwnerUUID != null) {
         compound.setUniqueId("PixieOwnerUUID", this.pixieOwnerUUID);
      }

      compound.setInteger("TicksExisted", this.ticksExisted);
      ItemStackHelper.saveAllItems(compound, this.pixieItems);
      return compound;
   }

   public SPacketUpdateTileEntity getUpdatePacket() {
      NBTTagCompound tag = new NBTTagCompound();
      this.writeToNBT(tag);
      return new SPacketUpdateTileEntity(this.pos, 1, tag);
   }

   public NBTTagCompound getUpdateTag() {
      return this.writeToNBT(new NBTTagCompound());
   }

   public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
      this.readFromNBT(packet.getNbtCompound());
      if (!this.world.isRemote) {
         IceAndFire.NETWORK_WRAPPER.sendToAll(new MessageUpdatePixieHouseModel(this.pos.toLong(), packet.getNbtCompound().getInteger("PixieType")));
      }

   }

   public void readFromNBT(NBTTagCompound compound) {
      this.hasPixie = compound.getBoolean("HasPixie");
      this.pixieType = compound.getInteger("PixieType");
      this.hasProduced = compound.getBoolean("HasProduced");
      this.ticksExisted = compound.getInteger("TicksExisted");
      this.tamedPixie = compound.getBoolean("TamedPixie");
      this.pixieOwnerUUID = compound.getUniqueId("PixieOwnerUUID");
      this.pixieItems = NonNullList.withSize(1, ItemStack.EMPTY);
      ItemStackHelper.loadAllItems(compound, this.pixieItems);
      super.readFromNBT(compound);
   }

   public void update() {
      ++this.ticksExisted;
      if (this.hasPixie) {
         IceAndFire.PROXY.spawnParticle("if_pixie", this.world, (double)((float)this.pos.getX() + 0.5F) + (double)(this.rand.nextFloat() * 0.3F * 2.0F) - 0.30000001192092896D, (double)this.pos.getY() + (double)(this.rand.nextFloat() * 0.6F), (double)((float)this.pos.getZ() + 0.5F) + (double)(this.rand.nextFloat() * 0.3F * 2.0F) - 0.30000001192092896D, (double)EntityPixie.PARTICLE_RGB[this.pixieType][0], (double)EntityPixie.PARTICLE_RGB[this.pixieType][1], (double)EntityPixie.PARTICLE_RGB[this.pixieType][2]);
      }

      if (this.ticksExisted % 24000 * 2 == 0 && !this.hasProduced && this.hasPixie && !this.getWorld().isRemote) {
         this.hasProduced = true;
         IceAndFire.NETWORK_WRAPPER.sendToAll(new MessageUpdatePixieJar(this.pos.toLong(), true));
      }

      this.prevRotationYaw = this.rotationYaw;
      if (this.rand.nextInt(30) == 0) {
         this.rotationYaw = this.rand.nextFloat() * 360.0F - 180.0F;
      }

      if (this.ticksExisted % 40 == 0 && this.rand.nextInt(6) == 0) {
         this.world.playSound((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D, ModSounds.PIXIE_IDLE, SoundCategory.NEUTRAL, 1.0F, 1.0F, false);
      }

   }

   public void releasePixie() {
      EntityPixie pixie = new EntityPixie(this.world);
      pixie.setPositionAndRotation((double)((float)this.pos.getX() + 0.5F), (double)((float)this.pos.getY() + 1.0F), (double)((float)this.pos.getZ() + 0.5F), (float)(new EaglercraftRandom()).nextInt(360), 0.0F);
      pixie.setHeldItem(EnumHand.MAIN_HAND, (ItemStack)this.pixieItems.get(0));
      pixie.setColor(this.pixieType);
      if (!this.world.isRemote) {
         this.world.spawnEntity(pixie);
      }

      this.hasPixie = false;
      this.pixieType = 0;
      pixie.ticksUntilHouseAI = 500;
      pixie.setTamed(this.tamedPixie);
      pixie.setOwnerId(this.pixieOwnerUUID);
      if (!this.world.isRemote) {
         IceAndFire.NETWORK_WRAPPER.sendToAll(new MessageUpdatePixieHouse(this.pos.toLong(), false, 0));
      }

   }

   private float updateRotation(float float1, float float2, float float3) {
      float f = MathHelper.wrapDegrees(float2 - float1);
      if (f > float3) {
         f = float3;
      }

      if (f < -float3) {
         f = -float3;
      }

      return float1 + f;
   }
}
