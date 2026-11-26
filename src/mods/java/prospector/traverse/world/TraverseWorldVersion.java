package prospector.traverse.world;

import net.lax1dude.eaglercraft.v1_8.sp.server.WorldsDB;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import prospector.traverse.core.TraverseConstants;
import prospector.traverse.shadow.shootingstar.version.Version;

public class TraverseWorldVersion {
   public static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   public VFile2 versionConfig;
   public Version version;

   public TraverseWorldVersion(VFile2 worldDir) {
      VFile2 traverseDir = WorldsDB.newVFile(worldDir, "traverse");
      this.versionConfig = WorldsDB.newVFile(traverseDir, "instance_version.json");
      if ("1".equals("@major@")) {
         this.version = TraverseConstants.DEV_VERSION;
      } else {
         this.version = new Version(Integer.parseInt("1"), Integer.parseInt("6"), Integer.parseInt("0"));
      }

      this.reloadVersionFile();
   }

   public void reloadVersionFile() {
      if (!this.versionConfig.exists()) {
         this.writeVersionFile(new TraverseWorldVersion.VersionConfig(this.version));
      }

      if (this.versionConfig.exists()) {
         TraverseWorldVersion.VersionConfig config = null;

         try {
            config = TraverseWorldVersion.VersionConfig.fromJson(GSON.fromJson(this.versionConfig.getAllChars(), JsonObject.class));
            this.version = config.version;
         } catch (Exception var15) {
            var15.printStackTrace();
         }

         if (config == null) {
            config = new TraverseWorldVersion.VersionConfig(this.version);
            this.writeVersionFile(config);
         }
      }

   }

   public void writeVersionFile(TraverseWorldVersion.VersionConfig config) {
      this.versionConfig.setAllChars(GSON.toJson(config.toJson()));

      this.reloadVersionFile();
   }

   public static class VersionConfig {
      public Version version;

      public VersionConfig(Version version) {
         this.version = version;
      }

      public JsonObject toJson() {
         JsonObject json = new JsonObject();
         if (this.version != null) {
            JsonObject versionJson = new JsonObject();
            versionJson.addProperty("major", this.version.major);
            versionJson.addProperty("minor", this.version.minor);
            versionJson.addProperty("patch", this.version.patch);
            json.add("version", versionJson);
         }

         return json;
      }

      public static TraverseWorldVersion.VersionConfig fromJson(JsonObject json) {
         Version version = null;
         if (json.has("version") && json.get("version").isJsonObject()) {
            JsonObject versionJson = json.getAsJsonObject("version");
            version = new Version(versionJson.has("major") ? versionJson.get("major").getAsInt() : 0, versionJson.has("minor") ? versionJson.get("minor").getAsInt() : 0, versionJson.has("patch") ? versionJson.get("patch").getAsInt() : 0);
         }

         return new TraverseWorldVersion.VersionConfig(version);
      }

      public Version getVersion() {
         return this.version;
      }

      public void setVersion(Version version) {
         this.version = version;
      }
   }
}
