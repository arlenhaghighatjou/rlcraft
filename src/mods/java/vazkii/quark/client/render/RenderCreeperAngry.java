package vazkii.quark.client.render;

import javax.annotation.Nonnull;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.entity.RenderCreeper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderCreeperAngry extends RenderCreeper {
   public RenderCreeperAngry(RenderManager renderManagerIn) {
      super(renderManagerIn);
   }

   protected void renderModel(@Nonnull EntityCreeper living, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      GlStateManager.color(1.0F, 1.0F, 1.0F);
      super.renderModel(living, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
      GlStateManager.color(1.0F, 1.0F, 1.0F);
   }

   protected ResourceLocation getEntityTexture(EntityCreeper entity) {
      if (entity.getCreeperState() == 1) {
         float inverseRed = 1.0F - (entity.getCreeperFlashIntensity(0.0F) / 1.17F + 0.1F);
         GL11.glColor3f(1.0F, inverseRed, inverseRed);
      }

      return super.getEntityTexture(entity);
   }

   public static IRenderFactory<EntityCreeper> factory() {
      return RenderCreeperAngry::new;
   }
}
