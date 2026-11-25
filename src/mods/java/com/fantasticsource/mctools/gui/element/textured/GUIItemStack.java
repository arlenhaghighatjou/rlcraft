package com.fantasticsource.mctools.gui.element.textured;

import com.fantasticsource.mctools.Render;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.view.GUIPanZoomView;
import java.nio.FloatBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

public class GUIItemStack extends GUIElement {
   private double unscaledWidth;
   private double unscaledHeight;
   private ItemStack stack;

   public GUIItemStack(GUIScreen screen, double unscaledWidth, double unscaledHeight, ItemStack stack) {
      super(screen, 1.0D, 1.0D);
      this.unscaledWidth = unscaledWidth;
      this.unscaledHeight = unscaledHeight;
      this.setItemStack(stack);
   }

   public GUIItemStack(GUIScreen screen, double x, double y, double unscaledWidth, double unscaledHeight, ItemStack stack) {
      super(screen, x, y, 1.0D, 1.0D);
      this.unscaledWidth = unscaledWidth;
      this.unscaledHeight = unscaledHeight;
      this.setItemStack(stack);
   }

   public ItemStack getItemStack() {
      return this.stack;
   }

   public void setItemStack(ItemStack stack) {
      this.stack = stack;
      this.tooltip = stack;
   }

   public GUIItemStack recalc(int subIndexChanged) {
      this.width = this.unscaledWidth / (double)this.screen.width;
      this.height = this.unscaledHeight / (double)this.screen.height;
      if (this.parent != null) {
         this.width /= this.parent.absoluteWidth();
         this.height /= this.parent.absoluteHeight();
      }

      this.recalcAndRepositionSubElements(0);
      this.postRecalc();
      return this;
   }

   public void draw() {
      FloatBuffer projection = Render.getCurrentProjectionMatrix();
      FloatBuffer modelView = Render.getCurrentModelViewMatrix();
      Render.setProjectionMatrix(GUIScreen.mcProjection);
      Render.setModelViewMatrix(GUIScreen.mcModelView);
      GlStateManager.enableTexture2D();
      RenderHelper.enableGUIStandardItemLighting();
      ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
      GlStateManager.pushMatrix();
      GlStateManager.translate((double)this.absolutePxX() / (double)sr.getScaleFactor(), (double)this.absolutePxY() / (double)sr.getScaleFactor(), 0.0D);
      if (this.parent instanceof GUIPanZoomView) {
         GlStateManager.scale(((GUIPanZoomView)this.parent).getZoom(), ((GUIPanZoomView)this.parent).getZoom(), 1.0D);
      }

      Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(this.stack, 0, 0);
      GlStateManager.popMatrix();
      Render.setProjectionMatrix(projection);
      Render.setModelViewMatrix(modelView);
      RenderHelper.disableStandardItemLighting();
      GlStateManager.shadeModel(7425);
      this.drawChildren();
   }
}
