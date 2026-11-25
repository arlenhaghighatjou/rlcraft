package me.lucko.spark.forge;

import java.nio.file.Path;
import me.lucko.spark.forge.plugin.Forge1122ClientSparkPlugin;
import me.lucko.spark.forge.plugin.Forge1122ServerSparkPlugin;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(
   modid = "spark",
   name = "spark",
   version = "1.6.3",
   acceptableRemoteVersions = "*"
)
public class Forge1122SparkMod {
   private Path configDirectory;
   private Forge1122ServerSparkPlugin activeServerPlugin;

   public Forge1122SparkMod() {
   }

   public String getVersion() {
      return ((Mod)Forge1122SparkMod.class.getAnnotation(Mod.class)).version();
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent e) {
      this.configDirectory = e.getModConfigurationDirectory().toPath();
   }

   @EventHandler
   public void clientInit(FMLInitializationEvent e) {
      if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
         Forge1122ClientSparkPlugin.register(this);
      }

   }

   @EventHandler
   public void serverInit(FMLServerStartingEvent e) {
      this.activeServerPlugin = Forge1122ServerSparkPlugin.register(this, e);
   }

   @EventHandler
   public void serverStop(FMLServerStoppingEvent e) {
      if (this.activeServerPlugin != null) {
         this.activeServerPlugin.disable();
         this.activeServerPlugin = null;
      }

   }

   public Path getConfigDirectory() {
      if (this.configDirectory == null) {
         throw new IllegalStateException("Config directory not set");
      } else {
         return this.configDirectory;
      }
   }
}
