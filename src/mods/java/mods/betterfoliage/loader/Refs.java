package mods.betterfoliage.loader;

import kotlin.Metadata;
import mods.octarinecore.metaprog.ClassRef;
import mods.octarinecore.metaprog.FieldRef;
import mods.octarinecore.metaprog.MethodRef;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0015\n\u0002\u0018\u0002\n\u0002\b]\n\u0002\u0010\u000e\n\u0002\b,\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006R\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u000b\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\nR\u0011\u0010\r\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\nR\u0011\u0010\u000f\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\nR\u0011\u0010\u0011\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\nR\u0011\u0010\u0013\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\nR\u0011\u0010\u0015\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\nR\u0011\u0010\u0017\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\nR\u0011\u0010\u0019\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\nR\u0011\u0010\u001b\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\nR\u0011\u0010\u001d\u001a\u00020\u001e¢\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010 R\u0011\u0010!\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\"\u0010\nR\u0011\u0010#\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b$\u0010\nR\u0011\u0010%\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b&\u0010\nR\u0011\u0010'\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b(\u0010\nR\u0011\u0010)\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b*\u0010\nR\u0011\u0010+\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b,\u0010\nR\u0011\u0010-\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b.\u0010\nR\u0011\u0010/\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b0\u0010\nR\u0011\u00101\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b2\u0010\nR\u0011\u00103\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b4\u0010\nR\u0011\u00105\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b6\u0010\nR\u0011\u00107\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b8\u0010\nR\u0011\u00109\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b:\u0010\nR\u0011\u0010;\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b<\u0010\nR\u0011\u0010=\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b>\u0010\nR\u0011\u0010?\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b@\u0010\nR\u0011\u0010A\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\bB\u0010\nR\u0011\u0010C\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\bD\u0010\nR\u0011\u0010E\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\bF\u0010\nR\u0011\u0010G\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\bH\u0010\nR\u0011\u0010I\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\bJ\u0010\nR\u0011\u0010K\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\bL\u0010\u0006R\u0011\u0010M\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\bN\u0010\nR\u0011\u0010O\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\bP\u0010\nR\u0011\u0010Q\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\bR\u0010\nR\u0011\u0010S\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\bT\u0010\nR\u0011\u0010U\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\bV\u0010\nR\u0011\u0010W\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\bX\u0010\nR\u0011\u0010Y\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\bZ\u0010\nR\u0011\u0010[\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\\\u0010\nR\u0011\u0010]\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b^\u0010\nR\u0011\u0010_\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b`\u0010\nR\u0011\u0010a\u001a\u00020\u001e¢\u0006\b\n\u0000\u001a\u0004\bb\u0010 R\u0011\u0010c\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\bd\u0010\u0006R\u0011\u0010e\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\bf\u0010\u0006R\u0011\u0010g\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\bh\u0010\u0006R\u0011\u0010i\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\bj\u0010\u0006R\u0011\u0010k\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\bl\u0010\u0006R\u0011\u0010m\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\bn\u0010\u0006R\u0011\u0010o\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\bp\u0010\u0006R\u0011\u0010q\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\br\u0010\u0006R\u0011\u0010s\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\bt\u0010\u0006R\u0011\u0010u\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\bv\u0010\u0006R\u0011\u0010w\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\bw\u0010\u0006R\u0011\u0010x\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\bx\u0010\u0006R\u0011\u0010y\u001a\u00020\u001e¢\u0006\b\n\u0000\u001a\u0004\bz\u0010 R\u0011\u0010{\u001a\u00020|¢\u0006\b\n\u0000\u001a\u0004\b}\u0010~R\u0012\u0010\u007f\u001a\u00020\u001e¢\u0006\t\n\u0000\u001a\u0005\b\u0080\u0001\u0010 R\u0013\u0010\u0081\u0001\u001a\u00020\u001e¢\u0006\t\n\u0000\u001a\u0005\b\u0082\u0001\u0010 R\u0013\u0010\u0083\u0001\u001a\u00020\u0004¢\u0006\t\n\u0000\u001a\u0005\b\u0084\u0001\u0010\u0006R\u0013\u0010\u0085\u0001\u001a\u00020\u0004¢\u0006\t\n\u0000\u001a\u0005\b\u0086\u0001\u0010\u0006R\u0013\u0010\u0087\u0001\u001a\u00020\u0004¢\u0006\t\n\u0000\u001a\u0005\b\u0088\u0001\u0010\u0006R\u0013\u0010\u0089\u0001\u001a\u00020\u001e¢\u0006\t\n\u0000\u001a\u0005\b\u008a\u0001\u0010 R\u0013\u0010\u008b\u0001\u001a\u00020\u0004¢\u0006\t\n\u0000\u001a\u0005\b\u008c\u0001\u0010\u0006R\u0013\u0010\u008d\u0001\u001a\u00020\u0004¢\u0006\t\n\u0000\u001a\u0005\b\u008e\u0001\u0010\u0006R\u0013\u0010\u008f\u0001\u001a\u00020\u0004¢\u0006\t\n\u0000\u001a\u0005\b\u0090\u0001\u0010\u0006R\u0013\u0010\u0091\u0001\u001a\u00020\u001e¢\u0006\t\n\u0000\u001a\u0005\b\u0092\u0001\u0010 R\u0013\u0010\u0093\u0001\u001a\u00020\u0004¢\u0006\t\n\u0000\u001a\u0005\b\u0094\u0001\u0010\u0006R\u0013\u0010\u0095\u0001\u001a\u00020\u0004¢\u0006\t\n\u0000\u001a\u0005\b\u0096\u0001\u0010\u0006R\u0013\u0010\u0097\u0001\u001a\u00020\u0004¢\u0006\t\n\u0000\u001a\u0005\b\u0098\u0001\u0010\u0006R\u0013\u0010\u0099\u0001\u001a\u00020\u0004¢\u0006\t\n\u0000\u001a\u0005\b\u009a\u0001\u0010\u0006R\u0013\u0010\u009b\u0001\u001a\u00020\u0004¢\u0006\t\n\u0000\u001a\u0005\b\u009c\u0001\u0010\u0006R\u0012\u0010\u009d\u0001\u001a\u00020\u001e¢\u0006\b\n\u0000\u001a\u0004\bP\u0010 R\u0013\u0010\u009e\u0001\u001a\u00020\u0004¢\u0006\t\n\u0000\u001a\u0005\b\u009f\u0001\u0010\u0006R\u0013\u0010 \u0001\u001a\u00020\u0004¢\u0006\t\n\u0000\u001a\u0005\b¡\u0001\u0010\u0006R\u0013\u0010¢\u0001\u001a\u00020\u001e¢\u0006\t\n\u0000\u001a\u0005\b£\u0001\u0010 R\u0013\u0010¤\u0001\u001a\u00020\u0004¢\u0006\t\n\u0000\u001a\u0005\b¥\u0001\u0010\u0006R\u0013\u0010¦\u0001\u001a\u00020\u0004¢\u0006\t\n\u0000\u001a\u0005\b§\u0001\u0010\u0006¨\u0006¨\u0001"},
   d2 = {"Lmods/betterfoliage/loader/Refs;", "", "()V", "AOF_constructor", "Lmods/octarinecore/metaprog/MethodRef;", "getAOF_constructor", "()Lmods/octarinecore/metaprog/MethodRef;", "AmbientOcclusionFace", "Lmods/octarinecore/metaprog/ClassRef;", "getAmbientOcclusionFace", "()Lmods/octarinecore/metaprog/ClassRef;", "BakedQuad", "getBakedQuad", "BetterFoliageHooks", "getBetterFoliageHooks", "Block", "getBlock", "BlockModelRenderer", "getBlockModelRenderer", "BlockPos", "getBlockPos", "BlockRenderLayer", "getBlockRenderLayer", "BlockRendererDispatcher", "getBlockRendererDispatcher", "BlockStateBase", "getBlockStateBase", "BufferBuilder", "getBufferBuilder", "CCOFChunkCache", "Lmods/octarinecore/metaprog/FieldRef;", "getCCOFChunkCache", "()Lmods/octarinecore/metaprog/FieldRef;", "ChunkCache", "getChunkCache", "ChunkCompileTaskGenerator", "getChunkCompileTaskGenerator", "CustomColors", "getCustomColors", "EnumFacing", "getEnumFacing", "IBlockAccess", "getIBlockAccess", "IBlockState", "getIBlockState", "IModel", "getIModel", "IRegistry", "getIRegistry", "List", "getList", "Map", "getMap", "ModelBlock", "getModelBlock", "ModelLoader", "getModelLoader", "ModelResourceLocation", "getModelResourceLocation", "MultiModel", "getMultiModel", "MultipartModel", "getMultipartModel", "MutableBlockPos", "getMutableBlockPos", "OptifineChunkCache", "getOptifineChunkCache", "OptifineClassTransformer", "getOptifineClassTransformer", "Random", "getRandom", "RenderChunk", "getRenderChunk", "RenderEnv", "getRenderEnv", "RenderEnv_reset", "getRenderEnv_reset", "ResourceLocation", "getResourceLocation", "SVertexBuilder", "getSVertexBuilder", "ShadersModIntegration", "getShadersModIntegration", "StateImplementation", "getStateImplementation", "String", "getString", "TextureAtlasSprite", "getTextureAtlasSprite", "VanillaModelWrapper", "getVanillaModelWrapper", "WeightedRandomModel", "getWeightedRandomModel", "World", "getWorld", "WorldClient", "getWorldClient", "base_MM", "getBase_MM", "canRenderBlockInLayer", "getCanRenderBlockInLayer", "canRenderInLayer", "getCanRenderInLayer", "doesSideBlockRendering", "getDoesSideBlockRendering", "doesSideBlockRenderingOverride", "getDoesSideBlockRenderingOverride", "getAmbientOcclusionLightValue", "getGetAmbientOcclusionLightValue", "getAmbientOcclusionLightValueOverride", "getGetAmbientOcclusionLightValueOverride", "getBlockId", "getGetBlockId", "getBlockIdOverride", "getGetBlockIdOverride", "getColorMultiplier", "getGetColorMultiplier", "getMetadata", "getGetMetadata", "isOpaqueCube", "isOpaqueCubeOverride", "location_VMW", "getLocation_VMW", "mcVersion", "", "getMcVersion", "()Ljava/lang/String;", "model_VMW", "getModel_VMW", "models_WRM", "getModels_WRM", "onAfterBakeModels", "getOnAfterBakeModels", "onAfterLoadModelDefinitions", "getOnAfterLoadModelDefinitions", "onRandomDisplayTick", "getOnRandomDisplayTick", "partModels_MPM", "getPartModels_MPM", "popEntity", "getPopEntity", "pushEntity_num", "getPushEntity_num", "pushEntity_state", "getPushEntity_state", "quadSprite", "getQuadSprite", "randomDisplayTick", "getRandomDisplayTick", "rebuildChunk", "getRebuildChunk", "renderBlock", "getRenderBlock", "renderWorldBlock", "getRenderWorldBlock", "resetChangedState", "getResetChangedState", "sVertexBuilder", "setupModelRegistry", "getSetupModelRegistry", "showBarrierParticles", "getShowBarrierParticles", "stateModels", "getStateModels", "useNeighborBrightness", "getUseNeighborBrightness", "useNeighborBrightnessOverride", "getUseNeighborBrightnessOverride", "BetterFoliage-MC1.12"}
)
public final class Refs {
   @NotNull
   private static final String mcVersion = FMLInjectionData.data()[4].toString();
   @NotNull
   private static final ClassRef String = new ClassRef("java.lang.String");
   @NotNull
   private static final ClassRef Map = new ClassRef("java.util.Map");
   @NotNull
   private static final ClassRef List = new ClassRef("java.util.List");
   @NotNull
   private static final ClassRef Random = new ClassRef("java.util.Random");
   @NotNull
   private static final ClassRef IBlockAccess = new ClassRef("net.minecraft.world.IBlockAccess");
   @NotNull
   private static final ClassRef IBlockState = new ClassRef("net.minecraft.block.state.IBlockState");
   @NotNull
   private static final ClassRef BlockStateBase = new ClassRef("net.minecraft.block.state.BlockStateBase");
   @NotNull
   private static final ClassRef BlockPos = new ClassRef("net.minecraft.util.math.BlockPos");
   @NotNull
   private static final ClassRef MutableBlockPos = new ClassRef("net.minecraft.util.math.BlockPos$MutableBlockPos");
   @NotNull
   private static final ClassRef BlockRenderLayer = new ClassRef("net.minecraft.util.BlockRenderLayer");
   @NotNull
   private static final ClassRef EnumFacing = new ClassRef("net.minecraft.util.EnumFacing");
   @NotNull
   private static final ClassRef World = new ClassRef("net.minecraft.world.World");
   @NotNull
   private static final ClassRef WorldClient = new ClassRef("net.minecraft.client.multiplayer.WorldClient");
   @NotNull
   private static final ClassRef ChunkCache = new ClassRef("net.minecraft.world.ChunkCache");
   @NotNull
   private static final MethodRef showBarrierParticles = new MethodRef(WorldClient, "showBarrierParticles", "showBarrierParticles", ClassRef.Companion.getVoid(), new ClassRef[]{ClassRef.Companion.getInt(), ClassRef.Companion.getInt(), ClassRef.Companion.getInt(), ClassRef.Companion.getInt(), Random, ClassRef.Companion.getBoolean(), MutableBlockPos});
   @NotNull
   private static final ClassRef Block = new ClassRef("net.minecraft.block.Block");
   @NotNull
   private static final ClassRef StateImplementation = new ClassRef("net.minecraft.block.state.BlockStateContainer$StateImplementation");
   @NotNull
   private static final MethodRef canRenderInLayer = new MethodRef(Block, "canRenderInLayer", ClassRef.Companion.getBoolean(), new ClassRef[]{IBlockState, BlockRenderLayer});
   @NotNull
   private static final MethodRef getAmbientOcclusionLightValue = new MethodRef(StateImplementation, "getAmbientOcclusionLightValue", "getAmbientOcclusionLightValue", ClassRef.Companion.getFloat(), new ClassRef[0]);
   @NotNull
   private static final MethodRef useNeighborBrightness = new MethodRef(StateImplementation, "useNeighborBrightness", "useNeighborBrightness", ClassRef.Companion.getBoolean(), new ClassRef[0]);
   @NotNull
   private static final MethodRef doesSideBlockRendering = new MethodRef(StateImplementation, "doesSideBlockRendering", ClassRef.Companion.getBoolean(), new ClassRef[]{IBlockAccess, BlockPos, EnumFacing});
   @NotNull
   private static final MethodRef isOpaqueCube = new MethodRef(StateImplementation, "isOpaqueCube", "isOpaqueCube", ClassRef.Companion.getBoolean(), new ClassRef[0]);
   @NotNull
   private static final MethodRef randomDisplayTick = new MethodRef(Block, "randomDisplayTick", "randomDisplayTick", ClassRef.Companion.getVoid(), new ClassRef[]{IBlockState, World, BlockPos, Random});
   @NotNull
   private static final ClassRef BlockModelRenderer = new ClassRef("net.minecraft.client.renderer.BlockModelRenderer");
   @NotNull
   private static final ClassRef AmbientOcclusionFace = new ClassRef("net.minecraft.client.renderer.BlockModelRenderer$AmbientOcclusionFace");
   @NotNull
   private static final ClassRef ChunkCompileTaskGenerator = new ClassRef("net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator");
   @NotNull
   private static final ClassRef BufferBuilder = new ClassRef("net.minecraft.client.renderer.BufferBuilder");
   @NotNull
   private static final MethodRef AOF_constructor = new MethodRef(AmbientOcclusionFace, "<init>", ClassRef.Companion.getVoid(), new ClassRef[]{BlockModelRenderer});
   @NotNull
   private static final ClassRef RenderChunk = new ClassRef("net.minecraft.client.renderer.chunk.RenderChunk");
   @NotNull
   private static final MethodRef rebuildChunk = new MethodRef(RenderChunk, "rebuildChunk", "rebuildChunk", ClassRef.Companion.getVoid(), new ClassRef[]{ClassRef.Companion.getFloat(), ClassRef.Companion.getFloat(), ClassRef.Companion.getFloat(), ChunkCompileTaskGenerator});
   @NotNull
   private static final ClassRef BlockRendererDispatcher = new ClassRef("net.minecraft.client.renderer.BlockRendererDispatcher");
   @NotNull
   private static final MethodRef renderBlock = new MethodRef(BlockRendererDispatcher, "renderBlock", "renderBlock", ClassRef.Companion.getBoolean(), new ClassRef[]{IBlockState, BlockPos, IBlockAccess, BufferBuilder});
   @NotNull
   private static final ClassRef TextureAtlasSprite = new ClassRef("net.minecraft.client.renderer.texture.TextureAtlasSprite");
   @NotNull
   private static final ClassRef IRegistry = new ClassRef("net.minecraft.util.registry.IRegistry");
   @NotNull
   private static final ClassRef ModelLoader = new ClassRef("net.minecraftforge.client.model.ModelLoader");
   @NotNull
   private static final FieldRef stateModels = new FieldRef(ModelLoader, "stateModels", Map);
   @NotNull
   private static final MethodRef setupModelRegistry = new MethodRef(ModelLoader, "setupModelRegistry", "setupModelRegistry", IRegistry, new ClassRef[0]);
   @NotNull
   private static final ClassRef IModel = new ClassRef("net.minecraftforge.client.model.IModel");
   @NotNull
   private static final ClassRef ModelBlock = new ClassRef("net.minecraft.client.renderer.block.model.ModelBlock");
   @NotNull
   private static final ClassRef ResourceLocation = new ClassRef("net.minecraft.util.ResourceLocation");
   @NotNull
   private static final ClassRef ModelResourceLocation = new ClassRef("net.minecraft.client.renderer.block.model.ModelResourceLocation");
   @NotNull
   private static final ClassRef VanillaModelWrapper = new ClassRef("net.minecraftforge.client.model.ModelLoader$VanillaModelWrapper");
   @NotNull
   private static final FieldRef model_VMW = new FieldRef(VanillaModelWrapper, "model", ModelBlock);
   @NotNull
   private static final FieldRef location_VMW = new FieldRef(VanillaModelWrapper, "location", ModelBlock);
   @NotNull
   private static final ClassRef WeightedRandomModel = new ClassRef("net.minecraftforge.client.model.ModelLoader$WeightedRandomModel");
   @NotNull
   private static final FieldRef models_WRM = new FieldRef(WeightedRandomModel, "models", List);
   @NotNull
   private static final ClassRef MultiModel = new ClassRef("net.minecraftforge.client.model.MultiModel");
   @NotNull
   private static final FieldRef base_MM = new FieldRef(MultiModel, "base", IModel);
   @NotNull
   private static final ClassRef MultipartModel = new ClassRef("net.minecraftforge.client.model.ModelLoader$MultipartModel");
   @NotNull
   private static final FieldRef partModels_MPM = new FieldRef(MultipartModel, "partModels", List);
   @NotNull
   private static final ClassRef BakedQuad = new ClassRef("net.minecraft.client.renderer.block.model.BakedQuad");
   @NotNull
   private static final MethodRef resetChangedState = new MethodRef(new ClassRef("net.minecraftforge.common.config.Configuration"), "resetChangedState", ClassRef.Companion.getVoid(), new ClassRef[0]);
   @NotNull
   private static final ClassRef BetterFoliageHooks = new ClassRef("mods.betterfoliage.client.Hooks");
   @NotNull
   private static final MethodRef getAmbientOcclusionLightValueOverride = new MethodRef(BetterFoliageHooks, "getAmbientOcclusionLightValueOverride", ClassRef.Companion.getFloat(), new ClassRef[]{ClassRef.Companion.getFloat(), IBlockState});
   @NotNull
   private static final MethodRef useNeighborBrightnessOverride = new MethodRef(BetterFoliageHooks, "getUseNeighborBrightnessOverride", ClassRef.Companion.getBoolean(), new ClassRef[]{ClassRef.Companion.getBoolean(), IBlockState});
   @NotNull
   private static final MethodRef doesSideBlockRenderingOverride = new MethodRef(BetterFoliageHooks, "doesSideBlockRenderingOverride", ClassRef.Companion.getBoolean(), new ClassRef[]{ClassRef.Companion.getBoolean(), IBlockAccess, BlockPos, EnumFacing});
   @NotNull
   private static final MethodRef isOpaqueCubeOverride = new MethodRef(BetterFoliageHooks, "isOpaqueCubeOverride", ClassRef.Companion.getBoolean(), new ClassRef[]{ClassRef.Companion.getBoolean(), IBlockState});
   @NotNull
   private static final MethodRef onRandomDisplayTick = new MethodRef(BetterFoliageHooks, "onRandomDisplayTick", ClassRef.Companion.getVoid(), new ClassRef[]{World, IBlockState, BlockPos});
   @NotNull
   private static final MethodRef onAfterLoadModelDefinitions = new MethodRef(BetterFoliageHooks, "onAfterLoadModelDefinitions", ClassRef.Companion.getVoid(), new ClassRef[]{ModelLoader});
   @NotNull
   private static final MethodRef onAfterBakeModels = new MethodRef(BetterFoliageHooks, "onAfterBakeModels", ClassRef.Companion.getVoid(), new ClassRef[]{Map});
   @NotNull
   private static final MethodRef renderWorldBlock = new MethodRef(BetterFoliageHooks, "renderWorldBlock", ClassRef.Companion.getBoolean(), new ClassRef[]{BlockRendererDispatcher, IBlockState, BlockPos, IBlockAccess, BufferBuilder, BlockRenderLayer});
   @NotNull
   private static final MethodRef canRenderBlockInLayer = new MethodRef(BetterFoliageHooks, "canRenderBlockInLayer", ClassRef.Companion.getBoolean(), new ClassRef[]{Block, IBlockState, BlockRenderLayer});
   @NotNull
   private static final ClassRef OptifineClassTransformer = new ClassRef("optifine.OptiFineClassTransformer");
   @NotNull
   private static final ClassRef OptifineChunkCache = new ClassRef("net.optifine.override.ChunkCacheOF");
   @NotNull
   private static final FieldRef CCOFChunkCache = new FieldRef(OptifineChunkCache, "chunkCache", ChunkCache);
   @NotNull
   private static final MethodRef getBlockId = new MethodRef(BlockStateBase, "getBlockId", ClassRef.Companion.getInt(), new ClassRef[0]);
   @NotNull
   private static final MethodRef getMetadata = new MethodRef(BlockStateBase, "getMetadata", ClassRef.Companion.getInt(), new ClassRef[0]);
   @NotNull
   private static final ClassRef RenderEnv = new ClassRef("net.optifine.render.RenderEnv");
   @NotNull
   private static final MethodRef RenderEnv_reset = new MethodRef(RenderEnv, "reset", ClassRef.Companion.getVoid(), new ClassRef[]{IBlockState, BlockPos});
   @NotNull
   private static final FieldRef quadSprite = new FieldRef(BufferBuilder, "quadSprite", TextureAtlasSprite);
   @NotNull
   private static final ClassRef CustomColors = new ClassRef("net.optifine.CustomColors");
   @NotNull
   private static final MethodRef getColorMultiplier = new MethodRef(CustomColors, "getColorMultiplier", ClassRef.Companion.getInt(), new ClassRef[]{BakedQuad, IBlockState, IBlockAccess, BlockPos, RenderEnv});
   @NotNull
   private static final ClassRef SVertexBuilder = new ClassRef("net.optifine.shaders.SVertexBuilder");
   @NotNull
   private static final FieldRef sVertexBuilder = new FieldRef(BufferBuilder, "sVertexBuilder", SVertexBuilder);
   @NotNull
   private static final MethodRef pushEntity_state = new MethodRef(SVertexBuilder, "pushEntity", ClassRef.Companion.getVoid(), new ClassRef[]{IBlockState, BlockPos, IBlockAccess, BufferBuilder});
   @NotNull
   private static final MethodRef pushEntity_num = new MethodRef(SVertexBuilder, "pushEntity", ClassRef.Companion.getVoid(), new ClassRef[]{ClassRef.Companion.getLong()});
   @NotNull
   private static final MethodRef popEntity = new MethodRef(SVertexBuilder, "popEntity", ClassRef.Companion.getVoid(), new ClassRef[0]);
   @NotNull
   private static final ClassRef ShadersModIntegration = new ClassRef("mods.betterfoliage.client.integration.ShadersModIntegration");
   @NotNull
   private static final MethodRef getBlockIdOverride = new MethodRef(ShadersModIntegration, "getBlockIdOverride", ClassRef.Companion.getLong(), new ClassRef[]{ClassRef.Companion.getLong(), IBlockState});
   public static final Refs INSTANCE;

