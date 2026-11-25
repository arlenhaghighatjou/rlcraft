package xzeroair.trinkets.races;

import net.minecraft.util.math.MathHelper;

public class EntityRaceMixed extends EntityRace {
   EntityRace primary;
   EntityRace sub;

   public EntityRaceMixed(String name, String uuid) {
      super(name, uuid, 0, 0);
   }

   public EntityRace getPrimary() {
      return this.primary;
   }

   public EntityRaceMixed setPrimary(EntityRace primary) {
      this.primary = primary;
      return this;
   }

   public EntityRace getSub() {
      return this.sub;
   }

   public EntityRaceMixed setSub(EntityRace sub) {
      this.sub = sub;
      return this;
   }

   public int getMagicAffinity() {
      int a = this.getPrimary().magicAffinityValue;
      int b = this.getSub().magicAffinityValue;
      if (a > b) {
         return MathHelper.floor((1.5D * (double)a + (double)b) / 3.0D);
      } else {
         return a < b ? MathHelper.floor(((double)a + 1.5D * (double)b) / 3.0D) : MathHelper.floor((float)((a + b) / 2));
      }
   }
}
