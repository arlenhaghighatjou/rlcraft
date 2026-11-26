package net.silentchaos512.scalinghealth;

import net.minecraft.world.storage.MapStorage;
import net.silentchaos512.scalinghealth.world.ScalingHealthSavedData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.function.Supplier;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.silentchaos512.lib.base.IModBase;
import net.silentchaos512.lib.registry.SRegistry;
import net.silentchaos512.lib.util.I18nHelper;
import net.silentchaos512.lib.util.LogHelper;
import net.silentchaos512.scalinghealth.command.CommandRecalculate;
import net.silentchaos512.scalinghealth.command.CommandScalingHealth;
import net.silentchaos512.scalinghealth.init.ModItems;
import net.silentchaos512.scalinghealth.proxy.ScalingHealthCommonProxy;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "scalinghealth",
   name = "Scaling Health",
   version = "1.3.37",
   dependencies = "required-after:forge@[14.23.5.2779,);required-after:silentlib@[3.0.13,);after:morpheus",
   guiFactory = "net.silentchaos512.scalinghealth.gui.GuiFactoryScalingHealth",
   acceptedMinecraftVersions = "[1.12.2]"
)
@MethodsReturnNonnullByDefault
public class ScalingHealth implements IModBase {
   public static final String MOD_ID_OLD = "ScalingHealth";
   public static final String MOD_ID_LOWER = "scalinghealth";
   public static final String MOD_NAME = "Scaling Health";
   public static final String VERSION = "1.3.37";
   public static final String VERSION_SILENTLIB = "3.0.13";
   public static final int BUILD_NUM = 147;
   public static final String DEPENDENCIES = "required-after:forge@[14.23.5.2779,);required-after:silentlib@[3.0.13,);after:morpheus";
   public static final String RESOURCE_PREFIX = "scalinghealth:";
   public static final String GAME_RULE_DIFFICULTY = "ScalingHealthDifficulty";
   public static final EaglercraftRandom random = new EaglercraftRandom();
   /** @deprecated */
   @Deprecated
   public static final LogHelper logHelper = new LogHelper("Scaling Health", 147);
   public static final Logger LOGGER = LogManager.getLogger("Scaling Health");
   public static final I18nHelper i18n = new I18nHelper("scalinghealth", logHelper, true);
   public static SRegistry registry = new SRegistry();
   public static CreativeTabs creativeTab = registry.makeCreativeTab("scalinghealth", () -> {
      return new ItemStack(ModItems.heart);
   });
   public static ScalingHealth instance;
   public static ScalingHealthCommonProxy proxy;

   public ScalingHealth() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new net.silentchaos512.scalinghealth.proxy.ScalingHealthClientProxy() : new net.silentchaos512.scalinghealth.proxy.ScalingHealthCommonProxy();
         net.silentchaos512.scalinghealth.event.PlayerBonusRegenHandler.register(MinecraftForge.EVENT_BUS);
         if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            net.silentchaos512.scalinghealth.event.ScalingHealthClientEvents.register(MinecraftForge.EVENT_BUS);
         }
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         this.postInit((FMLPostInitializationEvent)event);
      } else if (event instanceof FMLServerStartingEvent) {
         this.onServerLoad((FMLServerStartingEvent)event);
      } else if (event instanceof FMLServerStartedEvent) {
         this.onServerStarted((FMLServerStartedEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      registry.setMod(this);
      MapStorage.storageProviders.put(ScalingHealthSavedData.class, ScalingHealthSavedData::new);
      registry.getRecipeMaker().setJsonHellMode(this.isDevBuild());
      proxy.preInit(registry, event);
   }

   public void init(FMLInitializationEvent event) {
      proxy.init(registry, event);
   }

   public void postInit(FMLPostInitializationEvent event) {
      proxy.postInit(registry, event);
   }

   public void onServerLoad(FMLServerStartingEvent event) {
      event.registerServerCommand(new CommandScalingHealth());
      event.registerServerCommand(new CommandRecalculate());
   }

   public void onServerStarted(FMLServerStartedEvent event) {
      MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
      if (server != null) {
         server.worlds[0].getGameRules().setOrCreateGameRule("ScalingHealthDifficulty", "true");
      }

   }

   public String getModId() {
      return "scalinghealth";
   }

   public String getModName() {
      return "Scaling Health";
   }

   public String getVersion() {
      return "1.3.37";
   }

   public int getBuildNum() {
      return 147;
   }
}
