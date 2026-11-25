package mods.betterfoliage.client.render.column;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IntIterator;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntRange;
import mods.betterfoliage.client.chunk.ChunkOverlayLayer;
import mods.betterfoliage.client.chunk.ChunkOverlayManager;
import mods.betterfoliage.client.config.Config;
import mods.betterfoliage.client.render.Utils;
import mods.octarinecore.client.render.BlockContext;
import mods.octarinecore.client.resource.ModelRenderRegistry;
import mods.octarinecore.common.GeometryKt;
import mods.octarinecore.common.Int3;
import mods.octarinecore.common.Rotation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000x\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0006\b&\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0003J\u000e\u0010\u001d\u001a\u00020\u00022\u0006\u0010\u001e\u001a\u00020\u001fJ\u0018\u0010\u001d\u001a\u00020\u00022\u0006\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020#H\u0016J\u0018\u0010$\u001a\u00020%2\u0006\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020#H\u0016J\"\u0010&\u001a\u00020'*\u00020\u001f2\u0006\u0010(\u001a\u00020)2\u0006\u0010*\u001a\u00020+2\u0006\u0010,\u001a\u00020\u0006J;\u0010-\u001a\b\u0012\u0004\u0012\u00020/0.*\b\u0012\u0004\u0012\u00020/0.2\u0006\u0010\u001e\u001a\u00020\u001f2\u0006\u0010(\u001a\u00020)2\u0006\u00100\u001a\u00020+2\u0006\u00101\u001a\u000202¢\u0006\u0002\u00103J(\u00104\u001a\u00020%*\b\u0012\u0004\u0012\u00020/0.2\u0006\u00105\u001a\u0002022\u0006\u00106\u001a\u00020/H\u0086\b¢\u0006\u0002\u00107R\u0017\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u001e\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\f0\nX¦\u0004¢\u0006\u0006\u001a\u0004\b\r\u0010\u000eR\u0012\u0010\u000f\u001a\u00020\fX¦\u0004¢\u0006\u0006\u001a\u0004\b\u0010\u0010\u0011R\u0012\u0010\u0012\u001a\u00020\fX¦\u0004¢\u0006\u0006\u001a\u0004\b\u0013\u0010\u0011R\u0012\u0010\u0014\u001a\u00020\fX¦\u0004¢\u0006\u0006\u001a\u0004\b\u0015\u0010\u0011R\u0018\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00180\u0017X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0019\u0010\u001aR\u001e\u0010\u001b\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\f0\nX¦\u0004¢\u0006\u0006\u001a\u0004\b\u001c\u0010\u000e¨\u00068"},
   d2 = {"Lmods/betterfoliage/client/render/column/ColumnRenderLayer;", "Lmods/betterfoliage/client/chunk/ChunkOverlayLayer;", "Lmods/betterfoliage/client/render/column/ColumnLayerData;", "()V", "allNeighborOffsets", "", "Lmods/octarinecore/common/Int3;", "getAllNeighborOffsets", "()Ljava/util/List;", "blockPredicate", "Lkotlin/Function1;", "Lnet/minecraft/block/state/IBlockState;", "", "getBlockPredicate", "()Lkotlin/jvm/functions/Function1;", "connectSolids", "getConnectSolids", "()Z", "defaultToY", "getDefaultToY", "lenientConnect", "getLenientConnect", "registry", "Lmods/octarinecore/client/resource/ModelRenderRegistry;", "Lmods/betterfoliage/client/render/column/ColumnTextureInfo;", "getRegistry", "()Lmods/octarinecore/client/resource/ModelRenderRegistry;", "surroundPredicate", "getSurroundPredicate", "calculate", "ctx", "Lmods/octarinecore/client/render/BlockContext;", "world", "Lnet/minecraft/world/IBlockAccess;", "pos", "Lnet/minecraft/util/math/BlockPos;", "onBlockUpdate", "", "blockType", "Lmods/betterfoliage/client/render/column/ColumnLayerData$SpecialRender$BlockType;", "rotation", "Lmods/octarinecore/common/Rotation;", "axis", "Lnet/minecraft/util/EnumFacing$Axis;", "offset", "checkNeighbors", "", "Lmods/betterfoliage/client/render/column/ColumnLayerData$SpecialRender$QuadrantType;", "logAxis", "yOff", "", "([Lmods/betterfoliage/client/render/column/ColumnLayerData$SpecialRender$QuadrantType;Lmods/octarinecore/client/render/BlockContext;Lmods/octarinecore/common/Rotation;Lnet/minecraft/util/EnumFacing$Axis;I)[Lmods/betterfoliage/client/render/column/ColumnLayerData$SpecialRender$QuadrantType;", "upgrade", "idx", "value", "([Lmods/betterfoliage/client/render/column/ColumnLayerData$SpecialRender$QuadrantType;ILmods/betterfoliage/client/render/column/ColumnLayerData$SpecialRender$QuadrantType;)V", "BetterFoliage-MC1.12"}
)
public abstract class ColumnRenderLayer implements ChunkOverlayLayer<ColumnLayerData> {
   @NotNull
   private final List<Int3> allNeighborOffsets;

