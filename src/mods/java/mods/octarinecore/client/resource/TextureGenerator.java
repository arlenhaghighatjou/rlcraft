package mods.octarinecore.client.resource;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.TypeCastException;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000Z\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\b&\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u0012\u0010\u0005\u001a\u0004\u0018\u00010\u00062\u0006\u0010\u0007\u001a\u00020\bH&J?\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u00032*\u0010\f\u001a\u0016\u0012\u0012\b\u0001\u0012\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u000f0\u000e0\r\"\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u000f0\u000e¢\u0006\u0002\u0010\u0010J\u0012\u0010\u0011\u001a\u0004\u0018\u00010\u00122\u0006\u0010\u0007\u001a\u00020\bH\u0016J$\u0010\u0013\u001a\u0004\u0018\u00010\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0012\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\n0\u0018J\u0010\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u0007\u001a\u00020\bH\u0016J\u001c\u0010\u001b\u001a\u0010\u0012\u0004\u0012\u00020\u001c\u0012\u0004\u0012\u00020\n\u0018\u00010\u000e2\u0006\u0010\u0007\u001a\u00020\b¨\u0006\u001d"},
   d2 = {"Lmods/octarinecore/client/resource/TextureGenerator;", "Lmods/octarinecore/client/resource/ParameterBasedGenerator;", "domain", "", "(Ljava/lang/String;)V", "generate", "Ljava/awt/image/BufferedImage;", "params", "Lmods/octarinecore/client/resource/ParameterList;", "generatedResource", "Lnet/minecraft/util/ResourceLocation;", "iconName", "extraParams", "", "Lkotlin/Pair;", "", "(Ljava/lang/String;[Lkotlin/Pair;)Lnet/minecraft/util/ResourceLocation;", "getInputStream", "Ljava/io/InputStream;", "getMultisizeTexture", "Lnet/minecraft/client/resources/IResource;", "maxSize", "", "maskPath", "Lkotlin/Function1;", "resourceExists", "", "targetResource", "Lmods/octarinecore/client/resource/ResourceType;", "BetterFoliage-MC1.12"}
)
public abstract class TextureGenerator extends ParameterBasedGenerator {
   @NotNull
   public final ResourceLocation generatedResource(@NotNull String iconName, @NotNull Pair<String, ? extends Object>... extraParams) {
      Intrinsics.checkParameterIsNotNull(iconName, "iconName");
      Intrinsics.checkParameterIsNotNull(extraParams, "extraParams");
      String var10000 = this.getDomain();
      ResourceLocation var3 = Utils.textureLocation(iconName);
      String var26 = var10000;
      boolean var4 = false;
      boolean var5 = false;
      int $i$a$-let-TextureGenerator$generatedResource$1 = 0;
      Map var9 = MapsKt.mapOf(new Pair[]{TuplesKt.to("dom", var3.getResourceDomain()), TuplesKt.to("path", var3.getResourcePath())});
      int $i$f$map = 0;
      Collection destination$iv$iv = (Collection)(new ArrayList(extraParams.length));
      int $i$f$mapTo = 0;

      for(Object item$iv$iv : extraParams) {
         int $i$a$-map-TextureGenerator$generatedResource$1$1 = 0;
         Pair var23 = new Pair(((Pair)item$iv$iv).getFirst(), ((Pair)item$iv$iv).getSecond().toString());
         destination$iv$iv.add(var23);
      }

      List var21 = (List)destination$iv$iv;
      Map var32 = MapsKt.plus(var9, (Iterable)var21);
      String var28 = "generate";
      Map var29 = var32;
      String var27 = (new ParameterList(var29, var28)).toString();
      return new ResourceLocation(var26, var27);
   }

