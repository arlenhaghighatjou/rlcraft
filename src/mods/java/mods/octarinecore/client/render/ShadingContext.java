package mods.octarinecore.client.render;

import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import mods.octarinecore.common.GeometryKt;
import mods.octarinecore.common.Int3;
import mods.octarinecore.common.Rotation;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0016\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u001e\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u00172\u0006\u0010\u001d\u001a\u00020\u00172\u0006\u0010\u001e\u001a\u00020\u0017J\u000e\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020\"J$\u0010#\u001a\u00020$2\u0006\u0010!\u001a\u00020\"2\u0014\b\u0002\u0010%\u001a\u000e\u0012\u0004\u0012\u00020\u0017\u0012\u0004\u0012\u00020\u00040&R\u001a\u0010\u0003\u001a\u00020\u0004X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\bR\u0019\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n\u00a2\u0006\n\n\u0002\u0010\u000e\u001a\u0004\b\f\u0010\rR\u001a\u0010\u000f\u001a\u00020\u0010X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014R\u0015\u0010\u0015\u001a\u00020\u0016*\u00020\u00178F\u00a2\u0006\u0006\u001a\u0004\b\u0018\u0010\u0019\u00a8\u0006'"},
   d2 = {"Lmods/octarinecore/client/render/ShadingContext;", "", "()V", "aoEnabled", "", "getAoEnabled", "()Z", "setAoEnabled", "(Z)V", "aoFaces", "", "Lmods/octarinecore/client/render/AoFaceData;", "getAoFaces", "()[Lmods/octarinecore/client/render/AoFaceData;", "[Lmods/octarinecore/client/render/AoFaceData;", "rotation", "Lmods/octarinecore/common/Rotation;", "getRotation", "()Lmods/octarinecore/common/Rotation;", "setRotation", "(Lmods/octarinecore/common/Rotation;)V", "aoMultiplier", "", "Lnet/minecraft/util/EnumFacing;", "getAoMultiplier", "(Lnet/minecraft/util/EnumFacing;)F", "aoShading", "Lmods/octarinecore/client/render/AoData;", "face", "corner1", "corner2", "blockData", "Lmods/octarinecore/client/render/BlockData;", "offset", "Lmods/octarinecore/common/Int3;", "updateShading", "", "predicate", "Lkotlin/Function1;", "BetterFoliage-MC1.12"}
)
public class ShadingContext {
   @NotNull
   private Rotation rotation = Rotation.Companion.getIdentity();
   private boolean aoEnabled = Minecraft.isAmbientOcclusionEnabled();
   @NotNull
   private final AoFaceData[] aoFaces;

   @NotNull
   public final Rotation getRotation() {
      return this.rotation;
   }

   public final void setRotation(@NotNull Rotation var1) {
      Intrinsics.checkParameterIsNotNull(<set-?>, "<set-?>");
      this.rotation = <set-?>;
   }

   public final boolean getAoEnabled() {
      return this.aoEnabled;
   }

   public final void setAoEnabled(boolean var1) {
      this.aoEnabled = <set-?>;
   }

   @NotNull
   public final AoFaceData[] getAoFaces() {
      return this.aoFaces;
   }

   public final float getAoMultiplier(@NotNull EnumFacing $this$aoMultiplier) {
      // $FF: Couldn't be decompiled
   }

   public final void updateShading(@NotNull Int3 offset, @NotNull Function1<? super EnumFacing, Boolean> predicate) {
      Intrinsics.checkParameterIsNotNull(offset, "offset");
      Intrinsics.checkParameterIsNotNull(predicate, "predicate");
      Object[] $this$forEach$iv = GeometryKt.getForgeDirs();
      int $i$f$forEach = 0;

      for(Object element$iv : $this$forEach$iv) {
         int $i$a$-forEach-ShadingContext$updateShading$2 = 0;
         if (((Boolean)predicate.invoke(element$iv)).booleanValue()) {
            AoFaceData.update$default(this.aoFaces[((EnumFacing)element$iv).ordinal()], offset, false, this.getAoMultiplier((EnumFacing)element$iv), 2, (Object)null);
         }
      }

   }

   @NotNull
   public final AoData aoShading(@NotNull EnumFacing face, @NotNull EnumFacing corner1, @NotNull EnumFacing corner2) {
      Intrinsics.checkParameterIsNotNull(face, "face");
      Intrinsics.checkParameterIsNotNull(corner1, "corner1");
      Intrinsics.checkParameterIsNotNull(corner2, "corner2");
      return this.aoFaces[GeometryKt.rotate(face, this.rotation).ordinal()].get(GeometryKt.rotate(corner1, this.rotation), GeometryKt.rotate(corner2, this.rotation));
   }

   @NotNull
   public final BlockData blockData(@NotNull Int3 offset) {
      Intrinsics.checkParameterIsNotNull(offset, "offset");
      return RendererHolder.getBlockContext().blockData(offset.rotate(this.rotation));
   }

   public ShadingContext() {
      byte var1 = 6;
      AoFaceData[] var2 = new AoFaceData[var1];

      for(int var3 = 0; var3 < var1; ++var3) {
         int $i$a$-<init>-ShadingContext$aoFaces$1 = 0;
         AoFaceData var9 = new AoFaceData(GeometryKt.getForgeDirs()[var3]);
         var2[var3] = var9;
      }

      this.aoFaces = var2;
   }
}
