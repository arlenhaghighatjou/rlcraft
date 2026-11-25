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
import net.minecraft.util.ResourceLocation;
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
   d2 = {"Lmods/betterfoliage/client/integration/IC2LogSupport;", "Lmods/octarinecore/client/resource/ModelRenderRegistryBase;", "Lmods/betterfoliage/client/render/column/ColumnTextureInfo;", "()V", "logger", "Lorg/apache/logging/log4j/Logger;", "getLogger", "()Lorg/apache/logging/log4j/Logger;", "processModel", "Lmods/octarinecore/client/resource/ModelRenderKey;", "state", "Lnet/minecraft/block/state/IBlockState;", "modelLoc", "Lnet/minecraft/client/renderer/block/model/ModelResourceLocation;", "model", "Lnet/minecraftforge/client/model/IModel;", "BetterFoliage-MC1.12"}
)
public final class IC2LogSupport extends ModelRenderRegistryBase<ColumnTextureInfo> {
   @NotNull
   private static final Logger logger = BetterFoliageMod.INSTANCE.getLogDetail();
   public static final IC2LogSupport INSTANCE;

   @NotNull
   public Logger getLogger() {
      return logger;
   }

   @Nullable
   public ModelRenderKey<ColumnTextureInfo> processModel(@NotNull IBlockState state, @NotNull ModelResourceLocation modelLoc, @NotNull IModel model) {
      Intrinsics.checkParameterIsNotNull(state, "state");
      Intrinsics.checkParameterIsNotNull(modelLoc, "modelLoc");
      Intrinsics.checkParameterIsNotNull(model, "model");
      ClassRef var10000 = IC2RubberIntegration.INSTANCE.getBlockRubWood();
      Block var10001 = state.getBlock();
      Intrinsics.checkExpressionValueIsNotNull(var10001, "state.block");
      if (!var10000.isInstance(var10001)) {
         return null;
      } else {
         Pair var48 = (Pair)CollectionsKt.firstOrNull(Utils.getModelBlockAndLoc(model));
         if (var48 == null) {
            return null;
         } else {
            Pair blockLoc = var48;
            ImmutableSet var49 = (ImmutableSet)state.getProperties().entrySet();
            Intrinsics.checkExpressionValueIsNotNull(var49, "state.properties.entries");
            Iterable spotDir = (Iterable)var49;
            boolean textureNames = false;
            int $i$f$any = 0;
            Iterator $this$mapTo$iv$iv = spotDir.iterator();

            while(true) {
               if ($this$mapTo$iv$iv.hasNext()) {
                  Object element$iv = $this$mapTo$iv$iv.next();
                  Entry it = (Entry)element$iv;
                  int $i$a$-find-IC2LogSupport$processModel$type$1 = 0;
                  if (!Intrinsics.areEqual(((IProperty)it.getKey()).getName(), "state")) {
                     continue;
                  }

                  var49 = (ImmutableSet)element$iv;
                  break;
               }

               var49 = null;
               break;
            }

            Entry var51 = (Entry)var49;
            if (var51 != null) {
               Comparable var52 = (Comparable)var51.getValue();
               if (var52 != null) {
                  String var53 = var52.toString();
                  if (var53 != null) {
                     String type = var53;
                     if (Utils.derivesFrom(blockLoc, new ResourceLocation("block/cube_column"))) {
                        label118: {
                           switch(type.hashCode()) {
                           case -494036157:
                              if (type.equals("plain_x")) {
                                 var56 = Axis.X;
                                 break label118;
                              }
                              break;
                           case -494036156:
                              if (type.equals("plain_y")) {
                                 var56 = Axis.Y;
                                 break label118;
                              }
                              break;
                           case -494036155:
                              if (type.equals("plain_z")) {
                                 var56 = Axis.Z;
                                 break label118;
                              }
                           }

                           var56 = null;
                        }

                        Axis axis = var56;
                        Iterable $this$map$iv = (Iterable)CollectionsKt.listOf(new String[]{"end", "end", "side"});
                        $i$f$map = 0;
                        Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));
                        int $i$f$mapTo = 0;

                        for(Object item$iv$iv : $this$map$iv) {
                           String var45 = (String)item$iv$iv;
                           int $i$a$-map-IC2LogSupport$processModel$textureNames$1 = 0;
                           String var47 = ((ModelBlock)blockLoc.getFirst()).resolveTextureName(var45);
                           destination$iv$iv.add(var47);
                        }

                        List textureNames = (List)destination$iv$iv;
                        $this$map$iv = (Iterable)textureNames;
                        $i$f$any = 0;
                        boolean var57;
                        if ($this$map$iv instanceof Collection && ((Collection)$this$map$iv).isEmpty()) {
                           var57 = false;
                        } else {
                           $this$mapTo$iv$iv = $this$map$iv.iterator();

                           while(true) {
                              if (!$this$mapTo$iv$iv.hasNext()) {
                                 var57 = false;
                                 break;
                              }

                              destination$iv$iv = (Collection)$this$mapTo$iv$iv.next();
                              String it = (String)destination$iv$iv;
                              int $i$a$-any-IC2LogSupport$processModel$1 = 0;
                              if (Intrinsics.areEqual(it, "missingno")) {
                                 var57 = true;
                                 break;
                              }
                           }
                        }

                        if (var57) {
                           return null;
                        }

                        this.getLogger().log(Level.DEBUG, "IC2LogSupport: block state " + state.toString());
                        this.getLogger().log(Level.DEBUG, "IC2LogSupport:             axis=" + axis + ", end=" + (String)textureNames.get(0) + ", side=" + (String)textureNames.get(2));
                        return (ModelRenderKey)(new SimpleColumnInfo.Key(this.getLogger(), axis, textureNames));
                     }

                     label158: {
                        label157: {
                           label156: {
                              label155: {
                                 label154: {
                                    switch(type.hashCode()) {
                                    case -2007510292:
                                       if (type.equals("wet_north")) {
                                          break label157;
                                       }
                                       break;
                                    case -2002889804:
                                       if (type.equals("wet_south")) {
                                          break label156;
                                       }
                                       break;
                                    case -701364879:
                                       if (type.equals("dry_north")) {
                                          break label157;
                                       }
                                       break;
                                    case -696744391:
                                       if (type.equals("dry_south")) {
                                          break label156;
                                       }
                                       break;
                                    case -203587274:
                                       if (type.equals("wet_east")) {
                                          break label154;
                                       }
                                       break;
                                    case -203047192:
                                       if (type.equals("wet_west")) {
                                          break label155;
                                       }
                                       break;
                                    case -161453551:
                                       if (type.equals("dry_east")) {
                                          break label154;
                                       }
                                       break;
                                    case -160913469:
                                       if (type.equals("dry_west")) {
                                          break label155;
                                       }
                                    }

                                    var54 = null;
                                    break label158;
                                 }

                                 var54 = EnumFacing.EAST;
                                 break label158;
                              }

                              var54 = EnumFacing.WEST;
                              break label158;
                           }

                           var54 = EnumFacing.SOUTH;
                           break label158;
                        }

                        var54 = EnumFacing.NORTH;
                     }

                     EnumFacing spotDir = var54;
                     Iterable $this$map$iv = (Iterable)CollectionsKt.listOf(new String[]{"up", "down", "north", "south"});
                     $i$f$map = 0;
                     Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));
                     int $i$f$mapTo = 0;

                     for(Object item$iv$iv : $this$map$iv) {
                        String it = (String)item$iv$iv;
                        int $i$a$-map-IC2LogSupport$processModel$textureNames$2 = 0;
                        String var18 = ((ModelBlock)blockLoc.getFirst()).resolveTextureName(it);
                        destination$iv$iv.add(var18);
                     }

                     List textureNames = (List)destination$iv$iv;
                     $this$map$iv = (Iterable)textureNames;
                     $i$f$any = 0;
                     boolean var55;
                     if ($this$map$iv instanceof Collection && ((Collection)$this$map$iv).isEmpty()) {
                        var55 = false;
                     } else {
                        $this$mapTo$iv$iv = $this$map$iv.iterator();

                        while(true) {
                           if (!$this$mapTo$iv$iv.hasNext()) {
                              var55 = false;
                              break;
                           }

                           destination$iv$iv = (Collection)$this$mapTo$iv$iv.next();
                           String it = (String)destination$iv$iv;
                           int $i$a$-any-IC2LogSupport$processModel$2 = 0;
                           if (Intrinsics.areEqual(it, "missingno")) {
                              var55 = true;
                              break;
                           }
                        }
                     }

                     if (var55) {
                        return null;
                     }

                     this.getLogger().log(Level.DEBUG, "IC2LogSupport: block state " + state.toString());
                     this.getLogger().log(Level.DEBUG, "IC2LogSupport:             spotDir=" + spotDir + ", up=" + (String)textureNames.get(0) + ", down=" + (String)textureNames.get(1) + ", side=" + (String)textureNames.get(2) + ", spot=" + (String)textureNames.get(3));
                     return spotDir != null ? (ModelRenderKey)(new RubberLogInfo.Key(this.getLogger(), Axis.Y, spotDir, textureNames)) : (ModelRenderKey)(new SimpleColumnInfo.Key(this.getLogger(), Axis.Y, textureNames));
                  }
               }
            }

            return null;
         }
      }
   }

   private IC2LogSupport() {
   }

   static {
      IC2LogSupport var0 = new IC2LogSupport();
      INSTANCE = var0;
   }
}
