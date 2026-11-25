package yalter.mousetweaks;

public class Constants {
   public static final String MOD_NAME = "Mouse Tweaks";
   public static final String MOD_ID = "mousetweaks";
   public static final String VERSION = "2.10.1";
   public static final String UPDATE_URL = "http://play.sourceruns.org/yalter/MouseTweaks/update.json";
   static final String CONFIG_RMB_TWEAK = "RMBTweak";
   static final String CONFIG_LMB_TWEAK_WITH_ITEM = "LMBTweakWithItem";
   static final String CONFIG_LMB_TWEAK_WITHOUT_ITEM = "LMBTweakWithoutItem";
   static final String CONFIG_WHEEL_TWEAK = "WheelTweak";
   static final String CONFIG_WHEEL_SEARCH_ORDER = "WheelSearchOrder";
   static final String CONFIG_WHEEL_SCROLL_DIRECTION = "WheelScrollDirection";
   static final String CONFIG_ONTICK_METHOD_ORDER = "OnTickMethodOrder";
   static final String CONFIG_SCROLL_HANDLING = "ScrollHandling";
   static final String CONFIG_DEBUG = "Debug";
   static final String CONFIG_SCROLL_ITEM_SCALING = "ScrollItemScaling";
   public static final ObfuscatedName IGNOREMOUSEUP_NAME = new ObfuscatedName("ignoreMouseUp", "ignoreMouseUp", "I");
   public static final ObfuscatedName DRAGSPLITTING_NAME = new ObfuscatedName("dragSplitting", "dragSplitting", "u");
   public static final ObfuscatedName DRAGSPLITTINGBUTTON_NAME = new ObfuscatedName("dragSplittingButton", "dragSplittingButton", "H");
   public static final ObfuscatedName GETSLOTATPOSITION_NAME = new ObfuscatedName("getSlotAtPosition", "getSlotAtPosition", "d");
   public static final ObfuscatedName HANDLEMOUSECLICK_NAME = new ObfuscatedName("handleMouseClick", "handleMouseClick", "a");
   static final String ONTICKMETHOD_FORGE_NAME = "Forge";
   static final String ONTICKMETHOD_LITELOADER_NAME = "LiteLoader";

   public Constants() {
   }

   public static enum EntryPoint {
      UNDEFINED,
      FORGE,
      LITELOADER;

      private EntryPoint() {
      }
   }
}
