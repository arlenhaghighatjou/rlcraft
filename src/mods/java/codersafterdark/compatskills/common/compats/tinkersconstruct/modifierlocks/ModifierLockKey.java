package codersafterdark.compatskills.common.compats.tinkersconstruct.modifierlocks;

import codersafterdark.reskillable.api.data.LockKey;
import slimeknights.tconstruct.library.modifiers.IToolMod;

public class ModifierLockKey implements LockKey {
   private final IToolMod modifier;

   public ModifierLockKey(IToolMod modifier) {
      this.modifier = modifier;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (o instanceof ModifierLockKey) {
         ModifierLockKey other = (ModifierLockKey)o;
         return this.modifier == null ? other.modifier == null : this.modifier.getIdentifier().equals(other.modifier.getIdentifier());
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.modifier == null ? super.hashCode() : this.modifier.getIdentifier().hashCode();
   }
}
