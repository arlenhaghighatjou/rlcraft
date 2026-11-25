package jaredbgreat.dldungeons.themes;

import jaredbgreat.dldungeons.builder.BlockFamily;
import jaredbgreat.dldungeons.builder.RegisteredBlock;
import jaredbgreat.dldungeons.nbt.NBTHelper;
import jaredbgreat.dldungeons.pieces.chests.LootCategory;
import jaredbgreat.dldungeons.pieces.chests.LootHandler;
import jaredbgreat.dldungeons.pieces.chests.LootItem;
import jaredbgreat.dldungeons.pieces.chests.LootListSet;
import jaredbgreat.dldungeons.pieces.chests.TreasureChest;
import jaredbgreat.dldungeons.setup.Externalizer;
import jaredbgreat.dldungeons.util.debug.Logging;
import jaredbgreat.dldungeons.util.parser.Tokenizer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ThemeReader {
   private static File configDir;
   private static File themesDir;
   private static File chestDir;
   private static File blocksDir;
   public static final String CHESTS_DIR = "SpecialChests";
   public static final String BLOCKS_DIR = "BlockFamilies";
   private static ArrayList<File> files;
   private static final String ESTRING = "";

   public ThemeReader() {
   }

   public static void setThemesDir(File dir) {
      Logging.logInfo("themesdir is " + dir);
      themesDir = dir;
   }

   public static void setConfigDir(File dir) {
      Logging.logInfo("themesdir is " + dir);
      configDir = dir;
   }

   public static String getThemesDir() {
      return themesDir + File.separator;
   }

   public static String getConfigDir() {
      return configDir + File.separator;
   }

   private static int findChestFiles(File dir) {
      int num = 0;
      files = new ArrayList();
      String[] fileNames = dir.list();
      if (fileNames.length < 1) {
         return 0;
      } else {
         for(String name : fileNames) {
            if (name.length() >= 5 && name.substring(name.length() - 4).equalsIgnoreCase(".cfg")) {
               files.add(new File(name));
               ++num;
            }
         }

         return num;
      }
   }

   private static int findThemeFiles(File dir) {
      int num = 0;
      files = new ArrayList();
      String[] fileNames = dir.list();
      if (fileNames.length < 1) {
         Externalizer exporter = new Externalizer(dir.toString() + File.separator);
         exporter.makeThemes();
         fileNames = dir.list();
      }

      if (fileNames.length < 1) {
         return 0;
      } else {
         for(String name : fileNames) {
            if (name.length() > 4 && !name.equals("template.cfg") && name.substring(name.length() - 4).equalsIgnoreCase(".cfg")) {
               files.add(new File(name));
               ++num;
            }
         }

         return num;
      }
   }

   private static int findBlockFiles(File dir) {
      int num = 0;
      files = new ArrayList();
      if (!dir.exists()) {
         dir.mkdirs();
      }

      String[] fileNames = dir.list();
      if (fileNames.length < 1) {
         Externalizer exporter = new Externalizer(dir.toString() + File.separator);
         exporter.makeBlocks();
         fileNames = dir.list();
      }

      if (fileNames.length < 1) {
         return 0;
      } else {
         for(String name : fileNames) {
            if (name.length() > 5 && name.substring(name.length() - 5).equalsIgnoreCase(".json")) {
               files.add(new File(name));
               ++num;
            }
         }

         if (num > 0) {
            readBlockFamilies(blocksDir);
         }

         return num;
      }
   }

   public static void readThemes() {
      TreasureChest.initSlots();
      openNBTConfig();
      openLoot("chests.cfg", true);
      chestDir = new File(configDir.toString() + File.separator + "SpecialChests");
      if (!chestDir.exists()) {
         chestDir.mkdir();
      }

      int num = findChestFiles(chestDir);
      Logging.logInfo("Found " + num + " special chest configs.");

      for(File file : files) {
         openLoot(file.toString(), false);
      }

      blocksDir = new File(configDir.toString() + File.separator + "BlockFamilies");
      num = findBlockFiles(blocksDir);
      Logging.logInfo("Found " + num + " block family configs.");
      num = findThemeFiles(themesDir);
      Logging.logInfo("Found " + num + " themes.");

      for(File file : files) {
         readTheme(file);
      }

   }

   private static void readBlockFamilies(File dir) {
      if (dir.exists() && dir.isDirectory() && dir.canRead()) {
         BufferedReader instream = null;
         String[] fileNames = dir.list();

         for(String fn : fileNames) {
            if (fn.substring(fn.length() - 5).equalsIgnoreCase(".json")) {
               StringBuilder json = new StringBuilder();
               fn = dir.toString() + File.separator + fn;

               try {
                  instream = new BufferedReader(new FileReader(fn));

                  while(instream.ready()) {
                     json.append(instream.readLine());
                  }

                  instream.close();
               } catch (FileNotFoundException var9) {
                  var9.printStackTrace();
               } catch (IOException var10) {
                  var10.printStackTrace();
               }

               RegisteredBlock.add(BlockFamily.makeBlockFamily(json.toString()));
            }
         }

      } else {
         Logging.logInfo("WARNING: " + dir + " did not exist or was not a valid directory!");
      }
   }

   public static void openNBTConfig() {
      BufferedReader instream = null;
      File nbtconfig = new File(configDir.toString() + File.separator + "nbt.cfg");
      if (nbtconfig.exists()) {
         try {
            instream = new BufferedReader(new FileReader(nbtconfig.toString()));
            readNBT(instream);
            if (instream != null) {
               instream.close();
            }
         } catch (IOException var3) {
            var3.printStackTrace();
         }
      } else {
         Logging.logInfo("File nbt.cfg is missing; will fallabck on default loot");
      }

   }

   public static void readNBT(BufferedReader instream) throws IOException {
      Logging.logInfo("Loading custom NBT tags (nbt.cfg)");
      Tokenizer tokens = null;
      String line = null;

      while((line = instream.readLine()) != null) {
         if (line.length() >= 2 && line.charAt(0) != '#') {
            NBTHelper.parseNBTLine(line);
         }
      }

   }

   public static void openLoot(String name, boolean isMain) {
      LootCategory cat = LootHandler.getLootHandler().createCategory(name);
      BufferedReader instream = null;
      File chests;
      if (isMain) {
         chests = new File(configDir.toString() + File.separator + name);
      } else {
         chests = new File(configDir.toString() + File.separator + "SpecialChests" + File.separator + name);
      }

      if (chests.exists()) {
         try {
            instream = new BufferedReader(new FileReader(chests.toString()));
            readLoot(instream, name, cat.getLists());
            if (instream != null) {
               instream.close();
            }
         } catch (IOException var6) {
            var6.printStackTrace();
         }
      } else {
         Logging.logInfo("File " + name + " is missing; will fallabck on default loot");
         cat.getLists().addDefaultLoot();
      }

   }

   public static void readLoot(BufferedReader instream, String filename, LootListSet loots) throws IOException {
      Logging.logInfo("Loading chest loot file (" + filename + ")");
      Tokenizer tokens = null;
      String line = null;

      while((line = instream.readLine()) != null) {
         if (line.length() >= 2 && line.charAt(0) != '#') {
            tokens = new Tokenizer(line, " ,;:\t\n\r\f=");
            if (tokens.hasMoreTokens()) {
               String type = tokens.nextToken().toLowerCase();
               if (tokens.hasMoreTokens()) {
                  int level = intParser(tokens);
                  if (tokens.hasMoreTokens() && level != 0) {
                     String modid = tokens.nextToken();
                     if (modid.toLowerCase().equalsIgnoreCase("item") || modid.toLowerCase().equalsIgnoreCase("block")) {
                        modid = "minecraft";
                     }

                     if (tokens.hasMoreTokens()) {
                        String name = tokens.nextToken();
                        if (tokens.hasMoreTokens()) {
                           int min = intParser(tokens);
                           if (tokens.hasMoreTokens() && min >= 1) {
                              int max = intParser(tokens);
                              String item = modid + ":" + name;
                              LootItem loot = new LootItem(item, min, max, level);
                              if (item != null && loot != null) {
                                 while(tokens.hasMoreTokens()) {
                                    loot.addNbt(tokens.nextToken());
                                 }

                                 loot.trimNbt();
                                 loots.addItem(loot, type, level);
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      loots.addDiscs();
   }

   private static void readTheme(File file) {
      BufferedReader instream = null;

      try {
         instream = new BufferedReader(new FileReader(themesDir.toString() + File.separator + file.toString()));
         parseTheme(instream, file.toString());
         if (instream != null) {
            instream.close();
         }
      } catch (IOException var10) {
         var10.printStackTrace();
      } catch (NoSuchElementException var11) {
         if (instream != null) {
            try {
               instream.close();
            } catch (IOException var9) {
               var9.printStackTrace();
            }
         }

         Logging.logError("Theme " + file.toString() + " contained a fatal error!");
         var11.printStackTrace();
         File broken = new File(themesDir.toString() + File.separator + file.toString());
         File errorDir = new File(themesDir.toString() + File.separator + "errors");
         if (!errorDir.exists()) {
            errorDir.mkdir();
         }

         int i = 0;

         File errorFile;
         while(true) {
            errorFile = new File(errorDir.toString() + File.separator + file.toString() + ".err" + i++);
            if (!errorFile.exists()) {
               break;
            }
         }

         if (!broken.renameTo(errorFile)) {
            try {
               Files.move(broken.toPath(), errorFile.toPath());
            } catch (IOException var8) {
               Logger.getLogger(ThemeReader.class.getName()).log(Level.SEVERE, (String)null, var8);
            }
         }

         if (errorFile.exists()) {
            Logging.logError("Theme " + errorFile + " was renamed to prevent  further reading attempts; please fix it.");
         } else {
            Logging.logError("Theme " + errorFile + " was NOT renamed to prevent  further reading attempts (please fix it); something went wrong!");
         }
      }

   }

   public static void parseTheme(BufferedReader instream, String name) throws IOException, NoSuchElementException {
      Theme theme = new Theme();
      theme.name = name;
      theme.version = 2;
      Tokenizer tokens = null;
      String line = null;
      String delimeters = " ,;\t\n\r\f=";

      while((line = instream.readLine()) != null) {
         if (line.length() >= 2 && line.charAt(0) != '#') {
            tokens = new Tokenizer(line, delimeters);
            if (tokens.hasMoreTokens()) {
               String token = tokens.nextToken().toLowerCase();
               if (token.equalsIgnoreCase("miny")) {
                  theme.minY = intParser(theme.minY, tokens);
               } else if (token.equalsIgnoreCase("maxy")) {
                  theme.maxY = intParser(theme.maxY, tokens);
               } else if (token.equalsIgnoreCase("buildfoundation")) {
                  theme.buildFoundation = booleanParser(theme.buildFoundation, tokens);
               } else if (token.equalsIgnoreCase("sizes")) {
                  theme.sizes = sizeParser(theme.sizes, tokens);
               } else if (token.equalsIgnoreCase("dimensionwhitelist")) {
                  theme.dimensionWhitelist = dimensionParser(tokens);
               } else if (token.equalsIgnoreCase("outside")) {
                  theme.outside = elementParser(theme.outside, tokens);
               } else if (token.equalsIgnoreCase("liquids")) {
                  theme.liquids = elementParser(theme.liquids, tokens);
               } else if (token.equalsIgnoreCase("subrooms")) {
                  theme.subrooms = elementParser(theme.subrooms, tokens);
               } else if (token.equalsIgnoreCase("islands")) {
                  theme.islands = elementParser(theme.islands, tokens);
               } else if (token.equalsIgnoreCase("pillars")) {
                  theme.pillars = elementParser(theme.pillars, tokens);
               } else if (token.equalsIgnoreCase("symmetry")) {
                  theme.symmetry = elementParser(theme.symmetry, tokens);
               } else if (token.equalsIgnoreCase("variability")) {
                  theme.variability = elementParser(theme.variability, tokens);
               } else if (token.equalsIgnoreCase("degeneracy")) {
                  theme.degeneracy = elementParser(theme.degeneracy, tokens);
               } else if (token.equalsIgnoreCase("complexity")) {
                  theme.complexity = elementParser(theme.complexity, tokens);
               } else if (token.equalsIgnoreCase("verticle")) {
                  theme.verticle = elementParser(theme.verticle, tokens);
               } else if (token.equalsIgnoreCase("naturals")) {
                  theme.naturals = elementParser(theme.naturals, tokens);
               } else if (token.equalsIgnoreCase("entrances")) {
                  theme.entrances = elementParser(theme.entrances, tokens);
               } else if (token.equalsIgnoreCase("air")) {
                  theme.air = blockParser(theme.air, tokens, (float)theme.version);
               } else if (token.equalsIgnoreCase("walls")) {
                  theme.walls = blockParser(theme.walls, tokens, (float)theme.version);
               } else if (token.equalsIgnoreCase("caveblock")) {
                  theme.caveWalls = blockParser(theme.caveWalls, tokens, (float)theme.version);
               } else if (token.equalsIgnoreCase("floors")) {
                  theme.floors = blockParser(theme.floors, tokens, (float)theme.version);
               } else if (token.equalsIgnoreCase("ceilings")) {
                  theme.ceilings = blockParser(theme.ceilings, tokens, (float)theme.version);
               } else if (token.equalsIgnoreCase("fencing")) {
                  theme.fencing = blockParser(theme.fencing, tokens, (float)theme.version);
               } else if (token.equalsIgnoreCase("liquid")) {
                  theme.liquid = blockParser(theme.liquid, tokens, (float)theme.version);
               } else if (token.equalsIgnoreCase("pillarblock")) {
                  theme.pillarBlock = blockParser(theme.pillarBlock, tokens, (float)theme.version);
               } else if (token.equalsIgnoreCase("commonmobs")) {
                  theme.commonMobs = parseMobs(theme.commonMobs, tokens);
               } else if (token.equalsIgnoreCase("hardmobs")) {
                  theme.hardMobs = parseMobs(theme.hardMobs, tokens);
               } else if (token.equalsIgnoreCase("brutemobs")) {
                  theme.bruteMobs = parseMobs(theme.bruteMobs, tokens);
               } else if (token.equalsIgnoreCase("elitemobs")) {
                  theme.eliteMobs = parseMobs(theme.eliteMobs, tokens);
               } else if (token.equalsIgnoreCase("bossmobs")) {
                  theme.bossMobs = parseMobs(theme.bossMobs, tokens);
               } else if (token.equalsIgnoreCase("biomes")) {
                  theme.biomes = biomeParser(tokens);
               } else if (token.equalsIgnoreCase("notinbiomes")) {
                  theme.notIn = biomeParser(tokens);
               } else if (token.equalsIgnoreCase("biomewhitelist")) {
                  theme.biomewl.addAll(specificBiomeParser(tokens));
               } else if (token.equalsIgnoreCase("biomeblacklist")) {
                  theme.biomebl.addAll(specificBiomeParser(tokens));
               } else if (token.equalsIgnoreCase("chestsfile")) {
                  theme.lootCat = tokens.nextToken();
               } else if (!token.equalsIgnoreCase("type")) {
                  if (token.equalsIgnoreCase("flags")) {
                     theme.flags = flagParser(tokens);
                  } else if (token.equalsIgnoreCase("version")) {
                     theme.version = (int)floatParser((float)theme.version, tokens);
                  }
               } else {
                  theme.type = typeParser(tokens);

                  for(ThemeType type : theme.type) {
                     ThemeType.addThemeToType(theme, type);
                  }

                  if (theme.type.contains(ThemeType.WATER)) {
                     theme.air = new int[]{RegisteredBlock.add("minecraft:water")};
                  }

                  if (theme.type.contains(ThemeType.SWAMP)) {
                     theme.flags.add(ThemeFlags.SWAMPY);
                  }
               }
            }
         }
      }

      if (theme.air.length < 1) {
         theme.air = new int[]{RegisteredBlock.add("minecraft:air")};
      }

      theme.fixMobs();
      theme.biomeRegister();
      if (theme.caveWalls.length < 1) {
         theme.caveWalls = theme.walls;
      }

   }

   private static Element elementParser(Element el, Tokenizer tokens) {
      boolean valid = false;
      int[] values = new int[]{0, 0, 0, 0, 0, 0};

      for(int i = 0; i < values.length && tokens.hasMoreTokens(); ++i) {
         String num = tokens.nextToken();
         values[i] = Integer.parseInt(num);
         if (values[i] < 0) {
            values[i] = 0;
         }

         if (values[i] > 0) {
            valid = true;
         }
      }

      return valid ? new Element(values[0], values[1], values[2], values[3], values[4], values[5]) : el;
   }

   private static SizeElement sizeParser(SizeElement el, Tokenizer tokens) {
      boolean valid = false;
      int[] values = new int[]{0, 0, 0, 0, 0};

      for(int i = 0; i < values.length && tokens.hasMoreTokens(); ++i) {
         String num = tokens.nextToken();
         values[i] = Integer.parseInt(num);
         if (values[i] < 0) {
            values[i] = 0;
         }

         if (values[i] > 0) {
            valid = true;
         }
      }

      return valid ? new SizeElement(values[0], values[1], values[2], values[3], values[4]) : el;
   }

   private static int[] dimensionParser(Tokenizer tokens) {
      if (tokens.getToken(1) != null && !tokens.getToken(1).equalsIgnoreCase("all")) {
         int[] rtn = new int[tokens.countTokens() - 1];

         for(int i = 0; tokens.hasMoreTokens(); rtn[i++] = Integer.parseInt(tokens.nextToken())) {
            ;
         }

         return rtn;
      } else {
         return new int[0];
      }
   }

   private static int intParser(int el, Tokenizer tokens) {
      boolean valid = false;
      int value = 0;
      String num = "";

      try {
         if (tokens.hasMoreTokens()) {
            num = tokens.nextToken();
            value = Integer.parseInt(num);
            if (value > 5 && value < 224) {
               valid = true;
            }
         }
      } catch (Exception var6) {
         Logging.logError("ThemeReader.intParser(int el, Tokenizer tokens) tried to read non-number as integer");
         Logging.logError("Value passed as and integer was: " + num);
         var6.printStackTrace();
         return el;
      }

      return valid ? value : el;
   }

   private static float floatParser(float el, Tokenizer tokens) {
      float value = 0.0F;
      String num = "";

      try {
         if (tokens.hasMoreTokens()) {
            num = tokens.nextToken();
            value = Float.parseFloat(num);
         }

         return value;
      } catch (Exception var5) {
         Logging.logError("ThemeReader.floatParser(float el, Tokenizer tokens) tried to read non-number as float");
         Logging.logError("Value passed as and foat was: " + num);
         return el;
      }
   }

   private static int intParser(Tokenizer tokens) {
      int value = 0;
      String num = "";

      try {
         if (tokens.hasMoreTokens()) {
            num = tokens.nextToken().trim();
            value = Integer.parseInt(num);
         }

         return value;
      } catch (Exception var4) {
         Logging.logError("ThemeReader.intParser(Tokenizer tokens) tried to read non-number as integer");
         Logging.logError("Value passed as and integer was: " + num);
         return -1;
      }
   }

   private static boolean booleanParser(boolean el, Tokenizer tokens) {
      boolean valid = false;
      if (tokens.hasMoreTokens()) {
         boolean bool = Boolean.parseBoolean(tokens.nextToken());
         return bool;
      } else {
         return el;
      }
   }

   private static int[] blockParser(int[] el, Tokenizer tokens, float version) throws NoSuchElementException {
      ArrayList<String> values = new ArrayList();

      while(tokens.hasMoreTokens()) {
         String nums = tokens.nextToken();
         values.add(String.valueOf(RegisteredBlock.add(nums)));
      }

      int[] out = new int[values.size() + el.length];

      for(int i = 0; i < el.length; ++i) {
         out[i] = el[i];
      }

      for(int i = 0; i < values.size(); ++i) {
         out[i + el.length] = Integer.parseInt((String)values.get(i));
      }

      return out;
   }

   private static ArrayList<String> parseMobs(ArrayList<String> el, Tokenizer tokens) {
      ArrayList<String> mobs;
      if (el != null) {
         mobs = el;
      } else {
         mobs = new ArrayList();
      }

      while(tokens.hasMoreTokens()) {
         String nextMob = tokens.nextToken();
         mobs.add(nextMob);
      }

      return mobs;
   }

   private static HashSet<Type> biomeParser(Tokenizer tokens) {
      HashSet<Type> biomes = new HashSet();

      while(tokens.hasMoreTokens()) {
         String name = tokens.nextToken().toUpperCase();
         if (name.equalsIgnoreCase("ALL")) {
            biomes.add(Type.FOREST);
            biomes.add(Type.PLAINS);
            biomes.add(Type.MOUNTAIN);
            biomes.add(Type.HILLS);
            biomes.add(Type.SWAMP);
            biomes.add(Type.WATER);
            biomes.add(Type.JUNGLE);
            biomes.add(Type.WASTELAND);
            biomes.add(Type.BEACH);
            biomes.add(Type.NETHER);
            biomes.add(Type.END);
            biomes.add(Type.MUSHROOM);
            biomes.add(Type.MAGICAL);
            biomes.add(Type.HOT);
            biomes.add(Type.COLD);
            biomes.add(Type.DENSE);
            biomes.add(Type.SPARSE);
            biomes.add(Type.WET);
            biomes.add(Type.DRY);
            biomes.add(Type.SAVANNA);
            biomes.add(Type.CONIFEROUS);
            biomes.add(Type.SPOOKY);
            biomes.add(Type.DEAD);
            biomes.add(Type.LUSH);
            biomes.add(Type.MESA);
            biomes.add(Type.SANDY);
            biomes.add(Type.SNOWY);
         } else if (name.equalsIgnoreCase("FOREST")) {
            biomes.add(Type.FOREST);
         } else if (name.equalsIgnoreCase("PLAINS")) {
            biomes.add(Type.PLAINS);
         } else if (name.equalsIgnoreCase("MOUNTAINS")) {
            biomes.add(Type.MOUNTAIN);
         } else if (name.equalsIgnoreCase("HILLS")) {
            biomes.add(Type.HILLS);
         } else if (name.equalsIgnoreCase("SWAMP")) {
            biomes.add(Type.SWAMP);
         } else if (name.equalsIgnoreCase("WATER")) {
            biomes.add(Type.WATER);
         } else if (name.equalsIgnoreCase("JUNGLE")) {
            biomes.add(Type.JUNGLE);
         } else if (name.equalsIgnoreCase("WASTELAND")) {
            biomes.add(Type.WASTELAND);
         } else if (name.equalsIgnoreCase("BEACH")) {
            biomes.add(Type.BEACH);
         } else if (name.equalsIgnoreCase("NETHER")) {
            biomes.add(Type.NETHER);
         } else if (name.equalsIgnoreCase("END")) {
            biomes.add(Type.END);
         } else if (name.equalsIgnoreCase("MUSHROOM")) {
            biomes.add(Type.MUSHROOM);
         } else if (name.equalsIgnoreCase("MAGICAL")) {
            biomes.add(Type.MAGICAL);
         } else if (name.equalsIgnoreCase("HOT")) {
            biomes.add(Type.HOT);
         } else if (name.equalsIgnoreCase("COLD")) {
            biomes.add(Type.COLD);
         } else if (name.equalsIgnoreCase("DENSE")) {
            biomes.add(Type.DENSE);
         } else if (name.equalsIgnoreCase("SPARSE")) {
            biomes.add(Type.SPARSE);
         } else if (name.equalsIgnoreCase("WET")) {
            biomes.add(Type.WET);
         } else if (name.equalsIgnoreCase("DRY")) {
            biomes.add(Type.DRY);
         } else if (name.equalsIgnoreCase("SAVANNA")) {
            biomes.add(Type.SAVANNA);
         } else if (name.equalsIgnoreCase("CONIFEROUS")) {
            biomes.add(Type.CONIFEROUS);
         } else if (name.equalsIgnoreCase("SPOOKY")) {
            biomes.add(Type.SPOOKY);
         } else if (name.equalsIgnoreCase("DEAD")) {
            biomes.add(Type.DEAD);
         } else if (name.equalsIgnoreCase("LUSH")) {
            biomes.add(Type.LUSH);
         } else if (name.equalsIgnoreCase("MESA")) {
            biomes.add(Type.MESA);
         } else if (name.equalsIgnoreCase("SANDY")) {
            biomes.add(Type.SANDY);
         } else if (name.equalsIgnoreCase("SNOWY")) {
            biomes.add(Type.SNOWY);
         }
      }

      return biomes;
   }

   private static EnumSet<ThemeType> typeParser(Tokenizer tokens) {
      EnumSet<ThemeType> types = EnumSet.noneOf(ThemeType.class);

      while(tokens.hasMoreTokens()) {
         String name = tokens.nextToken().toUpperCase();
         if (name.equalsIgnoreCase("FOREST")) {
            types.add(ThemeType.FOREST);
         } else if (name.equalsIgnoreCase("PLAINS")) {
            types.add(ThemeType.PLAINS);
         } else if (name.equalsIgnoreCase("MOUNTAINS")) {
            types.add(ThemeType.MOUNTAIN);
         } else if (name.equalsIgnoreCase("SWAMP")) {
            types.add(ThemeType.SWAMP);
         } else if (name.equalsIgnoreCase("WATER")) {
            types.add(ThemeType.WATER);
         } else if (name.equalsIgnoreCase("DESERT")) {
            types.add(ThemeType.DESERT);
         } else if (name.equalsIgnoreCase("FROZEN")) {
            types.add(ThemeType.FROZEN);
         } else if (name.equalsIgnoreCase("JUNGLE")) {
            types.add(ThemeType.JUNGLE);
         } else if (name.equalsIgnoreCase("WASTELAND")) {
            types.add(ThemeType.WASTELAND);
         } else if (name.equalsIgnoreCase("NETHER")) {
            types.add(ThemeType.NETHER);
         } else if (name.equalsIgnoreCase("END")) {
            types.add(ThemeType.END);
         } else if (name.equalsIgnoreCase("MUSHROOM")) {
            types.add(ThemeType.MUSHROOM);
         } else if (name.equalsIgnoreCase("MAGICAL")) {
            types.add(ThemeType.MAGICAL);
         } else if (name.equalsIgnoreCase("DUNGEON")) {
            types.add(ThemeType.DUNGEON);
         } else if (name.equalsIgnoreCase("URBAN")) {
            types.add(ThemeType.URBAN);
         } else if (name.equalsIgnoreCase("NECRO")) {
            types.add(ThemeType.NECRO);
         } else if (name.equalsIgnoreCase("FIERY")) {
            types.add(ThemeType.FIERY);
         } else if (name.equalsIgnoreCase("SHADOW")) {
            types.add(ThemeType.SHADOW);
         } else if (name.equalsIgnoreCase("TECH")) {
            types.add(ThemeType.TECH);
         } else if (name.equalsIgnoreCase("PARADISE")) {
            types.add(ThemeType.PARADISE);
         }
      }

      return types;
   }

   private static Set<Biome> specificBiomeParser(Tokenizer tokens) {
      Set<Biome> biomes = new HashSet();

      while(tokens.hasMoreTokens()) {
         biomes.add(ForgeRegistries.BIOMES.getValue(new ResourceLocation(tokens.nextToken())));
      }

      return biomes;
   }

   private static EnumSet<ThemeFlags> flagParser(Tokenizer tokens) {
      EnumSet<ThemeFlags> flags = EnumSet.noneOf(ThemeFlags.class);

      while(tokens.hasMoreTokens()) {
         String name = tokens.nextToken().toUpperCase();
         if (name.equalsIgnoreCase("SWAMPY")) {
            flags.add(ThemeFlags.SWAMPY);
         } else if (name.equalsIgnoreCase("WATER")) {
            flags.add(ThemeFlags.WATER);
         } else if (name.equalsIgnoreCase("SURFACE")) {
            flags.add(ThemeFlags.SURFACE);
         }
      }

      return flags;
   }
}
