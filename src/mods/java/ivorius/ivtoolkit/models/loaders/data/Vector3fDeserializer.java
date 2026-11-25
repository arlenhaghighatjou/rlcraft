package ivorius.ivtoolkit.models.loaders.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.lax1dude.eaglercraft.v1_8.vector.Vector3f;

public class Vector3fDeserializer implements JsonDeserializer<Vector3f> {
   public Vector3fDeserializer() {
   }

   public Vector3f deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      float[] array = (float[])context.deserialize(json, float[].class);
      return new Vector3f(array[0], array[1], array[2]);
   }
}
