package com.lycanitesmobs.client.gui.beastiary;

import com.lycanitesmobs.GuiHandler;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.client.gui.beastiary.lists.CreatureFilterList;
import com.lycanitesmobs.client.gui.beastiary.lists.CreatureList;
import com.lycanitesmobs.client.gui.beastiary.lists.SubspeciesList;
import com.lycanitesmobs.client.gui.buttons.CreatureButton;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.network.MessageSummonSetSelection;
import com.lycanitesmobs.core.pets.SummonSet;
import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;

public class SummoningBeastiaryScreen extends BeastiaryScreen {
   public CreatureList petList;
   public SubspeciesList subspeciesList;
   private int summoningSlotIdStart = 200;
   private int petCommandIdStart = 300;

   public static void openToPlayer(EntityPlayer player) {
      if (player != null) {
         player.openGui(LycanitesMobs.instance, GuiHandler.GuiType.BEASTIARY.id, player.getEntityWorld(), GuiHandler.Beastiary.SUMMONING.id, 0, 0);
      }

   }

   public SummoningBeastiaryScreen(EntityPlayer player) {
      super(player);
   }

   public String getTitle() {
      return this.playerExt.beastiary.getSummonableList().isEmpty() ? LanguageManager.translate("gui.beastiary.summoning.empty.title") : LanguageManager.translate("gui.beastiary.summoning");
   }

   public void initControls() {
      super.initControls();
      int petListHeight = this.colLeftHeight;
      int petListY = this.colLeftY;
      this.petList = new CreatureList(CreatureList.Type.SUMMONABLE, this, (CreatureFilterList)null, this.colLeftWidth, petListHeight, petListY, petListY + petListHeight, this.colLeftX);
      int subspeciesListHeight = 80;
      int subspeciesListY = this.colRightY + 70;
      this.subspeciesList = new SubspeciesList(this, true, 90, subspeciesListHeight, subspeciesListY, subspeciesListY + subspeciesListHeight, this.colRightX);
      int summoningSlots = this.playerExt.summonSetMax;
      int buttonSpacing = 2;
      int buttonWidth = 32;
      int buttonHeight = 32;
      int buttonX = this.colRightX + Math.round((float)this.colRightWidth / 2.0F) - Math.round((float)(buttonWidth + buttonSpacing) * ((float)summoningSlots / 2.0F));
      int buttonY = this.colRightY + 10;
      int tabSpacing = buttonSpacing;

      for(int i = 1; i <= summoningSlots; ++i) {
         String buttonText = String.valueOf(i);
         CreatureInfo creatureInfo = this.playerExt.getSummonSet(i).getCreatureInfo();
         buttonX += tabSpacing;
         GuiButton tabButton = new CreatureButton(this.summoningSlotIdStart + i, buttonX, buttonY, buttonWidth, buttonHeight, buttonText, creatureInfo);
         this.buttonList.add(tabButton);
         if (i == this.playerExt.selectedSummonSet) {
            tabButton.enabled = false;
         }

         tabSpacing = buttonWidth + buttonSpacing;
      }

      int buttonMarginX = 10 + Math.max(Math.max(this.getFontRenderer().getStringWidth(LanguageManager.translate("gui.pet.actions")), this.getFontRenderer().getStringWidth(LanguageManager.translate("gui.pet.stance"))), this.getFontRenderer().getStringWidth(LanguageManager.translate("gui.pet.movement")));
      buttonWidth = 80;
      buttonHeight = 20;
      buttonX = this.colRightX + buttonMarginX;
      buttonY = this.colRightY + this.colRightHeight - (buttonHeight + buttonSpacing) * 3;
      GuiButton button = new GuiButton(BaseCreatureEntity.PET_COMMAND_ID.PVP.id + this.petCommandIdStart, buttonX, buttonY, buttonWidth, buttonHeight, LanguageManager.translate("gui.pet.pvp"));
      this.buttonList.add(button);
      buttonX = this.colRightX + buttonMarginX;
      buttonY = buttonY + buttonHeight + 2;
      button = new GuiButton(BaseCreatureEntity.PET_COMMAND_ID.PASSIVE.id + this.petCommandIdStart, buttonX, buttonY, buttonWidth, buttonHeight, LanguageManager.translate("gui.pet.passive"));
      this.buttonList.add(button);
      buttonX = buttonX + buttonWidth + buttonSpacing;
      button = new GuiButton(BaseCreatureEntity.PET_COMMAND_ID.DEFENSIVE.id + this.petCommandIdStart, buttonX, buttonY, buttonWidth, buttonHeight, LanguageManager.translate("gui.pet.defensive"));
      this.buttonList.add(button);
      buttonX = buttonX + buttonWidth + buttonSpacing;
      button = new GuiButton(BaseCreatureEntity.PET_COMMAND_ID.ASSIST.id + this.petCommandIdStart, buttonX, buttonY, buttonWidth, buttonHeight, LanguageManager.translate("gui.pet.assist"));
      this.buttonList.add(button);
      buttonX = buttonX + buttonWidth + buttonSpacing;
      button = new GuiButton(BaseCreatureEntity.PET_COMMAND_ID.AGGRESSIVE.id + this.petCommandIdStart, buttonX, buttonY, buttonWidth, buttonHeight, LanguageManager.translate("gui.pet.aggressive"));
      this.buttonList.add(button);
      buttonX = this.colRightX + buttonMarginX;
      buttonY = buttonY + buttonHeight + 2;
      button = new GuiButton(BaseCreatureEntity.PET_COMMAND_ID.FOLLOW.id + this.petCommandIdStart, buttonX, buttonY, buttonWidth, buttonHeight, LanguageManager.translate("gui.pet.follow"));
      this.buttonList.add(button);
      buttonX = buttonX + buttonWidth + buttonSpacing;
      button = new GuiButton(BaseCreatureEntity.PET_COMMAND_ID.WANDER.id + this.petCommandIdStart, buttonX, buttonY, buttonWidth, buttonHeight, LanguageManager.translate("gui.pet.wander"));
      this.buttonList.add(button);
      buttonX = buttonX + buttonWidth + buttonSpacing;
      button = new GuiButton(BaseCreatureEntity.PET_COMMAND_ID.SIT.id + this.petCommandIdStart, buttonX, buttonY, buttonWidth, buttonHeight, LanguageManager.translate("gui.pet.sit"));
      this.buttonList.add(button);
   }

