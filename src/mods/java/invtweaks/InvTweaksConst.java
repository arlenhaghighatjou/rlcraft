package invtweaks;

import java.io.File;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class InvTweaksConst {
   public static final String MOD_VERSION = "1.64+dev.151.822d839";
   public static final String TREE_VERSION = "1.12.2";
   public static final String INVTWEAKS_CHANNEL = "InventoryTweaks";
   public static final byte PROTOCOL_VERSION = 2;
   public static final byte PACKET_LOGIN = 0;
   public static final byte PACKET_CLICK = 1;
   public static final byte PACKET_SORTCOMPLETE = 2;
   public static final int RULESET_SWAP_DELAY = 1000;
   public static final int POLLING_DELAY = 3;
   public static final int CHEST_ALGORITHM_SWAP_MAX_INTERVAL = 2000;
   public static final int TOOLTIP_DELAY = 800;
   public static final File MINECRAFT_DIR;
   public static final File MINECRAFT_CONFIG_DIR;
   public static final File INVTWEAKS_CONFIG_DIR;
   public static final File INVTWEAKS_TREES_DIR;
   public static final File CONFIG_PROPS_FILE;
   public static final File CONFIG_RULES_FILE;
   public static final File CONFIG_TREE_FILE;
   public static final File OLD_CONFIG_TREE_FILE;
   public static final File OLDER_CONFIG_TREE_FILE;
   public static final File OLDER_CONFIG_RULES_FILE;
   public static final File TEMP_DIR = new File(System.getProperty("java.io.tmpdir"));
   public static final File MERGED_TREE_FILE = new File(TEMP_DIR, "InvTweaksTree.txt");
   public static final File MERGED_TREE_FILE_ALT;
   public static final String INVTWEAKS_RESOURCE_DOMAIN = "inventorytweaks";
   public static final ResourceLocation DEFAULT_CONFIG_FILE = new ResourceLocation("inventorytweaks", "defaultconfig.dat");
   public static final ResourceLocation DEFAULT_CONFIG_TREE_FILE = new ResourceLocation("inventorytweaks", "itemtree.xml");
   public static final String HELP_URL = "http://inventory-tweaks.readthedocs.org";
   public static final String TREE_URL = "https://github.com/IMarvinTPA/InventoryTweaksTrees";
   public static final String INGAME_LOG_PREFIX = "InvTweaks: ";
   public static final Level DEBUG = Level.INFO;
   public static final int JIMEOWAN_ID = 54696386;
   public static final int INVENTORY_SIZE = 36;
   public static final int INVENTORY_ROW_SIZE = 9;
   public static final int HOTBAR_SIZE = 9;
   public static final int INVENTORY_HOTBAR_SIZE = 9;
   public static final int DAMAGE_WILDCARD = 32767;

   public InvTweaksConst() {
   }

   static {
      MINECRAFT_DIR = Minecraft.getMinecraft().mcDataDir;
      MINECRAFT_CONFIG_DIR = new File(MINECRAFT_DIR, "config/");
      INVTWEAKS_CONFIG_DIR = new File(MINECRAFT_CONFIG_DIR, "InvTweaks/");
      INVTWEAKS_TREES_DIR = new File(INVTWEAKS_CONFIG_DIR, "trees/");
      CONFIG_PROPS_FILE = new File(INVTWEAKS_CONFIG_DIR, "InvTweaks.cfg");
      CONFIG_RULES_FILE = new File(INVTWEAKS_CONFIG_DIR, "InvTweaksRules.txt");
      CONFIG_TREE_FILE = new File(INVTWEAKS_CONFIG_DIR, "InvTweaksTree.txt");
      OLD_CONFIG_TREE_FILE = new File(MINECRAFT_CONFIG_DIR, "InvTweaksTree.txt");
      OLDER_CONFIG_TREE_FILE = new File(MINECRAFT_CONFIG_DIR, "InvTweaksTree.xml");
      OLDER_CONFIG_RULES_FILE = new File(MINECRAFT_DIR, "InvTweaksRules.txt");
      MERGED_TREE_FILE_ALT = new File(INVTWEAKS_TREES_DIR, "InvTweaksTree.txt");
   }
}
