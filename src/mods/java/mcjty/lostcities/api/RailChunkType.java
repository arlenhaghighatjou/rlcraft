package mcjty.lostcities.api;

public enum RailChunkType {
   NONE(false, false),
   STATION_SURFACE(true, true),
   STATION_UNDERGROUND(true, false),
   STATION_EXTENSION_SURFACE(true, true),
   STATION_EXTENSION_UNDERGROUND(true, false),
   GOING_DOWN_TWO_FROM_SURFACE(false, true),
   GOING_DOWN_ONE_FROM_SURFACE(false, true),
   GOING_DOWN_FURTHER(false, false),
   HORIZONTAL(false, false),
   THREE_SPLIT(false, false),
   VERTICAL(false, false),
   DOUBLE_BEND(false, false),
   RAILS_END_HERE(false, false);

   private final boolean isStation;
   private final boolean isSurface;

   private RailChunkType(boolean isStation, boolean isSurface) {
      this.isStation = isStation;
      this.isSurface = isSurface;
   }

   public boolean isStation() {
      return this.isStation;
   }

   public boolean isSurface() {
      return this.isSurface;
   }
}
