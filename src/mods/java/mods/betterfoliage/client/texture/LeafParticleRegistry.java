package mods.betterfoliage.client.texture;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import mods.octarinecore.client.resource.IconSet;
import mods.octarinecore.client.resource.LoadModelDataEvent;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent.Post;
import net.minecraftforge.client.event.TextureStitchEvent.Pre;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0011\u0010\u000e\u001a\u00020\u00062\u0006\u0010\u000f\u001a\u00020\u0005H\u0086\u0002J\u0010\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013H\u0007J\u0010\u0010\u0014\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0015H\u0007J\u0010\u0010\u0016\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0017H\u0007R-\u0010\u0003\u001a\u001e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00060\u0004j\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u0006`\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\n\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r\u00a8\u0006\u0018"},
   d2 = {"Lmods/betterfoliage/client/texture/LeafParticleRegistry;", "", "()V", "particles", "Ljava/util/HashMap;", "", "Lmods/octarinecore/client/resource/IconSet;", "Lkotlin/collections/HashMap;", "getParticles", "()Ljava/util/HashMap;", "typeMappings", "Lmods/betterfoliage/client/texture/TextureMatcher;", "getTypeMappings", "()Lmods/betterfoliage/client/texture/TextureMatcher;", "get", "type", "handleLoadModelData", "", "event", "Lmods/octarinecore/client/resource/LoadModelDataEvent;", "handlePostStitch", "Lnet/minecraftforge/client/event/TextureStitchEvent$Post;", "handlePreStitch", "Lnet/minecraftforge/client/event/TextureStitchEvent$Pre;", "BetterFoliage-MC1.12"}
)
public final class LeafParticleRegistry {
   @NotNull
   private static final TextureMatcher typeMappings = new TextureMatcher();
   @NotNull
   private static final HashMap<String, IconSet> particles = new HashMap();
   public static final LeafParticleRegistry INSTANCE;

   @NotNull
   public final TextureMatcher getTypeMappings() {
      return typeMappings;
   }

   @NotNull
   public final HashMap<String, IconSet> getParticles() {
      return particles;
   }

   @NotNull
   public final IconSet get(@NotNull String type) {
      Intrinsics.checkParameterIsNotNull(type, "type");
      IconSet var10000 = (IconSet)particles.get(type);
      if (var10000 == null) {
         var10000 = (IconSet)particles.get("default");
         if (var10000 == null) {
            Intrinsics.throwNpe();
         }

         var10000 = var10000;
      }

      Intrinsics.checkExpressionValueIsNotNull(var10000, "particles[type] ?: particles[\"default\"]!!");
      return var10000;
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public final void handleLoadModelData(@NotNull LoadModelDataEvent event) {
      Intrinsics.checkParameterIsNotNull(event, "event");
      particles.clear();
      typeMappings.loadMappings(new ResourceLocation("betterfoliage", "leaf_texture_mappings.cfg"));
   }

   @SubscribeEvent
   public final void handlePreStitch(@NotNull Pre event) {
      Intrinsics.checkParameterIsNotNull(event, "event");
      Iterable $this$map$iv = (Iterable)typeMappings.getMappings();
      int $i$f$map = 0;
      Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));
      int $i$f$mapTo = 0;

      for(Object item$iv$iv : $this$map$iv) {
         TextureMatcher.Mapping it = (TextureMatcher.Mapping)item$iv$iv;
         int $i$a$-map-LeafParticleRegistry$handlePreStitch$allTypes$1 = 0;
         String var16 = it.getType();
         destination$iv$iv.add(var16);
      }

      List allTypes = CollectionsKt.distinct((Iterable)CollectionsKt.plus((Collection)((List)destination$iv$iv), "default"));
      $this$map$iv = (Iterable)allTypes;
      $i$f$map = 0;

      for(Object element$iv : $this$map$iv) {
         String leafType = (String)element$iv;
         int $i$a$-forEach-LeafParticleRegistry$handlePreStitch$1 = 0;
         IconSet var22 = new IconSet("betterfoliage", "blocks/falling_leaf_" + leafType + "_%d");
         boolean var23 = false;
         boolean var24 = false;
         int $i$a$-apply-LeafParticleRegistry$handlePreStitch$1$particleSet$1 = 0;
         TextureMap var10001 = event.getMap();
         Intrinsics.checkExpressionValueIsNotNull(var10001, "event.map");
         var22.onPreStitch(var10001);
         if (Intrinsics.areEqual(leafType, "default") || var22.getNum() > 0) {
            ((Map)particles).put(leafType, var22);
         }
      }

   }

   @SubscribeEvent
   public final void handlePostStitch(@NotNull Post event) {
      Intrinsics.checkParameterIsNotNull(event, "event");
      Map $this$forEach$iv = (Map)particles;
      int $i$f$forEach = 0;
      boolean var5 = false;

      for(Entry element$iv : $this$forEach$iv.entrySet()) {
         int $i$a$-forEach-LeafParticleRegistry$handlePostStitch$1 = 0;
         boolean var11 = false;
         IconSet particleSet = (IconSet)element$iv.getValue();
         TextureMap var10001 = event.getMap();
         Intrinsics.checkExpressionValueIsNotNull(var10001, "event.map");
         particleSet.onPostStitch(var10001);
      }

   }

   private LeafParticleRegistry() {
   }

   static {
      LeafParticleRegistry var0 = new LeafParticleRegistry();
      INSTANCE = var0;
      boolean var1 = false;
      MinecraftForge.EVENT_BUS.register(var0);
   }
}
