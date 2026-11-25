package mods.betterfoliage.client.render;

import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmName;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import mods.betterfoliage.client.config.Config;
import mods.octarinecore.client.render.CornerSingleFallback;
import mods.octarinecore.client.render.EdgeInterpolateFallback;
import mods.octarinecore.client.render.FaceCenter;
import mods.octarinecore.client.render.FaceFlat;
import mods.octarinecore.client.render.Model;
import mods.octarinecore.client.render.Quad;
import mods.octarinecore.client.render.Shader;
import mods.octarinecore.client.render.ShadersKt;
import mods.octarinecore.client.render.ShadingKt;
import mods.octarinecore.client.render.UV;
import mods.octarinecore.client.render.Vertex;
import mods.octarinecore.common.Double3;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 2,
   d1 = {"\u0000.\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\b\b\u001a\u001a\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\b0\u00072\u0006\u0010\t\u001a\u00020\n\u001a\u001a\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\b0\u00072\u0006\u0010\t\u001a\u00020\n\u001a(\u0010\f\u001a\u00020\r*\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\n2\u0014\b\u0002\u0010\u0010\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\b0\u0007\u001a \u0010\u0011\u001a\u00020\r*\u00020\u000e2\u0014\b\u0002\u0010\u0010\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\b0\u0007\u001a8\u0010\u0012\u001a\u00020\r*\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\n2\u0006\u0010\u0013\u001a\u00020\n2\u0006\u0010\u0014\u001a\u00020\n2\u0014\b\u0002\u0010\u0010\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\b0\u0007\u001a0\u0010\u0015\u001a\u00020\r*\u00020\u000e2\u0006\u0010\u0013\u001a\u00020\n2\u0006\u0010\u0014\u001a\u00020\n2\u0014\b\u0002\u0010\u0010\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\b0\u0007\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0086T\u00a2\u0006\u0002\n\u0000\"\u0011\u0010\u0002\u001a\u00020\u00038F\u00a2\u0006\u0006\u001a\u0004\b\u0004\u0010\u0005\u00a8\u0006\u0016"},
   d2 = {"chamferAffinity", "", "zProtectionScale", "Lmods/octarinecore/common/Double3;", "getZProtectionScale", "()Lmods/octarinecore/common/Double3;", "bottomExtension", "Lkotlin/Function1;", "Lmods/octarinecore/client/render/Quad;", "size", "", "topExtension", "columnLid", "", "Lmods/octarinecore/client/render/Model;", "radius", "transform", "columnLidSquare", "columnSide", "yBottom", "yTop", "columnSideSquare", "BetterFoliage-MC1.12"}
)
@JvmName(
   name = "ModelColumn"
)
public final class ModelColumn {
   public static final float chamferAffinity = 0.9F;

   @NotNull
   public static final Double3 getZProtectionScale() {
      return new Double3(Config.roundLogs.INSTANCE.getZProtection(), 1.0D, Config.roundLogs.INSTANCE.getZProtection());
   }

