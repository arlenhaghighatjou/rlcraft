package com.endertech.minecraft.forge.versions;

import com.endertech.common.CommonCollect;
import com.endertech.common.CommonMath;
import com.endertech.common.CommonString;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.SystemUtils;

abstract class UserAgent implements CommonCollect.IWeighted {
   protected static final String NONE = "";
   protected static final String KHTML_LIKE_GECKO = "KHTML, like Gecko";
   static final CommonCollect.WeightedList<UserAgent> COMMON_AGENTS = CommonCollect.WeightedList.<UserAgent>withElements(new UserAgent("5.0", (String)CommonCollect.getRandomElementFrom(Lists.newArrayList(new String[]{"103.0.5060.134", "103.0.5060.114", "103.0.5060.66", "103.0.5060.53", "103.0.5060.33", "102.0.5005.167", "102.0.5005.149", "102.0.5005.63", "102.0.5005.62", "102.0.5005.61", "101.0.4951.67", "101.0.4951.64", "101.0.4951.54", "101.0.4951.34", "100.0.4896.127", "100.0.4896.60", "99.0.4844.84", "99.0.4844.82", "99.0.4844.74", "99.0.4844.51", "99.0.4844.27", "98.0.4758.102", "98.0.4758.82", "98.0.4758.81", "98.0.4758.80", "97.0.4692.99", "97.0.4692.71", "96.0.4664.110", "96.0.4664.93", "96.0.4664.55", "95.0.4638.69", "95.0.4638.54", "94.0.4606.81", "94.0.4606.71", "94.0.4606.61", "94.0.4606.54", "93.0.4577.95", "93.0.4577.82", "93.0.4577.63", "92.0.4515.162", "92.0.4515.159", "92.0.4515.131", "92.0.4515.107", "91.0.4472.164", "91.0.4472.124", "91.0.4472.114", "91.0.4472.106", "91.0.4472.101", "91.0.4472.77", "90.0.4430.212", "90.0.4430.93", "90.0.4430.85", "90.0.4430.72", "89.0.4389.128", "89.0.4389.114", "89.0.4389.90", "89.0.4389.82", "88.0.4324.190", "88.0.4324.150", "88.0.4324.146", "88.0.4324.104", "87.0.4280.141", "87.0.4280.88", "87.0.4280.66", "86.0.4240.198", "86.0.4240.111", "86.0.4240.75", "85.0.4183.121", "85.0.4183.102", "85.0.4183.83", "84.0.4147.135", "84.0.4147.105", "83.0.4103.116", "83.0.4103.97", "81.0.4044.138", "81.0.4044.129", "80.0.3987.163", "80.0.3987.149", "80.0.3987.132", "79.0.3945.130", "79.0.3945.117", "79.0.3945.88", "78.0.3904.108", "76.0.3809.100", "75.0.3770.100", "74.0.3729.169", "74.0.3729.157", "72.0.3626.121", "70.0.3538.102", "70.0.3538.77", "69.0.3497.100", "68.0.3440.106", "67.0.3396.99", "65.0.3325.181", "64.0.3282.186", "63.0.3239.84", "60.0.3112.113", "60.0.3112.90"})), "537.36") {
      protected String systemDetails() {
         return this.isOsLinux() ? this.systemLinux("") : this.systemWindowsNT(true);
      }

      protected String platform() {
         return this.platformAppleWebKit();
      }

      protected String platformDetails() {
         return "KHTML, like Gecko";
      }

      protected String extensions() {
         return this.joinMain(new String[]{this.extensionChrome(this.agentVersion), this.extensionSafari(this.platformVersion)});
      }

      public int getWeight() {
         return 2800000;
      }
   }, new UserAgent("5.0", CommonMath.Random.between(50, 105) + ".0", "20100101") {
      protected String systemDetails() {
         return this.joinDetails(new String[]{this.isOsLinux() ? this.systemLinux("") : this.systemWindowsNT(true), this.revision(this.agentVersion)});
      }

      protected String platform() {
         return this.platformGecko();
      }

      protected String platformDetails() {
         return "";
      }

      protected String extensions() {
         return this.extensionFirefox(this.agentVersion);
      }

      public int getWeight() {
         return 137000;
      }
   });
   protected final String mozillaVersion;
   protected final String agentVersion;
   protected final String platformVersion;

   public UserAgent(String mozillaVersion, String agentVersion, String platformVersion) {
      this.mozillaVersion = mozillaVersion;
      this.agentVersion = agentVersion;
      this.platformVersion = platformVersion;
   }

   protected abstract String systemDetails();

   protected abstract String platform();

   protected abstract String platformDetails();

   protected abstract String extensions();

   protected String bracketedOrEmpty(String string) {
      return string.isEmpty() ? string : "(" + string + ")";
   }

   protected boolean isOsLinux() {
      return SystemUtils.IS_OS_LINUX;
   }

   protected boolean isOsWindows() {
      return SystemUtils.IS_OS_WINDOWS;
   }

   protected String systemLinux(String name) {
      return this.joinDetails("X11", name, "Linux x86_64");
   }

   protected String systemWindowsNT(boolean x64) {
      String os = this.joinMain("Windows", "NT", SystemUtils.OS_VERSION);
      return x64 ? this.joinDetails(os, "Win64", "x64") : os;
   }

   protected String platformAppleWebKit() {
      return "AppleWebKit/" + this.platformVersion;
   }

   protected String platformGecko() {
      return "Gecko/" + this.platformVersion;
   }

   protected String extensionChrome(String version) {
      return "Chrome/" + version;
   }

   protected String extensionSafari(String version) {
      return "Safari/" + version;
   }

   protected String extensionFirefox(String version) {
      return "Firefox/" + version;
   }

   protected String revision(String version) {
      return "rv:" + version;
   }

   protected String joinMain(String... elements) {
      return CommonString.Joiner.with(" ").join(elements);
   }

   protected String joinDetails(String... details) {
      return CommonString.Joiner.with("; ").join(details);
   }

   public String toString() {
      return this.joinMain("Mozilla/" + this.mozillaVersion, this.bracketedOrEmpty(this.systemDetails()), this.platform(), this.bracketedOrEmpty(this.platformDetails()), this.extensions());
   }
}
