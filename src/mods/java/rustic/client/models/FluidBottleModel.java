package rustic.client.models;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableSet.Builder;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.ItemTextureQuadConverter;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

public class FluidBottleModel implements IModel {
   public static final ModelResourceLocation LOCATION = new ModelResourceLocation(new ResourceLocation("rustic", "fluid_bottle"), "inventory");
   private static final float NORTH_Z_FLUID = 0.468625F;
   private static final float SOUTH_Z_FLUID = 0.531375F;
   public static final IModel MODEL = new FluidBottleModel();
   @Nullable
   private final ResourceLocation liquidLocation = new ResourceLocation("minecraft", "items/potion_overlay");
   @Nullable
   private final ResourceLocation bottleLocation = new ResourceLocation("minecraft", "items/potion_bottle_drinkable");
   @Nullable
   private final Fluid fluid;

   public FluidBottleModel() {
      this.fluid = null;
   }

   public FluidBottleModel(Fluid fluid) {
      this.fluid = fluid;
   }

   public IModel retexture(ImmutableMap<String, String> textures) {
      return new FluidBottleModel(this.fluid);
   }

   public IModel process(ImmutableMap<String, String> customData) {
      String fluidName = (String)customData.get("fluid");
      Fluid fluid = FluidRegistry.getFluid(fluidName);
      if (fluid == null) {
         fluid = this.fluid;
      }

      return new FluidBottleModel(fluid);
   }

   public Collection<ResourceLocation> getDependencies() {
      return ImmutableList.of();
   }

   public Collection<ResourceLocation> getTextures() {
      Builder<ResourceLocation> builder = ImmutableSet.builder();
      if (this.liquidLocation != null) {
         builder.add(this.liquidLocation);
      }

      if (this.bottleLocation != null) {
         builder.add(this.bottleLocation);
      }

      return builder.build();
   }

   public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
      ImmutableMap<TransformType, TRSRTransformation> transformMap = PerspectiveMapWrapper.getTransforms(state);
      TRSRTransformation transform = (TRSRTransformation)state.apply(Optional.empty()).orElse(TRSRTransformation.identity());
      TextureAtlasSprite fluidSprite = null;
      com.google.common.collect.ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
      if (this.fluid != null) {
         fluidSprite = (TextureAtlasSprite)bakedTextureGetter.apply(this.fluid.getStill());
      }

      if (this.bottleLocation != null) {
         IBakedModel model = (new ItemLayerModel(ImmutableList.of(this.bottleLocation))).bake(state, format, bakedTextureGetter);
         builder.addAll(model.getQuads((IBlockState)null, (EnumFacing)null, 0L));
      }

      if (this.liquidLocation != null && fluidSprite != null) {
         TextureAtlasSprite liquid = (TextureAtlasSprite)bakedTextureGetter.apply(this.liquidLocation);
         builder.addAll(ItemTextureQuadConverter.convertTexture(format, transform, liquid, fluidSprite, 0.468625F, EnumFacing.NORTH, this.fluid.getColor()));
         builder.addAll(ItemTextureQuadConverter.convertTexture(format, transform, liquid, fluidSprite, 0.531375F, EnumFacing.SOUTH, this.fluid.getColor()));
      }