   public static final void columnSide(@NotNull Model $this$columnSide, double radius, double yBottom, double yTop, @NotNull Function1<? super Quad, Quad> transform) {
      Intrinsics.checkParameterIsNotNull($this$columnSide, "$this$columnSide");
      Intrinsics.checkParameterIsNotNull(transform, "transform");
      double halfRadius = radius * 0.5D;
      Iterable $this$forEach$iv = (Iterable)CollectionsKt.listOf(new Quad[]{Quad.setAoShader$default(Quad.clampUV$default($this$columnSide.verticalRectangle(0.0D, 0.5D, 0.5D - radius, 0.5D, yBottom, yTop), 0.0D, 0.5D - radius, 0.0D, 0.0D, 12, (Object)null), ShadingKt.faceOrientedInterpolate(EnumFacing.SOUTH), (Function2)null, 2, (Object)null).setAoShader(ShadingKt.faceOrientedAuto$default((EnumFacing)null, ShadersKt.cornerAo(Axis.Y), (Function2)null, 5, (Object)null), null.INSTANCE), Quad.setAoShader$default(Quad.clampUV$default($this$columnSide.verticalRectangle(0.5D - radius, 0.5D, 0.5D - halfRadius, 0.5D - halfRadius, yBottom, yTop), 0.5D - radius, 0.0D, 0.0D, 0.0D, 14, (Object)null), ShadingKt.faceOrientedAuto$default(EnumFacing.SOUTH, ShadersKt.cornerInterpolate(Axis.Y, 0.9F, Config.roundLogs.INSTANCE.getDimming()), (Function2)null, 4, (Object)null), (Function2)null, 2, (Object)null).setAoShader(ShadingKt.faceOrientedAuto$default(EnumFacing.SOUTH, ShadersKt.cornerInterpolate(Axis.Y, 0.5F, Config.roundLogs.INSTANCE.getDimming()), (Function2)null, 4, (Object)null), null.INSTANCE)});
      int $i$f$forEach = 0;

      for(Object element$iv : $this$forEach$iv) {
         Quad it = (Quad)element$iv;
         int $i$a$-forEach-ModelColumn$columnSide$4 = 0;
         $this$columnSide.add((Quad)transform.invoke(it.setFlatShader(new FaceFlat(EnumFacing.SOUTH))));
      }

      $this$forEach$iv = (Iterable)CollectionsKt.listOf(new Quad[]{Quad.setAoShader$default(Quad.clampUV$default($this$columnSide.verticalRectangle(0.5D - halfRadius, 0.5D - halfRadius, 0.5D, 0.5D - radius, yBottom, yTop), 0.0D, radius - 0.5D, 0.0D, 0.0D, 13, (Object)null), ShadingKt.faceOrientedAuto$default(EnumFacing.EAST, ShadersKt.cornerInterpolate(Axis.Y, 0.9F, Config.roundLogs.INSTANCE.getDimming()), (Function2)null, 4, (Object)null), (Function2)null, 2, (Object)null).setAoShader(ShadingKt.faceOrientedAuto$default(EnumFacing.EAST, ShadersKt.cornerInterpolate(Axis.Y, 0.5F, Config.roundLogs.INSTANCE.getDimming()), (Function2)null, 4, (Object)null), null.INSTANCE), Quad.setAoShader$default(Quad.clampUV$default($this$columnSide.verticalRectangle(0.5D, 0.5D - radius, 0.5D, 0.0D, yBottom, yTop), radius - 0.5D, 0.0D, 0.0D, 0.0D, 12, (Object)null), ShadingKt.faceOrientedInterpolate(EnumFacing.EAST), (Function2)null, 2, (Object)null).setAoShader(ShadingKt.faceOrientedAuto$default((EnumFacing)null, ShadersKt.cornerAo(Axis.Y), (Function2)null, 5, (Object)null), null.INSTANCE)});
      $i$f$forEach = 0;

      for(Object element$iv : $this$forEach$iv) {
         Quad it = (Quad)element$iv;
         int $i$a$-forEach-ModelColumn$columnSide$7 = 0;
         $this$columnSide.add((Quad)transform.invoke(it.setFlatShader(new FaceFlat(EnumFacing.EAST))));
      }

      List var17 = $this$columnSide.getQuads();
      $i$f$forEach = 1;
      int idx2$iv = 2;
      int $i$f$exchange = 0;
      Object e$iv = var17.get($i$f$forEach);
      var17.set($i$f$forEach, var17.get(idx2$iv));
      var17.set(idx2$iv, e$iv);
   }

