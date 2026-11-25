package com.yyon.grapplinghook;

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
}
