package hunternif.mc.atlas.client.gui;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import hunternif.mc.atlas.client.Textures;
import hunternif.mc.atlas.client.gui.core.GuiComponent;
import hunternif.mc.atlas.util.AtlasRenderHelper;
import java.util.Collections;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class GuiScaleBar extends GuiComponent {
   private static final int WIDTH = 20;
   private static final int HEIGHT = 8;
   private static final Map<Double, ResourceLocation> textureMap;
   private double mapScale = 1.0D;

   GuiScaleBar() {
      this.setSize(20, 8);
   }

   void setMapScale(double scale) {
      this.mapScale = scale;
   }

   private ResourceLocation getTexture() {
      return (ResourceLocation)textureMap.get(Double.valueOf(this.mapScale));
   }

   public void drawScreen(int mouseX, int mouseY, float partialTick) {
      ResourceLocation texture = this.getTexture();
      if (texture != null) {
         AtlasRenderHelper.drawFullTexture(texture, (double)this.getGuiX(), (double)this.getGuiY(), 20, 8);
         if (this.isMouseOver) {
            this.drawTooltip(Collections.singletonList(I18n.format("gui.antiqueatlas.scalebar", new Object[0])), Minecraft.getMinecraft().fontRenderer);
         }

      }
   }

   static {
      Builder<Double, ResourceLocation> builder = ImmutableMap.builder();
      builder.put(Double.valueOf(0.0625D), Textures.SCALEBAR_512);
      builder.put(Double.valueOf(0.125D), Textures.SCALEBAR_256);
      builder.put(Double.valueOf(0.25D), Textures.SCALEBAR_128);
      builder.put(Double.valueOf(0.5D), Textures.SCALEBAR_64);
      builder.put(Double.valueOf(1.0D), Textures.SCALEBAR_32);
      builder.put(Double.valueOf(2.0D), Textures.SCALEBAR_16);
      builder.put(Double.valueOf(4.0D), Textures.SCALEBAR_8);
      builder.put(Double.valueOf(8.0D), Textures.SCALEBAR_4);
      textureMap = builder.build();
   }
}
