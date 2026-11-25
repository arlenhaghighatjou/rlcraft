package crafttweaker.mc1120.entity;

import crafttweaker.api.entity.IEntityDrop;
import crafttweaker.api.item.IItemStack;
import crafttweaker.util.IntegerRange;

public class EntityDrop implements IEntityDrop {
   private final IItemStack itemStack;
   private final IntegerRange range;
   private final float chance;
   private final boolean playerOnly;

   public EntityDrop(IItemStack itemStack, int min, int max, float chance, boolean playerOnly) {
      this.itemStack = itemStack;
      this.range = new IntegerRange(min, max);
      this.chance = chance;
      this.playerOnly = playerOnly;
   }

   public EntityDrop(IItemStack itemStack, int min, int max, float chance) {
      this(itemStack, min, max, chance, false);
   }

   public IItemStack getItemStack() {
      return this.itemStack;
   }

   public int getMin() {
      return this.range.getMin();
   }

   public int getMax() {
      return this.range.getMax();
   }

   public IntegerRange getRange() {
      return this.range;
   }

   public float getChance() {
      return this.chance;
   }

   public boolean isPlayerOnly() {
      return this.playerOnly;
   }
}
