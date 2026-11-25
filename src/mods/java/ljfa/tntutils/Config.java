package ljfa.tntutils;

import java.io.File;
import java.util.IdentityHashMap;
import java.util.Map;
import ljfa.tntutils.exception.InvalidConfigValueException;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Config {
   public static Configuration conf;
   public static final String CAT_GENERAL = "general";
   public static final String CAT_BLOCKDMG = "block_damage";
   public static final String CAT_ENTDMG = "entity_damage";
   public static boolean explosionCommand;
   public static float sizeMultiplier;
   public static float dropChanceModifier;
   public static boolean disableExplosions;
   public static boolean preventChainExpl;
   public static boolean disableTNT;
   public static boolean disableTNTMinecart;
   public static boolean disableBlockDamage;
   public static boolean disableCreeperBlockDamage;
   public static Map<Block, Integer> blackWhiteList;
   public static boolean listIsWhitelist;
   public static boolean blacklistActive;
   public static boolean spareTileEntities;
   public static boolean disableEntityDamage;
   public static boolean disablePlayerDamage;
   public static boolean disableItemDamage;
   public static boolean disableNPCDamage;

   public Config() {
   }

   public static void loadConfig(File file) {
      if (conf == null) {
         conf = new Configuration(file);
      }

      conf.load();
      loadValues();
      MinecraftForge.EVENT_BUS.register(new Config.ChangeHandler());
   }

   public static void loadValues() {
      explosionCommand = conf.get("general", "addExplosionCommand", true, "Adds the \"/explosion\" command").setRequiresMcRestart(true).getBoolean();
      sizeMultiplier = (float)conf.get("general", "sizeMultiplier", 1.0D, "Multiplies the size of all explosions by this", 0.0D, 50.0D).getDouble();
      dropChanceModifier = (float)conf.get("general", "dropChanceIncrease", 0.0D, "Modifies the chance that explosions will drop destroyed blocks as items\n-1 = never drop anything, 0 = Vanilla behavior, 1 = always drop items.\nIncreasing will only affect explosions of size <= 10 since a large number of dropped items can cause lag.", -1.0D, 1.0D).getDouble();
      disableExplosions = conf.get("general", "disableExplosions", false, "Entirely disables all effects from explosions").getBoolean();
      preventChainExpl = conf.get("general", "preventChainExplosions", false, "Prevents explosions from triggering TNT and thus disables chain explosions").getBoolean();
      disableTNT = conf.get("general", "disableTNT", false, "Disables TNT explosions").setRequiresMcRestart(true).getBoolean();
      disableTNTMinecart = conf.get("general", "disableTNTMinecart", false, "Disables the placement of TNT minecarts").setRequiresMcRestart(true).getBoolean();
      listIsWhitelist = conf.get("block_damage", "destructionListIsWhitelist", false, "If false, the list above is a blacklist. If true, it is a whitelist").getBoolean();
      disableBlockDamage = conf.get("block_damage", "disableBlockDamage", false, "Disables all block damage from explosions").getBoolean();
      disableCreeperBlockDamage = conf.get("block_damage", "disableCreeperBlockDamage", false, "\"Environmentally Friendly Creepers\": Makes creepers not destroy blocks").getBoolean();
      spareTileEntities = conf.get("block_damage", "disableTileEntityDamage", false, "Makes explosions not destroy blocks with tile entities").getBoolean();
      disableEntityDamage = conf.get("entity_damage", "disableEntityDamage", false, "Disables explosion damage to all entities (also includes minecarts, paintings etc.)").getBoolean();
      disablePlayerDamage = conf.get("entity_damage", "disablePlayerDamage", false, "Disables explosion damage to players").getBoolean();
      disableItemDamage = conf.get("entity_damage", "disableItemDamage", false, "Disables explosion damage to items laying on the ground").getBoolean();
      disableNPCDamage = conf.get("entity_damage", "disableNPCDamage", false, "Disables explosion damage to animals and mobs").getBoolean();
   }

   public static void createBlacklistSet() {
      String[] blacklistArray = conf.get("block_damage", "destructionBlackOrWhitelist", new String[0], "A list of blocks (optionally with metadata) that will either never or only be destroyed by explosions\nWhether this list is a blacklist or whitelist gets determined by the \"destructionListIsWhitelist\" option below\nSyntax: modid:block or modid:block/meta").getStringList();
      blackWhiteList = new IdentityHashMap();

      for(String str : blacklistArray) {
         int ind = str.indexOf(47);
         String blockname;
         int metamask;
         if (ind != -1) {
            String metaStr = str.substring(ind + 1);

            int meta;
            try {
               meta = Integer.parseInt(metaStr);
            } catch (NumberFormatException var11) {
               throw new InvalidConfigValueException("destructionBlackOrWhitelist: Invalid number format: " + metaStr, var11);
            }

            if (meta < 0 || meta >= 16) {
               throw new InvalidConfigValueException("destructionBlackOrWhitelist: Metadata out of range: " + metaStr);
            }

            blockname = str.substring(0, ind);
            metamask = 1 << meta;
         } else {
            blockname = str;
            metamask = 65535;
         }

         Block block = (Block)Block.REGISTRY.getObject(new ResourceLocation(blockname));
         if (block == Blocks.AIR || block == null) {
            throw new InvalidConfigValueException("destructionBlackOrWhitelist: Invalid block name: " + blockname);
         }

         if (!blackWhiteList.containsKey(block)) {
            blackWhiteList.put(block, Integer.valueOf(metamask));
         } else {
            blackWhiteList.put(block, Integer.valueOf(metamask | ((Integer)blackWhiteList.get(block)).intValue()));
         }

         TNTUtils.logger.debug("Added block to blacklist: %s, mask 0x%X", blockname, Integer.valueOf(metamask));
      }

      blacklistActive = blackWhiteList.size() != 0;
      if (listIsWhitelist && !blacklistActive) {
         disableBlockDamage = true;
      }

   }

   public static void modifyResistances() {
      String[] entries = conf.get("block_damage", "blockResistances", new String[0], "Change the explosion resistance of individual blocks.\nSyntax: modid:block=value").setRequiresMcRestart(true).getStringList();

      for(String str : entries) {
         int ind = str.indexOf(61);
         if (ind == -1) {
            throw new InvalidConfigValueException("blockResistances: Syntax error: " + str);
         }

         String blockName = str.substring(0, ind);
         String valueStr = str.substring(ind + 1);
         Block block = (Block)Block.REGISTRY.getObject(new ResourceLocation(blockName));
         if (block == Blocks.AIR || block == null) {
            throw new InvalidConfigValueException("blockResistances: Invalid block name: " + blockName);
         }

         try {
            float resist = Float.parseFloat(valueStr);
            block.setResistance(resist);
            TNTUtils.logger.debug("Changed resistance of %s to %g", blockName, Float.valueOf(resist));
         } catch (NumberFormatException var10) {
            throw new InvalidConfigValueException("blockResistances: Invalid number format: " + valueStr, var10);
         }
      }

   }

   public static void save() {
      if (conf.hasChanged()) {
         conf.save();
      }

   }

   public static class ChangeHandler {
      public ChangeHandler() {
      }

      @SubscribeEvent
      public void onConfigChanged(OnConfigChangedEvent event) {
         if (event.getModID().equals("tnt_utilities")) {
            try {
               Config.loadValues();
               Config.createBlacklistSet();
               Config.modifyResistances();
            } finally {
               Config.save();
            }
         }

      }
   }
}