   @NotNull
   public final String getMcVersion() {
      return mcVersion;
   }

   @NotNull
   public final ClassRef getString() {
      return String;
   }

   @NotNull
   public final ClassRef getMap() {
      return Map;
   }

   @NotNull
   public final ClassRef getList() {
      return List;
   }

   @NotNull
   public final ClassRef getRandom() {
      return Random;
   }

   @NotNull
   public final ClassRef getIBlockAccess() {
      return IBlockAccess;
   }

   @NotNull
   public final ClassRef getIBlockState() {
      return IBlockState;
   }

   @NotNull
   public final ClassRef getBlockStateBase() {
      return BlockStateBase;
   }

   @NotNull
   public final ClassRef getBlockPos() {
      return BlockPos;
   }

   @NotNull
   public final ClassRef getMutableBlockPos() {
      return MutableBlockPos;
   }

   @NotNull
   public final ClassRef getBlockRenderLayer() {
      return BlockRenderLayer;
   }

   @NotNull
   public final ClassRef getEnumFacing() {
      return EnumFacing;
   }

   @NotNull
   public final ClassRef getWorld() {
      return World;
   }

   @NotNull
   public final ClassRef getWorldClient() {
      return WorldClient;
   }

   @NotNull
   public final ClassRef getChunkCache() {
      return ChunkCache;
   }

