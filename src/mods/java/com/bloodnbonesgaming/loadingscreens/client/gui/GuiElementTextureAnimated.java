package com.bloodnbonesgaming.loadingscreens.client.gui;

import com.bloodnbonesgaming.loadingscreens.IOHelper;
import com.bloodnbonesgaming.loadingscreens.LoadingScreens;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
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
      try {
         IResource resource = minecraft.getResourceManager().getResource(location);
         Throwable var4 = null;

         boolean var73;
         try {
            InputStream input = resource.getInputStream();
            Throwable var6 = null;

            try {
               ImageInputStream imageStream = ImageIO.createImageInputStream(input);
               Throwable var8 = null;

               try {
                  Iterator<ImageReader> readers = ImageIO.getImageReaders(imageStream);
                  if (!readers.hasNext()) {
                     throw new IOException("No suitable reader found for image" + location);
                  }

                  ImageReader reader = (ImageReader)readers.next();
                  reader.setInput(imageStream);
                  int frames = reader.getNumImages(true);
                  BufferedImage[] images = new BufferedImage[frames];

                  for(int i = 0; i < frames; ++i) {
                     images[i] = reader.read(i);
                  }

                  reader.dispose();
                  int width = images[0].getWidth();
                  int height = images[0].getHeight();
                  if (height > width && height % width == 0) {
                     frames = height / width;
                     BufferedImage original = images[0];
                     height = width;
                     images = new BufferedImage[frames];

                     for(int i = 0; i < frames; ++i) {
                        images[i] = original.getSubimage(0, i * height, width, height);
                     }
                  }

                  this.frames = frames;
                  BufferedImage combined = this.combineBufferedImages(images);
                  IOHelper.outputImage(combined, location.getResourcePath().substring(0, location.getResourcePath().length() - 4));
                  this.gifTexture = new DynamicTexture(combined);
                  var73 = true;
               } catch (Throwable var63) {
                  var8 = var63;
                  throw var63;
               } finally {
                  if (imageStream != null) {
                     if (var8 != null) {
                        try {
                           imageStream.close();
                        } catch (Throwable var62) {
                           var8.addSuppressed(var62);
                        }
                     } else {
                        imageStream.close();
                     }
                  }

               }
            } catch (Throwable var65) {
               var6 = var65;
               throw var65;
            } finally {
               if (input != null) {
                  if (var6 != null) {
                     try {
                        input.close();
                     } catch (Throwable var61) {
                        var6.addSuppressed(var61);
                     }
                  } else {
                     input.close();
                  }
               }

            }
         } catch (Throwable var67) {
            var4 = var67;
            throw var67;
         } finally {
            if (resource != null) {
               if (var4 != null) {
                  try {
                     resource.close();
                  } catch (Throwable var60) {
                     var4.addSuppressed(var60);
                  }
               } else {
                  resource.close();
               }
            }

         }

         return var73;
      } catch (Exception var69) {
         LoadingScreens.instance.getLog().error("Could not load animated texture for " + location.toString());
         var69.printStackTrace();
         return false;
      }
   }

   private BufferedImage combineBufferedImages(BufferedImage[] images) {
      int singleWidth = images[0].getWidth();
      int singleHeight = images[0].getHeight();
      int totalHeight = singleHeight * images.length;
      BufferedImage masterImage = new BufferedImage(singleWidth, totalHeight, 2);
      Graphics2D graphics = masterImage.createGraphics();

      for(int i = 0; i < images.length; ++i) {
         graphics.drawImage(images[i], 0, singleHeight * i, (ImageObserver)null);
      }

      return masterImage;
   }
}
