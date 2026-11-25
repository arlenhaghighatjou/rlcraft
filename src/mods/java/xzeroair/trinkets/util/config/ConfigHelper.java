package xzeroair.trinkets.util.config;

import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.compat.OreDictionaryCompat;
import xzeroair.trinkets.util.helpers.StringUtils;

public class ConfigHelper {
   private static String nameRegex = "([a-zA-Z0-9_*]{1,})";
   private static String itemIDRegex = "(" + nameRegex + ":" + nameRegex + ")";
   private static String materialRegex = "(material" + nameRegex + ")";
   private static String metaRegex = "(([0-9]*)|([*]))";
   private static String metaRegexOptional = "(;" + metaRegex + ")?";
   private static String doubleRegex = "(([-])?([0-9]{1,})([.][0-9]{1,})?)";
   private static String doubleRegexOptional = "(;" + doubleRegex + ")?";
   private static String optionalWordRegex = "(;[a-zA-Z]*)?";

   public ConfigHelper() {
   }

   public static String cleanConfigEntry(String config) {
      String configEntry = config.replaceAll("([\\[\\]\\|,;] ?)|(  )", " ").trim().replace(" ", ";");
      return configEntry;
   }

   public static ConfigHelper.AttributeEntry getAttributeEntry(String string) {
      String configEntry = cleanConfigEntry(string.replaceFirst("[nN][aA][mM][eE][:]", "").replaceFirst("[aA][mM][oO][uU][nN][tT][:]", "").replaceFirst("[oO][pP][eE][rR][aA][tT][iI][oO][nN][:]", ""));
      String[] vars = configEntry.split(";");
      String arg1 = StringUtils.getStringFromArray(vars, 0);
      String arg2 = StringUtils.getStringFromArray(vars, 1);
      String arg3 = StringUtils.getStringFromArray(vars, 2);
      String arg4 = StringUtils.getStringFromArray(vars, 3);
      String attributeRegex = "([a-zA-Z0-9_.]*)";
      String amountRegex = doubleRegex;
      String opRegex = "([0-2])";
      String boolRegex = "(true)|(false)";
      String attribute = "";
      double amount = 0.0D;
      int op = 0;
      boolean saved = false;
      boolean isAttribute = false;
      if (!arg1.isEmpty() && arg1.matches(attributeRegex)) {
         attribute = arg1;
         if (!arg2.isEmpty() && arg2.matches(amountRegex)) {
            try {
               amount = Double.parseDouble(arg2.replace("+", ""));
               isAttribute = true;
            } catch (Exception var19) {
               var19.printStackTrace();
            }

            if (!arg3.isEmpty() && arg3.matches(opRegex)) {
               try {
                  op = Integer.parseInt(arg3);
               } catch (Exception var18) {
                  var18.printStackTrace();
               }
            }

            if (!arg4.isEmpty() && arg4.toLowerCase().matches(boolRegex) && arg4.equalsIgnoreCase("true")) {
               saved = true;
            }
         }
      }

      return isAttribute && !attribute.isEmpty() && amount != 0.0D ? new ConfigHelper.AttributeEntry(attribute, amount, op, saved) : null;
   }

   private static <T> T continueCommand(String string, Function<String, T> func) {
      return (T)func.apply(string);
   }

   private static <T> T continueCommand(String arg1, String arg2, BiFunction<String, String, T> func) {
      return (T)func.apply(arg1, arg2);
   }

   public static class ArmorEntry extends ConfigHelper.ConfigObject {
      protected double weight;

      public ArmorEntry(String configEntry) {
         super(configEntry);
         this.weight = TrinketsConfig.SERVER.races.faelis.penalty_amount;
         this.initValues(this.getObjectArgs());
      }

