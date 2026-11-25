package mods.betterfoliage.client.render;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IntIterator;
import kotlin.jvm.JvmName;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function6;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntRange;
import mods.octarinecore.client.render.Model;
import mods.octarinecore.client.render.Quad;
import mods.octarinecore.client.render.RenderVertex;
import mods.octarinecore.client.render.Shader;
import mods.octarinecore.client.render.ShadingContext;
import mods.octarinecore.client.render.UV;
import mods.octarinecore.client.render.Vertex;
import mods.octarinecore.common.Double3;
import mods.octarinecore.common.GeometryKt;
import mods.octarinecore.common.Int3;
import mods.octarinecore.common.Rotation;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 2,
   d1 = {"\u0000v\n\u0000\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u000e\u0010+\u001a\u00020\u001b2\u0006\u0010,\u001a\u00020\u000e\u001a\n\u0010-\u001a\u00020%*\u00020)\u001a\u0012\u0010.\u001a\u00020%*\u00020)2\u0006\u0010/\u001a\u00020&\u001a.\u00100\u001a\u00020\u0011*\u0002012\u0006\u00102\u001a\u0002012\u0006\u00103\u001a\u0002012\u0012\u00104\u001a\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020%05\u001a\u0018\u00106\u001a\b\u0012\u0004\u0012\u00020\u000f07*\u00020\u000f2\u0006\u00108\u001a\u000209\u001a,\u00106\u001a\b\u0012\u0004\u0012\u00020\u000f07*\u00020\u000f2\u0006\u00108\u001a\u0002092\u0012\u0010:\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u000f05\"\u0019\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001¢\u0006\n\n\u0002\u0010\u0005\u001a\u0004\b\u0003\u0010\u0004\"\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\t\"D\u0010\n\u001a5\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u000bj\u0002`\u0012¢\u0006\u0002\b\u0013¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015\"\u0019\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001¢\u0006\n\n\u0002\u0010\u0005\u001a\u0004\b\u0017\u0010\u0004\"\u0019\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001¢\u0006\n\n\u0002\u0010\u0005\u001a\u0004\b\u0019\u0010\u0004\"\u0011\u0010\u001a\u001a\u00020\u001b¢\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001d\"\u0011\u0010\u001e\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\t\"\u0011\u0010 \u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b!\u0010\t\"D\u0010\"\u001a5\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u000bj\u0002`\u0012¢\u0006\u0002\b\u0013¢\u0006\b\n\u0000\u001a\u0004\b#\u0010\u0015\"\u0015\u0010$\u001a\u00020%*\u00020&8F¢\u0006\u0006\u001a\u0004\b$\u0010'\"\u0015\u0010(\u001a\u00020%*\u00020)8F¢\u0006\u0006\u001a\u0004\b(\u0010*¨\u0006;"},
   d2 = {"denseLeavesRot", "", "Lmods/octarinecore/common/Rotation;", "getDenseLeavesRot", "()[Lmods/octarinecore/common/Rotation;", "[Lmods/octarinecore/common/Rotation;", "down1", "Lmods/octarinecore/common/Int3;", "getDown1", "()Lmods/octarinecore/common/Int3;", "greywash", "Lkotlin/Function6;", "Lmods/octarinecore/client/render/RenderVertex;", "Lmods/octarinecore/client/render/ShadingContext;", "", "Lmods/octarinecore/client/render/Quad;", "Lmods/octarinecore/client/render/Vertex;", "", "Lmods/octarinecore/client/render/PostProcessLambda;", "Lkotlin/ExtensionFunctionType;", "getGreywash", "()Lkotlin/jvm/functions/Function6;", "normalLeavesRot", "getNormalLeavesRot", "rotationFromUp", "getRotationFromUp", "snowOffset", "Lmods/octarinecore/common/Double3;", "getSnowOffset", "()Lmods/octarinecore/common/Double3;", "up1", "getUp1", "up2", "getUp2", "whitewash", "getWhitewash", "isCutout", "", "Lnet/minecraft/util/BlockRenderLayer;", "(Lnet/minecraft/util/BlockRenderLayer;)Z", "isSnow", "Lnet/minecraft/block/state/IBlockState;", "(Lnet/minecraft/block/state/IBlockState;)Z", "xzDisk", "modelIdx", "canRenderInCutout", "canRenderInLayer", "layer", "mix", "Lmods/octarinecore/client/render/Model;", "first", "second", "predicate", "Lkotlin/Function1;", "toCross", "", "rotAxis", "Lnet/minecraft/util/EnumFacing;", "trans", "BetterFoliage-MC1.12"}
)
@JvmName(
   name = "Utils"
)
public final class Utils {
   @NotNull
   private static final Int3 up1 = new Int3(TuplesKt.to(Integer.valueOf(1), EnumFacing.UP));
   @NotNull
   private static final Int3 up2 = new Int3(TuplesKt.to(Integer.valueOf(2), EnumFacing.UP));
   @NotNull
   private static final Int3 down1 = new Int3(TuplesKt.to(Integer.valueOf(1), EnumFacing.DOWN));
   @NotNull
   private static final Double3 snowOffset = GeometryKt.times(EnumFacing.UP, 0.0625D);
   @NotNull
   private static final Rotation[] normalLeavesRot = new Rotation[]{Rotation.Companion.getIdentity()};
   @NotNull
   private static final Rotation[] denseLeavesRot = new Rotation[]{Rotation.Companion.getIdentity(), Rotation.Companion.getRot90()[EnumFacing.EAST.ordinal()], Rotation.Companion.getRot90()[EnumFacing.SOUTH.ordinal()]};
   @NotNull
   private static final Function6<RenderVertex, ShadingContext, Integer, Quad, Integer, Vertex, Unit> whitewash = (Function6)null.INSTANCE;
   @NotNull
   private static final Function6<RenderVertex, ShadingContext, Integer, Quad, Integer, Vertex, Unit> greywash = (Function6)null.INSTANCE;
   @NotNull
   private static final Rotation[] rotationFromUp = new Rotation[]{Rotation.Companion.getRot90()[EnumFacing.EAST.ordinal()].times(2), Rotation.Companion.getIdentity(), Rotation.Companion.getRot90()[EnumFacing.WEST.ordinal()], Rotation.Companion.getRot90()[EnumFacing.EAST.ordinal()], Rotation.Companion.getRot90()[EnumFacing.SOUTH.ordinal()], Rotation.Companion.getRot90()[EnumFacing.NORTH.ordinal()]};

