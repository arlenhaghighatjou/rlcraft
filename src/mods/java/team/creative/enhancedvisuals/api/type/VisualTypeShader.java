package team.creative.enhancedvisuals.api.type;

import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import team.creative.enhancedvisuals.api.Visual;
import team.creative.enhancedvisuals.api.VisualCategory;
import team.creative.enhancedvisuals.api.VisualHandler;
import team.creative.enhancedvisuals.client.render.EnhancedShaderGroup;

public abstract class VisualTypeShader extends VisualType {
   public ResourceLocation location;
   @SideOnly(Side.CLIENT)
   public EnhancedShaderGroup shaderGroup;

   public VisualTypeShader(String name, ResourceLocation location) {
      super(name, VisualCategory.shader);
      this.location = location;
   }

   @SideOnly(Side.CLIENT)
   public void loadResources(IResourceManager manager) {
      if (ShaderLinkHelper.getStaticShaderLinkHelper() != null) {
         Minecraft mc = Minecraft.getMinecraft();
         if (this.shaderGroup != null) {
            this.shaderGroup.deleteShaderGroup();
         }

         try {
            this.shaderGroup = new EnhancedShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), this.location);
            this.shaderGroup.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
         } catch (IOException | JsonSyntaxException var4) {
            var4.printStackTrace();
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public int getVariantAmount() {
      return 0;
   }

   @SideOnly(Side.CLIENT)
   public void resize(Framebuffer buffer) {
      if (this.shaderGroup != null) {
         this.shaderGroup.createBindFramebuffers(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
      }

   }

   public void render(VisualHandler handler, Visual visual, TextureManager manager, int screenWidth, int screenHeight, float partialTicks) {
      if (this.shaderGroup == null) {
         this.loadResources(Minecraft.getMinecraft().getResourceManager());
      }

      if (this.shaderGroup != null) {
         this.changeProperties(visual.getOpacity());
         this.shaderGroup.render(partialTicks);
      }

   }

   @SideOnly(Side.CLIENT)
   public abstract void changeProperties(float var1);
}
