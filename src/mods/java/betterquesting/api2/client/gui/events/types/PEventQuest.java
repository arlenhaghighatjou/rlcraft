package betterquesting.api2.client.gui.events.types;

import betterquesting.api2.client.gui.events.PanelEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class PEventQuest extends PanelEvent {
   private final Set<Integer> questIDs;

   public PEventQuest(int questID) {
      this.questIDs = Collections.singleton(Integer.valueOf(questID));
   }

   public PEventQuest(Collection<Integer> questIDs) {
      this.questIDs = Collections.unmodifiableSet(new TreeSet(questIDs));
   }

   public Set<Integer> getQuestID() {
      return this.questIDs;
   }

   public boolean canCancel() {
      return false;
   }
}
