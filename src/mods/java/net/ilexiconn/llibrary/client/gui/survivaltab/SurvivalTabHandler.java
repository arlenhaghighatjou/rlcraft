package net.ilexiconn.llibrary.client.gui.survivaltab;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum SurvivalTabHandler {
   INSTANCE;

   private List<SurvivalTab> survivalTabList = new ArrayList();
   private int currentPage;

   private SurvivalTabHandler() {
   }

   public SurvivalTab create(String label, Class<? extends GuiContainer> container) {
      SurvivalTab survivalTab = new SurvivalTab(this.survivalTabList.size(), label, container);
      this.survivalTabList.add(survivalTab);
      return survivalTab;
   }

   public List<SurvivalTab> getSurvivalTabList() {
      return this.survivalTabList;
   }

   public int getCurrentPage() {
      return this.currentPage;
   }

   public void setCurrentPage(int page) {
      this.currentPage = page;
   }
}
