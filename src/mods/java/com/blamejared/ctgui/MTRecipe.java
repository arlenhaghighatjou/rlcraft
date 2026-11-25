package com.blamejared.ctgui;

import com.blamejared.ctgui.api.GuiRegistry;
import com.blamejared.ctgui.commands.CommandCTGUI;
import com.blamejared.ctgui.proxy.CommonProxy;
import com.blamejared.ctgui.reference.Reference;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(
   modid = "ctgui",
   name = "CT-GUI",
   version = "1.0.0",
   clientSideOnly = true,
   dependencies = "required-after:crafttweaker;",
   acceptedMinecraftVersions = "[1.12, 1.13)"
)
public class MTRecipe {
   @Instance("ctgui")
   public static MTRecipe INSTANCE;
   @SidedProxy(
      clientSide = "com.blamejared.ctgui.proxy.ClientProxy",
      serverSide = "com.blamejared.ctgui.proxy.CommonProxy"
   )
   public static CommonProxy PROXY;

   public MTRecipe() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      PROXY.registerGuis();
      PROXY.registerEvents();
      GuiRegistry.registerGuis((String[])Reference.GUI_HANDLED.toArray(new String[0]));
   }

   @EventHandler
   public void serverStarting(FMLServerStartingEvent e) {
      e.registerServerCommand(new CommandCTGUI());
   }
}
