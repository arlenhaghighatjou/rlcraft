package slimeknights.mantle.pulsar.config;

import java.io.File;
import java.util.Locale;
import javax.annotation.Nonnull;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Loader;
import slimeknights.mantle.pulsar.pulse.PulseMeta;

public class ForgeCFG implements IConfiguration {
   private Configuration config;
   private final String confPath;
   private final String description;

   public ForgeCFG(String confName, String description) {
      this.confPath = Loader.instance().getConfigDir().toString() + File.separator + confName + ".cfg";
      this.description = description.toLowerCase(Locale.US);
   }

   public void load() {
      this.config = new Configuration(new File(this.confPath), "1");
      this.config.load();
   }

   public boolean isModuleEnabled(@Nonnull PulseMeta meta) {
      Property prop = this.config.get(this.description, meta.getId(), meta.isDefaultEnabled(), meta.getDescription());
      prop.setRequiresMcRestart(true);
      return prop.getBoolean(meta.isEnabled());
   }

   public void flush() {
      if (this.config.hasChanged()) {
         this.config.save();
      }

   }

   public Configuration getConfig() {
      return this.config;
   }

   public ConfigCategory getCategory() {
      return this.config.getCategory(this.description);
   }
}
