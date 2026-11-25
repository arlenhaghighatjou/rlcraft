package bq_standard.tasks;

import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.ParticipantInfo;
import bq_standard.client.gui.editors.tasks.GuiEditTaskAdvancement;
import bq_standard.client.gui.tasks.PanelTaskAdvancement;
import bq_standard.core.BQ_Standard;
import bq_standard.tasks.factory.FactoryTaskAdvancement;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

public class TaskAdvancement implements ITask {
   private final Set<UUID> completeUsers = new TreeSet();
   public ResourceLocation advID;

   public TaskAdvancement() {
   }

   public String getUnlocalisedName() {
      return "bq_standard.task.advancement";
   }

   public ResourceLocation getFactoryID() {
      return FactoryTaskAdvancement.INSTANCE.getRegistryName();
   }

   public void onAdvancementGet(DBEntry<IQuest> quest, ParticipantInfo pInfo, Advancement advancement) {
      if (advancement != null && this.advID != null && this.advID.equals(advancement.getId())) {
         this.detect(pInfo, quest);
      }
   }

   public void detect(ParticipantInfo pInfo, DBEntry<IQuest> quest) {
      if (pInfo.PLAYER instanceof EntityPlayerMP && pInfo.PLAYER.getServer() != null && this.advID != null) {
         Advancement adv = pInfo.PLAYER.getServer().getAdvancementManager().getAdvancement(this.advID);
         if (adv != null) {
            PlayerAdvancements playerAdv = pInfo.PLAYER.getServer().getPlayerList().getPlayerAdvancements((EntityPlayerMP)pInfo.PLAYER);
            if (playerAdv.getProgress(adv).isDone()) {
               this.setComplete(pInfo.UUID);
            }

            pInfo.markDirty(Collections.singletonList(Integer.valueOf(quest.getID())));
         }
      }
   }

   public boolean isComplete(UUID uuid) {
      return this.completeUsers.contains(uuid);
   }

   public void setComplete(UUID uuid) {
      this.completeUsers.add(uuid);
   }

   public void resetUser(@Nullable UUID uuid) {
      if (uuid == null) {
         this.completeUsers.clear();
      } else {
         this.completeUsers.remove(uuid);
      }

   }

   @Nullable
   @SideOnly(Side.CLIENT)
   public IGuiPanel getTaskGui(IGuiRect rect, DBEntry<IQuest> quest) {
      return new PanelTaskAdvancement(rect, this);
   }

   @Nullable
   @SideOnly(Side.CLIENT)
   public GuiScreen getTaskEditor(GuiScreen parent, DBEntry<IQuest> quest) {
      return new GuiEditTaskAdvancement(parent, quest, this);
   }

   public NBTTagCompound writeProgressToNBT(NBTTagCompound nbt, @Nullable List<UUID> users) {
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
            this.completeUsers.add(UUID.fromString(cList.getStringTagAt(i)));
         } catch (Exception var6) {
            BQ_Standard.logger.log(Level.ERROR, "Unable to load UUID for task", var6);
         }
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      nbt.setString("advancement_id", this.advID == null ? "" : this.advID.toString());
      return nbt;
   }

   public void readFromNBT(NBTTagCompound nbt) {
      String id = nbt.getString("advancement_id");
      this.advID = StringUtils.isNullOrEmpty(id) ? null : new ResourceLocation(id);
   }
}
