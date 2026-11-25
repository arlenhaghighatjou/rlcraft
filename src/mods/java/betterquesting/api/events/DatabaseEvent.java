package betterquesting.api.events;

import net.minecraftforge.fml.common.eventhandler.Event;

/** @deprecated */
@Deprecated
public abstract class DatabaseEvent extends Event {
   private final DatabaseEvent.DBType TYPE;

   public DatabaseEvent(DatabaseEvent.DBType type) {
      this.TYPE = type;
   }

   public DatabaseEvent.DBType getType() {
      return this.TYPE;
   }

   public static enum DBType {
      QUEST,
      CHAPTER,
      PARTY,
      NAMES,
      ALL,
      OTHER;

      private DBType() {
      }
   }

   /** @deprecated */
   @Deprecated
   public static class Load extends DatabaseEvent {
      public Load(DatabaseEvent.DBType type) {
         super(type);
      }
   }

   /** @deprecated */
   @Deprecated
   public static class Save extends DatabaseEvent {
      public Save(DatabaseEvent.DBType type) {
         super(type);
      }
   }

   /** @deprecated */
   @Deprecated
   public static class Update extends DatabaseEvent {
      public Update(DatabaseEvent.DBType type) {
         super(type);
      }
   }
}
