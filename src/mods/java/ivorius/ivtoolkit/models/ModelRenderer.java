package ivorius.ivtoolkit.models;

import ivorius.ivtoolkit.models.attributes.BlendingAttribute;
import ivorius.ivtoolkit.models.attributes.ColorAttribute;
import ivorius.ivtoolkit.models.attributes.TextureAttribute;
import ivorius.ivtoolkit.models.data.IndexData;
import ivorius.ivtoolkit.models.data.VertexAttribute;
import ivorius.ivtoolkit.models.data.VertexAttributes;
import ivorius.ivtoolkit.models.data.VertexData;
import ivorius.ivtoolkit.models.textures.Texture;
import ivorius.ivtoolkit.models.utils.MathUtils;
import ivorius.ivtoolkit.models.utils.MatrixMathUtils;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.lax1dude.eaglercraft.v1_8.vector.Matrix4f;
import net.lax1dude.eaglercraft.v1_8.vector.Vector4f;

public class ModelRenderer {
   private static final FloatBuffer MATRIX_BUFFER = GLAllocation.createDirectFloatBuffer(16);
   private static final Matrix4f TEMP_MATRIX = new Matrix4f();
   private static final Vector4f TEMP_VEC = new Vector4f();
   private static final float[] WHITE = new float[]{1.0F, 1.0F, 1.0F};

   public ModelRenderer() {
   }

   public static void renderModelDirectly(Model model) {
      WorldRenderer renderer = Tessellator.getInstance().getBuffer();
      model.calculateTransforms();
      model.nodes.stream().filter((node) -> {
         return node.parts.size() > 0;
      }).forEach((node) -> {
         GlStateManager.pushMatrix();
         MatrixMathUtils.setTRS(TEMP_MATRIX, node.translation, node.rotation, node.scale);
         glMultMatrix(TEMP_MATRIX);

         for(NodePart nodePart : node.parts) {
            renderNodePart(renderer, nodePart);
         }

         GlStateManager.popMatrix();
      });
   }

