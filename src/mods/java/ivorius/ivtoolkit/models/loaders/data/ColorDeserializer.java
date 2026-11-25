package ivorius.ivtoolkit.models.loaders.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.awt.Color;
import java.lang.reflect.Type;

public class ColorDeserializer implements JsonDeserializer<Color> {
   public ColorDeserializer() {
   }

   public Color deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      float[] array = (float[])context.deserialize(json, float[].class);
      return new Color(array[0], array[1], array[2], array.length >= 4 ? array[3] : 1.0F);
   }
}
