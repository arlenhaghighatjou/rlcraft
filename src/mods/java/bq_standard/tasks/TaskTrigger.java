package bq_standard.tasks;

import betterquesting.api.api.QuestingAPI;
import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.cache.CapabilityProviderQuestCache;
import betterquesting.api2.cache.QuestCache;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.client.gui.panels.content.PanelTextBox;
import betterquesting.api2.client.gui.themes.presets.PresetColor;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.ParticipantInfo;
import bq_standard.advancment_hacks.BqsAdvListener;
import bq_standard.core.BQ_Standard;
import bq_standard.tasks.factory.FactoryTaskTrigger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class TaskTrigger implements ITask {
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   private final Set<EaglercraftUUID> completeUsers = new TreeSet();
   private String triggerID = "minecraft:impossible";
   private String critJson = "{}";
   private BqsAdvListener listener = null;
   private boolean needsSetup = true;
   public String desc = "";

   public TaskTrigger() {
   }

   public String getCriteriaJson() {
      return this.critJson;
   }

   public void setCriteriaJson(String json) {
      if (!this.critJson.equals(json)) {
         this.critJson = StringUtils.isNullOrEmpty(json) ? "{}" : json;

         try {
            GSON.fromJson(json, JsonObject.class);
         } catch (Exception var3) {
            BQ_Standard.logger.error("Unable to parse JSON for trigger task");
            this.critJson = "{}";
         }

         this.needsSetup = true;
      }
   }

   public String getTriggerID() {
      return this.triggerID;
   }

   public void setTriggerID(String id) {
      if (!this.triggerID.equals(id)) {
         this.triggerID = id;
         this.needsSetup = true;
      }
   }

   private void setupListener(DBEntry<IQuest> quest) {
      this.needsSetup = false;
      int tskID = ((IQuest)quest.getValue()).getTasks().getID(this);
      ICriterionTrigger trig = CriteriaTriggers.get(new ResourceLocation(this.triggerID));
      if (trig != null) {
         try {
            ICriterionInstance in = trig.deserializeInstance((JsonObject)GSON.fromJson(this.critJson, JsonObject.class), (JsonDeserializationContext)null);
            this.listener = new BqsAdvListener(trig, in, quest.getID(), tskID);
         } catch (Exception var5) {
            ;
         }

      }
   }

   public BqsAdvListener<?> getListener() {
      return this.listener;
   }

   public boolean hasSetup() {
      return !this.needsSetup;
   }

   public void onCriteriaComplete(EntityPlayerMP player, BqsAdvListener advList, int questID) {
      if (advList == this.listener) {
         EaglercraftUUID playerID = QuestingAPI.getQuestingUUID(player);
         this.setComplete(playerID);
         QuestCache qc = (QuestCache)player.getCapability(CapabilityProviderQuestCache.CAP_QUEST_CACHE, (EnumFacing)null);
         if (qc != null) {
            qc.markQuestDirty(questID);
         }

      }
   }

   public void checkSetup(@Nonnull EntityPlayer player, @Nonnull DBEntry<IQuest> quest) {
      if (this.needsSetup) {
         this.setupListener(quest);
      }
   }

   public String getUnlocalisedName() {
      return "bq_standard.task.trigger";
   }

   public ResourceLocation getFactoryID() {
      return FactoryTaskTrigger.INSTANCE.getRegistryName();
   }

   public void detect(ParticipantInfo pInfo, DBEntry<IQuest> quest) {
   }

   public boolean isComplete(EaglercraftUUID uuid) {
      return this.completeUsers.contains(uuid);
   }

   public void setComplete(EaglercraftUUID uuid) {
      this.completeUsers.add(uuid);
   }

   public void resetUser(@Nullable EaglercraftUUID uuid) {
      if (uuid == null) {
         this.completeUsers.clear();
      } else {
         this.completeUsers.remove(uuid);
      }

   }

   @Nullable
   @SideOnly(Side.CLIENT)
   public IGuiPanel getTaskGui(IGuiRect rect, DBEntry<IQuest> quest) {
      return (new PanelTextBox(rect, this.desc)).setColor(PresetColor.TEXT_MAIN.getColor());
   }

   @Nullable
   @SideOnly(Side.CLIENT)
   public GuiScreen getTaskEditor(GuiScreen parent, DBEntry<IQuest> quest) {
      return null;
   }

   public NBTTagCompound writeProgressToNBT(NBTTagCompound nbt, @Nullable List<EaglercraftUUID> users) {
      NBTTagList jArray = new NBTTagList();
      this.completeUsers.forEach((uuid) -> {
         if (users == null || users.contains(uuid)) {
            jArray.appendTag(new NBTTagString(uuid.toString()));
         }

      });
      nbt.setTag("completeUsers", jArray);
      return nbt;
   }

   public void readProgressFromNBT(NBTTagCompound nbt, boolean merge) {
      if (!merge) {
         this.completeUsers.clear();
      }

      NBTTagList cList = nbt.getTagList("completeUsers", 8);

      for(int i = 0; i < cList.tagCount(); ++i) {
         try {
            this.completeUsers.add(EaglercraftUUID.fromString(cList.getStringTagAt(i)));
         } catch (Exception var6) {
            BQ_Standard.logger.log(Level.ERROR, "Unable to load EaglercraftUUID for task", var6);
         }
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      nbt.setString("description", this.desc);
      nbt.setString("trigger", this.triggerID);
      nbt.setString("conditions", this.critJson);
      return nbt;
   }

   public void readFromNBT(NBTTagCompound nbt) {
      this.desc = nbt.getString("description");
      this.setTriggerID(nbt.getString("trigger"));
      this.setCriteriaJson(nbt.getString("conditions"));
   }
}
