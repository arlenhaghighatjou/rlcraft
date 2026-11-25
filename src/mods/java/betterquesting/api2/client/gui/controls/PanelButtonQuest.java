package betterquesting.api2.client.gui.controls;

import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.enums.EnumLogic;
import betterquesting.api.enums.EnumQuestState;
import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api.storage.IQuestSettings;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api2.client.gui.misc.GuiRectangle;
import betterquesting.api2.client.gui.resources.colors.IGuiColor;
import betterquesting.api2.client.gui.resources.textures.GuiTextureColored;
import betterquesting.api2.client.gui.resources.textures.IGuiTexture;
import betterquesting.api2.client.gui.resources.textures.OreDictTexture;
import betterquesting.api2.client.gui.themes.presets.PresetColor;
import betterquesting.api2.client.gui.themes.presets.PresetTexture;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.QuestTranslation;
import betterquesting.questing.QuestDatabase;
import betterquesting.storage.QuestSettings;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;

public class PanelButtonQuest extends PanelButtonStorage<DBEntry<IQuest>> {
   public final GuiRectangle rect;
   public final EntityPlayer player;

   public PanelButtonQuest(GuiRectangle rect, int id, String txt, DBEntry<IQuest> value) {
      super(rect, id, txt, value);
      this.rect = rect;
      this.player = Minecraft.getMinecraft().player;
      EnumQuestState qState = value == null ? EnumQuestState.LOCKED : ((IQuest)value.getValue()).getState(QuestingAPI.getQuestingUUID(this.player));
      IGuiTexture txFrame = null;
      IGuiColor txIconCol = null;
      boolean main = value == null ? false : ((Boolean)((IQuest)value.getValue()).getProperty(NativeProps.MAIN)).booleanValue();
      boolean lock = false;
      switch(qState) {
      case LOCKED:
         txFrame = main ? PresetTexture.QUEST_MAIN_0.getTexture() : PresetTexture.QUEST_NORM_0.getTexture();
         txIconCol = PresetColor.QUEST_ICON_LOCKED.getColor();
         lock = true;
         break;
      case UNLOCKED:
         txFrame = main ? PresetTexture.QUEST_MAIN_1.getTexture() : PresetTexture.QUEST_NORM_1.getTexture();
         txIconCol = PresetColor.QUEST_ICON_UNLOCKED.getColor();
         break;
      case UNCLAIMED:
         txFrame = main ? PresetTexture.QUEST_MAIN_2.getTexture() : PresetTexture.QUEST_NORM_2.getTexture();
         txIconCol = PresetColor.QUEST_ICON_PENDING.getColor();
         break;
      case COMPLETED:
         txFrame = main ? PresetTexture.QUEST_MAIN_3.getTexture() : PresetTexture.QUEST_NORM_3.getTexture();
         txIconCol = PresetColor.QUEST_ICON_COMPLETE.getColor();
      }

      IGuiTexture btnTx = new GuiTextureColored(txFrame, txIconCol);
      this.setTextures(btnTx, btnTx, btnTx);
      this.setIcon(new OreDictTexture(1.0F, value == null ? new BigItemStack(Items.NETHER_STAR) : (BigItemStack)((IQuest)value.getValue()).getProperty(NativeProps.ICON), false, true), 4);
      this.setActive(((IQuestSettings)QuestingAPI.getAPI(ApiReference.SETTINGS)).canUserEdit(this.player) || !lock);
   }

   public List<String> getTooltip(int mx, int my) {
      if (!this.getTransform().contains(mx, my)) {
         return null;
      } else {
         DBEntry<IQuest> value = (DBEntry)this.getStoredValue();
         return value == null ? Collections.emptyList() : this.getQuestTooltip(value.getValue(), this.player, value.getID());
      }
   }

   private List<String> getQuestTooltip(IQuest quest, EntityPlayer player, int qID) {
      List<String> tooltip = this.getStandardTooltip(quest, player, qID);
      if (Minecraft.getMinecraft().gameSettings.advancedItemTooltips && ((Boolean)QuestSettings.INSTANCE.getProperty(NativeProps.EDIT_MODE)).booleanValue()) {
         tooltip.add("");
         tooltip.addAll(this.getAdvancedTooltip(quest, player, qID));
      }

      return tooltip;
   }

