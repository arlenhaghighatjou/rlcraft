package mods.octarinecore.common.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.TypeCastException;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import mods.octarinecore.client.gui.NonVerboseArrayEntry;
import mods.octarinecore.client.resource.Utils;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010!\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\t\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\u0010\u0011\n\u0000\b&\u0018\u0000*\u0004\b\u0000\u0010\u00012\u00020\u0002B\u0015\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0004¢\u0006\u0002\u0010\u0006J(\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020#2\u0006\u0010$\u001a\u00020\u00042\u0006\u0010%\u001a\u00020\u00042\u0006\u0010&\u001a\u00020\u0004H\u0016J\u0017\u0010'\u001a\u0004\u0018\u00018\u00002\u0006\u0010(\u001a\u00020\u0004H&¢\u0006\u0002\u0010)J\b\u0010*\u001a\u00020!H\u0016J.\u0010+\u001a\u001a\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00040-\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00040-0,2\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0004R\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00028\u00000\b¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u001c\u0010\u000b\u001a\u0004\u0018\u00010\fX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u001a\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\f0\u00148VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0015\u0010\nR\u0014\u0010\u0016\u001a\u00020\u00178VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0018\u0010\u0019R\u0011\u0010\u0005\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0012R\u0017\u0010\u001b\u001a\b\u0012\u0004\u0012\u00028\u00000\b¢\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\nR\u001c\u0010\u001d\u001a\u0004\u0018\u00010\fX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001e\u0010\u000e\"\u0004\b\u001f\u0010\u0010¨\u0006."},
   d2 = {"Lmods/octarinecore/common/config/BlackWhiteListConfigOption;", "VALUE", "Lmods/octarinecore/common/config/ConfigPropertyBase;", "domain", "", "path", "(Ljava/lang/String;Ljava/lang/String;)V", "blackList", "", "getBlackList", "()Ljava/util/List;", "blacklistProperty", "Lnet/minecraftforge/common/config/Property;", "getBlacklistProperty", "()Lnet/minecraftforge/common/config/Property;", "setBlacklistProperty", "(Lnet/minecraftforge/common/config/Property;)V", "getDomain", "()Ljava/lang/String;", "guiProperties", "", "getGuiProperties", "hasChanged", "", "getHasChanged", "()Z", "getPath", "whiteList", "getWhiteList", "whitelistProperty", "getWhitelistProperty", "setWhitelistProperty", "attach", "", "target", "Lnet/minecraftforge/common/config/Configuration;", "langPrefix", "categoryName", "propertyName", "convertValue", "line", "(Ljava/lang/String;)Ljava/lang/Object;", "read", "readDefaults", "Lkotlin/Pair;", "", "BetterFoliage-MC1.12"}
)
public abstract class BlackWhiteListConfigOption<VALUE> extends ConfigPropertyBase {
   @NotNull
   private final List<VALUE> blackList;
   @NotNull
   private final List<VALUE> whiteList;
   @Nullable
   private Property blacklistProperty;
   @Nullable
   private Property whitelistProperty;
   @NotNull
   private final String domain;
   @NotNull
   private final String path;

   @NotNull
   public final List<VALUE> getBlackList() {
      return this.blackList;
   }

   @NotNull
   public final List<VALUE> getWhiteList() {
      return this.whiteList;
   }

   @Nullable
   public final Property getBlacklistProperty() {
      return this.blacklistProperty;
   }

   public final void setBlacklistProperty(@Nullable Property var1) {
      this.blacklistProperty = <set-?>;
   }

   @Nullable
   public final Property getWhitelistProperty() {
      return this.whitelistProperty;
   }

   public final void setWhitelistProperty(@Nullable Property var1) {
      this.whitelistProperty = <set-?>;
   }

   public boolean getHasChanged() {
      return (this.blacklistProperty != null ? this.blacklistProperty.hasChanged() : false) || (this.whitelistProperty != null ? this.whitelistProperty.hasChanged() : false);
   }

