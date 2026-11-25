package io.github.xcube16.iseedragons.asm;

import io.github.xcube16.iseedragons.ISD;
import java.nio.file.Paths;
import javax.annotation.Nullable;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ASMConfig {
   private final Configuration config;

   public ASMConfig(String modName) {
      this.config = new Configuration(Paths.get("config").resolve(modName + ".cfg").toAbsolutePath().toFile(), true);
      this.config.load();
   }

   public boolean getBoolean(String id, boolean _default) {
      Property prop = this.getProperty(id);
      return prop == null ? _default : prop.getBoolean(_default);
   }

   @Nullable
   public <T> Property getProperty(String s) {
      int i = s.lastIndexOf(".");
      if (i < 0) {
         ISD.logger.warn("malformed config key: " + s);
      }

      return this.config.getCategory(s.substring(0, i)).get(s.substring(i + 1));
   }
}
