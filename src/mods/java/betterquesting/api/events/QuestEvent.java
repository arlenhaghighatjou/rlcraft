package betterquesting.api.events;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import net.minecraftforge.fml.common.eventhandler.Event;

public class QuestEvent extends Event {
   private final QuestEvent.Type type;
   private final UUID playerID;
   private final Set<Integer> questIDs;

   public Set<Integer> getQuestIDs() {
      return this.questIDs;
   }

   public UUID getPlayerID() {
      return this.playerID;
   }

   public QuestEvent.Type getType() {
      return this.type;
   }

   public QuestEvent(QuestEvent.Type type, UUID playerID, int questID) {
      this.type = type;
      this.playerID = playerID;
      this.questIDs = Collections.singleton(Integer.valueOf(questID));
   }

   public QuestEvent(QuestEvent.Type type, UUID playerID, Collection<Integer> questIDs) {
      this.type = type;
      this.playerID = playerID;
      this.questIDs = Collections.unmodifiableSet(new TreeSet(questIDs));
   }

   public static enum Type {
      COMPLETED,
      UPDATED,
      RESET;

      private Type() {
      }
   }
}
