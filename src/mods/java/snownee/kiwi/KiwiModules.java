package snownee.kiwi;

import com.google.common.collect.ImmutableList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import snownee.kiwi.test.TestModule;

public final class KiwiModules {
   public static final List<KiwiModules.Info> MODULES = ImmutableList.of(new KiwiModules.Info("kiwi", null, null, true, true, TestModule::new, () -> new Object[]{TestModule.FIRST_ITEM, TestModule.MISC, TestModule.FIRST_BLOCK, TestModule.FIRST_POTION}));
   static final Map<IModule, Supplier<Object[]>> ENTRIES = new IdentityHashMap();

   private KiwiModules() {
   }

   public static final class Info {
      public final String modid;
      public final String name;
      public final String dependency;
      public final boolean optional;
      public final boolean disabledByDefault;
      public final Supplier<IModule> factory;
      public final Supplier<Object[]> entries;

      public Info(String modid, String name, String dependency, boolean optional, boolean disabledByDefault, Supplier<IModule> factory, Supplier<Object[]> entries) {
         this.modid = modid;
         this.name = name;
         this.dependency = dependency;
         this.optional = optional;
         this.disabledByDefault = disabledByDefault;
         this.factory = factory;
         this.entries = entries;
      }
   }
}