   @NotNull
   public List<Property> getGuiProperties() {
      Property[] var10000 = new Property[2];
      Property var10003 = this.whitelistProperty;
      if (this.whitelistProperty == null) {
         Intrinsics.throwNpe();
      }

      var10000[0] = var10003;
      var10003 = this.blacklistProperty;
      if (this.blacklistProperty == null) {
         Intrinsics.throwNpe();
      }

      var10000[1] = var10003;
      return CollectionsKt.listOf(var10000);
   }

   public void attach(@NotNull Configuration target, @NotNull String langPrefix, @NotNull String categoryName, @NotNull String propertyName) {
      Intrinsics.checkParameterIsNotNull(target, "target");
      Intrinsics.checkParameterIsNotNull(langPrefix, "langPrefix");
      Intrinsics.checkParameterIsNotNull(categoryName, "categoryName");
      Intrinsics.checkParameterIsNotNull(propertyName, "propertyName");
      this.setLang((String)null);
      Pair defaults = this.readDefaults(this.domain, this.path);
      this.blacklistProperty = target.get(categoryName, propertyName + "Blacklist", (String[])defaults.getFirst());
      this.whitelistProperty = target.get(categoryName, propertyName + "Whitelist", (String[])defaults.getSecond());
      Property[] var10000 = new Property[2];
      Property var10003 = this.blacklistProperty;
      if (this.blacklistProperty == null) {
         Intrinsics.throwNpe();
      }

      var10000[0] = var10003;
      var10003 = this.whitelistProperty;
      if (this.whitelistProperty == null) {
         Intrinsics.throwNpe();
      }

      var10000[1] = var10003;
      Iterable $this$forEach$iv = (Iterable)CollectionsKt.listOf(var10000);
      int $i$f$forEach = 0;

      for(Object element$iv : $this$forEach$iv) {
         Property it = (Property)element$iv;
         int $i$a$-forEach-BlackWhiteListConfigOption$attach$1 = 0;
         it.setConfigEntryClass(NonVerboseArrayEntry.class);
         it.setLanguageKey(langPrefix + '.' + categoryName + '.' + it.getName());
      }

      this.read();
   }

   @Nullable
   public abstract VALUE convertValue(@NotNull String var1);

   public void read() {
      Pair[] var10000 = new Pair[2];
      Pair var10003 = new Pair;
      List var10005 = this.blackList;
      Property var10006 = this.blacklistProperty;
      if (this.blacklistProperty == null) {
         Intrinsics.throwNpe();
      }

      var10003.<init>(var10005, var10006);
      var10000[0] = var10003;
      var10003 = new Pair;
      var10005 = this.whiteList;
      var10006 = this.whitelistProperty;
      if (this.whitelistProperty == null) {
         Intrinsics.throwNpe();
      }

      var10003.<init>(var10005, var10006);
      var10000[1] = var10003;
      Iterable $this$forEach$iv = (Iterable)CollectionsKt.listOf(var10000);
      int $i$f$forEach = 0;

      for(Object element$iv : $this$forEach$iv) {
         Pair it = (Pair)element$iv;
         int $i$a$-forEach-BlackWhiteListConfigOption$read$1 = 0;
         ((List)it.getFirst()).clear();
         String[] var16 = ((Property)it.getSecond()).getStringList();
         Intrinsics.checkExpressionValueIsNotNull(var16, "it.second.stringList");
         Object[] $this$forEach$iv = var16;
         int $i$f$forEach = 0;

         for(Object element$iv : $this$forEach$iv) {
            int $i$a$-forEach-BlackWhiteListConfigOption$read$1$1 = 0;
            Intrinsics.checkExpressionValueIsNotNull(element$iv, "line");
            Object value = this.convertValue((String)element$iv);
            if (value != null) {
               ((List)it.getFirst()).add(value);
            }
         }
      }

   }