   public void drawBackground(int mouseX, int mouseY, float partialTicks) {
      super.drawBackground(mouseX, mouseY, partialTicks);
   }

   protected void updateControls(int mouseX, int mouseY, float partialTicks) {
      super.updateControls(mouseX, mouseY, partialTicks);
      if (!this.playerExt.beastiary.getSummonableList().isEmpty()) {
         this.petList.drawScreen(mouseX, mouseY, partialTicks);
         this.subspeciesList.drawScreen(mouseX, mouseY, partialTicks);

         for(GuiButton button : this.buttonList) {
            if (button.id >= this.summoningSlotIdStart && button.id < this.petCommandIdStart) {
               button.enabled = button.id - this.summoningSlotIdStart != this.playerExt.selectedSummonSet;
               if (button instanceof CreatureButton) {
                  CreatureButton buttonCreature = (CreatureButton)button;
                  buttonCreature.creatureInfo = this.playerExt.getSummonSet(button.id - this.summoningSlotIdStart).getCreatureInfo();
               }
            } else if (button.id >= this.petCommandIdStart) {
               if (this.playerExt.getSelectedSummonSet() == null) {
                  button.visible = false;
               } else {
                  button.visible = true;
                  if (button.id == BaseCreatureEntity.PET_COMMAND_ID.PVP.id + this.petCommandIdStart) {
                     if (this.playerExt.getSelectedSummonSet().getPVP()) {
                        button.displayString = LanguageManager.translate("gui.pet.pvp") + ": " + LanguageManager.translate("common.yes");
                     } else {
                        button.displayString = LanguageManager.translate("gui.pet.pvp") + ": " + LanguageManager.translate("common.no");
                     }
                  } else if (button.id == BaseCreatureEntity.PET_COMMAND_ID.PASSIVE.id + this.petCommandIdStart) {
                     button.enabled = !this.playerExt.getSelectedSummonSet().passive;
                  } else if (button.id == BaseCreatureEntity.PET_COMMAND_ID.DEFENSIVE.id + this.petCommandIdStart) {
                     button.enabled = this.playerExt.getSelectedSummonSet().getPassive() || this.playerExt.getSelectedSummonSet().getAssist() || this.playerExt.getSelectedSummonSet().getAggressive();
                  } else if (button.id == BaseCreatureEntity.PET_COMMAND_ID.ASSIST.id + this.petCommandIdStart) {
                     button.enabled = this.playerExt.getSelectedSummonSet().getPassive() || !this.playerExt.getSelectedSummonSet().getAssist() || this.playerExt.getSelectedSummonSet().getAggressive();
                  } else if (button.id == BaseCreatureEntity.PET_COMMAND_ID.AGGRESSIVE.id + this.petCommandIdStart) {
                     button.enabled = this.playerExt.getSelectedSummonSet().getPassive() || !this.playerExt.getSelectedSummonSet().getAggressive();
                  } else if (button.id == BaseCreatureEntity.PET_COMMAND_ID.FOLLOW.id + this.petCommandIdStart) {
                     button.enabled = this.playerExt.getSelectedSummonSet().getSitting() || !this.playerExt.getSelectedSummonSet().getFollowing();
                  } else if (button.id != BaseCreatureEntity.PET_COMMAND_ID.WANDER.id + this.petCommandIdStart) {
                     if (button.id == BaseCreatureEntity.PET_COMMAND_ID.SIT.id + this.petCommandIdStart) {
                        button.enabled = !this.playerExt.getSelectedSummonSet().getSitting();
                     }
                  } else {
                     button.enabled = this.playerExt.getSelectedSummonSet().getSitting() || this.playerExt.getSelectedSummonSet().getFollowing();
                  }
               }
            }
         }

      }
   }

