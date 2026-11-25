package slimeknights.mantle.config;

import com.google.common.reflect.TypeToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import slimeknights.mantle.configurate.commented.CommentedConfigurationNode;
import slimeknights.mantle.configurate.objectmapping.ObjectMappingException;

public abstract class AbstractConfig {
   List<AbstractConfigFile> configFileList = new ArrayList();
   private List<Function<?, ?>> configFiles = new ArrayList();

   public AbstractConfig() {
   }

   public void save() {
      this.configFiles.forEach((function) -> {
         function.apply((Object)null);
      });
   }

   public <T extends AbstractConfigFile> T load(T configFile, Class<T> clazz) {
      try {
         CommentedConfigurationNode node = configFile.load();
         T val = node.getValue(TypeToken.of(clazz), configFile);
         val.loader = configFile.loader;
         val.file = configFile.file;
         val.insertDefaults();
         val.setConfigVersion();
         this.configFileList.add(val);
         this.configFiles.add((configFile1) -> {
            this.save(val, clazz);
            return (T)true;
         });
         return val;
      } catch (ObjectMappingException | IOException var5) {
         var5.printStackTrace();
         return configFile;
      }
   }

   public <T extends AbstractConfigFile> void save(T configFile, Class<T> clazz) {
      if (configFile.needsSaving()) {
         try {
            CommentedConfigurationNode node = configFile.load();
            node.setValue(TypeToken.of(clazz), configFile);
            configFile.save(node);
         } catch (ObjectMappingException | IOException var4) {
            var4.printStackTrace();
         }
      }

      configFile.clearNeedsSaving();
   }

   public static boolean syncConfig(AbstractConfig param0, List<AbstractConfigFile> param1) {
      // $FF: Couldn't be decompiled
   }
}
