package slimeknights.mantle.client.book.data;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResource;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.client.book.BookLoader;
import slimeknights.mantle.client.book.data.content.ContentError;
import slimeknights.mantle.client.book.data.content.PageContent;
import slimeknights.mantle.client.book.data.element.BlockData;
import slimeknights.mantle.client.book.data.element.DataLocation;
import slimeknights.mantle.client.book.data.element.ItemStackData;
import slimeknights.mantle.client.book.repository.BookRepository;

@SideOnly(Side.CLIENT)
public class PageData implements IDataItem {
   private static final transient ArrayList<PageData.ValueHotswap> hotswaps = new ArrayList();
   public String name;
   public String type;
   public String data;
   public float scale;
   public transient SectionData parent;
   public transient BookRepository source;
   public transient PageContent content;

   public PageData() {
      this(false);
   }

   public PageData(boolean custom) {
      this.name = null;
      this.type = "";
      this.data = "";
      this.scale = 1.0F;
      if (custom) {
         this.data = "no-load";
      }

   }

   public String translate(String string) {
      return this.parent.translate(string);
   }

   public void load() {
      if (this.name == null) {
         this.name = "page" + this.parent.unnamedPageCounter++;
      }

      this.name = this.name.toLowerCase();
      if (!this.data.equals("no-load")) {
         IResource pageInfo = this.source.getResource(this.source.getResourceLocation(this.data));
         if (pageInfo != null) {
            String data = this.source.resourceToString(pageInfo);
            if (!data.isEmpty()) {
               Class<? extends PageContent> ctype = BookLoader.getPageType(this.type);

               try {
                  this.content = (PageContent)BookLoader.GSON.fromJson(data, ctype);
               } catch (Exception var18) {
                  this.content = new ContentError(ctype == null ? "Failed to create a page of type \"" + this.type + "\", perhaps the type is not registered?" : "Failed to create a page of type \"" + this.type + "\", perhaps the page file \"" + this.data + "\" is missing or invalid?", var18);
               }
            }
         }
      }

      if (this.content == null) {
         try {
            this.content = (PageContent)BookLoader.getPageType(this.type).newInstance();
         } catch (IllegalAccessException | NullPointerException | InstantiationException var15) {
            this.content = new ContentError("Failed to create a page of type \"" + this.type + "\", perhaps the type is not registered?");
         }
      }

      try {
         this.content.parent = this;
         this.content.load();
      } catch (Exception var14) {
         this.content = new ContentError("Failed to load page " + this.parent.name + "." + this.name + ".", var14);
         var14.printStackTrace();
      }

      this.content.source = this.source;

      for(Field f : this.content.getClass().getFields()) {
         if (!Modifier.isTransient(f.getModifiers()) && !Modifier.isStatic(f.getModifiers()) && !Modifier.isFinal(f.getModifiers())) {
            try {
               if (f.get(this.content) == null) {
                  continue;
               }
            } catch (IllegalAccessException var16) {
               var16.printStackTrace();
            }

            for(PageData.ValueHotswap swap : hotswaps) {
               Class<?> c = f.getType();
               if (c.isArray() && c.getComponentType().isAssignableFrom(swap.t)) {
                  try {
                     f.setAccessible(true);
                     Object[] o = f.get(this.content);

                     for(Object ob : o) {
                        swap.swap(this.source, ob);
                     }
                  } catch (IllegalAccessException var17) {
                     var17.printStackTrace();
                  }
               } else if (swap.t.isAssignableFrom(c)) {
                  try {
                     f.setAccessible(true);
                     Object o = f.get(this.content);
                     swap.swap(this.source, o);
                  } catch (IllegalAccessException var13) {
                     var13.printStackTrace();
                  }
               }
            }
         }
      }

   }

   public static void addSwap(Class<?> t, Class<? extends PageData.ValueHotswap> swap) {
      try {
         PageData.ValueHotswap hotswap = (PageData.ValueHotswap)swap.newInstance();
         hotswap.t = t;
         hotswaps.add(hotswap);
      } catch (IllegalAccessException | InstantiationException var3) {
         var3.printStackTrace();
      }

   }

   public String getTitle() {
      String title = (String)this.parent.parent.strings.get(this.parent.name + "." + this.name);
      return title == null ? this.name : title;
   }

   static {
      addSwap(DataLocation.class, (new PageData.ValueHotswap<DataLocation>() {
         public void swap(BookRepository source, DataLocation object) {
            object.location = object.file == "$BLOCK_ATLAS" ? TextureMap.LOCATION_BLOCKS_TEXTURE : source.getResourceLocation(object.file, true);
         }
      }).getClass());
      addSwap(ItemStackData.class, (new PageData.ValueHotswap<ItemStackData>() {
         public void swap(BookRepository source, ItemStackData object) {
            object.source = source;
            object.itemListLocation = source.getResourceLocation(object.itemList);
            if (object.itemListLocation != null) {
               object.id = "->itemList";
            }

         }
      }).getClass());
      addSwap(BlockData.class, (new PageData.ValueHotswap<BlockData>() {
         public void swap(BookRepository source, BlockData object) {
            if (object.endPos == null) {
               object.endPos = object.pos;
            }

         }
      }).getClass());
   }

   public abstract static class ValueHotswap<T> {
      protected Class<?> t;

      public ValueHotswap() {
      }

      public abstract void swap(BookRepository var1, T var2);
   }
}
