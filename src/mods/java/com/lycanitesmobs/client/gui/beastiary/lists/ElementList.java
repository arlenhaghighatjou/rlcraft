package com.lycanitesmobs.client.gui.beastiary.lists;

import com.lycanitesmobs.client.gui.beastiary.ElementsBeastiaryScreen;
import com.lycanitesmobs.core.info.ElementInfo;
import com.lycanitesmobs.core.info.ElementManager;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.client.GuiScrollingList;

public class ElementList extends GuiScrollingList {
   private ElementsBeastiaryScreen parentGui;
   private Map<Integer, String> elementNames = new HashMap();

   public ElementList(ElementsBeastiaryScreen parentGui, int width, int height, int top, int bottom, int x) {
      super(Minecraft.getMinecraft(), width, height, top, bottom, x, 24, width, height);
      this.parentGui = parentGui;
      int i = 0;
      List<ElementInfo> elements = new ArrayList();
      elements.addAll(ElementManager.getInstance().elements.values());
      elements.sort(Comparator.comparing(ElementInfo::getTitle));

      for(ElementInfo elementInfo : elements) {
         this.elementNames.put(Integer.valueOf(i++), elementInfo.name);
      }

   }

   protected int getSize() {
      return this.elementNames.size();
   }

   protected void elementClicked(int index, boolean doubleClick) {
      this.selectedIndex = index;
      this.parentGui.elementInfo = ElementManager.getInstance().getElement((String)this.elementNames.get(Integer.valueOf(index)));
   }

   protected boolean isSelected(int index) {
      return this.selectedIndex == index;
   }

   protected void drawBackground() {
   }

   protected int getContentHeight() {
      return this.getSize() * this.slotHeight;
   }

   protected void drawSlot(int index, int boxRight, int boxTop, int boxBottom, Tessellator tessellator) {
      ElementInfo elementInfo = ElementManager.getInstance().getElement((String)this.elementNames.get(Integer.valueOf(index)));
      if (elementInfo != null) {
         this.parentGui.getFontRenderer().drawString(elementInfo.getTitle(), this.left + 4, boxTop + 4, 16777215);
      }
   }
}
