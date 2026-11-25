package bq_standard.importers.ftbq;

public class FTBEntry {
   public final int id;
   public final Object obj;
   public final FTBEntry.FTBEntryType type;

   public FTBEntry(int id, Object obj, FTBEntry.FTBEntryType type) {
      this.id = id;
      this.obj = obj;
      this.type = type;
   }

   public static enum FTBEntryType {
      QUEST,
      LINE,
      VAR;

      private FTBEntryType() {
      }
   }
}
