package reborncore.common.powerSystem;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.commons.io.FileUtils;
import reborncore.common.RebornCoreConfig;

public class PowerSystem {
   public static File selectedFile;
   private static PowerSystem.EnergySystem selectedSystem = PowerSystem.EnergySystem.values()[0];
   private static final char[] magnitude = new char[]{'k', 'M', 'G', 'T'};

   public PowerSystem() {
   }

   public static String getLocaliszedPower(double eu) {
      return getLocaliszedPower((int)eu);
   }

   public static String getLocaliszedPowerNoSuffix(double eu) {
      return getLocaliszedPowerNoSuffix((int)eu);
   }

   public static String getLocaliszedPowerFormatted(double eu) {
      return getLocaliszedPowerFormatted((int)eu);
   }

   public static String getLocaliszedPowerFormattedNoSuffix(double eu) {
      return getLocaliszedPowerFormattedNoSuffix((int)eu);
   }

   public static String getLocaliszedPower(int eu) {
      return getDisplayPower() == PowerSystem.EnergySystem.EU ? getRoundedString(eu, PowerSystem.EnergySystem.EU.abbreviation) : getRoundedString(eu * RebornCoreConfig.euPerFU, getDisplayPower().abbreviation);
   }

   public static String getLocaliszedPowerNoSuffix(int eu) {
      return getDisplayPower() == PowerSystem.EnergySystem.EU ? getRoundedString(eu, "") : getRoundedString(eu * RebornCoreConfig.euPerFU, "");
   }

   public static String getLocaliszedPowerFormatted(int eu) {
      return getDisplayPower() == PowerSystem.EnergySystem.EU ? getRoundedString(eu, PowerSystem.EnergySystem.EU.abbreviation) : getRoundedString(eu * RebornCoreConfig.euPerFU, getDisplayPower().abbreviation, true);
   }

   public static String getLocaliszedPowerFormattedNoSuffix(int eu) {
      return getDisplayPower() == PowerSystem.EnergySystem.EU ? getRoundedString(eu, "", true) : getRoundedString(eu * RebornCoreConfig.euPerFU, "", true);
   }

   private static String getRoundedString(int value, String units) {
      return getRoundedString(value, units, false);
   }

   private static String getRoundedString(int euValue, String units, boolean doFormat) {
      String ret = "";
      float value = 0.0F;
      int i = 0;
      boolean showMagnitude = true;
      if (euValue < 0) {
         ret = "-";
         euValue = -euValue;
      }

      if (euValue < 1000) {
         doFormat = false;
         showMagnitude = false;
         value = (float)euValue;
      } else if (euValue >= 1000) {
         i = 0;

         while(true) {
            if (euValue < 10000 && euValue % 1000 >= 100) {
               value = (float)(euValue / 1000);
               value = value + (float)euValue % 1000.0F / 1000.0F;
               break;
            }

            euValue /= 1000;
            if (euValue < 1000) {
               value = (float)euValue;
               break;
            }

            ++i;
         }
      }

      if (FMLCommonHandler.instance().getEffectiveSide().isClient() && doFormat) {
         ret = ret + NumberFormat.getNumberInstance(Locale.forLanguageTag(Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode())).format((double)value);
      } else {
         ret = ret + value;
      }

      if (showMagnitude) {
         ret = ret + magnitude[i];
      }

      if (units != "") {
         ret = ret + " " + units;
      }

      return ret;
   }

   public static PowerSystem.EnergySystem getDisplayPower() {
      if (!((Boolean)selectedSystem.enabled.get()).booleanValue()) {
         bumpPowerConfig();
      }

      return selectedSystem;
   }

   public static void bumpPowerConfig() {
      int value = selectedSystem.ordinal() + 1;
      if (value == PowerSystem.EnergySystem.values().length) {
         value = 0;
      }

      selectedSystem = PowerSystem.EnergySystem.values()[value];
      writeFile();
   }

   public static void readFile() {
      if (!selectedFile.exists()) {
         writeFile();
      }

      if (selectedFile.exists()) {
         try {
            String value = FileUtils.readFileToString(selectedFile, StandardCharsets.UTF_8);
            selectedSystem = (PowerSystem.EnergySystem)Arrays.stream(PowerSystem.EnergySystem.values()).filter((energySystem) -> {
               return energySystem.abbreviation.equalsIgnoreCase(value);
            }).findFirst().orElse(PowerSystem.EnergySystem.values()[0]);
         } catch (IOException var1) {
            var1.printStackTrace();
         }
      }

   }

   public static void writeFile() {
      try {
         FileUtils.write(selectedFile, selectedSystem.abbreviation, StandardCharsets.UTF_8);
      } catch (IOException var1) {
         var1.printStackTrace();
      }

      readFile();
   }

   public static enum EnergySystem {
      FE(-4315110, "FE", 113, 151, -6943475),
      EU(-8387072, "EU", 141, 151, -10027008);

      public int colour;
      public int altColour;
      public String abbreviation;
      public int xBar;
      public int yBar;
      public Supplier<Boolean> enabled = () -> {
         return true;
      };

      private EnergySystem(int colour, String abbreviation, int xBar, int yBar, int altColour) {
         this.colour = colour;
         this.abbreviation = abbreviation;
         this.xBar = xBar;
         this.yBar = yBar;
         this.altColour = altColour;
      }
   }
}
