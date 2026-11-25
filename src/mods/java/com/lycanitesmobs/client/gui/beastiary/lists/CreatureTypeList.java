package com.lycanitesmobs.client.gui.beastiary.lists;

import com.lycanitesmobs.client.gui.beastiary.BeastiaryScreen;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.CreatureType;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.renderer.Tessellator;

public class CreatureTypeList extends CreatureFilterList {
   private Map<Integer, CreatureType> creatureTypeList = new HashMap();

   public CreatureTypeList(BeastiaryScreen parentGui, int width, int height, int top, int bottom, int x) {
      super(parentGui, width, height, top, bottom, x, 16);
      this.refreshList();
   }

   public void refreshList() {
      this.creatureTypeList.clear();
      int groupIndex = 0;
      List<CreatureType> creatureTypes = new ArrayList();
      creatureTypes.addAll(CreatureManager.getInstance().creatureTypes.values());
      creatureTypes.sort(Comparator.comparing(CreatureType::getTitle));

      for(CreatureType creatureType : creatureTypes) {
         if (this.parentGui.playerExt.beastiary.getCreaturesDiscovered(creatureType) > 0) {
            this.creatureTypeList.put(Integer.valueOf(groupIndex++), creatureType);
         }
      }

   }

   protected int getSize() {
      return this.creatureTypeList.size();
   }

   protected void elementClicked(int index, boolean doubleClick) {
      this.parentGui.playerExt.selectedCreatureType = (CreatureType)this.creatureTypeList.get(Integer.valueOf(index));
      super.elementClicked(index, doubleClick);
   }

   protected boolean isSelected(int index) {
      return this.parentGui.playerExt.selectedCreatureType != null && this.parentGui.playerExt.selectedCreatureType.equals(this.creatureTypeList.get(Integer.valueOf(index)));
   }

   protected void drawBackground() {
   }

   protected void drawSlot(int index, int boxRight, int boxTop, int boxBottom, Tessellator tessellator) {
      CreatureType creatureType = (CreatureType)this.creatureTypeList.get(Integer.valueOf(index));
      if (creatureType != null) {
         this.parentGui.getFontRenderer().drawString(creatureType.getTitle(), (float)(this.left + 4), (float)(boxTop + 2), 16777215, true);
      }
   }

   public boolean canListCreature(CreatureInfo creatureInfo, CreatureList.Type listType) {
      if (this.parentGui.playerExt.selectedCreatureType != null && creatureInfo != null) {
         return creatureInfo.creatureType == this.parentGui.playerExt.selectedCreatureType;
      } else {
         return false;
      }
   }
}
