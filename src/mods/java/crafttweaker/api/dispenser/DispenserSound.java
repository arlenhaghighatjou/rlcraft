package crafttweaker.api.dispenser;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("crafttweaker.dispenser.DispenserSound")
public enum DispenserSound {
   DISPENSE(1000),
   FAIL(1001),
   LAUNCH(1002);

   private final int soundType;

   private DispenserSound(int soundType) {
      this.soundType = soundType;
   }

   public int getSoundType() {
      return this.soundType;
   }

   @ZenMethod
   public static DispenserSound dispense() {
      return DISPENSE;
   }

   @ZenMethod
   public static DispenserSound fail() {
      return FAIL;
   }

   @ZenMethod
   public static DispenserSound launch() {
      return LAUNCH;
   }
}
