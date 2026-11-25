package com.bloodnbonesgaming.loadingscreens.client.gui;

import com.bloodnbonesgaming.loadingscreens.LoadingScreens;
import java.awt.image.BufferedImage;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

public abstract class GuiElementTexture extends GuiElementBase {
   protected final ResourceLocation texture;
   protected int imageWidth;
   protected int imageHeight;
   protected int absRenderWidth = 0;
   protected int absRenderHeight = 0;
   protected double relRenderWidth = 0.0D;
   protected double relRenderHeight = 0.0D;
   protected boolean preserveAspectRatio = false;

   public GuiElementTexture(EnumGuiLocation location, ResourceLocation texture) {
      super(location);
      this.texture = texture;
      int width = 0;
      int height = 0;
      IResource iresource = null;

      try {
         iresource = Minecraft.getMinecraft().getResourceManager().getResource(texture);
         BufferedImage bufferedimage = TextureUtil.readBufferedImage(iresource.getInputStream());
         width = bufferedimage.getWidth();
         height = bufferedimage.getHeight();
      } catch (IOException var10) {
         LoadingScreens.instance.getLog().error("Could not load texture for " + texture.toString());
      } finally {
         IOUtils.closeQuietly(iresource);
      }

      this.imageWidth = width;
      this.imageHeight = height;
   }

   public GuiElementTexture(EnumGuiLocation location, ResourceLocation texture, int imageWidth, int imageHeight) {
      super(location);
      this.texture = texture;
      this.imageWidth = imageWidth;
      this.imageHeight = imageHeight;
   }

   public void setAbsRenderWidth(int width) {
      this.absRenderWidth = width;
   }

   public void setAbsRenderHeight(int height) {
      this.absRenderHeight = height;
   }

   public void setAbsRender(int width, int height) {
      this.absRenderWidth = width;
      this.absRenderHeight = height;
   }

   public void setRelRenderWidth(double width) {
      this.relRenderWidth = width;
   }

   public void setRelRenderHeight(double height) {
      this.relRenderHeight = height;
   }

   public void setRelRender(double width, double height) {
      this.relRenderWidth = width;
      this.relRenderHeight = height;
   }

   public void preserveAspectRatio() {
      this.preserveAspectRatio = true;
   }

   public void render(Minecraft minecraft, int guiWidth, int guiHeight) {
      GlStateManager.disableLighting();
      GlStateManager.disableFog();
      minecraft.getTextureManager().bindTexture(this.texture);
      int width = (int)(this.relRenderWidth * (double)guiWidth + (double)this.absRenderWidth);
      int height = (int)(this.relRenderHeight * (double)guiHeight + (double)this.absRenderHeight);
      if (this.preserveAspectRatio) {
         if (width > height) {
            height = width;
         } else {
            width = height;
         }
      }

      this.drawTexture(this.location.getX(guiWidth, width) + (int)(this.relXOffset * (double)guiWidth) + this.absXOffset, this.location.getY(guiHeight, height) + (int)(this.relYOffset * (double)guiWidth) + this.absYOffset, this.imageWidth, this.imageHeight, width, height, 0.0D, 0.0D, (double)this.imageWidth, (double)this.imageHeight);
   }

   abstract void drawTexture(int var1, int var2, int var3, int var4, int var5, int var6, double var7, double var9, double var11, double var13);
}
