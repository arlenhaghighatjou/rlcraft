package com.yyon.grapplinghook.network;

import com.yyon.grapplinghook.GrappleConfig;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.nio.charset.StandardCharsets;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class LoggedInMessage implements IMessage {
   GrappleConfig.Config conf = null;

   public LoggedInMessage() {
   }

   public LoggedInMessage(GrappleConfig.Config serverconf) {
      this.conf = serverconf;
   }

   public void fromBytes(ByteBuf buf) {
      this.conf = new GrappleConfig.Config();
      this.conf.airstrafe_acceleration = buf.readDouble();
      this.conf.airstrafe_max_speed = buf.readDouble();
      this.conf.climb_speed = buf.readDouble();
      this.conf.default_angle = buf.readDouble();
      this.conf.default_attract = buf.readBoolean();
      this.conf.default_attractradius = buf.readDouble();
      this.conf.default_detachonkeyrelease = buf.readBoolean();
      this.conf.default_doublehook = buf.readBoolean();
      this.conf.default_durability = buf.readInt();
      this.conf.default_enderstaff = buf.readBoolean();
      this.conf.default_hookgravity = buf.readDouble();
      this.conf.default_maxlen = buf.readDouble();
      this.conf.default_motor = buf.readBoolean();
      this.conf.default_motoracceleration = buf.readDouble();
      this.conf.default_motordampener = buf.readBoolean();
      this.conf.default_motormaxspeed = buf.readDouble();
      this.conf.default_motorwhencrouching = buf.readBoolean();
      this.conf.default_motorwhennotcrouching = buf.readBoolean();
      this.conf.default_oneropepull = buf.readBoolean();
      this.conf.default_phaserope = buf.readBoolean();
      this.conf.default_playermovementmult = buf.readDouble();
      this.conf.default_pullbackwards = buf.readBoolean();
      this.conf.default_reelin = buf.readBoolean();
      this.conf.default_repel = buf.readBoolean();
      this.conf.default_repelforce = buf.readDouble();
      this.conf.default_rocket_active_time = buf.readDouble();
      this.conf.default_rocket_force = buf.readDouble();
      this.conf.default_rocket_refuel_ratio = buf.readDouble();
      this.conf.default_rocket_vertical_angle = buf.readDouble();
      this.conf.default_rocketenabled = buf.readBoolean();
      this.conf.default_smartdoublemotor = buf.readBoolean();
      this.conf.default_smartmotor = buf.readBoolean();
      this.conf.default_sneakingangle = buf.readDouble();
      this.conf.default_sneakingverticalthrowangle = buf.readDouble();
      this.conf.default_sticky = buf.readBoolean();
      this.conf.default_throwspeed = buf.readDouble();
      this.conf.default_verticalthrowangle = buf.readDouble();
      this.conf.dont_doublejump_if_falling_faster_than = buf.readDouble();
      this.conf.dont_override_movement_in_air = buf.readBoolean();
      this.conf.doublejump_relative_to_falling = buf.readBoolean();
      this.conf.doublejumpforce = buf.readDouble();
      this.conf.enable_angle = buf.readInt();
      this.conf.enable_attract = buf.readInt();
      this.conf.enable_attractradius = buf.readInt();
      this.conf.enable_detachonkeyrelease = buf.readInt();
      this.conf.enable_doublehook = buf.readInt();
      this.conf.enable_enderstaff = buf.readInt();
      this.conf.enable_hookgravity = buf.readInt();
      this.conf.enable_maxlen = buf.readInt();
      this.conf.enable_motor = buf.readInt();
      this.conf.enable_motoracceleration = buf.readInt();
      this.conf.enable_motordampener = buf.readInt();
      this.conf.enable_motormaxspeed = buf.readInt();
      this.conf.enable_motorwhencrouching = buf.readInt();
      this.conf.enable_motorwhennotcrouching = buf.readInt();
      this.conf.enable_oneropepull = buf.readInt();
      this.conf.enable_phaserope = buf.readInt();
      this.conf.enable_playermovementmult = buf.readInt();
      this.conf.enable_pullbackwards = buf.readInt();
      this.conf.enable_reelin = buf.readInt();
      this.conf.enable_repel = buf.readInt();
      this.conf.enable_repelforce = buf.readInt();
      this.conf.enable_rocket = buf.readInt();
      this.conf.enable_rocket_active_time = buf.readInt();
      this.conf.enable_rocket_force = buf.readInt();
      this.conf.enable_rocket_refuel_ratio = buf.readInt();
      this.conf.enable_rocket_vertical_angle = buf.readInt();
      this.conf.enable_smartdoublemotor = buf.readInt();
      this.conf.enable_smartmotor = buf.readInt();
      this.conf.enable_sneakingangle = buf.readInt();
      this.conf.enable_sneakingverticalthrowangle = buf.readInt();
      this.conf.enable_sticky = buf.readInt();
      this.conf.enable_throwspeed = buf.readInt();
      this.conf.enable_verticalthrowangle = buf.readInt();
      this.conf.enchant_rarity_double_jump = buf.readInt();
      this.conf.enchant_rarity_sliding = buf.readInt();
      this.conf.enchant_rarity_wallrun = buf.readInt();
      this.conf.ender_staff_recharge = buf.readInt();
      this.conf.ender_staff_strength = buf.readDouble();
      this.conf.grappleBreakBlocks = readString(buf);
      this.conf.grappleIgnoreBlocks = readString(buf);
      this.conf.grapplingBlocks = readString(buf);
      this.conf.grapplingNonBlocks = readString(buf);
      this.conf.hookaffectsentities = buf.readBoolean();
      this.conf.longfallbootsrecipe = buf.readBoolean();
      this.conf.max_angle = buf.readDouble();
      this.conf.max_attractradius = buf.readDouble();
      this.conf.max_hookgravity = buf.readDouble();
      this.conf.max_maxlen = buf.readDouble();
      this.conf.max_motoracceleration = buf.readDouble();
      this.conf.max_motormaxspeed = buf.readDouble();
      this.conf.max_playermovementmult = buf.readDouble();
      this.conf.max_repelforce = buf.readDouble();
      this.conf.max_rocket_active_time = buf.readDouble();
      this.conf.max_rocket_force = buf.readDouble();
      this.conf.max_rocket_refuel_ratio = buf.readDouble();
      this.conf.max_rocket_vertical_angle = buf.readDouble();
      this.conf.max_sneakingangle = buf.readDouble();
      this.conf.max_sneakingverticalthrowangle = buf.readDouble();
      this.conf.max_throwspeed = buf.readDouble();
      this.conf.max_upgrade_angle = buf.readDouble();
      this.conf.max_upgrade_attractradius = buf.readDouble();
      this.conf.max_upgrade_hookgravity = buf.readDouble();
      this.conf.max_upgrade_maxlen = buf.readDouble();
      this.conf.max_upgrade_motoracceleration = buf.readDouble();
      this.conf.max_upgrade_motormaxspeed = buf.readDouble();
      this.conf.max_upgrade_playermovementmult = buf.readDouble();
      this.conf.max_upgrade_repelforce = buf.readDouble();
      this.conf.max_upgrade_rocket_active_time = buf.readDouble();
      this.conf.max_upgrade_rocket_force = buf.readDouble();
      this.conf.max_upgrade_rocket_refuel_ratio = buf.readDouble();
      this.conf.max_upgrade_rocket_vertical_angle = buf.readDouble();
      this.conf.max_upgrade_sneakingangle = buf.readDouble();
      this.conf.max_upgrade_sneakingverticalthrowangle = buf.readDouble();
      this.conf.max_upgrade_throwspeed = buf.readDouble();
      this.conf.max_upgrade_verticalthrowangle = buf.readDouble();
      this.conf.max_verticalthrowangle = buf.readDouble();
      this.conf.max_wallrun_time = buf.readDouble();
      this.conf.min_hookgravity = buf.readDouble();
      this.conf.min_rocket_refuel_ratio = buf.readDouble();
      this.conf.min_upgrade_hookgravity = buf.readDouble();
      this.conf.min_upgrade_rocket_refuel_ratio = buf.readDouble();
      this.conf.override_allowflight = buf.readBoolean();
      this.conf.rope_jump_at_angle = buf.readBoolean();
      this.conf.rope_jump_cooldown_s = buf.readDouble();
      this.conf.rope_jump_power = buf.readDouble();
      this.conf.rope_snap_buffer = buf.readDouble();
      this.conf.sliding_end_min_speed = buf.readDouble();
      this.conf.sliding_friction = buf.readDouble();
      this.conf.sliding_min_speed = buf.readDouble();
      this.conf.slidingjumpforce = buf.readDouble();
      this.conf.upgraded_maxlen = buf.readDouble();
      this.conf.upgraded_throwspeed = buf.readDouble();
      this.conf.wall_jump_side = buf.readDouble();
      this.conf.wall_jump_up = buf.readDouble();
      this.conf.wallrun_drag = buf.readDouble();
      this.conf.wallrun_max_speed = buf.readDouble();
      this.conf.wallrun_min_speed = buf.readDouble();
      this.conf.wallrun_speed = buf.readDouble();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeDouble(this.conf.airstrafe_acceleration);
      buf.writeDouble(this.conf.airstrafe_max_speed);
      buf.writeDouble(this.conf.climb_speed);
      buf.writeDouble(this.conf.default_angle);
      buf.writeBoolean(this.conf.default_attract);
      buf.writeDouble(this.conf.default_attractradius);
      buf.writeBoolean(this.conf.default_detachonkeyrelease);
      buf.writeBoolean(this.conf.default_doublehook);
      buf.writeInt(this.conf.default_durability);
      buf.writeBoolean(this.conf.default_enderstaff);
      buf.writeDouble(this.conf.default_hookgravity);
      buf.writeDouble(this.conf.default_maxlen);
      buf.writeBoolean(this.conf.default_motor);
      buf.writeDouble(this.conf.default_motoracceleration);
      buf.writeBoolean(this.conf.default_motordampener);
      buf.writeDouble(this.conf.default_motormaxspeed);
      buf.writeBoolean(this.conf.default_motorwhencrouching);
      buf.writeBoolean(this.conf.default_motorwhennotcrouching);
      buf.writeBoolean(this.conf.default_oneropepull);
      buf.writeBoolean(this.conf.default_phaserope);
      buf.writeDouble(this.conf.default_playermovementmult);
      buf.writeBoolean(this.conf.default_pullbackwards);
      buf.writeBoolean(this.conf.default_reelin);
      buf.writeBoolean(this.conf.default_repel);
      buf.writeDouble(this.conf.default_repelforce);
      buf.writeDouble(this.conf.default_rocket_active_time);
      buf.writeDouble(this.conf.default_rocket_force);
      buf.writeDouble(this.conf.default_rocket_refuel_ratio);
      buf.writeDouble(this.conf.default_rocket_vertical_angle);
      buf.writeBoolean(this.conf.default_rocketenabled);
      buf.writeBoolean(this.conf.default_smartdoublemotor);
      buf.writeBoolean(this.conf.default_smartmotor);
      buf.writeDouble(this.conf.default_sneakingangle);
      buf.writeDouble(this.conf.default_sneakingverticalthrowangle);
      buf.writeBoolean(this.conf.default_sticky);
      buf.writeDouble(this.conf.default_throwspeed);
      buf.writeDouble(this.conf.default_verticalthrowangle);
      buf.writeDouble(this.conf.dont_doublejump_if_falling_faster_than);
      buf.writeBoolean(this.conf.dont_override_movement_in_air);
      buf.writeBoolean(this.conf.doublejump_relative_to_falling);
      buf.writeDouble(this.conf.doublejumpforce);
      buf.writeInt(this.conf.enable_angle);
      buf.writeInt(this.conf.enable_attract);
      buf.writeInt(this.conf.enable_attractradius);
      buf.writeInt(this.conf.enable_detachonkeyrelease);
      buf.writeInt(this.conf.enable_doublehook);
      buf.writeInt(this.conf.enable_enderstaff);
      buf.writeInt(this.conf.enable_hookgravity);
      buf.writeInt(this.conf.enable_maxlen);
      buf.writeInt(this.conf.enable_motor);
      buf.writeInt(this.conf.enable_motoracceleration);
      buf.writeInt(this.conf.enable_motordampener);
      buf.writeInt(this.conf.enable_motormaxspeed);
      buf.writeInt(this.conf.enable_motorwhencrouching);
      buf.writeInt(this.conf.enable_motorwhennotcrouching);
      buf.writeInt(this.conf.enable_oneropepull);
      buf.writeInt(this.conf.enable_phaserope);
      buf.writeInt(this.conf.enable_playermovementmult);
      buf.writeInt(this.conf.enable_pullbackwards);
      buf.writeInt(this.conf.enable_reelin);
      buf.writeInt(this.conf.enable_repel);
      buf.writeInt(this.conf.enable_repelforce);
      buf.writeInt(this.conf.enable_rocket);
      buf.writeInt(this.conf.enable_rocket_active_time);
      buf.writeInt(this.conf.enable_rocket_force);
      buf.writeInt(this.conf.enable_rocket_refuel_ratio);
      buf.writeInt(this.conf.enable_rocket_vertical_angle);
      buf.writeInt(this.conf.enable_smartdoublemotor);
      buf.writeInt(this.conf.enable_smartmotor);
      buf.writeInt(this.conf.enable_sneakingangle);
      buf.writeInt(this.conf.enable_sneakingverticalthrowangle);
      buf.writeInt(this.conf.enable_sticky);
      buf.writeInt(this.conf.enable_throwspeed);
      buf.writeInt(this.conf.enable_verticalthrowangle);
      buf.writeInt(this.conf.enchant_rarity_double_jump);
      buf.writeInt(this.conf.enchant_rarity_sliding);
      buf.writeInt(this.conf.enchant_rarity_wallrun);
      buf.writeInt(this.conf.ender_staff_recharge);
      buf.writeDouble(this.conf.ender_staff_strength);
      writeString(buf, this.conf.grappleBreakBlocks);
      writeString(buf, this.conf.grappleIgnoreBlocks);
      writeString(buf, this.conf.grapplingBlocks);
      writeString(buf, this.conf.grapplingNonBlocks);
      buf.writeBoolean(this.conf.hookaffectsentities);
      buf.writeBoolean(this.conf.longfallbootsrecipe);
      buf.writeDouble(this.conf.max_angle);
      buf.writeDouble(this.conf.max_attractradius);
      buf.writeDouble(this.conf.max_hookgravity);
      buf.writeDouble(this.conf.max_maxlen);
      buf.writeDouble(this.conf.max_motoracceleration);
      buf.writeDouble(this.conf.max_motormaxspeed);
      buf.writeDouble(this.conf.max_playermovementmult);
      buf.writeDouble(this.conf.max_repelforce);
      buf.writeDouble(this.conf.max_rocket_active_time);
      buf.writeDouble(this.conf.max_rocket_force);
      buf.writeDouble(this.conf.max_rocket_refuel_ratio);
      buf.writeDouble(this.conf.max_rocket_vertical_angle);
      buf.writeDouble(this.conf.max_sneakingangle);
      buf.writeDouble(this.conf.max_sneakingverticalthrowangle);
      buf.writeDouble(this.conf.max_throwspeed);
      buf.writeDouble(this.conf.max_upgrade_angle);
      buf.writeDouble(this.conf.max_upgrade_attractradius);
      buf.writeDouble(this.conf.max_upgrade_hookgravity);
      buf.writeDouble(this.conf.max_upgrade_maxlen);
      buf.writeDouble(this.conf.max_upgrade_motoracceleration);
      buf.writeDouble(this.conf.max_upgrade_motormaxspeed);
      buf.writeDouble(this.conf.max_upgrade_playermovementmult);
      buf.writeDouble(this.conf.max_upgrade_repelforce);
      buf.writeDouble(this.conf.max_upgrade_rocket_active_time);
      buf.writeDouble(this.conf.max_upgrade_rocket_force);
      buf.writeDouble(this.conf.max_upgrade_rocket_refuel_ratio);
      buf.writeDouble(this.conf.max_upgrade_rocket_vertical_angle);
      buf.writeDouble(this.conf.max_upgrade_sneakingangle);
      buf.writeDouble(this.conf.max_upgrade_sneakingverticalthrowangle);
      buf.writeDouble(this.conf.max_upgrade_throwspeed);
      buf.writeDouble(this.conf.max_upgrade_verticalthrowangle);
      buf.writeDouble(this.conf.max_verticalthrowangle);
      buf.writeDouble(this.conf.max_wallrun_time);
      buf.writeDouble(this.conf.min_hookgravity);
      buf.writeDouble(this.conf.min_rocket_refuel_ratio);
      buf.writeDouble(this.conf.min_upgrade_hookgravity);
      buf.writeDouble(this.conf.min_upgrade_rocket_refuel_ratio);
      buf.writeBoolean(this.conf.override_allowflight);
      buf.writeBoolean(this.conf.rope_jump_at_angle);
      buf.writeDouble(this.conf.rope_jump_cooldown_s);
      buf.writeDouble(this.conf.rope_jump_power);
      buf.writeDouble(this.conf.rope_snap_buffer);
      buf.writeDouble(this.conf.sliding_end_min_speed);
      buf.writeDouble(this.conf.sliding_friction);
      buf.writeDouble(this.conf.sliding_min_speed);
      buf.writeDouble(this.conf.slidingjumpforce);
      buf.writeDouble(this.conf.upgraded_maxlen);
      buf.writeDouble(this.conf.upgraded_throwspeed);
      buf.writeDouble(this.conf.wall_jump_side);
      buf.writeDouble(this.conf.wall_jump_up);
      buf.writeDouble(this.conf.wallrun_drag);
      buf.writeDouble(this.conf.wallrun_max_speed);
      buf.writeDouble(this.conf.wallrun_min_speed);
      buf.writeDouble(this.conf.wallrun_speed);
   }

   private static String readString(ByteBuf buf) {
      byte[] data = new byte[buf.readInt()];
      buf.readBytes(data);
      return new String(data, StandardCharsets.UTF_8);
   }

   private static void writeString(ByteBuf buf, String str) {
      byte[] data = str.getBytes(StandardCharsets.UTF_8);
      buf.writeInt(data.length);
      buf.writeBytes(data);
   }

   public static class Handler implements IMessageHandler<LoggedInMessage, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(LoggedInMessage message, MessageContext ctx) {
         IThreadListener mainThread = Minecraft.getMinecraft();
         mainThread.addScheduledTask(new LoggedInMessage.Handler.runner(message, ctx));
         return null;
      }

      public class runner implements Runnable {
         LoggedInMessage message;
         MessageContext ctx;

         public runner(LoggedInMessage message, MessageContext ctx) {
            this.message = message;
            this.ctx = ctx;
         }

         public void run() {
            GrappleConfig.setserveroptions(this.message.conf);
         }
      }
   }
}
