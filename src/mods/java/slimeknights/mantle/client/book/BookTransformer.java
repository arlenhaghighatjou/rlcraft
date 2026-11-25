package slimeknights.mantle.client.book;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.SectionData;
import slimeknights.mantle.client.book.data.content.ContentSectionList;
import slimeknights.mantle.client.book.data.content.ContentTableOfContents;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.gui.book.GuiBook;

public abstract class BookTransformer {
   public BookTransformer() {
   }

   public static BookTransformer IndexTranformer() {
      return BookTransformer.IndexTranformer.INSTANCE;
   }

   public static BookTransformer contentTableTransformer() {
      return BookTransformer.ContentTableTransformer.INSTANCE;
   }

   public static BookTransformer contentTableTransformerForSection(String sectionName) {
      return new BookTransformer.ContentTableTransformer(sectionName);
   }

   public abstract void transform(BookData var1);

   protected static class ContentTableTransformer extends BookTransformer {
      public static final BookTransformer.ContentTableTransformer INSTANCE = new BookTransformer.ContentTableTransformer();
      private final String sectionToTransform;

      public ContentTableTransformer(String sectionToTransform) {
         this.sectionToTransform = sectionToTransform;
      }

      public ContentTableTransformer() {
         this.sectionToTransform = null;
      }

      public void transform(BookData book) {
         int ENTRIES_PER_PAGE = 24;

         for(SectionData section : book.sections) {
            if (!section.name.equals("index") && (this.sectionToTransform == null || section.name.equals(this.sectionToTransform))) {
               int genPages = (int)Math.ceil((double)((float)section.getPageCount() * 1.0F / 24.0F));
               if (genPages != 0) {
                  PageData[] pages = new PageData[genPages];

                  for(int i = 0; i < pages.length; ++i) {
                     pages[i] = new PageData(true);
                     pages[i].name = "tableofcontents" + i;
                     TextData[] text = new TextData[i > pages.length - 1 ? 24 : section.getPageCount() - (genPages - 1) * 24];

                     for(int j = 0; j < text.length; ++j) {
                        text[j] = new TextData(i * 24 + j + 1 + ". " + ((PageData)section.pages.get(i * 24 + j)).getTitle());
                        text[j].action = "go-to-page-rtn:" + section.name + "." + ((PageData)section.pages.get(i * 24 + j)).name;
                     }

                     pages[i].content = new ContentTableOfContents(i == 0 ? section.getTitle() : "", text);
                  }

                  for(int i = pages.length - 1; i >= 0; --i) {
                     section.pages.add(0, pages[i]);
                  }
               }
            }
         }

      }
   }

   protected static class IndexTranformer extends BookTransformer {
      public static final BookTransformer.IndexTranformer INSTANCE = new BookTransformer.IndexTranformer();

      protected IndexTranformer() {
      }

      public void transform(BookData book) {
         SectionData index = new SectionData(true) {
            public void update(GuiBook.AdvancementCache advancementCache) {
               this.pages.clear();
               List<SectionData> visibleSections = this.parent.getVisibleSections(advancementCache);
               if (!visibleSections.isEmpty()) {
                  visibleSections.remove(0);
                  PageData[] pages = new PageData[(int)Math.ceil((double)((float)visibleSections.size() / 9.0F))];

                  for(int i = 0; i < pages.length; ++i) {
                     pages[i] = new PageData(true);
                     pages[i].name = "page" + (i + 1);
                     ContentSectionList content = new ContentSectionList();
                     pages[i].content = content;

                     for(int j = i * 9; j - i * 9 < 9 && j < visibleSections.size(); ++j) {
                        content.addSection((SectionData)visibleSections.get(j));
                     }
                  }

                  this.pages = new ArrayList(Arrays.asList(pages));
               }
            }
         };
         index.name = "index";
         book.sections.add(0, index);
      }
   }
}
