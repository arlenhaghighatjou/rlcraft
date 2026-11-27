package com.bloodnbonesgaming.loadingscreens.client.gui;

import com.bloodnbonesgaming.lib.util.script.ScriptCall;
import com.bloodnbonesgaming.loadingscreens.LoadingScreens;
import net.lax1dude.eaglercraft.v1_8.opengl.ImageData;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.lax1dude.eaglercraft.v1_8.IOUtils;

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
         ImageData bufferedimage = TextureUtil.readBufferedImage(iresource.getInputStream());
         width = bufferedimage.width;
         height = bufferedimage.height;
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

   public Object call(String method, Object[] args) throws Exception {
      if (args.length == 0 && method.equals("preserveAspectRatio")) {
         this.preserveAspectRatio();
         return null;
      }

      if (args.length == 1 && ScriptCall.isNumber(args[0])) {
         if (method.equals("setAbsRenderWidth")) {
            this.setAbsRenderWidth(ScriptCall.toInt(args[0]));
            return null;
         }

         if (method.equals("setAbsRenderHeight")) {
            this.setAbsRenderHeight(ScriptCall.toInt(args[0]));
            return null;
         }

         if (method.equals("setRelRenderWidth")) {
            this.setRelRenderWidth(ScriptCall.toDouble(args[0]));
            return null;
         }

         if (method.equals("setRelRenderHeight")) {
            this.setRelRenderHeight(ScriptCall.toDouble(args[0]));
            return null;
         }
      }

      if (args.length == 2 && ScriptCall.isNumber(args[0]) && ScriptCall.isNumber(args[1])) {
         if (method.equals("setAbsRender")) {
            this.setAbsRender(ScriptCall.toInt(args[0]), ScriptCall.toInt(args[1]));
            return null;
         }

         if (method.equals("setRelRender")) {
            this.setRelRender(ScriptCall.toDouble(args[0]), ScriptCall.toDouble(args[1]));
            return null;
         }
      }

      return super.call(method, args);
   }
}