   public void drawForeground(int mouseX, int mouseY, float partialTicks) {
      super.drawForeground(mouseX, mouseY, partialTicks);
      int marginX = 0;
      int nextX = this.colRightX + marginX;
      int nextY = this.colRightY + 44;
      int width = this.colRightWidth - marginX;
      if (this.playerExt.beastiary.getSummonableList().isEmpty()) {
         String text = LanguageManager.translate("gui.beastiary.summoning.empty.info");
         this.drawSplitString(text, nextX, nextY, width, 16777215, true);
      } else {
         CreatureInfo selectedCreature = this.playerExt.getSelectedSummonSet().getCreatureInfo();
         if (selectedCreature != null) {
            this.renderCreature(selectedCreature, this.colRightX + marginX / 2 + this.colRightWidth / 2, this.colRightY + Math.round((float)this.colRightHeight / 2.0F), mouseX, mouseY, partialTicks);
         }

         String text = "§l" + LanguageManager.translate("gui.beastiary.player.focus") + ": ";
         this.getFontRenderer().drawString(text, (float)nextX, (float)nextY, 16777215, true);
         int barX = nextX + this.getFontRenderer().getStringWidth(text);
         int focusMax = Math.round((float)this.playerExt.summonFocusMax / (float)this.playerExt.summonFocusCharge);
         int focusAvailable = (int)Math.floor((double)this.playerExt.summonFocus / (double)this.playerExt.summonFocusCharge);
         float focusFilling = (float)this.playerExt.summonFocus / (float)this.playerExt.summonFocusCharge - (float)focusAvailable;
         this.drawBar(AssetManager.getTexture("GUIPetSpiritEmpty"), barX, nextY, 0.0F, 9.0F, 9.0F, focusMax, 10);
         this.drawBar(AssetManager.getTexture("GUIPetSpiritUsed"), barX, nextY, 0.0F, 9.0F, 9.0F, focusAvailable, 10);
         if (focusFilling > 0.0F) {
            this.drawTexture(AssetManager.getTexture("GUIPetSpiritFilling"), (float)(barX + 9 * focusAvailable), (float)nextY, 0.0F, focusFilling, 1.0F, focusFilling * 9.0F, 9.0F);
         }

         if (selectedCreature != null) {
            nextY = nextY + 4 + this.getFontRenderer().getWordWrappedHeight(text, this.colRightWidth);
            text = "§l" + LanguageManager.translate("creature.stat.focus") + ": ";
            this.getFontRenderer().drawString(text, (float)nextX, (float)nextY, 16777215, true);
            this.drawLevel(selectedCreature, AssetManager.getTexture("GUIPetLevel"), nextX + this.getFontRenderer().getStringWidth(text), nextY);
         } else {
            nextY = nextY + 4 + this.getFontRenderer().getWordWrappedHeight(text, this.colRightWidth);
            text = LanguageManager.translate("gui.beastiary.summoning.select");
            this.drawSplitString(text, nextX, nextY, width, 16777215, true);
         }

         int buttonHeight = 20;
         int buttonSpacing = 2;
         int buttonY = this.colRightY + this.colRightHeight - (buttonHeight + buttonSpacing) * 3;
         this.getFontRenderer().drawString("§l" + LanguageManager.translate("gui.pet.actions"), (float)this.colRightX, (float)(buttonY + 6), 16777215, true);
         buttonY = buttonY + buttonHeight + buttonSpacing;
         this.getFontRenderer().drawString("§l" + LanguageManager.translate("gui.pet.stance"), (float)this.colRightX, (float)(buttonY + 6), 16777215, true);
         buttonY = buttonY + buttonHeight + buttonSpacing;
         this.getFontRenderer().drawString("§l" + LanguageManager.translate("gui.pet.movement"), (float)this.colRightX, (float)(buttonY + 6), 16777215, true);
      }
   }

