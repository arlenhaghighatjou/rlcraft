package mods.octarinecore.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import mods.octarinecore.Utils;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 2,
   d1 = {"\u0000t\n\u0000\n\u0002\u0010\u0011\n\u0002\u0010\u0015\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0017\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\u001c\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\u001aB\u00104\u001a\u000e\u0012\u0004\u0012\u0002H5\u0012\u0004\u0012\u0002060\u000e\"\u0004\b\u0000\u001052\u0006\u00107\u001a\u0002012\f\u00108\u001a\b\u0012\u0004\u0012\u0002H5092\u0012\u0010:\u001a\u000e\u0012\u0004\u0012\u0002H5\u0012\u0004\u0012\u0002010;\u001aB\u0010<\u001a\u000e\u0012\u0004\u0012\u0002H5\u0012\u0004\u0012\u0002060\u000e\"\u0004\b\u0000\u001052\u0006\u0010=\u001a\u0002012\f\u00108\u001a\b\u0012\u0004\u0012\u0002H5092\u0012\u0010>\u001a\u000e\u0012\u0004\u0012\u0002H5\u0012\u0004\u0012\u0002010;\u001a\u000e\u0010?\u001a\u00020@2\u0006\u0010A\u001a\u00020\u000f\u001a\u0015\u0010B\u001a\u00020C*\u00020C2\u0006\u0010D\u001a\u00020\u0015H\u0086\u0002\u001a\u0012\u0010E\u001a\u00020\u000f*\u00020\u000f2\u0006\u0010?\u001a\u00020@\u001a\u0015\u0010F\u001a\u000201*\u00020\u000f2\u0006\u0010G\u001a\u000206H\u0086\u0002\"\u0017\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u00018F\u00a2\u0006\u0006\u001a\u0004\b\u0003\u0010\u0004\"\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\t\"\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\t\"#\u0010\r\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u000f0\u000e0\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\t\"\u0017\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00120\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\t\"\u0017\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00150\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\t\"\u0019\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0001\u00a2\u0006\n\n\u0002\u0010\u001a\u001a\u0004\b\u0018\u0010\u0019\"\u0017\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\t\"\u0015\u0010\u001d\u001a\u00020\u000b*\u00020\u000f8F\u00a2\u0006\u0006\u001a\u0004\b\u001e\u0010\u001f\"!\u0010 \u001a\u00020\u000f*\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u000b0\u000e8F\u00a2\u0006\u0006\u001a\u0004\b!\u0010\"\"\u0015\u0010#\u001a\u00020\u0015*\u00020\u000f8F\u00a2\u0006\u0006\u001a\u0004\b$\u0010%\"\u001b\u0010&\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0006*\u00020\u000f8F\u00a2\u0006\u0006\u001a\u0004\b'\u0010(\"\u001b\u0010)\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0001*\u00020\u000f8F\u00a2\u0006\u0006\u001a\u0004\b*\u0010+\"\u0015\u0010,\u001a\u00020-*\u00020\u000b8F\u00a2\u0006\u0006\u001a\u0004\b.\u0010/\"\u0015\u00100\u001a\u000201*\u00020\u000f8F\u00a2\u0006\u0006\u001a\u0004\b2\u00103\u00a8\u0006H"},
   d2 = {"ROTATION_MATRIX", "", "", "getROTATION_MATRIX", "()[[I", "axes", "", "Lnet/minecraft/util/EnumFacing$Axis;", "getAxes", "()Ljava/util/List;", "axisDirs", "Lnet/minecraft/util/EnumFacing$AxisDirection;", "getAxisDirs", "boxEdges", "Lkotlin/Pair;", "Lnet/minecraft/util/EnumFacing;", "getBoxEdges", "faceCorners", "Lmods/octarinecore/common/FaceCorners;", "getFaceCorners", "forgeDirOffsets", "Lmods/octarinecore/common/Int3;", "getForgeDirOffsets", "forgeDirs", "getForgeDirs", "()[Lnet/minecraft/util/EnumFacing;", "[Lnet/minecraft/util/EnumFacing;", "forgeDirsHorizontal", "getForgeDirsHorizontal", "dir", "getDir", "(Lnet/minecraft/util/EnumFacing;)Lnet/minecraft/util/EnumFacing$AxisDirection;", "face", "getFace", "(Lkotlin/Pair;)Lnet/minecraft/util/EnumFacing;", "offset", "getOffset", "(Lnet/minecraft/util/EnumFacing;)Lmods/octarinecore/common/Int3;", "perpendiculars", "getPerpendiculars", "(Lnet/minecraft/util/EnumFacing;)Ljava/util/List;", "rotations", "getRotations", "(Lnet/minecraft/util/EnumFacing;)[Lnet/minecraft/util/EnumFacing;", "sign", "", "getSign", "(Lnet/minecraft/util/EnumFacing$AxisDirection;)Ljava/lang/String;", "vec", "Lmods/octarinecore/common/Double3;", "getVec", "(Lnet/minecraft/util/EnumFacing;)Lmods/octarinecore/common/Double3;", "nearestAngle", "T", "", "vector", "objs", "", "objAngle", "Lkotlin/Function1;", "nearestPosition", "vertex", "objPos", "rot", "Lmods/octarinecore/common/Rotation;", "axis", "plus", "Lnet/minecraft/util/math/BlockPos;", "other", "rotate", "times", "scale", "BetterFoliage-MC1.12"}
)
public final class GeometryKt {
   @NotNull
   private static final List<Axis> axes = CollectionsKt.listOf(new Axis[]{Axis.X, Axis.Y, Axis.Z});
   @NotNull
   private static final List<AxisDirection> axisDirs = CollectionsKt.listOf(new AxisDirection[]{AxisDirection.POSITIVE, AxisDirection.NEGATIVE});
   @NotNull
   private static final EnumFacing[] forgeDirs = EnumFacing.values();
   @NotNull
   private static final List<EnumFacing> forgeDirsHorizontal = CollectionsKt.listOf(new EnumFacing[]{EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST});
   @NotNull
   private static final List<Int3> forgeDirOffsets;
   @NotNull
   private static final List<Pair<EnumFacing, EnumFacing>> boxEdges;
   @NotNull
   private static final List<FaceCorners> faceCorners;

