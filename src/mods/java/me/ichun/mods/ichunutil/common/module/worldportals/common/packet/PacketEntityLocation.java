package me.ichun.mods.ichunutil.common.module.worldportals.common.packet;

import io.netty.buffer.ByteBuf;
import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import me.ichun.mods.ichunutil.common.module.worldportals.common.WorldPortals;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;

public class PacketEntityLocation extends AbstractPacket {
   public int id;
   public double lX;
   public double lY;
   public double lZ;
   public double x;
   public double y;
   public double z;
   public float prevYaw;
   public float prevPitch;
   public float yaw;
   public float pitch;
   public double mX;
   public double mY;
   public double mZ;

   public PacketEntityLocation() {
   }

   public PacketEntityLocation(Entity ent) {
      this.id = ent.getEntityId();
      this.lX = ent.lastTickPosX;
      this.lY = ent.lastTickPosY;
      this.lZ = ent.lastTickPosZ;
      this.x = ent.posX;
      this.y = ent.posY;
      this.z = ent.posZ;
      this.prevYaw = ent.prevRotationYaw;
      this.prevPitch = ent.prevRotationPitch;
      this.yaw = ent.rotationYaw;
      this.pitch = ent.rotationPitch;
      this.mX = ent.motionX;
      this.mY = ent.motionY;
      this.mZ = ent.motionZ;
   }

   public PacketEntityLocation(int id, double lastX, double lastY, double lastZ, double x, double y, double z, float prevYaw, float prevPitch, float yaw, float pitch, double mX, double mY, double mZ) {
      this.id = id;
      this.lX = lastX;
      this.lY = lastY;
      this.lZ = lastZ;
      this.x = x;
      this.y = y;
      this.z = z;
      this.prevYaw = prevYaw;
      this.prevPitch = prevPitch;
      this.yaw = yaw;
      this.pitch = pitch;
      this.mX = mX;
      this.mY = mY;
      this.mZ = mZ;
   }

   public void writeTo(ByteBuf buf) {
      buf.writeInt(this.id);
      buf.writeDouble(this.lX);
      buf.writeDouble(this.lY);
      buf.writeDouble(this.lZ);
      buf.writeDouble(this.x);
      buf.writeDouble(this.y);
      buf.writeDouble(this.z);
      buf.writeFloat(this.prevYaw);
      buf.writeFloat(this.prevPitch);
      buf.writeFloat(this.yaw);
      buf.writeFloat(this.pitch);
      buf.writeDouble(this.mX);
      buf.writeDouble(this.mY);
      buf.writeDouble(this.mZ);
   }

   public void readFrom(ByteBuf buf) {
      this.id = buf.readInt();
      this.lX = buf.readDouble();
      this.lY = buf.readDouble();
      this.lZ = buf.readDouble();
      this.x = buf.readDouble();
      this.y = buf.readDouble();
      this.z = buf.readDouble();
      this.prevYaw = buf.readFloat();
      this.prevPitch = buf.readFloat();
      this.yaw = buf.readFloat();
      this.pitch = buf.readFloat();
      this.mX = buf.readDouble();
      this.mY = buf.readDouble();
      this.mZ = buf.readDouble();
   }

   public void execute(Side side, EntityPlayer player) {
      Entity ent = player.getEntityWorld().getEntityByID(this.id);
      if (ent != null && (!player.getEntityWorld().isRemote || player != ent)) {
         ent.setLocationAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
         ent.motionX = this.mX;
         ent.motionY = this.mY;
         ent.motionZ = this.mZ;
         if (!player.getEntityWorld().isRemote) {
            if (ent instanceof EntityPlayerMP && ent == player) {
               EntityPlayerMP ply = (EntityPlayerMP)ent;
               ply.connection.lastPositionUpdate = ply.connection.networkTickCount;
               ply.setPositionAndRotation(this.x, this.y, this.z, this.yaw, this.pitch);
               ply.motionX = this.mX;
               ply.motionY = this.mY;
               ply.motionZ = this.mZ;
            }

            WorldPortals.channel.sendToAllAround(new PacketEntityLocation(ent), new TargetPoint(ent.dimension, ent.posX, ent.posY, ent.posZ, 256.0D));
         } else {
            float yawDifference = 0.0F;
            float prevYawDifference = 0.0F;
            EntityLivingBase living = null;
            float riderYaw = 0.0F;
            float prevRiderYaw = 0.0F;
            if (ent instanceof EntityLivingBase) {
               living = (EntityLivingBase)ent;
               yawDifference = living.renderYawOffset - ent.rotationYaw;
               prevYawDifference = living.prevRenderYawOffset - ent.rotationYaw;
            }

            for(Entity passenger : ent.getPassengers()) {
               riderYaw = passenger.rotationYaw - ent.rotationYaw;
               prevRiderYaw = passenger.prevRotationYaw - ent.rotationYaw;
            }

            ent.lastTickPosX = ent.prevPosX = this.lX;
            ent.lastTickPosY = ent.prevPosY = this.lY;
            ent.lastTickPosZ = ent.prevPosZ = this.lZ;
            ent.posX = this.x;
            ent.posY = this.y;
            ent.posZ = this.z;
            ent.prevRotationYaw = this.prevYaw;
            ent.prevRotationPitch = this.prevPitch;
            ent.rotationYaw = this.yaw;
            ent.rotationPitch = this.pitch;
            ent.setPosition(ent.posX, ent.posY, ent.posZ);

            for(Entity passenger : ent.getPassengers()) {
               ent.updatePassenger(passenger);
               passenger.rotationYaw = passenger.prevRotationYaw = ent.rotationYaw;
               passenger.rotationYaw += riderYaw;
               passenger.prevRotationYaw += prevRiderYaw;
            }

            if (ent instanceof EntityLivingBase) {
               living.renderYawOffset = living.prevRenderYawOffset = ent.rotationYaw;
               living.renderYawOffset += yawDifference;
               living.prevRenderYawOffset += prevYawDifference;
            } else if (ent instanceof EntityArrow) {
               ((EntityArrow)ent).inGround = false;
            }
         }
      }

   }

   public Side receivingSide() {
      return null;
   }
}
