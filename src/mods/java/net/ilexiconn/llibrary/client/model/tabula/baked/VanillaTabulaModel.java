package net.ilexiconn.llibrary.client.model.tabula.baked;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.vecmath.Point2f;
import javax.vecmath.Point2i;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaCubeContainer;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaModelContainer;
import net.ilexiconn.llibrary.client.util.Matrix;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.lax1dude.eaglercraft.v1_8.opengl.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class VanillaTabulaModel implements IModel {
   private TabulaModelContainer model;
   private ResourceLocation particle;
   private ImmutableList<ResourceLocation> textures;
   private ImmutableMap<TransformType, TRSRTransformation> transforms;

   public VanillaTabulaModel(TabulaModelContainer model, ResourceLocation particle, ImmutableList<ResourceLocation> textures, ImmutableMap<TransformType, TRSRTransformation> transforms) {
      this.model = model;
      this.particle = particle;
      this.textures = textures;
      this.transforms = transforms;
   }

   public Collection<ResourceLocation> getDependencies() {
      return ImmutableList.of();
   }

   public Collection<ResourceLocation> getTextures() {
      return this.textures;
   }

   public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, EaglerTextureAtlasSprite> bakedTextureGetter) {
      List<ResourceLocation> locations = Lists.newArrayList(this.textures);
      if (locations.isEmpty()) {
         locations.add(new ResourceLocation("missingno"));
      }

      Builder<BakedQuad> builder = ImmutableList.builder();
      EaglerTextureAtlasSprite particleSprite = (EaglerTextureAtlasSprite)bakedTextureGetter.apply(this.particle == null ? (ResourceLocation)locations.get(0) : this.particle);
      int layer = 0;

      for(ResourceLocation resourceLocation : locations) {
         Matrix matrix = new Matrix();
         EaglerTextureAtlasSprite sprite = (EaglerTextureAtlasSprite)bakedTextureGetter.apply(resourceLocation);
         TRSRTransformation transformation = (TRSRTransformation)state.apply(Optional.empty()).orElse(TRSRTransformation.identity());
         matrix.multiply(transformation.getMatrix());
         matrix.translate(0.5D, 1.5D, 0.5D);
         matrix.scale(-0.0625D, -0.0625D, 0.0625D);
         this.build(matrix, builder, format, this.model.getCubes(), sprite, layer++);
      }

      ImmutableList<BakedQuad> leQuads = builder.build();
      return new BakedTabulaModel(leQuads, particleSprite, this.transforms);
   }

   private void build(Matrix mat, Builder<BakedQuad> builder, VertexFormat format, List<TabulaCubeContainer> cubeContainerList, EaglerTextureAtlasSprite sprite, int layer) {
      for(TabulaCubeContainer cube : cubeContainerList) {
         int[] dimensions = cube.getDimensions();
         double[] position = cube.getPosition();
         double[] offset = cube.getOffset();
         double[] rotation = cube.getRotation();
         double[] glScale = cube.getScale();
         int[] txOffset = cube.getTextureOffset();
         boolean hasTransparency = this.hasTransparency(cube, sprite);
         mat.push();
         mat.translate(position[0], position[1], position[2]);
         if (glScale[0] != 1.0D || glScale[1] != 1.0D || glScale[2] != 1.0D) {
            mat.scale(glScale[0], glScale[1], glScale[2]);
         }

         if (rotation[2] != 0.0D) {
            mat.rotate(rotation[2], 0.0D, 0.0D, 1.0D);
         }

         if (rotation[1] != 0.0D) {
            mat.rotate(rotation[1], 0.0D, 1.0D, 0.0D);
         }

         if (rotation[0] != 0.0D) {
            mat.rotate(rotation[0], 1.0D, 0.0D, 0.0D);
         }

         float x = (float)offset[0];
         float y = (float)offset[1];
         float z = (float)offset[2];
         float s = (float)cube.getMCScale();
         int w = dimensions[0];
         int h = dimensions[1];
         int d = dimensions[2];
         float x0 = x - s;
         float y0 = y - s;
         float z0 = z - s;
         float x1 = x + s + (float)w;
         float y1 = y + s + (float)h;
         float z1 = z + s + (float)d;
         boolean isTxMirror = cube.isTextureMirrorEnabled();
         if (isTxMirror) {
            float x1_ = x1;
            x1 = x0;
            x0 = x1_;
         }

         Point3f vertex000 = new Point3f(x0, y0, z0);
         Point3f vertex100 = new Point3f(x1, y0, z0);
         Point3f vertex110 = new Point3f(x1, y1, z0);
         Point3f vertex010 = new Point3f(x0, y1, z0);
         Point3f vertex001 = new Point3f(x0, y0, z1);
         Point3f vertex101 = new Point3f(x1, y0, z1);
         Point3f vertex111 = new Point3f(x1, y1, z1);
         Point3f vertex011 = new Point3f(x0, y1, z1);
         mat.transform(vertex000);
         mat.transform(vertex100);
         mat.transform(vertex110);
         mat.transform(vertex010);
         mat.transform(vertex001);
         mat.transform(vertex101);
         mat.transform(vertex111);
         mat.transform(vertex011);
         int u = txOffset[0];
         int v = txOffset[1];
         Point2i rightMinUV = new Point2i(u + d + w, v + d);
         Point2i rightMaxUV = new Point2i(u + d + w + d, v + d + h);
         Point2i leftMinUV = new Point2i(u, v + d);
         Point2i leftMaxUV = new Point2i(u + d, v + d + h);
         Point2i topMinUV = new Point2i(u + d, v);
         Point2i topMaxUV = new Point2i(u + d + w + w, v);
         Point2i frontMinUV = new Point2i(u + d, v + d);
         Point2i frontMaxUV = new Point2i(u + d + w, v + d + h);
         Point2i backMinUV = new Point2i(u + d + w + d, v + d);
         Point2i backMaxUV = new Point2i(u + d + w + d + w, v + d + h);
         this.buildQuad(builder, format, isTxMirror, vertex101, vertex100, vertex110, vertex111, rightMinUV, rightMaxUV, sprite, hasTransparency, layer);
         this.buildQuad(builder, format, isTxMirror, vertex000, vertex001, vertex011, vertex010, leftMinUV, leftMaxUV, sprite, hasTransparency, layer);
         this.buildQuad(builder, format, isTxMirror, vertex101, vertex001, vertex000, vertex100, topMinUV, rightMinUV, sprite, hasTransparency, layer);
         this.buildQuad(builder, format, isTxMirror, vertex110, vertex010, vertex011, vertex111, rightMinUV, topMaxUV, sprite, hasTransparency, layer);
         this.buildQuad(builder, format, isTxMirror, vertex100, vertex000, vertex010, vertex110, frontMinUV, frontMaxUV, sprite, hasTransparency, layer);
         this.buildQuad(builder, format, isTxMirror, vertex001, vertex101, vertex111, vertex011, backMinUV, backMaxUV, sprite, hasTransparency, layer);
         this.build(mat, builder, format, cube.getChildren(), sprite, layer);
         mat.pop();
      }

   }

   private boolean hasTransparency(TabulaCubeContainer cube, EaglerTextureAtlasSprite sprite) {
      int textureWidth = this.model.getTextureWidth();
      int textureHeight = this.model.getTextureHeight();
      int width = sprite.getIconWidth();
      int height = sprite.getIconHeight();
      int frameCount = sprite.getFrameCount();
      if (frameCount > 0) {
         for(int i = 0; i < frameCount; ++i) {
            int[] pixels = sprite.getFrameTextureData(i)[0];
            int[] textureOffset = cube.getTextureOffset();
            int[] dimensions = cube.getDimensions();
            int textureX = textureOffset[0] * width / textureWidth;
            int textureY = textureOffset[1] * height / textureHeight;
            int dimensionX = dimensions[0] * width / textureWidth;
            int dimensionY = dimensions[1] * height / textureHeight;
            int dimensionZ = dimensions[2] * width / textureWidth;
            boolean hasTransparencyTop = this.hasTransparency(pixels, textureX + dimensionZ, textureY, dimensionX * 2, dimensionZ, width, height);
            boolean hasTransparencyBottom = this.hasTransparency(pixels, textureX, textureY + dimensionZ, (dimensionX + dimensionZ) * 2, dimensionY, width, height);
            if (hasTransparencyTop || hasTransparencyBottom) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean hasTransparency(int[] pixels, int minX, int minY, int dimensionX, int dimensionY, int width, int height) {
      int maxX = Math.min(width, minX + dimensionX);
      int maxY = Math.min(height, minY + dimensionY);

      for(int x = Math.max(0, minX); x < maxX; ++x) {
         for(int y = Math.max(0, minY); y < maxY; ++y) {
            int pixel = pixels[x + y * width];
            int alpha = pixel >> 24 & 255;
            if (alpha < 255) {
               return true;
            }
         }
      }

      return false;
   }

   private void buildQuad(Builder<BakedQuad> builder, VertexFormat format, boolean isTxMirror, Point3f vert0, Point3f vert1, Point3f vert2, Point3f vert3, Point2i minUV, Point2i maxUV, EaglerTextureAtlasSprite sprite, boolean hasTransparency, int layer) {
      Point3f[] vertices = new Point3f[]{vert0, vert1, vert2, vert3};
      if (!this.isQuadOneDimensional(vertices)) {
         Point2i[] uvs = new Point2i[]{new Point2i(maxUV.x, minUV.y), new Point2i(minUV.x, minUV.y), new Point2i(minUV.x, maxUV.y), new Point2i(maxUV.x, maxUV.y)};
         if (isTxMirror) {
            Point3f[] verticesMirrored = new Point3f[vertices.length];
            Point2i[] uvsMirrored = new Point2i[vertices.length];
            int i = 0;

            for(int j = vertices.length - 1; i < vertices.length; --j) {
               verticesMirrored[i] = vertices[j];
               uvsMirrored[i] = uvs[j];
               ++i;
            }

            vertices = verticesMirrored;
            uvs = uvsMirrored;
         }

         Vector3f v01 = new Vector3f();
         Vector3f v21 = new Vector3f();
         Vector3f normal = new Vector3f();
         v01.sub(vertices[0], vertices[1]);
         v21.sub(vertices[2], vertices[1]);
         normal.cross(v21, v01);
         normal.normalize();
         net.minecraftforge.client.model.pipeline.UnpackedBakedQuad.Builder quadBuilder = new net.minecraftforge.client.model.pipeline.UnpackedBakedQuad.Builder(format);
         EnumFacing quadFacing = EnumFacing.getFacingFromVector(normal.x, normal.y, normal.z);
         quadBuilder.setQuadOrientation(quadFacing);
         quadBuilder.setTexture(sprite);
         quadBuilder.setQuadTint(layer);
         float width = (float)this.model.getTextureWidth();
         float height = (float)this.model.getTextureHeight();

         for(int i = 0; i < vertices.length; ++i) {
            Point2i uvi = uvs[i];
            Point2f uv = new Point2f(sprite.getInterpolatedU((double)((float)uvi.x / width * 16.0F)), sprite.getInterpolatedV((double)((float)uvi.y / height * 16.0F)));
            this.putVertexData(quadBuilder, format, vertices[i], normal, uv);
         }

         builder.add(quadBuilder.build());
         if (hasTransparency) {
            quadBuilder = new net.minecraftforge.client.model.pipeline.UnpackedBakedQuad.Builder(format);
            quadBuilder.setQuadOrientation(quadFacing.getOpposite());
            quadBuilder.setTexture(sprite);

            for(int i = vertices.length - 1; i >= 0; --i) {
               Point2i uvi = uvs[i];
               Point2f uv = new Point2f(sprite.getInterpolatedU((double)((float)uvi.x / width * 16.0F)), sprite.getInterpolatedV((double)((float)uvi.y / height * 16.0F)));
               this.putVertexData(quadBuilder, format, vertices[i], normal, uv);
            }

            builder.add(quadBuilder.build());
         }

      }
   }

   private boolean isQuadOneDimensional(Point3f[] vertices) {
      for(int i = 0; i < vertices.length; ++i) {
         Point3f vertex = vertices[i];

         for(int n = i + 1; n < vertices.length; ++n) {
            float epsilon = 1.0E-4F;
            if (vertex.epsilonEquals(vertices[n], epsilon)) {
               return true;
            }
         }
      }

      return false;
   }

   private void putVertexData(net.minecraftforge.client.model.pipeline.UnpackedBakedQuad.Builder builder, VertexFormat format, Point3f vert, Vector3f normal, Point2f uv) {
      for(int e = 0; e < format.getElementCount(); ++e) {
         switch(format.getElement(e).getUsage()) {
         case POSITION:
            builder.put(e, new float[]{vert.x, vert.y, vert.z});
            break;
         case COLOR:
            builder.put(e, new float[]{1.0F, 1.0F, 1.0F, 1.0F});
            break;
         case UV:
            builder.put(e, new float[]{uv.x, uv.y, 0.0F, 1.0F});
            break;
         case NORMAL:
            builder.put(e, new float[]{normal.x, normal.y, normal.z});
            break;
         default:
            builder.put(e, new float[0]);
         }
      }

   }

   public IModelState getDefaultState() {
      return TRSRTransformation.identity();
   }
}