   @NotNull
   public static final List<Axis> getAxes() {
      return axes;
   }

   @NotNull
   public static final List<AxisDirection> getAxisDirs() {
      return axisDirs;
   }

   @NotNull
   public static final AxisDirection getDir(@NotNull EnumFacing $this$dir) {
      Intrinsics.checkParameterIsNotNull($this$dir, "$this$dir");
      AxisDirection var10000 = $this$dir.getAxisDirection();
      Intrinsics.checkExpressionValueIsNotNull(var10000, "axisDirection");
      return var10000;
   }

   @NotNull
   public static final String getSign(@NotNull AxisDirection $this$sign) {
      // $FF: Couldn't be decompiled
   }

   @NotNull
   public static final EnumFacing[] getForgeDirs() {
      return forgeDirs;
   }

   @NotNull
   public static final List<EnumFacing> getForgeDirsHorizontal() {
      return forgeDirsHorizontal;
   }

   @NotNull
   public static final List<Int3> getForgeDirOffsets() {
      return forgeDirOffsets;
   }

   @NotNull
   public static final EnumFacing getFace(@NotNull Pair<? extends Axis, ? extends AxisDirection> $this$face) {
      Intrinsics.checkParameterIsNotNull($this$face, "$this$face");
      return Intrinsics.areEqual($this$face, TuplesKt.to(Axis.X, AxisDirection.POSITIVE)) ? EnumFacing.EAST : (Intrinsics.areEqual($this$face, TuplesKt.to(Axis.X, AxisDirection.NEGATIVE)) ? EnumFacing.WEST : (Intrinsics.areEqual($this$face, TuplesKt.to(Axis.Y, AxisDirection.POSITIVE)) ? EnumFacing.UP : (Intrinsics.areEqual($this$face, TuplesKt.to(Axis.Y, AxisDirection.NEGATIVE)) ? EnumFacing.DOWN : (Intrinsics.areEqual($this$face, TuplesKt.to(Axis.Z, AxisDirection.POSITIVE)) ? EnumFacing.SOUTH : EnumFacing.NORTH))));
   }

