package com.elenai.elenaidodge.event;

import com.elenai.elenaidodge.ModConfig;
import com.elenai.elenaidodge.api.LedgeGrabEvent;
import com.elenai.elenaidodge.capability.ledgegrabcooldown.ILedgeGrabCooldown;
import com.elenai.elenaidodge.capability.ledgegrabcooldown.LedgeGrabCooldownProvider;
import com.elenai.elenaidodge.capability.ledgegrabs.ILedgeGrabs;
import com.elenai.elenaidodge.capability.ledgegrabs.LedgeGrabsProvider;
import com.elenai.elenaidodge.init.PotionInit;
import com.elenai.elenaidodge.util.Utils;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockWall;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ServerLedgeGrabEventListener {
   public ServerLedgeGrabEventListener() {
   }

   @SubscribeEvent
   public void onServerLedgeGrab(LedgeGrabEvent.ServerLedgeGrabEvent event) {
      EntityPlayer player = event.getPlayer();
      Double rayLength = new Double(0.4D + ModConfig.common.ledgeGrab.distance);
      Vec3d playerRotation = Utils.getVectorForHorizontalRotation(event.getPlayer().rotationYawHead);
      Vec3d PlayerRotationHorizontal = new Vec3d(playerRotation.x, 0.0D, playerRotation.z);
      Vec3d rayPath = PlayerRotationHorizontal.scale(rayLength.doubleValue());
      Vec3d from = player.getPositionVector().addVector(0.0D, (double)(player.getEyeHeight() - 1.0F) + ModConfig.common.ledgeGrab.extraHeight, 0.0D);
      Vec3d fromMid = player.getPositionVector().addVector(0.0D, 0.2D, 0.0D);
      Vec3d fromTall = player.getPositionVector().addVector(0.0D, 0.6D, 0.0D);
      Vec3d fromLow = player.getPositionVector().addVector(0.0D, -1.0D, 0.0D);
      Vec3d to = from.add(rayPath);
      Vec3d toTall = fromTall.add(rayPath);
      Vec3d toLow = fromLow.add(rayPath);
      Vec3d toMid = fromMid.add(rayPath);
      RayTraceResult ctx = player.world.rayTraceBlocks(from, to);
      RayTraceResult ctxTall = player.world.rayTraceBlocks(fromTall, toTall);
      RayTraceResult ctxLow = player.world.rayTraceBlocks(fromLow, toLow);
      RayTraceResult ctxMid = player.world.rayTraceBlocks(fromMid, toMid);
      if ((ctx == null || !player.world.getBlockState(ctx.getBlockPos()).getMaterial().isSolid()) && (ctxMid == null || !player.world.getBlockState(ctxMid.getBlockPos()).getMaterial().isSolid()) && (ctxTall == null || !player.world.getBlockState(ctxTall.getBlockPos()).getMaterial().isSolid()) && (ctxLow == null || !(player.world.getBlockState(ctxLow.getBlockPos()).getBlock() instanceof BlockFence)) && (ctxLow == null || !(player.world.getBlockState(ctxLow.getBlockPos()).getBlock() instanceof BlockWall)) && (ctxLow == null || !(player.world.getBlockState(ctxLow.getBlockPos()).getBlock() instanceof BlockFenceGate))) {
         event.setCanceled(true);
      }

      Vec3d fromPlayerHead = player.getPositionVector().addVector(0.0D, (double)player.getEyeHeight() - 0.4D + ModConfig.common.ledgeGrab.extraHeight, 0.0D);
      Vec3d toHead = fromPlayerHead.add(rayPath);
      RayTraceResult ctxHead = player.world.rayTraceBlocks(fromPlayerHead, toHead);
      if (ctxHead != null && player.world.getBlockState(ctxHead.getBlockPos()).getMaterial().isSolid()) {
         event.setCanceled(true);
      }

      Double floorRayLength = new Double(0.9D);
      Vec3d rayPathFloor = (new Vec3d(0.0D, -1.0D, 0.0D)).scale(floorRayLength.doubleValue());
      Vec3d fromPlayer = player.getPositionVector();
      Vec3d toFloor = fromPlayer.add(rayPathFloor);
      RayTraceResult ctxFloor = player.world.rayTraceBlocks(fromPlayer, toFloor);
      if (ctxFloor != null && player.world.getBlockState(ctxFloor.getBlockPos()).getMaterial().isSolid()) {
         event.setCanceled(true);
      }

      if (!ModConfig.common.ledgeGrab.enable && !Loader.isModLoaded("reskillable") || !Utils.ledgeGrabTraitUnlocked(event.getPlayer())) {
         event.setCanceled(!player.isPotionActive(PotionInit.CLIMBER_EFFECT));
      }

      ILedgeGrabCooldown lgc = (ILedgeGrabCooldown)player.getCapability(LedgeGrabCooldownProvider.LEDGEGRABCOOLDOWN_CAP, (EnumFacing)null);
      if (lgc.getLedgeGrabs() > 0) {
         event.setCanceled(true);
      }

      if (player.getFoodStats().getFoodLevel() <= ModConfig.common.ledgeGrab.hunger) {
         event.setCanceled(true);
      }

      switch(event.getDirection()) {
      case NORTH:
         if (playerRotation.z > 0.0D - ModConfig.common.ledgeGrab.angle) {
            event.setCanceled(true);
         }
         break;
      case EAST:
         if (playerRotation.x < 0.0D + ModConfig.common.ledgeGrab.angle) {
            event.setCanceled(true);
         }
         break;
      case SOUTH:
         if (playerRotation.z < 0.0D + ModConfig.common.ledgeGrab.angle) {
            event.setCanceled(true);
         }
         break;
      case WEST:
         if (playerRotation.x > 0.0D - ModConfig.common.ledgeGrab.angle) {
            event.setCanceled(true);
         }
      }

      if (player.isCreative() || player.isSpectator()) {
         event.setCanceled(true);
      }

      if (!ModConfig.common.ledgeGrab.falling && player.chasingPosY > player.posY) {
         event.setCanceled(true);
      }

      if ((double)player.fallDistance > ModConfig.common.ledgeGrab.fallDistance && ModConfig.common.ledgeGrab.fallDistance != 0.0D) {
         event.setCanceled(true);
      }

      ILedgeGrabs l = (ILedgeGrabs)player.getCapability(LedgeGrabsProvider.LEDGEGRABS_CAP, (EnumFacing)null);
      if (l.getLedgeGrabs() + 1 > ModConfig.common.ledgeGrab.maximum && ModConfig.common.ledgeGrab.maximum != 0) {
         event.setCanceled(true);
      }

      if (player.isRiding()) {
         event.setCanceled(true);
      }

   }
}
