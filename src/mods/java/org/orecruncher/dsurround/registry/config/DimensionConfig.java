package org.orecruncher.dsurround.registry.config;

import com.google.gson.annotations.SerializedName;
import javax.annotation.Nonnull;

public class DimensionConfig {
   @SerializedName("dimId")
   public Integer dimensionId = null;
   @SerializedName("name")
   public String name = null;
   @SerializedName("seaLevel")
   public Integer seaLevel = null;
   @SerializedName("skyHeight")
   public Integer skyHeight = null;
   @SerializedName("cloudHeight")
   public Integer cloudHeight = null;
   @SerializedName("haze")
   public Boolean hasHaze = null;
   @SerializedName("aurora")
   public Boolean hasAurora = null;
   @SerializedName("weather")
   public Boolean hasWeather = null;
   @SerializedName("fog")
   public Boolean hasFog = null;
   @SerializedName("alwaysOutside")
   public Boolean alwaysOutside = null;

   public DimensionConfig() {
   }

   @Nonnull
   public String toString() {
      StringBuilder builder = new StringBuilder();
      if (this.dimensionId != null) {
         builder.append("dimensionId: ").append(this.dimensionId.intValue()).append(" ");
      }

      if (this.name != null) {
         builder.append("name: ").append(this.name).append(" ");
      }

      if (this.seaLevel != null) {
         builder.append("seaLevel: ").append(this.seaLevel.intValue()).append(" ");
      }

      if (this.skyHeight != null) {
         builder.append("skyHeight: ").append(this.skyHeight.intValue()).append(" ");
      }

      if (this.cloudHeight != null) {
         builder.append("cloudHeight: ").append(this.cloudHeight.intValue()).append(" ");
      }

      if (this.hasAurora != null) {
         builder.append("hasAurora: ").append(this.hasAurora.booleanValue()).append(" ");
      }

      if (this.hasHaze != null) {
         builder.append("hasHaze: ").append(this.hasHaze.booleanValue()).append(" ");
      }

      if (this.hasWeather != null) {
         builder.append("hasWeather: ").append(this.hasWeather.booleanValue()).append(" ");
      }

      if (this.hasFog != null) {
         builder.append("hasFog: ").append(this.hasFog.booleanValue()).append(" ");
      }

      if (this.alwaysOutside != null) {
         builder.append("alwaysOutside: ").append(this.alwaysOutside.booleanValue()).append(" ");
      }

      return builder.toString();
   }

   public int hashCode() {
      return this.dimensionId != null ? this.dimensionId.hashCode() : (this.name != null ? this.name.hashCode() : super.hashCode());
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof DimensionConfig)) {
         return false;
      } else {
         DimensionConfig dc = (DimensionConfig)obj;
         return this.dimensionId != null && this.dimensionId.equals(dc.dimensionId) || this.name != null && this.name.equals(dc.name);
      }
   }
}
