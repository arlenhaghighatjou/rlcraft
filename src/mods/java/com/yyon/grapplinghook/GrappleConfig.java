package com.yyon.grapplinghook;

import net.minecraftforge.common.config.ConfigEntry;
import net.minecraftforge.common.config.ConfigManager;

@net.minecraftforge.common.config.Config(
   modid = "grapplemod",
   name = "grappling_hook",
   category = ""
)
public class GrappleConfig {
   public static GrappleConfig.Config options = new GrappleConfig.Config();
   private static GrappleConfig.Config server_options = null;
   public static GrappleConfig.ClientConfig client_options = new GrappleConfig.ClientConfig();

   public GrappleConfig() {
   }

   public static GrappleConfig.Config getconf() {
      return server_options == null ? options : server_options;
   }

   public static void setserveroptions(GrappleConfig.Config newserveroptions) {
      server_options = newserveroptions;
   }

   public static class ClientConfig {
      public double wallrun_sound_effect_time_s = 0.35D;
      public float wallrun_camera_tilt_degrees = 10.0F;
      public float wallrun_camera_animation_s = 0.5F;
      public float wallrun_sound_volume = 1.0F;
      public float doublejump_sound_volume = 1.0F;
      public float slide_sound_volume = 1.0F;
      public float wallrunjump_sound_volume = 1.0F;
      public float enderstaff_sound_volume = 1.0F;
      public float rocket_sound_volume = 1.0F;

      public ClientConfig() {
      }
   }

