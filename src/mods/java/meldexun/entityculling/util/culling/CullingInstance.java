package meldexun.entityculling.util.culling;

import meldexun.entityculling.EntityCulling;
import meldexun.entityculling.util.ICullable;
import meldexun.entityculling.util.ResourceSupplier;
import meldexun.matrixutil.Matrix4f;
import meldexun.renderlib.util.BufferUtil;
import meldexun.renderlib.util.GLBuffer;
import meldexun.renderlib.util.GLShader;
import meldexun.renderlib.util.GLUtil;
import meldexun.renderlib.util.RenderUtil;
import meldexun.renderlib.util.GLShader.Builder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.CullFace;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL42;

public class CullingInstance {
   private static final int MAX_OBJ_COUNT = 65536;
   private static final String A_POS = "a_Pos";
   private static final String A_OFFSET = "a_Offset";
   private static final String A_SIZE = "a_Size";
   private static final String A_OBJID = "a_ObjID";
   private static final String U_MATRIX = "u_ModelViewProjectionMatrix";
   private static CullingInstance instance;
   private static CullingInstance shadow_instance;
   private final GLShader shader = (new Builder()).addShader(35633, new ResourceSupplier(new ResourceLocation("entityculling", "shaders/vert.glsl"))).addShader(35632, new ResourceSupplier(new ResourceLocation("entityculling", "shaders/frag.glsl"))).build();
   public final int cubeVertexBuffer = GL15.glGenBuffers();
   public final int cubeIndexBuffer;
   private final GLBuffer vboBuffer = new GLBuffer(1835008L, 2, 0, true, 2);
   private final int vao;
   private final GLBuffer cpuSSBO = new GLBuffer(262144L, 513, 0, true, 1);
   private final GLBuffer gpuSSBO = new GLBuffer(262144L, 0, 0);
   private int syncQuery = -1;
   private int objCount;
   private int frame;

   public CullingInstance() {
      GL15.glBindBuffer(34962, this.cubeVertexBuffer);
      GL15.glBufferData(34962, BufferUtil.buffer(new byte[]{0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 1, 1, 0, 0, 1, 0, 1, 1, 1, 0, 1, 1, 1}), 35044);
      GL15.glBindBuffer(34962, 0);
      this.cubeIndexBuffer = GL15.glGenBuffers();
      GL15.glBindBuffer(34963, this.cubeIndexBuffer);
      GL15.glBufferData(34963, BufferUtil.buffer(new byte[]{7, 3, 5, 1, 0, 3, 2, 7, 6, 5, 4, 0, 6, 2}), 35044);
      GL15.glBindBuffer(34963, 0);
      this.vao = GL30.glGenVertexArrays();
      GL30.glBindVertexArray(this.vao);
      GL15.glBindBuffer(34962, this.cubeVertexBuffer);
      GL20.glVertexAttribPointer(this.shader.getAttribute("a_Pos"), 3, 5120, false, 0, 0L);
      GL20.glEnableVertexAttribArray(this.shader.getAttribute("a_Pos"));
      GL15.glBindBuffer(34962, this.vboBuffer.getBuffer());
      GL20.glVertexAttribPointer(this.shader.getAttribute("a_Offset"), 3, 5126, false, 28, 0L);
      GL20.glVertexAttribPointer(this.shader.getAttribute("a_Size"), 3, 5126, false, 28, 12L);
      GL30.glVertexAttribIPointer(this.shader.getAttribute("a_ObjID"), 1, 5124, 28, 24L);
      GL20.glEnableVertexAttribArray(this.shader.getAttribute("a_Offset"));
      GL20.glEnableVertexAttribArray(this.shader.getAttribute("a_Size"));
      GL20.glEnableVertexAttribArray(this.shader.getAttribute("a_ObjID"));
      GL33.glVertexAttribDivisor(this.shader.getAttribute("a_Offset"), 1);
      GL33.glVertexAttribDivisor(this.shader.getAttribute("a_Size"), 1);
      GL33.glVertexAttribDivisor(this.shader.getAttribute("a_ObjID"), 1);
      GL15.glBindBuffer(34962, 0);
      GL15.glBindBuffer(34963, this.cubeIndexBuffer);
      GL30.glBindVertexArray(0);
   }

