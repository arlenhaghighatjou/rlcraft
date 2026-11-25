package mods.betterfoliage.client.integration;

import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.render.column.ColumnTextureInfo;
import mods.betterfoliage.client.render.column.SimpleColumnInfo;
import mods.octarinecore.client.resource.ModelRenderKey;
import mods.octarinecore.client.resource.ModelRenderRegistryBase;
import mods.octarinecore.client.resource.Utils;
import mods.octarinecore.metaprog.ClassRef;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraftforge.client.model.IModel;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0003J(\u0010\b\u001a\n\u0012\u0004\u0012\u00020\u0002\u0018\u00010\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fH\u0016R\u0014\u0010\u0004\u001a\u00020\u0005X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0010"},
   d2 = {"Lmods/betterfoliage/client/integration/TechRebornLogSupport;", "Lmods/octarinecore/client/resource/ModelRenderRegistryBase;", "Lmods/betterfoliage/client/render/column/ColumnTextureInfo;", "()V", "logger", "Lorg/apache/logging/log4j/Logger;", "getLogger", "()Lorg/apache/logging/log4j/Logger;", "processModel", "Lmods/octarinecore/client/resource/ModelRenderKey;", "state", "Lnet/minecraft/block/state/IBlockState;", "modelLoc", "Lnet/minecraft/client/renderer/block/model/ModelResourceLocation;", "model", "Lnet/minecraftforge/client/model/IModel;", "BetterFoliage-MC1.12"}
)
public final class TechRebornLogSupport extends ModelRenderRegistryBase<ColumnTextureInfo> {
   @NotNull
   private static final Logger logger = BetterFoliageMod.INSTANCE.getLogDetail();
   public static final TechRebornLogSupport INSTANCE;

   @NotNull
   public Logger getLogger() {
      return logger;
   }

