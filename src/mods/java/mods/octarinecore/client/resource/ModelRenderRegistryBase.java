package mods.octarinecore.client.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import mods.betterfoliage.loader.Refs;
import mods.octarinecore.client.render.BlockContext;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryNamespacedDefaultedByKey;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.TextureStitchEvent.Post;
import net.minecraftforge.client.event.TextureStitchEvent.Pre;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000`\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010%\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010$\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b&\u0018\u0000*\u0004\b\u0000\u0010\u00012\b\u0012\u0004\u0012\u0002H\u00010\u00022\b\u0012\u0004\u0012\u0002H\u00010\u0003B\u0005¢\u0006\u0002\u0010\u0004J(\u0010\u0018\u001a\u0004\u0018\u00018\u00002\u0006\u0010\u0019\u001a\u00020\u000f2\u0006\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001dH\u0096\u0002¢\u0006\u0002\u0010\u001eJ\u0010\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020\"H\u0007J\u0010\u0010#\u001a\u00020 2\u0006\u0010!\u001a\u00020$H\u0007J\u0010\u0010%\u001a\u00020 2\u0006\u0010!\u001a\u00020&H\u0007R\u0014\u0010\u0005\u001a\u00020\u00068VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0007\u0010\bR\u0016\u0010\t\u001a\u0004\u0018\u00010\nX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR#\u0010\r\u001a\u0014\u0012\u0004\u0012\u00020\u000f\u0012\n\u0012\b\u0012\u0004\u0012\u00028\u00000\u00100\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R&\u0010\u0013\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00028\u00000\u0014X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\u0012\"\u0004\b\u0016\u0010\u0017¨\u0006'"},
   d2 = {"Lmods/octarinecore/client/resource/ModelRenderRegistryBase;", "T", "Lmods/octarinecore/client/resource/ModelRenderRegistry;", "Lmods/octarinecore/client/resource/ModelRenderDataExtractor;", "()V", "logName", "", "getLogName", "()Ljava/lang/String;", "logger", "Lorg/apache/logging/log4j/Logger;", "getLogger", "()Lorg/apache/logging/log4j/Logger;", "stateToKey", "", "Lnet/minecraft/block/state/IBlockState;", "Lmods/octarinecore/client/resource/ModelRenderKey;", "getStateToKey", "()Ljava/util/Map;", "stateToValue", "", "getStateToValue", "setStateToValue", "(Ljava/util/Map;)V", "get", "state", "world", "Lnet/minecraft/world/IBlockAccess;", "pos", "Lnet/minecraft/util/math/BlockPos;", "(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;)Ljava/lang/Object;", "handleLoadModelData", "", "event", "Lmods/octarinecore/client/resource/LoadModelDataEvent;", "handlePostStitch", "Lnet/minecraftforge/client/event/TextureStitchEvent$Post;", "handlePreStitch", "Lnet/minecraftforge/client/event/TextureStitchEvent$Pre;", "BetterFoliage-MC1.12"}
)
public abstract class ModelRenderRegistryBase<T> implements ModelRenderRegistry<T>, ModelRenderDataExtractor<T> {
   @Nullable
   private final Logger logger;
   @NotNull
   private final Map<IBlockState, ModelRenderKey<T>> stateToKey;
   @NotNull
   private Map<IBlockState, ? extends T> stateToValue;

   @Nullable
   public Logger getLogger() {
      return this.logger;
   }

   @NotNull
   public String getLogName() {
      String var10000 = this.getClass().getName();
      Intrinsics.checkExpressionValueIsNotNull(var10000, "this::class.java.name");
      return var10000;
   }

   @NotNull
   public final Map<IBlockState, ModelRenderKey<T>> getStateToKey() {
      return this.stateToKey;
   }

   @NotNull
   public final Map<IBlockState, T> getStateToValue() {
      return this.stateToValue;
   }

   public final void setStateToValue(@NotNull Map<IBlockState, ? extends T> var1) {
      Intrinsics.checkParameterIsNotNull(<set-?>, "<set-?>");
      this.stateToValue = <set-?>;
   }

   @Nullable
   public T get(@NotNull IBlockState state, @NotNull IBlockAccess world, @NotNull BlockPos pos) {
      Intrinsics.checkParameterIsNotNull(state, "state");
      Intrinsics.checkParameterIsNotNull(world, "world");
      Intrinsics.checkParameterIsNotNull(pos, "pos");
      return (T)this.stateToValue.get(state);
   }

   @SubscribeEvent
   public final void handleLoadModelData(@NotNull LoadModelDataEvent event) {
      Intrinsics.checkParameterIsNotNull(event, "event");
      this.stateToValue = MapsKt.emptyMap();
      RegistryNamespacedDefaultedByKey var10000 = Block.REGISTRY;
      Intrinsics.checkExpressionValueIsNotNull(Block.REGISTRY, "Block.REGISTRY");
      Iterable $this$flatMap$iv = (Iterable)var10000;
      int $i$f$flatMap = 0;
      Collection destination$iv$iv = (Collection)(new ArrayList());
      int $i$f$flatMapTo = 0;

      for(Object element$iv$iv : $this$flatMap$iv) {
         int $i$a$-flatMap-ModelRenderRegistryBase$handleLoadModelData$stateMappings$1 = 0;
         BlockModelShapes var32 = event.getLoader().blockModelShapes;
         Intrinsics.checkExpressionValueIsNotNull(var32, "event.loader.blockModelShapes");
         Map var33 = var32.getBlockStateMapper().blockStateMap;
         Intrinsics.checkExpressionValueIsNotNull(var33, "event.loader.blockModelS…StateMapper.blockStateMap");
         Map var12 = var33;
         boolean model = false;
         var33 = (Map)var12.get(element$iv$iv);
         if (!(var33 instanceof IStateMapper)) {
            var33 = null;
         }

         IStateMapper var35 = (IStateMapper)var33;
         if (var35 == null) {
            var35 = (IStateMapper)(new DefaultStateMapper());
         }

         IStateMapper mapper = var35;
         if (element$iv$iv == null) {
            throw new TypeCastException("null cannot be cast to non-null type net.minecraft.block.Block");
         }

         Map var36 = mapper.putStateModelLocations((Block)element$iv$iv);
         if (var36 == null) {
            throw new TypeCastException("null cannot be cast to non-null type kotlin.collections.Map<net.minecraft.block.state.IBlockState, net.minecraft.client.renderer.block.model.ModelResourceLocation>");
         }

         Iterable list$iv$iv = (Iterable)var36.entrySet();
         CollectionsKt.addAll(destination$iv$iv, list$iv$iv);
      }

      List stateMappings = (List)destination$iv$iv;
      var10000 = (RegistryNamespacedDefaultedByKey)Refs.INSTANCE.getStateModels().get(event.getLoader());
      if (var10000 == null) {
         throw new TypeCastException("null cannot be cast to non-null type kotlin.collections.Map<net.minecraft.client.renderer.block.model.ModelResourceLocation, net.minecraftforge.client.model.IModel>");
      } else {
         Map stateModels = (Map)var10000;
         Iterable $this$forEach$iv = (Iterable)stateMappings;
         int $i$f$forEach = 0;

         for(Object element$iv : $this$forEach$iv) {
            Entry mapping = (Entry)element$iv;
            int $i$a$-forEach-ModelRenderRegistryBase$handleLoadModelData$1 = 0;
            if (((IBlockState)mapping.getKey()).getBlock() != null) {
               IModel var38 = (IModel)stateModels.get(mapping.getValue());
               if (var38 != null) {
                  IModel var27 = var38;
                  boolean var28 = false;
                  boolean var29 = false;
                  IModel model = var27;
                  int $i$a$-let-ModelRenderRegistryBase$handleLoadModelData$1$1 = 0;

                  try {
                     ModelRenderKey var42 = this.processModel((IBlockState)mapping.getKey(), (ModelResourceLocation)mapping.getValue(), model);
                     if (var42 != null) {
                        ModelRenderKey var15 = var42;
                        boolean e = false;
                        boolean var17 = false;
                        int $i$a$-let-ModelRenderRegistryBase$handleLoadModelData$1$1$1 = 0;
                        this.stateToKey.put(mapping.getKey(), var15);
                        Unit var43 = Unit.INSTANCE;
                     } else {
                        var42 = null;
                     }
                  } catch (Exception var20) {
                     Logger var39 = this.getLogger();
                     if (var39 != null) {
                        var39.warn("Exception while trying to process model " + (ModelResourceLocation)mapping.getValue(), (Throwable)var20);
                        Unit var41 = Unit.INSTANCE;
                     } else {
                        var39 = null;
                     }
                  }
               }
            }
         }

      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public final void handlePreStitch(@NotNull Pre event) {
      Intrinsics.checkParameterIsNotNull(event, "event");
      Map $this$forEach$iv = this.stateToKey;
      int $i$f$forEach = 0;
      boolean var5 = false;

      for(Entry element$iv : $this$forEach$iv.entrySet()) {
         int $i$a$-forEach-ModelRenderRegistryBase$handlePreStitch$1 = 0;
         boolean var11 = false;
         ModelRenderKey key = (ModelRenderKey)element$iv.getValue();
         TextureMap var10001 = event.getMap();
         Intrinsics.checkExpressionValueIsNotNull(var10001, "event.map");
         key.onPreStitch(var10001);
      }

   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public final void handlePostStitch(@NotNull Post event) {
      Intrinsics.checkParameterIsNotNull(event, "event");
      Map $this$mapValues$iv = this.stateToKey;
      int $i$f$mapValues = 0;
      Map destination$iv$iv = (Map)(new LinkedHashMap(MapsKt.mapCapacity($this$mapValues$iv.size())));
      int $i$f$mapValuesTo = 0;
      Iterable $this$associateByTo$iv$iv$iv = (Iterable)$this$mapValues$iv.entrySet();
      int $i$f$associateByTo = 0;

      for(Object element$iv$iv$iv : $this$associateByTo$iv$iv$iv) {
         Entry it$iv$iv = (Entry)element$iv$iv$iv;
         int $i$a$-associateByTo-MapsKt__MapsKt$mapValuesTo$1$iv = 0;
         Object var14 = it$iv$iv.getKey();
         Entry $dstr$_u24__u24$key = (Entry)element$iv$iv$iv;
         int $i$a$-mapValues-ModelRenderRegistryBase$handlePostStitch$1 = 0;
         boolean var18 = false;
         ModelRenderKey key = (ModelRenderKey)$dstr$_u24__u24$key.getValue();
         TextureMap var10001 = event.getMap();
         Intrinsics.checkExpressionValueIsNotNull(var10001, "event.map");
         Object var23 = key.resolveSprites(var10001);
         destination$iv$iv.put(var14, var23);
      }

      this.stateToValue = destination$iv$iv;
      this.stateToKey.clear();
   }

   public ModelRenderRegistryBase() {
      boolean var1 = false;
      Map var3 = (Map)(new LinkedHashMap());
      this.stateToKey = var3;
      var1 = false;
      var3 = MapsKt.emptyMap();
      this.stateToValue = var3;
   }

   @Nullable
   public T get(@NotNull BlockContext ctx) {
      Intrinsics.checkParameterIsNotNull(ctx, "ctx");
      return (T)ModelRenderRegistry.DefaultImpls.get(this, ctx);
   }
}
