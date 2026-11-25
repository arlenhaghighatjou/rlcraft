package com.ferreusveritas.dynamictrees.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.function.Function;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumFacing.Axis;

public class ThickRingTextureAtlasSprite extends EaglerTextureAtlasSprite {
   private final ResourceLocation baseRingLocation;
   private final ResourceLocation baseRingLocationAlternate;

   public ThickRingTextureAtlasSprite(ResourceLocation spriteName, ResourceLocation baseRingLocation) {
      super(spriteName.toString());
      this.baseRingLocation = baseRingLocation;
      this.baseRingLocationAlternate = null;
   }

   public ThickRingTextureAtlasSprite(ResourceLocation spriteName, ResourceLocation baseRingLocation, ResourceLocation baseRingLocationAlternate) {
      super(spriteName.toString());
      this.baseRingLocation = baseRingLocation;
      this.baseRingLocationAlternate = baseRingLocationAlternate;
   }

   public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location) {
      return true;
   }

   public ResourceLocation solveRingTexture(Function<ResourceLocation, EaglerTextureAtlasSprite> textureGetter) {
      if (this.baseRingLocationAlternate == null) {
         return this.baseRingLocation;
      } else if (this.baseRingLocation.getResourcePath().endsWith("top")) {
         return this.baseRingLocation;
      } else if (this.baseRingLocationAlternate.getResourcePath().endsWith("top")) {
         return this.baseRingLocationAlternate;
      } else {
         int deltaA = this.getDeltaBorderVsCenterColor((EaglerTextureAtlasSprite)textureGetter.apply(this.baseRingLocation));
         int deltaB = this.getDeltaBorderVsCenterColor((EaglerTextureAtlasSprite)textureGetter.apply(this.baseRingLocationAlternate));
         return deltaA > deltaB ? this.baseRingLocation : this.baseRingLocationAlternate;
      }
   }

   private int getDeltaBorderVsCenterColor(EaglerTextureAtlasSprite sprite) {
      TextureUtils.PixelBuffer pixbuf = new TextureUtils.PixelBuffer(sprite);
      int u = pixbuf.w / 16;
      TextureUtils.PixelBuffer wide = new TextureUtils.PixelBuffer(u * 14, u * 1);
      TextureUtils.PixelBuffer tall = new TextureUtils.PixelBuffer(u * 1, u * 14);
      int[] samples = new int[4];
      pixbuf.blit(wide, u * -1, u * 0);
      samples[0] = wide.averageColor();
      pixbuf.blit(wide, u * -1, u * -15);
      samples[1] = wide.averageColor();
      pixbuf.blit(tall, u * 0, u * -1);
      samples[2] = tall.averageColor();
      pixbuf.blit(tall, u * -15, u * -1);
      samples[3] = tall.averageColor();
      int borderColor = TextureUtils.avgColors(samples);

      for(int i = 0; i < 4; ++i) {
         pixbuf.blit(wide, u * -1, u * -(i * 3 + 3));
         samples[i] = wide.averageColor();
      }

      int innerColor = TextureUtils.avgColors(samples);
      int[] cA = TextureUtils.decompose(borderColor);
      int[] cB = TextureUtils.decompose(innerColor);
      int delR = cB[0] - cA[0];
      int delG = cB[1] - cA[1];
      int delB = cB[2] - cA[2];
      return delR * delR + delG * delG + delB * delB;
   }

   public boolean load(IResourceManager manager, ResourceLocation location, Function<ResourceLocation, EaglerTextureAtlasSprite> textureGetter) {
      EaglerTextureAtlasSprite baseTexture = (EaglerTextureAtlasSprite)textureGetter.apply(this.solveRingTexture(textureGetter));
      int srcWidth = baseTexture.getIconWidth();
      int srcHeight = baseTexture.getIconHeight();
      this.width = srcWidth * 3;
      this.height = srcHeight * 3;
      TextureUtils.PixelBuffer basePixbuf = new TextureUtils.PixelBuffer(baseTexture);
      TextureUtils.PixelBuffer majPixbuf = this.createMajorTexture(basePixbuf);
      int mipmapLevels = baseTexture.getFrameTextureData(0).length;
      int[][] textureData = new int[mipmapLevels][];
      textureData[0] = majPixbuf.pixels;
      this.setFramesTextureData(Lists.newArrayList(new int[][][]{textureData}));
      return false;
   }

   private TextureUtils.PixelBuffer createMajorTexture(TextureUtils.PixelBuffer baseBuffer) {
      int w = baseBuffer.w * 3;
      int h = baseBuffer.h * 3;
      int scale = baseBuffer.w / 16;
      TextureUtils.PixelBuffer antPixbuf = this.createBarklessAntecedent(baseBuffer);
      TextureUtils.PixelBuffer majPixbuf = new TextureUtils.PixelBuffer(w, h);
      TextureUtils.PixelBuffer[] corners = new TextureUtils.PixelBuffer[4];
      TextureUtils.PixelBuffer[] edges = new TextureUtils.PixelBuffer[4];

      for(int i = 0; i < 4; ++i) {
         corners[i] = new TextureUtils.PixelBuffer(6 * scale, 6 * scale);
         edges[i] = new TextureUtils.PixelBuffer(4 * scale, 6 * scale);
         antPixbuf.blit(corners[i], 0, 0, i);
         antPixbuf.blit(edges[i], -6 * scale, 0, i);
      }

      int centerX = 24;
      int centerY = 24;

      for(int nesting = 0; nesting < 3; ++nesting) {
         int edge = 2;
         int pixbufSel = 0;

         for(EnumFacing dir : EnumFacing.HORIZONTALS) {
            EnumFacing ovr = dir.rotateY();
            int offX = dir.getFrontOffsetX();
            int offY = dir.getFrontOffsetZ();
            int compX = (offX == 1 ? -6 : 0) + (dir.getAxis() == Axis.Z ? -2 : 0);
            int compY = (offY == 1 ? -6 : 0) + (dir.getAxis() == Axis.X ? -2 : 0);
            int startX = offX * (14 + nesting * 6);
            int startY = offY * (14 + nesting * 6);

            for(int way = -1; way <= 1; way += 2) {
               for(int i = 0; i < 4 + nesting; ++i) {
                  int rowX = ovr.getFrontOffsetX() * i * way * 4;
                  int rowY = ovr.getFrontOffsetZ() * i * way * 4;
                  int realX = centerX + startX + compX + rowX;
                  int realY = centerY + startY + compY + rowY;
                  edges[pixbufSel++ * 13402141 >> 1 & 3].blit(majPixbuf, realX * scale, realY * scale, edge);
               }
            }

            ++edge;
         }
      }

      int[] cornerX = new int[]{-1, 1, 1, -1};
      int[] cornerY = new int[]{-1, -1, 1, 1};
      antPixbuf.blit(majPixbuf, 16 * scale, 16 * scale);

      for(int nesting = 1; nesting < 4; ++nesting) {
         for(int corner = 0; corner < 4; ++corner) {
            TextureUtils.PixelBuffer cornerPixels = corners[corner + nesting & 3];
            int cX = cornerX[corner];
            int cY = cornerY[corner];
            int offX = cX * 6 * nesting + cX * 5;
            int offY = cY * 6 * nesting + cY * 5;
            int realX = 21 + offX;
            int realY = 21 + offY;
            cornerPixels.blit(majPixbuf, realX * scale, realY * scale, corner);
         }
      }

      for(int i = 0; i < 4; ++i) {
         corners[i] = new TextureUtils.PixelBuffer(scale, scale);
         edges[i] = new TextureUtils.PixelBuffer(14 * scale, scale);
         baseBuffer.blit(corners[i], 0, 0, i);
         baseBuffer.blit(edges[i], -1 * scale, 0, i);
      }

      int pixbufSel = 0;

      for(int row = 0; row < 4; ++row) {
         TextureUtils.PixelBuffer edge = edges[pixbufSel++ * 13402141 >> 1 & 3];
         int span = edge.w;
         edge.blit(majPixbuf, (1 + row * span) * scale, 0, 0);
         edge.blit(majPixbuf, (majPixbuf.w - edge.h) * scale, (1 + row * span) * scale, 1);
         edge.blit(majPixbuf, (majPixbuf.w - 1 - span - row * span) * scale, (majPixbuf.h - edge.h) * scale, 2);
         edge.blit(majPixbuf, 0, (majPixbuf.h - 1 - edge.w - row * span) * scale, 3);
      }

      for(int corner = 0; corner < 4; ++corner) {
         int cX = cornerX[corner] + 1 >> 1;
         int cY = cornerY[corner] + 1 >> 1;
         TextureUtils.PixelBuffer cornerPixels = corners[corner];
         cornerPixels.blit(majPixbuf, cX * (majPixbuf.w - cornerPixels.w) * scale, cY * (majPixbuf.h - cornerPixels.h) * scale, corner);
      }

      return majPixbuf;
   }

   private TextureUtils.PixelBuffer createBarklessAntecedent(TextureUtils.PixelBuffer baseBuffer) {
      TextureUtils.PixelBuffer antecedent = new TextureUtils.PixelBuffer(baseBuffer);
      int scale = baseBuffer.w / 16;
      baseBuffer.blit(antecedent, 3 * scale, 3 * scale, 1);
      baseBuffer.blit(antecedent, -3 * scale, 3 * scale, 1);
      baseBuffer.blit(antecedent, 3 * scale, -3 * scale, 1);
      baseBuffer.blit(antecedent, -3 * scale, -3 * scale, 1);
      TextureUtils.PixelBuffer ringStrip = new TextureUtils.PixelBuffer(6 * scale, 1 * scale);
      baseBuffer.blit(ringStrip, -5 * scale, -3 * scale);
      ringStrip.blit(antecedent, 0 * scale, 2 * scale, -1);
      ringStrip.blit(antecedent, 15 * scale, 8 * scale, 1);
      baseBuffer.blit(ringStrip, -5 * scale, -12 * scale);
      ringStrip.blit(antecedent, 0 * scale, 8 * scale, 1);
      ringStrip.blit(antecedent, 15 * scale, 2 * scale, -1);
      ringStrip = new TextureUtils.PixelBuffer(1 * scale, 6 * scale);
      baseBuffer.blit(ringStrip, -3 * scale, -5 * scale);
      ringStrip.blit(antecedent, 2 * scale, 0 * scale, -1);
      ringStrip.blit(antecedent, 8 * scale, 15 * scale, 1);
      baseBuffer.blit(ringStrip, -12 * scale, -5 * scale);
      ringStrip.blit(antecedent, 8 * scale, 0 * scale, 1);
      ringStrip.blit(antecedent, 2 * scale, 15 * scale, -1);
      ringStrip = null;
      TextureUtils.PixelBuffer center = new TextureUtils.PixelBuffer(14 * scale, 14 * scale);
      baseBuffer.blit(center, -1 * scale, -1 * scale);
      center.blit(antecedent, 1 * scale, 1 * scale);
      return antecedent;
   }

   public Collection<ResourceLocation> getDependencies() {
      return this.baseRingLocationAlternate == null ? ImmutableList.of(this.baseRingLocation) : ImmutableList.of(this.baseRingLocation, this.baseRingLocationAlternate);
   }
}
