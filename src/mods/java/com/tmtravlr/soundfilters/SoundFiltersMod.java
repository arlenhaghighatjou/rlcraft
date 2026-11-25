package com.tmtravlr.soundfilters;

import com.tmtravlr.soundfilters.filters.FilterLowPass;
import com.tmtravlr.soundfilters.filters.FilterReverb;
import java.util.Comparator;
import java.util.Random;
import java.util.TreeMap;
import net.minecraft.block.Block;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;

@Mod(
   modid = "soundfilters",
   name = "Sound Filters",
   version = "0.12.1_for_1.12",
   clientSideOnly = true,
   acceptedMinecraftVersions = "[1.12]"
)
public class SoundFiltersMod {
   public static final String MOD_ID = "soundfilters";
   public static final String MOD_NAME = "Sound Filters";
   public static final String MOD_VERSION = "0.12.1_for_1.12";
   @Instance("soundfilters")
   public static SoundFiltersMod soundFilters;
   @SidedProxy(
      clientSide = "com.tmtravlr.soundfilters.ClientProxy",
      serverSide = "com.tmtravlr.soundfilters.CommonProxy"
   )
   public static CommonProxy proxy;
   public static Logger logger;
   private static Random rand = new Random();
   public static int profileSize = 1024;
   public static boolean doSkyChecks = true;
   public static boolean doReverb = true;
   public static float reverbPercent = 1.0F;
   public static boolean doLowPass = true;
   public static float waterLowPassAmount = 0.4F;
   public static float waterVolume = 1.0F;
   public static float lavaLowPassAmount = 0.1F;
   public static float lavaVolume = 0.6F;
   public static boolean doOcclusion = true;
   public static float occlusionPercent = 1.0F;
   public static FilterLowPass lowPassFilter = new FilterLowPass();
   public static FilterReverb reverbFilter = new FilterReverb();
   public static Comparator<SoundFiltersMod.BlockMeta> BlockComparator = new Comparator<SoundFiltersMod.BlockMeta>() {
      public int compare(SoundFiltersMod.BlockMeta first, SoundFiltersMod.BlockMeta second) {
         if (Block.getIdFromBlock(second.block) - Block.getIdFromBlock(first.block) != 0) {
            return Block.getIdFromBlock(second.block) - Block.getIdFromBlock(first.block);
         } else {
            return second.meta - first.meta != 0 ? second.meta - first.meta : 0;
         }
      }
   };
   public static TreeMap<SoundFiltersMod.BlockMeta, Double> customOcclusion = new TreeMap(BlockComparator);
   public static TreeMap<SoundFiltersMod.BlockMeta, Double> customReverb = new TreeMap(BlockComparator);
   public static boolean DEBUG = false;
   public static boolean SUPER_DUPER_DEBUG = false;

   public SoundFiltersMod() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      logger = event.getModLog();
      Configuration config = new Configuration(event.getSuggestedConfigurationFile());
      config.load();
      DEBUG = config.getBoolean("Debug", "debug", false, "Set to true to write simple debug info to the console.");
      SUPER_DUPER_DEBUG = config.getBoolean("High Output Debug", "debug", false, "You probably don't want to set this to true\nunless you actually want to debug the mod.\nIt writes quite a lot in the console.");
      doLowPass = config.getBoolean("Use Low Pass?", "low_pass", true, "Set to false to disable low pass filter in water and lava.");
      waterLowPassAmount = config.getFloat("Water Low Pass Amount", "low_pass", 0.4F, 0.0F, 1.0F, "The amount of low pass that will be applied in water. Lower is stronger.");
      lavaLowPassAmount = config.getFloat("Water Low Pass Volume", "low_pass", 1.0F, 0.0F, 1.0F, "The multiplier for volume when you are in water. Lower is quieter.");
      lavaLowPassAmount = config.getFloat("Lava Low Pass Amount", "low_pass", 0.2F, 0.0F, 1.0F, "The amount of low pass that will be applied in lava. Lower is stronger.");
      lavaLowPassAmount = config.getFloat("Lava Low Pass Volume", "low_pass", 0.6F, 0.0F, 1.0F, "The multiplier for volume when you are in lava. Lower is quieter.");
      doOcclusion = config.getBoolean("Use Occluded Sounds (muting sounds behind solid walls)?", "occlusion", true, "Set to false to disable low pass filter for sounds behind solid walls.\nIf you are getting lag, disabling this might help.");
      occlusionPercent = config.getFloat("Occlusion Percent", "occlusion", 1.0F, 0.0F, Float.POSITIVE_INFINITY, "The percentage of occlusion you can get. You can lower this if you find\nthe occlusion to be too much or raise it for a more noticeable\neffect.");
      String[] occlusionBlocksList = config.getStringList("Specific block occlusion:", "occlusion", new String[]{"wool-16-2.0"}, "Add new entries (each on a new line) in the format\n<block id>-<metadata>-<occlusion double> to customize how much sound\nthey should absorb when they are between you and the sound source.\nFor the metadata, 16 means any metadata value. The amount is a\ndouble, with 0.0 absorbing no sound (like air), and 1.0 being the normal\namount, and 2.0 being twice the normal amount. By default, wool has\nentry wool-16-2.0 which is twice the normal sound absorbtion.");
      doReverb = config.getBoolean("Use Reverb?", "reverb", true, "Set to false to disable reverb.");
      reverbPercent = config.getFloat("Reverb Percent", "reverb", 1.0F, 0.0F, 2.0F, "The percentage of reverb you can get. You can lower this if you find\nthe reverb to be too much (or raise it if you really want an\necho).");
      profileSize = config.getInt("Number of blocks reverb will check through:", "reverb", 1024, 0, Integer.MAX_VALUE, "If you are getting lag, set this number lower. The higher it is,\nthe more realistic the reverb will be.");
      doSkyChecks = config.getBoolean("Do sky checks:", "reverb", true, "If this is true, when you're in an area that can see the sky, then\nthere will be less reverb. This is for aboveground areas with\nlots of stone and such like extreme hills biomes. There still might\nbe some, but less then when the sky isn't visible.");
      String[] reverbBlocksList = config.getStringList("Specific block reverb:", "reverb", new String[]{"soul_sand-16-2.0"}, "Add values to this list (each on a new line) in the format \n<block id>-<metadata>-<reverb double>, to change how the block\nwith that metadata absorbs or creates reverb. If the\nmetadata is 16, that means it will apply to any metadata value.\nBy default things like wool, snow, carpets, and plants absorb reverb\n(value 0.0), things like wood and dirt are neutral (value 1.0),\nand things like stone, metal, ice, and glass create reverb (value 2.0).\nSo if, say, you wanted to add pumpkins of any metadata to the blocks\nthat create reverb, you would put pumpkin-16-2.0 on a new line.");
      config.save();

