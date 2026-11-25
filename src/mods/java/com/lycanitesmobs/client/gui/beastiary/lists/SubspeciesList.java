package com.lycanitesmobs.client.gui.beastiary.lists;

import com.lycanitesmobs.client.gui.beastiary.BeastiaryScreen;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.Subspecies;
import com.lycanitesmobs.core.info.Variant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.client.GuiScrollingList;

public class SubspeciesList extends GuiScrollingList {
   private BeastiaryScreen parentGui;
   private CreatureInfo creature;
   private Map<Integer, SubspeciesList.Entry> entryList = new HashMap();
   private boolean summoning;

   public SubspeciesList(BeastiaryScreen parentGui, boolean summoning, int width, int height, int top, int bottom, int x) {
      super(Minecraft.getMinecraft(), width, height, top, bottom, x, 24, width, height);
      this.parentGui = parentGui;
      this.summoning = summoning;
      this.refreshList();
   }

   public void refreshList() {
      this.entryList.clear();
      if (!this.summoning) {
         this.creature = this.parentGui.playerExt.selectedCreature;
      } else {
         this.creature = this.parentGui.playerExt.getSelectedSummonSet().getCreatureInfo();
      }

      if (this.creature != null) {
         int index = 0;

         for(Subspecies subspecies : this.creature.subspecies.values()) {
            this.entryList.put(Integer.valueOf(index++), new SubspeciesList.Entry(subspecies.index, 0));
            Iterator var4 = subspecies.variants.keySet().iterator();

            while(var4.hasNext()) {
               int variantIndex = ((Integer)var4.next()).intValue();
               if (this.parentGui.playerExt.getBeastiary().hasKnowledgeRank(this.creature.getName(), 2)) {
                  Variant variant = subspecies.getVariant(variantIndex);
                  if (variant != null && (!this.summoning || !"rare".equals(variant.rarity))) {
                     this.entryList.put(Integer.valueOf(index++), new SubspeciesList.Entry(subspecies.index, variant.index));
                  }
               }
            }
         }

      }
   }

   protected int getSize() {
      return this.entryList.size();
   }

   protected void elementClicked(int index, boolean doubleClick) {
      this.selectedIndex = index;
      if (!this.summoning) {
         this.parentGui.playerExt.selectedSubspecies = ((SubspeciesList.Entry)this.entryList.get(Integer.valueOf(index))).subspeciesIndex;
         this.parentGui.playerExt.selectedVariant = ((SubspeciesList.Entry)this.entryList.get(Integer.valueOf(index))).variantIndex;
      } else {
         this.parentGui.playerExt.getSelectedSummonSet().setSubspecies(((SubspeciesList.Entry)this.entryList.get(Integer.valueOf(index))).subspeciesIndex);
         this.parentGui.playerExt.getSelectedSummonSet().setVariant(((SubspeciesList.Entry)this.entryList.get(Integer.valueOf(index))).variantIndex);
         this.parentGui.playerExt.sendSummonSetToServer((byte)this.parentGui.playerExt.selectedSummonSet);
      }

   }

   protected boolean isSelected(int index) {
      if (!this.summoning) {
         return this.parentGui.playerExt.selectedSubspecies == ((SubspeciesList.Entry)this.entryList.get(Integer.valueOf(index))).subspeciesIndex && this.parentGui.playerExt.selectedVariant == ((SubspeciesList.Entry)this.entryList.get(Integer.valueOf(index))).variantIndex;
      } else {
         return this.parentGui.playerExt.getSelectedSummonSet().getSubspecies() == ((SubspeciesList.Entry)this.entryList.get(Integer.valueOf(index))).subspeciesIndex && this.parentGui.playerExt.getSelectedSummonSet().getVariant() == ((SubspeciesList.Entry)this.entryList.get(Integer.valueOf(index))).variantIndex;
      }
   }

   protected void drawBackground() {
      if (!this.summoning) {
         if (this.creature != this.parentGui.playerExt.selectedCreature) {
            this.refreshList();
         }
      } else if (this.creature != this.parentGui.playerExt.getSelectedSummonSet().getCreatureInfo()) {
         this.refreshList();
      }

   }

   protected int getContentHeight() {
      return this.getSize() * this.slotHeight;
   }

   protected void drawSlot(int index, int boxRight, int boxTop, int boxBottom, Tessellator tessellator) {
      int subspeciesId = ((SubspeciesList.Entry)this.entryList.get(Integer.valueOf(index))).subspeciesIndex;
      Subspecies subspecies = this.creature.getSubspecies(subspeciesId);
      String subspeciesName = "";
      if (subspecies.name != null) {
         subspeciesName = " " + subspecies.getTitle();
      }

      int variantId = ((SubspeciesList.Entry)this.entryList.get(Integer.valueOf(index))).variantIndex;
      Variant variant = subspecies.getVariant(variantId);
      String variantName = "Normal";
      if (variant != null) {
         variantName = variant.getTitle();
      }

      int nameY = boxTop + 6;
      this.parentGui.getFontRenderer().drawString(variantName + subspeciesName, this.left + 10, nameY, 16777215);
   }

   public static class Entry {
      int subspeciesIndex;
      int variantIndex;

      public Entry(int subspeciesIndex, int variantIndex) {
         this.subspeciesIndex = subspeciesIndex;
         this.variantIndex = variantIndex;
      }
   }
}