      public boolean doesItemMatchEntry(ItemStack stack) {
         if (stack != null && !stack.isEmpty() && !this.isEmpty()) {
            boolean superMatched = super.doesItemMatchEntry(stack);
            if (stack.getItem() instanceof ItemArmor) {
               ItemArmor armor = (ItemArmor)stack.getItem();
               String armorType = armor.armorType.getName();
               String optionalType = StringUtils.getStringFromArray(this.getObjectArgs(), 0);
               if (!optionalType.isEmpty() && optionalType.matches("([a-zA-Z]{1,})")) {
                  String fixedType = this.fixArmorType(optionalType);
                  return superMatched && armorType.equalsIgnoreCase(fixedType);
               }
            }

            return superMatched;
         } else {
            return false;
         }
      }

      private void initValues(String[] args) {
         String optionalType = StringUtils.getStringFromArray(this.getObjectArgs(), 0);
         if (!optionalType.isEmpty()) {
            if (optionalType.matches(ConfigHelper.doubleRegex)) {
               try {
                  this.weight = Double.parseDouble(optionalType);
               } catch (Exception var6) {
                  Trinkets.log.error("Invalid format for entry: " + this.getOriginalEntry());
                  var6.printStackTrace();
                  this.weight = 0.0D;
               }
            } else {
               String optionalWeight = StringUtils.getStringFromArray(this.getObjectArgs(), 1);
               if (!optionalWeight.isEmpty() && optionalWeight.matches(ConfigHelper.doubleRegex)) {
                  try {
                     this.weight = Double.parseDouble(optionalWeight);
                  } catch (Exception var5) {
                     Trinkets.log.error("Invalid format for entry: " + this.getOriginalEntry());
                     var5.printStackTrace();
                     this.weight = 0.0D;
                  }
               }
            }
         }

      }

      protected String fixArmorType(String string) {
         return string.replace("boot", "feet").replace("boots", "feet").replace("feets", "feet").replace("leggings", "legs").replace("pants", "legs").replace("chestplate", "chest").replace("shirt", "chest").replace("helm", "head").replace("hat", "head");
      }

      public final double getWeight() {
         return this.weight;
      }
   }

   public static class AttributeEntry {
      private String attribute;
      private double amount;
      private int operation;
      private boolean isSaved;

      public AttributeEntry(String attribute, double amount, int operation, boolean isSaved) {
         this.attribute = attribute;
         this.amount = amount;
         this.operation = operation;
         this.isSaved = isSaved;
      }

      public String getAttribute() {
         return this.attribute;
      }

      public double getAmount() {
         return this.amount;
      }

      public int getOperation() {
         return this.operation;
      }

      public boolean isSaved() {
         return this.isSaved;
      }

      public String toString() {
         return "AttributeEntry [attribute=" + this.attribute + ", amount=" + this.amount + ", operation=" + this.operation + ", isSaved=" + this.isSaved + "]";
      }
   }

   public static class ConfigObject {
      protected String originalEntry;
      protected String modID;
      protected String objectID;
      protected int meta;
      protected String[] objectArgs;
      protected ConfigHelper.EntryType objectType;

      public ConfigObject(String modID, String objectID, int meta) {
         this(modID, objectID, meta, new String[0]);
      }

      public ConfigObject(String config) {
         this("minecraft", "air", OreDictionaryCompat.wildcard);
         this.originalEntry = config;
         this.generateConfigObject(config);
      }

      public ConfigObject(String modID, String objectID, int meta, String[] args) {
         this.originalEntry = modID + ":" + objectID + ";" + meta;
         this.modID = modID;
         this.objectID = objectID;
         this.meta = meta;
         this.objectType = ConfigHelper.EntryType.UNKNOWN;
         this.objectArgs = args;
      }

      public final String getModID() {
         return this.modID;
      }

      public final String getObjectID() {
         return this.objectID;
      }

      public final String getObjectRegistryName() {
         return this.getModID() + ":" + this.getObjectID();
      }

      public final int getMeta() {
         return this.meta;
      }

      public final ConfigHelper.EntryType getObjectType() {
         return this.objectType;
      }

      public final String[] getObjectArgs() {
         return this.objectArgs;
      }

      public final String getOriginalEntry() {
         return this.originalEntry;
      }

