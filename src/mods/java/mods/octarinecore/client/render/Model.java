package mods.octarinecore.client.render;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import mods.octarinecore.common.Double3;
import mods.octarinecore.common.FaceCorners;
import mods.octarinecore.common.GeometryKt;
import net.minecraft.util.EnumFacing;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010!\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u001c\n\u0000\u0018\u00002\u00020\u0001B\u0015\b\u0016\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\u0002\u0010\u0005B\u0005¢\u0006\u0002\u0010\u0006J\u000e\u0010\u000b\u001a\u00020\u00042\u0006\u0010\f\u001a\u00020\rJ.\u0010\u000e\u001a\u00020\u00042\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00102\u0006\u0010\u0012\u001a\u00020\u00102\u0006\u0010\u0013\u001a\u00020\u00102\u0006\u0010\u0014\u001a\u00020\u0010J\u001a\u0010\u0015\u001a\u00020\u00162\u0012\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040\u0018J\u001a\u0010\u0019\u001a\u00020\u00162\u0012\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u00020\u001a\u0012\u0004\u0012\u00020\u001a0\u0018J6\u0010\u001b\u001a\u00020\u00042\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00102\u0006\u0010\u0012\u001a\u00020\u00102\u0006\u0010\u0013\u001a\u00020\u00102\u0006\u0010\u001c\u001a\u00020\u00102\u0006\u0010\u001d\u001a\u00020\u0010J\n\u0010\u001e\u001a\u00020\u001f*\u00020\u0004J\u0010\u0010 \u001a\u00020\u0016*\b\u0012\u0004\u0012\u00020\u00040!R\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00040\b¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n¨\u0006\""},
   d2 = {"Lmods/octarinecore/client/render/Model;", "", "other", "", "Lmods/octarinecore/client/render/Quad;", "(Ljava/util/List;)V", "()V", "quads", "", "getQuads", "()Ljava/util/List;", "faceQuad", "face", "Lnet/minecraft/util/EnumFacing;", "horizontalRectangle", "x1", "", "z1", "x2", "z2", "y", "transformQ", "", "trans", "Lkotlin/Function1;", "transformV", "Lmods/octarinecore/client/render/Vertex;", "verticalRectangle", "yBottom", "yTop", "add", "", "addAll", "", "BetterFoliage-MC1.12"}
)
public final class Model {
   @NotNull
   private final List<Quad> quads;

   @NotNull
   public final List<Quad> getQuads() {
      return this.quads;
   }

   public final boolean add(@NotNull Quad $this$add) {
      Intrinsics.checkParameterIsNotNull($this$add, "$this$add");
      return this.quads.add($this$add);
   }

   public final void addAll(@NotNull Iterable<Quad> $this$addAll) {
      Intrinsics.checkParameterIsNotNull($this$addAll, "$this$addAll");
      int $i$f$forEach = 0;

      for(Object element$iv : $this$addAll) {
         Quad it = (Quad)element$iv;
         int $i$a$-forEach-Model$addAll$1 = 0;
         this.quads.add(it);
      }

   }

