package org.orecruncher.dsurround.registry.footstep;

import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.registry.config.VariatorConfig;

@SideOnly(Side.CLIENT)
public class Variator {
   public final int IMMOBILE_DURATION;
   public final boolean EVENT_ON_JUMP;
   public final float LAND_HARD_DISTANCE_MIN;
   public final float SPEED_TO_JUMP_AS_MULTIFOOT;
   public final float SPEED_TO_RUN;
   public final float STRIDE;
   public final float STRIDE_STAIR;
   public final float STRIDE_LADDER;
   public final float QUADRUPED_MULTIPLIER;
   public final boolean PLAY_WANDER;
   public final boolean QUADRUPED;
   public final boolean PLAY_JUMP;
   public final float DISTANCE_TO_CENTER;
   public final boolean HAS_FOOTPRINT;
   public final FootprintStyle FOOTPRINT_STYLE;
   public final float FOOTPRINT_SCALE;
   public final float VOLUME_SCALE;

   public Variator() {
      this.IMMOBILE_DURATION = 200;
      this.EVENT_ON_JUMP = true;
      this.LAND_HARD_DISTANCE_MIN = 0.9F;
      this.SPEED_TO_JUMP_AS_MULTIFOOT = 0.005F;
      this.SPEED_TO_RUN = 0.22F;
      this.STRIDE = 0.75F;
      this.STRIDE_STAIR = this.STRIDE * 0.65F;
      this.STRIDE_LADDER = 0.5F;
      this.QUADRUPED_MULTIPLIER = 1.25F;
      this.PLAY_WANDER = true;
      this.QUADRUPED = false;
      this.PLAY_JUMP = false;
      this.DISTANCE_TO_CENTER = 0.2F;
      this.HAS_FOOTPRINT = true;
      this.FOOTPRINT_STYLE = FootprintStyle.LOWRES_SQUARE;
      this.FOOTPRINT_SCALE = 1.0F;
      this.VOLUME_SCALE = 1.0F;
   }

   public Variator(@Nonnull VariatorConfig cfg) {
      this.IMMOBILE_DURATION = cfg.immobileDuration;
      this.EVENT_ON_JUMP = cfg.eventOnJump;
      this.LAND_HARD_DISTANCE_MIN = cfg.landHardDistanceMin;
      this.SPEED_TO_JUMP_AS_MULTIFOOT = cfg.speedToJumpAsMultifoot;
      this.SPEED_TO_RUN = cfg.speedToRun;
      this.STRIDE = cfg.stride;
      this.STRIDE_STAIR = cfg.strideStair;
      this.STRIDE_LADDER = cfg.strideLadder;
      this.QUADRUPED_MULTIPLIER = cfg.quadrupedMultiplier;
      this.PLAY_WANDER = cfg.playWander;
      this.QUADRUPED = cfg.quadruped;
      this.PLAY_JUMP = cfg.playJump;
      this.DISTANCE_TO_CENTER = cfg.distanceToCenter;
      this.HAS_FOOTPRINT = cfg.hasFootprint;
      this.FOOTPRINT_STYLE = FootprintStyle.getStyle(cfg.footprintStyle);
      this.FOOTPRINT_SCALE = cfg.footprintScale;
      this.VOLUME_SCALE = cfg.volumeScale;
   }
}
