package codersafterdark.compatskills.common.compats.betterquesting;

import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.ParticipantInfo;
import codersafterdark.compatskills.CompatSkills;
import codersafterdark.compatskills.common.compats.betterquesting.gui.GuiTaskRequirementEditor;
import codersafterdark.compatskills.common.compats.betterquesting.gui.PanelTaskRequirement;
import codersafterdark.reskillable.api.ReskillableAPI;
import codersafterdark.reskillable.api.data.PlayerData;
import codersafterdark.reskillable.api.data.PlayerDataHandler;
import codersafterdark.reskillable.api.data.RequirementHolder;
import codersafterdark.reskillable.api.requirement.Requirement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

public class TaskRequirement implements ITask {
   private final Set<UUID> completeUsers = new TreeSet();
   private List<String> requirements = new ArrayList();
   private RequirementHolder holder;
   private boolean hasUncacheable;

   public TaskRequirement() {
   }

   public String getUnlocalisedName() {
      return "compatskills.misc.betterquesting.requirement_task";
   }

   public ResourceLocation getFactoryID() {
      return FactoryTaskRequirement.INSTANCE.getRegistryName();
   }

   public void detect(ParticipantInfo participant, DBEntry<IQuest> quest) {
      if (!this.isComplete(participant.UUID)) {
         PlayerData data = PlayerDataHandler.get(participant.PLAYER);
         if (data != null) {
            this.setHolder();
            if (data.matchStats(this.holder)) {
               this.setComplete(participant.UUID);
            }

            participant.markDirty(Collections.singletonList(Integer.valueOf(quest.getID())));
         }
      }
   }

   private void setHolder() {
      if (this.holder == null) {
         List<Requirement> reqs = new ArrayList();

         for(String req : this.requirements) {
            Requirement requirement = ReskillableAPI.getInstance().getRequirementRegistry().getRequirement(req);
            if (requirement != null) {
               if (!requirement.isCacheable()) {
                  this.hasUncacheable = true;
               }

               reqs.add(requirement);
            }
         }

         this.holder = new RequirementHolder(reqs);
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

   @SideOnly(Side.CLIENT)
   public IGuiPanel getTaskGui(IGuiRect rect, DBEntry<IQuest> quest) {
      return new PanelTaskRequirement(rect, this);
   }

   @Nullable
   @SideOnly(Side.CLIENT)
   public GuiScreen getTaskEditor(GuiScreen parent, DBEntry<IQuest> quest) {
      return new GuiTaskRequirementEditor(parent, quest, this);
   }

   public NBTTagCompound writeToNBT(NBTTagCompound json) {
      NBTTagList reqs = new NBTTagList();

      for(String req : this.requirements) {
         reqs.appendTag(new NBTTagString(req));
      }

      json.setTag("requirements", reqs);
      return json;
   }

   public void readFromNBT(NBTTagCompound json) {
      this.requirements.clear();

      for(NBTBase req : json.getTagList("requirements", 8)) {
         if (req instanceof NBTTagString) {
            this.requirements.add(((NBTTagString)req).getString());
         }
      }

      this.resetHolder();
   }

   private void resetHolder() {
      this.holder = null;
      this.hasUncacheable = false;
      this.setHolder();
   }

   public NBTTagCompound writeProgressToNBT(NBTTagCompound json, @Nullable List<UUID> users) {
      NBTTagList jArray = new NBTTagList();
      this.completeUsers.stream().filter((completeUser) -> {
         return users == null || users.contains(completeUser);
      }).map((completeUser) -> {
         return new NBTTagString(completeUser.toString());
      }).forEach(jArray::appendTag);
      json.setTag("completeUsers", jArray);
      return json;
   }

   public void readProgressFromNBT(NBTTagCompound json, boolean merge) {
      if (!merge) {
         this.completeUsers.clear();
      }

      NBTTagList cList = json.getTagList("completeUsers", 8);

      for(int i = 0; i < cList.tagCount(); ++i) {
         try {
            this.completeUsers.add(UUID.fromString(cList.getStringTagAt(i)));
         } catch (Exception var6) {
            CompatSkills.logger.log(Level.ERROR, "Unable to load UUID for task", var6);
         }
      }

   }

   public RequirementHolder getRequirementHolder() {
      this.setHolder();
      return this.holder;
   }

   public List<String> getRequirements() {
      return this.requirements;
   }

   public void updateRequirements(List<String> requirements) {
      this.requirements = requirements;
      this.resetHolder();
   }

   public boolean hasUncacheable() {
      return this.hasUncacheable;
   }
}
