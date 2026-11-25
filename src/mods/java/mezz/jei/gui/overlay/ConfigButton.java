package mezz.jei.gui.overlay;

import java.util.List;
import mezz.jei.Internal;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.config.Config;
import mezz.jei.config.JEIModConfigGui;
import mezz.jei.config.KeyBindings;
import mezz.jei.gui.GuiHelper;
import mezz.jei.gui.elements.GuiIconToggleButton;
import mezz.jei.util.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextFormatting;
import net.lax1dude.eaglercraft.v1_8.Keyboard;

public class ConfigButton extends GuiIconToggleButton {
   private final IngredientListOverlay parent;

   public static ConfigButton create(IngredientListOverlay parent) {
      GuiHelper guiHelper = Internal.getHelpers().getGuiHelper();
      return new ConfigButton(guiHelper.getConfigButtonIcon(), guiHelper.getConfigButtonCheatIcon(), parent);
   }

   private ConfigButton(IDrawable disabledIcon, IDrawable enabledIcon, IngredientListOverlay parent) {
      super(disabledIcon, enabledIcon);
      this.parent = parent;
   }

   protected void getTooltips(List<String> tooltip) {
      tooltip.add(Translator.translateToLocal("jei.tooltip.config"));
      if (!Config.isOverlayEnabled()) {
         tooltip.add(TextFormatting.GOLD + Translator.translateToLocal("jei.tooltip.ingredient.list.disabled"));
         tooltip.add(TextFormatting.GOLD + Translator.translateToLocalFormatted("jei.tooltip.ingredient.list.disabled.how.to.fix", KeyBindings.toggleOverlay.getDisplayName()));
      } else if (!this.parent.isListDisplayed()) {
         tooltip.add(TextFormatting.GOLD + Translator.translateToLocal("jei.tooltip.not.enough.space"));
      }

      if (Config.isCheatItemsEnabled()) {
         tooltip.add(TextFormatting.RED + Translator.translateToLocal("jei.tooltip.cheat.mode.button.enabled"));
         KeyBinding toggleCheatMode = KeyBindings.toggleCheatMode;
         if (toggleCheatMode.getKeyCode() != 0) {
            tooltip.add(TextFormatting.RED + Translator.translateToLocalFormatted("jei.tooltip.cheat.mode.how.to.disable.hotkey", toggleCheatMode.getDisplayName()));
         } else {
            String controlKeyLocalization = Translator.translateToLocal(Minecraft.IS_RUNNING_ON_MAC ? "key.jei.ctrl.mac" : "key.jei.ctrl");
            tooltip.add(TextFormatting.RED + Translator.translateToLocalFormatted("jei.tooltip.cheat.mode.how.to.disable.no.hotkey", controlKeyLocalization));
         }
      }

   }

   protected boolean isIconToggledOn() {
      return Config.isCheatItemsEnabled();
   }

   protected boolean onMouseClicked(int mouseX, int mouseY) {
      if (!Config.isOverlayEnabled()) {
         return false;
      } else {
         if (!Keyboard.getEventKeyState() || Keyboard.getEventKey() != 29 && Keyboard.getEventKey() != 157) {
            Minecraft minecraft = Minecraft.getMinecraft();
            if (minecraft.currentScreen != null) {
               GuiScreen configScreen = new JEIModConfigGui(minecraft.currentScreen);
               this.parent.updateScreen(configScreen, false);
               minecraft.displayGuiScreen(configScreen);
            }
         } else {
            Config.toggleCheatItemsEnabled();
         }

         return true;
      }
   }
}