   @NotNull
   public abstract ModelRenderRegistry<ColumnTextureInfo> getRegistry();

   @NotNull
   public abstract Function1<IBlockState, Boolean> getBlockPredicate();

   @NotNull
   public abstract Function1<IBlockState, Boolean> getSurroundPredicate();

   public abstract boolean getConnectSolids();

   public abstract boolean getLenientConnect();

   public abstract boolean getDefaultToY();

   @NotNull
   public final List<Int3> getAllNeighborOffsets() {
      return this.allNeighborOffsets;
   }

   public void onBlockUpdate(@NotNull IBlockAccess world, @NotNull BlockPos pos) {
      Intrinsics.checkParameterIsNotNull(world, "world");
      Intrinsics.checkParameterIsNotNull(pos, "pos");
      Iterable $this$forEach$iv = (Iterable)this.allNeighborOffsets;
      int $i$f$forEach = 0;

      for(Object element$iv : $this$forEach$iv) {
         Int3 offset = (Int3)element$iv;
         int $i$a$-forEach-ColumnRenderLayer$onBlockUpdate$1 = 0;
         ChunkOverlayManager.INSTANCE.clear(this, GeometryKt.plus(pos, offset));
      }

   }

   @NotNull
   public ColumnLayerData calculate(@NotNull IBlockAccess world, @NotNull BlockPos pos) {
      Intrinsics.checkParameterIsNotNull(world, "world");
      Intrinsics.checkParameterIsNotNull(pos, "pos");
      return this.calculate(new BlockContext(world, pos));
   }

