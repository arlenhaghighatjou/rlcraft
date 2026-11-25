package ivorius.ivtoolkit.models.loaders.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import org.lwjgl.util.vector.Quaternion;

public class QuaternionDeserializer implements JsonDeserializer<Quaternion> {
   public QuaternionDeserializer() {
   }

   public Quaternion deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      float[] array = (float[])context.deserialize(json, float[].class);
      return new Quaternion(array[0], array[1], array[2], array[3]);
   }
}
