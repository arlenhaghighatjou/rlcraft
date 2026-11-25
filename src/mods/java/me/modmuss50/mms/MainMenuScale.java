package me.modmuss50.mms;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Pre;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.Display;

@Mod(
   modid = "mainmenuscale",
   name = "Main Menu Scale",
   clientSideOnly = true,
   dependencies = "required-after:reborncore",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class MainMenuScale {
   private boolean isMenuOpen = false;
   private int guiScale = -1;
   private int lastHeight = 0;

   public MainMenuScale() {
   }

   @EventHandler
   public void init(FMLPreInitializationEvent event) {
      MinecraftForge.EVENT_BUS.register(this);
   }

   @SubscribeEvent
   public void initGuiPre(Pre event) {
      if (!(event.getGui() instanceof GuiMainMenu) && !event.getGui().getClass().getName().equals("lumien.custommainmenu.gui.GuiCustom")) {
         this.closeMainMenu();
      } else {
         this.openMainMenu(event.getGui());
      }

   }

   public void openMainMenu(GuiScreen gui) {
      int height = Display.getHeight();
      if (!this.isMenuOpen || this.lastHeight != height) {
         this.lastHeight = height;
         if (!this.isMenuOpen) {
            this.guiScale = Minecraft.getMinecraft().gameSettings.guiScale;
         }

         this.isMenuOpen = true;
         int scale = Config.GUI_SCALE;
         if (Config.HIGH_RES_HEIGHT != -1 && height >= Config.HIGH_RES_HEIGHT) {
            scale = Config.HIGH_RES_SCALE;
         }

         Minecraft.getMinecraft().gameSettings.guiScale = scale;
         this.updateRes();
      }
   }

   public void closeMainMenu() {
      if (!this.isMenuOpen) {
         this.lastHeight = -1;
      } else {
         this.isMenuOpen = false;
         Minecraft.getMinecraft().gameSettings.guiScale = this.guiScale;
         this.updateRes();
      }
   }

   public void updateRes() {
      Minecraft minecraft = Minecraft.getMinecraft();
      ScaledResolution scaledresolution = new ScaledResolution(minecraft);
      int i = scaledresolution.getScaledWidth();
      int j = scaledresolution.getScaledHeight();
      minecraft.currentScreen.setWorldAndResolution(minecraft, i, j);
   }
}