   @NotNull
   public final ColumnLayerData calculate(@NotNull BlockContext ctx) {
      Intrinsics.checkParameterIsNotNull(ctx, "ctx");
      if (ctx.isSurroundedBy(this.getSurroundPredicate())) {
         return ColumnLayerData.SkipRender.INSTANCE;
      } else {
         ColumnTextureInfo var10000 = (ColumnTextureInfo)this.getRegistry().get(ctx);
         if (var10000 == null) {
            return ColumnLayerData.ResolveError.INSTANCE;
         } else {
            ColumnTextureInfo columnTextures = var10000;
            Axis var26 = columnTextures.getAxis();
            if (var26 == null) {
               if (!this.getDefaultToY()) {
                  return ColumnLayerData.NormalRender.INSTANCE;
               }

               var26 = Axis.Y;
            }

            Axis logAxis = var26;
            Rotation baseRotation = Utils.getRotationFromUp()[GeometryKt.getFace(TuplesKt.to(logAxis, AxisDirection.POSITIVE)).ordinal()];
            ColumnLayerData.SpecialRender.BlockType upType = this.blockType(ctx, baseRotation, logAxis, new Int3(0, 1, 0));
            ColumnLayerData.SpecialRender.BlockType downType = this.blockType(ctx, baseRotation, logAxis, new Int3(0, -1, 0));
            byte quadrantsTop = 4;
            ColumnLayerData.SpecialRender.QuadrantType[] quadrantsBottom = new ColumnLayerData.SpecialRender.QuadrantType[quadrantsTop];

            for(int var10 = 0; var10 < quadrantsTop; ++var10) {
               int $i$a$-<init>-ColumnRenderLayer$calculate$quadrants$1 = 0;
               ColumnLayerData.SpecialRender.QuadrantType var18 = ColumnLayerData.SpecialRender.QuadrantType.SMALL_RADIUS;
               quadrantsBottom[var10] = var18;
            }

            ColumnLayerData.SpecialRender.QuadrantType[] quadrants = this.checkNeighbors(quadrantsBottom, ctx, baseRotation, logAxis, 0);
            byte var20 = 4;
            ColumnLayerData.SpecialRender.QuadrantType[] var21 = new ColumnLayerData.SpecialRender.QuadrantType[var20];

            for(int it = 0; it < var20; ++it) {
               int $i$a$-<init>-ColumnRenderLayer$calculate$quadrantsTop$1 = 0;
               ColumnLayerData.SpecialRender.QuadrantType var17 = ColumnLayerData.SpecialRender.QuadrantType.SMALL_RADIUS;
               var21[it] = var17;
            }

            if (upType == ColumnLayerData.SpecialRender.BlockType.PARALLEL) {
               this.checkNeighbors(var21, ctx, baseRotation, logAxis, 1);
            }

            byte var22 = 4;
            ColumnLayerData.SpecialRender.QuadrantType[] var23 = new ColumnLayerData.SpecialRender.QuadrantType[var22];

            for(int var24 = 0; var24 < var22; ++var24) {
               int $i$a$-<init>-ColumnRenderLayer$calculate$quadrantsBottom$1 = 0;
               ColumnLayerData.SpecialRender.QuadrantType var25 = ColumnLayerData.SpecialRender.QuadrantType.SMALL_RADIUS;
               var23[var24] = var25;
            }

            if (downType == ColumnLayerData.SpecialRender.BlockType.PARALLEL) {
               this.checkNeighbors(var23, ctx, baseRotation, logAxis, -1);
            }

            return new ColumnLayerData.SpecialRender(columnTextures, upType, downType, quadrants, var21, var23);
         }
      }
   }

   public final void upgrade(@NotNull ColumnLayerData.SpecialRender.QuadrantType[] $this$upgrade, int idx, @NotNull ColumnLayerData.SpecialRender.QuadrantType value) {
      int $i$f$upgrade = 0;
      Intrinsics.checkParameterIsNotNull($this$upgrade, "$this$upgrade");
      Intrinsics.checkParameterIsNotNull(value, "value");
      if ($this$upgrade[idx].ordinal() < value.ordinal()) {
         $this$upgrade[idx] = value;
      }

   }

