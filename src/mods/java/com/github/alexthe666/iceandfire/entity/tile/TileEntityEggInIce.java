package com.github.alexthe666.iceandfire.entity.tile;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityDragonEgg;
import com.github.alexthe666.iceandfire.entity.EntityIceDragon;
import com.github.alexthe666.iceandfire.enums.EnumDragonEgg;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class TileEntityEggInIce extends TileEntity implements ITickable {
   public EnumDragonEgg type;
   public int age;
   public int ticksExisted;
   @Nullable
   public UUID ownerUUID;

   public TileEntityEggInIce() {
   }

   public NBTTagCompound writeToNBT(NBTTagCompound tag) {
      if (this.type != null) {
         tag.setByte("Color", (byte)this.type.ordinal());
      } else {
         tag.setByte("Color", (byte)0);
      }

      tag.setByte("Age", (byte)this.age);
      if (this.ownerUUID == null) {
         tag.setString("OwnerUUID", "");
      } else {
         tag.setString("OwnerUUID", this.ownerUUID.toString());
      }

      return super.writeToNBT(tag);
   }

   public void readFromNBT(NBTTagCompound tag) {
      super.readFromNBT(tag);
      this.type = EnumDragonEgg.values()[tag.getByte("Color")];
      this.age = tag.getByte("Age");
      String s;
      if (tag.hasKey("OwnerUUID", 8)) {
         s = tag.getString("OwnerUUID");
      } else {
         String s1 = tag.getString("Owner");
         s = PreYggdrasilConverter.convertMobOwnerIfNeeded(this.world.getMinecraftServer(), s1);
      }

      if (!s.isEmpty()) {
         this.ownerUUID = UUID.fromString(s);
      }

   }

   public SPacketUpdateTileEntity getUpdatePacket() {
      NBTTagCompound tag = new NBTTagCompound();
      this.writeToNBT(tag);
      return new SPacketUpdateTileEntity(this.pos, 1, tag);
   }

   public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
      this.readFromNBT(packet.getNbtCompound());
   }

   public void spawnEgg() {
      if (this.type != null) {
         EntityDragonEgg egg = new EntityDragonEgg(this.world);
         egg.setType(this.type);
         egg.setPosition((double)this.pos.getX() + 0.5D, (double)(this.pos.getY() + 1), (double)this.pos.getZ() + 0.5D);
         egg.setOwnerId(this.ownerUUID);
         if (!this.world.isRemote) {
            this.world.spawnEntity(egg);
         }
      }

   }

   public void update() {
      ++this.age;
      if (this.age >= IceAndFire.CONFIG.dragonEggTime && this.type != null) {
         this.world.destroyBlock(this.pos, false);
         this.world.setBlockState(this.pos, Blocks.WATER.getDefaultState());
         EntityIceDragon dragon = new EntityIceDragon(this.world);
         dragon.setPosition((double)this.pos.getX() + 0.5D, (double)(this.pos.getY() + 1), (double)this.pos.getZ() + 0.5D);
         dragon.setVariant(this.type.ordinal() - 4);
         dragon.setGender((new Random()).nextBoolean());
         dragon.setTamed(true);
         dragon.setHunger(50);
         dragon.setOwnerId(this.ownerUUID);
         if (!this.world.isRemote) {
            this.world.spawnEntity(dragon);
         }
      }

      ++this.ticksExisted;
   }
}
