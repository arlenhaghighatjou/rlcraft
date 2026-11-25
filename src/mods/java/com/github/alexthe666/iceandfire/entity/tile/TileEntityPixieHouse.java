package com.github.alexthe666.iceandfire.entity.tile;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityPixie;
import com.github.alexthe666.iceandfire.message.MessageUpdatePixieHouse;
import com.github.alexthe666.iceandfire.message.MessageUpdatePixieHouseModel;
import java.util.Random;
import java.util.UUID;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;

public class TileEntityPixieHouse extends TileEntity implements ITickable {
   private static final float PARTICLE_WIDTH = 0.3F;
   private static final float PARTICLE_HEIGHT = 0.6F;
   public int houseType;
   public boolean hasPixie;
   public boolean tamedPixie;
   public UUID pixieOwnerUUID;
   public int pixieType;
   public int ticksExisted;
   public NonNullList<ItemStack> pixieItems = NonNullList.withSize(1, ItemStack.EMPTY);
   private Random rand = new Random();

   public TileEntityPixieHouse() {
   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      compound.setInteger("HouseType", this.houseType);
      compound.setBoolean("HasPixie", this.hasPixie);
      compound.setInteger("PixieType", this.pixieType);
      compound.setBoolean("TamedPixie", this.tamedPixie);
      if (this.pixieOwnerUUID != null) {
         compound.setUniqueId("PixieOwnerUUID", this.pixieOwnerUUID);
      }

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
         IceAndFire.NETWORK_WRAPPER.sendToAll(new MessageUpdatePixieHouseModel(this.pos.toLong(), packet.getNbtCompound().getInteger("HouseType")));
      }

   }

   public void readFromNBT(NBTTagCompound compound) {
      this.houseType = compound.getInteger("HouseType");
      this.hasPixie = compound.getBoolean("HasPixie");
      this.pixieType = compound.getInteger("PixieType");
      this.tamedPixie = compound.getBoolean("TamedPixie");
      this.pixieOwnerUUID = compound.getUniqueId("TicksExisted");
      this.pixieItems = NonNullList.withSize(1, ItemStack.EMPTY);
      ItemStackHelper.loadAllItems(compound, this.pixieItems);
      super.readFromNBT(compound);
   }

   public void update() {
      ++this.ticksExisted;
      if (!this.world.isRemote && this.hasPixie && (new Random()).nextInt(100) == 0) {
         this.releasePixie();
      }

      if (this.hasPixie) {
         IceAndFire.PROXY.spawnParticle("if_pixie", this.world, (double)((float)this.pos.getX() + 0.5F) + (double)(this.rand.nextFloat() * 0.3F * 2.0F) - 0.30000001192092896D, (double)this.pos.getY() + (double)(this.rand.nextFloat() * 0.6F), (double)((float)this.pos.getZ() + 0.5F) + (double)(this.rand.nextFloat() * 0.3F * 2.0F) - 0.30000001192092896D, (double)EntityPixie.PARTICLE_RGB[this.pixieType][0], (double)EntityPixie.PARTICLE_RGB[this.pixieType][1], (double)EntityPixie.PARTICLE_RGB[this.pixieType][2]);
      }

   }

   public void releasePixie() {
      EntityPixie pixie = new EntityPixie(this.world);
      pixie.setPositionAndRotation((double)((float)this.pos.getX() + 0.5F), (double)((float)this.pos.getY() + 1.0F), (double)((float)this.pos.getZ() + 0.5F), (float)(new Random()).nextInt(360), 0.0F);
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
}
