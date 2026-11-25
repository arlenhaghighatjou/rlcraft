package net.ilexiconn.llibrary.client.model.obj;

import java.util.Locale;
import org.lwjgl.util.vector.Vector2f;

public class TextureCoords {
   private Vector2f uvCoords;
   private int index;

   public TextureCoords(float u, float v) {
      this(new Vector2f(u, v));
   }

   public TextureCoords(Vector2f uvCoords) {
      this.uvCoords = uvCoords;
   }

   public void register(OBJModel model) {
      this.index = model.getUVIndex();
   }

   public int getIndex() {
      return this.index;
   }

   public String toString() {
      return "vt " + String.format(Locale.US, "%.6f", this.uvCoords.x) + " " + String.format(Locale.US, "%.6f", this.uvCoords.y);
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof TextureCoords)) {
         return false;
      } else {
         TextureCoords uv = (TextureCoords)obj;
         return uv.uvCoords.x == this.uvCoords.x && uv.uvCoords.y == this.uvCoords.y;
      }
   }
}
