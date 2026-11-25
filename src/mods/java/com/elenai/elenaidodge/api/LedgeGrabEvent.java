package com.elenai.elenaidodge.api;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;

public class LedgeGrabEvent extends Event {
   protected final Side side;
   protected final EntityPlayer player;
   protected double force;
   protected int cooldown;
   @Nullable
   protected LedgeGrabEvent.Direction direction;

   public LedgeGrabEvent(Side side, double force, EntityPlayer player, int cooldown, LedgeGrabEvent.Direction direction) {
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

   @Cancelable
   public static class ServerLedgeGrabEvent extends LedgeGrabEvent {
      public ServerLedgeGrabEvent(double force, EntityPlayer player, int cooldown, LedgeGrabEvent.Direction direction) {
         super(Side.CLIENT, force, player, cooldown, direction);
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

      public LedgeGrabEvent.Direction getDirection() {
         return this.direction;
      }

      public void setDirection(LedgeGrabEvent.Direction direction) {
         this.direction = direction;
      }
   }
}
