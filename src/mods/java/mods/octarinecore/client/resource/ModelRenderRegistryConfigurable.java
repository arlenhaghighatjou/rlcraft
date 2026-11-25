package mods.octarinecore.client.resource;

import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import mods.octarinecore.common.config.IBlockMatcher;
import mods.octarinecore.common.config.ModelTextureList;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b&\u0018\u0000*\u0004\b\u0000\u0010\u00012\b\u0012\u0004\u0012\u0002H\u00010\u0002B\u0005\u00a2\u0006\u0002\u0010\u0003J&\u0010\r\u001a\n\u0012\u0004\u0012\u00028\u0000\u0018\u00010\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00120\tH&J(\u0010\r\u001a\n\u0012\u0004\u0012\u00028\u0000\u0018\u00010\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0016H\u0016R\u0012\u0010\u0004\u001a\u00020\u0005X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007R\u0018\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tX\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\u000b\u0010\f\u00a8\u0006\u0017"},
   d2 = {"Lmods/octarinecore/client/resource/ModelRenderRegistryConfigurable;", "T", "Lmods/octarinecore/client/resource/ModelRenderRegistryBase;", "()V", "matchClasses", "Lmods/octarinecore/common/config/IBlockMatcher;", "getMatchClasses", "()Lmods/octarinecore/common/config/IBlockMatcher;", "modelTextures", "", "Lmods/octarinecore/common/config/ModelTextureList;", "getModelTextures", "()Ljava/util/List;", "processModel", "Lmods/octarinecore/client/resource/ModelRenderKey;", "state", "Lnet/minecraft/block/state/IBlockState;", "textures", "", "modelLoc", "Lnet/minecraft/client/renderer/block/model/ModelResourceLocation;", "model", "Lnet/minecraftforge/client/model/IModel;", "BetterFoliage-MC1.12"}
)
public abstract class ModelRenderRegistryConfigurable<T> extends ModelRenderRegistryBase<T> {
   @NotNull
   public abstract IBlockMatcher getMatchClasses();

   @NotNull
   public abstract List<ModelTextureList> getModelTextures();