   @NotNull
   public final ColumnLayerData.SpecialRender.QuadrantType[] checkNeighbors(@NotNull ColumnLayerData.SpecialRender.QuadrantType[] $this$checkNeighbors, @NotNull BlockContext ctx, @NotNull Rotation rotation, @NotNull Axis logAxis, int yOff) {
      Intrinsics.checkParameterIsNotNull($this$checkNeighbors, "$this$checkNeighbors");
      Intrinsics.checkParameterIsNotNull(ctx, "ctx");
      Intrinsics.checkParameterIsNotNull(rotation, "rotation");
      Intrinsics.checkParameterIsNotNull(logAxis, "logAxis");
      ColumnLayerData.SpecialRender.BlockType blkS = this.blockType(ctx, rotation, logAxis, new Int3(0, yOff, 1));
      ColumnLayerData.SpecialRender.BlockType blkE = this.blockType(ctx, rotation, logAxis, new Int3(1, yOff, 0));
      ColumnLayerData.SpecialRender.BlockType blkN = this.blockType(ctx, rotation, logAxis, new Int3(0, yOff, -1));
      ColumnLayerData.SpecialRender.BlockType blkW = this.blockType(ctx, rotation, logAxis, new Int3(-1, yOff, 0));
      if (this.getConnectSolids()) {
         if (blkS == ColumnLayerData.SpecialRender.BlockType.SOLID) {
            byte blkNW = 3;
            ColumnLayerData.SpecialRender.QuadrantType value$iv = ColumnLayerData.SpecialRender.QuadrantType.SQUARE;
            int $i$f$upgrade = 0;
            if ($this$checkNeighbors[blkNW].ordinal() < value$iv.ordinal()) {
               $this$checkNeighbors[blkNW] = value$iv;
            }

            blkNW = 0;
            value$iv = ColumnLayerData.SpecialRender.QuadrantType.SQUARE;
            $i$f$upgrade = 0;
            if ($this$checkNeighbors[blkNW].ordinal() < value$iv.ordinal()) {
               $this$checkNeighbors[blkNW] = value$iv;
            }
         }

         if (blkE == ColumnLayerData.SpecialRender.BlockType.SOLID) {
            byte var20 = 0;
            ColumnLayerData.SpecialRender.QuadrantType value$iv = ColumnLayerData.SpecialRender.QuadrantType.SQUARE;
            int $i$f$upgrade = 0;
            if ($this$checkNeighbors[var20].ordinal() < value$iv.ordinal()) {
               $this$checkNeighbors[var20] = value$iv;
            }

            var20 = 1;
            value$iv = ColumnLayerData.SpecialRender.QuadrantType.SQUARE;
            $i$f$upgrade = 0;
            if ($this$checkNeighbors[var20].ordinal() < value$iv.ordinal()) {
               $this$checkNeighbors[var20] = value$iv;
            }
         }

         if (blkN == ColumnLayerData.SpecialRender.BlockType.SOLID) {
            byte var22 = 1;
            ColumnLayerData.SpecialRender.QuadrantType value$iv = ColumnLayerData.SpecialRender.QuadrantType.SQUARE;
            int $i$f$upgrade = 0;
            if ($this$checkNeighbors[var22].ordinal() < value$iv.ordinal()) {
               $this$checkNeighbors[var22] = value$iv;
            }

            var22 = 2;
            value$iv = ColumnLayerData.SpecialRender.QuadrantType.SQUARE;
            $i$f$upgrade = 0;
            if ($this$checkNeighbors[var22].ordinal() < value$iv.ordinal()) {
               $this$checkNeighbors[var22] = value$iv;
            }
         }

         if (blkW == ColumnLayerData.SpecialRender.BlockType.SOLID) {
            byte var24 = 2;
            ColumnLayerData.SpecialRender.QuadrantType value$iv = ColumnLayerData.SpecialRender.QuadrantType.SQUARE;
            int $i$f$upgrade = 0;
            if ($this$checkNeighbors[var24].ordinal() < value$iv.ordinal()) {
               $this$checkNeighbors[var24] = value$iv;
            }

            var24 = 3;
            value$iv = ColumnLayerData.SpecialRender.QuadrantType.SQUARE;
            $i$f$upgrade = 0;
            if ($this$checkNeighbors[var24].ordinal() < value$iv.ordinal()) {
               $this$checkNeighbors[var24] = value$iv;
            }
         }

         if (blkS == ColumnLayerData.SpecialRender.BlockType.SOLID && blkE == ColumnLayerData.SpecialRender.BlockType.SOLID) {
            byte var26 = 0;
            ColumnLayerData.SpecialRender.QuadrantType value$iv = ColumnLayerData.SpecialRender.QuadrantType.INVISIBLE;
            int $i$f$upgrade = 0;
            if ($this$checkNeighbors[var26].ordinal() < value$iv.ordinal()) {
               $this$checkNeighbors[var26] = value$iv;
            }
         }

         if (blkN == ColumnLayerData.SpecialRender.BlockType.SOLID && blkE == ColumnLayerData.SpecialRender.BlockType.SOLID) {
            byte var27 = 1;
            ColumnLayerData.SpecialRender.QuadrantType value$iv = ColumnLayerData.SpecialRender.QuadrantType.INVISIBLE;
            int $i$f$upgrade = 0;
            if ($this$checkNeighbors[var27].ordinal() < value$iv.ordinal()) {
               $this$checkNeighbors[var27] = value$iv;
            }
         }

         if (blkN == ColumnLayerData.SpecialRender.BlockType.SOLID && blkW == ColumnLayerData.SpecialRender.BlockType.SOLID) {
            byte var28 = 2;
            ColumnLayerData.SpecialRender.QuadrantType value$iv = ColumnLayerData.SpecialRender.QuadrantType.INVISIBLE;
            int $i$f$upgrade = 0;
            if ($this$checkNeighbors[var28].ordinal() < value$iv.ordinal()) {
               $this$checkNeighbors[var28] = value$iv;
            }
         }

         if (blkS == ColumnLayerData.SpecialRender.BlockType.SOLID && blkW == ColumnLayerData.SpecialRender.BlockType.SOLID) {
            byte var29 = 3;
            ColumnLayerData.SpecialRender.QuadrantType value$iv = ColumnLayerData.SpecialRender.QuadrantType.INVISIBLE;
            int $i$f$upgrade = 0;
            if ($this$checkNeighbors[var29].ordinal() < value$iv.ordinal()) {
               $this$checkNeighbors[var29] = value$iv;
            }
         }
      }

      ColumnLayerData.SpecialRender.BlockType blkSE = this.blockType(ctx, rotation, logAxis, new Int3(1, yOff, 1));
      ColumnLayerData.SpecialRender.BlockType blkNE = this.blockType(ctx, rotation, logAxis, new Int3(1, yOff, -1));
      ColumnLayerData.SpecialRender.BlockType blkNW = this.blockType(ctx, rotation, logAxis, new Int3(-1, yOff, -1));
      ColumnLayerData.SpecialRender.BlockType blkSW = this.blockType(ctx, rotation, logAxis, new Int3(-1, yOff, 1));
      if (this.getLenientConnect()) {
         if (blkE == ColumnLayerData.SpecialRender.BlockType.PARALLEL && (blkSE == ColumnLayerData.SpecialRender.BlockType.PARALLEL || blkNE == ColumnLayerData.SpecialRender.BlockType.PARALLEL)) {
            byte idx$iv = 0;
            ColumnLayerData.SpecialRender.QuadrantType value$iv = ColumnLayerData.SpecialRender.QuadrantType.SQUARE;
            int $i$f$upgrade = 0;
            if ($this$checkNeighbors[idx$iv].ordinal() < value$iv.ordinal()) {
               $this$checkNeighbors[idx$iv] = value$iv;
            }

            idx$iv = 1;
            value$iv = ColumnLayerData.SpecialRender.QuadrantType.SQUARE;
            $i$f$upgrade = 0;
            if ($this$checkNeighbors[idx$iv].ordinal() < value$iv.ordinal()) {
               $this$checkNeighbors[idx$iv] = value$iv;
            }
         }

         if (blkN == ColumnLayerData.SpecialRender.BlockType.PARALLEL && (blkNE == ColumnLayerData.SpecialRender.BlockType.PARALLEL || blkNW == ColumnLayerData.SpecialRender.BlockType.PARALLEL)) {
            byte var55 = 1;
            ColumnLayerData.SpecialRender.QuadrantType value$iv = ColumnLayerData.SpecialRender.QuadrantType.SQUARE;
            int $i$f$upgrade = 0;
            if ($this$checkNeighbors[var55].ordinal() < value$iv.ordinal()) {
               $this$checkNeighbors[var55] = value$iv;
            }

            var55 = 2;
            value$iv = ColumnLayerData.SpecialRender.QuadrantType.SQUARE;
            $i$f$upgrade = 0;
            if ($this$checkNeighbors[var55].ordinal() < value$iv.ordinal()) {
               $this$checkNeighbors[var55] = value$iv;
            }
         }

         if (blkW == ColumnLayerData.SpecialRender.BlockType.PARALLEL && (blkNW == ColumnLayerData.SpecialRender.BlockType.PARALLEL || blkSW == ColumnLayerData.SpecialRender.BlockType.PARALLEL)) {
            byte var57 = 2;
            ColumnLayerData.SpecialRender.QuadrantType value$iv = ColumnLayerData.SpecialRender.QuadrantType.SQUARE;
            int $i$f$upgrade = 0;
            if ($this$checkNeighbors[var57].ordinal() < value$iv.ordinal()) {
               $this$checkNeighbors[var57] = value$iv;
            }

            var57 = 3;
            value$iv = ColumnLayerData.SpecialRender.QuadrantType.SQUARE;
            $i$f$upgrade = 0;
            if ($this$checkNeighbors[var57].ordinal() < value$iv.ordinal()) {
               $this$checkNeighbors[var57] = value$iv;
            }
         }

         if (blkS == ColumnLayerData.SpecialRender.BlockType.PARALLEL && (blkSE == ColumnLayerData.SpecialRender.BlockType.PARALLEL || blkSW == ColumnLayerData.SpecialRender.BlockType.PARALLEL)) {
            byte var59 = 3;
            ColumnLayerData.SpecialRender.QuadrantType value$iv = ColumnLayerData.SpecialRender.QuadrantType.SQUARE;
            int $i$f$upgrade = 0;
            if ($this$checkNeighbors[var59].ordinal() < value$iv.ordinal()) {
               $this$checkNeighbors[var59] = value$iv;
            }

            var59 = 0;
            value$iv = ColumnLayerData.SpecialRender.QuadrantType.SQUARE;
            $i$f$upgrade = 0;
            if ($this$checkNeighbors[var59].ordinal() < value$iv.ordinal()) {
               $this$checkNeighbors[var59] = value$iv;
            }
         }
      }

      if (blkN == ColumnLayerData.SpecialRender.BlockType.PARALLEL && blkW == ColumnLayerData.SpecialRender.BlockType.PARALLEL && (this.getLenientConnect() || blkNW == ColumnLayerData.SpecialRender.BlockType.PARALLEL)) {
         byte var61 = 0;
         ColumnLayerData.SpecialRender.QuadrantType value$iv = ColumnLayerData.SpecialRender.QuadrantType.LARGE_RADIUS;
         int $i$f$upgrade = 0;
         if ($this$checkNeighbors[var61].ordinal() < value$iv.ordinal()) {
            $this$checkNeighbors[var61] = value$iv;
         }

         var61 = 1;
         value$iv = ColumnLayerData.SpecialRender.QuadrantType.SQUARE;
         $i$f$upgrade = 0;
         if ($this$checkNeighbors[var61].ordinal() < value$iv.ordinal()) {
            $this$checkNeighbors[var61] = value$iv;
         }

         var61 = 3;
         value$iv = ColumnLayerData.SpecialRender.QuadrantType.SQUARE;
         $i$f$upgrade = 0;
         if ($this$checkNeighbors[var61].ordinal() < value$iv.ordinal()) {
            $this$checkNeighbors[var61] = value$iv;
         }

         var61 = 2;
         value$iv = ColumnLayerData.SpecialRender.QuadrantType.INVISIBLE;
         $i$f$upgrade = 0;
         if ($this$checkNeighbors[var61].ordinal() < value$iv.ordinal()) {
            $this$checkNeighbors[var61] = value$iv;
         }
      }

      if (blkS == ColumnLayerData.SpecialRender.BlockType.PARALLEL && blkW == ColumnLayerData.SpecialRender.BlockType.PARALLEL && (this.getLenientConnect() || blkSW == ColumnLayerData.SpecialRender.BlockType.PARALLEL)) {
         byte var65 = 1;
         ColumnLayerData.SpecialRender.QuadrantType value$iv = ColumnLayerData.SpecialRender.QuadrantType.LARGE_RADIUS;
         int $i$f$upgrade = 0;
         if ($this$checkNeighbors[var65].ordinal() < value$iv.ordinal()) {
            $this$checkNeighbors[var65] = value$iv;
         }

         var65 = 0;
         value$iv = ColumnLayerData.SpecialRender.QuadrantType.SQUARE;
         $i$f$upgrade = 0;
         if ($this$checkNeighbors[var65].ordinal() < value$iv.ordinal()) {
            $this$checkNeighbors[var65] = value$iv;
         }

         var65 = 2;
         value$iv = ColumnLayerData.SpecialRender.QuadrantType.SQUARE;
         $i$f$upgrade = 0;
         if ($this$checkNeighbors[var65].ordinal() < value$iv.ordinal()) {
            $this$checkNeighbors[var65] = value$iv;
         }

         var65 = 3;
         value$iv = ColumnLayerData.SpecialRender.QuadrantType.INVISIBLE;
         $i$f$upgrade = 0;
         if ($this$checkNeighbors[var65].ordinal() < value$iv.ordinal()) {
            $this$checkNeighbors[var65] = value$iv;
         }
      }

      if (blkS == ColumnLayerData.SpecialRender.BlockType.PARALLEL && blkE == ColumnLayerData.SpecialRender.BlockType.PARALLEL && (this.getLenientConnect() || blkSE == ColumnLayerData.SpecialRender.BlockType.PARALLEL)) {
         byte var69 = 2;
         ColumnLayerData.SpecialRender.QuadrantType value$iv = ColumnLayerData.SpecialRender.QuadrantType.LARGE_RADIUS;
         int $i$f$upgrade = 0;
         if ($this$checkNeighbors[var69].ordinal() < value$iv.ordinal()) {
            $this$checkNeighbors[var69] = value$iv;
         }

         var69 = 1;
         value$iv = ColumnLayerData.SpecialRender.QuadrantType.SQUARE;
         $i$f$upgrade = 0;
         if ($this$checkNeighbors[var69].ordinal() < value$iv.ordinal()) {
            $this$checkNeighbors[var69] = value$iv;
         }

         var69 = 3;
         value$iv = ColumnLayerData.SpecialRender.QuadrantType.SQUARE;
         $i$f$upgrade = 0;
         if ($this$checkNeighbors[var69].ordinal() < value$iv.ordinal()) {
            $this$checkNeighbors[var69] = value$iv;
         }

         var69 = 0;
         value$iv = ColumnLayerData.SpecialRender.QuadrantType.INVISIBLE;
         $i$f$upgrade = 0;
         if ($this$checkNeighbors[var69].ordinal() < value$iv.ordinal()) {
            $this$checkNeighbors[var69] = value$iv;
         }
      }

      if (blkN == ColumnLayerData.SpecialRender.BlockType.PARALLEL && blkE == ColumnLayerData.SpecialRender.BlockType.PARALLEL && (this.getLenientConnect() || blkNE == ColumnLayerData.SpecialRender.BlockType.PARALLEL)) {
         byte var73 = 3;
         ColumnLayerData.SpecialRender.QuadrantType value$iv = ColumnLayerData.SpecialRender.QuadrantType.LARGE_RADIUS;
         int $i$f$upgrade = 0;
         if ($this$checkNeighbors[var73].ordinal() < value$iv.ordinal()) {
            $this$checkNeighbors[var73] = value$iv;
         }

         var73 = 0;
         value$iv = ColumnLayerData.SpecialRender.QuadrantType.SQUARE;
         $i$f$upgrade = 0;
         if ($this$checkNeighbors[var73].ordinal() < value$iv.ordinal()) {
            $this$checkNeighbors[var73] = value$iv;
         }

         var73 = 2;
         value$iv = ColumnLayerData.SpecialRender.QuadrantType.SQUARE;
         $i$f$upgrade = 0;
         if ($this$checkNeighbors[var73].ordinal() < value$iv.ordinal()) {
            $this$checkNeighbors[var73] = value$iv;
         }

         var73 = 1;
         value$iv = ColumnLayerData.SpecialRender.QuadrantType.INVISIBLE;
         $i$f$upgrade = 0;
         if ($this$checkNeighbors[var73].ordinal() < value$iv.ordinal()) {
            $this$checkNeighbors[var73] = value$iv;
         }
      }

      return $this$checkNeighbors;
   }

