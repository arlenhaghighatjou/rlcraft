package hunternif.mc.atlas.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;

public abstract class AbstractJSONConfig<T extends SaveData> implements Config<T> {
   private final File file;

   protected AbstractJSONConfig(File file) {
      this.file = file;
   }

   public void load(T data) {
      JsonElement root = FileUtil.readJson(this.file);
      if (root == null) {
         Log.info("Config %s not found; creating new", this.file.getName());
         this.save(data);
      } else {
         try {
            JsonElement versionElem = root.getAsJsonObject().get("version");
            boolean outdated = false;
            JsonObject jsonData;
            int version;
            if (versionElem == null) {
               Log.warn("Outdated config %s", this.file.getName());
               version = 0;
               jsonData = root.getAsJsonObject();
               outdated = true;
            } else {
               version = versionElem.getAsInt();
               if (this.currentVersion() > version) {
                  Log.warn("Outdated config %s: version was %d, but current is %d", this.file.getName(), version, this.currentVersion());
                  outdated = true;
               }

               JsonElement jsonElem = root.getAsJsonObject().get("data");
               if (jsonElem == null) {
                  Log.error("Malformed config " + this.file.getName());
                  return;
               }

               jsonData = jsonElem.getAsJsonObject();
            }

            this.loadData(jsonData, data, version);
            if (outdated) {
               this.save(data);
            }
         } catch (NumberFormatException | IllegalStateException var8) {
            Log.error(var8, "Malformed config %s", this.file.getName());
         }

      }
   }

   public void save(T data) {
      JsonObject root = new JsonObject();
      root.addProperty("version", Integer.valueOf(this.currentVersion()));
      JsonObject jsonData = new JsonObject();
      this.saveData(jsonData, data);
      root.add("data", jsonData);
      FileUtil.writeJson(root, this.file);
   }

   protected abstract void loadData(JsonObject var1, T var2, int var3);

   protected abstract void saveData(JsonObject var1, T var2);

   protected abstract int currentVersion();
}
