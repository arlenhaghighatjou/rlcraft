package cursedflames.bountifulbaubles.baubleeffect;

import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;

public interface IFireResistance {
   default float getResistance() {
      return 1.0F;
   }

   default float getResistanceLava() {
      return 0.0F;
   }

   default float getMaxNegate() {
      return 0.0F;
   }

   EaglercraftUUID getFireResistID();
}
