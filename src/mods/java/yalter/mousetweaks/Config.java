package yalter.mousetweaks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

public class Config {
   protected static final Properties defaultValues = new Properties();
   protected String fileName;
   public boolean rmbTweak = true;
   public boolean lmbTweakWithItem = true;
   public boolean lmbTweakWithoutItem = true;
   public boolean wheelTweak = true;
   public WheelSearchOrder wheelSearchOrder = WheelSearchOrder.LAST_TO_FIRST;
   public WheelScrollDirection wheelScrollDirection = WheelScrollDirection.NORMAL;
   public Set<OnTickMethod> onTickMethodOrder = new LinkedHashSet();
   public ScrollHandling scrollHandling = ScrollHandling.SIMPLE;
   public ScrollItemScaling scrollItemScaling = ScrollItemScaling.PROPORTIONAL;
   public static boolean debug = false;

   public Config(String fileName) {
      this.fileName = fileName;
   }

   public void read() {
      Properties properties = new Properties(defaultValues);

      try {
         FileReader configReader = new FileReader(this.fileName);
         properties.load(configReader);
         configReader.close();
      } catch (FileNotFoundException var3) {
         ;
      } catch (IOException var4) {
         Logger.Log("Failed to read the config file: " + this.fileName);
         var4.printStackTrace();
      }

      this.rmbTweak = parseIntOrDefault(properties.getProperty("RMBTweak"), 1) != 0;
      this.lmbTweakWithItem = parseIntOrDefault(properties.getProperty("LMBTweakWithItem"), 1) != 0;
      this.lmbTweakWithoutItem = parseIntOrDefault(properties.getProperty("LMBTweakWithoutItem"), 1) != 0;
      this.wheelTweak = parseIntOrDefault(properties.getProperty("WheelTweak"), 1) != 0;
      this.wheelSearchOrder = WheelSearchOrder.fromId(parseIntOrDefault(properties.getProperty("WheelSearchOrder"), 1));
      this.wheelScrollDirection = WheelScrollDirection.fromId(parseIntOrDefault(properties.getProperty("WheelScrollDirection"), 0));
      this.onTickMethodOrderFromString(properties.getProperty("OnTickMethodOrder"));
      this.scrollHandling = ScrollHandling.fromId(parseIntOrDefault(properties.getProperty("ScrollHandling"), 0));
      this.scrollItemScaling = ScrollItemScaling.fromId(parseIntOrDefault(properties.getProperty("ScrollItemScaling"), 0));
      debug = parseIntOrDefault(properties.getProperty("Debug"), 0) != 0;
   }

   private static int parseIntOrDefault(String s, int defaultValue) {
      try {
         return Integer.parseInt(s);
      } catch (NumberFormatException var3) {
         return defaultValue;
      }
   }

   public void save() {
      try {
         File config = new File(this.fileName);
         boolean existed = config.exists();
         File parentDir = config.getParentFile();
         if (!parentDir.exists()) {
            parentDir.mkdirs();
         }

         FileWriter configWriter = new FileWriter(config);
         writeBoolean(configWriter, "RMBTweak", this.rmbTweak);
         writeBoolean(configWriter, "LMBTweakWithItem", this.lmbTweakWithItem);
         writeBoolean(configWriter, "LMBTweakWithoutItem", this.lmbTweakWithoutItem);
         writeBoolean(configWriter, "WheelTweak", this.wheelTweak);
         writeString(configWriter, "WheelSearchOrder", String.valueOf(this.wheelSearchOrder.ordinal()));
         writeString(configWriter, "WheelScrollDirection", String.valueOf(this.wheelScrollDirection.ordinal()));
         writeString(configWriter, "OnTickMethodOrder", this.onTickMethodOrderString());
         writeString(configWriter, "ScrollHandling", String.valueOf(this.scrollHandling.ordinal()));
         writeString(configWriter, "ScrollItemScaling", String.valueOf(this.scrollItemScaling.ordinal()));
         writeBoolean(configWriter, "Debug", debug);
         configWriter.close();
         if (!existed) {
            Logger.Log("Created the config file.");
         }
      } catch (IOException var5) {
         Logger.Log("Failed to write the config file: " + this.fileName);
         var5.printStackTrace();
      }

   }

   private static void writeString(FileWriter configWriter, String name, String value) throws IOException {
      configWriter.write(name + '=' + value + '\n');
   }

   private static void writeBoolean(FileWriter configWriter, String name, boolean value) throws IOException {
      writeString(configWriter, name, value ? "1" : "0");
   }

   public String onTickMethodOrderString() {
      StringBuilder result = new StringBuilder();

      for(OnTickMethod method : this.onTickMethodOrder) {
         if (result.length() > 0) {
            result.append(", ");
         }

         switch(method) {
         case FORGE:
            result.append("Forge");
            break;
         case LITELOADER:
            result.append("LiteLoader");
         }
      }

      return result.toString();
   }

   public void onTickMethodOrderFromString(String string) {
      this.onTickMethodOrder.clear();
      String[] onTickMethods = string.trim().split("[\\s]*,[\\s]*");

      for(String method : onTickMethods) {
         if ("Forge".equalsIgnoreCase(method)) {
            this.onTickMethodOrder.add(OnTickMethod.FORGE);
         } else if ("LiteLoader".equalsIgnoreCase(method)) {
            this.onTickMethodOrder.add(OnTickMethod.LITELOADER);
         }
      }

      this.onTickMethodOrder.add(OnTickMethod.FORGE);
      this.onTickMethodOrder.add(OnTickMethod.LITELOADER);
   }

   static {
      defaultValues.setProperty("RMBTweak", "1");
      defaultValues.setProperty("LMBTweakWithItem", "1");
      defaultValues.setProperty("LMBTweakWithoutItem", "1");
      defaultValues.setProperty("WheelTweak", "1");
      defaultValues.setProperty("WheelSearchOrder", "1");
      defaultValues.setProperty("WheelScrollDirection", "0");
      defaultValues.setProperty("OnTickMethodOrder", "Forge, LiteLoader");
      defaultValues.setProperty("ScrollHandling", "0");
      defaultValues.setProperty("ScrollItemScaling", "0");
      defaultValues.setProperty("Debug", "0");
   }
}
