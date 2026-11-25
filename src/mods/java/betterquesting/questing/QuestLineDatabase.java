package betterquesting.questing;

import betterquesting.api.questing.IQuestLine;
import betterquesting.api.questing.IQuestLineDatabase;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.storage.SimpleDatabase;
import betterquesting.api2.utils.QuestLineSorter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;

public final class QuestLineDatabase extends SimpleDatabase<IQuestLine> implements IQuestLineDatabase {
   public static final QuestLineDatabase INSTANCE = new QuestLineDatabase();
   private final List<Integer> lineOrder = new ArrayList();
   private final QuestLineSorter SORTER = new QuestLineSorter(this);

   public QuestLineDatabase() {
   }

   public synchronized int getOrderIndex(int lineID) {
      int order = this.lineOrder.indexOf(Integer.valueOf(lineID));
      if (order >= 0) {
         return order;
      } else if (this.getValue(lineID) == null) {
         return -1;
      } else {
         this.lineOrder.add(Integer.valueOf(lineID));
         return this.lineOrder.size() - 1;
      }
   }

   public synchronized void setOrderIndex(int lineID, int index) {
      this.lineOrder.remove(Integer.valueOf(lineID));
      this.lineOrder.add(MathHelper.clamp(index, 0, this.lineOrder.size()), Integer.valueOf(lineID));
   }

   public synchronized List<DBEntry<IQuestLine>> getSortedEntries() {
      List<DBEntry<IQuestLine>> list = new ArrayList(this.getEntries());
      list.sort(this.SORTER);
      return list;
   }

   public synchronized IQuestLine createNew(int id) {
      IQuestLine ql = new QuestLine();
      if (id >= 0) {
         this.add(id, ql);
      }

      return ql;
   }

   public synchronized void removeQuest(int questID) {
      for(DBEntry<IQuestLine> ql : this.getEntries()) {
         ((IQuestLine)ql.getValue()).removeID(questID);
      }

   }

   public synchronized NBTTagList writeToNBT(NBTTagList json, @Nullable List<Integer> subset) {
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

   public synchronized void readFromNBT(NBTTagList json, boolean merge) {
      if (!merge) {
         this.reset();
      }

      List<IQuestLine> unassigned = new ArrayList();
      HashMap<Integer, Integer> orderMap = new HashMap();

      for(int i = 0; i < json.tagCount(); ++i) {
         NBTTagCompound jql = json.getCompoundTagAt(i);
         int id = jql.hasKey("lineID", 99) ? jql.getInteger("lineID") : -1;
         int order = jql.hasKey("order", 99) ? jql.getInteger("order") : -1;
         IQuestLine line = (IQuestLine)this.getValue(id);
         if (line == null) {
            line = new QuestLine();
         }

         line.readFromNBT(jql, false);
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
      this.lineOrder.clear();
      Iterator var13 = orderKeys.iterator();

      while(var13.hasNext()) {
         int o = ((Integer)var13.next()).intValue();
         this.lineOrder.add(orderMap.get(Integer.valueOf(o)));
      }

   }

   public synchronized void reset() {
      super.reset();
      this.lineOrder.clear();
   }
}