      for(String occlusionInfo : occlusionBlocksList) {
         Block block = null;
         String blockName = "";
         int meta = -1;
         double strength = -1.0D;

         try {
            int lastDashIndex = occlusionInfo.lastIndexOf(45);
            int firstDashIndex = occlusionInfo.substring(0, lastDashIndex).lastIndexOf(45);
            blockName = occlusionInfo.substring(0, firstDashIndex);
            meta = Integer.valueOf(occlusionInfo.substring(firstDashIndex + 1, lastDashIndex)).intValue();
            strength = Double.valueOf(occlusionInfo.substring(lastDashIndex + 1)).doubleValue();
            block = Block.getBlockFromName(blockName);
         } catch (Exception var17) {
            logger.error("Error while loading in custom occlusion entry!" + (blockName == "" ? "" : " Block ID was " + blockName), var17);
         }

         if (block != null && meta >= 0 && strength >= 0.0D) {
            if (DEBUG) {
               logger.debug("Loaded custom occlusion: block " + blockName + ", with " + (meta == 16 ? "any meta" : "meta " + meta) + ", and amount " + strength);
            }

            customOcclusion.put(new SoundFiltersMod.BlockMeta(block, meta), Double.valueOf(strength));
         }
      }

      for(String reverbInfo : reverbBlocksList) {
         Block block = null;
         String blockName = "";
         int meta = -1;
         double strength = -1.0D;

         try {
            int lastDashIndex = reverbInfo.lastIndexOf(45);
            int firstDashIndex = reverbInfo.substring(0, lastDashIndex).lastIndexOf(45);
            blockName = reverbInfo.substring(0, firstDashIndex);
            meta = Integer.valueOf(reverbInfo.substring(firstDashIndex + 1, lastDashIndex)).intValue();
            strength = Double.valueOf(reverbInfo.substring(lastDashIndex + 1)).doubleValue();
            block = Block.getBlockFromName(blockName);
         } catch (Exception var16) {
            logger.error("Error while loading in custom reverb entry!" + (blockName == "" ? "" : " Block ID was " + blockName), var16);
         }

         if (block != null && meta >= 0 && strength >= 0.0D) {
            if (DEBUG) {
               logger.debug("Loaded custom reverb: block " + blockName + ", with " + (meta == 16 ? "any meta" : "meta " + meta) + ", and amount " + strength);
            }

            customReverb.put(new SoundFiltersMod.BlockMeta(block, meta), Double.valueOf(strength));
         }
      }

      proxy.registerTickHandlers();
      proxy.registerEventHandlers();
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
      reverbFilter.density = 0.0F;
      reverbFilter.diffusion = 0.6F;
      reverbFilter.gain = 0.15F;
      reverbFilter.gainHF = 0.8F;
      reverbFilter.decayTime = 0.1F;
      reverbFilter.decayHFRatio = 0.7F;
      reverbFilter.reflectionsGain = 0.6F;
      reverbFilter.reflectionsDelay = 0.0F;
      reverbFilter.lateReverbGain = 0.9F;
      reverbFilter.lateReverbDelay = 0.0F;
      reverbFilter.airAbsorptionGainHF = 0.99F;
      reverbFilter.roomRolloffFactor = 0.0F;
   }

   static {
      if (SoundSystemConfig.getLibraries() != null) {
         SoundSystemConfig.getLibraries().clear();
      }

      try {
         SoundSystemConfig.addLibrary(ModifiedLWJGLOpenALLibrary.class);
      } catch (SoundSystemException var1) {
         System.out.println("[Sound Filters] Problem while loading modified library!");
         var1.printStackTrace();
      }

      System.out.println("[Sound Filters] Loaded modified library.");
   }

   public static class BlockMeta {
      public Block block;
      public int meta;

      public BlockMeta(Block b, int m) {
         this.block = b;
         this.meta = m;
      }
   }
}