      public final boolean isEmpty() {
         String reg = this.getObjectRegistryName();
         boolean flag1 = reg.contentEquals("minecraft:air");
         boolean flag2 = reg.contentEquals("*:air");
         return flag1 || flag2;
      }

      protected void generateConfigObject(String configEntry) {
         configEntry = ConfigHelper.cleanConfigEntry(configEntry);
         String[] checkArgs = configEntry.split(";", 2);
         String entry = StringUtils.getStringFromArray(checkArgs, 0);
         if (!entry.isEmpty()) {
            String[] configArgs = StringUtils.getStringFromArray(checkArgs, 1).split(";", 2);
            boolean existsInOreDict = OreDictionaryCompat.existsInOreDictionary(entry);
            if (existsInOreDict) {
               this.objectType = ConfigHelper.EntryType.OREDICTIONARY;
               this.modID = "oreDict";
               this.objectID = entry;
               String leftoverArgs = StringUtils.getStringFromArray(configArgs, 0);
               if (!leftoverArgs.isEmpty()) {
                  this.objectArgs = leftoverArgs.split(";");
               }
            } else if (EntityList.isRegistered(new ResourceLocation(entry))) {
               this.objectType = ConfigHelper.EntryType.ENTITY;
               if (entry.contains(":")) {
                  String[] entryArgs = entry.split(":");
                  String mod = StringUtils.getStringFromArray(entryArgs, 0);
                  String entity = StringUtils.getStringFromArray(entryArgs, 1);
                  if (!mod.isEmpty()) {
                     this.modID = mod;
                  }

                  if (!entity.isEmpty()) {
                     this.objectID = entity;
                  }
               } else {
                  this.objectID = entry;
               }

               String leftoverArgs = StringUtils.getStringFromArray(configArgs, 0);
               if (!leftoverArgs.isEmpty()) {
                  this.objectArgs = leftoverArgs.split(";");
               }
            } else if (entry.matches(ConfigHelper.itemIDRegex)) {
               this.objectType = ConfigHelper.EntryType.NORMAL;
               String[] itemArgs = entry.split(":");
               String mod = StringUtils.getStringFromArray(itemArgs, 0);
               String item = StringUtils.getStringFromArray(itemArgs, 1);
               if (!mod.isEmpty()) {
                  this.modID = mod;
               }

               if (!item.isEmpty()) {
                  this.objectID = item;
               }

               if (mod.equalsIgnoreCase("minecraft") && item.equalsIgnoreCase("potion")) {
                  String leftoverArgs = StringUtils.getStringFromArray(configArgs, 0);
                  if (!leftoverArgs.isEmpty()) {
                     this.objectArgs = leftoverArgs.split(";");
                  }
               } else {
                  String Meta = StringUtils.getStringFromArray(configArgs, 0);
                  if (!Meta.contentEquals("*") && Meta.matches("([0-9]{1,})")) {
                     try {
                        int wildcard = OreDictionaryCompat.wildcard;
                        this.meta = Integer.parseInt(Meta);
                        if (this.meta > wildcard) {
                           this.meta = wildcard;
                        }

                        if (this.meta < 0) {
                           this.meta = wildcard;
                        }
                     } catch (Exception var11) {
                        var11.printStackTrace();
                     }
                  }

                  String leftoverArgs = StringUtils.getStringFromArray(configArgs, 1);
                  if (!leftoverArgs.isEmpty()) {
                     this.objectArgs = leftoverArgs.split(";");
                  }
               }
            } else if (entry.startsWith("material") && !entry.replaceFirst("material", "").isEmpty()) {
               this.objectType = ConfigHelper.EntryType.MATERIAL;
               this.modID = "ObjectMaterial";
               this.objectID = entry.replaceFirst("material", "");
               String leftoverArgs = StringUtils.getStringFromArray(configArgs, 0);
               if (!leftoverArgs.isEmpty()) {
                  this.objectArgs = leftoverArgs.split(";");
               }
            } else {
               this.objectType = ConfigHelper.EntryType.UNKNOWN;
               this.modID = "unknown";
               this.objectID = entry;
               String leftoverArgs = StringUtils.getStringFromArray(configArgs, 0);
               if (!leftoverArgs.isEmpty()) {
                  this.objectArgs = leftoverArgs.split(";");
               }
            }
         }

      }