   @Nullable
   public ModelRenderKey<ColumnTextureInfo> processModel(@NotNull IBlockState state, @NotNull ModelResourceLocation modelLoc, @NotNull IModel model) {
      Intrinsics.checkParameterIsNotNull(state, "state");
      Intrinsics.checkParameterIsNotNull(modelLoc, "modelLoc");
      Intrinsics.checkParameterIsNotNull(model, "model");
      ClassRef var10000 = TechRebornRubberIntegration.INSTANCE.getBlockRubberLog();
      Block var10001 = state.getBlock();
      Intrinsics.checkExpressionValueIsNotNull(var10001, "state.block");
      if (!var10000.isInstance(var10001)) {
         return null;
      } else {
         Pair var54 = (Pair)CollectionsKt.firstOrNull(Utils.getModelBlockAndLoc(model));
         if (var54 == null) {
            return null;
         } else {
            Pair blockLoc = var54;
            ImmutableSet var55 = (ImmutableSet)state.getProperties().entrySet();
            Intrinsics.checkExpressionValueIsNotNull(var55, "state.properties.entries");
            Iterable sapSide = (Iterable)var55;
            boolean textureNames = false;
            int $i$f$all = 0;
            Iterator $this$mapTo$iv$iv = sapSide.iterator();

            while(true) {
               if ($this$mapTo$iv$iv.hasNext()) {
                  Object element$iv = $this$mapTo$iv$iv.next();
                  Entry it = (Entry)element$iv;
                  int $i$a$-find-TechRebornLogSupport$processModel$hasSap$1 = 0;
                  if (!Intrinsics.areEqual(((IProperty)it.getKey()).getName(), "hassap")) {
                     continue;
                  }

                  var55 = (ImmutableSet)element$iv;
                  break;
               }

               var55 = null;
               break;
            }

            Comparable var57 = (Entry)var55 != null ? (Comparable)((Entry)var55).getValue() : null;
            if (!(var57 instanceof Boolean)) {
               var57 = null;
            }

            Boolean var58 = (Boolean)var57;
            if (var58 == null) {
               return null;
            } else {
               boolean hasSap = var58.booleanValue();
               ImmutableSet var59 = (ImmutableSet)state.getProperties().entrySet();
               Intrinsics.checkExpressionValueIsNotNull(var59, "state.properties.entries");
               Iterable var20 = (Iterable)var59;
               boolean $this$all$iv = false;
               boolean var31 = false;
               Iterator var34 = var20.iterator();

               while(true) {
                  if (var34.hasNext()) {
                     Object var39 = var34.next();
                     Entry it = (Entry)var39;
                     int $i$a$-find-TechRebornLogSupport$processModel$sapSide$1 = 0;
                     if (!Intrinsics.areEqual(((IProperty)it.getKey()).getName(), "sapside")) {
                        continue;
                     }

                     var59 = (ImmutableSet)var39;
                     break;
                  }

                  var59 = null;
                  break;
               }

               Comparable var61 = (Entry)var59 != null ? (Comparable)((Entry)var59).getValue() : null;
               if (!(var61 instanceof EnumFacing)) {
                  var61 = null;
               }

               EnumFacing var62 = (EnumFacing)var61;
               if (var62 == null) {
                  return null;
               } else {
                  EnumFacing sapSide = var62;
                  this.getLogger().log(Level.DEBUG, this.getLogName() + ": block state " + state);
                  if (hasSap) {
                     Iterable $this$map$iv = (Iterable)CollectionsKt.listOf(new String[]{"end", "end", "sapside", "side"});
                     $i$f$map = 0;
                     Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));
                     int $i$f$mapTo = 0;

                     for(Object item$iv$iv : $this$map$iv) {
                        String it = (String)item$iv$iv;
                        int $i$a$-map-TechRebornLogSupport$processModel$textureNames$1 = 0;
                        String var18 = ((ModelBlock)blockLoc.getFirst()).resolveTextureName(it);
                        destination$iv$iv.add(var18);
                     }

                     List textureNames = (List)destination$iv$iv;
                     this.getLogger().log(Level.DEBUG, this.getLogName() + ":             spotDir=" + sapSide + ", end=" + (String)textureNames.get(0) + ", side=" + (String)textureNames.get(2) + ", spot=" + (String)textureNames.get(3));
                     $this$map$iv = (Iterable)textureNames;
                     $i$f$all = 0;
                     boolean var63;
                     if ($this$map$iv instanceof Collection && ((Collection)$this$map$iv).isEmpty()) {
                        var63 = true;
                     } else {
                        Iterator var32 = $this$map$iv.iterator();

                        while(true) {
                           if (!var32.hasNext()) {
                              var63 = true;
                              break;
                           }

                           destination$iv$iv = (Collection)var32.next();
                           String it = (String)destination$iv$iv;
                           int $i$a$-all-TechRebornLogSupport$processModel$1 = 0;
                           if (!(Intrinsics.areEqual(it, "missingno") ^ true)) {
                              var63 = false;
                              break;
                           }
                        }
                     }

                     if (var63) {
                        return (ModelRenderKey)(new RubberLogInfo.Key(this.getLogger(), Axis.Y, sapSide, textureNames));
                     }
                  } else {
                     Iterable $this$map$iv = (Iterable)CollectionsKt.listOf(new String[]{"end", "end", "side"});
                     $i$f$map = 0;
                     Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));
                     int $i$f$mapTo = 0;

                     for(Object item$iv$iv : $this$map$iv) {
                        String var51 = (String)item$iv$iv;
                        int $i$a$-map-TechRebornLogSupport$processModel$textureNames$2 = 0;
                        String var53 = ((ModelBlock)blockLoc.getFirst()).resolveTextureName(var51);
                        destination$iv$iv.add(var53);
                     }

                     List textureNames = (List)destination$iv$iv;
                     this.getLogger().log(Level.DEBUG, this.getLogName() + ":             end=" + (String)textureNames.get(0) + ", side=" + (String)textureNames.get(2));
                     $this$map$iv = (Iterable)textureNames;
                     $i$f$all = 0;
                     boolean var64;
                     if ($this$map$iv instanceof Collection && ((Collection)$this$map$iv).isEmpty()) {
                        var64 = true;
                     } else {
                        Iterator var33 = $this$map$iv.iterator();

                        while(true) {
                           if (!var33.hasNext()) {
                              var64 = true;
                              break;
                           }

                           destination$iv$iv = (Collection)var33.next();
                           String it = (String)destination$iv$iv;
                           int $i$a$-all-TechRebornLogSupport$processModel$2 = 0;
                           if (!(Intrinsics.areEqual(it, "missingno") ^ true)) {
                              var64 = false;
                              break;
                           }
                        }
                     }

                     if (var64) {
                        return (ModelRenderKey)(new SimpleColumnInfo.Key(this.getLogger(), Axis.Y, textureNames));
                     }
                  }

                  return null;
               }
            }
         }
      }
   }

   private TechRebornLogSupport() {
   }

   static {
      TechRebornLogSupport var0 = new TechRebornLogSupport();
      INSTANCE = var0;
   }
}