   @NotNull
   public final Pair<String[], String[]> readDefaults(@NotNull String domain, @NotNull String path) {
      Intrinsics.checkParameterIsNotNull(domain, "domain");
      Intrinsics.checkParameterIsNotNull(path, "path");
      boolean whiteList = false;
      ArrayList blackList = new ArrayList();
      boolean defaults = false;
      ArrayList whiteList = new ArrayList();
      IResource var10000 = Utils.get((IResourceManager)Utils.getResourceManager(), domain, path);
      List defaults = var10000 != null ? Utils.getLines(var10000) : null;
      if (defaults != null) {
         Iterable $this$map$iv = (Iterable)defaults;
         int $i$f$map = 0;
         Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));
         int $i$f$mapTo = 0;

         for(Object item$iv$iv : $this$map$iv) {
            String it = (String)item$iv$iv;
            int $i$a$-map-BlackWhiteListConfigOption$readDefaults$1 = 0;
            boolean var16 = false;
            if (it == null) {
               throw new TypeCastException("null cannot be cast to non-null type kotlin.CharSequence");
            }

            String var18 = StringsKt.trim((CharSequence)it).toString();
            destination$iv$iv.add(var18);
         }

         $this$map$iv = (Iterable)((List)destination$iv$iv);
         $i$f$map = 0;
         destination$iv$iv = (Collection)(new ArrayList());
         $i$f$mapTo = 0;

         for(Object element$iv$iv : $this$map$iv) {
            label63: {
               String it = (String)element$iv$iv;
               int $i$a$-filter-BlackWhiteListConfigOption$readDefaults$2 = 0;
               if (!StringsKt.startsWith$default(it, "//", false, 2, (Object)null)) {
                  CharSequence var15 = (CharSequence)it;
                  boolean var40 = false;
                  if (var15.length() > 0) {
                     var43 = true;
                     break label63;
                  }
               }

               var43 = false;
            }

            if (var43) {
               destination$iv$iv.add(element$iv$iv);
            }
         }

         $this$map$iv = (Iterable)((List)destination$iv$iv);
         $i$f$map = 0;

         for(Object element$iv : $this$map$iv) {
            String it = (String)element$iv;
            int $i$a$-forEach-BlackWhiteListConfigOption$readDefaults$3 = 0;
            if (StringsKt.startsWith$default(it, "-", false, 2, (Object)null)) {
               byte var37 = 1;
               boolean var39 = false;
               if (it == null) {
                  throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
               }

               String var44 = it.substring(var37);
               Intrinsics.checkExpressionValueIsNotNull(var44, "(this as java.lang.String).substring(startIndex)");
               String var41 = var44;
               blackList.add(var41);
            } else {
               whiteList.add(it);
            }
         }
      }

      Collection $this$toTypedArray$iv = (Collection)blackList;
      int $i$f$toTypedArray = 0;
      Object[] var45 = $this$toTypedArray$iv.toArray(new String[0]);
      if (var45 == null) {
         throw new TypeCastException("null cannot be cast to non-null type kotlin.Array<T>");
      } else {
         $this$toTypedArray$iv = (Collection)whiteList;
         Object[] var17 = var45;
         $i$f$toTypedArray = 0;
         var45 = $this$toTypedArray$iv.toArray(new String[0]);
         if (var45 == null) {
            throw new TypeCastException("null cannot be cast to non-null type kotlin.Array<T>");
         } else {
            Object[] var42 = var45;
            return TuplesKt.to(var17, var42);
         }
      }
   }

   @NotNull
   public final String getDomain() {
      return this.domain;
   }

   @NotNull
   public final String getPath() {
      return this.path;
   }

   public BlackWhiteListConfigOption(@NotNull String domain, @NotNull String path) {
      Intrinsics.checkParameterIsNotNull(domain, "domain");
      Intrinsics.checkParameterIsNotNull(path, "path");
      super();
      this.domain = domain;
      this.path = path;
      boolean var3 = false;
      List var5 = (List)(new ArrayList());
      this.blackList = var5;
      var3 = false;
      var5 = (List)(new ArrayList());
      this.whiteList = var5;
   }
}
