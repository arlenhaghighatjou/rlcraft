package vazkii.quark.base.client.gui.config;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.client.gui.GuiButtonColor;
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.base.module.ModuleLoader;

public final class ConfigEvents {
   public ConfigEvents() {
   }

   @SubscribeEvent
   public static void onGuiOpen(GuiOpenEvent event) {
      if (ModuleLoader.firstLoad && event.getGui() instanceof GuiMainMenu) {
         ModuleLoader.firstLoad = false;
         event.setGui(new GuiConfigFirstLoad(event.getGui()));
      }

   }

   @SubscribeEvent
   public static void onGuiInit(InitGuiEvent event) {
      GuiScreen gui = event.getGui();
      if (GlobalConfig.enableQButton && (gui instanceof GuiMainMenu || gui instanceof GuiIngameMenu)) {
         ImmutableSet<String> targets = GlobalConfig.qButtonOnRight ? ImmutableSet.of(I18n.format("fml.menu.modoptions", new Object[0]), I18n.format("menu.online", new Object[0]).replace("Minecraft", "").trim()) : ImmutableSet.of(I18n.format("menu.options", new Object[0]), I18n.format("fml.menu.mods", new Object[0]));
         List<GuiButton> buttons = event.getButtonList();

         for(GuiButton b : buttons) {
            if (targets.contains(b.displayString)) {
               GuiButton qButton = new GuiButtonQ(b.x + (GlobalConfig.qButtonOnRight ? 103 : -24), b.y);
               buttons.add(qButton);
               return;
            }
         }
      }

   }

   @SubscribeEvent
   public static void onButtonClick(ActionPerformedEvent event) {
      if (event.getButton() instanceof GuiButtonColor) {
         Minecraft.getMinecraft().displayGuiScreen(new GuiConfigRoot(event.getGui()));
      }

   }
}
