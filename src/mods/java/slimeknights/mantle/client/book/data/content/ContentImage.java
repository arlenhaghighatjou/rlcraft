package slimeknights.mantle.client.book.data.content;

import java.util.ArrayList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.element.ImageData;
import slimeknights.mantle.client.gui.book.GuiBook;
import slimeknights.mantle.client.gui.book.element.BookElement;
import slimeknights.mantle.client.gui.book.element.ElementImage;

@SideOnly(Side.CLIENT)
public class ContentImage extends PageContent {
   public String title = null;
   public ImageData image;

   public ContentImage() {
   }

   public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
      int y = 16;
      if (this.title != null && !this.title.isEmpty()) {
         this.addTitle(list, this.title);
      } else {
         y = 0;
      }

      if (this.image != null && this.image.location != null) {
         list.add(new ElementImage(0, y, GuiBook.PAGE_WIDTH, GuiBook.PAGE_HEIGHT - y, this.image));
      } else {
         list.add(new ElementImage(ImageData.MISSING));
      }

   }
}
