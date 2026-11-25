package mods.betterfoliage.client.integration;

import com.google.common.collect.ImmutableSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.texture.LeafInfo;
import mods.betterfoliage.client.texture.StandardLeafKey;
import mods.octarinecore.Utils;
import mods.octarinecore.client.render.BlockContext;
import mods.octarinecore.client.resource.ModelRenderKey;
import mods.octarinecore.client.resource.ModelRenderRegistry;
import mods.octarinecore.metaprog.ClassRef;
import mods.octarinecore.metaprog.MethodRef;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.TextureStitchEvent.Post;
import net.minecraftforge.client.event.TextureStitchEvent.Pre;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010%\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010$\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u00c6\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0003J#\u0010\u0013\u001a\u0004\u0018\u00010\u00022\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0019H\u0096\u0002J\u0010\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001dH\u0007J\u0010\u0010\u001e\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001fH\u0007R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R#\u0010\b\u001a\u0014\u0012\u0004\u0012\u00020\n\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00020\u000b0\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR&\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u00020\u000fX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\r\"\u0004\b\u0011\u0010\u0012\u00a8\u0006 "},
   d2 = {"Lmods/betterfoliage/client/integration/ForestryLeafRegistry;", "Lmods/octarinecore/client/resource/ModelRenderRegistry;", "Lmods/betterfoliage/client/texture/LeafInfo;", "()V", "logger", "Lorg/apache/logging/log4j/Logger;", "getLogger", "()Lorg/apache/logging/log4j/Logger;", "textureToKey", "", "Lnet/minecraft/util/ResourceLocation;", "Lmods/octarinecore/client/resource/ModelRenderKey;", "getTextureToKey", "()Ljava/util/Map;", "textureToValue", "", "getTextureToValue", "setTextureToValue", "(Ljava/util/Map;)V", "get", "state", "Lnet/minecraft/block/state/IBlockState;", "world", "Lnet/minecraft/world/IBlockAccess;", "pos", "Lnet/minecraft/util/math/BlockPos;", "handlePostStitch", "", "event", "Lnet/minecraftforge/client/event/TextureStitchEvent$Post;", "handlePreStitch", "Lnet/minecraftforge/client/event/TextureStitchEvent$Pre;", "BetterFoliage-MC1.12"}
)
public final class ForestryLeafRegistry implements ModelRenderRegistry<LeafInfo> {
   @NotNull
   private static final Logger logger = BetterFoliageMod.INSTANCE.getLogDetail();
   @NotNull
   private static final Map<ResourceLocation, ModelRenderKey<LeafInfo>> textureToKey = (Map)(new LinkedHashMap());
   @NotNull
   private static Map<ResourceLocation, LeafInfo> textureToValue = MapsKt.emptyMap();
   public static final ForestryLeafRegistry INSTANCE;

   @NotNull
   public final Logger getLogger() {
      return logger;
   }

   @NotNull
   public final Map<ResourceLocation, ModelRenderKey<LeafInfo>> getTextureToKey() {
      return textureToKey;
   }

   @NotNull
   public final Map<ResourceLocation, LeafInfo> getTextureToValue() {
      return textureToValue;
   }

   public final void setTextureToValue(@NotNull Map<ResourceLocation, LeafInfo> var1) {
      Intrinsics.checkParameterIsNotNull(<set-?>, "<set-?>");
      textureToValue = <set-?>;
   }

   @Nullable
   public LeafInfo get(@NotNull IBlockState state, @NotNull IBlockAccess world, @NotNull BlockPos pos) {
      Intrinsics.checkParameterIsNotNull(state, "state");
      Intrinsics.checkParameterIsNotNull(world, "world");
      Intrinsics.checkParameterIsNotNull(pos, "pos");
      ImmutableSet var10000 = (ImmutableSet)state.getProperties().entrySet();
      Intrinsics.checkExpressionValueIsNotNull(var10000, "state.properties.entries");
      Iterable tile = (Iterable)var10000;
      boolean textureLoc = false;
      boolean it = false;
      Iterator var8 = tile.iterator();

      while(true) {
         if (!var8.hasNext()) {
            var10000 = null;
            break;
         }

         Object species;
         label47: {
            species = var8.next();
            Entry it = (Entry)species;
            int $i$a$-find-ForestryLeafRegistry$get$1 = 0;
            ClassRef var24 = ForestryIntegration.INSTANCE.getPropertyTreeType();
            Object var10001 = it.getKey();
            Intrinsics.checkExpressionValueIsNotNull(var10001, "it.key");
            if (var24.isInstance(var10001)) {
               var24 = ForestryIntegration.INSTANCE.getTreeDefinition();
               var10001 = it.getValue();
               Intrinsics.checkExpressionValueIsNotNull(var10001, "it.value");
               if (var24.isInstance(var10001)) {
                  var26 = true;
                  break label47;
               }
            }

            var26 = false;
         }

         if (var26) {
            var10000 = (ImmutableSet)species;
            break;
         }
      }

      Entry var28 = (Entry)var10000;
      if (var28 != null) {
         Entry var15 = var28;
         textureLoc = false;
         boolean var18 = false;
         int $i$a$-let-ForestryLeafRegistry$get$2 = 0;
         Object species = ForestryIntegration.INSTANCE.getTdSpecies().get(var15.getValue());
         MethodRef var31 = ForestryIntegration.INSTANCE.getGetLeafSpriteProvider();
         if (species == null) {
            Intrinsics.throwNpe();
         }

         Object spriteProvider = var31.invoke(species);
         var31 = ForestryIntegration.INSTANCE.getGetSprite();
         if (spriteProvider == null) {
            Intrinsics.throwNpe();
         }

         Object textureLoc = var31.invoke(spriteProvider, false, Minecraft.isFancyGraphicsEnabled());
         Map var12 = textureToValue;
         boolean var13 = false;
         return (LeafInfo)var12.get(textureLoc);
      } else {
         TileEntity var29 = Utils.getTileEntitySafe(world, pos);
         if (var29 != null) {
            TileEntity tile = var29;
            if (!ForestryIntegration.INSTANCE.getTileLeaves().isInstance(tile)) {
               return null;
            } else {
               var29 = (TileEntity)ForestryIntegration.INSTANCE.getTiLgetLeaveSprite().invoke(tile, Minecraft.isFancyGraphicsEnabled());
               if (var29 != null) {
                  Object textureLoc = var29;
                  Map var6 = textureToValue;
                  it = false;
                  return (LeafInfo)var6.get(textureLoc);
               } else {
                  return null;
               }
            }
         } else {
            return null;
         }
      }
   }

