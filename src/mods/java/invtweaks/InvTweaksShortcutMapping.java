package invtweaks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;

public class InvTweaksShortcutMapping {
   @NotNull
   private List<Integer> keysToHold = new ArrayList();

   public InvTweaksShortcutMapping(int keyCode) {
      this.keysToHold.add(Integer.valueOf(keyCode));
   }

   public InvTweaksShortcutMapping(@NotNull String... keyNames) {
      for(String keyName : keyNames) {
         keyName = keyName.trim().replace("KEY_", "").replace("ALT", "MENU");
         this.keysToHold.add(Integer.valueOf(Keyboard.getKeyIndex(keyName)));
      }

   }

   public boolean isTriggered(@NotNull Map<Integer, Boolean> pressedKeys) {
      for(Integer keyToHold : this.keysToHold) {
         if (keyToHold.intValue() == 29) {
            if (!((Boolean)pressedKeys.get(keyToHold)).booleanValue() || Keyboard.isKeyDown(184)) {
               return false;
            }
         } else if (!((Boolean)pressedKeys.get(keyToHold)).booleanValue()) {
            return false;
         }
      }

      return true;
   }

   @NotNull
   public List<Integer> getKeyCodes() {
      return this.keysToHold;
   }
}