   @Nullable
   public final Pair<ResourceType, ResourceLocation> targetResource(@NotNull ParameterList params) {
      Intrinsics.checkParameterIsNotNull(params, "params");
      Iterable $this$all$iv = (Iterable)CollectionsKt.listOf(new String[]{"dom", "path"});
      int $i$f$all = 0;
      boolean var10000;
      if ($this$all$iv instanceof Collection && ((Collection)$this$all$iv).isEmpty()) {
         var10000 = true;
      } else {
         Iterator var5 = $this$all$iv.iterator();

         while(true) {
            if (!var5.hasNext()) {
               var10000 = true;
               break;
            }

            Object element$iv = var5.next();
            String it = (String)element$iv;
            int $i$a$-all-TextureGenerator$targetResource$baseTexture$1 = 0;
            if (!params.contains(it)) {
               var10000 = false;
               break;
            }
         }
      }

      if (var10000) {
         ResourceLocation var12 = new ResourceLocation;
         String var10002 = params.get("dom");
         if (var10002 == null) {
            Intrinsics.throwNpe();
         }

         String var10003 = params.get("path");
         if (var10003 == null) {
            Intrinsics.throwNpe();
         }

         var12.<init>(var10002, var10003);
         ResourceLocation baseTexture = var12;
         String var13 = params.getValue();
         if (var13 != null) {
            String var9 = var13;
            $i$f$all = 0;
            if (var9 == null) {
               throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
            }

            var13 = var9.toLowerCase();
            Intrinsics.checkExpressionValueIsNotNull(var13, "(this as java.lang.String).toLowerCase()");
         } else {
            var13 = null;
         }

         Pair var15;
         if (var13 != null) {
            String var10 = var13;
            switch(var10.hashCode()) {
            case -1774288545:
               if (var10.equals("generate_n.png")) {
                  var15 = TuplesKt.to(ResourceType.NORMAL, Utils.plus(baseTexture, "_n.png"));
                  return var15;
               }
               break;
            case -1769670940:
               if (var10.equals("generate_s.png")) {
                  var15 = TuplesKt.to(ResourceType.SPECULAR, Utils.plus(baseTexture, "_s.png"));
                  return var15;
               }
               break;
            case 219728409:
               if (var10.equals("generate.png.mcmeta")) {
                  var15 = TuplesKt.to(ResourceType.METADATA, Utils.plus(baseTexture, ".png.mcmeta"));
                  return var15;
               }
               break;
            case 1717366320:
               if (var10.equals("generate.png")) {
                  var15 = TuplesKt.to(ResourceType.COLOR, Utils.plus(baseTexture, ".png"));
                  return var15;
               }
            }
         }

         var15 = null;
         return var15;
      } else {
         return null;
      }
   }

   public boolean resourceExists(@NotNull ParameterList params) {
      Intrinsics.checkParameterIsNotNull(params, "params");
      Pair var10000 = this.targetResource(params);
      boolean var8;
      if (var10000 != null) {
         ResourceLocation var7 = (ResourceLocation)var10000.getSecond();
         if (var7 != null) {
            ResourceLocation var2 = var7;
            boolean var3 = false;
            boolean var4 = false;
            int $i$a$-let-TextureGenerator$resourceExists$1 = 0;
            var8 = Utils.get((IResourceManager)Utils.getResourceManager(), var2) != null;
            return var8;
         }
      }

      var8 = false;
      return var8;
   }

   @Nullable
   public InputStream getInputStream(@NotNull ParameterList params) {
      // $FF: Couldn't be decompiled
   }

   @Nullable
   public abstract BufferedImage generate(@NotNull ParameterList var1);

   @Nullable
   public final IResource getMultisizeTexture(int maxSize, @NotNull Function1<? super Integer, ? extends ResourceLocation> maskPath) {
      Intrinsics.checkParameterIsNotNull(maskPath, "maskPath");
      int size = maxSize;
      boolean $this$map$iv = false;

      List sizes;
      for(sizes = (List)(new ArrayList()); size > 2; size /= 2) {
         sizes.add(Integer.valueOf(size));
      }

      Iterable $this$map$iv = (Iterable)sizes;
      int $i$f$map = 0;
      Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));
      int $i$f$mapTo = 0;

      for(Object item$iv$iv : $this$map$iv) {
         int it = ((Number)item$iv$iv).intValue();
         int $i$a$-map-TextureGenerator$getMultisizeTexture$1 = 0;
         IResource var15 = Utils.get((IResourceManager)Utils.getResourceManager(), (ResourceLocation)maskPath.invoke(Integer.valueOf(it)));
         destination$iv$iv.add(var15);
      }

      return (IResource)CollectionsKt.firstOrNull(CollectionsKt.filterNotNull((Iterable)((List)destination$iv$iv)));
   }

   public TextureGenerator(@NotNull String domain) {
      Intrinsics.checkParameterIsNotNull(domain, "domain");
      super(domain);
   }
}