   @SubscribeEvent
   public final void handlePreStitch(@NotNull Pre event) {
      Intrinsics.checkParameterIsNotNull(event, "event");
      textureToValue = MapsKt.emptyMap();
      Object var10000 = ForestryIntegration.INSTANCE.getTeLleafTextures().getStatic();
      if (var10000 == null) {
         throw new TypeCastException("null cannot be cast to non-null type kotlin.collections.Map<*, *>");
      } else {
         Map allLeaves = (Map)var10000;
         Iterable $this$forEach$iv = (Iterable)allLeaves.entrySet();
         int $i$f$forEach = 0;

         for(Object element$iv : $this$forEach$iv) {
            Entry it = (Entry)element$iv;
            int $i$a$-forEach-ForestryLeafRegistry$handlePreStitch$1 = 0;
            logger.log(Level.DEBUG, "ForestryLeavesSupport: base leaf type " + String.valueOf(it.getKey()));
            ResourceLocation[] var21 = new ResourceLocation[4];
            String var10003 = (String)ForestryIntegration.INSTANCE.getTeLplain().get(it.getValue());
            if (var10003 == null) {
               throw new TypeCastException("null cannot be cast to non-null type net.minecraft.util.ResourceLocation");
            }

            var21[0] = (ResourceLocation)var10003;
            var10003 = ForestryIntegration.INSTANCE.getTeLfancy().get(it.getValue());
            if (var10003 == null) {
               throw new TypeCastException("null cannot be cast to non-null type net.minecraft.util.ResourceLocation");
            }

            var21[1] = (ResourceLocation)var10003;
            var10003 = ForestryIntegration.INSTANCE.getTeLpollplain().get(it.getValue());
            if (var10003 == null) {
               throw new TypeCastException("null cannot be cast to non-null type net.minecraft.util.ResourceLocation");
            }

            var21[2] = (ResourceLocation)var10003;
            var10003 = ForestryIntegration.INSTANCE.getTeLpollfancy().get(it.getValue());
            if (var10003 == null) {
               throw new TypeCastException("null cannot be cast to non-null type net.minecraft.util.ResourceLocation");
            }

            var21[3] = (ResourceLocation)var10003;
            Iterable $this$forEach$iv = (Iterable)CollectionsKt.listOf(var21);
            int $i$f$forEach = 0;

            for(Object element$iv : $this$forEach$iv) {
               ResourceLocation textureLocation = (ResourceLocation)element$iv;
               int $i$a$-forEach-ForestryLeafRegistry$handlePreStitch$1$1 = 0;
               Logger var10002 = logger;
               var10003 = textureLocation.toString();
               Intrinsics.checkExpressionValueIsNotNull(var10003, "textureLocation.toString()");
               StandardLeafKey var15 = new StandardLeafKey(var10002, var10003);
               boolean var16 = false;
               boolean var17 = false;
               int $i$a$-apply-ForestryLeafRegistry$handlePreStitch$1$1$key$1 = 0;
               TextureMap var10001 = event.getMap();
               Intrinsics.checkExpressionValueIsNotNull(var10001, "event.map");
               var15.onPreStitch(var10001);
               textureToKey.put(textureLocation, var15);
            }
         }

      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public final void handlePostStitch(@NotNull Post event) {
      Intrinsics.checkParameterIsNotNull(event, "event");
      Map $this$mapValues$iv = textureToKey;
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
         int $i$a$-mapValues-ForestryLeafRegistry$handlePostStitch$1 = 0;
         boolean var18 = false;
         ModelRenderKey key = (ModelRenderKey)$dstr$_u24__u24$key.getValue();
         TextureMap var10001 = event.getMap();
         Intrinsics.checkExpressionValueIsNotNull(var10001, "event.map");
         LeafInfo var22 = (LeafInfo)key.resolveSprites(var10001);
         destination$iv$iv.put(var14, var22);
      }

      textureToValue = destination$iv$iv;
      textureToKey.clear();
   }

   private ForestryLeafRegistry() {
   }

   static {
      ForestryLeafRegistry var0 = new ForestryLeafRegistry();
      INSTANCE = var0;
      boolean var1 = false;
   }

   @Nullable
   public LeafInfo get(@NotNull BlockContext ctx) {
      Intrinsics.checkParameterIsNotNull(ctx, "ctx");
      return (LeafInfo)ModelRenderRegistry.DefaultImpls.get(this, ctx);
   }
}
