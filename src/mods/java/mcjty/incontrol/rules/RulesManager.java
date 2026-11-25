package mcjty.incontrol.rules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import mcjty.incontrol.InControl;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class RulesManager {
   public static List<SpawnRule> rules = new ArrayList();
   public static List<SummonAidRule> summonAidRules = new ArrayList();
   public static List<PotentialSpawnRule> potentialSpawnRules = new ArrayList();
   public static List<LootRule> lootRules = new ArrayList();
   public static List<ExperienceRule> experienceRules = new ArrayList();
   private static String path;

   public RulesManager() {
   }

   public static void reloadRules() {
      rules.clear();
      summonAidRules.clear();
      potentialSpawnRules.clear();
      lootRules.clear();
      experienceRules.clear();
      readAllRules();
   }

   public static void setRulePath(File directory) {
      path = directory.getPath();
   }

   public static void readRules() {
      readAllRules();
   }

   private static boolean exists(String file) {
      File f = new File(file);
      return f.exists() && !f.isDirectory();
   }

   public static boolean readCustomSpawn(String file) {
      System.out.println("file = " + file);
      if (!exists(file)) {
         return false;
      } else {
         rules.clear();
         readRules((String)null, file, SpawnRule::parse, rules);
         return true;
      }
   }

   public static boolean readCustomSummonAid(String file) {
      if (!exists(file)) {
         return false;
      } else {
         summonAidRules.clear();
         readRules((String)null, file, SummonAidRule::parse, summonAidRules);
         return true;
      }
   }

   public static boolean readCustomPotentialSpawn(String file) {
      if (!exists(file)) {
         return false;
      } else {
         potentialSpawnRules.clear();
         readRules((String)null, file, PotentialSpawnRule::parse, potentialSpawnRules);
         return true;
      }
   }

   public static boolean readCustomLoot(String file) {
      if (!exists(file)) {
         return false;
      } else {
         lootRules.clear();
         readRules((String)null, file, LootRule::parse, lootRules);
         return true;
      }
   }

   private static void readAllRules() {
      readRules(path, "spawn.json", SpawnRule::parse, rules);
      readRules(path, "summonaid.json", SummonAidRule::parse, summonAidRules);
      readRules(path, "potentialspawn.json", PotentialSpawnRule::parse, potentialSpawnRules);
      readRules(path, "loot.json", LootRule::parse, lootRules);
      readRules(path, "experience.json", ExperienceRule::parse, experienceRules);
   }

   private static <T> void readRules(String path, String filename, Function<JsonElement, T> parser, List<T> rules) {
      JsonElement element = getRootElement(path, filename);
      if (element != null) {
         int i = 0;

         for(JsonElement entry : element.getAsJsonArray()) {
            T rule = (T)parser.apply(entry);
            if (rule != null) {
               rules.add(rule);
            } else {
               InControl.setup.getLogger().log(Level.ERROR, "Rule " + i + " in " + filename + " is invalid, skipping!");
            }

            ++i;
         }

      }
   }

   private static JsonElement getRootElement(String path, String filename) {
      File file;
      if (path == null) {
         file = new File(filename);
      } else {
         file = new File(path + File.separator + "incontrol", filename);
      }

      if (!file.exists()) {
         makeEmptyRuleFile(file);
         return null;
      } else {
         InControl.setup.getLogger().log(Level.INFO, "Reading spawn rules from " + filename);
         InputStream inputstream = null;

         try {
            var9 = new FileInputStream(file);
         } catch (FileNotFoundException var8) {
            InControl.setup.getLogger().log(Level.ERROR, "Error reading " + filename + "!");
            return null;
         }

         BufferedReader br;
         try {
            br = new BufferedReader(new InputStreamReader(var9, "UTF-8"));
         } catch (UnsupportedEncodingException var7) {
            InControl.setup.getLogger().log(Level.ERROR, "Error reading " + filename + "!");
            return null;
         }

         JsonParser parser = new JsonParser();
         JsonElement element = parser.parse(br);
         return element;
      }
   }

   private static void makeEmptyRuleFile(File file) {
      PrintWriter writer;
      try {
         writer = new PrintWriter(file);
      } catch (FileNotFoundException var4) {
         InControl.setup.getLogger().log(Level.ERROR, "Error writing " + file.getName() + "!");
         return;
      }

      JsonArray array = new JsonArray();
      Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
      writer.print(gson.toJson(array));
      writer.close();
   }
}