   @NotNull
   public final MethodRef getShowBarrierParticles() {
      return showBarrierParticles;
   }

   @NotNull
   public final ClassRef getBlock() {
      return Block;
   }

   @NotNull
   public final ClassRef getStateImplementation() {
      return StateImplementation;
   }

   @NotNull
   public final MethodRef getCanRenderInLayer() {
      return canRenderInLayer;
   }

   @NotNull
   public final MethodRef getGetAmbientOcclusionLightValue() {
      return getAmbientOcclusionLightValue;
   }

   @NotNull
   public final MethodRef getUseNeighborBrightness() {
      return useNeighborBrightness;
   }

   @NotNull
   public final MethodRef getDoesSideBlockRendering() {
      return doesSideBlockRendering;
   }

   @NotNull
   public final MethodRef isOpaqueCube() {
      return isOpaqueCube;
   }

   @NotNull
   public final MethodRef getRandomDisplayTick() {
      return randomDisplayTick;
   }

   @NotNull
   public final ClassRef getBlockModelRenderer() {
      return BlockModelRenderer;
   }

   @NotNull
   public final ClassRef getAmbientOcclusionFace() {
      return AmbientOcclusionFace;
   }

   @NotNull
   public final ClassRef getChunkCompileTaskGenerator() {
      return ChunkCompileTaskGenerator;
   }

