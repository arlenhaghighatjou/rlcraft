package greymerk.roguelike.treasure.loot.provider;

import com.github.fnar.minecraft.item.Potion;
import com.github.fnar.minecraft.item.RldItemStack;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

public class PotionLootItem extends LootItem {
   public PotionLootItem(int weight, int level) {
      super(weight, level);
   }

   public RldItemStack getLootItem(EaglercraftRandom random) {
      Potion.Effect[] effects = new Potion.Effect[]{Potion.Effect.HEALING, Potion.Effect.STRENGTH, Potion.Effect.SWIFTNESS, Potion.Effect.REGENERATION};
      Potion.Effect effect = Potion.Effect.chooseRandomAmong(random, effects);
      return Potion.newPotion().withForm(Potion.Form.REGULAR).withEffect(effect).withAmplification().asStack();
   }
}
