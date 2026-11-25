package team.creative.enhancedvisuals.api.type;

import com.creativemd.creativecore.common.config.api.CreativeConfig;
import com.creativemd.creativecore.common.config.api.ICreativeConfig;
import com.creativemd.creativecore.common.config.api.CreativeConfig.DecimalRange;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import team.creative.enhancedvisuals.api.Visual;
import team.creative.enhancedvisuals.api.VisualCategory;
import team.creative.enhancedvisuals.api.VisualHandler;

public abstract class VisualType implements ICreativeConfig {
   private static List<VisualType> types = new ArrayList();
   @CreativeConfig
   public boolean disabled = false;
   private boolean isEffectedByWater = true;
   @CreativeConfig
   @DecimalRange(
      max = 1.0D,
      min = 0.0D
   )
   public float opacity = 1.0F;
   public final String name;
   public final VisualCategory cat;

   public static Collection<VisualType> getTypes() {
      return types;
   }

   public VisualType(String name, VisualCategory cat) {
      this.name = name;
      this.cat = cat;
      types.add(this);
   }

   public VisualType setIgnoreWater() {
      this.isEffectedByWater = false;
      return this;
   }

   public boolean isAffectedByWater() {
      return this.cat.isAffectedByWater() && this.isEffectedByWater;
   }

   @SideOnly(Side.CLIENT)
   public abstract void loadResources(IResourceManager var1);

   @SideOnly(Side.CLIENT)
   public abstract void render(VisualHandler var1, Visual var2, TextureManager var3, int var4, int var5, float var6);

   public void configured() {
   }

   @SideOnly(Side.CLIENT)
   public int getVariantAmount() {
      return 1;
   }

   public Color getColor() {
      return null;
   }

   @SideOnly(Side.CLIENT)
   public void resize(Framebuffer buffer) {
   }

   public boolean canRotate() {
      return true;
   }

   public boolean isVisible(VisualHandler handler, Visual visual) {
      return visual.getOpacity() > 0.0F;
   }

   public boolean scaleVariants() {
      return false;
   }

   public double randomScale(Random rand) {
      return 1.0D;
   }

   public int getWidth(int screenWidth) {
      return screenWidth;
   }

   public int getHeight(int screenHeight) {
      return screenHeight;
   }
}
