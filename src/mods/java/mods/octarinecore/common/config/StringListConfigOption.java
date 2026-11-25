package mods.octarinecore.common.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
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
   d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010!\n\u0002\b\b\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u0011\n\u0002\b\u0002\b&\u0018\u0000*\u0004\b\u0000\u0010\u00012\u00020\u0002B\u0015\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0004¢\u0006\u0002\u0010\u0006J(\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020\u00042\u0006\u0010 \u001a\u00020\u00042\u0006\u0010!\u001a\u00020\u0004H\u0016J\u0017\u0010\"\u001a\u0004\u0018\u00018\u00002\u0006\u0010#\u001a\u00020\u0004H&¢\u0006\u0002\u0010$J\b\u0010%\u001a\u00020\u001cH\u0016J!\u0010&\u001a\b\u0012\u0004\u0012\u00020\u00040'2\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0004¢\u0006\u0002\u0010(R\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u001a\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\f\u0010\rR\u0014\u0010\u000e\u001a\u00020\u000f8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0010\u0010\u0011R\u0017\u0010\u0012\u001a\b\u0012\u0004\u0012\u00028\u00000\u0013¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\rR\u001a\u0010\u0015\u001a\u00020\u000bX\u0086.¢\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u0017\"\u0004\b\u0018\u0010\u0019R\u0011\u0010\u0005\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\b¨\u0006)"},
   d2 = {"Lmods/octarinecore/common/config/StringListConfigOption;", "VALUE", "Lmods/octarinecore/common/config/ConfigPropertyBase;", "domain", "", "path", "(Ljava/lang/String;Ljava/lang/String;)V", "getDomain", "()Ljava/lang/String;", "guiProperties", "", "Lnet/minecraftforge/common/config/Property;", "getGuiProperties", "()Ljava/util/List;", "hasChanged", "", "getHasChanged", "()Z", "list", "", "getList", "listProperty", "getListProperty", "()Lnet/minecraftforge/common/config/Property;", "setListProperty", "(Lnet/minecraftforge/common/config/Property;)V", "getPath", "attach", "", "target", "Lnet/minecraftforge/common/config/Configuration;", "langPrefix", "categoryName", "propertyName", "convertValue", "line", "(Ljava/lang/String;)Ljava/lang/Object;", "read", "readDefaults", "", "(Ljava/lang/String;Ljava/lang/String;)[Ljava/lang/String;", "BetterFoliage-MC1.12"}
)
public abstract class StringListConfigOption<VALUE> extends ConfigPropertyBase {
   @NotNull
   private final List<VALUE> list;
   @NotNull
   public Property listProperty;
   @NotNull
   private final String domain;
   @NotNull
   private final String path;

   @NotNull
   public final List<VALUE> getList() {
      return this.list;
   }

   @NotNull
   public final Property getListProperty() {
      Property var10000 = this.listProperty;
      if (this.listProperty == null) {
         Intrinsics.throwUninitializedPropertyAccessException("listProperty");
      }

      return var10000;
   }

   public final void setListProperty(@NotNull Property var1) {
      Intrinsics.checkParameterIsNotNull(<set-?>, "<set-?>");
      this.listProperty = <set-?>;
   }

   public boolean getHasChanged() {
      Property var10000 = this.listProperty;
      if (this.listProperty == null) {
         Intrinsics.throwUninitializedPropertyAccessException("listProperty");
      }

      return var10000.hasChanged();
   }

   @NotNull
   public List<Property> getGuiProperties() {
      Property var10000 = this.listProperty;
      if (this.listProperty == null) {
         Intrinsics.throwUninitializedPropertyAccessException("listProperty");
      }

      return CollectionsKt.listOf(var10000);
   }

   public void attach(@NotNull Configuration target, @NotNull String langPrefix, @NotNull String categoryName, @NotNull String propertyName) {
      Intrinsics.checkParameterIsNotNull(target, "target");
      Intrinsics.checkParameterIsNotNull(langPrefix, "langPrefix");
      Intrinsics.checkParameterIsNotNull(categoryName, "categoryName");
      Intrinsics.checkParameterIsNotNull(propertyName, "propertyName");
      this.setLang((String)null);
      String[] defaults = this.readDefaults(this.domain, this.path);
      Property var10001 = target.get(categoryName, String.valueOf(propertyName), defaults);
      Intrinsics.checkExpressionValueIsNotNull(var10001, "target.get(categoryName,…propertyName}\", defaults)");
      this.listProperty = var10001;
      Property var10000 = this.listProperty;
      if (this.listProperty == null) {
         Intrinsics.throwUninitializedPropertyAccessException("listProperty");
      }

      var10000.setConfigEntryClass(NonVerboseArrayEntry.class);
      var10000 = this.listProperty;
      if (this.listProperty == null) {
         Intrinsics.throwUninitializedPropertyAccessException("listProperty");
      }

      StringBuilder var7 = (new StringBuilder()).append(langPrefix).append('.').append(categoryName).append('.');
      Property var10002 = this.listProperty;
      if (this.listProperty == null) {
         Intrinsics.throwUninitializedPropertyAccessException("listProperty");
      }

      var10000.setLanguageKey(var7.append(var10002.getName()).toString());
      this.read();
   }