      return new FluidBottleModel.BakedFluidBottle(this, builder.build(), fluidSprite, format, Maps.immutableEnumMap(transformMap), Maps.newHashMap());
   }

   public IModelState getDefaultState() {
      return TRSRTransformation.identity();
   }

   private static final class BakedFluidBottle implements IBakedModel {
      private final FluidBottleModel parent;
      private final Map<String, IBakedModel> cache;
      private final ImmutableMap<TransformType, TRSRTransformation> transforms;
      private final ImmutableList<BakedQuad> quads;
      private final TextureAtlasSprite particle;
      private final VertexFormat format;
      private static final TRSRTransformation flipX = new TRSRTransformation((Vector3f)null, (Quat4f)null, new Vector3f(-1.0F, 1.0F, 1.0F), (Quat4f)null);

      public BakedFluidBottle(FluidBottleModel parent, ImmutableList<BakedQuad> quads, TextureAtlasSprite particle, VertexFormat format, ImmutableMap<TransformType, TRSRTransformation> transforms, Map<String, IBakedModel> cache) {
         this.quads = quads;
         this.particle = particle;
         this.format = format;
         this.parent = parent;
         this.transforms = itemTransforms();
         this.cache = cache;
      }

      public ItemOverrideList getOverrides() {
         return FluidBottleModel.BakedFluidBottleOverrideHandler.INSTANCE;
      }

      public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
         return PerspectiveMapWrapper.handlePerspective(this, this.transforms, cameraTransformType);
      }

      private static ImmutableMap<TransformType, TRSRTransformation> itemTransforms() {
         TRSRTransformation thirdperson = get(0.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.55F);
         TRSRTransformation firstperson = get(1.13F, 3.2F, 1.13F, 0.0F, -90.0F, 25.0F, 0.68F);
         com.google.common.collect.ImmutableMap.Builder<TransformType, TRSRTransformation> builder = ImmutableMap.builder();
         builder.put(TransformType.GROUND, get(0.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5F));
         builder.put(TransformType.HEAD, get(0.0F, 13.0F, 7.0F, 0.0F, 180.0F, 0.0F, 1.0F));
         builder.put(TransformType.THIRD_PERSON_RIGHT_HAND, thirdperson);
         builder.put(TransformType.THIRD_PERSON_LEFT_HAND, leftify(thirdperson));
         builder.put(TransformType.FIRST_PERSON_RIGHT_HAND, firstperson);
         builder.put(TransformType.FIRST_PERSON_LEFT_HAND, leftify(firstperson));
         return builder.build();
      }

      private static TRSRTransformation get(float tx, float ty, float tz, float ax, float ay, float az, float s) {
         return TRSRTransformation.blockCenterToCorner(new TRSRTransformation(new Vector3f(tx / 16.0F, ty / 16.0F, tz / 16.0F), TRSRTransformation.quatFromXYZDegrees(new Vector3f(ax, ay, az)), new Vector3f(s, s, s), (Quat4f)null));
      }

      private static TRSRTransformation leftify(TRSRTransformation transform) {
         return TRSRTransformation.blockCenterToCorner(flipX.compose(TRSRTransformation.blockCornerToCenter(transform)).compose(flipX));
      }

      public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
         return side == null ? this.quads : ImmutableList.of();
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

      public TextureAtlasSprite getParticleTexture() {
         return this.particle;
      }

      public ItemCameraTransforms getItemCameraTransforms() {
         return ItemCameraTransforms.DEFAULT;
      }
   }

   private static final class BakedFluidBottleOverrideHandler extends ItemOverrideList {
      public static final FluidBottleModel.BakedFluidBottleOverrideHandler INSTANCE = new FluidBottleModel.BakedFluidBottleOverrideHandler();

      private BakedFluidBottleOverrideHandler() {
         super(ImmutableList.of());
      }

      @Nonnull
      public IBakedModel handleItemState(@Nonnull IBakedModel originalModel, @Nonnull ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
         FluidStack fluidStack = null;
         if (stack.hasTagCompound() && stack.getTagCompound().hasKey("Fluid")) {
            fluidStack = FluidStack.loadFluidStackFromNBT(stack.getTagCompound().getCompoundTag("Fluid"));
         }

         if (fluidStack == null) {
            return originalModel;
         } else {
            FluidBottleModel.BakedFluidBottle model = (FluidBottleModel.BakedFluidBottle)originalModel;
            Fluid fluid = fluidStack.getFluid();
            String name = fluid.getName();
            if (!model.cache.containsKey(name)) {
               IModel parent = model.parent.process(ImmutableMap.of("fluid", name));
               Function<ResourceLocation, TextureAtlasSprite> textureGetter = new Function<ResourceLocation, TextureAtlasSprite>() {
                  public TextureAtlasSprite apply(ResourceLocation location) {
                     return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
                  }
               };
               IBakedModel bakedModel = parent.bake(new SimpleModelState(model.transforms), model.format, textureGetter);
               model.cache.put(name, bakedModel);
               return bakedModel;
            } else {
               return (IBakedModel)model.cache.get(name);
            }
         }
      }
   }

   public static enum LoaderFluidBottle implements ICustomModelLoader {
      INSTANCE;

      private LoaderFluidBottle() {
      }

      public boolean accepts(ResourceLocation modelLocation) {
         return modelLocation.getResourceDomain().equals("rustic") && modelLocation.getResourcePath().contains("fluid_bottle");
      }

      public IModel loadModel(ResourceLocation modelLocation) {
         return FluidBottleModel.MODEL;
      }

      public void onResourceManagerReload(IResourceManager resourceManager) {
      }
   }
}
