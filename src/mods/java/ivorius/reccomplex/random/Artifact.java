package ivorius.reccomplex.random;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Artifact {
   private static List<String> objectTypes = Arrays.asList("tool", "eye", "board", "weapon", "toy", "memory", "relic", "orb", "block", "killer", "diminisher", "eater", "devourer", "helper", "teacher", "remedy", "prophet", "stone", "artifact", "scroll", "amulet", "ring", "tablet");
   private static List<String> traits = Arrays.asList("ominous", "odd", "uncontrollable", "catastrophical", "silent", "furious", "banned", "secret", "unknown", "popular", "forgotten", "lost", "heroic", "famous", "colossal", "mad", "wise", "uncontrollable", "glorious", "unprecedented", "unbelievable", "incredible", "lesser", "greater", "striking", "red", "gold", "silver", "blue", "marine", "glowing", "crimson", "violet", "white", "black", "sinister");
   private static List<String> powers = Arrays.asList("tears", "darkness", "gloom", "twilight", "light", "fire", "gold", "luck", "cold", "wealth", "fury", "magic", "nature", "beasts", "rapture", "salvation", "destruction", "perdition");
   private String objectType;
   private String trait;
   private String power;
   private String uniqueName;

   public Artifact(String objectType, String trait, String power, String uniqueName) {
      this.objectType = objectType;
      this.trait = trait;
      this.power = power;
      this.uniqueName = uniqueName;
   }

   public static Artifact randomArtifact(Random random) {
      return randomArtifact(random, (String)getRandomElementFrom(objectTypes, random));
   }

   public static Artifact randomArtifact(Random random, String objectType) {
      String trait = random.nextFloat() < 0.2F ? (String)getRandomElementFrom(traits, random) : null;
      String power = random.nextFloat() < 0.1F ? Person.chaoticName(random, random.nextBoolean()) : (random.nextFloat() < 0.8F ? (String)getRandomElementFrom(powers, random) : null);
      String uniqueName = random.nextFloat() < 0.2F ? Person.chaoticName(random, random.nextBoolean()) : null;
      return new Artifact(objectType, trait, power, uniqueName);
   }

   private static <O> O getRandomElementFrom(List<O> list, Random random) {
      return (O)list.get(random.nextInt(list.size()));
   }

   private static String firstCharUppercase(String name) {
      return Character.toString(name.charAt(0)).toUpperCase() + name.substring(1);
   }

   public String getObjectType() {
      return this.objectType;
   }

   public String getTrait() {
      return this.trait;
   }

   public String getPower() {
      return this.power;
   }

   public String getUniqueName() {
      return this.uniqueName;
   }

   public String getFullName() {
      StringBuilder builder = new StringBuilder();
      if (this.uniqueName != null) {
         builder.append(this.uniqueName).append(", the ");
      } else {
         builder.append("The ");
      }

      if (this.trait != null) {
         builder.append(firstCharUppercase(this.trait)).append(' ');
      }

      builder.append(this.objectType);
      if (this.power != null) {
         builder.append(" of ").append(firstCharUppercase(this.power));
      }

      return builder.toString();
   }
}
