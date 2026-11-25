package ivorius.reccomplex.random;

import ivorius.ivtoolkit.strings.LayeredStringGenerator;
import ivorius.ivtoolkit.strings.LayeredStringGenerator.LayerSimple;
import ivorius.ivtoolkit.strings.LayeredStringGenerator.LayerStatic;
import ivorius.ivtoolkit.strings.LayeredStringGenerator.LayerUppercase;
import java.util.Arrays;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

public class Person {
   private static final LayeredStringGenerator chaoticNameGen;
   public static List<String> nordicNamesMale = Arrays.asList("Erik", "Magnus", "John", "William", "Lukas", "Elias", "Malik", "Aron", "Enuk", "Christian", "Peter", "Hans", "Jens", "Niels", "Kristian", "Aage", "Johannes", "Carl", "Svend", "Sven", "Jakup", "Benjamin", "Danjal", "Hanus", "Rei", "Simun", "Bardur", "Johan", "Jonas", "Aleksi", "Ville", "Niko", "Juho", "Teemu", "Joonas", "Jesse", "Joni", "Jere", "Antti", "Ole", "Lars", "Jorgen", "Jakob", "Jon", "Daniel", "Sigurdur", "Arnar", "Kristofer", "Einar", "Gunnar", "Alexander", "Andri", "Viktor", "Olof", "Lennart", "Pall");
   public static List<String> nordicNamesFemale = Arrays.asList("Emma", "Eva", "Sofia", "Pipaluk", "Emilia", "Alice", "Marie", "Anna", "Margrethe", "Kristine", "Johanne", "Karen", "Elisabeth", "Ellen", "Ingeborg", "Rebekka", "Helena", "Vir", "Ronja", "Katrin", "Liv", "Maria", "Sara", "Jenna", "Laura", "Roosa", "Veera", "Emilia", "Julia", "Sara", "Jenni", "Noora", "Ane", "Johanne", "Dorthe", "Margrethe", "Sofie", "Else", "Amalie", "Gudrun", "Helga", "Birta", "Maria");
   private String firstName;
   private String middleName;
   private String lastName;

   public Person(String firstName, String middleName, String lastName) {
      this.firstName = firstName;
      this.middleName = middleName;
      this.lastName = lastName;
   }

   public static Person randomHuman(EaglercraftRandom random, boolean male) {
      Person.NameType nameType = random.nextFloat() < 0.95F ? Person.NameType.NORDIC : Person.NameType.CHAOTIC;
      return randomPerson(random, male, nameType);
   }

   public static Person randomPerson(EaglercraftRandom random, boolean male, Person.NameType type) {
      switch(type) {
      case CHAOTIC:
         return randomChaoticPerson(random, male);
      case NORDIC:
         return randomNordicPerson(random, male);
      default:
         throw new RuntimeException();
      }
   }

   private static Person randomChaoticPerson(EaglercraftRandom random, boolean male) {
      return new Person(chaoticName(random, male), random.nextFloat() < 0.15F ? chaoticName(random, male) : null, random.nextFloat() < 0.4F ? chaoticName(random, male) : null);
   }

   private static Person randomNordicPerson(EaglercraftRandom random, boolean male) {
      String middleName = random.nextFloat() < 0.3F ? nordicName(random, male) : null;
      String lastName = random.nextFloat() < 0.95F ? nordicLastName(random, male, random.nextFloat() < 0.1F) : null;
      return new Person(nordicName(random, male), middleName, lastName);
   }

   public static String nordicName(EaglercraftRandom random, boolean male) {
      return male ? (String)getRandomElementFrom(nordicNamesMale, random) : (String)getRandomElementFrom(nordicNamesFemale, random);
   }

   public static String nordicLastName(EaglercraftRandom random, boolean male, boolean parentMale) {
      return nordicName(random, parentMale) + (male ? "sson" : "sdottir");
   }

   public static String chaoticName(EaglercraftRandom random, boolean male) {
      return chaoticNameGen.randomString(random);
   }

   private static <O> O getRandomElementFrom(List<O> list, EaglercraftRandom random) {
      return (O)list.get(random.nextInt(list.size()));
   }

   public String getFirstName() {
      return this.firstName;
   }

   public String getMiddleName() {
      return this.middleName;
   }

   public String getLastName() {
      return this.lastName;
   }

   public String getFullName() {
      StringBuilder builder = new StringBuilder();
      if (this.firstName != null) {
         builder.append(this.firstName);
      }

      if (this.middleName != null) {
         builder.append(' ').append(this.middleName);
      }

      if (this.lastName != null) {
         builder.append(' ').append(this.lastName);
      }

      return builder.toString().trim();
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Person person = (Person)o;
         if (this.firstName != null) {
            if (!this.firstName.equals(person.firstName)) {
               return false;
            }
         } else if (person.firstName != null) {
            return false;
         }

         if (this.middleName != null) {
            if (!this.middleName.equals(person.middleName)) {
               return false;
            }
         } else if (person.middleName != null) {
            return false;
         }

         return this.lastName != null ? this.lastName.equals(person.lastName) : person.lastName == null;
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.firstName != null ? this.firstName.hashCode() : 0;
      result = 31 * result + (this.middleName != null ? this.middleName.hashCode() : 0);
      result = 31 * result + (this.lastName != null ? this.lastName.hashCode() : 0);
      return result;
   }

   static {
      LayerStatic vowels = new LayerStatic(new String[]{"a", "e", "i", "o", "u", "ei", "ai", "ou", "j", "ji", "y", "oi", "au", "oo"});
      LayerStatic startConsonants = new LayerStatic(new String[]{"b", "c", "d", "f", "g", "h", "k", "l", "m", "n", "p", "q", "r", "s", "t", "v", "w", "x", "z", "ch", "bl", "br", "fl", "gl", "gr", "kl", "pr", "st", "sh", "th"});
      LayerStatic endConsonants = new LayerStatic(new String[]{"b", "d", "f", "g", "h", "k", "l", "m", "n", "p", "r", "s", "t", "v", "w", "z", "ch", "gh", "nn", "st", "sh", "th", "tt", "ss", "pf", "nt"});
      LayerSimple chaoticWord = new LayerSimple(new Object[]{'c', startConsonants, 'v', vowels, 'd', endConsonants});
      chaoticWord.addStrings(1.0D, new String[]{"vdv", "cvdvd", "cvd", "vdvd"});
      LayerSimple baseChaoticName = new LayerSimple(new Object[]{'n', new LayerUppercase(chaoticWord)});
      baseChaoticName.addStrings(4.0D, new String[]{"n"});
      baseChaoticName.addStrings(1.0D, new String[]{"n-n"});
      chaoticNameGen = new LayeredStringGenerator(baseChaoticName);
   }

   public static enum NameType {
      NORDIC,
      CHAOTIC;

      private NameType() {
      }
   }
}
