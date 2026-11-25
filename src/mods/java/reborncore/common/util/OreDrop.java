package reborncore.common.util;

import java.util.Random;
import net.minecraft.item.ItemStack;

public class OreDrop {
   public ItemStack drop;
   public Integer baseChance;
   public Integer minQuantity;
   public int maxQuantity;

   public OreDrop(ItemStack drop, int maxQuantity) {
      this.drop = drop;
      this.minQuantity = drop.getCount();
      if (this.minQuantity.intValue() == 0) {
         this.minQuantity = Integer.valueOf(1);
      }

      this.baseChance = Integer.valueOf(100);
      this.maxQuantity = maxQuantity;
   }

   public OreDrop(ItemStack drop, double baseChance, int maxQuantity) {
      this.drop = drop;
      this.minQuantity = drop.getCount();
      if (this.minQuantity.intValue() == 0) {
         this.minQuantity = Integer.valueOf(1);
      }

      this.baseChance = (int)(baseChance * 100.0D);
      this.maxQuantity = maxQuantity;
   }

   public ItemStack getDrops(int fortuneLevel, Random random) {
      int count;
      if (this.baseChance.intValue() == 100) {
         count = this.calculateFortuneMulti(fortuneLevel, random);
      } else if (this.calculateFortuneSingle(fortuneLevel, random)) {
         count = this.calculateBaseQuantity(random);
      } else {
         count = 0;
      }

      return new ItemStack(this.drop.getItem(), count, this.drop.getItemDamage());
   }

   private int calculateFortuneMulti(int fortune, Random random) {
      int chanceOfEachBonus = 100 / (fortune + 2);
      int roll = random.nextInt(100);
      return roll < chanceOfEachBonus * fortune ? this.calculateBaseQuantity(random) * (roll / chanceOfEachBonus + 2) : this.calculateBaseQuantity(random);
   }

   private boolean calculateFortuneSingle(int fortune, Random random) {
      double total = (double)(this.baseChance.intValue() + fortune * this.baseChance.intValue() / 2);
      int roll = random.nextInt(100);
      return (double)roll <= total;
   }

   private int calculateBaseQuantity(Random random) {
      return this.minQuantity.intValue() + random.nextInt(this.maxQuantity - this.minQuantity.intValue() + 1);
   }
}
