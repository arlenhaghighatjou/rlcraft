package team.creative.enhancedvisuals.api;

import com.creativemd.creativecore.common.config.premade.curve.Curve;
import java.awt.Color;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import team.creative.enhancedvisuals.api.type.VisualType;

public class Visual {
   public final VisualType type;
   public final VisualHandler handler;
   private float opacity;
   public final boolean endless;
   public final Curve animation;
   private boolean displayed = false;
   private int tick = 0;
   public Color color;
   public int variant;

   public Visual(VisualType type, VisualHandler handler, Curve animation, int variant) {
      this.type = type;
      this.handler = handler;
      this.animation = animation;
      this.variant = variant;
      this.endless = false;
      this.color = type.getColor();
   }

   public Visual(VisualType type, VisualHandler handler, int variant) {
      this.type = type;
      this.handler = handler;
      this.animation = null;
      this.variant = variant;
      this.endless = true;
      this.color = type.getColor();
   }

   public void setOpacityInternal(float opacity) {
      this.opacity = opacity;
   }

   public float getOpacityInternal() {
      return this.opacity;
   }

   public float getOpacity() {
      return this.handler.opacity * this.opacity * this.type.opacity;
   }

   public boolean displayed() {
      return this.displayed;
   }

   public void addToDisplay() {
      this.displayed = true;
   }

   public void removeFromDisplay() {
      this.displayed = false;
   }

   public VisualCategory getCategory() {
      return this.type.cat;
   }

   @SideOnly(Side.CLIENT)
   public void render(TextureManager manager, int screenWidth, int screenHeight, float partialTicks) {
      this.type.render(this.handler, this, manager, screenWidth, screenHeight, partialTicks);
   }

   public boolean isVisible() {
      return this.type.isVisible(this.handler, this);
   }

   public boolean tick() {
      if (this.endless) {
         return true;
      } else {
         this.opacity = (float)this.animation.valueAt((double)(this.tick++));
         return this.opacity > 0.0F;
      }
   }

   public int getWidth(int screenWidth) {
      return screenWidth;
   }

   public int getHeight(int screenHeight) {
      return screenHeight;
   }

   public boolean isAffectedByWater() {
      return this.type.isAffectedByWater();
   }
}
