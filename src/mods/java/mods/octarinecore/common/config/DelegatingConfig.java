package mods.octarinecore.common.config;

import com.google.common.collect.LinkedListMultimap;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TypeCastException;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import mods.betterfoliage.loader.Refs;
import mods.octarinecore.Utils;
import mods.octarinecore.metaprog.MethodRef;
import mods.octarinecore.metaprog.Reflection;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.PostConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010 \n\u0002\b\u0005\b&\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0005J\u000e\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0006\u001a\u00020\u0007J)\u0010\u0016\u001a\u00020\u00152\u001e\u0010\u0017\u001a\u001a\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u0019\u0012\u0004\u0012\u00020\u00150\u0018H\u0086\bJ\u0010\u0010\u001a\u001a\u00020\u00152\u0006\u0010\u001b\u001a\u00020\u001cH\u0007J\u0014\u0010\u001d\u001a\u00020\u001e2\f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u00190 J\u0010\u0010!\u001a\u00020\u00152\u0006\u0010\u001b\u001a\u00020\u001cH&J\u0006\u0010\"\u001a\u00020\u0015J\u0017\u0010#\u001a\u0004\u0018\u00010\u0019*\u00020\u00012\u0006\u0010$\u001a\u00020\u0003H\u0086\u0002R\u001c\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u0017\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00110\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013\u00a8\u0006%"},
   d2 = {"Lmods/octarinecore/common/config/DelegatingConfig;", "", "modId", "", "langPrefix", "(Ljava/lang/String;Ljava/lang/String;)V", "config", "Lnet/minecraftforge/common/config/Configuration;", "getConfig", "()Lnet/minecraftforge/common/config/Configuration;", "setConfig", "(Lnet/minecraftforge/common/config/Configuration;)V", "getLangPrefix", "()Ljava/lang/String;", "getModId", "rootGuiElements", "", "Lnet/minecraftforge/fml/client/config/IConfigElement;", "getRootGuiElements", "()Ljava/util/List;", "attach", "", "forEachProperty", "init", "Lkotlin/Function3;", "Lmods/octarinecore/common/config/ConfigPropertyBase;", "handleConfigChange", "event", "Lnet/minecraftforge/fml/client/event/ConfigChangedEvent$PostConfigChangedEvent;", "hasChanged", "", "elements", "", "onChange", "save", "get", "name", "BetterFoliage-MC1.12"}
)
public abstract class DelegatingConfig {
   @Nullable
   private Configuration config;
   @NotNull
   private final List<IConfigElement> rootGuiElements;
   @NotNull
   private final String modId;
   @NotNull
   private final String langPrefix;

   @Nullable
   public final Configuration getConfig() {
      return this.config;
   }

   public final void setConfig(@Nullable Configuration var1) {
      this.config = <set-?>;
   }

   @NotNull
   public final List<IConfigElement> getRootGuiElements() {
      return this.rootGuiElements;
   }

