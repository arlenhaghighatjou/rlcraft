package com.endertech.minecraft.mods.adhooks.items;

import com.endertech.common.FloatBounds;
import com.endertech.common.IntBounds;
import com.endertech.common.CommonMath.Approx;
import com.endertech.minecraft.forge.configs.ColorARGB;
import com.endertech.minecraft.forge.configs.ForgeConfig;
import com.endertech.minecraft.forge.configs.IHaveConfig;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class Rope implements IHaveConfig {
   public static final IntBounds LENGTH_BOUNDS = new IntBounds(Integer.valueOf(1), Integer.valueOf(80));
   public static final FloatBounds WIDTH_BOUNDS = new FloatBounds(1.0F, 10.0F);
   public static final FloatBounds ELASTICITY_BOUNDS = new FloatBounds(0.0F, 0.95F);
   public static final FloatBounds BOUNCE_REDUCTION_BOUNDS = new FloatBounds(0.1F, 0.3F);
   public final int segmentsAmount = 16;
   public final ColorARGB color;
   public final float width;
   protected int maxLength;
   protected float elasticity;
   protected final ForgeConfig config;

   public Rope(ForgeConfig config, int maxLength, float width, float elasticity, ColorARGB color) {
      this.config = config;
      this.color = ForgeConfig.getColorARGB(config, this.getClassCategory(), "color", color, "Defines a color in ARGB (Alpha, Red, Green, Blue) hex format (0xaarrggbb)");
      this.maxLength = ForgeConfig.getInt(config, this.getClassCategory(), "maxLength", maxLength, LENGTH_BOUNDS, "Defines the maximal rope length.");
      this.width = ForgeConfig.getFloat(config, this.getClassCategory(), "width", width, WIDTH_BOUNDS, "Defines rope width.");
      this.elasticity = ForgeConfig.getFloat(config, this.getClassCategory(), "elasticity", elasticity, ELASTICITY_BOUNDS, "Defines how elastic the rope is.");
   }

   public int getMaxLength() {
      return this.maxLength;
   }

   public float getElasticity() {
      return this.elasticity;
   }

   public float getBounceReduction() {
      return BOUNCE_REDUCTION_BOUNDS.approxDown(this.getElasticity()).floatValue();
   }

   public float getTensionForce(float ropeLength, float realDistance) {
      float diff = realDistance - ropeLength;
      float ropeStrength = this.getRopeStrength();
      return diff * ropeStrength;
   }

   public float getRopeStrength() {
      return Approx.down(this.getElasticity(), ELASTICITY_BOUNDS);
   }

   public ForgeConfig getConfig() {
      return this.config;
   }

   public float getMinLength(@Nullable EntityLivingBase shooter, @Nullable Entity target) {
      float shooterSize = shooter != null ? Math.max(shooter.width, shooter.height) : 0.0F;
      float targetSize = target != null ? Math.max(target.width, target.height) : 0.0F;
      float minLength = shooterSize + targetSize;
      minLength = (float)((double)minLength / 2.0D);
      return minLength;
   }

   public FloatBounds getLengthBounds(@Nullable EntityLivingBase shooter, @Nullable Entity target) {
      float minLength = this.getMinLength(shooter, target);
      return FloatBounds.from(Float.valueOf(minLength), Float.valueOf((float)this.getMaxLength()));
   }
}
