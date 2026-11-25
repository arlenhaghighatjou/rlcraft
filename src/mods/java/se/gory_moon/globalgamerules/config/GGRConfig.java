package se.gory_moon.globalgamerules.config;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.config.Property.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import se.gory_moon.globalgamerules.reference.Reference;

public class GGRConfig extends Configuration {
   public static final String CATEGORY_GAMERULES = "gamerules";
   public static final String CATEGORY_MISC = "misc";
   public static final String MISC_WORLDDIFFICULTY = "worldDifficulty";
   public static final String MISC_WORLDDIFFICULTYLOCK = "worldDifficultyLocked";
   public static final String MISC_SAVEGAMRULES = "saveGameRules";
   public static final String MISC_CUSTOMGAMERULES = "customGamerules";
   public HashMap<String, GGRConfig.Value> rules = new HashMap();
   public HashMap<String, GGRConfig.Value> misc = new HashMap();
   public HashMap<String, GGRConfig.Value> defaults;
   public HashMap<String, String> comments = new HashMap();
   public HashMap<String, Pair<String, GGRConfig.Value>> custom = new HashMap();

   public GGRConfig(VFile2 name) {
      super(name);
      this.rules.put("commandBlockOutput", this.defaultValue(true));
      this.rules.put("disableElytraMovementCheck", this.defaultValue(false));
      this.rules.put("doDaylightCycle", this.defaultValue(true));
      this.rules.put("doEntityDrops", this.defaultValue(true));
      this.rules.put("doFireTick", this.defaultValue(true));
      this.rules.put("doLimitedCrafting", this.defaultValue(false));
      this.rules.put("doMobLoot", this.defaultValue(true));
      this.rules.put("doMobSpawning", this.defaultValue(true));
      this.rules.put("doTileDrops", this.defaultValue(true));
      this.rules.put("doWeatherCycle", this.defaultValue(true));
      this.rules.put("gameLoopFunction", this.defaultValue("-"));
      this.rules.put("keepInventory", this.defaultValue(false));
      this.rules.put("logAdminCommands", this.defaultValue(true));
      this.rules.put("maxCommandChainLength", this.defaultValue(65536));
      this.rules.put("maxEntityCramming", this.defaultValue(24));
      this.rules.put("mobGriefing", this.defaultValue(true));
      this.rules.put("naturalRegeneration", this.defaultValue(true));
      this.rules.put("randomTickSpeed", this.defaultValue(3));
      this.rules.put("reducedDebugInfo", this.defaultValue(false));
      this.rules.put("sendCommandFeedback", this.defaultValue(true));
      this.rules.put("showDeathMessages", this.defaultValue(true));
      this.rules.put("spawnRadius", this.defaultValue(10));
      this.rules.put("spectatorsGenerateChunks", this.defaultValue(true));
      this.rules.put("announceAdvancements", this.defaultValue(true));
      this.misc.put("worldDifficulty", this.defaultValue(-1));
      this.misc.put("worldDifficultyLocked", this.defaultValue(false));
      this.misc.put("saveGameRules", new GGRConfig.Value("true", GGRConfig.ValueType.BOOLEAN, true, false));
      this.comments.put("commandBlockOutput", "Whether command blocks should notify admins when they perform commands");
      this.comments.put("disableElytraMovementCheck", "Whether the server should skip checking player speed when the player is wearing elytra.");
      this.comments.put("doDaylightCycle", "Whether the day-night cycle and moon phases progress");
      this.comments.put("doEntityDrops", "Whether entities that are not mobs should have drops");
      this.comments.put("doFireTick", "Whether fire should spread and naturally extinguish");
      this.comments.put("doLimitedCrafting", "Whether players should only be able to craft recipes that they've unlocked first");
      this.comments.put("doMobLoot", "Whether mobs should drop items");
      this.comments.put("doMobSpawning", "Whether mobs should naturally spawn");
      this.comments.put("doTileDrops", "Whether blocks should have drops");
      this.comments.put("doWeatherCycle", "Whether the weather will change");
      this.comments.put("gameLoopFunction", "The function to run every game tick");
      this.comments.put("keepInventory", "Whether the player should keep items in their inventory after death");
      this.comments.put("logAdminCommands", "Whether to log admin commands to server log");
      this.comments.put("maxCommandChainLength", "Determines the number at which the chain command block acts as a \"chain\".");
      this.comments.put("maxEntityCramming", "The maximum number of other pushable entities a mob or player can push, before taking 3 suffocation damage\nper half-second. Setting to 0 disables the rule. Damage affects survival-mode or adventure-mode players, and all mobs but bats.\nPushable entities include non-spectator-mode players, any mob except bats, as well as boats and minecarts.");
      this.comments.put("mobGriefing", "Whether creepers, zombies, endermen, ghasts, withers, ender dragons, rabbits, sheep, and villagers should be able to change blocks\nand whether villagers, zombies, skeletons, and zombie pigmen can pick up items");
      this.comments.put("naturalRegeneration", "Whether the player can regenerate health naturally if their hunger is full enough (doesn't affect external healing, such as\ngolden apples, the Regeneration effect, etc.)");
      this.comments.put("randomTickSpeed", "How often a random block tick occurs (such as plant growth, leaf decay, etc.) per chunk section per game tick. 0 will disable random\nticks, higher numbers will increase random ticks");
      this.comments.put("reducedDebugInfo", "Whether the debug screen shows all or reduced information; and whether the effects of F3+B (entity hitboxes) and F3+G (chunk boundaries) are shown.");
      this.comments.put("sendCommandFeedback", "Whether the feedback from commands executed by a player should show up in chat. Also affects the default behavior of whether command blocks store their output text");
      this.comments.put("showDeathMessages", "Whether a message appears in chat when a player dies");
      this.comments.put("spawnRadius", "The number of blocks outward from the world spawn coordinates that a player will spawn in when first joining a server or when dying without a spawnpoint.");
      this.comments.put("spectatorsGenerateChunks", "Whether players in spectator mode can generate chunks");
      this.comments.put("announceAdvancements", "If an announcement when a player gets an advancement should be done");
      this.comments.put("worldDifficulty", "Sets the difficulty of a world when loaded, respects it the difficulty is locked or not for the world\n-1: Disabled\n0: Peaceful\n1: Easy\n2: Normal\n3: Hard");
      this.comments.put("worldDifficultyLocked", "If a world's difficulty should be locked when loaded, if world already is locked it can't be change\nIf the global world difficulty is enabled it's set first");
      this.comments.put("saveGameRules", "If gamerules and world difficulty should be saved to config on world unload");
      this.addCustomCategoryComment("gamerules", "Set the values to ('true'/'false'/an integer or a string defaultValue) depending if you want to have the GameRule (enabled/disabled or have a different defaultValue)");
      this.addCustomCategoryComment("misc", "A collection of misc configs");
      this.defaults = (HashMap)this.rules.clone();
      this.defaults.putAll((Map)this.misc.clone());
   }

