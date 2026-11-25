package org.orecruncher.dsurround.client.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.config.GuiConfigEntries.IConfigEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;

@SideOnly(Side.CLIENT)
public class GuiFilteredConfigEntries extends GuiConfigEntries {
   private static final FontRenderer fontRenderer;
   private static final String searchLabel = I18n.format("fml.menu.mods.search", new Object[0]);
   private static final int searchLabelWidth;
   private static final int renderColor = 16768256;
   private GuiTextField search;
   private String lastFilterText = "";
   private List<IConfigEntry> filteredList;
   private final GuiConfig parent;

   public GuiFilteredConfigEntries(GuiConfig parent, Minecraft mc) {
      super(parent, mc);
      this.parent = parent;
      this.filteredList = this.listEntries;
   }

   public void initGui() {
      super.initGui();
      int w = 240;
      int y = (this.parent.width - 240 - searchLabelWidth - 10) / 2 + searchLabelWidth + 5;
      this.search = new GuiTextField(0, fontRenderer, y, this.top, 240, 14);
      this.search.setText(this.lastFilterText);
      this.search.setFocused(false);
      this.search.setCanLoseFocus(true);
      this.top = this.owningScreen.titleLine2 != null ? 53 : 43;
   }

   protected void filterText() {
      this.lastFilterText = this.search.getText();
      if (StringUtils.isEmpty(this.lastFilterText)) {
         this.filteredList = this.listEntries;
      } else {
         this.filteredList = new ArrayList();
         String filter = this.lastFilterText.toLowerCase();

         for(IConfigEntry e : this.listEntries) {
            IConfigElement ce = e.getConfigElement();
            String trans = I18n.format(ce.getLanguageKey(), new Object[0]);
            if (trans.equals(ce.getLanguageKey())) {
               trans = ce.getName();
            }

            if (trans.toLowerCase().contains(filter)) {
               this.filteredList.add(e);
            }
         }
      }

   }

   public boolean mouseClicked(int x, int y, int button) {
      boolean result = super.mouseClicked(x, y, button);
      result = result | this.search.mouseClicked(x, y, button);
      if (button == 1 && x >= this.search.x && x < this.search.x + this.search.width && y >= this.search.y && y < this.search.y + this.search.height) {
         this.search.setText("");
      }

      return result;
   }

   public void mouseClickedPassThru(int mouseX, int mouseY, int mouseEvent) {
      for(IConfigEntry entry : this.filteredList) {
         entry.mouseClicked(mouseX, mouseY, mouseEvent);
      }

   }

   public void keyTyped(char c, int keyCode) {
      for(IConfigEntry entry : this.filteredList) {
         entry.keyTyped(c, keyCode);
      }

      this.search.textboxKeyTyped(c, keyCode);
   }

   public void updateScreen() {
      for(IConfigEntry entry : this.filteredList) {
         entry.updateCursorCounter();
      }

      this.search.updateCursorCounter();
      if (!this.search.getText().equals(this.lastFilterText)) {
         this.filterText();
      }

   }

   public int getSize() {
      return this.filteredList.size();
   }

   public IConfigEntry getListEntry(int index) {
      return (IConfigEntry)this.filteredList.get(index);
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      super.drawScreen(mouseX, mouseY, partialTicks);
      int w = 240;
      int x = (this.parent.width - 240 - searchLabelWidth - 10) / 2;
      fontRenderer.drawString(searchLabel, x, this.top - 18, 16768256);
      this.search.drawTextBox();
   }

   public void drawScreenPost(int mouseX, int mouseY, float partialTicks) {
      for(IConfigEntry entry : this.filteredList) {
         entry.drawToolTip(mouseX, mouseY);
      }

   }

   static {
      fontRenderer = Minecraft.getMinecraft().fontRenderer;
      searchLabelWidth = fontRenderer.getStringWidth(searchLabel);
   }
}
