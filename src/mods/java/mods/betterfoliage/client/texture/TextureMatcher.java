package mods.betterfoliage.client.texture;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001:\u0001\u0010B\u0005¢\u0006\u0002\u0010\u0002J\u0010\u0010\b\u001a\u0004\u0018\u00010\t2\u0006\u0010\n\u001a\u00020\tJ\u0010\u0010\b\u001a\u0004\u0018\u00010\t2\u0006\u0010\u000b\u001a\u00020\fJ\u000e\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\fR\u0017\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0011"},
   d2 = {"Lmods/betterfoliage/client/texture/TextureMatcher;", "", "()V", "mappings", "", "Lmods/betterfoliage/client/texture/TextureMatcher$Mapping;", "getMappings", "()Ljava/util/List;", "getType", "", "iconName", "resource", "Lnet/minecraft/util/ResourceLocation;", "loadMappings", "", "mappingLocation", "Mapping", "BetterFoliage-MC1.12"}
)
public final class TextureMatcher {
   @NotNull
   private final List<TextureMatcher.Mapping> mappings;

   @NotNull
   public final List<TextureMatcher.Mapping> getMappings() {
      return this.mappings;
   }

   @Nullable
   public final String getType(@NotNull ResourceLocation resource) {
      Intrinsics.checkParameterIsNotNull(resource, "resource");
      Iterable $this$filter$iv = (Iterable)this.mappings;
      int $i$f$filter = 0;
      Collection destination$iv$iv = (Collection)(new ArrayList());
      int $i$f$filterTo = 0;

      for(Object element$iv$iv : $this$filter$iv) {
         TextureMatcher.Mapping it = (TextureMatcher.Mapping)element$iv$iv;
         int $i$a$-filter-TextureMatcher$getType$1 = 0;
         if (it.matches(resource)) {
            destination$iv$iv.add(element$iv$iv);
         }
      }

      $this$filter$iv = (Iterable)((List)destination$iv$iv);
      $i$f$filter = 0;
      destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$filter$iv, 10)));
      $i$f$filterTo = 0;

      for(Object item$iv$iv : $this$filter$iv) {
         TextureMatcher.Mapping var19 = (TextureMatcher.Mapping)item$iv$iv;
         int $i$a$-map-TextureMatcher$getType$2 = 0;
         String var12 = var19.getType();
         destination$iv$iv.add(var12);
      }

      return (String)CollectionsKt.firstOrNull((List)destination$iv$iv);
   }

   @Nullable
   public final String getType(@NotNull String iconName) {
      Intrinsics.checkParameterIsNotNull(iconName, "iconName");
      ResourceLocation var2 = new ResourceLocation(iconName);
      boolean var3 = false;
      boolean var4 = false;
      int $i$a$-let-TextureMatcher$getType$3 = 0;
      return this.getType(var2);
   }

   public final void loadMappings(@NotNull ResourceLocation mappingLocation) {
      Intrinsics.checkParameterIsNotNull(mappingLocation, "mappingLocation");
      this.mappings.clear();
      IResource var10000 = mods.octarinecore.client.resource.Utils.get((IResourceManager)mods.octarinecore.client.resource.Utils.getResourceManager(), mappingLocation);
      if (var10000 != null) {
         List var62 = mods.octarinecore.client.resource.Utils.getLines(var10000);
         if (var62 != null) {
            List var2 = var62;
            boolean var3 = false;
            boolean var4 = false;
            int $i$a$-let-TextureMatcher$loadMappings$1 = 0;
            Iterable $this$filter$iv = (Iterable)var2;
            int $i$f$filter = 0;
            Collection destination$iv$iv = (Collection)(new ArrayList());
            int $i$f$filterTo = 0;

            for(Object element$iv$iv : $this$filter$iv) {
               String it = (String)element$iv$iv;
               int $i$a$-filter-TextureMatcher$loadMappings$1$1 = 0;
               if (!StringsKt.startsWith$default(it, "//", false, 2, (Object)null)) {
                  destination$iv$iv.add(element$iv$iv);
               }
            }

            $this$filter$iv = (Iterable)((List)destination$iv$iv);
            $i$f$filter = 0;
            destination$iv$iv = (Collection)(new ArrayList());
            $i$f$filterTo = 0;

            for(Object element$iv$iv : $this$filter$iv) {
               String it = (String)element$iv$iv;
               int $i$a$-filter-TextureMatcher$loadMappings$1$2 = 0;
               CharSequence var16 = (CharSequence)it;
               boolean var17 = false;
               if (var16.length() != 0) {
                  destination$iv$iv.add(element$iv$iv);
               }
            }

            $this$filter$iv = (Iterable)((List)destination$iv$iv);
            $i$f$filter = 0;

            for(Object element$iv : $this$filter$iv) {
               String line = (String)element$iv;
               int $i$a$-forEach-TextureMatcher$loadMappings$1$3 = 0;
               boolean var42 = false;
               if (line == null) {
                  throw new TypeCastException("null cannot be cast to non-null type kotlin.CharSequence");
               }

               List line2 = StringsKt.split$default((CharSequence)StringsKt.trim((CharSequence)line).toString(), new char[]{'='}, false, 0, 6, (Object)null);
               if (line2.size() == 2) {
                  String var43 = (String)line2.get(0);
                  boolean var51 = false;
                  if (var43 == null) {
                     throw new TypeCastException("null cannot be cast to non-null type kotlin.CharSequence");
                  }

                  List mapping = StringsKt.split$default((CharSequence)StringsKt.trim((CharSequence)var43).toString(), new char[]{':'}, false, 0, 6, (Object)null);
                  if (mapping.size() == 1) {
                     var62 = this.mappings;
                     var43 = (String)mapping.get(0);
                     Object var57 = null;
                     List var20 = var62;
                     var51 = false;
                     if (var43 == null) {
                        throw new TypeCastException("null cannot be cast to non-null type kotlin.CharSequence");
                     }

                     String var21 = StringsKt.trim((CharSequence)var43).toString();
                     var43 = (String)line2.get(1);
                     var51 = false;
                     if (var43 == null) {
                        throw new TypeCastException("null cannot be cast to non-null type kotlin.CharSequence");
                     }

                     String var22 = StringsKt.trim((CharSequence)var43).toString();
                     var20.add(new TextureMatcher.Mapping((String)var57, var21, var22));
                  } else if (mapping.size() == 2) {
                     var62 = this.mappings;
                     var43 = (String)mapping.get(0);
                     List var59 = var62;
                     var51 = false;
                     if (var43 == null) {
                        throw new TypeCastException("null cannot be cast to non-null type kotlin.CharSequence");
                     }

                     String var58 = StringsKt.trim((CharSequence)var43).toString();
                     var43 = (String)mapping.get(1);
                     var51 = false;
                     if (var43 == null) {
                        throw new TypeCastException("null cannot be cast to non-null type kotlin.CharSequence");
                     }

                     String var60 = StringsKt.trim((CharSequence)var43).toString();
                     var43 = (String)line2.get(1);
                     var51 = false;
                     if (var43 == null) {
                        throw new TypeCastException("null cannot be cast to non-null type kotlin.CharSequence");
                     }

                     String var61 = StringsKt.trim((CharSequence)var43).toString();
                     var59.add(new TextureMatcher.Mapping(var58, var60, var61));
                  }
               }
            }
         }
      }

   }

   public TextureMatcher() {
      boolean var1 = false;
      List var3 = (List)(new ArrayList());
      this.mappings = var3;
   }

   @Metadata(
      mv = {1, 1, 15},
      bv = {1, 0, 3},
      k = 1,
      d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u001f\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003¢\u0006\u0002\u0010\u0006J\u000b\u0010\u000b\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\t\u0010\f\u001a\u00020\u0003HÆ\u0003J\t\u0010\r\u001a\u00020\u0003HÆ\u0003J)\u0010\u000e\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0003HÆ\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0012\u001a\u00020\u0013HÖ\u0001J\u000e\u0010\u0014\u001a\u00020\u00102\u0006\u0010\u0015\u001a\u00020\u0016J\t\u0010\u0017\u001a\u00020\u0003HÖ\u0001R\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\bR\u0011\u0010\u0005\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\b¨\u0006\u0018"},
      d2 = {"Lmods/betterfoliage/client/texture/TextureMatcher$Mapping;", "", "domain", "", "path", "type", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getDomain", "()Ljava/lang/String;", "getPath", "getType", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "matches", "iconLocation", "Lnet/minecraft/util/ResourceLocation;", "toString", "BetterFoliage-MC1.12"}
   )
   public static final class Mapping {
      @Nullable
      private final String domain;
      @NotNull
      private final String path;
      @NotNull
      private final String type;

      public final boolean matches(@NotNull ResourceLocation iconLocation) {
         Intrinsics.checkParameterIsNotNull(iconLocation, "iconLocation");
         boolean var9;
         if (this.domain == null || Intrinsics.areEqual(this.domain, iconLocation.getResourceDomain())) {
            String var10000 = iconLocation.getResourcePath();
            Intrinsics.checkExpressionValueIsNotNull(var10000, "iconLocation.path");
            String $this$stripStart$iv = var10000;
            String str$iv = "blocks/";
            int $i$f$stripStart = 0;
            if (StringsKt.startsWith$default($this$stripStart$iv, str$iv, false, 2, (Object)null)) {
               int var6 = str$iv.length();
               boolean var7 = false;
               if ($this$stripStart$iv == null) {
                  throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
               }

               var10000 = $this$stripStart$iv.substring(var6);
               Intrinsics.checkExpressionValueIsNotNull(var10000, "(this as java.lang.String).substring(startIndex)");
            } else {
               var10000 = $this$stripStart$iv;
            }

            if (StringsKt.contains((CharSequence)var10000, (CharSequence)this.path, true)) {
               var9 = true;
               return var9;
            }
         }

         var9 = false;
         return var9;
      }

      @Nullable
      public final String getDomain() {
         return this.domain;
      }

      @NotNull
      public final String getPath() {
         return this.path;
      }

      @NotNull
      public final String getType() {
         return this.type;
      }

      public Mapping(@Nullable String domain, @NotNull String path, @NotNull String type) {
         Intrinsics.checkParameterIsNotNull(path, "path");
         Intrinsics.checkParameterIsNotNull(type, "type");
         super();
         this.domain = domain;
         this.path = path;
         this.type = type;
      }

      @Nullable
      public final String component1() {
         return this.domain;
      }

      @NotNull
      public final String component2() {
         return this.path;
      }

      @NotNull
      public final String component3() {
         return this.type;
      }

      @NotNull
      public final TextureMatcher.Mapping copy(@Nullable String domain, @NotNull String path, @NotNull String type) {
         Intrinsics.checkParameterIsNotNull(path, "path");
         Intrinsics.checkParameterIsNotNull(type, "type");
         return new TextureMatcher.Mapping(domain, path, type);
      }

      @NotNull
      public String toString() {
         return "Mapping(domain=" + this.domain + ", path=" + this.path + ", type=" + this.type + ")";
      }

      public int hashCode() {
         return ((this.domain != null ? this.domain.hashCode() : 0) * 31 + (this.path != null ? this.path.hashCode() : 0)) * 31 + (this.type != null ? this.type.hashCode() : 0);
      }

      public boolean equals(@Nullable Object var1) {
         if (this != var1) {
            if (var1 instanceof TextureMatcher.Mapping) {
               TextureMatcher.Mapping var2 = (TextureMatcher.Mapping)var1;
               if (Intrinsics.areEqual(this.domain, var2.domain) && Intrinsics.areEqual(this.path, var2.path) && Intrinsics.areEqual(this.type, var2.type)) {
                  return true;
               }
            }

            return false;
         } else {
            return true;
         }
      }
   }
}