   protected void actionPerformed(GuiButton button) throws IOException {
      if (button.id >= this.summoningSlotIdStart && button.id < this.petCommandIdStart) {
         this.playerExt.setSelectedSummonSet(button.id - this.summoningSlotIdStart);
         MessageSummonSetSelection message = new MessageSummonSetSelection(this.playerExt);
         LycanitesMobs.packetHandler.sendToServer(message);
      }

      SummonSet summonSet = this.playerExt.getSelectedSummonSet();
      if (summonSet != null && button.id >= this.petCommandIdStart) {
         int petCommandId = button.id - this.petCommandIdStart;
         if (petCommandId == BaseCreatureEntity.PET_COMMAND_ID.PVP.id) {
            summonSet.pvp = !summonSet.pvp;
         } else if (petCommandId == BaseCreatureEntity.PET_COMMAND_ID.PASSIVE.id) {
            summonSet.passive = true;
            summonSet.assist = false;
            summonSet.aggressive = false;
         } else if (petCommandId == BaseCreatureEntity.PET_COMMAND_ID.DEFENSIVE.id) {
            summonSet.passive = false;
            summonSet.assist = false;
            summonSet.aggressive = false;
         } else if (petCommandId == BaseCreatureEntity.PET_COMMAND_ID.ASSIST.id) {
            summonSet.passive = false;
            summonSet.assist = true;
            summonSet.aggressive = false;
         } else if (petCommandId == BaseCreatureEntity.PET_COMMAND_ID.AGGRESSIVE.id) {
            summonSet.passive = false;
            summonSet.assist = true;
            summonSet.aggressive = true;
         } else if (petCommandId == BaseCreatureEntity.PET_COMMAND_ID.FOLLOW.id) {
            summonSet.following = true;
            summonSet.sitting = false;
         } else if (petCommandId == BaseCreatureEntity.PET_COMMAND_ID.WANDER.id) {
            summonSet.following = false;
            summonSet.sitting = false;
         } else if (petCommandId == BaseCreatureEntity.PET_COMMAND_ID.SIT.id) {
            summonSet.following = false;
            summonSet.sitting = true;
         }

         this.playerExt.sendSummonSetToServer((byte)this.playerExt.selectedSummonSet);
         if (this.playerExt.selectedPet == null) {
            openToPlayer(this.player);
         }

      } else {
         super.actionPerformed(button);
      }
   }

   public int getDisplaySubspecies(CreatureInfo creatureInfo) {
      return this.playerExt.getSelectedSummonSet().subspecies;
   }

   public int getDisplayVariant(CreatureInfo creatureInfo) {
      return this.playerExt.getSelectedSummonSet().variant;
   }

   public void playCreatureSelectSound(CreatureInfo creatureInfo) {
      this.player.getEntityWorld().playSound(this.player, this.player.posX, this.player.posY, this.player.posZ, AssetManager.getSound(creatureInfo.getName() + "_tame"), SoundCategory.NEUTRAL, 1.0F, 1.0F);
   }
}
