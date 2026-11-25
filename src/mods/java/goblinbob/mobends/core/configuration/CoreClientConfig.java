package goblinbob.mobends.core.configuration;

import goblinbob.mobends.core.bender.EntityBender;
import goblinbob.mobends.core.bender.EntityBenderRegistry;
import java.io.File;
import java.util.Collection;

public class CoreClientConfig extends CoreConfig {
   private static final String[] emptyStringList = new String[0];
   private static final String CATEGORY_GENERAL = "General";
   private static final String PROP_APPLIED_PACKS = "AppliedPacks";
   private static final String CATEGORY_ANIMATED = "Animated";
   public String[] appliedPackKeys = new String[0];

   public CoreClientConfig(File file) {
      super(file);
      this.load();
   }

   public void save() {
      for(EntityBender<?> entityBender : EntityBenderRegistry.instance.getRegistered()) {
         this.configuration.get("Animated", entityBender.getKey(), true).setValue(entityBender.isAnimated());
      }

      this.configuration.save();
   }

   public void load() {
      this.appliedPackKeys = this.configuration.get("General", "AppliedPacks", emptyStringList).getStringList();
   }

   public String[] getAppliedPacks() {
      return this.appliedPackKeys;
   }

   public void setAppliedPacks(String[] packNames) {
      this.appliedPackKeys = packNames;
      this.configuration.get("General", "AppliedPacks", emptyStringList).set(packNames);
   }

   public void setAppliedPacks(Collection<String> packNames) {
      this.setAppliedPacks((String[])packNames.toArray(new String[0]));
   }

   public boolean isEntityAnimated(String alterEntryKey) {
      return this.configuration.get("Animated", alterEntryKey, true).getBoolean();
   }
}
