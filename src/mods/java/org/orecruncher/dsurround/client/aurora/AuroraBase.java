package org.orecruncher.dsurround.client.aurora;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.capabilities.dimension.IDimensionInfo;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;
import org.orecruncher.lib.Color;
import org.orecruncher.lib.random.XorShiftRandom;

@SideOnly(Side.CLIENT)
public abstract class AuroraBase implements IAurora {
   protected final EaglercraftRandom random;
   protected final AuroraBand band;
   protected final int bandCount;
   protected final float offset;
   protected final AuroraLifeTracker tracker;
   protected final AuroraColor colors;
   protected final EntityPlayer player;
   protected final IDimensionInfo dimInfo;

   public AuroraBase(long seed) {
      this(seed, false);
   }

   public AuroraBase(long seed, boolean flag) {
      this(new XorShiftRandom(seed), flag);
   }

   public AuroraBase(EaglercraftRandom rand, boolean flag) {
      this.random = rand;
      this.bandCount = Math.min(this.random.nextInt(3) + 1, ModOptions.aurora.maxBands);
      this.offset = (float)(this.random.nextInt(20) + 20);
      this.colors = AuroraColor.get(this.random);
      AuroraFactory.AuroraGeometry geo = AuroraFactory.AuroraGeometry.get(this.random);
      this.band = new AuroraBand(this.random, geo, flag, flag);
      this.tracker = new AuroraLifeTracker(512, 1);
      this.player = EnvironStateHandler.EnvironState.getPlayer();
      this.dimInfo = EnvironStateHandler.EnvironState.getDimensionInfo();
   }

   private boolean isAlive() {
      return this.tracker.isAlive();
   }

   public void setFading(boolean flag) {
      this.tracker.setFading(flag);
   }

   public boolean isDying() {
      return this.tracker.isFading();
   }

   public void update() {
      this.tracker.update();
   }

   public boolean isComplete() {
      return !this.isAlive();
   }

   protected float getAlpha() {
      return this.tracker.ageRatio() * (float)this.band.getAlphaLimit() / 255.0F;
   }

   protected double getTranslationX(float partialTick) {
      return this.player.posX - (this.player.lastTickPosX + (this.player.posX - this.player.lastTickPosX) * (double)partialTick);
   }

   protected double getTranslationZ(float partialTick) {
      return this.player.posZ - 150.0D - (this.player.lastTickPosZ + (this.player.posZ - this.player.lastTickPosZ) * (double)partialTick);
   }

   protected double getTranslationY(float partialTick) {
      if (this.player.posY > (double)this.dimInfo.getSeaLevel()) {
         double limit = (double)(this.dimInfo.getSkyHeight() + this.dimInfo.getCloudHeight()) / 2.0D;
         double d1 = limit - (double)this.dimInfo.getSeaLevel();
         double d2 = this.player.posY - (double)this.dimInfo.getSeaLevel();
         return 64.0D * (d1 - d2) / d1;
      } else {
         return 64.0D;
      }
   }

   @Nonnull
   protected Color getBaseColor() {
      return this.colors.baseColor;
   }

   @Nonnull
   protected Color getFadeColor() {
      return this.colors.fadeColor;
   }

   @Nonnull
   protected Color getMiddleColor() {
      return this.colors.middleColor;
   }

   public abstract void render(float var1);

   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("bands: ").append(this.bandCount);
      builder.append(", off: ").append(this.offset);
      builder.append(", len: ").append(this.band.length);
      builder.append(", base: ").append(this.getBaseColor().toString());
      builder.append(", fade: ").append(this.getFadeColor().toString());
      builder.append(", alpha: ").append((int)(this.getAlpha() * 255.0F));
      if (!this.tracker.isAlive()) {
         builder.append(", DEAD");
      } else if (this.tracker.isFading()) {
         builder.append(", FADING");
      }

      return builder.toString();
   }
}
