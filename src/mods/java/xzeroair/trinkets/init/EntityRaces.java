package xzeroair.trinkets.init;

import net.minecraft.util.ResourceLocation;
import xzeroair.trinkets.races.EntityRace;

public class EntityRaces {
   public static final EntityRace none = getRegisteredRace("none");
   public static final EntityRace human = getRegisteredRace("human");
   public static final EntityRace fairy = getRegisteredRace("fairy");
   public static final EntityRace dwarf = getRegisteredRace("dwarf");
   public static final EntityRace titan = getRegisteredRace("titan");
   public static final EntityRace goblin = getRegisteredRace("goblin");
   public static final EntityRace elf = getRegisteredRace("elf");
   public static final EntityRace faelis = getRegisteredRace("faelis");
   public static final EntityRace dragon = getRegisteredRace("dragon");

   public EntityRaces() {
   }

   private static EntityRace getRegisteredRace(String name) {
      EntityRace race = (EntityRace)EntityRace.Registry.getValue(new ResourceLocation("xat", name));
      if (race == null) {
         throw new IllegalStateException("Invalid Race requested: " + name);
      } else {
         return race;
      }
   }
}
