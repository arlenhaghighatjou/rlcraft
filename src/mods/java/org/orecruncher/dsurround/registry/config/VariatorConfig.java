package org.orecruncher.dsurround.registry.config;

import com.google.gson.annotations.SerializedName;

public class VariatorConfig {
   @SerializedName("immobileDuration")
   public int immobileDuration = 200;
   @SerializedName("eventOnJump")
   public boolean eventOnJump = true;
   @SerializedName("landHardDistanceMin")
   public float landHardDistanceMin = 0.9F;
   @SerializedName("speedToJumpAsMultifoot")
   public float speedToJumpAsMultifoot = 0.005F;
   @SerializedName("speedToRun")
   public float speedToRun = 0.22F;
   @SerializedName("stride")
   public float stride = 0.75F;
   @SerializedName("strideStair")
   public float strideStair;
   @SerializedName("strideLadder")
   public float strideLadder;
   @SerializedName("quadrupedMultiplier")
   public float quadrupedMultiplier;
   @SerializedName("playWander")
   public boolean playWander;
   @SerializedName("quadruped")
   public boolean quadruped;
   @SerializedName("playJump")
   public boolean playJump;
   @SerializedName("distanceToCenter")
   public float distanceToCenter;
   @SerializedName("hasFootprint")
   public boolean hasFootprint;
   @SerializedName("footprintStyle")
   public int footprintStyle;
   @SerializedName("footprintScale")
   public float footprintScale;
   @SerializedName("volumeScale")
   public float volumeScale;

   public VariatorConfig() {
      this.strideStair = this.stride * 0.65F;
      this.strideLadder = 0.5F;
      this.quadrupedMultiplier = 1.25F;
      this.playWander = true;
      this.quadruped = false;
      this.playJump = false;
      this.distanceToCenter = 0.2F;
      this.hasFootprint = true;
      this.footprintStyle = 6;
      this.footprintScale = 1.0F;
      this.volumeScale = 1.0F;
   }
}
