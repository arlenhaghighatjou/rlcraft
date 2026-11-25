package hunternif.mc.atlas.marker;

import hunternif.mc.atlas.registry.MarkerType;
import hunternif.mc.atlas.util.IntVec2;
import hunternif.mc.atlas.util.Log;
import net.minecraft.client.resources.I18n;

public class Marker {
   public static final int TYPE_LIMIT = 128;
   public static final int LABEL_LIMIT = 128;
   private final int id;
   private final String type;
   private final String label;
   private final int dim;
   private final int x;
   private final int z;
   private final boolean visibleAhead;
   private boolean isGlobal;

   public Marker(int id, String type, String label, int dimension, int x, int z, boolean visibleAhead) {
      this.id = id;
      if (type.length() > 128) {
         type = type.substring(0, 128);
         Log.warn("Marker type is to long. Trimming to %d characters. Type: %s", Integer.valueOf(128), type);
      }

      this.type = type;
      if (label.length() > 128) {
         label = label.substring(0, 128);
         Log.warn("Marker type is to long. Trimming to %d characters. Type: %s", Integer.valueOf(128), label);
      }

      this.label = label == null ? "" : label;
      this.dim = dimension;
      this.x = x;
      this.z = z;
      this.visibleAhead = visibleAhead;
   }

   public int getId() {
      return this.id;
   }

   public String getType() {
      return this.type;
   }

   public boolean isOfType(MarkerType type) {
      return this.getType() != null && type != null ? this.getType().equals(type.getRegistryName().toString()) : false;
   }

   public String getLabel() {
      return this.label;
   }

   public String getLocalizedLabel() {
      String[] parts = this.label.split("\\s", 2);
      String key = parts[0];
      if (key.matches("([a-zA-Z_]\\.?)+")) {
         String param = parts.length > 1 ? parts[1] : "";
         String translated = I18n.format(key, new Object[]{param});
         if (!key.equals(translated)) {
            return translated;
         }
      }

      return this.label;
   }

   public int getDimension() {
      return this.dim;
   }

   public int getX() {
      return this.x;
   }

   public int getZ() {
      return this.z;
   }

   public int getChunkX() {
      return this.x >> 4;
   }

   public int getChunkZ() {
      return this.z >> 4;
   }

   public boolean isVisibleAhead() {
      return this.visibleAhead;
   }

   public boolean isGlobal() {
      return this.isGlobal;
   }

   Marker setGlobal(boolean value) {
      this.isGlobal = value;
      return this;
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof Marker)) {
         return false;
      } else {
         Marker marker = (Marker)obj;
         return this.id == marker.id;
      }
   }

   public IntVec2 getChunkCoords() {
      return new IntVec2((double)(this.x >> 4), (double)(this.z >> 4));
   }

   public String toString() {
      return "#" + this.id + "\"" + this.label + "\"@(" + this.x + ", " + this.z + ")";
   }
}
