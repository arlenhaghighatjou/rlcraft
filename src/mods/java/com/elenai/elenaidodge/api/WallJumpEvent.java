package com.elenai.elenaidodge.api;

import com.elenai.elenaidodge.util.ClientStorage;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;

public class WallJumpEvent extends Event {
   protected final Side side;
   protected final EntityPlayer player;
   protected double force;
   protected int cooldown;
   @Nullable
   protected WallJumpEvent.Direction direction;

   public WallJumpEvent(Side side, double force, EntityPlayer player, int cooldown, WallJumpEvent.Direction direction) {
      this.side = side;
      this.force = force;
      this.player = player;
      this.cooldown = cooldown;
      this.direction = direction;
   }

   public Side getSide() {
      return this.side;
   }

   public static enum Direction {
      NORTH,
      EAST,
      SOUTH,
      WEST;

      private Direction() {
      }
   }

   public static class RequestWallJumpEvent extends WallJumpEvent {
      public RequestWallJumpEvent() {
         super(Side.SERVER, 0.0D, Minecraft.getMinecraft().player, ClientStorage.cooldown, (WallJumpEvent.Direction)null);
      }
   }

   @Cancelable
   public static class ServerWallJumpEvent extends WallJumpEvent {
      public ServerWallJumpEvent(double force, EntityPlayer player, int cooldown) {
         super(Side.CLIENT, force, player, cooldown, (WallJumpEvent.Direction)null);
      }

      public double getForce() {
         return this.force;
      }

      public void setForce(double force) {
         this.force = force;
      }

      public EntityPlayer getPlayer() {
         return this.player;
      }

      public int getCooldown() {
         return this.cooldown;
      }

      public void setCooldown(int cooldown) {
         this.cooldown = cooldown;
      }

      public WallJumpEvent.Direction getDirection() {
         return this.direction;
      }

      public void setDirection(WallJumpEvent.Direction direction) {
         this.direction = direction;
      }
   }
}