   public static final void columnSideSquare(@NotNull Model $this$columnSideSquare, double yBottom, double yTop, @NotNull Function1<? super Quad, Quad> transform) {
      Intrinsics.checkParameterIsNotNull($this$columnSideSquare, "$this$columnSideSquare");
      Intrinsics.checkParameterIsNotNull(transform, "transform");
      Iterable $this$forEach$iv = (Iterable)CollectionsKt.listOf(new Quad[]{Quad.setAoShader$default(Quad.clampUV$default($this$columnSideSquare.verticalRectangle(0.0D, 0.5D, 0.5D, 0.5D, yBottom, yTop), 0.0D, 0.0D, 0.0D, 0.0D, 14, (Object)null), ShadingKt.faceOrientedInterpolate(EnumFacing.SOUTH), (Function2)null, 2, (Object)null).setAoShader(ShadingKt.faceOrientedAuto$default((EnumFacing)null, ShadersKt.cornerAo(Axis.Y), (Function2)null, 5, (Object)null), null.INSTANCE), Quad.setAoShader$default(Quad.clampUV$default($this$columnSideSquare.verticalRectangle(0.5D, 0.5D, 0.5D, 0.0D, yBottom, yTop), 0.0D, 0.0D, 0.0D, 0.0D, 13, (Object)null), ShadingKt.faceOrientedInterpolate(EnumFacing.EAST), (Function2)null, 2, (Object)null).setAoShader(ShadingKt.faceOrientedAuto$default((EnumFacing)null, ShadersKt.cornerAo(Axis.Y), (Function2)null, 5, (Object)null), null.INSTANCE)});
      int $i$f$forEach = 0;

      for(Object element$iv : $this$forEach$iv) {
         Quad it = (Quad)element$iv;
         int $i$a$-forEach-ModelColumn$columnSideSquare$4 = 0;
         $this$columnSideSquare.add((Quad)transform.invoke(Quad.setFlatShader$default(it, ShadingKt.faceOrientedAuto$default((EnumFacing)null, ShadersKt.getCornerFlat(), (Function2)null, 5, (Object)null), (Function2)null, 2, (Object)null)));
      }

   }