   @Nullable
   public abstract VALUE convertValue(@NotNull String var1);

   public void read() {
      this.list.clear();
      Property var10000 = this.listProperty;
      if (this.listProperty == null) {
         Intrinsics.throwUninitializedPropertyAccessException("listProperty");
      }

      String[] var10 = var10000.getStringList();
      Intrinsics.checkExpressionValueIsNotNull(var10, "listProperty.stringList");
      Object[] $this$forEach$iv = var10;
      int $i$f$forEach = 0;

      for(Object element$iv : $this$forEach$iv) {
         int $i$a$-forEach-StringListConfigOption$read$1 = 0;
         Intrinsics.checkExpressionValueIsNotNull(element$iv, "line");
         Object value = this.convertValue((String)element$iv);
         if (value != null) {
            this.list.add(value);
         }
      }

   }

   @NotNull
   public final String[] readDefaults(@NotNull String domain, @NotNull String path) {
      Intrinsics.checkParameterIsNotNull(domain, "domain");
      Intrinsics.checkParameterIsNotNull(path, "path");
      boolean defaults = false;
      ArrayList list = new ArrayList();
      IResource var10000 = Utils.get((IResourceManager)Utils.getResourceManager(), domain, path);
      List defaults = var10000 != null ? Utils.getLines(var10000) : null;
      if (defaults != null) {
         Iterable $this$map$iv = (Iterable)defaults;
         int $i$f$map = 0;
         Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));
         int $i$f$mapTo = 0;

         for(Object item$iv$iv : $this$map$iv) {
            String it = (String)item$iv$iv;
            int $i$a$-map-StringListConfigOption$readDefaults$1 = 0;
            boolean var15 = false;
            if (it == null) {
               throw new TypeCastException("null cannot be cast to non-null type kotlin.CharSequence");
            }

            String var17 = StringsKt.trim((CharSequence)it).toString();
            destination$iv$iv.add(var17);
         }

         $this$map$iv = (Iterable)((List)destination$iv$iv);
         $i$f$map = 0;
         destination$iv$iv = (Collection)(new ArrayList());
         $i$f$mapTo = 0;

         for(Object element$iv$iv : $this$map$iv) {
            label50: {
               String it = (String)element$iv$iv;
               int $i$a$-filter-StringListConfigOption$readDefaults$2 = 0;
               if (!StringsKt.startsWith$default(it, "//", false, 2, (Object)null)) {
                  CharSequence var14 = (CharSequence)it;
                  boolean var34 = false;
                  if (var14.length() > 0) {
                     var35 = true;
                     break label50;
                  }
               }

               var35 = false;
            }

            if (var35) {
               destination$iv$iv.add(element$iv$iv);
            }
         }

         $this$map$iv = (Iterable)((List)destination$iv$iv);
         $i$f$map = 0;

         for(Object element$iv : $this$map$iv) {
            String it = (String)element$iv;
            int $i$a$-forEach-StringListConfigOption$readDefaults$3 = 0;
            list.add(it);
         }
      }

      Collection $this$toTypedArray$iv = (Collection)list;
      int $i$f$toTypedArray = 0;
      Object[] var36 = $this$toTypedArray$iv.toArray(new String[0]);
      if (var36 == null) {
         throw new TypeCastException("null cannot be cast to non-null type kotlin.Array<T>");
      } else {
         return (String[])var36;
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

   public StringListConfigOption(@NotNull String domain, @NotNull String path) {
      Intrinsics.checkParameterIsNotNull(domain, "domain");
      Intrinsics.checkParameterIsNotNull(path, "path");
      super();
      this.domain = domain;
      this.path = path;
      boolean var3 = false;
      List var5 = (List)(new ArrayList());
      this.list = var5;
   }
}
