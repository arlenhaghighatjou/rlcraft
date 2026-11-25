package meldexun.entityculling.util;

import meldexun.entityculling.util.culling.GLHelper;
import meldexun.matrixutil.Matrix4f;
import meldexun.renderlib.api.IBoundingBoxCache;
import meldexun.renderlib.util.BufferUtil;
import meldexun.renderlib.util.GLShader;
import meldexun.renderlib.util.GLUtil;
import meldexun.renderlib.util.MutableAABB;
import meldexun.renderlib.util.RenderUtil;
import meldexun.renderlib.util.GLShader.Builder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

public class BoundingBoxHelper {
   private static final String A_POS = "a_Pos";
   private static final String U_MATRIX = "u_ModelViewProjectionMatrix";
   private static BoundingBoxHelper instance;
   private final GLShader shader = (new Builder()).addShader(35633, new ResourceSupplier(new ResourceLocation("entityculling", "shaders/debug.vsh"))).addShader(35632, new ResourceSupplier(new ResourceLocation("entityculling", "shaders/debug.fsh"))).bindAttribute("a_Pos", 0).build();
   private final int cubeVertexBuffer = GL15.glGenBuffers();
   private final int triangleStripCubeIndexBuffer;
   private final int linesCubeIndexBuffer;
   private final int quadsCubeIndexBuffer;

   public BoundingBoxHelper() {
      GL15.glBindBuffer(34962, this.cubeVertexBuffer);
      GL15.glBufferData(34962, BufferUtil.buffer(new byte[]{0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 1, 1, 0, 0, 1, 0, 1, 1, 1, 0, 1, 1, 1}), 35044);
      GL15.glBindBuffer(34962, 0);
      this.triangleStripCubeIndexBuffer = GL15.glGenBuffers();
      GL15.glBindBuffer(34963, this.triangleStripCubeIndexBuffer);
      GL15.glBufferData(34963, BufferUtil.buffer(new byte[]{7, 3, 5, 1, 0, 3, 2, 7, 6, 5, 4, 0, 6, 2}), 35044);
      GL15.glBindBuffer(34963, 0);
      this.linesCubeIndexBuffer = GL15.glGenBuffers();
      GL15.glBindBuffer(34963, this.linesCubeIndexBuffer);
      GL15.glBufferData(34963, BufferUtil.buffer(new byte[]{0, 1, 1, 5, 5, 4, 4, 0, 0, 2, 1, 3, 5, 7, 4, 6, 2, 3, 3, 7, 7, 6, 6, 2}), 35044);
      GL15.glBindBuffer(34963, 0);
      this.quadsCubeIndexBuffer = GL15.glGenBuffers();
      GL15.glBindBuffer(34963, this.quadsCubeIndexBuffer);
      GL15.glBufferData(34963, BufferUtil.buffer(new byte[]{0, 4, 5, 1, 3, 7, 6, 2, 4, 0, 2, 6, 1, 5, 7, 3, 0, 1, 3, 2, 5, 4, 6, 7}), 35044);
      GL15.glBindBuffer(34963, 0);
   }

   public static BoundingBoxHelper getInstance() {
      if (instance == null) {
         instance = new BoundingBoxHelper();
      }

      return instance;
   }

   public void drawRenderBoxes(double partialTicks) {
      this.setupRenderState();
      GLShader.push();
      this.shader.use();
      GL15.glBindBuffer(34963, this.quadsCubeIndexBuffer);
      GL15.glBindBuffer(34962, this.cubeVertexBuffer);
      GL20.glVertexAttribPointer(this.shader.getAttribute("a_Pos"), 3, 5120, false, 0, 0L);
      GL20.glEnableVertexAttribArray(this.shader.getAttribute("a_Pos"));
      Minecraft mc = Minecraft.getMinecraft();

      for(Entity e : mc.world.loadedEntityList) {
         if (e != mc.getRenderViewEntity()) {
            MutableAABB aabb = ((IBoundingBoxCache)e).getCachedBoundingBox();
            if (aabb.isVisible(RenderUtil.getFrustum())) {
               Matrix4f matrix = RenderUtil.getProjectionModelViewMatrix().copy();
               matrix.translate((float)(aabb.minX() - RenderUtil.getCameraEntityX()), (float)(aabb.minY() - RenderUtil.getCameraEntityY()), (float)(aabb.minZ() - RenderUtil.getCameraEntityZ()));
               matrix.scale((float)aabb.sizeX(), (float)aabb.sizeY(), (float)aabb.sizeZ());
               GLUtil.setMatrix(this.shader.getUniform("u_ModelViewProjectionMatrix"), matrix);
               GL11.glDrawElements(7, 24, 5121, 0L);
               GL11.glPolygonMode(1032, 6913);
               GL11.glDrawElements(7, 24, 5121, 0L);
               GL11.glPolygonMode(1032, 6914);
            }
         }
      }

      for(TileEntity te : mc.world.loadedTileEntityList) {
         MutableAABB aabb = ((IBoundingBoxCache)te).getCachedBoundingBox();
         if (aabb.isVisible(RenderUtil.getFrustum())) {
            Matrix4f matrix = RenderUtil.getProjectionModelViewMatrix().copy();
            matrix.translate((float)(aabb.minX() - RenderUtil.getCameraEntityX()), (float)(aabb.minY() - RenderUtil.getCameraEntityY()), (float)(aabb.minZ() - RenderUtil.getCameraEntityZ()));
            matrix.scale((float)aabb.sizeX(), (float)aabb.sizeY(), (float)aabb.sizeZ());
            GLUtil.setMatrix(this.shader.getUniform("u_ModelViewProjectionMatrix"), matrix);
            GL11.glDrawElements(7, 24, 5121, 0L);
            GL11.glPolygonMode(1032, 6913);
            GL11.glDrawElements(7, 24, 5121, 0L);
            GL11.glPolygonMode(1032, 6914);
         }
      }

      GL20.glDisableVertexAttribArray(this.shader.getAttribute("a_Pos"));
      GL15.glBindBuffer(34962, 0);
      GL15.glBindBuffer(34963, 0);
      GLShader.pop();
      this.clearRenderState();
   }

   private void setupRenderState() {
      GLHelper.saveShaderGLState();
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.enableDepth();
      GlStateManager.depthFunc(515);
      GlStateManager.depthMask(false);
      GlStateManager.disableCull();
      GlStateManager.colorMask(true, true, true, true);
   }

   private void clearRenderState() {
      GLHelper.restoreShaderGLState();
   }
}