   @NotNull
   public final ColumnLayerData.SpecialRender.BlockType blockType(@NotNull BlockContext $this$blockType, @NotNull Rotation rotation, @NotNull Axis axis, @NotNull Int3 offset) {
      Intrinsics.checkParameterIsNotNull($this$blockType, "$this$blockType");
      Intrinsics.checkParameterIsNotNull(rotation, "rotation");
      Intrinsics.checkParameterIsNotNull(axis, "axis");
      Intrinsics.checkParameterIsNotNull(offset, "offset");
      Int3 offsetRot = offset.rotate(rotation);
      IBlockState state = $this$blockType.blockState(offsetRot);
      Function1 var10000 = this.getBlockPredicate();
      Intrinsics.checkExpressionValueIsNotNull(state, "state");
      ColumnLayerData.SpecialRender.BlockType var12;
      if (!((Boolean)var10000.invoke(state)).booleanValue()) {
         var12 = state.isOpaqueCube() ? ColumnLayerData.SpecialRender.BlockType.SOLID : ColumnLayerData.SpecialRender.BlockType.NONSOLID;
      } else {
         ModelRenderRegistry var13 = this.getRegistry();
         IBlockAccess var10002 = $this$blockType.getWorld();
         if (var10002 == null) {
            Intrinsics.throwNpe();
         }

         label36: {
            ColumnTextureInfo var14 = (ColumnTextureInfo)var13.get(state, var10002, GeometryKt.plus($this$blockType.getPos(), offsetRot));
            if (var14 != null) {
               var15 = var14.getAxis();
               if (var15 != null) {
                  break label36;
               }
            }

            var15 = Config.roundLogs.INSTANCE.getDefaultY() ? Axis.Y : null;
         }

         if (var15 != null) {
            Axis var7 = var15;
            boolean var8 = false;
            boolean var9 = false;
            int $i$a$-let-ColumnRenderLayer$blockType$1 = 0;
            var12 = var7 == axis ? ColumnLayerData.SpecialRender.BlockType.PARALLEL : ColumnLayerData.SpecialRender.BlockType.PERPENDICULAR;
            if ((var7 == axis ? ColumnLayerData.SpecialRender.BlockType.PARALLEL : ColumnLayerData.SpecialRender.BlockType.PERPENDICULAR) != null) {
               return var12;
            }
         }

         var12 = ColumnLayerData.SpecialRender.BlockType.SOLID;
      }

      return var12;
   }