   public static final void columnLid(@NotNull Model $this$columnLid, double radius, @NotNull Function1<? super Quad, Quad> transform) {
      Intrinsics.checkParameterIsNotNull($this$columnLid, "$this$columnLid");
      Intrinsics.checkParameterIsNotNull(transform, "transform");
      Vertex v1 = new Vertex(new Double3(0.0D, 0.5D, 0.0D), new UV(0.0D, 0.0D), (Shader)null, (Shader)null, 12, (DefaultConstructorMarker)null);
      Vertex v2 = new Vertex(new Double3(0.0D, 0.5D, 0.5D), new UV(0.0D, 0.5D), (Shader)null, (Shader)null, 12, (DefaultConstructorMarker)null);
      Vertex v3 = new Vertex(new Double3(0.5D - radius, 0.5D, 0.5D), new UV(0.5D - radius, 0.5D), (Shader)null, (Shader)null, 12, (DefaultConstructorMarker)null);
      Vertex v4 = new Vertex(new Double3(0.5D - radius * 0.5D, 0.5D, 0.5D - radius * 0.5D), new UV(0.5D, 0.5D), (Shader)null, (Shader)null, 12, (DefaultConstructorMarker)null);
      Vertex v5 = new Vertex(new Double3(0.5D, 0.5D, 0.5D - radius), new UV(0.5D, 0.5D - radius), (Shader)null, (Shader)null, 12, (DefaultConstructorMarker)null);
      Vertex v6 = new Vertex(new Double3(0.5D, 0.5D, 0.0D), new UV(0.5D, 0.0D), (Shader)null, (Shader)null, 12, (DefaultConstructorMarker)null);
      Quad this_$iv = Quad.setAoShader$default(new Quad(v1, v2, v3, v4), ShadingKt.faceOrientedAuto$default(EnumFacing.UP, ShadersKt.cornerAo(Axis.Y), (Function2)null, 4, (Object)null), (Function2)null, 2, (Object)null);
      int $i$f$transformVI = 0;
      Vertex var10000 = this_$iv.getV1();
      int $i$f$forEach = 0;
      Vertex vertex = var10000;
      int $i$a$-transformVI-ModelColumn$columnLid$q1$1 = 0;
      Shader var10003;
      switch($i$f$forEach) {
      case 0:
         var10003 = new FaceCenter(EnumFacing.UP);
         break;
      case 1:
         var10003 = new EdgeInterpolateFallback(EnumFacing.UP, EnumFacing.SOUTH, 0.0D, 0.0F, 8, (DefaultConstructorMarker)null);
         break;
      default:
         var10003 = vertex.getAoShader();
      }

      Vertex var20 = Vertex.copy$default(vertex, (Double3)null, (UV)null, var10003, (Shader)null, 11, (Object)null);
      Vertex var10001 = this_$iv.getV2();
      $i$f$forEach = (byte)1;
      vertex = var10001;
      $i$a$-transformVI-ModelColumn$columnLid$q1$1 = 0;
      switch($i$f$forEach) {
      case 0:
         var10003 = new FaceCenter(EnumFacing.UP);
         break;
      case 1:
         var10003 = new EdgeInterpolateFallback(EnumFacing.UP, EnumFacing.SOUTH, 0.0D, 0.0F, 8, (DefaultConstructorMarker)null);
         break;
      default:
         var10003 = vertex.getAoShader();
      }

      Vertex var21 = Vertex.copy$default(vertex, (Double3)null, (UV)null, var10003, (Shader)null, 11, (Object)null);
      Vertex var10002 = this_$iv.getV3();
      $i$f$forEach = (byte)2;
      vertex = var10002;
      $i$a$-transformVI-ModelColumn$columnLid$q1$1 = 0;
      switch($i$f$forEach) {
      case 0:
         var10003 = new FaceCenter(EnumFacing.UP);
         break;
      case 1:
         var10003 = new EdgeInterpolateFallback(EnumFacing.UP, EnumFacing.SOUTH, 0.0D, 0.0F, 8, (DefaultConstructorMarker)null);
         break;
      default:
         var10003 = vertex.getAoShader();
      }

      Vertex var22 = Vertex.copy$default(vertex, (Double3)null, (UV)null, var10003, (Shader)null, 11, (Object)null);
      Vertex var69 = this_$iv.getV4();
      $i$f$forEach = (byte)3;
      vertex = var69;
      $i$a$-transformVI-ModelColumn$columnLid$q1$1 = 0;
      Shader var70;
      switch($i$f$forEach) {
      case 0:
         var70 = new FaceCenter(EnumFacing.UP);
         break;
      case 1:
         var70 = new EdgeInterpolateFallback(EnumFacing.UP, EnumFacing.SOUTH, 0.0D, 0.0F, 8, (DefaultConstructorMarker)null);
         break;
      default:
         var70 = vertex.getAoShader();
      }

      Vertex var23 = Vertex.copy$default(vertex, (Double3)null, (UV)null, var70, (Shader)null, 11, (Object)null);
      Quad q1 = (new Quad(var20, var21, var22, var23)).cycleVertices(Config.INSTANCE.getNVidia() ? 0 : 1);
      Quad this_$iv = Quad.setAoShader$default(new Quad(v1, v4, v5, v6), ShadingKt.faceOrientedAuto$default(EnumFacing.UP, ShadersKt.cornerAo(Axis.Y), (Function2)null, 4, (Object)null), (Function2)null, 2, (Object)null);
      $i$f$transformVI = 0;
      var10000 = this_$iv.getV1();
      byte var43 = 0;
      Vertex vertex = var10000;
      int $i$a$-transformVI-ModelColumn$columnLid$q2$1 = 0;
      switch(var43) {
      case 0:
         var70 = new FaceCenter(EnumFacing.UP);
         break;
      case 1:
      case 2:
      default:
         var70 = vertex.getAoShader();
         break;
      case 3:
         var70 = new EdgeInterpolateFallback(EnumFacing.UP, EnumFacing.EAST, 0.0D, 0.0F, 8, (DefaultConstructorMarker)null);
      }

      var20 = Vertex.copy$default(vertex, (Double3)null, (UV)null, var70, (Shader)null, 11, (Object)null);
      var10001 = this_$iv.getV2();
      var43 = 1;
      vertex = var10001;
      $i$a$-transformVI-ModelColumn$columnLid$q2$1 = 0;
      switch(var43) {
      case 0:
         var70 = new FaceCenter(EnumFacing.UP);
         break;
      case 1:
      case 2:
      default:
         var70 = vertex.getAoShader();
         break;
      case 3:
         var70 = new EdgeInterpolateFallback(EnumFacing.UP, EnumFacing.EAST, 0.0D, 0.0F, 8, (DefaultConstructorMarker)null);
      }

      var21 = Vertex.copy$default(vertex, (Double3)null, (UV)null, var70, (Shader)null, 11, (Object)null);
      var10002 = this_$iv.getV3();
      var43 = 2;
      vertex = var10002;
      $i$a$-transformVI-ModelColumn$columnLid$q2$1 = 0;
      switch(var43) {
      case 0:
         var70 = new FaceCenter(EnumFacing.UP);
         break;
      case 1:
      case 2:
      default:
         var70 = vertex.getAoShader();
         break;
      case 3:
         var70 = new EdgeInterpolateFallback(EnumFacing.UP, EnumFacing.EAST, 0.0D, 0.0F, 8, (DefaultConstructorMarker)null);
      }

      var22 = Vertex.copy$default(vertex, (Double3)null, (UV)null, var70, (Shader)null, 11, (Object)null);
      Vertex var74 = this_$iv.getV4();
      var43 = 3;
      vertex = var74;
      $i$a$-transformVI-ModelColumn$columnLid$q2$1 = 0;
      Shader var75;
      switch(var43) {
      case 0:
         var75 = new FaceCenter(EnumFacing.UP);
         break;
      case 1:
      case 2:
      default:
         var75 = vertex.getAoShader();
         break;
      case 3:
         var75 = new EdgeInterpolateFallback(EnumFacing.UP, EnumFacing.EAST, 0.0D, 0.0F, 8, (DefaultConstructorMarker)null);
      }

      var23 = Vertex.copy$default(vertex, (Double3)null, (UV)null, var75, (Shader)null, 11, (Object)null);
      this_$iv = (new Quad(var20, var21, var22, var23)).cycleVertices(Config.INSTANCE.getNVidia() ? 0 : 1);
      Iterable $this$forEach$iv = (Iterable)CollectionsKt.listOf(new Quad[]{q1, this_$iv});
      $i$f$forEach = 0;

      for(Object element$iv : $this$forEach$iv) {
         Quad it = (Quad)element$iv;
         int $i$a$-forEach-ModelColumn$columnLid$2 = 0;
         $this$columnLid.add((Quad)transform.invoke(it.setFlatShader(new FaceFlat(EnumFacing.UP))));
      }

   }