   public static class Config {
      public double default_maxlen = 30.0D;
      public boolean default_phaserope = false;
      public boolean default_sticky = false;
      public double default_hookgravity = 1.0D;
      public double default_throwspeed = 2.0D;
      public boolean default_reelin = true;
      public double default_verticalthrowangle = 0.0D;
      public double default_sneakingverticalthrowangle = 0.0D;
      public boolean default_detachonkeyrelease = false;
      public boolean default_motor = false;
      public double default_motormaxspeed = 4.0D;
      public double default_motoracceleration = 0.2D;
      public boolean default_motorwhencrouching = false;
      public boolean default_motorwhennotcrouching = true;
      public boolean default_smartmotor = false;
      public boolean default_motordampener = false;
      public boolean default_pullbackwards = true;
      public double default_playermovementmult = 1.0D;
      public boolean default_enderstaff = false;
      public boolean default_repel = false;
      public double default_repelforce = 1.0D;
      public boolean default_attract = false;
      public double default_attractradius = 3.0D;
      public boolean default_doublehook = false;
      public boolean default_smartdoublemotor = true;
      public double default_angle = 20.0D;
      public double default_sneakingangle = 10.0D;
      public boolean default_oneropepull = false;
      public boolean default_rocketenabled = false;
      public double default_rocket_force = 1.0D;
      public double default_rocket_active_time = 0.5D;
      public double default_rocket_refuel_ratio = 15.0D;
      public double default_rocket_vertical_angle = 0.0D;
      public double upgraded_throwspeed = 3.5D;
      public double upgraded_maxlen = 60.0D;
      public double max_maxlen = 60.0D;
      public double max_hookgravity = 100.0D;
      public double max_throwspeed = 5.0D;
      public double max_verticalthrowangle = 45.0D;
      public double max_sneakingverticalthrowangle = 45.0D;
      public double max_motormaxspeed = 4.0D;
      public double max_motoracceleration = 0.2D;
      public double max_playermovementmult = 2.0D;
      public double max_repelforce = 1.0D;
      public double max_attractradius = 3.0D;
      public double max_angle = 45.0D;
      public double max_sneakingangle = 45.0D;
      public double max_upgrade_maxlen = 200.0D;
      public double max_upgrade_hookgravity = 100.0D;
      public double max_upgrade_throwspeed = 20.0D;
      public double max_upgrade_verticalthrowangle = 90.0D;
      public double max_upgrade_sneakingverticalthrowangle = 90.0D;
      public double max_upgrade_motormaxspeed = 10.0D;
      public double max_upgrade_motoracceleration = 1.0D;
      public double max_upgrade_playermovementmult = 5.0D;
      public double max_upgrade_repelforce = 5.0D;
      public double max_upgrade_attractradius = 10.0D;
      public double max_upgrade_angle = 90.0D;
      public double max_upgrade_sneakingangle = 90.0D;
      public double max_rocket_active_time = 0.5D;
      public double max_upgrade_rocket_active_time = 20.0D;
      public double max_rocket_force = 1.0D;
      public double max_upgrade_rocket_force = 5.0D;
      public double min_rocket_refuel_ratio = 15.0D;
      public double min_upgrade_rocket_refuel_ratio = 1.0D;
      public double max_rocket_refuel_ratio = 30.0D;
      public double max_upgrade_rocket_refuel_ratio = 30.0D;
      public double min_hookgravity = 1.0D;
      public double min_upgrade_hookgravity = 0.0D;
      public double max_upgrade_rocket_vertical_angle = 90.0D;
      public double max_rocket_vertical_angle = 90.0D;
      public String grapplingBlocks = "any";
      public String grapplingNonBlocks = "none";
      public int enable_maxlen = 0;
      public int enable_phaserope = 0;
      public int enable_sticky = 0;
      public int enable_hookgravity = 0;
      public int enable_throwspeed = 0;
      public int enable_reelin = 0;
      public int enable_verticalthrowangle = 0;
      public int enable_sneakingverticalthrowangle = 0;
      public int enable_detachonkeyrelease = 0;
      public int enable_motor = 0;
      public int enable_motormaxspeed = 0;
      public int enable_motoracceleration = 0;
      public int enable_motorwhencrouching = 0;
      public int enable_motorwhennotcrouching = 0;
      public int enable_smartmotor = 0;
      public int enable_motordampener = 1;
      public int enable_pullbackwards = 0;
      public int enable_playermovementmult = 0;
      public int enable_enderstaff = 0;
      public int enable_repel = 0;
      public int enable_repelforce = 0;
      public int enable_attract = 0;
      public int enable_attractradius = 0;
      public int enable_doublehook = 0;
      public int enable_smartdoublemotor = 0;
      public int enable_angle = 0;
      public int enable_sneakingangle = 0;
      public int enable_oneropepull = 0;
      public int enable_rocket = 0;
      public int enable_rocket_force = 0;
      public int enable_rocket_active_time = 0;
      public int enable_rocket_refuel_ratio = 0;
      public int enable_rocket_vertical_angle = 0;
      public boolean longfallbootsrecipe = true;
      public boolean hookaffectsentities = true;
      public double ender_staff_strength = 1.5D;
      public int ender_staff_recharge = 100;
      public double wall_jump_up = 0.7D;
      public double wall_jump_side = 0.4D;
      public double max_wallrun_time = 3.0D;
      public double doublejumpforce = 0.8D;
      public double slidingjumpforce = 0.6D;
      public double wallrun_speed = 0.1D;
      public double wallrun_max_speed = 0.7D;
      public double wallrun_drag = 0.01D;
      public double wallrun_min_speed = 0.0D;
      public double sliding_friction = 0.006666666828095913D;
      public boolean override_allowflight = true;
      public double airstrafe_max_speed = 0.7D;
      public double rope_snap_buffer = 5.0D;
      public int default_durability = 500;
      public double rope_jump_power = 1.0D;
      public boolean rope_jump_at_angle = false;
      public double sliding_min_speed = 0.15D;
      public double sliding_end_min_speed = 0.01D;
      public boolean doublejump_relative_to_falling = false;
      public boolean dont_override_movement_in_air = false;
      public double dont_doublejump_if_falling_faster_than = 9.99999999E8D;
      public double rope_jump_cooldown_s = 0.0D;
      public double climb_speed = 0.3D;
      public int enchant_rarity_double_jump = 0;
      public int enchant_rarity_sliding = 0;
      public int enchant_rarity_wallrun = 0;
      public double airstrafe_acceleration = 0.015D;
      public String grappleBreakBlocks = "none";
      public String grappleIgnoreBlocks = "minecraft:tallgrass,minecraft:double_plant";

      public Config() {
      }
   }

