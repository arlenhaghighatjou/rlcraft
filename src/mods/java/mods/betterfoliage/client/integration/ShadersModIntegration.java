package mods.betterfoliage.client.integration;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import mods.betterfoliage.client.Client;
import mods.betterfoliage.client.config.Config;
import mods.betterfoliage.loader.Refs;
import mods.octarinecore.common.config.ConfigurableBlockMatcher;
import mods.octarinecore.metaprog.Reflection;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.BlockTallGrass.EnumType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;
import org.jetbrains.annotations.NotNull;

@SideOnly(Side.CLIENT)
@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\r\u001a\u00020\u00042\u0006\u0010\u000e\u001a\u00020\u000fJ\u0018\u0010\u0010\u001a\u00020\u00042\u0006\u0010\u0011\u001a\u00020\u00042\u0006\u0010\u000e\u001a\u00020\u000fH\u0007J)\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\b\b\u0002\u0010\u0016\u001a\u00020\b2\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00130\u0018H\u0086\bJ)\u0010\u0019\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\b\b\u0002\u0010\u0016\u001a\u00020\b2\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00130\u0018H\u0086\bJ\u0016\u0010\u001a\u001a\u00020\u00132\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u000e\u001a\u00020\u000fJ9\u0010\u001d\u001a\u00020\u00132\u0006\u0010\u001e\u001a\u00020\u00042\u0006\u0010\u001f\u001a\u00020 2\u0006\u0010\u0014\u001a\u00020\u00152\b\b\u0002\u0010\u0016\u001a\u00020\b2\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00130\u0018H\u0086\bJ9\u0010\u001d\u001a\u00020\u00132\u0006\u0010!\u001a\u00020\u000f2\u0006\u0010\u001f\u001a\u00020 2\u0006\u0010\u0014\u001a\u00020\u00152\b\b\u0002\u0010\u0016\u001a\u00020\b2\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00130\u0018H\u0086\bR\u0011\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006R\u001c\u0010\u0007\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\u000e\n\u0000\u0012\u0004\b\t\u0010\u0002\u001a\u0004\b\u0007\u0010\nR\u0011\u0010\u000b\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u0006\u00a8\u0006\""},
   d2 = {"Lmods/betterfoliage/client/integration/ShadersModIntegration;", "", "()V", "grassDefaultBlockId", "", "getGrassDefaultBlockId", "()J", "isAvailable", "", "isAvailable$annotations", "()Z", "leavesDefaultBlockId", "getLeavesDefaultBlockId", "blockIdFor", "blockState", "Lnet/minecraft/block/state/IBlockState;", "getBlockIdOverride", "original", "grass", "", "renderer", "Lnet/minecraft/client/renderer/WorldRenderer;", "enabled", "func", "Lkotlin/Function0;", "leaves", "logEntityData", "name", "", "renderAs", "blockId", "renderType", "Lnet/minecraft/util/EnumBlockRenderType;", "state", "BetterFoliage-MC1.12"}
)
public final class ShadersModIntegration {
   private static final boolean isAvailable = Reflection.allAvailable(Refs.INSTANCE.getSVertexBuilder(), Refs.INSTANCE.getPushEntity_state(), Refs.INSTANCE.getPushEntity_num(), Refs.INSTANCE.getPopEntity());
   private static final long grassDefaultBlockId;
   private static final long leavesDefaultBlockId;
   public static final ShadersModIntegration INSTANCE;

   public static final boolean isAvailable() {
      return isAvailable;
   }

   public final long getGrassDefaultBlockId() {
      return grassDefaultBlockId;
   }

   public final long getLeavesDefaultBlockId() {
      return leavesDefaultBlockId;
   }

   public final long blockIdFor(@NotNull IBlockState blockState) {
      Intrinsics.checkParameterIsNotNull(blockState, "blockState");
      return (long)Block.REGISTRY.getIDForObject(blockState.getBlock()) & 65535L;
   }

