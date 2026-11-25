package com.lycanitesmobs.client.gui.beastiary.lists;

import com.lycanitesmobs.client.gui.beastiary.BeastiaryScreen;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.info.CreatureInfo;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.Tessellator;

public class PetTypeList extends CreatureFilterList {
   private Map<Integer, String> petTypeList = new HashMap();

   public PetTypeList(BeastiaryScreen parentGui, int width, int height, int top, int bottom, int x) {
      super(parentGui, width, height, top, bottom, x, 24);
      this.refreshList();
   }

   public void refreshList() {
      this.selectedIndex = this.parentGui.playerExt.selectedPetType;
      this.petTypeList.clear();
      this.petTypeList.put(Integer.valueOf(0), "gui.beastiary.pets");
      this.petTypeList.put(Integer.valueOf(1), "gui.beastiary.mounts");
      this.petTypeList.put(Integer.valueOf(2), "gui.beastiary.familiars");
   }

   protected int getSize() {
      return this.petTypeList.size();
   }

   protected void elementClicked(int index, boolean doubleClick) {
      super.elementClicked(index, doubleClick);
      this.parentGui.playerExt.selectedPetType = index;

      for(CreatureList creatureList : this.filteredLists) {
         if (creatureList != null) {
            this.updateCreatureListType(creatureList);
         }
      }

   }

   public void updateCreatureListType(CreatureList creatureList) {
      CreatureList.Type listType = null;
      if (this.selectedIndex == 0) {
         listType = CreatureList.Type.PET;
      } else if (this.selectedIndex == 1) {
         listType = CreatureList.Type.MOUNT;
      } else if (this.selectedIndex == 2) {
         listType = CreatureList.Type.FAMILIAR;
      }

      creatureList.changeType(listType);
   }

   protected boolean isSelected(int index) {
      return this.selectedIndex == index;
   }

   protected void drawBackground() {
   }

   protected void drawSlot(int index, int boxRight, int boxTop, int boxBottom, Tessellator tessellator) {
      String petListType = (String)this.petTypeList.get(Integer.valueOf(index));
      if (petListType != null) {
         this.parentGui.getFontRenderer().drawString(LanguageManager.translate(petListType), (float)(this.left + 2), (float)(boxTop + 4), 16777215, true);
      }
   }

   public void addFilteredList(CreatureList creatureList) {
      super.addFilteredList(creatureList);
      this.updateCreatureListType(creatureList);
   }

   public boolean canListCreature(CreatureInfo creatureInfo, CreatureList.Type listType) {
      if (creatureInfo != null && listType != null) {
         if (this.selectedIndex == 0 && listType == CreatureList.Type.PET) {
            return true;
         } else if (this.selectedIndex == 1 && listType == CreatureList.Type.MOUNT) {
            return true;
         } else {
            return this.selectedIndex == 2 && listType == CreatureList.Type.FAMILIAR;
         }
      } else {
         return false;
      }
   }
}