      public boolean doesItemMatchEntry(@Nonnull ItemStack stack) {
         if (stack != null && !stack.isEmpty() && !this.isEmpty()) {
            ConfigHelper.EntryType type = this.getObjectType();
            if (type.equals(ConfigHelper.EntryType.OREDICTIONARY)) {
               for(String oreDictionary : OreDictionaryCompat.getOreNames(stack)) {
                  if (oreDictionary.equalsIgnoreCase(this.objectID)) {
                     return true;
                  }
               }

               return false;
            } else if (type.equals(ConfigHelper.EntryType.MATERIAL)) {
               if (stack.getItem() instanceof ItemArmor) {
                  ItemArmor armor = (ItemArmor)stack.getItem();
                  ArmorMaterial material = armor.getArmorMaterial();
                  return material.toString().equalsIgnoreCase(this.objectID);
               } else if (stack.getItem() instanceof ItemTool) {
                  ItemTool tool = (ItemTool)stack.getItem();
                  return tool.getToolMaterialName().equalsIgnoreCase(this.objectID);
               } else if (stack.getItem() instanceof ItemSword) {
                  ItemSword sword = (ItemSword)stack.getItem();
                  return sword.getToolMaterialName().equalsIgnoreCase(this.objectID);
               } else if (stack.getItem() instanceof ItemHoe) {
                  ItemHoe sword = (ItemHoe)stack.getItem();
                  return sword.getMaterialName().equalsIgnoreCase(this.objectID);
               } else {
                  return false;
               }
            } else {
               String mod = stack.getItem().getRegistryName().getResourceDomain();
               String item = stack.getItem().getRegistryName().getResourcePath();
               if (this.modID.contentEquals("*") || this.modID.contentEquals(mod)) {
                  boolean metaMatches = this.meta == OreDictionaryCompat.wildcard || stack.getMetadata() == this.meta;
                  if (this.objectID.contentEquals(item) && metaMatches) {
                     return true;
                  }

                  String objectEntry = this.objectID.replace("*", "");
                  boolean startWildcard = this.objectID.startsWith("*");
                  boolean endWildcard = this.objectID.endsWith("*");
                  if (startWildcard && endWildcard) {
                     if (item.contains(objectEntry) && metaMatches) {
                        return true;
                     }
                  } else if (endWildcard) {
                     if (item.startsWith(objectEntry) && metaMatches) {
                        return true;
                     }
                  } else if (startWildcard && item.endsWith(objectEntry) && metaMatches) {
                     return true;
                  }
               }

               return false;
            }
         } else {
            return false;
         }
      }

