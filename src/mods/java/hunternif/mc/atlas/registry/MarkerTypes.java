package hunternif.mc.atlas.registry;

import hunternif.mc.atlas.client.Textures;
import net.minecraft.util.ResourceLocation;

public class MarkerTypes {
   public static final MarkerTypes INSTANCE = new MarkerTypes();
   public static MarkerType GOOGLE;
   public static MarkerType RED_X_LARGE;
   public static MarkerType RED_X_SMALL;
   public static MarkerType VILLAGE;
   public static MarkerType DIAMOND;
   public static MarkerType BED;
   public static MarkerType PICKAXE;
   public static MarkerType SWORD;
   public static MarkerType NETHER_PORTAL;
   public static MarkerType SKULL;
   public static MarkerType TOWER;
   public static MarkerType SCROLL;
   public static MarkerType TOMB;
   public static MarkerType END_CITY;
   public static MarkerType END_CITY_FAR;

   private MarkerTypes() {
      GOOGLE = this.marker("google", Textures.MARKER_GOOGLE_MARKER);
      RED_X_LARGE = this.marker("red_x_large", Textures.MARKER_RED_X_LARGE);
      RED_X_SMALL = this.marker("red_x_small", Textures.MARKER_RED_X_SMALL);
      DIAMOND = this.marker("diamond", Textures.MARKER_DIAMOND);
      BED = this.marker("bed", Textures.MARKER_BED);
      PICKAXE = this.marker("pickaxe", Textures.MARKER_PICKAXE);
      SWORD = this.marker("sword", Textures.MARKER_SWORD);
      SKULL = this.marker("skull", Textures.MARKER_SKULL);
      TOWER = this.marker("tower", Textures.MARKER_TOWER);
      SCROLL = this.marker("scroll", Textures.MARKER_SCROLL);
      TOMB = this.marker("tomb", Textures.MARKER_TOMB);
      VILLAGE = this.marker("village", Textures.MARKER_VILLAGE);
      NETHER_PORTAL = this.marker("nether_portal", Textures.MARKER_NETHER_PORTAL);
      END_CITY = this.marker("end_city", Textures.MARKER_END_CITY, Textures.MARKER_END_CITY_MIP_32, Textures.MARKER_END_CITY_MIP_16);
      END_CITY.setAlwaysShow(true).setClip(-1, 1000).setIsTile(true).setSize(2).setCenter(0.5D, 0.75D).setIsTechnical(true);
      END_CITY_FAR = this.marker("end_city_far", Textures.MARKER_END_CITY_FAR);
      END_CITY_FAR.setSize(1).setClip(-1000, -2).setIsTechnical(true);
   }

   private MarkerType marker(String name, ResourceLocation... textures) {
      MarkerType type = new MarkerType(new ResourceLocation("antiqueatlas", name), textures);
      this.reg(type);
      return type;
   }

   private void reg(MarkerType type) {
      MarkerRegistry.register(type);
   }
}
