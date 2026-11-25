package mods.octarinecore.client.render;

import java.lang.reflect.Constructor;
import java.util.BitSet;
import java.util.List;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import mods.betterfoliage.loader.Refs;
import mods.octarinecore.common.GeometryKt;
import mods.octarinecore.common.Int3;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockModelRenderer.AmbientOcclusionFace;
import net.minecraft.util.EnumFacing;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010 \n\u0002\b\f\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0007\n\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u0019\u0010\u001e\u001a\u00020\u000b2\u0006\u0010\u001f\u001a\u00020\u00032\u0006\u0010 \u001a\u00020\u0003H\u0086\u0002J\"\u0010!\u001a\u00020\"2\u0006\u0010#\u001a\u00020$2\b\b\u0002\u0010%\u001a\u00020&2\b\b\u0002\u0010'\u001a\u00020(R\u0015\u0010\u0005\u001a\u00060\u0006R\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\n\u001a\u00020\u000b¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u000e\u001a\u00020\u000b¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\rR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0012\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0011R\u0017\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0015¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\u0018\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0011R\u0011\u0010\u001a\u001a\u00020\u000b¢\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\rR\u0011\u0010\u001c\u001a\u00020\u000b¢\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\r¨\u0006)"},
   d2 = {"Lmods/octarinecore/client/render/AoFaceData;", "", "face", "Lnet/minecraft/util/EnumFacing;", "(Lnet/minecraft/util/EnumFacing;)V", "ao", "Lnet/minecraft/client/renderer/BlockModelRenderer$AmbientOcclusionFace;", "Lnet/minecraft/client/renderer/BlockModelRenderer;", "getAo", "()Lnet/minecraft/client/renderer/BlockModelRenderer$AmbientOcclusionFace;", "bottomLeft", "Lmods/octarinecore/client/render/AoData;", "getBottomLeft", "()Lmods/octarinecore/client/render/AoData;", "bottomRight", "getBottomRight", "getFace", "()Lnet/minecraft/util/EnumFacing;", "left", "getLeft", "ordered", "", "getOrdered", "()Ljava/util/List;", "top", "getTop", "topLeft", "getTopLeft", "topRight", "getTopRight", "get", "dir1", "dir2", "update", "", "offset", "Lmods/octarinecore/common/Int3;", "useBounds", "", "multiplier", "", "BetterFoliage-MC1.12"}
)
public final class AoFaceData {
   @NotNull
   private final AmbientOcclusionFace ao;
   @NotNull
   private final EnumFacing top;
   @NotNull
   private final EnumFacing left;
   @NotNull
   private final AoData topLeft;
   @NotNull
   private final AoData topRight;
   @NotNull
   private final AoData bottomLeft;
   @NotNull
   private final AoData bottomRight;
   @NotNull
   private final List<AoData> ordered;
   @NotNull
   private final EnumFacing face;

   @NotNull
   public final AmbientOcclusionFace getAo() {
      return this.ao;
   }

   @NotNull
   public final EnumFacing getTop() {
      return this.top;
   }

   @NotNull
   public final EnumFacing getLeft() {
      return this.left;
   }

   @NotNull
   public final AoData getTopLeft() {
      return this.topLeft;
   }

   @NotNull
   public final AoData getTopRight() {
      return this.topRight;
   }

   @NotNull
   public final AoData getBottomLeft() {
      return this.bottomLeft;
   }

   @NotNull
   public final AoData getBottomRight() {
      return this.bottomRight;
   }

   @NotNull
   public final List<AoData> getOrdered() {
      return this.ordered;
   }

   public final void update(@NotNull Int3 offset, boolean useBounds, float multiplier) {
      Intrinsics.checkParameterIsNotNull(offset, "offset");
      BlockContext ctx = RendererHolder.getBlockContext();
      IBlockState blockState = ctx.blockState(offset);
      float[] quadBounds = new float[12];
      BitSet $this$forEachIndexed$iv = new BitSet(3);
      int $i$f$forEachIndexed = 0;
      int index$iv = 0;
      int $i$a$-apply-AoFaceData$update$flags$1 = 0;
      $this$forEachIndexed$iv.set(0);
      this.ao.updateVertexBrightness(ctx.getWorld(), blockState, GeometryKt.plus(ctx.getPos(), offset), this.face, quadBounds, $this$forEachIndexed$iv);
      Iterable $this$forEachIndexed$iv = (Iterable)this.ordered;
      $i$f$forEachIndexed = 0;
      index$iv = 0;

      for(Object item$iv : $this$forEachIndexed$iv) {
         int var13 = index$iv++;
         boolean var14 = false;
         if (var13 < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         AoData aoData = (AoData)item$iv;
         int $i$a$-forEachIndexed-AoFaceData$update$1 = 0;
         aoData.set(this.ao.vertexBrightness[var13], this.ao.vertexColorMultiplier[var13] * multiplier);
      }

   }

   @NotNull
   public final AoData get(@NotNull EnumFacing dir1, @NotNull EnumFacing dir2) {
      Intrinsics.checkParameterIsNotNull(dir1, "dir1");
      Intrinsics.checkParameterIsNotNull(dir2, "dir2");
      boolean isTop = this.top == dir1 || this.top == dir2;
      boolean isLeft = this.left == dir1 || this.left == dir2;
      return isTop ? (isLeft ? this.topLeft : this.topRight) : (isLeft ? this.bottomLeft : this.bottomRight);
   }

   @NotNull
   public final EnumFacing getFace() {
      return this.face;
   }

   public AoFaceData(@NotNull EnumFacing face) {
      // $FF: Couldn't be decompiled
   }
}
