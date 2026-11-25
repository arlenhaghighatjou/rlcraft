package net.silentchaos512.utils.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.ConfigSpec.CorrectionListener;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.io.ParsingMode;
import com.electronwill.nightconfig.core.io.WritingMode;
import java.nio.file.Path;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import org.apache.logging.log4j.util.Supplier;

public class ConfigSpecWrapper {
   final CommentedConfig config;
   final ConfigSpec spec;

   public ConfigSpecWrapper(CommentedConfig config, ConfigSpec spec) {
      this.config = config;
      this.spec = spec;
      this.load();
   }

   public static ConfigSpecWrapper create(Path filePath) {
      return new ConfigSpecWrapper((CommentedConfig)CommentedFileConfig.builder(filePath.toFile()).preserveInsertionOrder().parsingMode(ParsingMode.REPLACE).writingMode(WritingMode.REPLACE).autoreload().build(), new ConfigSpec());
   }

   public ConfigValue.Builder builder(String path) {
      return ConfigValue.builder(this, path);
   }

   public void comment(String path, String comment) {
      this.config.setComment(path, comment);
   }

   public void comment(String path, String firstLine, String... rest) {
      StringBuilder builder = new StringBuilder(firstLine);

      for(String str : rest) {
         builder.append("\n").append(str);
      }

      this.comment(path, builder.toString());
   }

   public void validate() {
      if (!this.spec.isCorrect(this.config)) {
         String configName = this.getConfigName();
         LogManager.getLogger().warn("Correcting config file {}", configName);
         this.spec.correct(this.config, (action, path, incorrectValue, correctedValue) -> {
            LogManager.getLogger().warn("  {}: {} -> {}", path, incorrectValue, correctedValue);
         });
         this.save();
      }

   }

   private String getConfigName() {
      return this.config instanceof FileConfig ? ((FileConfig)this.config).getNioPath().toString() : this.config.toString();
   }

   public final void load() {
      if (this.config instanceof FileConfig) {
         LogManager.getLogger().info("Loading config file {}", new Supplier[]{this::getConfigName});
         ((FileConfig)this.config).load();
      }

   }

   public final void save() {
      if (this.config instanceof FileConfig) {
         LogManager.getLogger().info("Saving config file {}", new Supplier[]{this::getConfigName});
         ((FileConfig)this.config).save();
      }

   }

   public CommentedConfig getConfig() {
      return this.config;
   }

   public ConfigSpec getSpec() {
      return this.spec;
   }
}
