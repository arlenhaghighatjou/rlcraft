package team.creative.enhancedvisuals.api.type;

import com.creativemd.creativecore.common.config.api.CreativeConfig;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import team.creative.enhancedvisuals.EnhancedVisuals;
import team.creative.enhancedvisuals.api.Visual;
import team.creative.enhancedvisuals.api.VisualCategory;
import team.creative.enhancedvisuals.api.VisualHandler;
import team.creative.enhancedvisuals.client.render.TextureCache;

public abstract class VisualTypeTexture extends VisualType {
   @CreativeConfig
   public int animationSpeed;
   public String domain;
   @SideOnly(Side.CLIENT)
   public TextureCache[] resources;
   @SideOnly(Side.CLIENT)
   public Dimension dimension;

   public VisualTypeTexture(VisualCategory category, String name, String domain, int animationSpeed) {
      super(name, category);
      this.domain = domain;
      this.animationSpeed = animationSpeed;
   }

   public VisualTypeTexture(VisualCategory category, String name, int animationSpeed) {
      this(category, name, (String)null, animationSpeed);
   }

   @SideOnly(Side.CLIENT)
   public void loadResources(IResourceManager manager) {
      String baseLocation = "visuals/" + this.cat.name() + "/" + this.name + "/" + this.name;
      List<TextureCache> caches = new ArrayList();
      int i = 0;
      TextureCache resource = null;
      String domain = this.domain == null ? "enhancedvisuals" : this.domain;

      try {
         while((resource = TextureCache.parse(manager, domain, baseLocation + i)) != null) {
            if (i == 0) {
               BufferedImage image = ImageIO.read(manager.getResource(resource.getFirst()).getInputStream());
               this.dimension = new Dimension(image.getWidth(), image.getHeight());
            }

            caches.add(resource);
            ++i;
         }
      } catch (IOException var8) {
         var8.printStackTrace();
      }

      this.resources = (TextureCache[])caches.toArray(new TextureCache[0]);
      if (this.resources.length == 0) {
         EnhancedVisuals.LOGGER.warn("Could not find any resources for '" + this.name + "'!");
      }

   }

   @SideOnly(Side.CLIENT)
   public int getVariantAmount() {
      return this.resources.length;
   }

   @SideOnly(Side.CLIENT)
   public ResourceLocation getResource(Visual visual) {
      if (this.animationSpeed > 0) {
         long time = Math.abs(System.nanoTime() / 3000000L / (long)this.animationSpeed);
         return this.resources[(int)(time % (long)this.resources.length)].getResource();
      } else {
         return this.resources[visual.variant].getResource();
      }
   }

   @SideOnly(Side.CLIENT)
   public void render(VisualHandler handler, Visual visual, TextureManager manager, int screenWidth, int screenHeight, float partialTicks) {
      manager.bindTexture(this.getResource(visual));
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer renderer = tessellator.getBuffer();
      float red = visual.color != null ? (float)visual.color.getRed() / 255.0F : 1.0F;
      float green = visual.color != null ? (float)visual.color.getGreen() / 255.0F : 1.0F;
      float blue = visual.color != null ? (float)visual.color.getBlue() / 255.0F : 1.0F;
      double z = -90.0D;
      int width = visual.getWidth(screenWidth);
      int height = visual.getHeight(screenHeight);
      float opacity = visual.getOpacity();
      renderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      renderer.pos(0.0D, (double)height, z).tex(0.0D, 1.0D).color(red, green, blue, opacity).endVertex();
      renderer.pos((double)width, (double)height, z).tex(1.0D, 1.0D).color(red, green, blue, opacity).endVertex();
      renderer.pos((double)width, 0.0D, z).tex(1.0D, 0.0D).color(red, green, blue, opacity).endVertex();
      renderer.pos(0.0D, 0.0D, z).tex(0.0D, 0.0D).color(red, green, blue, opacity).endVertex();
      tessellator.draw();
   }

   public int getWidth(int screenWidth) {
      return this.dimension.width;
   }

   public int getHeight(int screenHeight) {
      return this.dimension.height;
   }
}
