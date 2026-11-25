package meldexun.renderlib;

import java.util.function.DoubleUnaryOperator;
import meldexun.matrixutil.MathUtil;
import meldexun.renderlib.config.RenderLibConfig;
import meldexun.renderlib.util.GLUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "renderlib"
)
public class RenderLib {
   public static final String MODID = "renderlib";
   public static final Logger LOGGER = LogManager.getLogger("renderlib");
   public static boolean isFairyLightsInstalled;
   public static boolean isValkyrienSkiesInstalled;

   public RenderLib() {
   }

   @EventHandler
   public void onFMLConstructionEvent(FMLConstructionEvent event) {
      MathUtil.setSinFunc((a) -> {
         return (double)MathHelper.sin((float)a);
      });
      MathUtil.setCosFunc((a) -> {
         return (double)MathHelper.cos((float)a);
      });
      GLUtil.init();
      RenderLibConfig.onConfigChanged();
      MinecraftForge.EVENT_BUS.register(this);
   }

   @EventHandler
   public void onFMLPostInitializationEvent(FMLPostInitializationEvent event) {
      isFairyLightsInstalled = Loader.isModLoaded("fairylights");
      isValkyrienSkiesInstalled = Loader.isModLoaded("valkyrienskies");
   }

   @SubscribeEvent
   public void onConfigChangedEvent(OnConfigChangedEvent event) {
      if (event.getModID().equals("renderlib")) {
         ConfigManager.sync("renderlib", Type.INSTANCE);
         RenderLibConfig.onConfigChanged();
         GLUtil.updateDebugOutput();
      }

   }
}