   private static void renderNodePart(WorldRenderer renderer, NodePart nodePart) {
      MeshPart meshPart = nodePart.meshPart;
      Material material = nodePart.material;
      Matrix4f[] bones = nodePart.bones;
      Mesh mesh = meshPart.mesh;
      IndexData indexData = mesh.getIndices();
      ShortBuffer indexBuf = indexData.getBuffer();
      VertexData vertexData = mesh.getVertices();
      FloatBuffer vertexBuf = vertexData.getBuffer();
      VertexAttributes vertexAttributes = vertexData.getAttributes();
      VertexAttribute posAttr = vertexAttributes.findByUsage(1);
      int vertexLengthInFloats = vertexAttributes.vertexSize >> 2;
      VertexAttribute textureCoordAttr = null;
      float[] rgb = WHITE;
      Texture texture = null;
      float[] uvs = null;
      if (material.has(TextureAttribute.Diffuse)) {
         TextureAttribute textureAttr = (TextureAttribute)material.get(TextureAttribute.class, TextureAttribute.Diffuse);
         texture = textureAttr.texture;
         textureCoordAttr = vertexAttributes.findByUsageAndUnit(16, 0);
         if (textureCoordAttr == null) {
            uvs = guessUVs(meshPart.primitiveType, texture, meshPart.numVertices);
         }
      } else if (material.has(ColorAttribute.Diffuse)) {
         ColorAttribute diffuse = (ColorAttribute)material.get(ColorAttribute.class, ColorAttribute.Diffuse);
         rgb = new float[]{(float)diffuse.color.getRed() / 255.0F, (float)diffuse.color.getGreen() / 255.0F, (float)diffuse.color.getBlue() / 255.0F};
      }

      BlendingAttribute blend = (BlendingAttribute)material.get(BlendingAttribute.class, BlendingAttribute.Type);
      if (blend != null) {
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(blend.sourceFunction, blend.destFunction);
         GlStateManager.color(rgb[0], rgb[1], rgb[2], blend.opacity);
      } else {
         GlStateManager.color(rgb[0], rgb[1], rgb[2]);
      }

      if (texture != null) {
         texture.bindTexture();
      } else {
         GlStateManager.disableTexture2D();
      }

      List<VertexAttribute> boneWeightAttributes = new ArrayList();

      for(VertexAttribute attribute : vertexAttributes) {
         if (attribute.usage == 64) {
            boneWeightAttributes.add(attribute);
         }
      }

      renderer.begin(meshPart.primitiveType, DefaultVertexFormats.POSITION_TEX);

      for(int i = meshPart.indexOffset; i < meshPart.numVertices + meshPart.indexOffset; ++i) {
         int vertexIndex = indexBuf.get(i) * vertexLengthInFloats;
         if (texture != null) {
            if (textureCoordAttr != null) {
               int textureIndex = vertexIndex + (textureCoordAttr.offset >> 2);
               renderer.tex((double)MathUtils.mix(texture.minU(), texture.maxU(), vertexBuf.get(textureIndex)), (double)MathUtils.mix(texture.minV(), texture.maxV(), vertexBuf.get(textureIndex + 1)));
            } else if (uvs != null) {
               renderer.tex((double)uvs[i * 2], (double)uvs[i * 2 + 1]);
            }
         }

         int posIndex = vertexIndex + (posAttr.offset >> 2);
         float vertexX = vertexBuf.get(posIndex);
         float vertexY = vertexBuf.get(posIndex + 1);
         float vertexZ = vertexBuf.get(posIndex + 2);
         if (boneWeightAttributes.size() > 0) {
            buildMatrix(TEMP_MATRIX, boneWeightAttributes, vertexBuf, vertexIndex, bones);
            TEMP_VEC.set(vertexX, vertexY, vertexZ, 1.0F);
            Matrix4f.transform(TEMP_MATRIX, TEMP_VEC, TEMP_VEC);
            renderer.pos((double)TEMP_VEC.x, (double)TEMP_VEC.y, (double)TEMP_VEC.z).endVertex();
         } else {
            renderer.pos((double)vertexX, (double)vertexY, (double)vertexZ).endVertex();
         }
      }

      Tessellator.getInstance().draw();
      if (texture == null) {
         GlStateManager.enableTexture2D();
      }

      if (blend != null) {
         GlStateManager.disableBlend();
      }

   }

   public static void buildMatrix(Matrix4f dst, Collection<VertexAttribute> boneWeightAttributes, FloatBuffer floatBuffer, int vertexIndex, Matrix4f[] bones) {
      dst.setZero();

      for(VertexAttribute attribute : boneWeightAttributes) {
         int attributeIndex = vertexIndex + (attribute.offset >> 2);
         float alpha = floatBuffer.get(attributeIndex + 1);
         if (alpha > 1.0E-5F) {
            Matrix4f bone = bones[(int)floatBuffer.get(attributeIndex)];
            MatrixMathUtils.add(bone, dst, alpha);
         }
      }

   }

   public static void glMultMatrix(Matrix4f matrix4f) {
      matrix4f.store(MATRIX_BUFFER);
      MATRIX_BUFFER.position(0);
      GlStateManager.multMatrix(MATRIX_BUFFER);
      MATRIX_BUFFER.rewind();
   }

   public static float[] guessUVs(int primitiveType, Texture texture, int length) {
      float[] uvs = new float[length * 2];

      for(int i = 0; i < uvs.length / 2; ++i) {
         switch(i % 4) {
         case 0:
            uvs[i * 2] = texture.minU();
            uvs[i * 2 + 1] = texture.minV();
            break;
         case 1:
            uvs[i * 2] = texture.maxU();
            uvs[i * 2 + 1] = texture.minV();
            break;
         case 2:
            uvs[i * 2] = texture.maxU();
            uvs[i * 2 + 1] = texture.maxV();
            break;
         case 3:
            uvs[i * 2] = texture.minU();
            uvs[i * 2 + 1] = texture.maxV();
         }
      }

      return uvs;
   }
}
