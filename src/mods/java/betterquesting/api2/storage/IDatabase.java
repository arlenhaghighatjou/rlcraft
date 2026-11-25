package betterquesting.api2.storage;

import java.util.List;

public interface IDatabase<T> {
   int nextID();

   DBEntry<T> add(int var1, T var2);

   boolean removeID(int var1);

   boolean removeValue(T var1);

   int getID(T var1);

   T getValue(int var1);

   int size();

   void reset();

   List<DBEntry<T>> getEntries();

   List<DBEntry<T>> bulkLookup(int... var1);
}
