package mcjty.fxcontrol;

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
import mcjty.fxcontrol.rules.EffectRule;
import mcjty.fxcontrol.rules.HarvestRule;
import mcjty.fxcontrol.rules.LeftClickRule;
import mcjty.fxcontrol.rules.PlaceRule;
import mcjty.fxcontrol.rules.RightClickRule;
import org.apache.logging.log4j.Level;

public class RulesManager {
   private static String path;
   public static List<EffectRule> effectRules = new ArrayList();
   public static List<HarvestRule> harvestRules = new ArrayList();
   public static List<PlaceRule> placeRules = new ArrayList();
   public static List<RightClickRule> rightclickRules = new ArrayList();
   public static List<LeftClickRule> leftclickRules = new ArrayList();

   public RulesManager() {
   }

   public static void reloadRules() {
      effectRules.clear();
      harvestRules.clear();
      placeRules.clear();
      rightclickRules.clear();
      leftclickRules.clear();
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

   private static void readAllRules() {
      readRules(path, "effects.json", EffectRule::parse, effectRules);
      readRules(path, "breakevents.json", HarvestRule::parse, harvestRules);
      readRules(path, "placeevents.json", PlaceRule::parse, placeRules);
      readRules(path, "rightclicks.json", RightClickRule::parse, rightclickRules);
      readRules(path, "leftclicks.json", LeftClickRule::parse, leftclickRules);
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
               FxControl.setup.getLogger().log(Level.ERROR, "Rule " + i + " in " + filename + " is invalid, skipping!");
            }

            ++i;
         }

         FxControl.setup.getLogger().log(Level.INFO, "Loaded " + i + " rules!");
      }
   }

   private static JsonElement getRootElement(String path, String filename) {
      File file;
      if (path == null) {
         file = new File(filename);
      } else {
         file = new File(path + File.separator + "fxcontrol", filename);
      }

      if (!file.exists()) {
         makeEmptyRuleFile(file);
         return null;
      } else {
         FxControl.setup.getLogger().log(Level.INFO, "Reading rules from " + filename);
         InputStream inputstream = null;

         try {
            var9 = new FileInputStream(file);
         } catch (FileNotFoundException var8) {
            FxControl.setup.getLogger().log(Level.ERROR, "Error reading " + filename + "!");
            return null;
         }

         BufferedReader br;
         try {
            br = new BufferedReader(new InputStreamReader(var9, "UTF-8"));
         } catch (UnsupportedEncodingException var7) {
            FxControl.setup.getLogger().log(Level.ERROR, "Error reading " + filename + "!");
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
         FxControl.setup.getLogger().log(Level.ERROR, "Error writing " + file.getName() + "!");
         return;
      }

      JsonArray array = new JsonArray();
      Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
      writer.print(gson.toJson(array));
      writer.close();
   }
}
