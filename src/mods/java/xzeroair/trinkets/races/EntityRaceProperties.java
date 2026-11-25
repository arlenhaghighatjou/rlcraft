package xzeroair.trinkets.races;

public class EntityRaceProperties {
   private String name = "Human";
   private String uuid = "00000000-0000-0000-0000-000000000000";
   private int primaryColor = 0;
   private int secondaryColor = 16777215;
   private int magicAffinityValue = 100;
   private int raceSize = 100;

   public EntityRaceProperties() {
   }

   public EntityRaceProperties(String name, String uuid, int primaryColor, int secondaryColor, int magicAffinityValue, int raceSize) {
      this.name = name;
      this.uuid = uuid;
      this.primaryColor = primaryColor;
      this.secondaryColor = secondaryColor;
      this.magicAffinityValue = magicAffinityValue;
      this.raceSize = raceSize;
   }

   public String getName() {
      return this.name;
   }

   public EntityRaceProperties setName(String name) {
      this.name = name;
      return this;
   }

   public String getUuid() {
      return this.uuid;
   }

   public EntityRaceProperties setUuid(String uuid) {
      this.uuid = uuid;
      return this;
   }

   public int getPrimaryColor() {
      return this.primaryColor;
   }

   public EntityRaceProperties setPrimaryColor(int primaryColor) {
      this.primaryColor = primaryColor;
      return this;
   }

   public int getSecondaryColor() {
      return this.secondaryColor;
   }

   public EntityRaceProperties setSecondaryColor(int secondaryColor) {
      this.secondaryColor = secondaryColor;
      return this;
   }

   public int getMagicAffinityValue() {
      return this.magicAffinityValue;
   }

   public EntityRaceProperties setMagicAffinityValue(int magicAffinityValue) {
      this.magicAffinityValue = magicAffinityValue;
      return this;
   }

   public int getRaceSize() {
      return this.raceSize;
   }

   public EntityRaceProperties setRaceSize(int raceSize) {
      this.raceSize = raceSize;
      return this;
   }
}