   public static CullingInstance getInstance() {
      if (instance == null) {
         instance = new CullingInstance();
      }

      return instance;
   }

   public static CullingInstance getShadowInstance() {
      if (shadow_instance == null) {
         shadow_instance = new CullingInstance();
      }

      return shadow_instance;
   }

   public boolean isVisible(ICullable.CullInfo cullInfo) {
      if (!cullInfo.wasLastTimeUpdated(this.frame)) {
         return true;
      } else {
         if (this.syncQuery != -1) {
            GL33.glGetQueryObjecti64(this.syncQuery, 34918);
            GL15.glDeleteQueries(this.syncQuery);
            this.syncQuery = -1;
         }

         return this.cpuSSBO.getBoolean((long)(cullInfo.getId(this.frame) * 4));
      }
   }

   public void addBox(ICullable.CullInfo cullInfo, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      if (cullInfo.getLastTimeUpdated() != this.frame) {
         if (RenderUtil.getCameraX() < minX || RenderUtil.getCameraX() > maxX || RenderUtil.getCameraY() < minY || RenderUtil.getCameraY() > maxY || RenderUtil.getCameraZ() < minZ || RenderUtil.getCameraZ() > maxZ) {
            this.vboBuffer.putFloat((long)(this.objCount * 28), (float)(minX - RenderUtil.getCameraEntityX()));
            this.vboBuffer.putFloat((long)(this.objCount * 28 + 4), (float)(minY - RenderUtil.getCameraEntityY()));
            this.vboBuffer.putFloat((long)(this.objCount * 28 + 8), (float)(minZ - RenderUtil.getCameraEntityZ()));
            this.vboBuffer.putFloat((long)(this.objCount * 28 + 12), (float)(maxX - minX));
            this.vboBuffer.putFloat((long)(this.objCount * 28 + 16), (float)(maxY - minY));
            this.vboBuffer.putFloat((long)(this.objCount * 28 + 20), (float)(maxZ - minZ));
            this.vboBuffer.putInt((long)(this.objCount * 28 + 24), this.objCount);
            cullInfo.setLastTimeUpdated(this.frame);
            cullInfo.setId(this.objCount);
            ++this.objCount;
         }
      }
   }

   public void updateResults(Matrix4f projViewMat) {
      ++this.frame;
      if (this.objCount > 0) {
         EntityCulling.gpuTimer.start();
         GLHelper.clearBufferSubData(this.gpuSSBO.getBuffer(), 0L, (long)(this.objCount * 4), 0);
         GLShader.push();
         this.shader.use();
         GLUtil.setMatrix(this.shader.getUniform("u_ModelViewProjectionMatrix"), projViewMat);
         GL30.glBindBufferBase(37074, 1, this.gpuSSBO.getBuffer());
         this.setupRenderState();
         GL30.glBindVertexArray(this.vao);
         GL31.glDrawElementsInstanced(5, 14, 5121, 0L, this.objCount);
         GL30.glBindVertexArray(0);
         this.clearRenderState();
         GL30.glBindBufferBase(37074, 1, 0);
         GLShader.pop();
         GLHelper.copyBufferSubData(this.gpuSSBO.getBuffer(), this.cpuSSBO.getBuffer(), 0L, 0L, (long)(this.objCount * 4));
         GL42.glMemoryBarrier(16384);
         if (this.syncQuery != -1) {
            GL15.glDeleteQueries(this.syncQuery);
            this.syncQuery = -1;
         }

         GL33.glQueryCounter(this.syncQuery = GL15.glGenQueries(), 36392);
         this.objCount = 0;
         EntityCulling.gpuTimer.stop();
      }

   }

   private void setupRenderState() {
      GLHelper.saveShaderGLState();
      GlStateManager.disableBlend();
      GlStateManager.enableDepth();
      GlStateManager.depthFunc(515);
      GlStateManager.depthMask(false);
      GlStateManager.enableCull();
      GlStateManager.cullFace(CullFace.BACK);
      GlStateManager.colorMask(false, false, false, false);
   }

   private void clearRenderState() {
      GLHelper.restoreShaderGLState();
   }
}
