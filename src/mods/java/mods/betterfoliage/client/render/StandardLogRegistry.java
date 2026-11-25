package mods.betterfoliage.client.render;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Map.Entry;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.config.Config;
import mods.betterfoliage.client.render.column.ColumnTextureInfo;
import mods.betterfoliage.client.render.column.SimpleColumnInfo;
import mods.octarinecore.client.resource.ModelRenderRegistryConfigurable;
import mods.octarinecore.common.config.ConfigurableBlockMatcher;
import mods.octarinecore.common.config.ModelTextureList;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing.Axis;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\bÆ\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0003J\u0010\u0010\u0011\u001a\u0004\u0018\u00010\u00122\u0006\u0010\u0013\u001a\u00020\u0014J\u001e\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0013\u001a\u00020\u00142\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00180\rH\u0016R\u0014\u0010\u0004\u001a\u00020\u0005X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0014\u0010\b\u001a\u00020\t8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\n\u0010\u000bR\u001a\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\r8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u000f\u0010\u0010¨\u0006\u0019"},
   d2 = {"Lmods/betterfoliage/client/render/StandardLogRegistry;", "Lmods/octarinecore/client/resource/ModelRenderRegistryConfigurable;", "Lmods/betterfoliage/client/render/column/ColumnTextureInfo;", "()V", "logger", "Lorg/apache/logging/log4j/Logger;", "getLogger", "()Lorg/apache/logging/log4j/Logger;", "matchClasses", "Lmods/octarinecore/common/config/ConfigurableBlockMatcher;", "getMatchClasses", "()Lmods/octarinecore/common/config/ConfigurableBlockMatcher;", "modelTextures", "", "Lmods/octarinecore/common/config/ModelTextureList;", "getModelTextures", "()Ljava/util/List;", "getAxis", "Lnet/minecraft/util/EnumFacing$Axis;", "state", "Lnet/minecraft/block/state/IBlockState;", "processModel", "Lmods/betterfoliage/client/render/column/SimpleColumnInfo$Key;", "textures", "", "BetterFoliage-MC1.12"}
)
public final class StandardLogRegistry extends ModelRenderRegistryConfigurable<ColumnTextureInfo> {
   @NotNull
   private static final Logger logger = BetterFoliageMod.INSTANCE.getLogDetail();
   public static final StandardLogRegistry INSTANCE;

   @NotNull
   public Logger getLogger() {
      return logger;
   }

   @NotNull
   public ConfigurableBlockMatcher getMatchClasses() {
      return Config.blocks.INSTANCE.getLogClasses();
   }

   @NotNull
   public List<ModelTextureList> getModelTextures() {
      return Config.blocks.INSTANCE.getLogModels().getList();
   }

   @NotNull
   public SimpleColumnInfo.Key processModel(@NotNull IBlockState state, @NotNull List<String> textures) {
      Intrinsics.checkParameterIsNotNull(state, "state");
      Intrinsics.checkParameterIsNotNull(textures, "textures");
      return new SimpleColumnInfo.Key(this.getLogger(), this.getAxis(state), textures);
   }

   @Nullable
   public final Axis getAxis(@NotNull final IBlockState state) {
      Intrinsics.checkParameterIsNotNull(state, "state");
      String var10000 = (String)mods.octarinecore.Utils.tryDefault((Object)null, new Function0<String>() {
         @NotNull
         public final String invoke() {
            return ((EnumAxis)state.getValue((IProperty)BlockLog.LOG_AXIS)).toString();
         }
      });
      if (var10000 == null) {
         label59: {
            ImmutableSet var13 = (ImmutableSet)state.getProperties().entrySet();
            Intrinsics.checkExpressionValueIsNotNull(var13, "state.properties.entries");
            Iterable var3 = (Iterable)var13;
            boolean var4 = false;
            boolean var6 = false;

            label53: {
               for(Object var8 : var3) {
                  Entry it = (Entry)var8;
                  int $i$a$-find-StandardLogRegistry$getAxis$axis$2 = 0;
                  String var14 = ((IProperty)it.getKey()).getName();
                  Intrinsics.checkExpressionValueIsNotNull(var14, "it.key.getName()");
                  String var11 = var14;
                  boolean var12 = false;
                  if (var11 == null) {
                     throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
                  }

                  var14 = var11.toLowerCase();
                  Intrinsics.checkExpressionValueIsNotNull(var14, "(this as java.lang.String).toLowerCase()");
                  if (Intrinsics.areEqual(var14, "axis")) {
                     var13 = (ImmutableSet)var8;
                     break label53;
                  }
               }

               var13 = null;
            }

            Entry var17 = (Entry)var13;
            if (var17 != null) {
               Comparable var18 = (Comparable)var17.getValue();
               if (var18 != null) {
                  var10000 = var18.toString();
                  break label59;
               }
            }

            var10000 = null;
         }
      }

      String axis = var10000;
      Axis var19;
      if (axis != null) {
         switch(axis.hashCode()) {
         case 120:
            if (axis.equals("x")) {
               var19 = Axis.X;
               return var19;
            }
            break;
         case 121:
            if (axis.equals("y")) {
               var19 = Axis.Y;
               return var19;
            }
            break;
         case 122:
            if (axis.equals("z")) {
               var19 = Axis.Z;
               return var19;
            }
         }
      }

      var19 = null;
      return var19;
   }

   private StandardLogRegistry() {
   }

   static {
      StandardLogRegistry var0 = new StandardLogRegistry();
      INSTANCE = var0;
   }
}