   private GGRConfig.Value defaultValue(boolean val) {
      return new GGRConfig.Value(Boolean.toString(val), GGRConfig.ValueType.BOOLEAN);
   }

   private GGRConfig.Value defaultValue(int val) {
      return new GGRConfig.Value(Integer.toString(val), GGRConfig.ValueType.INTEGER);
   }

   private GGRConfig.Value defaultValue(String val) {
      return new GGRConfig.Value(val, GGRConfig.ValueType.STRING);
   }

   public GGRConfig loadConfig() {
      this.load();
      this.syncConfigs();
      this.saveConfig();
      this.save();
      return this;
   }

   public void saveConfig() {
      ConfigCategory ruleCat = this.getCategory("gamerules");
      this.setValueToProp(ruleCat, this.rules);
      ConfigCategory miscCat = this.getCategory("misc");
      this.setValueToProp(miscCat, this.misc);
      String[] vals = (String[])((List)this.custom.entrySet().stream().map((stringPairEntry) -> {
         return (String)stringPairEntry.getKey() + "-" + ((GGRConfig.Value)((Pair)stringPairEntry.getValue()).getValue()).stringValue + "-" + (String)((Pair)stringPairEntry.getValue()).getKey();
      }).collect(Collectors.toList())).toArray(new String[0]);
      miscCat.get("customGamerules").setValues(vals);
      if (this.hasChanged()) {
         this.save();
      }

   }

   private void setValueToProp(ConfigCategory cat, HashMap<String, GGRConfig.Value> list) {
      list.forEach((s, value) -> {
         if (cat.containsKey(s)) {
            cat.put(s, value.getType().equals(GGRConfig.ValueType.BOOLEAN) ? cat.get(s).setValue(value.getBooleanValue()).setRequiresWorldRestart(value.getRequireWorldRestart()).setShowInGui(value.getShowInGui()) : (value.getType().equals(GGRConfig.ValueType.INTEGER) ? cat.get(s).setValue(value.getIntegerValue()).setRequiresWorldRestart(value.getRequireWorldRestart()).setShowInGui(value.getShowInGui()) : cat.get(s).setValue(value.getStringValue()).setRequiresWorldRestart(value.getRequireWorldRestart()).setShowInGui(value.getShowInGui())));
         } else {
            this.get(cat.getQualifiedName(), s, value.getStringValue(), "GameRule added by another mod or from commands").setRequiresWorldRestart(value.getRequireWorldRestart()).setShowInGui(value.getShowInGui());
         }

      });
   }

