package invtweaks;

import invtweaks.api.IItemTreeItem;
import invtweaks.forge.ClientProxy;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InvTweaksConfig {
   public static final String PROP_VERSION = "version";
   public static final String PROP_ENABLE_MIDDLE_CLICK = "enableMiddleClick";
   public static final String PROP_SHOW_CHEST_BUTTONS = "showChestButtons";
   public static final String PROP_ENABLE_SORTING_ON_PICKUP = "enableSortingOnPickup";
   public static final String PROP_ENABLE_AUTO_EQUIP_ARMOR = "enableAutoEquipArmor";
   public static final String PROP_ENABLE_AUTO_REFILL = "enableAutoRefill";
   public static final String PROP_AUTO_REFILL_BEFORE_BREAK = "autoRefillBeforeBreak";
   public static final String PROP_AUTO_REFILL_DAMAGE_THRESHHOLD = "autoRefillDamageThreshhold";
   public static final String PROP_INVERT_TOOL_DAMAGE = "invertToolDamageSorting";
   public static final String PROP_ENABLE_SHORTCUTS = "enableShortcuts";
   public static final String PROP_SHORTCUT_PREFIX = "shortcutKey";
   public static final String PROP_SHORTCUT_ONE_ITEM = "shortcutKeyOneItem";
   public static final String PROP_SHORTCUT_ALL_ITEMS = "shortcutKeyAllItems";
   public static final String PROP_SHORTCUT_EVERYTHING = "shortcutKeyEverything";
   public static final String PROP_SHORTCUT_DROP = "shortcutKeyDrop";
   public static final String PROP_SHORTCUT_UP = "shortcutKeyToUpperSection";
   public static final String PROP_SHORTCUT_DOWN = "shortcutKeyToLowerSection";
   public static final String PROP_TOOLTIP_PATH = "enableToolTipTreePath";
   public static final String PROP_ENABLE_SOUNDS = "enableSounds";
   public static final String PROP_ENABLE_SERVER_ITEMSWAP = "enableServerItemSwap";
   public static final String PROP_ENABLE_CONFIG_LOADED_MESSAGE = "enableConfigLoadedMesssage";
   public static final String PROP_ENABLE_CONTAINER_MIRRORING = "enableContainerMirroring";
   public static final String VALUE_TRUE = "true";
   public static final String VALUE_FALSE = "false";
   public static final String LOCKED = "locked";
   public static final String FROZEN = "frozen";
   public static final String AUTOREFILL = "autorefill";
   public static final String AUTOREFILL_NOTHING = "nothing";
   public static final String DEBUG = "debug";
   private final File rulesFile;
   private final File treeFile;
   private InvTweaksConfigProperties properties;
   private InvTweaksItemTree tree;
   private List<InvTweaksConfigInventoryRuleset> rulesets;
   private int currentRuleset = 0;
   @Nullable
   private String currentRulesetName = null;
   private List<String> invalidKeywords;
   private long storedConfigLastModified;

   public InvTweaksConfig(File rulesFile_, File treeFile_) {
      this.rulesFile = rulesFile_;
      this.treeFile = treeFile_;
      this.reset();
   }

   private static File getPropertyFile() {
      File configPropsFile = InvTweaksConst.CONFIG_PROPS_FILE;
      if (!configPropsFile.exists()) {
         try {
            configPropsFile.createNewFile();
         } catch (IOException var2) {
            InvTweaks.logInGameStatic("invtweaks.propsfile.errors");
            return null;
         }
      }

      return configPropsFile;
   }

   public void load() throws Exception {
      synchronized(this) {
         this.reset();
         this.loadProperties();
         this.saveProperties();
         if (this.tree != null) {
            MinecraftForge.EVENT_BUS.unregister(this.tree);
         }

         this.tree = InvTweaksItemTreeLoader.load(this.treeFile);
         char[] bytes = new char[(int)this.rulesFile.length()];
         FileReader reader = null;

         try {
            reader = new FileReader(this.rulesFile);
            reader.read(bytes);
         } finally {
            if (reader != null) {
               reader.close();
            }

         }

         String[] configLines = String.valueOf(bytes).replace("\r\n", "\n").replace('\r', '\n').split("\n");
         InvTweaksConfigInventoryRuleset activeRuleset = new InvTweaksConfigInventoryRuleset(this.tree, "Default");
         boolean defaultRuleset = true;
         boolean defaultRulesetEmpty = true;

         for(String line : configLines) {
            String trimmedLine = line.trim();
            if (!trimmedLine.isEmpty()) {
               if (!trimmedLine.matches("^[\\w]*[\\s]*:$")) {
                  try {
                     String invalidKeyword = activeRuleset.registerLine(trimmedLine);
                     if (defaultRuleset) {
                        defaultRulesetEmpty = false;
                     }

                     if (invalidKeyword != null) {
                        this.invalidKeywords.add(invalidKeyword);
                     }
                  } catch (InvalidParameterException var19) {
                     ;
                  }
               } else {
                  if (!defaultRuleset || !defaultRulesetEmpty) {
                     activeRuleset.finalizeRules();
                     this.rulesets.add(activeRuleset);
                  }

                  activeRuleset = new InvTweaksConfigInventoryRuleset(this.tree, trimmedLine.substring(0, trimmedLine.length() - 1));
                  defaultRuleset = false;
               }
            }
         }

         activeRuleset.finalizeRules();
         this.rulesets.add(activeRuleset);
         this.currentRuleset = 0;
         if (this.currentRulesetName != null) {
            int rulesetIndex = 0;

            for(InvTweaksConfigInventoryRuleset ruleset : this.rulesets) {
               if (ruleset.getName().equals(this.currentRulesetName)) {
                  this.currentRuleset = rulesetIndex;
                  break;
               }

               ++rulesetIndex;
            }
         }

         if (this.currentRuleset == 0) {
            if (!this.rulesets.isEmpty()) {
               this.currentRulesetName = ((InvTweaksConfigInventoryRuleset)this.rulesets.get(this.currentRuleset)).getName();
            } else {
               this.currentRulesetName = null;
            }
         }

      }
   }

   public boolean refreshProperties() throws IOException {
      long configLastModified = InvTweaksConst.CONFIG_PROPS_FILE.lastModified();
      if (this.storedConfigLastModified != configLastModified) {
         this.storedConfigLastModified = configLastModified;
         this.loadProperties();
         return true;
      } else {
         return false;
      }
   }

   public void saveProperties() {
      File configPropsFile = getPropertyFile();

      assert configPropsFile != null;

      if (configPropsFile.exists()) {
         try {
            FileOutputStream fos = new FileOutputStream(configPropsFile);
            this.properties.store(fos, "Inventory Tweaks Configuration\n(Regarding shortcuts, all key names can be found at: http://legacy.lwjgl.org/javadoc/org/lwjgl/input/Keyboard.html)");
            fos.flush();
            fos.close();
            this.storedConfigLastModified = InvTweaksConst.CONFIG_PROPS_FILE.lastModified();
         } catch (IOException var3) {
            InvTweaks.logInGameStatic("Failed to save config file " + InvTweaksConst.CONFIG_PROPS_FILE);
         }
      }

   }

   @NotNull
   public Map<String, String> getProperties(@NotNull String prefix) {
      Map<String, String> result = new HashMap();

      for(Object o : this.properties.keySet()) {
         String key = (String)o;
         if (key.startsWith(prefix)) {
            result.put(key, this.properties.getProperty(key));
         }
      }

      return result;
   }

   @NotNull
   public String getProperty(@NotNull String key) {
      String value = this.properties.getProperty(key);
      return value != null ? value : "";
   }

   public int getIntProperty(@NotNull String key) {
      return Integer.parseInt(this.getProperty(key));
   }

   public void setProperty(String key, String value) {
      this.properties.put(key, value);
      this.saveProperties();
   }

   public InvTweaksItemTree getTree() {
      return this.tree;
   }

   @Nullable
   public String getCurrentRulesetName() {
      return this.currentRulesetName;
   }

   @Nullable
   public String switchConfig(int i) {
      if (!this.rulesets.isEmpty() && i < this.rulesets.size() && i != this.currentRuleset) {
         this.currentRuleset = i;
         this.currentRulesetName = ((InvTweaksConfigInventoryRuleset)this.rulesets.get(this.currentRuleset)).getName();
         return this.currentRulesetName;
      } else {
         return null;
      }
   }

   @Nullable
   public String switchConfig() {
      return this.currentRuleset == -1 ? this.switchConfig(0) : this.switchConfig((this.currentRuleset + 1) % this.rulesets.size());
   }

   public List<InvTweaksConfigSortingRule> getRules() {
      return ((InvTweaksConfigInventoryRuleset)this.rulesets.get(this.currentRuleset)).getRules();
   }

   public List<String> getInvalidKeywords() {
      return this.invalidKeywords;
   }

   public int[] getLockPriorities() {
      return ((InvTweaksConfigInventoryRuleset)this.rulesets.get(this.currentRuleset)).getLockPriorities();
   }

   public boolean[] getFrozenSlots() {
      return ((InvTweaksConfigInventoryRuleset)this.rulesets.get(this.currentRuleset)).getFrozenSlots();
   }

   public boolean isAutoRefillEnabled(String itemID, int itemDamage) {
      if (this.getProperty("enableAutoRefill").equals("false")) {
         return false;
      } else {
         List<IItemTreeItem> items = this.tree.getItems(itemID, itemDamage);
         List<String> autoReplaceRules = ((InvTweaksConfigInventoryRuleset)this.rulesets.get(this.currentRuleset)).getAutoReplaceRules();
         boolean found = false;

         for(String keyword : autoReplaceRules) {
            if (keyword.equals("nothing")) {
               return false;
            }

            if (this.tree.matches(items, keyword)) {
               found = true;
            }
         }

         return found || autoReplaceRules.isEmpty();
      }
   }

   private void reset() {
      this.rulesets = new ArrayList();
      this.currentRuleset = -1;
      this.properties = new InvTweaksConfigProperties();
      this.properties.put("enableMiddleClick", "true");
      this.properties.put("showChestButtons", "true");
      this.properties.put("enableSortingOnPickup", "false");
      this.properties.put("enableAutoRefill", "true");
      this.properties.put("autoRefillBeforeBreak", "false");
      this.properties.put("autoRefillDamageThreshhold", "5");
      this.properties.put("enableSounds", "true");
      this.properties.put("enableShortcuts", "true");
      this.properties.put("enableAutoEquipArmor", "false");
      this.properties.put("enableServerItemSwap", "true");
      this.properties.put("enableConfigLoadedMesssage", "false");
      this.properties.put("invertToolDamageSorting", "true");
      this.properties.put("shortcutKeyAllItems", "LCONTROL+LSHIFT, RCONTROL+RSHIFT");
      this.properties.put("shortcutKeyEverything", "SPACE");
      this.properties.put("shortcutKeyOneItem", "LCONTROL, RCONTROL");
      this.properties.put("shortcutKeyToUpperSection", "UP");
      this.properties.put("shortcutKeyToLowerSection", "DOWN");
      this.properties.put("shortcutKeyDrop", "LALT, RALT");
      this.properties.put("enableToolTipTreePath", "false");
      this.properties.put("version", "1.64+dev.151.822d839".split(" ")[0]);
      this.invalidKeywords = new ArrayList();
   }

   private void loadProperties() throws IOException {
      File configPropsFile = getPropertyFile();
      InvTweaksConfigProperties newProperties = new InvTweaksConfigProperties();
      if (configPropsFile != null) {
         FileInputStream fis = new FileInputStream(configPropsFile);
         newProperties.load(fis);
         fis.close();
      }

      newProperties.sortKeys();
      if (newProperties.get("version") != null) {
         for(Entry<Object, Object> entry : newProperties.entrySet()) {
            this.properties.put(entry.getKey(), entry.getValue());
         }
      }

      this.properties.put("version", "1.64+dev.151.822d839".split(" ")[0]);
   }

   public int getSortKeyCode() {
      return ClientProxy.KEYBINDING_SORT.getKeyCode();
   }
}
