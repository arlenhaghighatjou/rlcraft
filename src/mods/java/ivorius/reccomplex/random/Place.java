package ivorius.reccomplex.random;

import java.util.Arrays;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

public class Place {
   private static List<String> placeTypes = Arrays.asList("hill", "mountain", "village", "town", "sea", "forest", "cave", "field", "tower", "dungeon", "realm", "dimension", "temple", "shrine", "hut", "shack", "cottage", "cavern", "crevice", "ruïns", "lowland", "plain", "peak", "cliff", "creek", "meadow", "tower", "lighthouse", "mansion", "sewer", "cellar", "church", "valley", "ravine", "ridge", "glacier", "river", "lake", "waterfall", "island", "isle", "reef");
   private static List<String> placeTraits = Arrays.asList("luscious", "heavenly", "lush", "dense", "palatial", "firey", "blazing", "hot", "mystical", "arcane", "anagogic", "hermetical", "orphic", "magical", "vast", "boundless", "colossal", "enormous", "extensive", "immensive", "monumental", "tremendous", "eternal", "pristine", "immaculate", "cold", "snowy", "frigid", "frozen", "frosty", "glacial", "brobdingnagian", "gamol", "icey", "barren", "deserted", "rotten", "undead", "cursed", "godlike", "vibrant", "doomed", "sacred", "divine");
   private String placeType;
   private String placeTrait;
   private String placeName;

   public Place(String placeType, String placeTrait, String placeName) {
      this.placeType = placeType;
      this.placeTrait = placeTrait;
      this.placeName = placeName;
   }

   public static Place randomPlace(EaglercraftRandom random) {
      String placeType = (String)getRandomElementFrom(placeTypes, random) + (random.nextFloat() < 0.6F ? "s" : "");
      String placeTrait = random.nextFloat() < 0.9F ? (String)getRandomElementFrom(placeTraits, random) : null;
      String placeName = null;
      if (random.nextFloat() < 0.6F) {
         switch(random.nextInt(4)) {
         case 0:
            placeName = "of ";
            break;
         case 1:
            placeName = "at ";
            break;
         case 2:
            placeName = "from ";
            break;
         case 3:
            placeName = " ";
         }

         placeName = placeName + Person.chaoticName(random, random.nextBoolean());
      }

      return new Place(placeType, placeTrait, placeName);
   }

   private static <O> O getRandomElementFrom(List<O> list, EaglercraftRandom random) {
      return (O)list.get(random.nextInt(list.size()));
   }

   public String getPlaceType() {
      return this.placeType;
   }

   public String getPlaceTrait() {
      return this.placeTrait;
   }

   public String getPlaceName() {
      return this.placeName;
   }

   public String getFullPlaceType() {
      return this.placeTrait != null ? this.placeTrait + " " + this.placeType : this.placeType;
   }

   public String getFullName() {
      return this.placeName != null ? this.getFullPlaceType() + " " + this.placeName : this.getFullPlaceType();
   }
}
