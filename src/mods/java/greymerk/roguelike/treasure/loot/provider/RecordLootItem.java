package greymerk.roguelike.treasure.loot.provider;

import com.github.fnar.minecraft.item.Record;
import com.github.fnar.minecraft.item.RldItemStack;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

public class RecordLootItem extends LootItem {
   public RecordLootItem(int weight, int level) {
      super(weight, level);
   }

   public RldItemStack getLootItem(EaglercraftRandom random) {
      Record.Song song = Record.Song.chooseRandom(random);
      return Record.newRecord().withSong(song).asStack();
   }
}
