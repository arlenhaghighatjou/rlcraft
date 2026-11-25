package invtweaks;

import java.util.LinkedList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Keyboard;

public class InvTweaksGuiShortcutsHelp extends GuiScreen {
   private static final int ID_DONE = 0;
   private InvTweaksObfuscation obf;
   private GuiScreen parentScreen;
   private InvTweaksConfig config;

   public InvTweaksGuiShortcutsHelp(Minecraft mc_, GuiScreen parentScreen_, InvTweaksConfig config_) {
      this.obf = new InvTweaksObfuscation(mc_);
      this.parentScreen = parentScreen_;
      this.config = config_;
   }

   public void initGui() {
      List<GuiButton> controlList = new LinkedList();
      controlList.add(new GuiButton(0, this.width / 2 - 100, this.height / 6 + 168, "Done"));
      this.buttonList = controlList;
   }

   public void drawScreen(int i, int j, float f) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.obf.getFontRenderer(), "WARNING: Since 1.3.1, shortcuts won't work as expected. Looking for a workaround...", this.width / 2, 5, 16711680);
      this.drawCenteredString(this.obf.getFontRenderer(), I18n.format("invtweaks.help.shortcuts.title", new Object[0]), this.width / 2, 20, 16777215);
      String clickLabel = I18n.format("invtweaks.help.shortcuts.click", new Object[0]);
      int y = this.height / 6 - 2;
      this.drawShortcutLine(I18n.format("invtweaks.help.shortcuts.onestack", new Object[0]), "LSHIFT " + I18n.format("invtweaks.help.shortcuts.or", new Object[0]) + " RSHIFT + " + clickLabel, 16776960, y);
      y = y + 12;
      this.drawShortcutLine("", this.buildUpOrDownLabel("shortcutKeyToUpperSection", this.obf.getKeyBindingForwardKeyCode(), I18n.format("invtweaks.help.shortcuts.forward", new Object[0])) + " + " + clickLabel, 16776960, y);
      y = y + 12;
      this.drawShortcutLine("", this.buildUpOrDownLabel("shortcutKeyToLowerSection", this.obf.getKeyBindingBackKeyCode(), I18n.format("invtweaks.help.shortcuts.backwards", new Object[0])) + " + " + clickLabel, 16776960, y);
      y = y + 12;
      this.drawShortcutLine(I18n.format("invtweaks.help.shortcuts.oneitem", new Object[0]), this.config.getProperty("shortcutKeyOneItem") + " + " + clickLabel, 16776960, y);
      y = y + 12;
      this.drawShortcutLine(I18n.format("invtweaks.help.shortcuts.allitems", new Object[0]), this.config.getProperty("shortcutKeyAllItems") + " + " + clickLabel, 16776960, y);
      y = y + 12;
      this.drawShortcutLine(I18n.format("invtweaks.help.shortcuts.everything", new Object[0]), this.config.getProperty("shortcutKeyEverything") + " + " + clickLabel, 16776960, y);
      y = y + 19;
      this.drawShortcutLine(I18n.format("invtweaks.help.shortcuts.hotbar", new Object[0]), "0-9 + " + clickLabel, 65331, y);
      y = y + 12;
      this.drawShortcutLine(I18n.format("invtweaks.help.shortcuts.emptyslot", new Object[0]), I18n.format("invtweaks.help.shortcuts.rightclick", new Object[0]), 65331, y);
      y = y + 12;
      this.drawShortcutLine(I18n.format("invtweaks.help.shortcuts.drop", new Object[0]), this.config.getProperty("shortcutKeyDrop") + " + " + clickLabel, 65331, y);
      y = y + 19;
      this.drawShortcutLine(I18n.format("invtweaks.help.shortcuts.craftall", new Object[0]), "LSHIFT, RSHIFT + " + clickLabel, 16746496, y);
      y = y + 12;
      this.drawShortcutLine(I18n.format("invtweaks.help.shortcuts.craftone", new Object[0]), this.config.getProperty("shortcutKeyOneItem") + " + " + clickLabel, 16746496, y);
      y = y + 19;
      String sortKeyName = this.getKeyName(this.config.getSortKeyCode(), "(Sort Key)");
      this.drawShortcutLine(I18n.format("invtweaks.help.shortcuts.selectconfig", new Object[0]), "0-9 + " + sortKeyName, 8978431, y);
      super.drawScreen(i, j, f);
   }

   protected void actionPerformed(@NotNull GuiButton guibutton) {
      switch(guibutton.id) {
      case 0:
         this.obf.displayGuiScreen(this.parentScreen);
      default:
      }
   }

   protected void keyTyped(char c, int keyCode) {
      if (keyCode == 1) {
         this.obf.displayGuiScreen(this.parentScreen);
      }

   }

   private String buildUpOrDownLabel(@NotNull String shortcutProp, int keyCode, String defaultKeyName) {
      String shortcutLabel = this.config.getProperty(shortcutProp);
      String keyLabel = this.getKeyName(keyCode, defaultKeyName);
      return keyLabel.equals(shortcutLabel) ? keyLabel : keyLabel + "/" + shortcutLabel;
   }

   protected String getKeyName(int keyCode, String defaultValue) {
      try {
         return Keyboard.getKeyName(keyCode);
      } catch (Exception var4) {
         return defaultValue;
      }
   }

   private void drawShortcutLine(@NotNull String label, @Nullable String value, int color, int y) {
      this.drawString(this.obf.getFontRenderer(), label, 30, y, -1);
      if (value != null) {
         this.drawString(this.obf.getFontRenderer(), value.contains("DEFAULT") ? "-" : value.replaceAll(", ", " " + I18n.format("invtweaks.help.shortcuts.or", new Object[0]) + " "), this.width / 2 - 30, y, color);
      }

   }
}
