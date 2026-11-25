package betterquesting.api2.storage;

import javax.annotation.Nonnull;

public final class DBEntry<T> implements Comparable<DBEntry<T>> {
   private final int id;
   @Nonnull
   private final T obj;

   public DBEntry(int id, T obj) {
      if (id < 0) {
         throw new IllegalArgumentException("Entry ID cannot be negative");
      } else if (obj == null) {
         throw new NullPointerException("Entry value cannot be null");
      } else {
         this.id = id;
         this.obj = obj;
      }
   }

   public final int getID() {
      return this.id;
   }

   @Nonnull
   public final T getValue() {
      return this.obj;
   }

   public int compareTo(DBEntry<T> o) {
      return Integer.compare(this.id, o.id);
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof DBEntry)) {
         return false;
      } else {
         DBEntry entry = (DBEntry)obj;
         return this.getID() == entry.getID() && this.getValue().equals(entry.getValue());
      }
   }
}