      public boolean doesBlockMatchEntry(@Nonnull IBlockState state) {
         if (state != null && !this.isEmpty()) {
            ItemStack blockStack = new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state));
            return this.doesItemMatchEntry(blockStack);
         } else {
            return false;
         }
      }
   }

   public static enum EntryType {
      UNKNOWN,
      NORMAL,
      ITEM,
      BLOCK,
      ENTITY,
      MATERIAL,
      POTION,
      OREDICTIONARY;

      private EntryType() {
      }
   }

   public static class MPRecoveryItem extends ConfigHelper.ConfigObject {
      protected float amount;
      protected boolean multiplied;

      public MPRecoveryItem(String configEntry) {
         super(configEntry);
         this.initValues(this.getObjectArgs());
      }

      private void initValues(String[] args) {
         String Amount = StringUtils.getStringFromArray(args, 0);
         if (!Amount.isEmpty()) {
            if (Amount.endsWith("%")) {
               this.multiplied = true;
            }

            Amount = Amount.replace("%", "");

            try {
               this.amount = Float.parseFloat(Amount);
            } catch (Exception var4) {
               Trinkets.log.error("Invalid format for entry: " + this.getOriginalEntry());
               var4.printStackTrace();
               this.amount = 0.0F;
            }
         }

      }

      public final float getAmount() {
         return this.amount;
      }

      public final boolean isMultiplied() {
         return this.multiplied;
      }
   }

   public static class ObjectPair<A, B> {
      private A first;
      private B second;

      public ObjectPair(A first, B second) {
         this.first = first;
         this.second = second;
      }

      public A getFirst() {
         return this.first;
      }

      public B getSecond() {
         return this.second;
      }
   }

   public static class PotionEntry extends ConfigHelper.ConfigObject {
      protected Potion potion;
      protected PotionEffect effect;
      protected int duration;
      protected int amplifier;

      public PotionEntry(String ConfigEntry) {
         super(ConfigEntry);
      }

      public boolean doesItemMatchEntry(ItemStack stack) {
         return stack != null && !stack.isEmpty() && !this.isEmpty() ? super.doesItemMatchEntry(stack) : false;
      }
   }

   public static class TreasureEntry extends ConfigHelper.ConfigObject {
      protected int color = 16766720;

      public TreasureEntry(String configEntry) {
         super(configEntry);
         this.initValues(this.getObjectArgs());
      }

      private void initValues(String[] args) {
         String Color = StringUtils.getStringFromArray(args, 0);
         if (!Color.isEmpty()) {
            try {
               this.color = Integer.parseInt(Color.replace("*", OreDictionaryCompat.wildcard + ""));
            } catch (Exception var4) {
               Trinkets.log.error("Invalid format for entry: " + this.getOriginalEntry());
               var4.printStackTrace();
               this.color = 16766720;
            }
         }

      }

      public final int getColor() {
         return this.color;
      }
   }

   public static class TrinketConfigStorage {
      public static TreeMap<String, ConfigHelper.MPRecoveryItem> MagicRecoveryItems = new TreeMap();
      public static TreeMap<Integer, ConfigHelper.TreasureEntry> TreasureBlocks = new TreeMap();
      public static TreeMap<String, ConfigHelper.ConfigObject> ClimbableBlocks = new TreeMap();
      public static TreeMap<String, ConfigHelper.ArmorEntry> ArmorWeightValues = new TreeMap();

      public TrinketConfigStorage() {
      }

      public static void init() {
         initRecoveryItems();
         initArmorWeightValues();
         initTreasureBlocks();
      }

      private static void initTreasureBlocks() {
         if (!TreasureBlocks.isEmpty()) {
            TreasureBlocks.clear();
         }

         String[] treasures = TrinketsConfig.SERVER.Items.DRAGON_EYE.BLOCKS.Blocks;
         int index = 0;

         for(String entry : treasures) {
            ConfigHelper.TreasureEntry treasure = new ConfigHelper.TreasureEntry(entry);
            if (!treasure.isEmpty()) {
               TreasureBlocks.put(Integer.valueOf(index), treasure);
               ++index;
            }
         }

      }

      private static void initArmorWeightValues() {
         if (!ArmorWeightValues.isEmpty()) {
            ArmorWeightValues.clear();
         }

         String[] weightValues = TrinketsConfig.SERVER.races.faelis.heavyArmor;

         for(String entry : weightValues) {
            ConfigHelper.ArmorEntry weightValue = new ConfigHelper.ArmorEntry(entry);
            if (!weightValue.isEmpty()) {
               ArmorWeightValues.put(weightValue.getObjectRegistryName(), weightValue);
            }
         }

      }

      private static void initRecoveryItems() {
         if (!MagicRecoveryItems.isEmpty()) {
            MagicRecoveryItems.clear();
         }

         String[] recovery = TrinketsConfig.SERVER.mana.recovery;

         for(String entry : recovery) {
            ConfigHelper.MPRecoveryItem recoveryItem = new ConfigHelper.MPRecoveryItem(entry);
            if (!recoveryItem.isEmpty()) {
               MagicRecoveryItems.put(recoveryItem.getObjectRegistryName(), recoveryItem);
            }
         }

      }
   }
}
