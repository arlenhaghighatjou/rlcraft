package codersafterdark.compatskills.common.compats.gamestages.gamestagelocks;

import codersafterdark.reskillable.api.data.LockKey;

public class GameStageLock implements LockKey {
   private final String gamestage;

   public GameStageLock(String gamestage) {
      this.gamestage = gamestage;
   }

   public String getGamestage() {
      return this.gamestage;
   }

   public boolean equals(Object o) {
      return o == this || o instanceof GameStageLock && this.gamestage.equals(((GameStageLock)o).gamestage);
   }

   public int hashCode() {
      return this.gamestage.hashCode();
   }
}
