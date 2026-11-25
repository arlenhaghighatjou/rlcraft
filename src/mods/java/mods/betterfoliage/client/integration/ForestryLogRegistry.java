package mods.betterfoliage.client.integration;

import com.google.common.collect.ImmutableSet;
import java.util.Iterator;
import java.util.Map.Entry;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.config.Config;
import mods.betterfoliage.client.render.StandardLogRegistry;
import mods.betterfoliage.client.render.column.ColumnTextureInfo;
import mods.betterfoliage.client.render.column.SimpleColumnInfo;
import mods.octarinecore.client.resource.ModelRenderKey;
import mods.octarinecore.client.resource.ModelRenderRegistryBase;
import mods.octarinecore.common.config.ConfigurableBlockMatcher;
import mods.octarinecore.metaprog.ClassRef;
import mods.octarinecore.metaprog.MethodRef;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u00c6\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0003J(\u0010\b\u001a\n\u0012\u0004\u0012\u00020\u0002\u0018\u00010\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fH\u0016R\u0014\u0010\u0004\u001a\u00020\u0005X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\u0010"},
   d2 = {"Lmods/betterfoliage/client/integration/ForestryLogRegistry;", "Lmods/octarinecore/client/resource/ModelRenderRegistryBase;", "Lmods/betterfoliage/client/render/column/ColumnTextureInfo;", "()V", "logger", "Lorg/apache/logging/log4j/Logger;", "getLogger", "()Lorg/apache/logging/log4j/Logger;", "processModel", "Lmods/octarinecore/client/resource/ModelRenderKey;", "state", "Lnet/minecraft/block/state/IBlockState;", "modelLoc", "Lnet/minecraft/client/renderer/block/model/ModelResourceLocation;", "model", "Lnet/minecraftforge/client/model/IModel;", "BetterFoliage-MC1.12"}
)
public final class ForestryLogRegistry extends ModelRenderRegistryBase<ColumnTextureInfo> {
   @NotNull
   private static final Logger logger = BetterFoliageMod.INSTANCE.getLogDetail();
   public static final ForestryLogRegistry INSTANCE;

   @NotNull
   public Logger getLogger() {
      return logger;
   }

   @Nullable
   public ModelRenderKey<ColumnTextureInfo> processModel(@NotNull IBlockState state, @NotNull ModelResourceLocation modelLoc, @NotNull IModel model) {
      Intrinsics.checkParameterIsNotNull(state, "state");
      Intrinsics.checkParameterIsNotNull(modelLoc, "modelLoc");
      Intrinsics.checkParameterIsNotNull(model, "model");
      ConfigurableBlockMatcher var10000 = Config.blocks.INSTANCE.getLogClasses();
      Block var10001 = state.getBlock();
      Intrinsics.checkExpressionValueIsNotNull(var10001, "state.block");
      if (!var10000.matchesClass(var10001)) {
         return null;
      } else {
         ImmutableSet var15 = (ImmutableSet)state.getProperties().entrySet();
         Intrinsics.checkExpressionValueIsNotNull(var15, "state.properties.entries");
         Iterable bark = (Iterable)var15;
         boolean heart = false;
         boolean var8 = false;
         Iterator var9 = bark.iterator();

         while(true) {
            if (!var9.hasNext()) {
               var15 = null;
               break;
            }

            Object var10;
            label36: {
               var10 = var9.next();
               Entry it = (Entry)var10;
               int $i$a$-find-ForestryLogRegistry$processModel$woodType$1 = 0;
               ClassRef var16 = ForestryIntegration.INSTANCE.getPropertyWoodType();
               var10001 = (Block)it.getKey();
               Intrinsics.checkExpressionValueIsNotNull(var10001, "it.key");
               if (var16.isInstance(var10001)) {
                  var16 = ForestryIntegration.INSTANCE.getIWoodType();
                  var10001 = (Block)it.getValue();
                  Intrinsics.checkExpressionValueIsNotNull(var10001, "it.value");
                  if (var16.isInstance(var10001)) {
                     var18 = true;
                     break label36;
                  }
               }

               var18 = false;
            }

            if (var18) {
               var15 = (ImmutableSet)var10;
               break;
            }
         }

         Entry var20 = (Entry)var15;
         if (var20 != null) {
            Entry woodType = var20;
            this.getLogger().log(Level.DEBUG, "ForestryLogRegistry: block state " + state);
            this.getLogger().log(Level.DEBUG, "ForestryLogRegistry:     variant " + (Comparable)woodType.getValue());
            MethodRef var21 = ForestryIntegration.INSTANCE.getBarkTex();
            var10001 = (Block)woodType.getValue();
            Intrinsics.checkExpressionValueIsNotNull(var10001, "woodType.value");
            String bark = (String)var21.invoke(var10001);
            var21 = ForestryIntegration.INSTANCE.getHeartTex();
            var10001 = (Block)woodType.getValue();
            Intrinsics.checkExpressionValueIsNotNull(var10001, "woodType.value");
            String heart = (String)var21.invoke(var10001);
            this.getLogger().log(Level.DEBUG, "ForestryLogSupport:    textures [heart=" + heart + ", bark=" + bark + ']');
            return bark != null && heart != null ? (ModelRenderKey)(new SimpleColumnInfo.Key(this.getLogger(), StandardLogRegistry.INSTANCE.getAxis(state), CollectionsKt.listOf(new String[]{heart, heart, bark}))) : null;
         } else {
            return null;
         }
      }
   }

   private ForestryLogRegistry() {
   }

   static {
      ForestryLogRegistry var0 = new ForestryLogRegistry();
      INSTANCE = var0;
   }
}