   public static final void columnLidSquare(@NotNull Model $this$columnLidSquare, @NotNull Function1<? super Quad, Quad> transform) {
      Intrinsics.checkParameterIsNotNull($this$columnLidSquare, "$this$columnLidSquare");
      Intrinsics.checkParameterIsNotNull(transform, "transform");
      double this_$iv = 0.5D;
      double idx = 0.5D;
      double var6 = 0.0D;
      double var8 = 0.5D;
      Quad var22 = $this$columnLidSquare.horizontalRectangle(0.0D, var6, var8, idx, this_$iv);
      int $i$f$transformVI = 0;
      Vertex var10000 = var22.getV1();
      byte var23 = 0;
      Vertex vertex = var10000;
      int $i$a$-transformVI-ModelColumn$columnLidSquare$2 = 0;
      UV var10002 = new UV(vertex.getXyz().getX(), vertex.getXyz().getZ());
      Shader var10003;
      switch(var23) {
      case 0:
         var10003 = new FaceCenter(EnumFacing.UP);
         break;
      case 1:
         var10003 = new EdgeInterpolateFallback(EnumFacing.UP, EnumFacing.SOUTH, 0.0D, 0.0F, 8, (DefaultConstructorMarker)null);
         break;
      case 2:
         var10003 = new CornerSingleFallback(EnumFacing.UP, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.UP, 0.0F, 16, (DefaultConstructorMarker)null);
         break;
      default:
         var10003 = new EdgeInterpolateFallback(EnumFacing.UP, EnumFacing.EAST, 0.0D, 0.0F, 8, (DefaultConstructorMarker)null);
      }

      Vertex var14 = Vertex.copy$default(vertex, (Double3)null, var10002, var10003, (Shader)null, 9, (Object)null);
      Vertex var10001 = var22.getV2();
      var23 = 1;
      vertex = var10001;
      $i$a$-transformVI-ModelColumn$columnLidSquare$2 = 0;
      var10002 = new UV(vertex.getXyz().getX(), vertex.getXyz().getZ());
      switch(var23) {
      case 0:
         var10003 = new FaceCenter(EnumFacing.UP);
         break;
      case 1:
         var10003 = new EdgeInterpolateFallback(EnumFacing.UP, EnumFacing.SOUTH, 0.0D, 0.0F, 8, (DefaultConstructorMarker)null);
         break;
      case 2:
         var10003 = new CornerSingleFallback(EnumFacing.UP, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.UP, 0.0F, 16, (DefaultConstructorMarker)null);
         break;
      default:
         var10003 = new EdgeInterpolateFallback(EnumFacing.UP, EnumFacing.EAST, 0.0D, 0.0F, 8, (DefaultConstructorMarker)null);
      }

      Vertex var15 = Vertex.copy$default(vertex, (Double3)null, var10002, var10003, (Shader)null, 9, (Object)null);
      Vertex var35 = var22.getV3();
      var23 = 2;
      vertex = var35;
      $i$a$-transformVI-ModelColumn$columnLidSquare$2 = 0;
      UV var36 = new UV(vertex.getXyz().getX(), vertex.getXyz().getZ());
      switch(var23) {
      case 0:
         var10003 = new FaceCenter(EnumFacing.UP);
         break;
      case 1:
         var10003 = new EdgeInterpolateFallback(EnumFacing.UP, EnumFacing.SOUTH, 0.0D, 0.0F, 8, (DefaultConstructorMarker)null);
         break;
      case 2:
         var10003 = new CornerSingleFallback(EnumFacing.UP, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.UP, 0.0F, 16, (DefaultConstructorMarker)null);
         break;
      default:
         var10003 = new EdgeInterpolateFallback(EnumFacing.UP, EnumFacing.EAST, 0.0D, 0.0F, 8, (DefaultConstructorMarker)null);
      }

      Vertex var16 = Vertex.copy$default(vertex, (Double3)null, var36, var10003, (Shader)null, 9, (Object)null);
      Vertex var40 = var22.getV4();
      var23 = 3;
      vertex = var40;
      $i$a$-transformVI-ModelColumn$columnLidSquare$2 = 0;
      var36 = new UV(vertex.getXyz().getX(), vertex.getXyz().getZ());
      Shader var41;
      switch(var23) {
      case 0:
         var41 = new FaceCenter(EnumFacing.UP);
         break;
      case 1:
         var41 = new EdgeInterpolateFallback(EnumFacing.UP, EnumFacing.SOUTH, 0.0D, 0.0F, 8, (DefaultConstructorMarker)null);
         break;
      case 2:
         var41 = new CornerSingleFallback(EnumFacing.UP, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.UP, 0.0F, 16, (DefaultConstructorMarker)null);
         break;
      default:
         var41 = new EdgeInterpolateFallback(EnumFacing.UP, EnumFacing.EAST, 0.0D, 0.0F, 8, (DefaultConstructorMarker)null);
      }

      Vertex var17 = Vertex.copy$default(vertex, (Double3)null, var36, var41, (Shader)null, 9, (Object)null);
      Quad var12 = new Quad(var14, var15, var16, var17);
      $this$columnLidSquare.add((Quad)transform.invoke(var12.setFlatShader(new FaceFlat(EnumFacing.UP))));
   }

