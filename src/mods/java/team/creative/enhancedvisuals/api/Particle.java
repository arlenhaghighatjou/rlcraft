package team.creative.enhancedvisuals.api;

import com.creativemd.creativecore.common.config.premade.curve.Curve;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import team.creative.enhancedvisuals.api.type.VisualType;

public class Particle extends Visual {
   public int x;
   public int y;
   public int width;
   public int height;
   public float rotation;

   public Particle(VisualType type, VisualHandler handler, Curve animation, int x, int y, int width, int height, float rotation, int variant) {
      super(type, handler, animation, variant);
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      this.rotation = rotation;
   }

   public void render(TextureManager manager, int screenWidth, int screenHeight, float partialTicks) {
      GlStateManager.translate((float)(this.x + this.width / 2), (float)(this.y + this.height / 2), 0.0F);
      GlStateManager.rotate(this.rotation, 0.0F, 0.0F, 1.0F);
      super.render(manager, screenWidth, screenHeight, partialTicks);
   }

   public int getWidth(int screenWidth) {
      return this.width;
   }

   public int getHeight(int screenHeight) {
      return this.height;
   }
}
