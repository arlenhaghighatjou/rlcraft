package com.endertech.minecraft.forge;

import com.endertech.minecraft.forge.coremod.CorePlugin;
import com.endertech.minecraft.forge.coremod.names.NotchMcp;
import com.endertech.minecraft.forge.messages.TileSharedDataMsg;
import com.endertech.minecraft.forge.messages.UpdateBlockMsg;
import com.endertech.minecraft.forge.messages.UsingItemMsg;
import com.endertech.minecraft.forge.player.MotionController;
import com.endertech.minecraft.forge.versions.Dependencies;
import com.endertech.minecraft.forge.world.Wind;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.SystemUtils;

@Mod(
   modid = "forgeendertech",
   name = "Forge Endertech",
   version = "1.12.2-4.5.6.1",
   guiFactory = "com.endertech.minecraft.forge.Config$Factory",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class ForgeMain extends ForgeMod {
   public static final String ID = "forgeendertech";
   public static final String NAME = "Forge Endertech";
   public static final String GUI_FACTORY = "com.endertech.minecraft.forge.Config$Factory";
   public static final String ACCEPTED = "[1.12.2]";
   public static final String DEPENDENCIES = "required-after:forgeendertech@[1.12.2-4.5.6.1,)";
   public static final boolean DEVELOPER_MODE = "forgeendertech".indexOf(SystemUtils.getUserHome().getName().toLowerCase()) == 5;
   public static final boolean DEVELOPER_ENVIRONMENT = !CorePlugin.obfuscatedCode() && DEVELOPER_MODE;
   @Instance("forgeendertech")
   public static ForgeMain instance;
   static boolean debug = false;

   public ForgeMain() {
   }

   public static boolean isDebugMode() {
      return debug || DEVELOPER_MODE;
   }

   public static void debugMsg(String msg) {
      if (isDebugMode()) {
         System.out.println(msg);
      }

   }

   public static void developMsg(String msg) {
      if (DEVELOPER_ENVIRONMENT) {
         debugMsg(msg);
      }

   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      this.setConfig(new Config("forgeendertech", event));
      this.getRegistrator().registerNetMessageHandledOnBothSides(MotionController.MotionMsg.class, MotionController.MotionMsg.Handler.class);
      this.getRegistrator().registerNetMessageHandledOnServer(UsingItemMsg.class, UsingItemMsg.Handler.class);
      this.getRegistrator().registerNetMessageHandledOnClient(UpdateBlockMsg.class, UpdateBlockMsg.Handler.class);
      this.getRegistrator().registerNetMessageHandledOnBothSides(TileSharedDataMsg.class, TileSharedDataMsg.Handler.class);
      this.getRegistrator().registerNetMessageHandledOnClient(Wind.WindMsg.class, Wind.WindMsg.Handler.class);
      super.preInit(event);
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      Wind.Weather2.initialize();
      super.init(event);
   }

   @EventHandler
   @SideOnly(Side.CLIENT)
   public void initClient(FMLInitializationEvent event) {
      Dependencies.checkIntegrityOnce();
      super.initClient(event);
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
      NotchMcp.clearLists();
      super.postInit(event);
   }

   public String getId() {
      return "forgeendertech";
   }

   public String getName() {
      return "Forge Endertech";
   }

   public ForgeMod getInstance() {
      return instance;
   }
}
