package greymerk.roguelike.treasure.loot.provider;

import com.github.fnar.minecraft.item.RldItemStack;
import greymerk.roguelike.treasure.loot.Book;
import greymerk.roguelike.treasure.loot.books.BookStarter;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

public class ItemBook extends LootItem {
   Book.Special type;

   public ItemBook(Book.Special type) {
      this(type, 1, 0);
   }

   public ItemBook(Book.Special type, int weight, int level) {
      super(weight, level);
      this.type = type;
   }

   public RldItemStack getLootItem(EaglercraftRandom random) {
      return (new BookStarter()).asStack();
   }
}
