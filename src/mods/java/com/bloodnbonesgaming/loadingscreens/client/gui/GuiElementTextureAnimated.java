package com.bloodnbonesgaming.loadingscreens.client.gui;

import com.bloodnbonesgaming.lib.util.script.ScriptCall;
import com.bloodnbonesgaming.loadingscreens.IOHelper;
import com.bloodnbonesgaming.loadingscreens.LoadingScreens;
import com.arlenh7.eaglercraft.image.GifDecoder;
import java.io.IOException;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglerInputStream;
import net.lax1dude.eaglercraft.v1_8.IOUtils;
import net.lax1dude.eaglercraft.v1_8.opengl.ImageData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

public class GuiElementTextureAnimated extends GuiElementTextureStretch {
   private int frames = 1;
   private int currentFrame = 0;
   private int updateTime = 5;
   private DynamicTexture gifTexture = null;

   public GuiElementTextureAnimated(EnumGuiLocation location, ResourceLocation texture) {
      super(location, texture);
      this.frames = this.imageHeight / this.imageWidth;
      this.imageHeight = this.imageWidth;
   }

   public void setUpdateTime(int time) {
      this.updateTime = time;
   }

   public void clean() {
      if (this.gifTexture != null) {
         this.gifTexture.deleteGlTexture();
         this.gifTexture = null;
         this.currentFrame = 0;
      }

   }

   public void render(Minecraft minecraft, int guiWidth, int guiHeight) {
      if (this.texture.getResourcePath().endsWith(".gif")) {
         if (this.gifTexture == null) {
            if (!this.loadAndTransformGIF(minecraft, this.texture)) {
               LoadingScreens.instance.getLog().info("Failed to load gif: " + this.texture.toString() + ".");
               return;
            }
         } else {
            this.gifTexture.updateDynamicTexture();
         }
      } else {
         minecraft.getTextureManager().bindTexture(this.texture);
      }

      int currentFrame = this.currentFrame / this.updateTime % this.frames;
      int width = (int)(this.relRenderWidth * (double)guiWidth + (double)this.absRenderWidth);
      int height = (int)(this.relRenderHeight * (double)guiHeight + (double)this.absRenderHeight);
      if (this.preserveAspectRatio) {
         if (width > height) {
            height = width;
         } else {
            width = height;
         }
      }

      this.drawTexture(this.location.getX(guiWidth, width) + (int)(this.relXOffset * (double)guiWidth) + this.absXOffset, this.location.getY(guiHeight, height) + (int)(this.relYOffset * (double)guiWidth) + this.absYOffset, this.imageWidth, this.imageHeight * this.frames, width, height, 0.0D, (double)(this.imageHeight * currentFrame), (double)this.imageWidth, (double)(this.imageHeight * currentFrame + this.imageHeight));
      ++this.currentFrame;
   }

   private boolean loadAndTransformGIF(Minecraft minecraft, ResourceLocation location) {
      IResource resource = null;

      try {
         resource = minecraft.getResourceManager().getResource(location);
         List<ImageData> frameList = GifDecoder.readFrames(EaglerInputStream.inputStreamToBytes(resource.getInputStream()));
         if (frameList.isEmpty()) {
            throw new IOException("No suitable reader found for image" + location);
         }

         int frames = frameList.size();
         ImageData[] images = new ImageData[frames];

         for(int i = 0; i < frames; ++i) {
            images[i] = (ImageData)frameList.get(i);
         }

         int width = images[0].width;
         int height = images[0].height;
         if (height > width && height % width == 0) {
            frames = height / width;
            ImageData original = images[0];
            height = width;
            images = new ImageData[frames];

            for(int i = 0; i < frames; ++i) {
               images[i] = original.getSubImage(0, i * height, width, height);
            }
         }

         this.frames = frames;
         ImageData combined = this.combineBufferedImages(images);
         IOHelper.outputImage(combined, location.getResourcePath().substring(0, location.getResourcePath().length() - 4));
         this.gifTexture = new DynamicTexture(combined);
         return true;
      } catch (Exception var69) {
         LoadingScreens.instance.getLog().error("Could not load animated texture for " + location.toString());
         var69.printStackTrace();
         return false;
      } finally {
         IOUtils.closeQuietly(resource);
      }
   }

   private ImageData combineBufferedImages(ImageData[] images) {
      int singleWidth = images[0].width;
      int singleHeight = images[0].height;
      int totalHeight = singleHeight * images.length;
      ImageData masterImage = new ImageData(singleWidth, totalHeight, true);

      for(int i = 0; i < images.length; ++i) {
         ImageData image = images[i];
         int copyWidth = Math.min(image.width, singleWidth);
         int copyHeight = Math.min(image.height, singleHeight);

         for(int y = 0; y < copyHeight; ++y) {
            for(int x = 0; x < copyWidth; ++x) {
               int src = image.pixels[y * image.width + x];
               int alpha = src >>> 24;
               if (alpha == 255) {
                  masterImage.pixels[(singleHeight * i + y) * singleWidth + x] = src;
               } else if (alpha != 0) {
                  int index = (singleHeight * i + y) * singleWidth + x;
                  int dst = masterImage.pixels[index];
                  int dstAlpha = dst >>> 24;
                  int outAlpha = alpha + dstAlpha * (255 - alpha) / 255;
                  int r = (((src >> 16) & 255) * alpha + ((dst >> 16) & 255) * dstAlpha * (255 - alpha) / 255) / outAlpha;
                  int g = (((src >> 8) & 255) * alpha + ((dst >> 8) & 255) * dstAlpha * (255 - alpha) / 255) / outAlpha;
                  int b = ((src & 255) * alpha + (dst & 255) * dstAlpha * (255 - alpha) / 255) / outAlpha;
                  masterImage.pixels[index] = outAlpha << 24 | r << 16 | g << 8 | b;
               }
            }
         }
      }

      return masterImage;
   }

   public Object call(String method, Object[] args) throws Exception {
      if (method.equals("setUpdateTime") && args.length == 1 && ScriptCall.isNumber(args[0])) {
         this.setUpdateTime(ScriptCall.toInt(args[0]));
         return null;
      }

      return super.call(method, args);
   }
}
