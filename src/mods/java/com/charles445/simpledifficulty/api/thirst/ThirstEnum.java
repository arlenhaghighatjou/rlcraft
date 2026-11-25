package com.charles445.simpledifficulty.api.thirst;

public enum ThirstEnum {
   NORMAL("normal", 3, 0.3F, 0.75F),
   RAIN("rain", 1, 0.05F, 0.0F),
   POTION("potion", 2, 0.2F, 0.0F),
   PURIFIED("purified", 6, 3.0F, 0.0F);

   private String name;
   private int thirst;
   private float saturation;
   private float dirty;

   private ThirstEnum(String name, int thirst, float saturation, float dirty) {
      this.name = name;
      this.thirst = thirst;
      this.saturation = saturation;
      this.dirty = dirty;
   }

   public String getName() {
      return this.name;
   }

   public int getThirst() {
      return this.thirst;
   }

   public float getSaturation() {
      return this.saturation;
   }

   public float getThirstyChance() {
      return this.dirty;
   }

   public String toString() {
      return this.getName();
   }
}