   public final void attach(@NotNull Configuration config) {
      Intrinsics.checkParameterIsNotNull(config, "config");
      this.config = config;
      LinkedListMultimap subProperties = LinkedListMultimap.create();
      this.rootGuiElements.clear();
      int $i$f$forEachProperty = 0;
      Iterable $this$forEach$iv$iv = (Iterable)Reflection.reflectFieldsOfType(this, ConfigPropertyBase.class);
      int $i$f$forEach = 0;

      for(Object element$iv$iv : $this$forEach$iv$iv) {
         Pair property$iv = (Pair)element$iv$iv;
         int $i$a$-forEach-DelegatingConfig$forEachProperty$1 = 0;
         String var10001 = (String)property$iv.getFirst();
         Intrinsics.checkExpressionValueIsNotNull(var10001, "property.first");
         var10001 = StringsKt.split$default((CharSequence)var10001, new String[]{"$"}, false, 0, 6, (Object)null).get(0);
         Object var10002 = property$iv.getSecond();
         if (var10002 == null) {
            throw new TypeCastException("null cannot be cast to non-null type mods.octarinecore.common.config.ConfigPropertyBase");
         }

         ConfigPropertyBase property = (ConfigPropertyBase)var10002;
         String name = (String)var10001;
         String category = "global";
         int $i$a$-forEachProperty-DelegatingConfig$attach$1 = 0;
         var10001 = property.getLang();
         if (var10001 == null) {
            var10001 = category + '.' + name;
         }

         property.setLang(var10001);
         property.attach(config, this.langPrefix, category, name);
         Iterable $this$forEach$iv = (Iterable)property.getGuiProperties();
         int $i$f$forEach = 0;

         for(Object element$iv : $this$forEach$iv) {
            Property guiProperty = (Property)element$iv;
            int $i$a$-forEach-DelegatingConfig$attach$1$1 = 0;
            Class var10000 = property.getGuiClass();
            if (var10000 != null) {
               Class var21 = var10000;
               boolean var22 = false;
               boolean var23 = false;
               int $i$a$-let-DelegatingConfig$attach$1$1$1 = 0;
               guiProperty.setConfigEntryClass(var21);
            }

            if (Intrinsics.areEqual(category, "global")) {
               this.rootGuiElements.add(new ConfigElement(guiProperty));
            } else {
               subProperties.put(category, guiProperty.getName());
            }
         }
      }

      for(Pair category$iv : Reflection.getReflectNestedObjects(this)) {
         Iterable $this$forEach$iv$iv = (Iterable)Reflection.reflectFieldsOfType(category$iv.getSecond(), ConfigPropertyBase.class);
         int $i$f$forEach = 0;

         for(Object element$iv$iv : $this$forEach$iv$iv) {
            Pair property$iv = (Pair)element$iv$iv;
            int $i$a$-forEach-DelegatingConfig$forEachProperty$2 = 0;
            Class var56 = (Class)category$iv.getFirst();
            String var61 = (String)property$iv.getFirst();
            Intrinsics.checkExpressionValueIsNotNull(var61, "property.first");
            var61 = StringsKt.split$default((CharSequence)var61, new String[]{"$"}, false, 0, 6, (Object)null).get(0);
            Object var64 = property$iv.getSecond();
            if (var64 == null) {
               throw new TypeCastException("null cannot be cast to non-null type mods.octarinecore.common.config.ConfigPropertyBase");
            }

            ConfigPropertyBase var42 = (ConfigPropertyBase)var64;
            String var43 = (String)var61;
            String category = (String)var56;
            int $i$a$-forEachProperty-DelegatingConfig$attach$1 = 0;
            var61 = var42.getLang();
            if (var61 == null) {
               var61 = category + '.' + var43;
            }

            var42.setLang(var61);
            var42.attach(config, this.langPrefix, category, var43);
            Iterable $this$forEach$iv = (Iterable)var42.getGuiProperties();
            int $i$f$forEach = 0;

            for(Object element$iv : $this$forEach$iv) {
               Property guiProperty = (Property)element$iv;
               int $i$a$-forEach-DelegatingConfig$attach$1$1 = 0;
               var56 = var42.getGuiClass();
               if (var56 != null) {
                  Class var52 = var56;
                  boolean var53 = false;
                  boolean var54 = false;
                  int $i$a$-let-DelegatingConfig$attach$1$1$1 = 0;
                  guiProperty.setConfigEntryClass(var52);
               }

               if (Intrinsics.areEqual(category, "global")) {
                  this.rootGuiElements.add(new ConfigElement(guiProperty));
               } else {
                  subProperties.put(category, guiProperty.getName());
               }
            }
         }
      }

      for(String category : subProperties.keySet()) {
         ConfigCategory configCategory = config.getCategory(category);
         configCategory.setLanguageKey(this.langPrefix + '.' + category);
         configCategory.setPropertyOrder(subProperties.get(category));
         this.rootGuiElements.add(new ConfigElement(configCategory));
      }

      this.save();
      Set var58 = config.getCategoryNames();
      Intrinsics.checkExpressionValueIsNotNull(var58, "config.categoryNames");
      Iterable $this$forEach$iv = (Iterable)var58;
      $i$f$forEachProperty = 0;

      for(Object element$iv : $this$forEach$iv) {
         String it = (String)element$iv;
         int $i$a$-forEach-DelegatingConfig$attach$2 = 0;
         config.getCategory(it).setShowInGui(subProperties.keySet().contains(it));
      }

   }

