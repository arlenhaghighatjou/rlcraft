package pl.asie.foamfix.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.ImmutableList.Builder;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.lax1dude.eaglercraft.v1_8.opengl.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.commons.lang3.tuple.Pair;
import pl.asie.foamfix.util.MethodHandleHelper;

public class FoamyItemLayerModel implements IModel {
   private static final ResourceLocation MISSINGNO = new ResourceLocation("missingno");
   private static final MethodHandle OVERRIDES_GET;
   private static final MethodHandle BUILD_QUAD;
   private final ItemLayerModel parent;

   public FoamyItemLayerModel(ItemLayerModel parent) {
      this.parent = parent;
   }

   public IModel retexture(ImmutableMap<String, String> textures) {
      return new FoamyItemLayerModel(this.parent.retexture(textures));
   }

   public Collection<ResourceLocation> getDependencies() {
      return this.parent.getDependencies();
   }

   public Collection<ResourceLocation> getTextures() {
      return this.parent.getTextures();
   }

   public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, EaglerTextureAtlasSprite> bakedTextureGetter) {
      return bakeStatic(this.parent, state, format, bakedTextureGetter);
   }

   public static IBakedModel bakeStatic(ItemLayerModel parent, IModelState state, VertexFormat format, Function<ResourceLocation, EaglerTextureAtlasSprite> bakedTextureGetter) {
      ImmutableMap<TransformType, TRSRTransformation> map = PerspectiveMapWrapper.getTransforms(state);
      TRSRTransformation guiTransform = (TRSRTransformation)map.get(TransformType.GUI);
      Builder<BakedQuad> builder = ImmutableList.builder();
      Optional<TRSRTransformation> transform = state.apply(Optional.empty());
      List<ResourceLocation> textures = (List)parent.getTextures();
      Builder<EaglerTextureAtlasSprite> textureAtlas = new Builder();
      EaglerTextureAtlasSprite particle = (EaglerTextureAtlasSprite)bakedTextureGetter.apply(textures.isEmpty() ? MISSINGNO : (ResourceLocation)textures.get(0));

      ItemOverrideList list;
      try {
         list = OVERRIDES_GET.invokeExact(parent);
      } catch (Throwable var17) {
         throw new RuntimeException(var17);
      }

      if (guiTransform != null && !guiTransform.isIdentity()) {
         for(int i = 0; i < textures.size(); ++i) {
            EaglerTextureAtlasSprite sprite = (EaglerTextureAtlasSprite)bakedTextureGetter.apply((ResourceLocation)textures.get(i));
            textureAtlas.add(sprite);
         }

         return new FoamyItemLayerModel.Static3DItemModel(map, format, particle, list, textureAtlas.build(), transform);
      } else {
         if (BUILD_QUAD != null) {
            for(int i = 0; i < textures.size(); ++i) {
               EaglerTextureAtlasSprite sprite = (EaglerTextureAtlasSprite)bakedTextureGetter.apply((ResourceLocation)textures.get(i));
               textureAtlas.add(sprite);

               try {
                  builder.add(BUILD_QUAD.invokeExact(format, transform, EnumFacing.SOUTH, sprite, i, 0.0F, 0.0F, 0.53125F, sprite.getMinU(), sprite.getMaxV(), 1.0F, 0.0F, 0.53125F, sprite.getMaxU(), sprite.getMaxV(), 1.0F, 1.0F, 0.53125F, sprite.getMaxU(), sprite.getMinV(), 0.0F, 1.0F, 0.53125F, sprite.getMinU(), sprite.getMinV()));
               } catch (Throwable var16) {
                  throw new RuntimeException(var16);
               }
            }
         } else {
            for(int i = 0; i < textures.size(); ++i) {
               EaglerTextureAtlasSprite sprite = (EaglerTextureAtlasSprite)bakedTextureGetter.apply((ResourceLocation)textures.get(i));
               textureAtlas.add(sprite);
               UnmodifiableIterator t = ItemLayerModel.getQuadsForSprite(i, sprite, format, transform).iterator();

               while(t.hasNext()) {
                  BakedQuad quad = (BakedQuad)t.next();
                  if (quad.getFace() == EnumFacing.SOUTH) {
                     builder.add(quad);
                  }
               }
            }
         }

         return (new FoamyItemLayerModel.DynamicItemModel(builder.build(), particle, map, list, textureAtlas.build(), format, transform)).otherModel;
      }
   }

   public IModelState getDefaultState() {
      return TRSRTransformation.identity();
   }

   static {
      MethodHandle handle = null;

      try {
         handle = MethodHandles.lookup().unreflect(ReflectionHelper.findMethod(ItemLayerModel.class, "buildQuad", "buildQuad", new Class[]{VertexFormat.class, Optional.class, EnumFacing.class, EaglerTextureAtlasSprite.class, Integer.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE}));
      } catch (Exception var3) {
         var3.printStackTrace();
      }

      BUILD_QUAD = handle;
      handle = null;

      try {
         handle = MethodHandleHelper.findFieldGetter(ItemLayerModel.class, "overrides");
      } catch (Exception var2) {
         throw new RuntimeException(var2);
      }

      OVERRIDES_GET = handle;
   }

   public static class Dynamic3DItemModel implements IBakedModel {
      private final FoamyItemLayerModel.DynamicItemModel parent;
      private SoftReference<List<BakedQuad>> quadsSoft = null;

      public Dynamic3DItemModel(FoamyItemLayerModel.DynamicItemModel parent) {
         this.parent = parent;
      }

      public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType type) {
         Pair<? extends IBakedModel, Matrix4f> pair = PerspectiveMapWrapper.handlePerspective(this, this.parent.transforms, type);
         return type == TransformType.GUI && pair.getRight() == null ? Pair.of(this.parent, (Object)null) : pair;
      }

      public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
         if (this.quadsSoft == null || this.quadsSoft.get() == null) {
            Builder<BakedQuad> builder = new Builder();

            for(int i = 0; i < this.parent.textures.size(); ++i) {
               EaglerTextureAtlasSprite sprite = (EaglerTextureAtlasSprite)this.parent.textures.get(i);
               builder.addAll(ItemLayerModel.getQuadsForSprite(i, sprite, this.parent.format, this.parent.transform));
            }

            this.quadsSoft = new SoftReference(builder.build());
         }

         return side == null ? (List)this.quadsSoft.get() : Collections.EMPTY_LIST;
      }

      public boolean isAmbientOcclusion() {
         return true;
      }

      public boolean isGui3d() {
         return false;
      }

      public boolean isBuiltInRenderer() {
         return false;
      }

      public EaglerTextureAtlasSprite getParticleTexture() {
         return this.parent.particle;
      }

      public ItemOverrideList getOverrides() {
         return this.parent.overrides;
      }
   }

   public static class DynamicItemModel implements IBakedModel {
      private final List<EaglerTextureAtlasSprite> textures;
      private final EaglerTextureAtlasSprite particle;
      private final ImmutableList<BakedQuad> fastQuads;
      private final ImmutableMap<TransformType, TRSRTransformation> transforms;
      private final VertexFormat format;
      private final Optional<TRSRTransformation> transform;
      private final IBakedModel otherModel;
      private final ItemOverrideList overrides;

      public DynamicItemModel(ImmutableList<BakedQuad> quads, EaglerTextureAtlasSprite particle, ImmutableMap<TransformType, TRSRTransformation> transforms, ItemOverrideList overrides, List<EaglerTextureAtlasSprite> textures, VertexFormat format, Optional<TRSRTransformation> transform) {
         this.fastQuads = quads;
         this.particle = particle;
         this.transforms = transforms;
         this.overrides = overrides;
         this.textures = textures;
         this.format = format;
         this.transform = transform;
         this.otherModel = new FoamyItemLayerModel.Dynamic3DItemModel(this);
      }

      public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
         return (List<BakedQuad>)(side == null ? this.fastQuads : Collections.EMPTY_LIST);
      }

      public boolean isAmbientOcclusion() {
         return true;
      }

      public boolean isGui3d() {
         return false;
      }

      public boolean isBuiltInRenderer() {
         return false;
      }

      public EaglerTextureAtlasSprite getParticleTexture() {
         return this.particle;
      }

      public ItemOverrideList getOverrides() {
         return this.overrides;
      }

      public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType type) {
         Pair<? extends IBakedModel, Matrix4f> pair = PerspectiveMapWrapper.handlePerspective(this, this.transforms, type);
         return type != TransformType.GUI ? Pair.of(this.otherModel, (Matrix4f)pair.getRight()) : pair;
      }
   }

   public static class Static3DItemModel implements IBakedModel {
      private final ImmutableMap<TransformType, TRSRTransformation> transforms;
      private final VertexFormat format;
      private final EaglerTextureAtlasSprite particle;
      private final ItemOverrideList overrides;
      private final List<EaglerTextureAtlasSprite> textures;
      private final Optional<TRSRTransformation> transform;
      private SoftReference<List<BakedQuad>> quadsSoft = null;

      public Static3DItemModel(ImmutableMap<TransformType, TRSRTransformation> transforms, VertexFormat format, EaglerTextureAtlasSprite particle, ItemOverrideList overrides, List<EaglerTextureAtlasSprite> textures, Optional<TRSRTransformation> transform) {
         this.transforms = transforms;
         this.format = format;
         this.particle = particle;
         this.overrides = overrides;
         this.textures = textures;
         this.transform = transform;
      }

      public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType type) {
         Pair<? extends IBakedModel, Matrix4f> pair = PerspectiveMapWrapper.handlePerspective(this, this.transforms, type);
         return pair;
      }

      public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
         if (this.quadsSoft == null || this.quadsSoft.get() == null) {
            Builder<BakedQuad> builder = new Builder();

            for(int i = 0; i < this.textures.size(); ++i) {
               EaglerTextureAtlasSprite sprite = (EaglerTextureAtlasSprite)this.textures.get(i);
               builder.addAll(ItemLayerModel.getQuadsForSprite(i, sprite, this.format, this.transform));
            }

            this.quadsSoft = new SoftReference(builder.build());
         }

         return side == null ? (List)this.quadsSoft.get() : Collections.EMPTY_LIST;
      }

      public boolean isAmbientOcclusion() {
         return true;
      }

      public boolean isGui3d() {
         return false;
      }

      public boolean isBuiltInRenderer() {
         return false;
      }

      public EaglerTextureAtlasSprite getParticleTexture() {
         return this.particle;
      }

      public ItemOverrideList getOverrides() {
         return this.overrides;
      }
   }
}
