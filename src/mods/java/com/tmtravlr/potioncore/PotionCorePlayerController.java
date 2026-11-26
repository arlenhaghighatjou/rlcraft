package com.tmtravlr.potioncore;

import com.tmtravlr.potioncore.network.CToSMessage;
import net.lax1dude.eaglercraft.v1_8.netty.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameType;
import net.minecraftforge.common.ForgeHooks;

public class PotionCorePlayerController extends PlayerControllerMP {
   private final Minecraft mc;
   public NetHandlerPlayClient netHandlerVisible;

   public PotionCorePlayerController(Minecraft mcIn, NetHandlerPlayClient netHandler) {
      super(mcIn, netHandler);
      this.mc = mcIn;
      this.netHandlerVisible = netHandler;
   }

   public void attackEntity(EntityPlayer playerIn, Entity targetEntity) {
      if (!PotionCoreHelper.hasServer) {
         super.attackEntity(playerIn, targetEntity);
      } else {
         this.syncCurrentPlayItem();
         this.attackEntityCustom(playerIn, targetEntity);
         if (this.getCurrentGameType() != GameType.SPECTATOR) {
            playerIn.attackTargetEntityWithCurrentItem(targetEntity);
         }

      }
   }

   public EnumActionResult interactWithEntity(EntityPlayer player, Entity target, EnumHand hand) {
      if (!PotionCoreHelper.hasServer) {
         return super.interactWithEntity(player, target, hand);
      } else {
         this.syncCurrentPlayItem();
         this.interactEntityCustom(player, target, hand);
         return this.getCurrentGameType() == GameType.SPECTATOR ? EnumActionResult.PASS : player.interactOn(target, hand);
      }
   }

   public EnumActionResult interactWithEntity(EntityPlayer player, Entity target, RayTraceResult raytrace, EnumHand hand) {
      if (!PotionCoreHelper.hasServer) {
         return super.interactWithEntity(player, target, raytrace, hand);
      } else {
         this.syncCurrentPlayItem();
         Vec3d vec3d = new Vec3d(raytrace.hitVec.x - target.posX, raytrace.hitVec.y - target.posY, raytrace.hitVec.z - target.posZ);
         this.interactAtEntityCustom(player, target, hand, vec3d);
         if (this.getCurrentGameType() == GameType.SPECTATOR) {
            return EnumActionResult.PASS;
         } else {
            EnumActionResult cancelResult = ForgeHooks.onInteractEntityAt(player, target, raytrace, hand);
            if (cancelResult != null) {
               return cancelResult;
            } else {
               return this.getCurrentGameType() == GameType.SPECTATOR ? EnumActionResult.PASS : target.applyPlayerInteraction(player, vec3d, hand);
            }
         }
      }
   }

   public float getBlockReachDistance() {
      return (float)PotionCoreAttributes.getPlayerReachDistance(this.mc.player);
   }

   private void syncCurrentPlayItem() {
      int i = this.mc.player.inventory.currentItem;
      if (i != this.currentPlayerItem) {
         this.currentPlayerItem = i;
         this.netHandlerVisible.sendPacket(new CPacketHeldItemChange(i));
      }

   }

   private void attackEntityCustom(EntityPlayer player, Entity victim) {
      PacketBuffer out = new PacketBuffer(Unpooled.buffer());
      out.writeInt(2);
      out.writeLong(player.getUniqueID().getMostSignificantBits());
      out.writeLong(player.getUniqueID().getLeastSignificantBits());
      out.writeInt(victim.getEntityId());
      out.writeInt(3);
      CToSMessage packet = new CToSMessage(out);
      PotionCore.networkWrapper.sendToServer(packet);
   }

   private void interactEntityCustom(EntityPlayer player, Entity target, EnumHand hand) {
      PacketBuffer out = new PacketBuffer(Unpooled.buffer());
      out.writeInt(2);
      out.writeLong(player.getUniqueID().getMostSignificantBits());
      out.writeLong(player.getUniqueID().getLeastSignificantBits());
      out.writeInt(target.getEntityId());
      out.writeInt(4);
      out.writeEnumValue(hand);
      CToSMessage packet = new CToSMessage(out);
      PotionCore.networkWrapper.sendToServer(packet);
   }

   private void interactAtEntityCustom(EntityPlayer player, Entity target, EnumHand hand, Vec3d position) {
      PacketBuffer out = new PacketBuffer(Unpooled.buffer());
      out.writeInt(2);
      out.writeLong(player.getUniqueID().getMostSignificantBits());
      out.writeLong(player.getUniqueID().getLeastSignificantBits());
      out.writeInt(target.getEntityId());
      out.writeInt(5);
      out.writeEnumValue(hand);
      out.writeDouble(position.x);
      out.writeDouble(position.y);
      out.writeDouble(position.z);
      CToSMessage packet = new CToSMessage(out);
      PotionCore.networkWrapper.sendToServer(packet);
   }
}
