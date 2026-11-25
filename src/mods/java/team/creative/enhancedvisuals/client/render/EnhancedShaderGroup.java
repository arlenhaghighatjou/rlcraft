package team.creative.enhancedvisuals.client.render;

import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class EnhancedShaderGroup extends ShaderGroup {
   private static Field shaders = ObfuscationReflectionHelper.findField(ShaderGroup.class, "listShaders");

   public EnhancedShaderGroup(TextureManager p_i1050_1_, IResourceManager resourceManagerIn, Framebuffer mainFramebufferIn, ResourceLocation p_i1050_4_) throws IOException, JsonSyntaxException {
      super(p_i1050_1_, resourceManagerIn, mainFramebufferIn, p_i1050_4_);
   }

   public List<Shader> getShaders() {
      try {
         return (List)shaders.get(this);
      } catch (IllegalAccessException | IllegalArgumentException var2) {
         return new ArrayList();
      }
   }
}
