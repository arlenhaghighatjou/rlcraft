package com.lycanitesmobs.client.gui.beastiary.lists;

import com.lycanitesmobs.client.gui.beastiary.BeastiaryScreen;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureKnowledge;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.client.GuiScrollingList;
import net.minecraftforge.fml.client.config.GuiUtils;

public class CreatureDescriptionList extends GuiScrollingList {
   protected BeastiaryScreen parentGui;
   public CreatureKnowledge creatureKnowledge;

   public CreatureDescriptionList(BeastiaryScreen parentGui, int width, int height, int top, int bottom, int x) {
      super(Minecraft.getMinecraft(), width, height, top, bottom, x, 10800, width, height);
      this.parentGui = parentGui;
   }

   protected int getSize() {
      return 1;
   }

   protected void elementClicked(int index, boolean doubleClick) {
      this.selectedIndex = index;
   }

   protected boolean isSelected(int index) {
      return false;
   }

   protected void drawBackground() {
   }

   protected int getContentHeight() {
      return this.parentGui.getFontRenderer().getWordWrappedHeight(this.getContent(), this.listWidth) + 10;
   }

   protected void drawSlot(int index, int boxRight, int boxTop, int boxBottom, Tessellator tessellator) {
      if (index == 0 && this.creatureKnowledge != null) {
         this.parentGui.drawSplitString(this.getContent(), this.left + 6, boxTop, this.listWidth - 20, 16777215, true);
      }

   }

   public String getContent() {
      if (this.creatureKnowledge == null) {
         return "";
      } else {
         CreatureInfo creatureInfo = this.creatureKnowledge.getCreatureInfo();
         if (creatureInfo == null) {
            return "";
         } else {
            String text = "";
            if (creatureInfo.creatureType != null) {
               if (creatureInfo.isTameable() && creatureInfo.creatureType.getTreatItem() != null) {
                  text = text + "\u00a7l" + LanguageManager.translate("gui.beastiary.tameable") + ": \u00a7r" + LanguageManager.translate(creatureInfo.creatureType.getTreatItem().getUnlocalizedName() + ".name") + "\n\n";
                  if (creatureInfo.isMountable()) {
                     text = text + "\u00a7l" + LanguageManager.translate("gui.beastiary.mountable") + "\u00a7r\n\n";
                  }
               }

               if (creatureInfo.isSummonable()) {
                  text = text + "\u00a7l" + LanguageManager.translate("gui.beastiary.summonable") + "\u00a7r\n\n";
               }

               if ((creatureInfo.isTameable() || creatureInfo.isSummonable()) && creatureInfo.isPerchable()) {
                  text = text + "\u00a7l" + LanguageManager.translate("gui.beastiary.perchable") + "\u00a7r\n\n";
               }
            }

            text = text + "\u00a7l" + LanguageManager.translate("gui.beastiary.diet") + ": \u00a7r" + creatureInfo.getDietNames() + "\n\n";
            text = text + "\u00a7l" + LanguageManager.translate("gui.beastiary.summary") + ": \u00a7r";
            text = text + "\n" + creatureInfo.getDescription();
            text = text + "\n\n\u00a7l" + LanguageManager.translate("creature.stat.base") + ": \u00a7r";
            if (this.creatureKnowledge.rank >= 2) {
               String statPrefix = "\n" + LanguageManager.translate("creature.stat.base") + " ";
               text = text + "\n" + LanguageManager.translate("creature.stat.health") + ": " + creatureInfo.health;
               text = text + "\n" + LanguageManager.translate("creature.stat.defense") + ": " + creatureInfo.defense;
               text = text + "\n" + LanguageManager.translate("creature.stat.speed") + ": " + creatureInfo.speed;
               text = text + "\n" + LanguageManager.translate("creature.stat.damage") + ": " + creatureInfo.damage;
               text = text + "\n" + LanguageManager.translate("creature.stat.pierce") + ": " + creatureInfo.pierce;
               String effectText = creatureInfo.effectDuration + "s " + creatureInfo.effectAmplifier + "X";
               if (creatureInfo.effectDuration <= 0.0D || creatureInfo.effectAmplifier < 0.0D) {
                  effectText = LanguageManager.translate("common.none");
               }

               text = text + "\n" + LanguageManager.translate("creature.stat.effect") + ": " + effectText;
            } else {
               text = text + "\n" + LanguageManager.translate("gui.beastiary.unlockedat") + " " + LanguageManager.translate("creature.stat.knowledge") + " " + 2;
            }

            text = text + "\n\n\u00a7l" + LanguageManager.translate("gui.beastiary.combat") + ": \u00a7r";
            if (this.creatureKnowledge.rank >= 2) {
               text = text + "\n" + creatureInfo.getCombatDescription();
            } else {
               text = text + "\n" + LanguageManager.translate("gui.beastiary.unlockedat") + " " + LanguageManager.translate("creature.stat.knowledge") + " " + 2;
            }

            text = text + "\n\n\u00a7l" + LanguageManager.translate("gui.beastiary.habitat") + ": \u00a7r";
            if (this.creatureKnowledge.rank >= 2) {
               text = text + "\n" + creatureInfo.getHabitatDescription();
            } else {
               text = text + "\n" + LanguageManager.translate("gui.beastiary.unlockedat") + " " + LanguageManager.translate("creature.stat.knowledge") + " " + 2;
            }

            text = text + "\n\n\u00a7l" + LanguageManager.translate("gui.beastiary.biomes") + ": \u00a7r";
            if (this.creatureKnowledge.rank >= 2) {
               text = text + "\n" + creatureInfo.getBiomeNames();
            } else {
               text = text + "\n" + LanguageManager.translate("gui.beastiary.unlockedat") + " " + LanguageManager.translate("creature.stat.knowledge") + " " + 2;
            }

            text = text + "\n\n\u00a7l" + LanguageManager.translate("gui.beastiary.drops") + ": \u00a7r";
            if (this.creatureKnowledge.rank >= 2) {
               text = text + "\n" + creatureInfo.getDropNames();
            } else {
               text = text + "\n" + LanguageManager.translate("gui.beastiary.unlockedat") + " " + LanguageManager.translate("creature.stat.knowledge") + " " + 2;
            }

            return text;
         }
      }
   }

   protected void drawGradientRect(int left, int top, int right, int bottom, int color1, int color2) {
      color1 = 856690704;
      GuiUtils.drawGradientRect(0, left, top, right, bottom, color1, color1);
   }
}