   @NotNull
   public static final Int3 getUp1() {
      return up1;
   }

   @NotNull
   public static final Int3 getUp2() {
      return up2;
   }

   @NotNull
   public static final Int3 getDown1() {
      return down1;
   }

   @NotNull
   public static final Double3 getSnowOffset() {
      return snowOffset;
   }

   @NotNull
   public static final Rotation[] getNormalLeavesRot() {
      return normalLeavesRot;
   }

   @NotNull
   public static final Rotation[] getDenseLeavesRot() {
      return denseLeavesRot;
   }

   @NotNull
   public static final Function6<RenderVertex, ShadingContext, Integer, Quad, Integer, Vertex, Unit> getWhitewash() {
      return whitewash;
   }

   @NotNull
   public static final Function6<RenderVertex, ShadingContext, Integer, Quad, Integer, Vertex, Unit> getGreywash() {
      return greywash;
   }

   public static final boolean isSnow(@NotNull IBlockState $this$isSnow) {
      Intrinsics.checkParameterIsNotNull($this$isSnow, "$this$isSnow");
      Material var1 = $this$isSnow.getMaterial();
      boolean var2 = false;
      boolean var3 = false;
      int $i$a$-let-Utils$isSnow$1 = 0;
      return Intrinsics.areEqual(var1, Material.SNOW) || Intrinsics.areEqual(var1, Material.CRAFTED_SNOW);
   }