   public final void transformQ(@NotNull Function1<? super Quad, Quad> trans) {
      Intrinsics.checkParameterIsNotNull(trans, "trans");
      List $this$replace$iv = this.quads;
      int $i$f$replace = 0;
      Iterable $this$forEachIndexed$iv$iv = (Iterable)$this$replace$iv;
      int $i$f$forEachIndexed = 0;
      int index$iv$iv = 0;

      for(Object item$iv$iv : $this$forEachIndexed$iv$iv) {
         int var9 = index$iv$iv++;
         boolean var10 = false;
         if (var9 < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         int $i$a$-forEachIndexed-Utils$replace$1 = 0;
         $this$replace$iv.set(var9, trans.invoke(item$iv$iv));
      }

   }

   public final void transformV(@NotNull Function1<? super Vertex, Vertex> trans) {
      Intrinsics.checkParameterIsNotNull(trans, "trans");
      List $this$replace$iv = this.quads;
      int $i$f$replace = 0;
      Iterable $this$forEachIndexed$iv$iv = (Iterable)$this$replace$iv;
      int $i$f$forEachIndexed = 0;
      int index$iv$iv = 0;

      for(Object item$iv$iv : $this$forEachIndexed$iv$iv) {
         int var9 = index$iv$iv++;
         boolean var10 = false;
         if (var9 < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         int $i$a$-forEachIndexed-Utils$replace$1 = 0;
         Quad it = (Quad)item$iv$iv;
         int $i$a$-replace-Model$transformV$1 = 0;
         int $i$f$transformV = 0;
         int $i$f$transformVI = 0;
         Vertex var47 = it.getV1();
         boolean idx$iv = false;
         Vertex vertex$iv = var47;
         int $i$a$-transformVI-Quad$transformV$1 = 0;
         Vertex var27 = (Vertex)trans.invoke(vertex$iv);
         Vertex var10001 = it.getV2();
         idx$iv = true;
         vertex$iv = var10001;
         $i$a$-transformVI-Quad$transformV$1 = 0;
         Vertex var28 = (Vertex)trans.invoke(vertex$iv);
         Vertex var10002 = it.getV3();
         idx$iv = true;
         vertex$iv = var10002;
         $i$a$-transformVI-Quad$transformV$1 = 0;
         Vertex var29 = (Vertex)trans.invoke(vertex$iv);
         Vertex var10003 = it.getV4();
         idx$iv = true;
         vertex$iv = var10003;
         $i$a$-transformVI-Quad$transformV$1 = 0;
         Vertex var30 = (Vertex)trans.invoke(vertex$iv);
         Quad var33 = new Quad(var27, var28, var29, var30);
         $this$replace$iv.set(var9, var33);
      }

   }

   @NotNull
   public final Quad verticalRectangle(double x1, double z1, double x2, double z2, double yBottom, double yTop) {
      return new Quad(new Vertex(new Double3(x1, yBottom, z1), UV.Companion.getBottomLeft(), (Shader)null, (Shader)null, 12, (DefaultConstructorMarker)null), new Vertex(new Double3(x2, yBottom, z2), UV.Companion.getBottomRight(), (Shader)null, (Shader)null, 12, (DefaultConstructorMarker)null), new Vertex(new Double3(x2, yTop, z2), UV.Companion.getTopRight(), (Shader)null, (Shader)null, 12, (DefaultConstructorMarker)null), new Vertex(new Double3(x1, yTop, z1), UV.Companion.getTopLeft(), (Shader)null, (Shader)null, 12, (DefaultConstructorMarker)null));
   }

   @NotNull
   public final Quad horizontalRectangle(double x1, double z1, double x2, double z2, double y) {
      double xMin = Math.min(x1, x2);
      double xMax = Math.max(x1, x2);
      double zMin = Math.min(z1, z2);
      double zMax = Math.max(z1, z2);
      return new Quad(new Vertex(new Double3(xMin, y, zMin), UV.Companion.getTopLeft(), (Shader)null, (Shader)null, 12, (DefaultConstructorMarker)null), new Vertex(new Double3(xMin, y, zMax), UV.Companion.getBottomLeft(), (Shader)null, (Shader)null, 12, (DefaultConstructorMarker)null), new Vertex(new Double3(xMax, y, zMax), UV.Companion.getBottomRight(), (Shader)null, (Shader)null, 12, (DefaultConstructorMarker)null), new Vertex(new Double3(xMax, y, zMin), UV.Companion.getTopRight(), (Shader)null, (Shader)null, 12, (DefaultConstructorMarker)null));
   }

   @NotNull
   public final Quad faceQuad(@NotNull EnumFacing face) {
      Intrinsics.checkParameterIsNotNull(face, "face");
      Double3 base = GeometryKt.getVec(face).times(0.5D);
      Double3 top = GeometryKt.getVec((EnumFacing)((FaceCorners)GeometryKt.getFaceCorners().get(face.ordinal())).getTopLeft().getFirst()).times(0.5D);
      Double3 left = GeometryKt.getVec((EnumFacing)((FaceCorners)GeometryKt.getFaceCorners().get(face.ordinal())).getTopLeft().getSecond()).times(0.5D);
      return new Quad(new Vertex(base.plus(top).plus(left), UV.Companion.getTopLeft(), (Shader)null, (Shader)null, 12, (DefaultConstructorMarker)null), new Vertex(base.minus(top).plus(left), UV.Companion.getBottomLeft(), (Shader)null, (Shader)null, 12, (DefaultConstructorMarker)null), new Vertex(base.minus(top).minus(left), UV.Companion.getBottomRight(), (Shader)null, (Shader)null, 12, (DefaultConstructorMarker)null), new Vertex(base.plus(top).minus(left), UV.Companion.getTopRight(), (Shader)null, (Shader)null, 12, (DefaultConstructorMarker)null));
   }

   public Model() {
      boolean var1 = false;
      List var3 = (List)(new ArrayList());
      this.quads = var3;
   }

   public Model(@NotNull List<Quad> other) {
      Intrinsics.checkParameterIsNotNull(other, "other");
      this();
      this.quads.addAll((Collection)other);
   }
}