   public final void logEntityData(@NotNull String name, @NotNull IBlockState blockState) {
      Intrinsics.checkParameterIsNotNull(name, "name");
      Intrinsics.checkParameterIsNotNull(blockState, "blockState");
      long blockId = (long)Block.REGISTRY.getIDForObject(blockState.getBlock()) & 65535L;
      long meta = (long)blockState.getRenderType().ordinal() & 65535L;
      long renderType = (long)blockState.getRenderType().ordinal() & 65535L;
      Client var10000 = Client.INSTANCE;
      Level var10001 = Level.INFO;
      Intrinsics.checkExpressionValueIsNotNull(Level.INFO, "INFO");
      var10000.log(var10001, "ShadersMod integration for " + name);
      var10000 = Client.INSTANCE;
      var10001 = Level.INFO;
      Intrinsics.checkExpressionValueIsNotNull(Level.INFO, "INFO");
      var10000.log(var10001, "     blockState=" + blockState);
      var10000 = Client.INSTANCE;
      var10001 = Level.INFO;
      Intrinsics.checkExpressionValueIsNotNull(Level.INFO, "INFO");
      var10000.log(var10001, "     blockId=" + blockId + ", meta=" + meta + ", type=" + renderType);
   }

   @JvmStatic
   public static final long getBlockIdOverride(long original, @NotNull IBlockState blockState) {
      Intrinsics.checkParameterIsNotNull(blockState, "blockState");
      ConfigurableBlockMatcher var10000 = Config.blocks.INSTANCE.getLeavesClasses();
      Block var10001 = blockState.getBlock();
      Intrinsics.checkExpressionValueIsNotNull(var10001, "blockState.block");
      if (var10000.matchesClass(var10001)) {
         return Config.shaders.INSTANCE.getLeavesId();
      } else {
         var10000 = Config.blocks.INSTANCE.getCrops();
         var10001 = blockState.getBlock();
         Intrinsics.checkExpressionValueIsNotNull(var10001, "blockState.block");
         return var10000.matchesClass(var10001) ? Config.shaders.INSTANCE.getGrassId() : original;
      }
   }

   public final void renderAs(long blockId, @NotNull EnumBlockRenderType renderType, @NotNull WorldRenderer renderer, boolean enabled, @NotNull Function0<Unit> func) {
      int $i$f$renderAs = 0;
      Intrinsics.checkParameterIsNotNull(renderType, "renderType");
      Intrinsics.checkParameterIsNotNull(renderer, "renderer");
      Intrinsics.checkParameterIsNotNull(func, "func");
      long var10000 = blockId | (long)(renderType.ordinal() << 16);
      if (isAvailable() && enabled) {
         Object var11 = Refs.INSTANCE.getSVertexBuilder().get(renderer);
         if (var11 == null) {
            Intrinsics.throwNpe();
         }

         Object vertexBuilder = var11;
         Refs.INSTANCE.getPushEntity_num().invoke(vertexBuilder, blockId);
         func.invoke();
         Refs.INSTANCE.getPopEntity().invoke(vertexBuilder);
      } else {
         func.invoke();
      }

   }

   public final void renderAs(@NotNull IBlockState state, @NotNull EnumBlockRenderType renderType, @NotNull WorldRenderer renderer, boolean enabled, @NotNull Function0<Unit> func) {
      int $i$f$renderAs = 0;
      Intrinsics.checkParameterIsNotNull(state, "state");
      Intrinsics.checkParameterIsNotNull(renderType, "renderType");
      Intrinsics.checkParameterIsNotNull(renderer, "renderer");
      Intrinsics.checkParameterIsNotNull(func, "func");
      long blockId$iv = this.blockIdFor(state);
      int $i$f$renderAs = 0;
      long var10000 = blockId$iv | (long)(renderType.ordinal() << 16);
      if (isAvailable() && enabled) {
         Object var14 = Refs.INSTANCE.getSVertexBuilder().get(renderer);
         if (var14 == null) {
            Intrinsics.throwNpe();
         }

         Object vertexBuilder$iv = var14;
         Refs.INSTANCE.getPushEntity_num().invoke(vertexBuilder$iv, blockId$iv);
         func.invoke();
         Refs.INSTANCE.getPopEntity().invoke(vertexBuilder$iv);
      } else {
         func.invoke();
      }

   }