   private List<String> getStandardTooltip(IQuest quest, EntityPlayer player, int qID) {
      List<String> list = new ArrayList();
      list.add(QuestTranslation.translate((String)quest.getProperty(NativeProps.NAME)) + (!Minecraft.getMinecraft().gameSettings.advancedItemTooltips ? "" : " #" + qID));
      EaglercraftUUID playerID = QuestingAPI.getQuestingUUID(player);
      if (quest.isComplete(playerID)) {
         list.add(TextFormatting.GREEN + QuestTranslation.translate("betterquesting.tooltip.complete"));
         if (!quest.hasClaimed(playerID)) {
            list.add(TextFormatting.GRAY + QuestTranslation.translate("betterquesting.tooltip.rewards_pending"));
         } else if (((Integer)quest.getProperty(NativeProps.REPEAT_TIME)).intValue() > 0) {
            long time = this.getRepeatSeconds(quest, player);
            DecimalFormat df = new DecimalFormat("00");
            String timeTxt = "";
            if (time >= 3600L) {
               timeTxt = timeTxt + time / 3600L + "h " + df.format(time % 3600L / 60L) + "m ";
            } else if (time >= 60L) {
               timeTxt = timeTxt + time / 60L + "m ";
            }

            timeTxt = timeTxt + df.format(time % 60L) + "s";
            list.add(TextFormatting.GRAY + QuestTranslation.translate("betterquesting.tooltip.repeat", timeTxt));
         }
      } else if (!quest.isUnlocked(playerID)) {
         list.add(TextFormatting.RED + "" + TextFormatting.UNDERLINE + QuestTranslation.translate("betterquesting.tooltip.requires") + " (" + ((EnumLogic)quest.getProperty(NativeProps.LOGIC_QUEST)).toString().toUpperCase() + ")");

         for(DBEntry<IQuest> req : QuestDatabase.INSTANCE.bulkLookup(quest.getRequirements())) {
            if (!((IQuest)req.getValue()).isComplete(playerID)) {
               list.add(TextFormatting.RED + "- " + QuestTranslation.translate((String)((IQuest)req.getValue()).getProperty(NativeProps.NAME)));
            }
         }
      } else {
         int n = 0;

         for(DBEntry<ITask> task : quest.getTasks().getEntries()) {
            if (((ITask)task.getValue()).isComplete(playerID)) {
               ++n;
            }
         }

         list.add(TextFormatting.GRAY + QuestTranslation.translate("betterquesting.tooltip.tasks_complete", n, quest.getTasks().size()));
      }

      return list;
   }

   private List<String> getAdvancedTooltip(IQuest quest, EntityPlayer player, int qID) {
      List<String> list = new ArrayList();
      list.add(TextFormatting.GRAY + QuestTranslation.translate("betterquesting.tooltip.global_quest", quest.getProperty(NativeProps.GLOBAL)));
      if (((Boolean)quest.getProperty(NativeProps.GLOBAL)).booleanValue()) {
         list.add(TextFormatting.GRAY + QuestTranslation.translate("betterquesting.tooltip.global_share", quest.getProperty(NativeProps.GLOBAL_SHARE)));
      }

      list.add(TextFormatting.GRAY + QuestTranslation.translate("betterquesting.tooltip.quest_logic", ((EnumLogic)quest.getProperty(NativeProps.LOGIC_QUEST)).toString().toUpperCase()));
      list.add(TextFormatting.GRAY + QuestTranslation.translate("betterquesting.tooltip.simultaneous", quest.getProperty(NativeProps.SIMULTANEOUS)));
      list.add(TextFormatting.GRAY + QuestTranslation.translate("betterquesting.tooltip.auto_claim", quest.getProperty(NativeProps.AUTO_CLAIM)));
      if (((Integer)quest.getProperty(NativeProps.REPEAT_TIME)).intValue() >= 0) {
         long time = (long)(((Integer)quest.getProperty(NativeProps.REPEAT_TIME)).intValue() / 20);
         DecimalFormat df = new DecimalFormat("00");
         String timeTxt = "";
         if (time >= 3600L) {
            timeTxt = timeTxt + time / 3600L + "h " + df.format(time % 3600L / 60L) + "m ";
         } else if (time >= 60L) {
            timeTxt = timeTxt + time / 60L + "m ";
         }

         timeTxt = timeTxt + df.format(time % 60L) + "s";
         list.add(TextFormatting.GRAY + QuestTranslation.translate("betterquesting.tooltip.repeat", timeTxt));
      } else {
         list.add(TextFormatting.GRAY + QuestTranslation.translate("betterquesting.tooltip.repeat", false));
      }

      return list;
   }

   private long getRepeatSeconds(IQuest quest, EntityPlayer player) {
      if (((Integer)quest.getProperty(NativeProps.REPEAT_TIME)).intValue() < 0) {
         return -1L;
      } else {
         NBTTagCompound ue = quest.getCompletionInfo(QuestingAPI.getQuestingUUID(player));
         return ue == null ? 0L : ((long)((Integer)quest.getProperty(NativeProps.REPEAT_TIME)).intValue() * 50L - (System.currentTimeMillis() - ue.getLong("timestamp"))) / 1000L;
      }
   }
}
