package reborncore.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import reborncore.common.IModInfo;
import reborncore.common.RebornCoreConfig;

/** @deprecated */
@Deprecated
public class VersionChecker {
   public static final String apiAddress = "https://files.modmuss50.me/api/v1/version.php";
   public String projectName;
   public IModInfo modInfo;
   ArrayList<VersionChecker.ModifacationVersionInfo> versions;
   public boolean isChecking;

   public VersionChecker(String projectName, IModInfo modInfo) {
      this.projectName = projectName;
      this.modInfo = modInfo;
   }

   public void checkVersion() throws IOException {
      if (RebornCoreConfig.versionCheck) {
         ;
      }

   }

   public void checkVersionThreaded() {
      (new Thread(new Runnable() {
         public void run() {
            try {
               VersionChecker.this.checkVersion();
            } catch (IOException var2) {
               var2.printStackTrace();
            }

         }
      })).start();
   }

   public boolean isLatestVersion() {
      return this.versions != null && !this.versions.isEmpty() ? ((VersionChecker.ModifacationVersionInfo)this.versions.get(0)).version.equals(this.modInfo.MOD_VERSION()) : true;
   }

   public VersionChecker.ModifacationVersionInfo getLatestVersion() {
      return this.versions != null && !this.versions.isEmpty() ? (VersionChecker.ModifacationVersionInfo)this.versions.get(0) : null;
   }

   public ArrayList<String> getChangeLogSinceCurrentVersion() {
      ArrayList<String> log = new ArrayList();
      if (!this.isLatestVersion()) {
         for(VersionChecker.ModifacationVersionInfo version : this.versions) {
            if (version.version.equals(this.modInfo.MOD_VERSION())) {
               break;
            }

            log.addAll(version.changeLog);
         }
      }

      return log;
   }

   public static void main(String[] args) throws IOException {
      System.out.println("Generating example json file");
      ArrayList<VersionChecker.ModifacationVersionInfo> infos = new ArrayList();
      ArrayList<String> changelog = new ArrayList();
      changelog.add("A change");
      changelog.add("Another change");
      infos.add(new VersionChecker.ModifacationVersionInfo("1.1.1", "1.7.10", changelog, "12th July", true));
      infos.add(new VersionChecker.ModifacationVersionInfo("1.2.0", "1.7.10", changelog, "28th July", true));
      Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
      String json = gson.toJson(infos);

      try {
         FileWriter writer = new FileWriter(new File("master.json"));
         writer.write(json);
         writer.close();
      } catch (IOException var6) {
         var6.printStackTrace();
      }

   }

   static class ModifacationVersionInfo {
      public String version;
      public String minecraftVersion;
      public ArrayList<String> changeLog;
      public String releaseDate;
      public boolean recommended;

      public ModifacationVersionInfo(String version, String minecraftVersion, ArrayList<String> changeLog, String releaseDate, boolean recommended) {
         this.version = version;
         this.minecraftVersion = minecraftVersion;
         this.changeLog = changeLog;
         this.releaseDate = releaseDate;
         this.recommended = recommended;
      }

      public ModifacationVersionInfo() {
      }
   }
}
