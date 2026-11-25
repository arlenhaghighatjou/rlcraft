package com.elenai.elenaidodge.event;

import com.elenai.elenaidodge.ModConfig;
import com.elenai.elenaidodge.api.LedgeGrabEvent;
import com.elenai.elenaidodge.api.WallJumpEvent;
import com.elenai.elenaidodge.capability.walljumpcooldown.IWallJumpCooldown;
import com.elenai.elenaidodge.capability.walljumpcooldown.WallJumpCooldownProvider;
import com.elenai.elenaidodge.capability.walljumps.IWallJumps;
import com.elenai.elenaidodge.capability.walljumps.WallJumpsProvider;
import com.elenai.elenaidodge.init.PotionInit;
import com.elenai.elenaidodge.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ServerWallJumpEventListener {
   public ServerWallJumpEventListener() {
   }

   @SubscribeEvent
   public void onServerWallJump(WallJumpEvent.ServerWallJumpEvent event) {
      EntityPlayer player = event.getPlayer();
      Double rayLength = new Double(0.4D + ModConfig.common.wallJump.distance);
      Vec3d playerRotation = Utils.getVectorForHorizontalRotation(event.getPlayer().rotationYawHead);
      Vec3d inverseRotation = playerRotation.scale(-1.0D);
      Vec3d rayPath = inverseRotation.scale(rayLength.doubleValue());
      Vec3d from = player.getPositionVector().addVector(0.0D, 0.6D, 0.0D);
      Vec3d to = from.add(rayPath);
      Vec3d playerRotationLeft = Utils.getVectorForHorizontalRotation(event.getPlayer().rotationYawHead + (float)ModConfig.common.wallJump.advanced.angle);
      Vec3d inverseRotationLeft = playerRotationLeft.scale(-1.0D);
      Vec3d rayPathLeft = inverseRotationLeft.scale(rayLength.doubleValue() * ModConfig.common.wallJump.advanced.angleLength);
      Vec3d fromLeft = player.getPositionVector().addVector(0.0D, 0.2D, 0.0D);
      Vec3d toLeft = fromLeft.add(rayPathLeft);
      Vec3d playerRotationRight = Utils.getVectorForHorizontalRotation(event.getPlayer().rotationYawHead - (float)ModConfig.common.wallJump.advanced.angle);
      Vec3d inverseRotationRight = playerRotationRight.scale(-1.0D);
      Vec3d rayPathRight = inverseRotationRight.scale(rayLength.doubleValue() * ModConfig.common.wallJump.advanced.angleLength);
      Vec3d fromRight = player.getPositionVector().addVector(0.0D, 0.2D, 0.0D);
      Vec3d toRight = fromLeft.add(rayPathRight);
      RayTraceResult ctx = player.world.rayTraceBlocks(from, to);
      RayTraceResult ctxLeft = player.world.rayTraceBlocks(fromLeft, toLeft);
      RayTraceResult ctxRight = player.world.rayTraceBlocks(fromRight, toRight);
      if (ctx != null && player.world.getBlockState(ctx.getBlockPos()).getMaterial().isSolid()) {
         event.setDirection(WallJumpEvent.Direction.valueOf(ctx.sideHit.getName().toUpperCase()));
      } else if (ctxLeft != null && player.world.getBlockState(ctxLeft.getBlockPos()).getMaterial().isSolid()) {
         event.setDirection(WallJumpEvent.Direction.valueOf(ctxLeft.sideHit.getName().toUpperCase()));
      } else if (ctxRight != null && player.world.getBlockState(ctxRight.getBlockPos()).getMaterial().isSolid()) {
         event.setDirection(WallJumpEvent.Direction.valueOf(ctxRight.sideHit.getName().toUpperCase()));
      } else {
         LedgeGrabEvent.ServerLedgeGrabEvent lgEvent = new LedgeGrabEvent.ServerLedgeGrabEvent(ModConfig.common.ledgeGrab.forwardsForce, player, 0, LedgeGrabEvent.Direction.valueOf(player.getHorizontalFacing().getName().toUpperCase()));
         if (!MinecraftForge.EVENT_BUS.post(lgEvent)) {
            Utils.handleLedgeGrab(lgEvent, (EntityPlayerMP)player);
         }

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

      if (!ModConfig.common.wallJump.enable && !Loader.isModLoaded("reskillable") || !Utils.wallJumpTraitUnlocked(event.getPlayer())) {
         event.setCanceled(!player.isPotionActive(PotionInit.NIMBLE_EFFECT));
      }

      if (event.getDirection() != null) {
         switch(event.getDirection()) {
         case NORTH:
            if (playerRotation.z > 0.0D - ModConfig.common.wallJump.angle) {
               event.setCanceled(true);
            }
            break;
         case EAST:
            if (playerRotation.x < 0.0D + ModConfig.common.wallJump.angle) {
               event.setCanceled(true);
            }
            break;
         case SOUTH:
            if (playerRotation.z < 0.0D + ModConfig.common.wallJump.angle) {
               event.setCanceled(true);
            }
            break;
         case WEST:
            if (playerRotation.x > 0.0D - ModConfig.common.wallJump.angle) {
               event.setCanceled(true);
            }
         }

         IWallJumps w = (IWallJumps)player.getCapability(WallJumpsProvider.WALLJUMPS_CAP, (EnumFacing)null);
         if (w.getWallJumps() + 1 > ModConfig.common.wallJump.maximum && ModConfig.common.wallJump.maximum != 0) {
            event.setCanceled(true);
         }

         if (player.getFoodStats().getFoodLevel() <= ModConfig.common.wallJump.hunger) {
            event.setCanceled(true);
         }

         if (!ModConfig.common.wallJump.falling && player.chasingPosY > player.posY) {
            event.setCanceled(true);
         }

         if (player.isCreative() || player.isSpectator()) {
            event.setCanceled(true);
         }

         if ((double)player.fallDistance > ModConfig.common.wallJump.fallDistance && ModConfig.common.wallJump.fallDistance != 0.0D) {
            event.setCanceled(true);
         }

         if (player.isRiding()) {
            event.setCanceled(true);
         }

         IWallJumpCooldown wjc = (IWallJumpCooldown)player.getCapability(WallJumpCooldownProvider.WALLJUMPCOOLDOWN_CAP, (EnumFacing)null);
         if (wjc.getWallJumps() > 0) {
            event.setCanceled(true);
         }

         if (!ModConfig.common.wallJump.oneBlock && event.getPlayer().world.getBlockState(event.getPlayer().getPosition().offset(EnumFacing.byName(event.getDirection().name().toUpperCase()))).getMaterial().blocksMovement()) {
            event.setCanceled(true);
         }
      } else {
         event.setCanceled(true);
      }

   }
}