   @NotNull
   public final ClassRef getBufferBuilder() {
      return BufferBuilder;
   }

   @NotNull
   public final MethodRef getAOF_constructor() {
      return AOF_constructor;
   }

   @NotNull
   public final ClassRef getRenderChunk() {
      return RenderChunk;
   }

   @NotNull
   public final MethodRef getRebuildChunk() {
      return rebuildChunk;
   }

   @NotNull
   public final ClassRef getBlockRendererDispatcher() {
      return BlockRendererDispatcher;
   }

   @NotNull
   public final MethodRef getRenderBlock() {
      return renderBlock;
   }

   @NotNull
   public final ClassRef getTextureAtlasSprite() {
      return TextureAtlasSprite;
   }

   @NotNull
   public final ClassRef getIRegistry() {
      return IRegistry;
   }

   @NotNull
   public final ClassRef getModelLoader() {
      return ModelLoader;
   }

   @NotNull
   public final FieldRef getStateModels() {
      return stateModels;
   }

   @NotNull
   public final MethodRef getSetupModelRegistry() {
      return setupModelRegistry;
   }

   @NotNull
   public final ClassRef getIModel() {
      return IModel;
   }

   @NotNull
   public final ClassRef getModelBlock() {
      return ModelBlock;
   }

   @NotNull
   public final ClassRef getResourceLocation() {
      return ResourceLocation;
   }

