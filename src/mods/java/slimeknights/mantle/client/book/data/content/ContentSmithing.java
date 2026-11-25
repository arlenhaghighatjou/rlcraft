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

public class ContentSmithing extends PageContent {
   public static final transient int TEX_SIZE = 512;
   public static final transient ImageData IMG_SMITHING = new ImageData(Textures.TEX_MISC, 88, 0, 210, 42, 512, 512);
   public static final transient int INPUT_X = 5;
   public static final transient int INPUT_Y = 5;
   public static final transient int MODIFIER_X = 89;
   public static final transient int MODIFIER_Y = 5;
   public static final transient int RESULT_X = 173;
   public static final transient int RESULT_Y = 5;
   public static final transient float ITEM_SCALE = 2.0F;
   public String title = "Smithing";
   public ItemStackData input;
   public ItemStackData modifier;
   public ItemStackData result;
   public TextData[] description;

   public ContentSmithing() {
   }

   public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
      int x = GuiBook.PAGE_WIDTH / 2 - IMG_SMITHING.width / 2;
      int y = 16;
      if (this.title != null && !this.title.isEmpty()) {
         this.addTitle(list, this.title);
      } else {
         y = 0;
      }

      list.add(new ElementImage(x, y, IMG_SMITHING.width, IMG_SMITHING.height, IMG_SMITHING, book.appearance.slotColor));
      if (this.input != null && !this.input.id.equals("")) {
         list.add(new ElementItem(x + 5, y + 5, 2.0F, this.input.getItems(), this.input.action));
      }

      if (this.modifier != null && !this.modifier.id.equals("")) {
         list.add(new ElementItem(x + 89, y + 5, 2.0F, this.modifier.getItems(), this.modifier.action));
      }

      if (this.result != null && !this.result.id.equals("")) {
         list.add(new ElementItem(x + 173, y + 5, 2.0F, this.result.getItems(), this.result.action));
      }

      if (this.description != null && this.description.length > 0) {
         list.add(new ElementText(0, IMG_SMITHING.height + y + 5, GuiBook.PAGE_WIDTH, GuiBook.PAGE_HEIGHT - IMG_SMITHING.height - y - 5, this.description));
      }

   }
}
