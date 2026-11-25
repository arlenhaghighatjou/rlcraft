package com.creativemd.creativecore.common.gui.controls.gui;

import java.util.ArrayList;
import java.util.List;

public class GuiComboBoxTranslated extends GuiComboBox {
   public String prefix;
   public List<String> translatedLines;

   public GuiComboBoxTranslated(String name, int x, int y, int width, String prefix, List<String> lines) {
      super(name, x, y, width, lines);
      this.prefix = prefix;
      List<String> translatedLines = new ArrayList();

      for(int i = 0; i < lines.size(); ++i) {
         translatedLines.add(this.getDisplay(i));
      }

      this.translatedLines = translatedLines;
   }

   public String getDisplay(int index) {
      return this.translatedLines != null ? (String)this.translatedLines.get(index) : translateOrDefault(this.prefix + "." + (String)this.lines.get(index), (String)this.lines.get(index));
   }

   protected GuiComboBoxExtension createBox() {
      return new GuiComboBoxExtension(this.name + "extension", this, this.posX, this.posY + this.height, this.width - this.getContentOffset() * 2, 100, this.translatedLines);
   }
}