   public final void forEachProperty(@NotNull Function3<? super String, ? super String, ? super ConfigPropertyBase, Unit> init) {
      int $i$f$forEachProperty = 0;
      Intrinsics.checkParameterIsNotNull(init, "init");
      Iterable $this$forEach$iv = (Iterable)Reflection.reflectFieldsOfType(this, ConfigPropertyBase.class);
      int $i$f$forEach = 0;

      for(Object element$iv : $this$forEach$iv) {
         Pair property = (Pair)element$iv;
         int $i$a$-forEach-DelegatingConfig$forEachProperty$1 = 0;
         Object var10002 = property.getFirst();
         Intrinsics.checkExpressionValueIsNotNull(var10002, "property.first");
         var10002 = StringsKt.split$default((CharSequence)var10002, new String[]{"$"}, false, 0, 6, (Object)null).get(0);
         Object var10003 = property.getSecond();
         if (var10003 == null) {
            throw new TypeCastException("null cannot be cast to non-null type mods.octarinecore.common.config.ConfigPropertyBase");
         }

         init.invoke("global", var10002, (ConfigPropertyBase)var10003);
      }

      for(Pair category : Reflection.getReflectNestedObjects(this)) {
         Iterable $this$forEach$iv = (Iterable)Reflection.reflectFieldsOfType(category.getSecond(), ConfigPropertyBase.class);
         int $i$f$forEach = 0;

         for(Object element$iv : $this$forEach$iv) {
            Pair property = (Pair)element$iv;
            int $i$a$-forEach-DelegatingConfig$forEachProperty$2 = 0;
            Object var10001 = category.getFirst();
            Object var18 = property.getFirst();
            Intrinsics.checkExpressionValueIsNotNull(var18, "property.first");
            var18 = StringsKt.split$default((CharSequence)var18, new String[]{"$"}, false, 0, 6, (Object)null).get(0);
            Object var20 = property.getSecond();
            if (var20 == null) {
               throw new TypeCastException("null cannot be cast to non-null type mods.octarinecore.common.config.ConfigPropertyBase");
            }

            init.invoke(var10001, var18, (ConfigPropertyBase)var20);
         }
      }

   }

   public final void save() {
      if (this.config != null ? this.config.hasChanged() : false) {
         Configuration var10000 = this.config;
         if (this.config == null) {
            Intrinsics.throwNpe();
         }

         var10000.save();
      }

   }

   public final boolean hasChanged(@NotNull List<? extends ConfigPropertyBase> elements) {
      Intrinsics.checkParameterIsNotNull(elements, "elements");
      Iterable $this$forEach$iv = (Iterable)Reflection.getReflectNestedObjects(this);
      int $i$f$forEach = 0;

      for(Object element$iv : $this$forEach$iv) {
         Pair category = (Pair)element$iv;
         int $i$a$-forEach-DelegatingConfig$hasChanged$1 = 0;
         if (CollectionsKt.contains((Iterable)elements, category.getSecond())) {
            boolean var32;
            label68: {
               Configuration var10000 = this.config;
               if (this.config != null) {
                  ConfigCategory var31 = var10000.getCategory((String)category.getFirst());
                  if (var31 != null) {
                     var32 = var31.hasChanged();
                     break label68;
                  }
               }

               var32 = false;
            }

            if (var32) {
               return true;
            }
         }
      }

      $i$f$forEach = 0;
      Iterable $this$forEach$iv$iv = (Iterable)Reflection.reflectFieldsOfType(this, ConfigPropertyBase.class);
      int $i$f$forEach = 0;

      for(Object element$iv$iv : $this$forEach$iv$iv) {
         Pair property$iv = (Pair)element$iv$iv;
         int $i$a$-forEach-DelegatingConfig$forEachProperty$1 = 0;
         Object var10001 = property$iv.getFirst();
         Intrinsics.checkExpressionValueIsNotNull(var10001, "property.first");
         var10001 = StringsKt.split$default((CharSequence)var10001, new String[]{"$"}, false, 0, 6, (Object)null).get(0);
         Object var10002 = property$iv.getSecond();
         if (var10002 == null) {
            throw new TypeCastException("null cannot be cast to non-null type mods.octarinecore.common.config.ConfigPropertyBase");
         }

         ConfigPropertyBase property = (ConfigPropertyBase)var10002;
         String name = (String)var10001;
         String category = "global";
         int $i$a$-forEachProperty-DelegatingConfig$hasChanged$2 = 0;
         if (elements.contains(property) && property.getHasChanged()) {
            return true;
         }
      }

      label53:
      for(Pair category$iv : Reflection.getReflectNestedObjects(this)) {
         Iterable $this$forEach$iv$iv = (Iterable)Reflection.reflectFieldsOfType(category$iv.getSecond(), ConfigPropertyBase.class);
         int $i$f$forEach = 0;
         Iterator var25 = $this$forEach$iv$iv.iterator();

         while(true) {
            if (!var25.hasNext()) {
               continue label53;
            }

            Object element$iv$iv = var25.next();
            Pair property$iv = (Pair)element$iv$iv;
            int $i$a$-forEach-DelegatingConfig$forEachProperty$2 = 0;
            Object var33 = category$iv.getFirst();
            Object var35 = property$iv.getFirst();
            Intrinsics.checkExpressionValueIsNotNull(var35, "property.first");
            var35 = StringsKt.split$default((CharSequence)var35, new String[]{"$"}, false, 0, 6, (Object)null).get(0);
            Object var37 = property$iv.getSecond();
            if (var37 == null) {
               throw new TypeCastException("null cannot be cast to non-null type mods.octarinecore.common.config.ConfigPropertyBase");
            }

            ConfigPropertyBase var27 = (ConfigPropertyBase)var37;
            String var28 = (String)var35;
            String category = (String)var33;
            int $i$a$-forEachProperty-DelegatingConfig$hasChanged$2 = 0;
            if (elements.contains(var27) && var27.getHasChanged()) {
               break;
            }
         }

         return true;
      }

      return false;
   }

