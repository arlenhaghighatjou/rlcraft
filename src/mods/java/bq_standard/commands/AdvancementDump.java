package bq_standard.commands;

import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.enums.EnumQuestVisibility;
import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.IQuestDatabase;
import betterquesting.api.questing.IQuestLine;
import betterquesting.api.questing.IQuestLineDatabase;
import betterquesting.api.questing.IQuestLineEntry;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.storage.IDatabaseNBT;
import bq_standard.tasks.TaskAdvancement;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Map.Entry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;

public class AdvancementDump {
   public static final AdvancementDump INSTANCE = new AdvancementDump();
   private HashMap<Advancement, DBEntry<IQuest>> idMap = new HashMap();

   public AdvancementDump() {
   }

   public void dumpAdvancements(MinecraftServer server) {
      this.idMap.clear();
      IQuestDatabase questDB = (IQuestDatabase)QuestingAPI.getAPI(ApiReference.QUEST_DB);
      IQuestLineDatabase lineDB = (IQuestLineDatabase)QuestingAPI.getAPI(ApiReference.LINE_DB);
      List<Advancement> roots = new ArrayList();

      for(Advancement adv : server.getAdvancementManager().getAdvancements()) {
         if (adv.getDisplay() != null) {
            int id = questDB.nextID();
            IQuest quest = questDB.createNew(id);
            IDatabaseNBT<ITask, NBTTagList, NBTTagList> taskDB = quest.getTasks();
            DisplayInfo disp = adv.getDisplay();
            if (adv.getParent() == null) {
               roots.add(adv);
               if (adv.getCriteria().isEmpty()) {
                  quest.setProperty(NativeProps.SILENT, Boolean.valueOf(true));
               }
            }

            quest.setProperty(NativeProps.NAME, disp.getTitle().getFormattedText());
            quest.setProperty(NativeProps.DESC, disp.getDescription().getFormattedText());
            quest.setProperty(NativeProps.ICON, new BigItemStack(disp.getIcon()));
            quest.setProperty(NativeProps.SILENT, Boolean.valueOf(true));
            quest.setProperty(NativeProps.VISIBILITY, !disp.isHidden() && adv.getParent() != null ? EnumQuestVisibility.UNLOCKED : EnumQuestVisibility.COMPLETED);
            quest.setProperty(NativeProps.MAIN, Boolean.valueOf(disp.getFrame() == FrameType.GOAL));
            TaskAdvancement task = new TaskAdvancement();
            task.advID = adv.getId();
            taskDB.add(taskDB.nextID(), task);
            this.idMap.put(adv, new DBEntry(id, quest));
         }
      }

      for(Entry<Advancement, DBEntry<IQuest>> entry : this.idMap.entrySet()) {
         if (((Advancement)entry.getKey()).getParent() != null) {
            Advancement parent = ((Advancement)entry.getKey()).getParent();
            DBEntry<IQuest> pq = (DBEntry)this.idMap.get(parent);
            if (pq != null) {
               this.addReq((IQuest)((DBEntry)entry.getValue()).getValue(), pq.getID());
            }
         }
      }

      for(Advancement adv : roots) {
         IQuestLine ql = lineDB.createNew(lineDB.nextID());
         ql.setProperty(NativeProps.NAME, adv.getDisplay().getTitle().getFormattedText());
         ql.setProperty(NativeProps.DESC, adv.getDisplay().getDescription().getFormattedText());
         IQuestLineEntry qleRoot = ql.createNew(((DBEntry)this.idMap.get(adv)).getID());
         qleRoot.setPosition((int)(adv.getDisplay().getX() * 32.0F), (int)(adv.getDisplay().getY() * 32.0F));
         qleRoot.setSize(24, 24);
         Queue<Iterator<Advancement>> iterStack = new ArrayDeque();
         iterStack.add(adv.getChildren().iterator());

         while(!iterStack.isEmpty()) {
            Iterator<Advancement> iter = (Iterator)iterStack.poll();

            while(iter.hasNext()) {
               Advancement child = (Advancement)iter.next();
               IQuestLineEntry qle = ql.createNew(((DBEntry)this.idMap.get(child)).getID());
               qle.setPosition((int)(child.getDisplay().getX() * 32.0F), (int)(child.getDisplay().getY() * 32.0F));
               qle.setSize(24, 24);
               iterStack.add(child.getChildren().iterator());
            }
         }
      }

   }

   private boolean containsReq(IQuest quest, int id) {
      for(int reqID : quest.getRequirements()) {
         if (id == reqID) {
            return true;
         }
      }

      return false;
   }

   private boolean addReq(IQuest quest, int id) {
      if (this.containsReq(quest, id)) {
         return false;
      } else {
         int[] orig = quest.getRequirements();
         int[] added = Arrays.copyOf(orig, orig.length + 1);
         added[orig.length] = id;
         quest.setRequirements(added);
         return true;
      }
   }
}
