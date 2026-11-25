package betterquesting.client.importers;

import betterquesting.api.questing.IQuestLine;
import betterquesting.api.questing.IQuestLineDatabase;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.storage.SimpleDatabase;
import betterquesting.api2.utils.QuestLineSorter;
import betterquesting.questing.QuestLine;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class ImportedQuestLines extends SimpleDatabase<IQuestLine> implements IQuestLineDatabase {
   private final List<Integer> lineOrder = new ArrayList();
   private final QuestLineSorter SORTER = new QuestLineSorter(this);

   public ImportedQuestLines() {
   }

   public int getOrderIndex(int lineID) {
      synchronized(this.lineOrder) {
         if (this.getValue(lineID) == null) {
            return -1;
         } else {
            if (!this.lineOrder.contains(Integer.valueOf(lineID))) {
               this.lineOrder.add(Integer.valueOf(lineID));
            }

            return this.lineOrder.indexOf(Integer.valueOf(lineID));
         }
      }
   }

   public void setOrderIndex(int lineID, int index) {
      synchronized(this.lineOrder) {
         this.lineOrder.remove(Integer.valueOf(lineID));
         this.lineOrder.add(index, Integer.valueOf(lineID));
      }
   }

   public List<DBEntry<IQuestLine>> getSortedEntries() {
      List<DBEntry<IQuestLine>> ary = new ArrayList(this.getEntries());
      ary.sort(this.SORTER);
      return ary;
   }

   public NBTTagList writeToNBT(NBTTagList json, List<Integer> subset) {
      for(DBEntry<IQuestLine> entry : this.getEntries()) {
         if (subset == null || subset.contains(Integer.valueOf(entry.getID()))) {
            NBTTagCompound jObj = (NBTTagCompound)((IQuestLine)entry.getValue()).writeToNBT(new NBTTagCompound(), (List)null);
            jObj.setInteger("lineID", entry.getID());
            jObj.setInteger("order", this.getOrderIndex(entry.getID()));
            json.appendTag(jObj);
         }
      }

      return json;
   }

   public void readFromNBT(NBTTagList json, boolean merge) {
      if (!merge) {
         this.reset();
      }

      List<IQuestLine> unassigned = new ArrayList();
      HashMap<Integer, Integer> orderMap = new HashMap();

      for(int i = 0; i < json.tagCount(); ++i) {
         NBTTagCompound jql = json.getCompoundTagAt(i);
         int id = jql.hasKey("lineID", 99) ? jql.getInteger("lineID") : -1;
         int order = jql.hasKey("order", 99) ? jql.getInteger("order") : -1;
         QuestLine line = new QuestLine();
         line.readFromNBT(jql, merge);
         if (id >= 0) {
            this.add(id, line);
         } else {
            unassigned.add(line);
         }

         if (order >= 0) {
            orderMap.put(Integer.valueOf(order), Integer.valueOf(id));
         }
      }

      for(IQuestLine q : unassigned) {
         this.add(this.nextID(), q);
      }

      List<Integer> orderKeys = new ArrayList(orderMap.keySet());
      Collections.sort(orderKeys);
      synchronized(this.lineOrder) {
         this.lineOrder.clear();
         Iterator var16 = orderKeys.iterator();

         while(var16.hasNext()) {
            int o = ((Integer)var16.next()).intValue();
            this.lineOrder.add(orderMap.get(Integer.valueOf(o)));
         }

      }
   }

   public void removeQuest(int questID) {
      for(DBEntry<IQuestLine> ql : this.getEntries()) {
         ((IQuestLine)ql.getValue()).removeID(questID);
      }

   }

   public IQuestLine createNew(int id) {
      QuestLine ql = new QuestLine();
      this.add(id, ql);
      return ql;
   }
}