   public abstract void onChange(@NotNull PostConfigChangedEvent var1);

   @SubscribeEvent
   public final void handleConfigChange(@NotNull PostConfigChangedEvent event) {
      Intrinsics.checkParameterIsNotNull(event, "event");
      if (Intrinsics.areEqual(event.getModID(), this.modId)) {
         int $i$f$forEachProperty = 0;
         Iterable $this$forEach$iv$iv = (Iterable)Reflection.reflectFieldsOfType(this, ConfigPropertyBase.class);
         int $i$f$forEach = 0;

         for(Object element$iv$iv : $this$forEach$iv$iv) {
            Pair property$iv = (Pair)element$iv$iv;
            int $i$a$-forEach-DelegatingConfig$forEachProperty$1 = 0;
            Object var10001 = property$iv.getFirst();
            Intrinsics.checkExpressionValueIsNotNull(var10001, "property.first");
            var10001 = StringsKt.split$default((CharSequence)var10001, new String[]{"$"}, false, 0, 6, (Object)null).get(0);
            Object var10002 = property$iv.getSecond();
            if (var10002 == null) {
               throw new TypeCastException("null cannot be cast to non-null type mods.octarinecore.common.config.ConfigPropertyBase");
            }

            ConfigPropertyBase prop = (ConfigPropertyBase)var10002;
            String n = (String)var10001;
            String c = "global";
            int $i$a$-forEachProperty-DelegatingConfig$handleConfigChange$1 = 0;
            prop.read();
         }

         for(Pair category$iv : Reflection.getReflectNestedObjects(this)) {
            Iterable $this$forEach$iv$iv = (Iterable)Reflection.reflectFieldsOfType(category$iv.getSecond(), ConfigPropertyBase.class);
            int $i$f$forEach = 0;

            for(Object element$iv$iv : $this$forEach$iv$iv) {
               Pair property$iv = (Pair)element$iv$iv;
               int $i$a$-forEach-DelegatingConfig$forEachProperty$2 = 0;
               Object var10000 = category$iv.getFirst();
               Object var28 = property$iv.getFirst();
               Intrinsics.checkExpressionValueIsNotNull(var28, "property.first");
               var28 = StringsKt.split$default((CharSequence)var28, new String[]{"$"}, false, 0, 6, (Object)null).get(0);
               Object var31 = property$iv.getSecond();
               if (var31 == null) {
                  throw new TypeCastException("null cannot be cast to non-null type mods.octarinecore.common.config.ConfigPropertyBase");
               }

               ConfigPropertyBase var22 = (ConfigPropertyBase)var31;
               String var23 = (String)var28;
               String c = (String)var10000;
               int $i$a$-forEachProperty-DelegatingConfig$handleConfigChange$1 = 0;
               var22.read();
            }
         }

         this.onChange(event);
         this.save();
         MethodRef var26 = Refs.INSTANCE.getResetChangedState();
         Configuration var30 = this.config;
         if (this.config == null) {
            Intrinsics.throwNpe();
         }

         var26.invoke(var30);
      }

   }

   @Nullable
   public final ConfigPropertyBase get(@NotNull Object $this$get, @NotNull String name) {
      // $FF: Couldn't be decompiled
   }

   @NotNull
   public final String getModId() {
      return this.modId;
   }

   @NotNull
   public final String getLangPrefix() {
      return this.langPrefix;
   }

   public DelegatingConfig(@NotNull String modId, @NotNull String langPrefix) {
      Intrinsics.checkParameterIsNotNull(modId, "modId");
      Intrinsics.checkParameterIsNotNull(langPrefix, "langPrefix");
      super();
      this.modId = modId;
      this.langPrefix = langPrefix;
      MinecraftForge.EVENT_BUS.register(this);
      boolean var3 = false;
      List var5 = (List)(new ArrayList());
      this.rootGuiElements = var5;
   }
}
