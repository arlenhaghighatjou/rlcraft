package betterquesting.api2.storage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;

public class SimpleDatabase<T> implements IDatabase<T> {
   private final TreeMap<Integer, T> mapDB = new TreeMap();
   private final BitSet idMap = new BitSet();
   private List<DBEntry<T>> refCache = null;

   public SimpleDatabase() {
   }

   public synchronized int nextID() {
      return this.idMap.nextClearBit(0);
   }

   public synchronized DBEntry<T> add(int id, T value) {
      if (value == null) {
         throw new NullPointerException("Value cannot be null");
      } else if (id < 0) {
         throw new IllegalArgumentException("ID cannot be negative");
      } else if (this.mapDB.putIfAbsent(Integer.valueOf(id), value) == null) {
         this.idMap.set(id);
         this.refCache = null;
         return new DBEntry<T>(id, value);
      } else {
         throw new IllegalArgumentException("ID or value is already contained within database");
      }
   }

   public synchronized boolean removeID(int key) {
      if (key < 0) {
         return false;
      } else if (this.mapDB.remove(Integer.valueOf(key)) != null) {
         this.idMap.clear(key);
         this.refCache = null;
         return true;
      } else {
         return false;
      }
   }

   public synchronized boolean removeValue(T value) {
      return value != null && this.removeID(this.getID(value));
   }

   public synchronized int getID(T value) {
      if (value == null) {
         return -1;
      } else {
         for(DBEntry<T> entry : this.getEntries()) {
            if (entry.getValue() == value) {
               return entry.getID();
            }
         }

         return -1;
      }
   }

   public synchronized T getValue(int id) {
      return (T)(id >= 0 && this.mapDB.size() > 0 ? this.mapDB.get(Integer.valueOf(id)) : null);
   }

   public synchronized int size() {
      return this.mapDB.size();
   }

   public synchronized void reset() {
      this.mapDB.clear();
      this.idMap.clear();
      this.refCache = Collections.emptyList();
   }

   public synchronized List<DBEntry<T>> getEntries() {
      if (this.refCache == null) {
         List<DBEntry<T>> temp = new ArrayList();

         for(Entry<Integer, T> entry : this.mapDB.entrySet()) {
            temp.add(new DBEntry(((Integer)entry.getKey()).intValue(), entry.getValue()));
         }

         this.refCache = Collections.unmodifiableList(temp);
      }

      return this.refCache;
   }

   public synchronized List<DBEntry<T>> bulkLookup(int... keys) {
      if (keys.length <= 0) {
         return Collections.emptyList();
      } else {
         int[] sortedKeys = new int[keys.length];
         System.arraycopy(keys, 0, sortedKeys, 0, keys.length);
         Arrays.sort(sortedKeys);
         List<DBEntry<T>> subList = new ArrayList();
         int n = 0;
         Iterator var5 = this.getEntries().iterator();

         while(var5.hasNext()) {
            DBEntry<T> entry;
            for(entry = (DBEntry)var5.next(); n < sortedKeys.length && sortedKeys[n] < entry.getID(); ++n) {
               ;
            }

            if (n >= sortedKeys.length) {
               break;
            }

            if (sortedKeys[n] == entry.getID()) {
               subList.add(entry);
            }
         }

         return subList;
      }
   }
}
