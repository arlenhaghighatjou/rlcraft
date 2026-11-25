package goblinbob.mobends.core.client.gui.packswindow;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class GuiTabNavigation {
   private int x;
   private int y;
   private List<GuiPackTab> tabs = new ArrayList();
   private GuiPackTab selectedTab = null;

   public GuiTabNavigation() {
   }

   public void initGui(int x, int y) {
      this.x = x;
      this.y = y;

      for(int i = 0; i < this.tabs.size(); ++i) {
         ((GuiPackTab)this.tabs.get(i)).initGui(x + i * 17, y);
      }

   }

   public GuiPackTab addTab(String tabName, int textureIndex) {
      GuiPackTab tab = new GuiPackTab(tabName, textureIndex);
      this.tabs.add(tab);
      return tab;
   }

   public void draw(int mouseX, int mouseY) {
      Minecraft mc = Minecraft.getMinecraft();

      for(GuiPackTab tab : this.tabs) {
         if (this.selectedTab != tab) {
            tab.draw(mouseX, mouseY);
         }
      }

      GuiPackTab selectedTab = this.getSelectedTab();
      if (selectedTab != null) {
         selectedTab.draw(mouseX, mouseY);
         mc.fontRenderer.drawStringWithShadow(I18n.format(selectedTab.titleKey, new Object[0]), (float)(this.x + 16 * this.tabs.size() + 10), (float)(this.y - 10), 16777215);
      }

   }

   public boolean mouseClicked(int mouseX, int mouseY, int button) {
      for(GuiPackTab tab : this.tabs) {
         if (tab.mouseClicked(mouseX, mouseY, button)) {
            this.selectTab(tab);
            return true;
         }
      }

      return false;
   }

   public void selectTab(int index) {
      this.selectTab((GuiPackTab)this.tabs.get(index));
   }

   public void selectTab(GuiPackTab tab) {
      this.selectedTab = tab;
      this.tabs.forEach((t) -> {
         t.setSelected(false);
      });
      this.selectedTab.setSelected(true);
   }

   public GuiPackTab getSelectedTab() {
      return this.selectedTab;
   }
}