   public ColumnRenderLayer() {
      byte $this$flatMap$iv = -1;
      Iterable var32 = (Iterable)(new IntRange($this$flatMap$iv, 1));
      int $i$f$flatMap = 0;
      Collection destination$iv$iv = (Collection)(new ArrayList());
      int $i$f$flatMapTo = 0;
      Iterator var6 = var32.iterator();

      while(var6.hasNext()) {
         int element$iv$iv = ((IntIterator)var6).nextInt();
         int offsetX = element$iv$iv;
         int $i$a$-flatMap-ColumnRenderLayer$allNeighborOffsets$1 = 0;
         byte $this$flatMap$iv = -1;
         Iterable $this$flatMap$iv = (Iterable)(new IntRange($this$flatMap$iv, 1));
         int $i$f$flatMap = 0;
         Collection destination$iv$iv = (Collection)(new ArrayList());
         int $i$f$flatMapTo = 0;
         Iterator var15 = $this$flatMap$iv.iterator();

         while(var15.hasNext()) {
            int element$iv$iv = ((IntIterator)var15).nextInt();
            int offsetY = element$iv$iv;
            int $i$a$-flatMap-ColumnRenderLayer$allNeighborOffsets$1$1 = 0;
            byte $this$map$iv = -1;
            Iterable $this$map$iv = (Iterable)(new IntRange($this$map$iv, 1));
            int $i$f$map = 0;
            Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));
            int $i$f$mapTo = 0;
            Iterator var24 = $this$map$iv.iterator();

            while(var24.hasNext()) {
               int item$iv$iv = ((IntIterator)var24).nextInt();
               int $i$a$-map-ColumnRenderLayer$allNeighborOffsets$1$1$1 = 0;
               Int3 var29 = new Int3(offsetX, offsetY, item$iv$iv);
               destination$iv$iv.add(var29);
            }

            Iterable list$iv$iv = (Iterable)((List)destination$iv$iv);
            CollectionsKt.addAll(destination$iv$iv, list$iv$iv);
         }

         Iterable list$iv$iv = (Iterable)((List)destination$iv$iv);
         CollectionsKt.addAll(destination$iv$iv, list$iv$iv);
      }

      List var31 = (List)destination$iv$iv;
      this.allNeighborOffsets = var31;
   }
}