   @NotNull
   public final ClassRef getModelResourceLocation() {
      return ModelResourceLocation;
   }

   @NotNull
   public final ClassRef getVanillaModelWrapper() {
      return VanillaModelWrapper;
   }

   @NotNull
   public final FieldRef getModel_VMW() {
      return model_VMW;
   }

   @NotNull
   public final FieldRef getLocation_VMW() {
      return location_VMW;
   }

   @NotNull
   public final ClassRef getWeightedRandomModel() {
      return WeightedRandomModel;
   }

   @NotNull
   public final FieldRef getModels_WRM() {
      return models_WRM;
   }

   @NotNull
   public final ClassRef getMultiModel() {
      return MultiModel;
   }

   @NotNull
   public final FieldRef getBase_MM() {
      return base_MM;
   }

   @NotNull
   public final ClassRef getMultipartModel() {
      return MultipartModel;
   }

   @NotNull
   public final FieldRef getPartModels_MPM() {
      return partModels_MPM;
   }

   @NotNull
   public final ClassRef getBakedQuad() {
      return BakedQuad;
   }

   @NotNull
   public final MethodRef getResetChangedState() {
      return resetChangedState;
   }

   @NotNull
   public final ClassRef getBetterFoliageHooks() {
      return BetterFoliageHooks;
   }