   @NotNull
   public static final Function1<Quad, Quad> topExtension(final double size) {
      return (Function1)(new Function1<Quad, Quad>() {
         @NotNull
         public final Quad invoke(@NotNull Quad q) {
            Intrinsics.checkParameterIsNotNull(q, "q");
            Quad this_$iv = Quad.clampUV$default(q, 0.0D, 0.0D, 0.5D - size, 0.0D, 11, (Object)null);
            int $i$f$transformVI = 0;
            Vertex var10000 = this_$iv.getV1();
            byte idx = 0;
            Vertex vertex = var10000;
            int $i$a$-transformVI-ModelColumn$topExtension$1$1 = 0;
            Vertex var9 = idx < 2 ? vertex : Vertex.copy$default(vertex, vertex.getXyz().times(ModelColumn.getZProtectionScale()), (UV)null, (Shader)null, (Shader)null, 14, (Object)null);
            Vertex var10001 = this_$iv.getV2();
            idx = 1;
            vertex = var10001;
            $i$a$-transformVI-ModelColumn$topExtension$1$1 = 0;
            Vertex var10 = idx < 2 ? vertex : Vertex.copy$default(vertex, vertex.getXyz().times(ModelColumn.getZProtectionScale()), (UV)null, (Shader)null, (Shader)null, 14, (Object)null);
            Vertex var10002 = this_$iv.getV3();
            idx = 2;
            vertex = var10002;
            $i$a$-transformVI-ModelColumn$topExtension$1$1 = 0;
            Vertex var11 = idx < 2 ? vertex : Vertex.copy$default(vertex, vertex.getXyz().times(ModelColumn.getZProtectionScale()), (UV)null, (Shader)null, (Shader)null, 14, (Object)null);
            Vertex var10003 = this_$iv.getV4();
            idx = 3;
            vertex = var10003;
            $i$a$-transformVI-ModelColumn$topExtension$1$1 = 0;
            Vertex var12 = idx < 2 ? vertex : Vertex.copy$default(vertex, vertex.getXyz().times(ModelColumn.getZProtectionScale()), (UV)null, (Shader)null, (Shader)null, 14, (Object)null);
            return new Quad(var9, var10, var11, var12);
         }
      });
   }

