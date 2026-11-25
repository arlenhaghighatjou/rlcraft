package invtweaks;

public enum InvTweaksShortcutType {
   MOVE_ALL_ITEMS,
   MOVE_EVERYTHING,
   MOVE_ONE_STACK,
   MOVE_ONE_ITEM,
   MOVE_UP,
   MOVE_DOWN,
   MOVE_TO_SPECIFIC_HOTBAR_SLOT,
   DROP;

   private InvTweaksShortcutType() {
   }

   public static InvTweaksShortcutType fromConfigKey(String property) {
      if ("shortcutKeyAllItems".equals(property)) {
         return MOVE_ALL_ITEMS;
      } else if ("shortcutKeyEverything".equals(property)) {
         return MOVE_EVERYTHING;
      } else if ("shortcutKeyToLowerSection".equals(property)) {
         return MOVE_DOWN;
      } else if ("shortcutKeyDrop".equals(property)) {
         return DROP;
      } else if ("shortcutKeyOneItem".equals(property)) {
         return MOVE_ONE_ITEM;
      } else {
         return "shortcutKeyToUpperSection".equals(property) ? MOVE_UP : null;
      }
   }
}