   public void syncConfigs() {
      this.syncConfigs(this.getCategory("gamerules").getValues(), this.rules, "gamerules");
      this.syncConfigs(this.getCategory("misc").getValues(), this.misc, "misc");
      this.custom.clear();
      String[] list = this.get("misc", "customGamerules", new String[]{"GGRExample-false-/tell @p Hello World"}, "Syntax is: name-enabled-command\nenabled is either true or false, all @p will be replaced with the playername").getStringList();

      for(String s : list) {
         String[] parts = s.split("-");
         if (parts.length >= 3 && !parts[0].isEmpty() && !parts[0].contains(" ") && !parts[1].isEmpty() && !parts[1].contains(" ")) {
            String command = String.join("-", (CharSequence[])Arrays.copyOfRange(parts, 2, parts.length));
            this.custom.put(parts[0], MutablePair.of(command, new GGRConfig.Value(parts[1], GGRConfig.ValueType.BOOLEAN, true)));
         } else {
            Reference.logger.error("Malformed custom gamerule: " + s);
         }
      }

      if (this.hasChanged()) {
         this.save();
      }

   }

   private void syncConfigs(Map<String, Property> values, HashMap<String, GGRConfig.Value> list, String cat) {
      list.forEach((s, value) -> {
         GGRConfig.Value var10000 = (GGRConfig.Value)list.put(s, new GGRConfig.Value(value.getType().equals(GGRConfig.ValueType.BOOLEAN) ? String.valueOf(this.get(cat, s, ((GGRConfig.Value)this.defaults.get(s)).getBooleanValue(), (String)this.comments.get(s)).getBoolean()) : (value.getType().equals(GGRConfig.ValueType.INTEGER) ? String.valueOf(this.get(cat, s, ((GGRConfig.Value)this.defaults.get(s)).getIntegerValue(), (String)this.comments.get(s)).getInt()) : this.get(cat, s, ((GGRConfig.Value)this.defaults.get(s)).getStringValue(), (String)this.comments.get(s)).getString()), value.getType(), value.getShowInGui()));
      });
      values.forEach((s, value) -> {
         GGRConfig.Value var10000 = (GGRConfig.Value)list.put(s, new GGRConfig.Value(value.getType().equals(Type.BOOLEAN) ? String.valueOf(this.get(cat, s, ((GGRConfig.Value)this.defaults.get(s)).getBooleanValue(), this.getComment(s)).getBoolean()) : (value.getType().equals(Type.INTEGER) ? String.valueOf(this.get(cat, s, ((GGRConfig.Value)this.defaults.get(s)).getIntegerValue(), this.getComment(s)).getInt()) : this.get(cat, s, this.defaults.get(s) != null ? ((GGRConfig.Value)this.defaults.get(s)).getStringValue() : value.getString(), this.getComment(s)).getString()), value.getType().equals(Type.BOOLEAN) ? GGRConfig.ValueType.BOOLEAN : (value.getType().equals(Type.INTEGER) ? GGRConfig.ValueType.INTEGER : GGRConfig.ValueType.STRING), list.get(s) != null ? ((GGRConfig.Value)list.get(s)).getShowInGui() : value.showInGui(), list.get(s) != null ? ((GGRConfig.Value)list.get(s)).getRequireWorldRestart() : "gamerules".equals(cat) || value.requiresWorldRestart()));
      });
   }

   private String getComment(String s) {
      return this.comments.get(s) != null ? (String)this.comments.get(s) : "GameRule added by another mod or from commands";
   }

   public void register(EventBus bus) {
      bus.register(OnConfigChangedEvent.class, this::onConfigChanged);
   }

   public void onConfigChanged(OnConfigChangedEvent event) {
      if (event.getModID().equals("globalgamerules")) {
         this.syncConfigs();
      }

   }

   public static class Value implements Cloneable {
      private String stringValue;
      private int integerValue;
      private boolean booleanValue;
      private boolean showInGui;
      private boolean requireWorldRestart;
      private GGRConfig.ValueType type;

      public Value(String s, GGRConfig.ValueType type) {
         this(s, type, true);
      }

      public Value(String s, GGRConfig.ValueType type, boolean showInGui) {
         this(s, type, showInGui, true);
      }

      public Value(String s, GGRConfig.ValueType type, boolean showInGui, boolean requireWorldRestart) {
         this.type = type;
         this.showInGui = showInGui;
         this.requireWorldRestart = requireWorldRestart;
         this.setValue(s);
      }

      private void setValue(String s) {
         this.stringValue = s;
         this.booleanValue = Boolean.parseBoolean(s);
         this.integerValue = this.booleanValue ? 1 : 0;

         try {
            this.integerValue = Integer.parseInt(s);
         } catch (NumberFormatException var3) {
            ;
         }

      }

      public int getIntegerValue() {
         return this.integerValue;
      }

      public boolean getBooleanValue() {
         return this.booleanValue;
      }

      public String getStringValue() {
         return this.stringValue;
      }

      public boolean getShowInGui() {
         return this.showInGui;
      }

      public boolean getRequireWorldRestart() {
         return this.requireWorldRestart;
      }

      public GGRConfig.ValueType getType() {
         return this.type;
      }

      protected GGRConfig.Value clone() throws CloneNotSupportedException {
         return (GGRConfig.Value)super.clone();
      }
   }

   public static enum ValueType {
      BOOLEAN,
      INTEGER,
      STRING;

      private ValueType() {
      }
   }
}