   @NotNull
   public static final List<Quad> toCross(@NotNull Quad $this$toCross, @NotNull EnumFacing rotAxis, @NotNull Function1<? super Quad, Quad> trans) {
      Intrinsics.checkParameterIsNotNull($this$toCross, "$this$toCross");
      Intrinsics.checkParameterIsNotNull(rotAxis, "rotAxis");
      Intrinsics.checkParameterIsNotNull(trans, "trans");
      byte $this$map$iv = 0;
      Iterable $this$map$iv = (Iterable)(new IntRange($this$map$iv, 3));
      int $i$f$map = 0;
      Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));
      int $i$f$mapTo = 0;
      Iterator var8 = $this$map$iv.iterator();

      while(var8.hasNext()) {
         int item$iv$iv = ((IntIterator)var8).nextInt();
         int $i$a$-map-Utils$toCross$1 = 0;
         Quad var13 = (Quad)trans.invoke($this$toCross.rotate(Rotation.Companion.getRot90()[rotAxis.ordinal()].times(item$iv$iv)).mirrorUV(item$iv$iv > 1, false));
         destination$iv$iv.add(var13);
      }

      return (List)destination$iv$iv;
   }

   @NotNull
   public static final List<Quad> toCross(@NotNull Quad $this$toCross, @NotNull EnumFacing rotAxis) {
      Intrinsics.checkParameterIsNotNull($this$toCross, "$this$toCross");
      Intrinsics.checkParameterIsNotNull(rotAxis, "rotAxis");
      return toCross($this$toCross, rotAxis, null.INSTANCE);
   }

   @NotNull
   public static final Double3 xzDisk(int modelIdx) {
      double var1 = 6.283185307179586D * (double)modelIdx / 64.0D;
      boolean var3 = false;
      boolean var4 = false;
      int $i$a$-let-Utils$xzDisk$1 = 0;
      return new Double3(Math.cos(var1), 0.0D, Math.sin(var1));
   }

   @NotNull
   public static final Rotation[] getRotationFromUp() {
      return rotationFromUp;
   }

   public static final void mix(@NotNull Model $this$mix, @NotNull Model first, @NotNull Model second, @NotNull Function1<? super Integer, Boolean> predicate) {
      Intrinsics.checkParameterIsNotNull($this$mix, "$this$mix");
      Intrinsics.checkParameterIsNotNull(first, "first");
      Intrinsics.checkParameterIsNotNull(second, "second");
      Intrinsics.checkParameterIsNotNull(predicate, "predicate");
      Iterable $this$forEachIndexed$iv = (Iterable)first.getQuads();
      int $i$f$forEachIndexed = 0;
      int index$iv = 0;

      for(Object item$iv : $this$forEachIndexed$iv) {
         int var9 = index$iv++;
         boolean var10 = false;
         if (var9 < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         Quad quad = (Quad)item$iv;
         int $i$a$-forEachIndexed-Utils$mix$1 = 0;
         Quad otherQuad = (Quad)second.getQuads().get(var9);
         $this$mix.add(new Quad(((Boolean)predicate.invoke(Integer.valueOf(0))).booleanValue() ? Vertex.copy$default(otherQuad.getV1(), (Double3)null, (UV)null, (Shader)null, (Shader)null, 15, (Object)null) : Vertex.copy$default(quad.getV1(), (Double3)null, (UV)null, (Shader)null, (Shader)null, 15, (Object)null), ((Boolean)predicate.invoke(Integer.valueOf(1))).booleanValue() ? Vertex.copy$default(otherQuad.getV2(), (Double3)null, (UV)null, (Shader)null, (Shader)null, 15, (Object)null) : Vertex.copy$default(quad.getV2(), (Double3)null, (UV)null, (Shader)null, (Shader)null, 15, (Object)null), ((Boolean)predicate.invoke(Integer.valueOf(2))).booleanValue() ? Vertex.copy$default(otherQuad.getV3(), (Double3)null, (UV)null, (Shader)null, (Shader)null, 15, (Object)null) : Vertex.copy$default(quad.getV3(), (Double3)null, (UV)null, (Shader)null, (Shader)null, 15, (Object)null), ((Boolean)predicate.invoke(Integer.valueOf(3))).booleanValue() ? Vertex.copy$default(otherQuad.getV4(), (Double3)null, (UV)null, (Shader)null, (Shader)null, 15, (Object)null) : Vertex.copy$default(quad.getV4(), (Double3)null, (UV)null, (Shader)null, (Shader)null, 15, (Object)null)));
      }

   }

   public static final boolean isCutout(@NotNull BlockRenderLayer $this$isCutout) {
      Intrinsics.checkParameterIsNotNull($this$isCutout, "$this$isCutout");
      return $this$isCutout == BlockRenderLayer.CUTOUT || $this$isCutout == BlockRenderLayer.CUTOUT_MIPPED;
   }

   public static final boolean canRenderInLayer(@NotNull IBlockState $this$canRenderInLayer, @NotNull BlockRenderLayer layer) {
      Intrinsics.checkParameterIsNotNull($this$canRenderInLayer, "$this$canRenderInLayer");
      Intrinsics.checkParameterIsNotNull(layer, "layer");
      return $this$canRenderInLayer.getBlock().canRenderInLayer($this$canRenderInLayer, layer);
   }

   public static final boolean canRenderInCutout(@NotNull IBlockState $this$canRenderInCutout) {
      Intrinsics.checkParameterIsNotNull($this$canRenderInCutout, "$this$canRenderInCutout");
      return $this$canRenderInCutout.getBlock().canRenderInLayer($this$canRenderInCutout, BlockRenderLayer.CUTOUT) || $this$canRenderInCutout.getBlock().canRenderInLayer($this$canRenderInCutout, BlockRenderLayer.CUTOUT_MIPPED);
   }
}