   @Nullable
   public ModelRenderKey<T> processModel(@NotNull IBlockState state, @NotNull ModelResourceLocation modelLoc, @NotNull IModel model) {
      Intrinsics.checkParameterIsNotNull(state, "state");
      Intrinsics.checkParameterIsNotNull(modelLoc, "modelLoc");
      Intrinsics.checkParameterIsNotNull(model, "model");
      IBlockMatcher var10000 = this.getMatchClasses();
      Block var10001 = state.getBlock();
      Intrinsics.checkExpressionValueIsNotNull(var10001, "state.block");
      Class var66 = var10000.matchingClass(var10001);
      if (var66 == null) {
         return null;
      } else {
         Class matchClass = var66;
         Logger var67 = this.getLogger();
         if (var67 != null) {
            var67.log(Level.DEBUG, this.getLogName() + ": block state " + state.toString());
         }

         var67 = this.getLogger();
         if (var67 != null) {
            var67.log(Level.DEBUG, this.getLogName() + ":       class " + state.getBlock().getClass().getName() + " matches " + matchClass.getName());
         }

         Iterable $this$distinctBy$iv = (Iterable)Utils.getModelBlockAndLoc(model);
         int $i$f$distinctBy = 0;
         HashSet set$iv = new HashSet();
         ArrayList list$iv = new ArrayList();

         for(Object e$iv : $this$distinctBy$iv) {
            Pair it = (Pair)e$iv;
            int $i$a$-distinctBy-ModelRenderRegistryConfigurable$processModel$allModels$1 = 0;
            it = (ResourceLocation)it.getSecond();
            if (set$iv.add(it)) {
               list$iv.add(e$iv);
            }
         }

         List allModels = (List)list$iv;
         if (allModels.isEmpty()) {
            var67 = this.getLogger();
            if (var67 != null) {
               var67.log(Level.DEBUG, this.getLogName() + ":       no models found");
            }

            return null;
         } else {
            $this$distinctBy$iv = (Iterable)allModels;
            $i$f$distinctBy = 0;

            for(Object element$iv : $this$distinctBy$iv) {
               Pair blockLoc = (Pair)element$iv;
               int $i$a$-forEach-ModelRenderRegistryConfigurable$processModel$1 = 0;
               Iterable $this$firstOrNull$iv = (Iterable)this.getModelTextures();
               int $i$f$firstOrNull = 0;
               Iterator $this$map$iv = $this$firstOrNull$iv.iterator();

               while(true) {
                  if (!$this$map$iv.hasNext()) {
                     var67 = null;
                     break;
                  }

                  Object element$iv = $this$map$iv.next();
                  ModelTextureList it = (ModelTextureList)element$iv;
                  int $i$a$-firstOrNull-ModelRenderRegistryConfigurable$processModel$1$modelMatch$1 = 0;
                  if (Utils.derivesFrom(blockLoc, it.getModelLocation())) {
                     var67 = (Logger)element$iv;
                     break;
                  }
               }

               ModelTextureList modelMatch = (ModelTextureList)var67;
               if (modelMatch != null) {
                  var67 = this.getLogger();
                  if (var67 != null) {
                     var67.log(Level.DEBUG, this.getLogName() + ":       model " + (ResourceLocation)blockLoc.getSecond() + " matches " + modelMatch.getModelLocation());
                  }

                  Iterable $this$map$iv = (Iterable)modelMatch.getTextureNames();
                  int $i$f$map = 0;
                  Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));
                  int $i$f$mapTo = 0;

                  for(Object item$iv$iv : $this$map$iv) {
                     String item$iv$iv = (String)item$iv$iv;
                     int $i$a$-map-ModelRenderRegistryConfigurable$processModel$1$textures$1 = 0;
                     Pair var24 = TuplesKt.to(item$iv$iv, ((ModelBlock)blockLoc.getFirst()).resolveTextureName(item$iv$iv));
                     destination$iv$iv.add(var24);
                  }

                  List textures = (List)destination$iv$iv;
                  Joiner var71 = Joiner.on(", ");
                  Iterable var41 = (Iterable)textures;
                  Joiner var22 = var71;
                  int $i$f$map = 0;
                  Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(var41, 10)));
                  int $i$f$mapTo = 0;

                  for(Object item$iv$iv : var41) {
                     Pair var61 = (Pair)item$iv$iv;
                     int $i$a$-map-ModelRenderRegistryConfigurable$processModel$1$texMapString$1 = 0;
                     String var26 = (String)var61.getFirst() + '=' + (String)var61.getSecond();
                     destination$iv$iv.add(var26);
                  }

                  List var63 = (List)destination$iv$iv;
                  String texMapString = var22.join((Iterable)var63);
                  Logger var72 = this.getLogger();
                  if (var72 != null) {
                     var72.log(Level.DEBUG, this.getLogName() + ":       textures [" + texMapString + ']');
                  }

                  $this$all$iv = (Iterable)textures;
                  $i$f$map = 0;
                  boolean var73;
                  if (var41 instanceof Collection && ((Collection)var41).isEmpty()) {
                     var73 = true;
                  } else {
                     Iterator var48 = var41.iterator();

                     while(true) {
                        if (!var48.hasNext()) {
                           var73 = true;
                           break;
                        }

                        destination$iv$iv = (Collection)var48.next();
                        Pair it = (Pair)destination$iv$iv;
                        int $i$a$-all-ModelRenderRegistryConfigurable$processModel$1$1 = 0;
                        if (!(Intrinsics.areEqual((String)it.getSecond(), "missingno") ^ true)) {
                           var73 = false;
                           break;
                        }
                     }
                  }

                  if (var73) {
                     var41 = (Iterable)textures;
                     $i$f$map = 0;
                     destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(var41, 10)));
                     $i$f$mapTo = 0;

                     for(Object item$iv$iv : var41) {
                        Pair var62 = (Pair)item$iv$iv;
                        int $i$a$-map-ModelRenderRegistryConfigurable$processModel$1$2 = 0;
                        String var27 = (String)var62.getSecond();
                        destination$iv$iv.add(var27);
                     }

                     List var65 = (List)destination$iv$iv;
                     return this.processModel(state, var65);
                  }
               }
            }

            return null;
         }
      }
   }

   @Nullable
   public abstract ModelRenderKey<T> processModel(@NotNull IBlockState var1, @NotNull List<String> var2);

   public ModelRenderRegistryConfigurable() {
   }
}