   @NotNull
   public static final Function1<Quad, Quad> bottomExtension(final double size) {
      return (Function1)(new Function1<Quad, Quad>() {
         @NotNull
         public final Quad invoke(@NotNull Quad q) {
            Intrinsics.checkParameterIsNotNull(q, "q");
            Quad this_$iv = Quad.clampUV$default(q, 0.0D, 0.0D, 0.0D, -0.5D + size, 7, (Object)null);
            int $i$f$transformVI = 0;
            Vertex var10000 = this_$iv.getV1();
            byte idx = 0;
            Vertex vertex = var10000;
            int $i$a$-transformVI-ModelColumn$bottomExtension$1$1 = 0;
            Vertex var9 = idx > 1 ? vertex : Vertex.copy$default(vertex, vertex.getXyz().times(ModelColumn.getZProtectionScale()), (UV)null, (Shader)null, (Shader)null, 14, (Object)null);
            Vertex var10001 = this_$iv.getV2();
            idx = 1;
            vertex = var10001;
            $i$a$-transformVI-ModelColumn$bottomExtension$1$1 = 0;
            Vertex var10 = idx > 1 ? vertex : Vertex.copy$default(vertex, vertex.getXyz().times(ModelColumn.getZProtectionScale()), (UV)null, (Shader)null, (Shader)null, 14, (Object)null);
            Vertex var10002 = this_$iv.getV3();
            idx = 2;
            vertex = var10002;
            $i$a$-transformVI-ModelColumn$bottomExtension$1$1 = 0;
            Vertex var11 = idx > 1 ? vertex : Vertex.copy$default(vertex, vertex.getXyz().times(ModelColumn.getZProtectionScale()), (UV)null, (Shader)null, (Shader)null, 14, (Object)null);
            Vertex var10003 = this_$iv.getV4();
            idx = 3;
            vertex = var10003;
            $i$a$-transformVI-ModelColumn$bottomExtension$1$1 = 0;
            Vertex var12 = idx > 1 ? vertex : Vertex.copy$default(vertex, vertex.getXyz().times(ModelColumn.getZProtectionScale()), (UV)null, (Shader)null, (Shader)null, 14, (Object)null);
            return new Quad(var9, var10, var11, var12);
         }
      });
   }
}