   @NotNull
   public final MethodRef getGetAmbientOcclusionLightValueOverride() {
      return getAmbientOcclusionLightValueOverride;
   }

   @NotNull
   public final MethodRef getUseNeighborBrightnessOverride() {
      return useNeighborBrightnessOverride;
   }

   @NotNull
   public final MethodRef getDoesSideBlockRenderingOverride() {
      return doesSideBlockRenderingOverride;
   }

   @NotNull
   public final MethodRef isOpaqueCubeOverride() {
      return isOpaqueCubeOverride;
   }

   @NotNull
   public final MethodRef getOnRandomDisplayTick() {
      return onRandomDisplayTick;
   }

   @NotNull
   public final MethodRef getOnAfterLoadModelDefinitions() {
      return onAfterLoadModelDefinitions;
   }

   @NotNull
   public final MethodRef getOnAfterBakeModels() {
      return onAfterBakeModels;
   }

   @NotNull
   public final MethodRef getRenderWorldBlock() {
      return renderWorldBlock;
   }

   @NotNull
   public final MethodRef getCanRenderBlockInLayer() {
      return canRenderBlockInLayer;
   }

   @NotNull
   public final ClassRef getOptifineClassTransformer() {
      return OptifineClassTransformer;
   }

   @NotNull
   public final ClassRef getOptifineChunkCache() {
      return OptifineChunkCache;
   }

