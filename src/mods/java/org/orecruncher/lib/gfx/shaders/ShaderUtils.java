package org.orecruncher.lib.gfx.shaders;

import java.nio.FloatBuffer;
import net.minecraft.client.renderer.GLAllocation;
import org.lwjgl.opengl.GL11;
import net.lax1dude.eaglercraft.v1_8.vector.Matrix4f;

public class ShaderUtils {
   private static final FloatBuffer SCRATCH = GLAllocation.createDirectFloatBuffer(16);

   private ShaderUtils() {
   }

   public static void setModelViewMatrix(String name, ShaderProgram program) throws ShaderException {
      GL11.glGetFloat(2982, SCRATCH);
      program.setMatrix4(name, SCRATCH);
      SCRATCH.clear();
   }

   public static void setProjectionMatrix(String name, ShaderProgram program) throws ShaderException {
      GL11.glGetFloat(2983, SCRATCH);
      program.setMatrix4(name, SCRATCH);
      SCRATCH.clear();
   }

   public static void setMVPMatrix(String name, ShaderProgram program) throws ShaderException {
      Matrix4f mv = new Matrix4f();
      GL11.glGetFloat(2982, SCRATCH);
      mv.load(SCRATCH);
      SCRATCH.clear();
      Matrix4f p = new Matrix4f();
      GL11.glGetFloat(2983, SCRATCH);
      p.load(SCRATCH);
      SCRATCH.clear();
      Matrix4f mvp = new Matrix4f();
      Matrix4f.mul(p, mv, mvp);
      mvp.store(SCRATCH);
      SCRATCH.clear();
      program.setMatrix4(name, SCRATCH);
   }
}
