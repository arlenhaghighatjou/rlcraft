package ivorius.reccomplex.utils.presets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ivorius.reccomplex.json.JsonUtils;
import java.lang.reflect.Type;

public class PresettedObjects {
   public PresettedObjects() {
   }

   public static <T> boolean read(JsonObject jsonObject, Gson gson, PresettedObject<T> object, String presetKey, String objectKey, Type type) {
      if (jsonObject.has(presetKey) && object.setPreset(JsonUtils.getString(jsonObject, presetKey))) {
         return true;
      } else if (!jsonObject.has(objectKey)) {
         object.setToDefault();
         return false;
      } else {
         object.setContents(gson.fromJson(jsonObject.get(objectKey), type));
         return true;
      }
   }

   public static <T> void write(JsonObject jsonObject, Gson gson, PresettedObject<T> object, String presetKey, String objectKey) {
      if (object.getPreset() != null) {
         jsonObject.addProperty(presetKey, object.getPreset());
      }

      jsonObject.add(objectKey, gson.toJsonTree(object.getContents()));
   }
}
