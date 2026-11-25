package betterquesting.api2.client.gui.events.types;

import betterquesting.api2.client.gui.events.PanelEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class PEventParty extends PanelEvent {
   private final Set<Integer> partyIDs;

   public PEventParty(int partyID) {
      this.partyIDs = Collections.singleton(Integer.valueOf(partyID));
   }

   public PEventParty(Collection<Integer> partyIDs) {
      this.partyIDs = Collections.unmodifiableSet(new TreeSet(partyIDs));
   }

   public Set<Integer> getPartyIDs() {
      return this.partyIDs;
   }

   public boolean canCancel() {
      return false;
   }
}