   @NotNull
   public final FieldRef getCCOFChunkCache() {
      return CCOFChunkCache;
   }

   @NotNull
   public final MethodRef getGetBlockId() {
      return getBlockId;
   }

   @NotNull
   public final MethodRef getGetMetadata() {
      return getMetadata;
   }

   @NotNull
   public final ClassRef getRenderEnv() {
      return RenderEnv;
   }

   @NotNull
   public final MethodRef getRenderEnv_reset() {
      return RenderEnv_reset;
   }

   @NotNull
   public final FieldRef getQuadSprite() {
      return quadSprite;
   }

   @NotNull
   public final ClassRef getCustomColors() {
      return CustomColors;
   }

   @NotNull
   public final MethodRef getGetColorMultiplier() {
      return getColorMultiplier;
   }

   @NotNull
   public final ClassRef getSVertexBuilder() {
      return SVertexBuilder;
   }

   @NotNull
   public final FieldRef getSVertexBuilder() {
      return sVertexBuilder;
   }

   @NotNull
   public final MethodRef getPushEntity_state() {
      return pushEntity_state;
   }

   @NotNull
   public final MethodRef getPushEntity_num() {
      return pushEntity_num;
   }

   @NotNull
   public final MethodRef getPopEntity() {
      return popEntity;
   }

   @NotNull
   public final ClassRef getShadersModIntegration() {
      return ShadersModIntegration;
   }

   @NotNull
   public final MethodRef getGetBlockIdOverride() {
      return getBlockIdOverride;
   }

   private Refs() {
   }

   static {
      Refs var0 = new Refs();
      INSTANCE = var0;
   }
}
