package org.orecruncher.dsurround.client.shader;

import net.lax1dude.eaglercraft.v1_8.opengl.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import org.orecruncher.lib.gfx.shaders.ShaderProgram;

public final class Shaders {
   public static final ShaderProgram AURORA = register("Aurora", new ResourceLocation("dsurround", "shaders/aurora.vert"), new ResourceLocation("dsurround", "shaders/aurora.frag"));

   public Shaders() {
   }

   public static boolean areShadersSupported() {
      return OpenGlHelper.areShadersSupported();
   }

   private static ShaderProgram register(String name, ResourceLocation vertex, ResourceLocation fragment) {
      try {
         return ShaderProgram.createProgram(name, vertex, fragment);
      } catch (Exception var4) {
         var4.printStackTrace();
         return null;
      }
   }
}