   public final void grass(@NotNull WorldRenderer renderer, boolean enabled, @NotNull Function0<Unit> func) {
      int $i$f$grass = 0;
      Intrinsics.checkParameterIsNotNull(renderer, "renderer");
      Intrinsics.checkParameterIsNotNull(func, "func");
      long blockId$iv = Config.shaders.INSTANCE.getGrassId();
      EnumBlockRenderType renderType$iv = EnumBlockRenderType.MODEL;
      int $i$f$renderAs = 0;
      long var10000 = blockId$iv | (long)(renderType$iv.ordinal() << 16);
      if (isAvailable() && enabled) {
         Object var13 = Refs.INSTANCE.getSVertexBuilder().get(renderer);
         if (var13 == null) {
            Intrinsics.throwNpe();
         }

         Object vertexBuilder$iv = var13;
         Refs.INSTANCE.getPushEntity_num().invoke(vertexBuilder$iv, blockId$iv);
         func.invoke();
         Refs.INSTANCE.getPopEntity().invoke(vertexBuilder$iv);
      } else {
         func.invoke();
      }

   }

   public final void leaves(@NotNull WorldRenderer renderer, boolean enabled, @NotNull Function0<Unit> func) {
      int $i$f$leaves = 0;
      Intrinsics.checkParameterIsNotNull(renderer, "renderer");
      Intrinsics.checkParameterIsNotNull(func, "func");
      long blockId$iv = Config.shaders.INSTANCE.getLeavesId();
      EnumBlockRenderType renderType$iv = EnumBlockRenderType.MODEL;
      int $i$f$renderAs = 0;
      long var10000 = blockId$iv | (long)(renderType$iv.ordinal() << 16);
      if (isAvailable() && enabled) {
         Object var13 = Refs.INSTANCE.getSVertexBuilder().get(renderer);
         if (var13 == null) {
            Intrinsics.throwNpe();
         }

         Object vertexBuilder$iv = var13;
         Refs.INSTANCE.getPushEntity_num().invoke(vertexBuilder$iv, blockId$iv);
         func.invoke();
         Refs.INSTANCE.getPopEntity().invoke(vertexBuilder$iv);
      } else {
         func.invoke();
      }

   }

   private ShadersModIntegration() {
   }

   static {
      ShadersModIntegration var0 = new ShadersModIntegration();
      INSTANCE = var0;
      BlockTallGrass var10001 = Blocks.TALLGRASS;
      Intrinsics.checkExpressionValueIsNotNull(Blocks.TALLGRASS, "Blocks.TALLGRASS");
      IBlockState var1 = var10001.getDefaultState().withProperty((IProperty)BlockTallGrass.TYPE, (Comparable)EnumType.GRASS);
      Intrinsics.checkExpressionValueIsNotNull(var1, "Blocks.TALLGRASS.default\u2026TallGrass.EnumType.GRASS)");
      grassDefaultBlockId = var0.blockIdFor(var1);
      BlockLeaves var2 = Blocks.LEAVES;
      Intrinsics.checkExpressionValueIsNotNull(Blocks.LEAVES, "Blocks.LEAVES");
      IBlockState var3 = var2.getDefaultState();
      Intrinsics.checkExpressionValueIsNotNull(var3, "Blocks.LEAVES.defaultState");
      leavesDefaultBlockId = var0.blockIdFor(var3);
      Client var10000 = Client.INSTANCE;
      Level var4 = Level.INFO;
      Intrinsics.checkExpressionValueIsNotNull(Level.INFO, "INFO");
      var10000.log(var4, "ShadersMod integration is " + (isAvailable ? "enabled" : "disabled"));
   }
}
