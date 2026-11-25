package slimeknights.mantle.client.book.data.content;

import java.util.ArrayList;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.element.ImageData;
import slimeknights.mantle.client.book.data.element.ItemStackData;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.gui.book.GuiBook;
import slimeknights.mantle.client.gui.book.Textures;
import slimeknights.mantle.client.gui.book.element.BookElement;
import slimeknights.mantle.client.gui.book.element.ElementImage;
import slimeknights.mantle.client.gui.book.element.ElementItem;
import slimeknights.mantle.client.gui.book.element.ElementText;

public class ContentBlockInteraction extends PageContent {
   public static final transient int TEX_SIZE = 512;
   public static final transient ImageData IMG_SMITHING = new ImageData(Textures.TEX_MISC, 0, 0, 88, 55, 512, 512);
   public static final transient int INPUT_X = 6;
   public static final transient int INPUT_Y = 18;
   public static final transient int BLOCK_X = 40;
   public static final transient int BLOCK_Y = 26;
   public static final transient float ITEM_SCALE = 2.0F;
   public static final transient float BLOCK_SCALE = 5.0F;
   public String title = "Block Interaction";
   public ItemStackData input;
   public ItemStackData block;
   public TextData[] description;

   public ContentBlockInteraction() {
   }

   public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
      int x = GuiBook.PAGE_WIDTH / 2 - IMG_SMITHING.width / 2 - 10;
      int y = 16;
      if (this.title != null && !this.title.isEmpty()) {
         this.addTitle(list, this.title);
      } else {
         y = 0;
      }

      list.add(new ElementImage(x, y, IMG_SMITHING.width, IMG_SMITHING.height, IMG_SMITHING, book.appearance.slotColor));
      if (this.input != null && !this.input.id.equals("")) {
         list.add(new ElementItem(x + 6, y + 18, 2.0F, this.input.getItems(), this.input.action));
      }

      if (this.block != null && !this.block.id.equals("")) {
         list.add(new ElementItem(x + 40, y + 26, 5.0F, this.block.getItems(), this.block.action));
      }

      if (this.description != null && this.description.length > 0) {
         list.add(new ElementText(0, IMG_SMITHING.height + y + 50, GuiBook.PAGE_WIDTH, GuiBook.PAGE_HEIGHT - IMG_SMITHING.height - y - 50, this.description));
      }

   }
}
