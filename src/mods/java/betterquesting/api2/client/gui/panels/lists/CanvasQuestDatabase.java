package betterquesting.api2.client.gui.panels.lists;

import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.IQuest;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.QuestTranslation;
import betterquesting.questing.QuestDatabase;
import java.util.ArrayDeque;
import java.util.Iterator;

public abstract class CanvasQuestDatabase extends CanvasSearch<DBEntry<IQuest>, DBEntry<IQuest>> {
   public CanvasQuestDatabase(IGuiRect rect) {
      super(rect);
   }

   protected Iterator<DBEntry<IQuest>> getIterator() {
      return QuestDatabase.INSTANCE.getEntries().iterator();
   }

   protected void queryMatches(DBEntry<IQuest> entry, String query, ArrayDeque<DBEntry<IQuest>> results) {
      if (("" + entry.getID()).contains(query) || ((String)((IQuest)entry.getValue()).getProperty(NativeProps.NAME)).toLowerCase().contains(query) || QuestTranslation.translate((String)((IQuest)entry.getValue()).getProperty(NativeProps.NAME)).toLowerCase().contains(query)) {
         results.add(entry);
      }

   }
}
