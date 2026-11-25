package xzeroair.trinkets.traits.statuseffects;

public enum StatusEffectsEnum {
   Normal(nextID(), "Normal", 100),
   stun(nextID(), "Stun", 100),
   paralysis(nextID(), "Paralysis", 100),
   bleed(nextID(), "Bleed", 100),
   Invigorated(nextID(), "Invigorated", 100),
   charm(nextID(), "Charm", 100),
   Rage(nextID(), "Rage", 100),
   Berserk(nextID(), "Berserk", 100),
   Corrupted(nextID(), "Corrupted", 100),
   Infected(nextID(), "Infected", 100),
   Inspiration(nextID(), "Inspiration", 100),
   Undead(nextID(), "Undead", 100),
   Incorporeal(nextID(), "Incorporeal", 100),
   ManaUp(nextID(), "ManaUp", 100);

   private static final StatusEffectsEnum[] ID = new StatusEffectsEnum[values().length];
   private int index;
   private String name;
   private int chance;
   private static int IndexID = 0;

   private StatusEffectsEnum(int index, String name, int chance) {
      this.index = index;
      this.name = name;
      this.chance = chance;
   }

   private static int nextID() {
      return IndexID++;
   }

   public int getIndex() {
      return this.index;
   }

   public String getName() {
      return this.name;
   }

   public static StatusEffectsEnum Type(int value) {
      if (value < 0 || value >= ID.length) {
         value = 0;
      }

      return ID[value];
   }

   public static StatusEffectsEnum getStatusByIndex(int i) {
      for(StatusEffectsEnum status : values()) {
         if (status.getIndex() == i) {
            return status;
         }
      }

      return Normal;
   }

   public static StatusEffectsEnum getStatusByName(String string) {
      for(StatusEffectsEnum status : values()) {
         if (status.getName().equalsIgnoreCase(string)) {
            return status;
         }
      }

      return Normal;
   }
}
