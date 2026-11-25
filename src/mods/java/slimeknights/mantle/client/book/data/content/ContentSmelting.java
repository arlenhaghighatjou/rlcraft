package slimeknights.mantle.client.book.data.content;

import java.util.ArrayList;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
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

public class ContentSmelting extends PageContent {
   public static final transient int TEX_SIZE = 128;
   public static final transient ImageData IMG_SMELTING = new ImageData(Textures.TEX_SMELTING, 0, 0, 110, 114, 128, 128);
   public static final transient int INPUT_X = 5;
   public static final transient int INPUT_Y = 5;
   public static final transient int RESULT_X = 74;
   public static final transient int RESULT_Y = 41;
   public static final transient int FUEL_X = 5;
   public static final transient int FUEL_Y = 77;
   public static final transient float ITEM_SCALE = 2.0F;
   public String title = "Smelting";
   public ItemStackData input;
   public ItemStackData result;
   public TextData[] description;

   public ContentSmelting() {
   }

   public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
      int x = GuiBook.PAGE_WIDTH / 2 - IMG_SMELTING.width / 2;
      int y = 16;
      TextData tdTitle = new TextData(this.title);
      tdTitle.underlined = true;
      list.add(new ElementText(0, 0, GuiBook.PAGE_WIDTH, 9, new TextData[]{tdTitle}));
      list.add(new ElementImage(x, y, IMG_SMELTING.width, IMG_SMELTING.height, IMG_SMELTING, book.appearance.slotColor));
      if (this.input != null && !this.input.id.equals("")) {
         list.add(new ElementItem(x + 5, y + 5, 2.0F, this.input.getItems(), this.input.action));
      }

      if (this.result != null && !this.result.id.equals("")) {
         list.add(new ElementItem(x + 74, y + 41, 2.0F, this.result.getItems(), this.result.action));
      }

      list.add(new ElementItem(x + 5, y + 77, 2.0F, this.getFuelsList()));
      if (this.description != null && this.description.length > 0) {
         list.add(new ElementText(0, IMG_SMELTING.height + y + 5, GuiBook.PAGE_WIDTH, GuiBook.PAGE_HEIGHT - y - 5, this.description));
      }

   }

   public NonNullList<ItemStack> getFuelsList() {
      NonNullList<ItemStack> fuels = NonNullList.withSize(13, ItemStack.EMPTY);
      fuels.set(0, new ItemStack(Blocks.WOODEN_SLAB));
      fuels.set(1, new ItemStack(Blocks.PLANKS));
      fuels.set(2, new ItemStack(Blocks.COAL_BLOCK));
      fuels.set(3, new ItemStack(Items.WOODEN_PICKAXE));
      fuels.set(4, new ItemStack(Items.WOODEN_SWORD));
      fuels.set(5, new ItemStack(Items.WOODEN_HOE));
      fuels.set(6, new ItemStack(Items.STICK));
      fuels.set(7, new ItemStack(Items.COAL));
      fuels.set(8, new ItemStack(Items.LAVA_BUCKET));
      fuels.set(9, new ItemStack(Blocks.SAPLING));
      fuels.set(10, new ItemStack(Items.BLAZE_ROD));
      fuels.set(11, new ItemStack(Items.WOODEN_SHOVEL));
      fuels.set(12, new ItemStack(Items.WOODEN_AXE));
      return fuels;
   }
}
