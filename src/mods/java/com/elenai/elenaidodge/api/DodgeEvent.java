package com.elenai.elenaidodge.api;

import com.elenai.elenaidodge.util.ClientStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;

public class DodgeEvent extends Event {
   protected final Side side;
   protected DodgeEvent.Direction direction;
   protected final EntityPlayer player;
   protected double force;
   protected int cooldown;

   public DodgeEvent(Side side, DodgeEvent.Direction direction, double force, EntityPlayer player, int cooldown) {
      this.side = side;
      this.direction = direction;
      this.force = force;
      this.player = player;
      this.cooldown = cooldown;
   }

   public Side getSide() {
      return this.side;
   }

   public DodgeEvent.Direction getDirection() {
      return this.direction;
   }

   public void setDirection(DodgeEvent.Direction direction) {
      this.direction = direction;
   }

   public static enum Direction {
      FORWARD,
      BACK,
      LEFT,
      RIGHT;

      private Direction() {
      }
   }

   public static class RequestDodgeEvent extends DodgeEvent {
      public RequestDodgeEvent(DodgeEvent.Direction direction) {
         super(Side.SERVER, direction, 0.0D, Minecraft.getMinecraft().player, ClientStorage.cooldown);
      }
   }

   @Cancelable
   public static class ServerDodgeEvent extends DodgeEvent {
      public ServerDodgeEvent(DodgeEvent.Direction direction, double force, EntityPlayer player, int cooldown) {
         super(Side.CLIENT, direction, force, player, cooldown);
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
   }
}
