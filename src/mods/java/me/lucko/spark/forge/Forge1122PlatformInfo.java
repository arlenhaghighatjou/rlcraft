package me.lucko.spark.forge;

import me.lucko.spark.common.platform.AbstractPlatformInfo;
import me.lucko.spark.common.platform.PlatformInfo;
import net.minecraftforge.common.ForgeVersion;

public class Forge1122PlatformInfo extends AbstractPlatformInfo {
   private final PlatformInfo.Type type;

   public Forge1122PlatformInfo(PlatformInfo.Type type) {
      this.type = type;
   }

   public PlatformInfo.Type getType() {
      return this.type;
   }

   public String getName() {
      return "Forge";
   }

   public String getVersion() {
      return ForgeVersion.getVersion();
   }

   public String getMinecraftVersion() {
      return "1.12.2";
   }
}