   @NotNull
   public static final List<EnumFacing> getPerpendiculars(@NotNull EnumFacing $this$perpendiculars) {
      Intrinsics.checkParameterIsNotNull($this$perpendiculars, "$this$perpendiculars");
      Iterable $this$filter$iv = (Iterable)axes;
      int $i$f$filter = 0;
      Collection destination$iv$iv = (Collection)(new ArrayList());
      int $i$f$filterTo = 0;

      for(Object element$iv$iv : $this$filter$iv) {
         Axis it = (Axis)element$iv$iv;
         int $i$a$-filter-GeometryKt$perpendiculars$1 = 0;
         if (it != $this$perpendiculars.getAxis()) {
            destination$iv$iv.add(element$iv$iv);
         }
      }

      $this$filter$iv = (Iterable)Utils.cross((Iterable)((List)destination$iv$iv), (Iterable)axisDirs);
      $i$f$filter = 0;
      destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$filter$iv, 10)));
      $i$f$filterTo = 0;

      for(Object item$iv$iv : $this$filter$iv) {
         Pair var18 = (Pair)item$iv$iv;
         int $i$a$-map-GeometryKt$perpendiculars$2 = 0;
         EnumFacing var11 = getFace(var18);
         destination$iv$iv.add(var11);
      }

      return (List)destination$iv$iv;
   }

   @NotNull
   public static final Int3 getOffset(@NotNull EnumFacing $this$offset) {
      Intrinsics.checkParameterIsNotNull($this$offset, "$this$offset");
      return (Int3)forgeDirOffsets.get($this$offset.ordinal());
   }

   @NotNull
   public static final int[][] getROTATION_MATRIX() {
      return new int[][]{{0, 1, 4, 5, 3, 2, 6}, {0, 1, 5, 4, 2, 3, 6}, {5, 4, 2, 3, 0, 1, 6}, {4, 5, 2, 3, 1, 0, 6}, {2, 3, 1, 0, 4, 5, 6}, {3, 2, 0, 1, 4, 5, 6}};
   }

   @NotNull
   public static final Double3 times(@NotNull EnumFacing $this$times, double scale) {
      Intrinsics.checkParameterIsNotNull($this$times, "$this$times");
      Vec3i var10002 = $this$times.getDirectionVec();
      Intrinsics.checkExpressionValueIsNotNull(var10002, "directionVec");
      double var3 = (double)var10002.getX() * scale;
      Vec3i var10003 = $this$times.getDirectionVec();
      Intrinsics.checkExpressionValueIsNotNull(var10003, "directionVec");
      double var4 = (double)var10003.getY() * scale;
      Vec3i var10004 = $this$times.getDirectionVec();
      Intrinsics.checkExpressionValueIsNotNull(var10004, "directionVec");
      return new Double3(var3, var4, (double)var10004.getZ() * scale);
   }

   @NotNull
   public static final Double3 getVec(@NotNull EnumFacing $this$vec) {
      Intrinsics.checkParameterIsNotNull($this$vec, "$this$vec");
      Vec3i var10002 = $this$vec.getDirectionVec();
      Intrinsics.checkExpressionValueIsNotNull(var10002, "directionVec");
      double var1 = (double)var10002.getX();
      Vec3i var10003 = $this$vec.getDirectionVec();
      Intrinsics.checkExpressionValueIsNotNull(var10003, "directionVec");
      double var2 = (double)var10003.getY();
      Vec3i var10004 = $this$vec.getDirectionVec();
      Intrinsics.checkExpressionValueIsNotNull(var10004, "directionVec");
      return new Double3(var1, var2, (double)var10004.getZ());
   }

   @NotNull
   public static final BlockPos plus(@NotNull BlockPos $this$plus, @NotNull Int3 other) {
      Intrinsics.checkParameterIsNotNull($this$plus, "$this$plus");
      Intrinsics.checkParameterIsNotNull(other, "other");
      return new BlockPos($this$plus.getX() + other.getX(), $this$plus.getY() + other.getY(), $this$plus.getZ() + other.getZ());
   }

   @NotNull
   public static final EnumFacing[] getRotations(@NotNull EnumFacing $this$rotations) {
      Intrinsics.checkParameterIsNotNull($this$rotations, "$this$rotations");
      byte var1 = 6;
      EnumFacing[] var2 = new EnumFacing[var1];

      for(int var3 = 0; var3 < var1; ++var3) {
         int $i$a$-<init>-GeometryKt$rotations$1 = 0;
         EnumFacing var8 = EnumFacing.values()[getROTATION_MATRIX()[$this$rotations.ordinal()][var3]];
         var2[var3] = var8;
      }

      return var2;
   }

   @NotNull
   public static final EnumFacing rotate(@NotNull EnumFacing $this$rotate, @NotNull Rotation rot) {
      Intrinsics.checkParameterIsNotNull($this$rotate, "$this$rotate");
      Intrinsics.checkParameterIsNotNull(rot, "rot");
      return rot.getForward()[$this$rotate.ordinal()];
   }

   @NotNull
   public static final Rotation rot(@NotNull EnumFacing axis) {
      Intrinsics.checkParameterIsNotNull(axis, "axis");
      return Rotation.Companion.getRot90()[axis.ordinal()];
   }

   @NotNull
   public static final List<Pair<EnumFacing, EnumFacing>> getBoxEdges() {
      return boxEdges;
   }

   @NotNull
   public static final <T> Pair<T, Double> nearestPosition(@NotNull Double3 vertex, @NotNull Iterable<? extends T> objs, @NotNull Function1<? super T, Double3> objPos) {
      Intrinsics.checkParameterIsNotNull(vertex, "vertex");
      Intrinsics.checkParameterIsNotNull(objs, "objs");
      Intrinsics.checkParameterIsNotNull(objPos, "objPos");
      int $i$f$map = 0;
      Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(objs, 10)));
      int $i$f$mapTo = 0;

      for(Object item$iv$iv : objs) {
         int $i$a$-map-GeometryKt$nearestPosition$1 = 0;
         Pair var15 = TuplesKt.to(item$iv$iv, Double.valueOf(((Double3)objPos.invoke(item$iv$iv)).minus(vertex).getLength()));
         destination$iv$iv.add(var15);
      }

      Iterable $this$minBy$iv = (Iterable)((List)destination$iv$iv);
      $i$f$map = 0;
      Iterator iterator$iv = $this$minBy$iv.iterator();
      Object var10000;
      if (!iterator$iv.hasNext()) {
         var10000 = null;
      } else {
         destination$iv$iv = (Collection)iterator$iv.next();
         if (!iterator$iv.hasNext()) {
            var10000 = destination$iv$iv;
         } else {
            Pair it = (Pair)destination$iv$iv;
            int $i$a$-minBy-GeometryKt$nearestPosition$2 = 0;
            double minValue$iv = ((Number)it.getSecond()).doubleValue();

            while(true) {
               Object e$iv = iterator$iv.next();
               Pair it = (Pair)e$iv;
               int $i$a$-minBy-GeometryKt$nearestPosition$2 = 0;
               double v$iv = ((Number)it.getSecond()).doubleValue();
               if (Double.compare(minValue$iv, v$iv) > 0) {
                  destination$iv$iv = (Collection)e$iv;
                  minValue$iv = v$iv;
               }

               if (!iterator$iv.hasNext()) {
                  break;
               }
            }

            var10000 = destination$iv$iv;
         }
      }

      if (var10000 == null) {
         Intrinsics.throwNpe();
      }

      return (Pair)var10000;
   }

   @NotNull
   public static final <T> Pair<T, Double> nearestAngle(@NotNull Double3 vector, @NotNull Iterable<? extends T> objs, @NotNull Function1<? super T, Double3> objAngle) {
      Intrinsics.checkParameterIsNotNull(vector, "vector");
      Intrinsics.checkParameterIsNotNull(objs, "objs");
      Intrinsics.checkParameterIsNotNull(objAngle, "objAngle");
      int $i$f$map = 0;
      Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(objs, 10)));
      int $i$f$mapTo = 0;

      for(Object item$iv$iv : objs) {
         int $i$a$-map-GeometryKt$nearestAngle$1 = 0;
         Pair var15 = TuplesKt.to(item$iv$iv, Double.valueOf(((Double3)objAngle.invoke(item$iv$iv)).dot(vector)));
         destination$iv$iv.add(var15);
      }

      Iterable $this$maxBy$iv = (Iterable)((List)destination$iv$iv);
      $i$f$map = 0;
      Iterator iterator$iv = $this$maxBy$iv.iterator();
      Object var10000;
      if (!iterator$iv.hasNext()) {
         var10000 = null;
      } else {
         destination$iv$iv = (Collection)iterator$iv.next();
         if (!iterator$iv.hasNext()) {
            var10000 = destination$iv$iv;
         } else {
            Pair it = (Pair)destination$iv$iv;
            int $i$a$-maxBy-GeometryKt$nearestAngle$2 = 0;
            double maxValue$iv = ((Number)it.getSecond()).doubleValue();

            while(true) {
               Object e$iv = iterator$iv.next();
               Pair it = (Pair)e$iv;
               int $i$a$-maxBy-GeometryKt$nearestAngle$2 = 0;
               double v$iv = ((Number)it.getSecond()).doubleValue();
               if (Double.compare(maxValue$iv, v$iv) < 0) {
                  destination$iv$iv = (Collection)e$iv;
                  maxValue$iv = v$iv;
               }

               if (!iterator$iv.hasNext()) {
                  break;
               }
            }

            var10000 = destination$iv$iv;
         }
      }

      if (var10000 == null) {
         Intrinsics.throwNpe();
      }

      return (Pair)var10000;
   }

   @NotNull
   public static final List<FaceCorners> getFaceCorners() {
      return faceCorners;
   }

   static {
      // $FF: Couldn't be decompiled
   }
}