   public static void register() {
      ConfigManager.register("grapplemod", "grappling_hook", "", null,
         ConfigEntry.category("options",
            ConfigEntry.value("default_maxlen", double.class, () -> GrappleConfig.options.default_maxlen, v -> GrappleConfig.options.default_maxlen = (Double)v),
            ConfigEntry.value("default_phaserope", boolean.class, () -> GrappleConfig.options.default_phaserope, v -> GrappleConfig.options.default_phaserope = (Boolean)v),
            ConfigEntry.value("default_sticky", boolean.class, () -> GrappleConfig.options.default_sticky, v -> GrappleConfig.options.default_sticky = (Boolean)v),
            ConfigEntry.value("default_hookgravity", double.class, () -> GrappleConfig.options.default_hookgravity, v -> GrappleConfig.options.default_hookgravity = (Double)v),
            ConfigEntry.value("default_throwspeed", double.class, () -> GrappleConfig.options.default_throwspeed, v -> GrappleConfig.options.default_throwspeed = (Double)v),
            ConfigEntry.value("default_reelin", boolean.class, () -> GrappleConfig.options.default_reelin, v -> GrappleConfig.options.default_reelin = (Boolean)v),
            ConfigEntry.value("default_verticalthrowangle", double.class, () -> GrappleConfig.options.default_verticalthrowangle, v -> GrappleConfig.options.default_verticalthrowangle = (Double)v),
            ConfigEntry.value("default_sneakingverticalthrowangle", double.class, () -> GrappleConfig.options.default_sneakingverticalthrowangle, v -> GrappleConfig.options.default_sneakingverticalthrowangle = (Double)v),
            ConfigEntry.value("default_detachonkeyrelease", boolean.class, () -> GrappleConfig.options.default_detachonkeyrelease, v -> GrappleConfig.options.default_detachonkeyrelease = (Boolean)v),
            ConfigEntry.value("default_motor", boolean.class, () -> GrappleConfig.options.default_motor, v -> GrappleConfig.options.default_motor = (Boolean)v),
            ConfigEntry.value("default_motormaxspeed", double.class, () -> GrappleConfig.options.default_motormaxspeed, v -> GrappleConfig.options.default_motormaxspeed = (Double)v),
            ConfigEntry.value("default_motoracceleration", double.class, () -> GrappleConfig.options.default_motoracceleration, v -> GrappleConfig.options.default_motoracceleration = (Double)v),
            ConfigEntry.value("default_motorwhencrouching", boolean.class, () -> GrappleConfig.options.default_motorwhencrouching, v -> GrappleConfig.options.default_motorwhencrouching = (Boolean)v),
            ConfigEntry.value("default_motorwhennotcrouching", boolean.class, () -> GrappleConfig.options.default_motorwhennotcrouching, v -> GrappleConfig.options.default_motorwhennotcrouching = (Boolean)v),
            ConfigEntry.value("default_smartmotor", boolean.class, () -> GrappleConfig.options.default_smartmotor, v -> GrappleConfig.options.default_smartmotor = (Boolean)v),
            ConfigEntry.value("default_motordampener", boolean.class, () -> GrappleConfig.options.default_motordampener, v -> GrappleConfig.options.default_motordampener = (Boolean)v),
            ConfigEntry.value("default_pullbackwards", boolean.class, () -> GrappleConfig.options.default_pullbackwards, v -> GrappleConfig.options.default_pullbackwards = (Boolean)v),
            ConfigEntry.value("default_playermovementmult", double.class, () -> GrappleConfig.options.default_playermovementmult, v -> GrappleConfig.options.default_playermovementmult = (Double)v),
            ConfigEntry.value("default_enderstaff", boolean.class, () -> GrappleConfig.options.default_enderstaff, v -> GrappleConfig.options.default_enderstaff = (Boolean)v),
            ConfigEntry.value("default_repel", boolean.class, () -> GrappleConfig.options.default_repel, v -> GrappleConfig.options.default_repel = (Boolean)v),
            ConfigEntry.value("default_repelforce", double.class, () -> GrappleConfig.options.default_repelforce, v -> GrappleConfig.options.default_repelforce = (Double)v),
            ConfigEntry.value("default_attract", boolean.class, () -> GrappleConfig.options.default_attract, v -> GrappleConfig.options.default_attract = (Boolean)v),
            ConfigEntry.value("default_attractradius", double.class, () -> GrappleConfig.options.default_attractradius, v -> GrappleConfig.options.default_attractradius = (Double)v),
            ConfigEntry.value("default_doublehook", boolean.class, () -> GrappleConfig.options.default_doublehook, v -> GrappleConfig.options.default_doublehook = (Boolean)v),
            ConfigEntry.value("default_smartdoublemotor", boolean.class, () -> GrappleConfig.options.default_smartdoublemotor, v -> GrappleConfig.options.default_smartdoublemotor = (Boolean)v),
            ConfigEntry.value("default_angle", double.class, () -> GrappleConfig.options.default_angle, v -> GrappleConfig.options.default_angle = (Double)v),
            ConfigEntry.value("default_sneakingangle", double.class, () -> GrappleConfig.options.default_sneakingangle, v -> GrappleConfig.options.default_sneakingangle = (Double)v),
            ConfigEntry.value("default_oneropepull", boolean.class, () -> GrappleConfig.options.default_oneropepull, v -> GrappleConfig.options.default_oneropepull = (Boolean)v),
            ConfigEntry.value("default_rocketenabled", boolean.class, () -> GrappleConfig.options.default_rocketenabled, v -> GrappleConfig.options.default_rocketenabled = (Boolean)v),
            ConfigEntry.value("default_rocket_force", double.class, () -> GrappleConfig.options.default_rocket_force, v -> GrappleConfig.options.default_rocket_force = (Double)v),
            ConfigEntry.value("default_rocket_active_time", double.class, () -> GrappleConfig.options.default_rocket_active_time, v -> GrappleConfig.options.default_rocket_active_time = (Double)v),
            ConfigEntry.value("default_rocket_refuel_ratio", double.class, () -> GrappleConfig.options.default_rocket_refuel_ratio, v -> GrappleConfig.options.default_rocket_refuel_ratio = (Double)v),
            ConfigEntry.value("default_rocket_vertical_angle", double.class, () -> GrappleConfig.options.default_rocket_vertical_angle, v -> GrappleConfig.options.default_rocket_vertical_angle = (Double)v),
            ConfigEntry.value("upgraded_throwspeed", double.class, () -> GrappleConfig.options.upgraded_throwspeed, v -> GrappleConfig.options.upgraded_throwspeed = (Double)v),
            ConfigEntry.value("upgraded_maxlen", double.class, () -> GrappleConfig.options.upgraded_maxlen, v -> GrappleConfig.options.upgraded_maxlen = (Double)v),
            ConfigEntry.value("max_maxlen", double.class, () -> GrappleConfig.options.max_maxlen, v -> GrappleConfig.options.max_maxlen = (Double)v),
            ConfigEntry.value("max_hookgravity", double.class, () -> GrappleConfig.options.max_hookgravity, v -> GrappleConfig.options.max_hookgravity = (Double)v),
            ConfigEntry.value("max_throwspeed", double.class, () -> GrappleConfig.options.max_throwspeed, v -> GrappleConfig.options.max_throwspeed = (Double)v),
            ConfigEntry.value("max_verticalthrowangle", double.class, () -> GrappleConfig.options.max_verticalthrowangle, v -> GrappleConfig.options.max_verticalthrowangle = (Double)v),
            ConfigEntry.value("max_sneakingverticalthrowangle", double.class, () -> GrappleConfig.options.max_sneakingverticalthrowangle, v -> GrappleConfig.options.max_sneakingverticalthrowangle = (Double)v),
            ConfigEntry.value("max_motormaxspeed", double.class, () -> GrappleConfig.options.max_motormaxspeed, v -> GrappleConfig.options.max_motormaxspeed = (Double)v),
            ConfigEntry.value("max_motoracceleration", double.class, () -> GrappleConfig.options.max_motoracceleration, v -> GrappleConfig.options.max_motoracceleration = (Double)v),
            ConfigEntry.value("max_playermovementmult", double.class, () -> GrappleConfig.options.max_playermovementmult, v -> GrappleConfig.options.max_playermovementmult = (Double)v),
            ConfigEntry.value("max_repelforce", double.class, () -> GrappleConfig.options.max_repelforce, v -> GrappleConfig.options.max_repelforce = (Double)v),
            ConfigEntry.value("max_attractradius", double.class, () -> GrappleConfig.options.max_attractradius, v -> GrappleConfig.options.max_attractradius = (Double)v),
            ConfigEntry.value("max_angle", double.class, () -> GrappleConfig.options.max_angle, v -> GrappleConfig.options.max_angle = (Double)v),
            ConfigEntry.value("max_sneakingangle", double.class, () -> GrappleConfig.options.max_sneakingangle, v -> GrappleConfig.options.max_sneakingangle = (Double)v),
            ConfigEntry.value("max_upgrade_maxlen", double.class, () -> GrappleConfig.options.max_upgrade_maxlen, v -> GrappleConfig.options.max_upgrade_maxlen = (Double)v),
            ConfigEntry.value("max_upgrade_hookgravity", double.class, () -> GrappleConfig.options.max_upgrade_hookgravity, v -> GrappleConfig.options.max_upgrade_hookgravity = (Double)v),
            ConfigEntry.value("max_upgrade_throwspeed", double.class, () -> GrappleConfig.options.max_upgrade_throwspeed, v -> GrappleConfig.options.max_upgrade_throwspeed = (Double)v),
            ConfigEntry.value("max_upgrade_verticalthrowangle", double.class, () -> GrappleConfig.options.max_upgrade_verticalthrowangle, v -> GrappleConfig.options.max_upgrade_verticalthrowangle = (Double)v),
            ConfigEntry.value("max_upgrade_sneakingverticalthrowangle", double.class, () -> GrappleConfig.options.max_upgrade_sneakingverticalthrowangle, v -> GrappleConfig.options.max_upgrade_sneakingverticalthrowangle = (Double)v),
            ConfigEntry.value("max_upgrade_motormaxspeed", double.class, () -> GrappleConfig.options.max_upgrade_motormaxspeed, v -> GrappleConfig.options.max_upgrade_motormaxspeed = (Double)v),
            ConfigEntry.value("max_upgrade_motoracceleration", double.class, () -> GrappleConfig.options.max_upgrade_motoracceleration, v -> GrappleConfig.options.max_upgrade_motoracceleration = (Double)v),
            ConfigEntry.value("max_upgrade_playermovementmult", double.class, () -> GrappleConfig.options.max_upgrade_playermovementmult, v -> GrappleConfig.options.max_upgrade_playermovementmult = (Double)v),
            ConfigEntry.value("max_upgrade_repelforce", double.class, () -> GrappleConfig.options.max_upgrade_repelforce, v -> GrappleConfig.options.max_upgrade_repelforce = (Double)v),
            ConfigEntry.value("max_upgrade_attractradius", double.class, () -> GrappleConfig.options.max_upgrade_attractradius, v -> GrappleConfig.options.max_upgrade_attractradius = (Double)v),
            ConfigEntry.value("max_upgrade_angle", double.class, () -> GrappleConfig.options.max_upgrade_angle, v -> GrappleConfig.options.max_upgrade_angle = (Double)v),
            ConfigEntry.value("max_upgrade_sneakingangle", double.class, () -> GrappleConfig.options.max_upgrade_sneakingangle, v -> GrappleConfig.options.max_upgrade_sneakingangle = (Double)v),
            ConfigEntry.value("max_rocket_active_time", double.class, () -> GrappleConfig.options.max_rocket_active_time, v -> GrappleConfig.options.max_rocket_active_time = (Double)v),
            ConfigEntry.value("max_upgrade_rocket_active_time", double.class, () -> GrappleConfig.options.max_upgrade_rocket_active_time, v -> GrappleConfig.options.max_upgrade_rocket_active_time = (Double)v),
            ConfigEntry.value("max_rocket_force", double.class, () -> GrappleConfig.options.max_rocket_force, v -> GrappleConfig.options.max_rocket_force = (Double)v),
            ConfigEntry.value("max_upgrade_rocket_force", double.class, () -> GrappleConfig.options.max_upgrade_rocket_force, v -> GrappleConfig.options.max_upgrade_rocket_force = (Double)v),
            ConfigEntry.value("min_rocket_refuel_ratio", double.class, () -> GrappleConfig.options.min_rocket_refuel_ratio, v -> GrappleConfig.options.min_rocket_refuel_ratio = (Double)v),
            ConfigEntry.value("min_upgrade_rocket_refuel_ratio", double.class, () -> GrappleConfig.options.min_upgrade_rocket_refuel_ratio, v -> GrappleConfig.options.min_upgrade_rocket_refuel_ratio = (Double)v),
            ConfigEntry.value("max_rocket_refuel_ratio", double.class, () -> GrappleConfig.options.max_rocket_refuel_ratio, v -> GrappleConfig.options.max_rocket_refuel_ratio = (Double)v),
            ConfigEntry.value("max_upgrade_rocket_refuel_ratio", double.class, () -> GrappleConfig.options.max_upgrade_rocket_refuel_ratio, v -> GrappleConfig.options.max_upgrade_rocket_refuel_ratio = (Double)v),
            ConfigEntry.value("min_hookgravity", double.class, () -> GrappleConfig.options.min_hookgravity, v -> GrappleConfig.options.min_hookgravity = (Double)v),
            ConfigEntry.value("min_upgrade_hookgravity", double.class, () -> GrappleConfig.options.min_upgrade_hookgravity, v -> GrappleConfig.options.min_upgrade_hookgravity = (Double)v),
            ConfigEntry.value("max_upgrade_rocket_vertical_angle", double.class, () -> GrappleConfig.options.max_upgrade_rocket_vertical_angle, v -> GrappleConfig.options.max_upgrade_rocket_vertical_angle = (Double)v),
            ConfigEntry.value("max_rocket_vertical_angle", double.class, () -> GrappleConfig.options.max_rocket_vertical_angle, v -> GrappleConfig.options.max_rocket_vertical_angle = (Double)v),
            ConfigEntry.value("grapplingBlocks", String.class, () -> GrappleConfig.options.grapplingBlocks, v -> GrappleConfig.options.grapplingBlocks = (String)v),
            ConfigEntry.value("grapplingNonBlocks", String.class, () -> GrappleConfig.options.grapplingNonBlocks, v -> GrappleConfig.options.grapplingNonBlocks = (String)v),
            ConfigEntry.value("enable_maxlen", int.class, () -> GrappleConfig.options.enable_maxlen, v -> GrappleConfig.options.enable_maxlen = (Integer)v),
            ConfigEntry.value("enable_phaserope", int.class, () -> GrappleConfig.options.enable_phaserope, v -> GrappleConfig.options.enable_phaserope = (Integer)v),
            ConfigEntry.value("enable_sticky", int.class, () -> GrappleConfig.options.enable_sticky, v -> GrappleConfig.options.enable_sticky = (Integer)v),
            ConfigEntry.value("enable_hookgravity", int.class, () -> GrappleConfig.options.enable_hookgravity, v -> GrappleConfig.options.enable_hookgravity = (Integer)v),
            ConfigEntry.value("enable_throwspeed", int.class, () -> GrappleConfig.options.enable_throwspeed, v -> GrappleConfig.options.enable_throwspeed = (Integer)v),
            ConfigEntry.value("enable_reelin", int.class, () -> GrappleConfig.options.enable_reelin, v -> GrappleConfig.options.enable_reelin = (Integer)v),
            ConfigEntry.value("enable_verticalthrowangle", int.class, () -> GrappleConfig.options.enable_verticalthrowangle, v -> GrappleConfig.options.enable_verticalthrowangle = (Integer)v),
            ConfigEntry.value("enable_sneakingverticalthrowangle", int.class, () -> GrappleConfig.options.enable_sneakingverticalthrowangle, v -> GrappleConfig.options.enable_sneakingverticalthrowangle = (Integer)v),
            ConfigEntry.value("enable_detachonkeyrelease", int.class, () -> GrappleConfig.options.enable_detachonkeyrelease, v -> GrappleConfig.options.enable_detachonkeyrelease = (Integer)v),
            ConfigEntry.value("enable_motor", int.class, () -> GrappleConfig.options.enable_motor, v -> GrappleConfig.options.enable_motor = (Integer)v),
            ConfigEntry.value("enable_motormaxspeed", int.class, () -> GrappleConfig.options.enable_motormaxspeed, v -> GrappleConfig.options.enable_motormaxspeed = (Integer)v),
            ConfigEntry.value("enable_motoracceleration", int.class, () -> GrappleConfig.options.enable_motoracceleration, v -> GrappleConfig.options.enable_motoracceleration = (Integer)v),
            ConfigEntry.value("enable_motorwhencrouching", int.class, () -> GrappleConfig.options.enable_motorwhencrouching, v -> GrappleConfig.options.enable_motorwhencrouching = (Integer)v),
            ConfigEntry.value("enable_motorwhennotcrouching", int.class, () -> GrappleConfig.options.enable_motorwhennotcrouching, v -> GrappleConfig.options.enable_motorwhennotcrouching = (Integer)v),
            ConfigEntry.value("enable_smartmotor", int.class, () -> GrappleConfig.options.enable_smartmotor, v -> GrappleConfig.options.enable_smartmotor = (Integer)v),
            ConfigEntry.value("enable_motordampener", int.class, () -> GrappleConfig.options.enable_motordampener, v -> GrappleConfig.options.enable_motordampener = (Integer)v),
            ConfigEntry.value("enable_pullbackwards", int.class, () -> GrappleConfig.options.enable_pullbackwards, v -> GrappleConfig.options.enable_pullbackwards = (Integer)v),
            ConfigEntry.value("enable_playermovementmult", int.class, () -> GrappleConfig.options.enable_playermovementmult, v -> GrappleConfig.options.enable_playermovementmult = (Integer)v),
            ConfigEntry.value("enable_enderstaff", int.class, () -> GrappleConfig.options.enable_enderstaff, v -> GrappleConfig.options.enable_enderstaff = (Integer)v),
            ConfigEntry.value("enable_repel", int.class, () -> GrappleConfig.options.enable_repel, v -> GrappleConfig.options.enable_repel = (Integer)v),
            ConfigEntry.value("enable_repelforce", int.class, () -> GrappleConfig.options.enable_repelforce, v -> GrappleConfig.options.enable_repelforce = (Integer)v),
            ConfigEntry.value("enable_attract", int.class, () -> GrappleConfig.options.enable_attract, v -> GrappleConfig.options.enable_attract = (Integer)v),
            ConfigEntry.value("enable_attractradius", int.class, () -> GrappleConfig.options.enable_attractradius, v -> GrappleConfig.options.enable_attractradius = (Integer)v),
            ConfigEntry.value("enable_doublehook", int.class, () -> GrappleConfig.options.enable_doublehook, v -> GrappleConfig.options.enable_doublehook = (Integer)v),
            ConfigEntry.value("enable_smartdoublemotor", int.class, () -> GrappleConfig.options.enable_smartdoublemotor, v -> GrappleConfig.options.enable_smartdoublemotor = (Integer)v),
            ConfigEntry.value("enable_angle", int.class, () -> GrappleConfig.options.enable_angle, v -> GrappleConfig.options.enable_angle = (Integer)v),
            ConfigEntry.value("enable_sneakingangle", int.class, () -> GrappleConfig.options.enable_sneakingangle, v -> GrappleConfig.options.enable_sneakingangle = (Integer)v),
            ConfigEntry.value("enable_oneropepull", int.class, () -> GrappleConfig.options.enable_oneropepull, v -> GrappleConfig.options.enable_oneropepull = (Integer)v),
            ConfigEntry.value("enable_rocket", int.class, () -> GrappleConfig.options.enable_rocket, v -> GrappleConfig.options.enable_rocket = (Integer)v),
            ConfigEntry.value("enable_rocket_force", int.class, () -> GrappleConfig.options.enable_rocket_force, v -> GrappleConfig.options.enable_rocket_force = (Integer)v),
            ConfigEntry.value("enable_rocket_active_time", int.class, () -> GrappleConfig.options.enable_rocket_active_time, v -> GrappleConfig.options.enable_rocket_active_time = (Integer)v),
            ConfigEntry.value("enable_rocket_refuel_ratio", int.class, () -> GrappleConfig.options.enable_rocket_refuel_ratio, v -> GrappleConfig.options.enable_rocket_refuel_ratio = (Integer)v),
            ConfigEntry.value("enable_rocket_vertical_angle", int.class, () -> GrappleConfig.options.enable_rocket_vertical_angle, v -> GrappleConfig.options.enable_rocket_vertical_angle = (Integer)v),
            ConfigEntry.value("longfallbootsrecipe", boolean.class, () -> GrappleConfig.options.longfallbootsrecipe, v -> GrappleConfig.options.longfallbootsrecipe = (Boolean)v),
            ConfigEntry.value("hookaffectsentities", boolean.class, () -> GrappleConfig.options.hookaffectsentities, v -> GrappleConfig.options.hookaffectsentities = (Boolean)v),
            ConfigEntry.value("ender_staff_strength", double.class, () -> GrappleConfig.options.ender_staff_strength, v -> GrappleConfig.options.ender_staff_strength = (Double)v),
            ConfigEntry.value("ender_staff_recharge", int.class, () -> GrappleConfig.options.ender_staff_recharge, v -> GrappleConfig.options.ender_staff_recharge = (Integer)v),
            ConfigEntry.value("wall_jump_up", double.class, () -> GrappleConfig.options.wall_jump_up, v -> GrappleConfig.options.wall_jump_up = (Double)v),
            ConfigEntry.value("wall_jump_side", double.class, () -> GrappleConfig.options.wall_jump_side, v -> GrappleConfig.options.wall_jump_side = (Double)v),
            ConfigEntry.value("max_wallrun_time", double.class, () -> GrappleConfig.options.max_wallrun_time, v -> GrappleConfig.options.max_wallrun_time = (Double)v),
            ConfigEntry.value("doublejumpforce", double.class, () -> GrappleConfig.options.doublejumpforce, v -> GrappleConfig.options.doublejumpforce = (Double)v),
            ConfigEntry.value("slidingjumpforce", double.class, () -> GrappleConfig.options.slidingjumpforce, v -> GrappleConfig.options.slidingjumpforce = (Double)v),
            ConfigEntry.value("wallrun_speed", double.class, () -> GrappleConfig.options.wallrun_speed, v -> GrappleConfig.options.wallrun_speed = (Double)v),
            ConfigEntry.value("wallrun_max_speed", double.class, () -> GrappleConfig.options.wallrun_max_speed, v -> GrappleConfig.options.wallrun_max_speed = (Double)v),
            ConfigEntry.value("wallrun_drag", double.class, () -> GrappleConfig.options.wallrun_drag, v -> GrappleConfig.options.wallrun_drag = (Double)v),
            ConfigEntry.value("wallrun_min_speed", double.class, () -> GrappleConfig.options.wallrun_min_speed, v -> GrappleConfig.options.wallrun_min_speed = (Double)v),
            ConfigEntry.value("sliding_friction", double.class, () -> GrappleConfig.options.sliding_friction, v -> GrappleConfig.options.sliding_friction = (Double)v),
            ConfigEntry.value("override_allowflight", boolean.class, () -> GrappleConfig.options.override_allowflight, v -> GrappleConfig.options.override_allowflight = (Boolean)v),
            ConfigEntry.value("airstrafe_max_speed", double.class, () -> GrappleConfig.options.airstrafe_max_speed, v -> GrappleConfig.options.airstrafe_max_speed = (Double)v),
            ConfigEntry.value("rope_snap_buffer", double.class, () -> GrappleConfig.options.rope_snap_buffer, v -> GrappleConfig.options.rope_snap_buffer = (Double)v),
            ConfigEntry.value("default_durability", int.class, () -> GrappleConfig.options.default_durability, v -> GrappleConfig.options.default_durability = (Integer)v),
            ConfigEntry.value("rope_jump_power", double.class, () -> GrappleConfig.options.rope_jump_power, v -> GrappleConfig.options.rope_jump_power = (Double)v),
            ConfigEntry.value("rope_jump_at_angle", boolean.class, () -> GrappleConfig.options.rope_jump_at_angle, v -> GrappleConfig.options.rope_jump_at_angle = (Boolean)v),
            ConfigEntry.value("sliding_min_speed", double.class, () -> GrappleConfig.options.sliding_min_speed, v -> GrappleConfig.options.sliding_min_speed = (Double)v),
            ConfigEntry.value("sliding_end_min_speed", double.class, () -> GrappleConfig.options.sliding_end_min_speed, v -> GrappleConfig.options.sliding_end_min_speed = (Double)v),
            ConfigEntry.value("doublejump_relative_to_falling", boolean.class, () -> GrappleConfig.options.doublejump_relative_to_falling, v -> GrappleConfig.options.doublejump_relative_to_falling = (Boolean)v),
            ConfigEntry.value("dont_override_movement_in_air", boolean.class, () -> GrappleConfig.options.dont_override_movement_in_air, v -> GrappleConfig.options.dont_override_movement_in_air = (Boolean)v),
            ConfigEntry.value("dont_doublejump_if_falling_faster_than", double.class, () -> GrappleConfig.options.dont_doublejump_if_falling_faster_than, v -> GrappleConfig.options.dont_doublejump_if_falling_faster_than = (Double)v),
            ConfigEntry.value("rope_jump_cooldown_s", double.class, () -> GrappleConfig.options.rope_jump_cooldown_s, v -> GrappleConfig.options.rope_jump_cooldown_s = (Double)v),
            ConfigEntry.value("climb_speed", double.class, () -> GrappleConfig.options.climb_speed, v -> GrappleConfig.options.climb_speed = (Double)v),
            ConfigEntry.value("enchant_rarity_double_jump", int.class, () -> GrappleConfig.options.enchant_rarity_double_jump, v -> GrappleConfig.options.enchant_rarity_double_jump = (Integer)v),
            ConfigEntry.value("enchant_rarity_sliding", int.class, () -> GrappleConfig.options.enchant_rarity_sliding, v -> GrappleConfig.options.enchant_rarity_sliding = (Integer)v),
            ConfigEntry.value("enchant_rarity_wallrun", int.class, () -> GrappleConfig.options.enchant_rarity_wallrun, v -> GrappleConfig.options.enchant_rarity_wallrun = (Integer)v),
            ConfigEntry.value("airstrafe_acceleration", double.class, () -> GrappleConfig.options.airstrafe_acceleration, v -> GrappleConfig.options.airstrafe_acceleration = (Double)v),
            ConfigEntry.value("grappleBreakBlocks", String.class, () -> GrappleConfig.options.grappleBreakBlocks, v -> GrappleConfig.options.grappleBreakBlocks = (String)v),
            ConfigEntry.value("grappleIgnoreBlocks", String.class, () -> GrappleConfig.options.grappleIgnoreBlocks, v -> GrappleConfig.options.grappleIgnoreBlocks = (String)v)),
         ConfigEntry.category("client_options",
            ConfigEntry.value("wallrun_sound_effect_time_s", double.class, () -> GrappleConfig.client_options.wallrun_sound_effect_time_s, v -> GrappleConfig.client_options.wallrun_sound_effect_time_s = (Double)v),
            ConfigEntry.value("wallrun_camera_tilt_degrees", float.class, () -> GrappleConfig.client_options.wallrun_camera_tilt_degrees, v -> GrappleConfig.client_options.wallrun_camera_tilt_degrees = (Float)v),
            ConfigEntry.value("wallrun_camera_animation_s", float.class, () -> GrappleConfig.client_options.wallrun_camera_animation_s, v -> GrappleConfig.client_options.wallrun_camera_animation_s = (Float)v),
            ConfigEntry.value("wallrun_sound_volume", float.class, () -> GrappleConfig.client_options.wallrun_sound_volume, v -> GrappleConfig.client_options.wallrun_sound_volume = (Float)v),
            ConfigEntry.value("doublejump_sound_volume", float.class, () -> GrappleConfig.client_options.doublejump_sound_volume, v -> GrappleConfig.client_options.doublejump_sound_volume = (Float)v),
            ConfigEntry.value("slide_sound_volume", float.class, () -> GrappleConfig.client_options.slide_sound_volume, v -> GrappleConfig.client_options.slide_sound_volume = (Float)v),
            ConfigEntry.value("wallrunjump_sound_volume", float.class, () -> GrappleConfig.client_options.wallrunjump_sound_volume, v -> GrappleConfig.client_options.wallrunjump_sound_volume = (Float)v),
            ConfigEntry.value("enderstaff_sound_volume", float.class, () -> GrappleConfig.client_options.enderstaff_sound_volume, v -> GrappleConfig.client_options.enderstaff_sound_volume = (Float)v),
            ConfigEntry.value("rocket_sound_volume", float.class, () -> GrappleConfig.client_options.rocket_sound_volume, v -> GrappleConfig.client_options.rocket_sound_volume = (Float)v)));
   }
}
