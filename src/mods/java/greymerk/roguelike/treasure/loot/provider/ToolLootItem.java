package greymerk.roguelike.treasure.loot.provider;

import com.github.fnar.minecraft.item.RldItemStack;
import com.github.fnar.minecraft.item.ToolType;
import com.github.fnar.roguelike.loot.special.tools.SpecialTool;
import greymerk.roguelike.treasure.loot.Equipment;
import greymerk.roguelike.treasure.loot.Quality;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

public class ToolLootItem extends LootItem {
   private Equipment type;
   private Quality quality;
   private boolean enchant;

   public ToolLootItem(int weight, int level) {
      super(weight, level);
   }

   public ToolLootItem(int weight, int level, Equipment type, Quality quality, boolean enchant) {
      super(weight, level);
      this.type = type;
      this.enchant = enchant;
      this.quality = quality;
   }

   public RldItemStack getLootItem(Random random) {
      return SpecialtyLootItem.rollForSpecial(random) ? SpecialTool.createTool(random, this.getQuality(random)) : this.getToolType(random).asItem().withQuality(this.getQuality(random)).plzEnchantAtLevel(this.getEnchantLevel(random)).asStack();
   }

   private int getEnchantLevel(Random random) {
      return this.enchant ? LootItem.getEnchantmentLevel(random, this.level) : 0;
   }

   private Quality getQuality(Random random) {
      return (Quality)Optional.ofNullable(this.quality).orElseGet(() -> {
         return Equipment.rollQuality(random, this.level);
      });
   }

   private ToolType getToolType(Random random) {
      return (ToolType)Optional.ofNullable(this.type).map(Equipment::asToolType).orElseGet(() -> {
         return ToolType.random(random);
      });
   }
}
