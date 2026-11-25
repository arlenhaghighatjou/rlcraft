package slimeknights.mantle.client.book.data;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.book.BookLoader;
import slimeknights.mantle.client.book.BookTransformer;
import slimeknights.mantle.client.book.data.content.ContentError;
import slimeknights.mantle.client.book.data.element.ItemStackData;
import slimeknights.mantle.client.book.repository.BookRepository;
import slimeknights.mantle.client.gui.book.GuiBook;

@SideOnly(Side.CLIENT)
public class BookData implements IDataItem {
   public transient int unnamedSectionCounter = 0;
   public transient ArrayList<SectionData> sections = new ArrayList();
   public transient AppearanceData appearance = new AppearanceData();
   public transient ArrayList<ItemStackData.ItemLink> itemLinks = new ArrayList();
   public transient HashMap<String, String> strings = new HashMap();
   public transient FontRenderer fontRenderer;
   private transient boolean initialized = false;
   protected final transient ArrayList<BookTransformer> transformers = new ArrayList();
   private ArrayList<BookRepository> repositories;

   public BookData(BookRepository... repositories) {
      this.repositories = new ArrayList(Arrays.asList(repositories));
   }

   public void reset() {
      this.initialized = false;
   }

   public void load() {
      if (!this.initialized) {
         Mantle.logger.info("Started loading book...");

         try {
            this.initialized = true;
            this.sections.clear();
            this.appearance = new AppearanceData();
            this.itemLinks.clear();

            for(BookRepository repo : this.repositories) {
               try {
                  List<SectionData> repoContents = repo.getSections();
                  this.sections.addAll(repoContents);

                  for(SectionData section : repoContents) {
                     section.source = repo;
                  }
               } catch (Exception var12) {
                  SectionData error = new SectionData();
                  error.name = "errorenous";
                  PageData page = new PageData(true);
                  page.name = "errorenous";
                  page.content = new ContentError("Failed to load repository " + repo.toString() + ".", var12);
                  error.pages.add(page);
                  this.sections.add(error);
                  var12.printStackTrace();
               }

               ResourceLocation appearanceLocation = repo.getResourceLocation("appearance.json");
               if (repo.resourceExists(appearanceLocation)) {
                  try {
                     this.appearance = (AppearanceData)BookLoader.GSON.fromJson(repo.resourceToString(repo.getResource(appearanceLocation)), AppearanceData.class);
                  } catch (Exception var11) {
                     var11.printStackTrace();
                  }
               }

               this.appearance.load();
               ResourceLocation itemLinkLocation = repo.getResourceLocation("items.json");
               if (repo.resourceExists(itemLinkLocation)) {
                  try {
                     this.itemLinks = new ArrayList(Arrays.asList(BookLoader.GSON.fromJson(repo.resourceToString(repo.getResource(itemLinkLocation)), ItemStackData.ItemLink[].class)));
                  } catch (Exception var10) {
                     var10.printStackTrace();
                  }
               }

               ResourceLocation languageLocation = repo.getResourceLocation("language.lang");
               if (repo.resourceExists(languageLocation)) {
                  try {
                     BufferedReader br = new BufferedReader(new InputStreamReader(repo.getResource(languageLocation).getInputStream(), "UTF-8"));

                     for(String next = br.readLine(); next != null; next = br.readLine()) {
                        if (!next.startsWith("//") && next.contains("=")) {
                           String key = next.substring(0, next.indexOf(61));
                           String value = next.substring(next.indexOf(61) + 1);
                           this.strings.put(key, value);
                        }
                     }
                  } catch (Exception var13) {
                     ;
                  }
               }
            }

            for(int i = 0; i < this.sections.size(); ++i) {
               SectionData section = (SectionData)this.sections.get(i);
               if (section.name != null) {
                  List<SectionData> matchingSections = (List)this.sections.stream().filter((sect) -> {
                     return section.name.equalsIgnoreCase(sect.name);
                  }).collect(Collectors.toList());
                  if (matchingSections.size() >= 2) {
                     LinkedSectionData linkedSection = new LinkedSectionData();

                     for(SectionData match : matchingSections) {
                        linkedSection.addSection(match);
                        if (match != section) {
                           this.sections.remove(match);
                        }
                     }

                     this.sections.set(i, linkedSection);
                  }
               }
            }

            for(SectionData section : this.sections) {
               if (section.source == null) {
                  section.source = BookRepository.DUMMY;
               }

               section.parent = this;
               section.load();
            }

            for(BookTransformer transformer : this.transformers) {
               transformer.transform(this);
            }

            for(SectionData section : this.sections) {
               if (section.source == null) {
                  section.source = BookRepository.DUMMY;
               }

               if (section.parent == null) {
                  section.parent = this;
                  section.load();
               }
            }
         } catch (Exception var14) {
            this.sections.clear();
            SectionData section = new SectionData(true);
            section.name = "errorenous";
            PageData page = new PageData(true);
            page.name = "errorenous";
            page.content = new ContentError("Failed to load the book due to an unexpected error.", var14);
            section.pages.add(page);
            this.sections.add(section);
            var14.printStackTrace();
         }

         Mantle.logger.info("Finished loading book");
      }
   }

   public SectionData findSection(String name) {
      return this.findSection(name, (GuiBook.AdvancementCache)null);
   }

   public SectionData findSection(String name, GuiBook.AdvancementCache advancementCache) {
      for(SectionData section : this.sections) {
         section.update(advancementCache);
         if (section.name.equals(name.toLowerCase())) {
            return section.isUnlocked(advancementCache) ? section : null;
         }
      }

      return null;
   }

   public int getFirstPageNumber(SectionData section) {
      return this.getFirstPageNumber(section, (GuiBook.AdvancementCache)null);
   }

   public int getFirstPageNumber(SectionData section, @Nullable GuiBook.AdvancementCache advancementCache) {
      int pages = 0;

      for(SectionData sect : this.sections) {
         sect.update(advancementCache);
         if (section == sect) {
            return section.isUnlocked(advancementCache) ? pages + 1 : -1;
         }

         if (sect.isUnlocked(advancementCache)) {
            pages += sect.getPageCount();
         }
      }

      return -1;
   }

   public PageData findPage(int number) {
      return this.findPage(number, (GuiBook.AdvancementCache)null);
   }

   public PageData findPage(int number, @Nullable GuiBook.AdvancementCache advancementCache) {
      if (number < 0) {
         return null;
      } else {
         int pages = 0;

         for(SectionData section : this.sections) {
            section.update(advancementCache);
            if (section.isUnlocked(advancementCache)) {
               if (pages + section.getPageCount() > number) {
                  return (PageData)section.pages.get(number - pages);
               }

               pages += section.getPageCount();
            }
         }

         return null;
      }
   }

   public PageData findPage(String location) {
      return this.findPage(location, (GuiBook.AdvancementCache)null);
   }

   public PageData findPage(String location, @Nullable GuiBook.AdvancementCache advancementCache) {
      return this.findPage(this.findPageNumber(location, advancementCache));
   }

   public int findPageNumber(String location) {
      return this.findPageNumber(location, (GuiBook.AdvancementCache)null);
   }

   public int findPageNumber(String location, @Nullable GuiBook.AdvancementCache advancementCache) {
      location = location.toLowerCase();
      int pages = 0;
      if (!location.contains(".")) {
         return -1;
      } else {
         String sectionName = location.substring(0, location.indexOf(46));
         String pageName = location.substring(location.indexOf(46) + 1);

         for(SectionData section : this.sections) {
            section.update(advancementCache);
            if (section.isUnlocked(advancementCache)) {
               if (!sectionName.equals(section.name)) {
                  pages += section.getPageCount();
               } else {
                  for(PageData page : section.pages) {
                     if (pageName.equals(page.name)) {
                        return pages + 1;
                     }

                     ++pages;
                  }
               }
            }
         }

         return -1;
      }
   }

   public int getPageCount() {
      return this.getPageCount((GuiBook.AdvancementCache)null);
   }

   public int getPageCount(@Nullable GuiBook.AdvancementCache advancementCache) {
      int pages = 0;

      for(SectionData section : this.sections) {
         section.update(advancementCache);
         pages += section.isUnlocked(advancementCache) ? section.getPageCount() : 0;
      }

      return pages;
   }

   public int getFullPageCount() {
      return this.getFullPageCount((GuiBook.AdvancementCache)null);
   }

   public int getFullPageCount(@Nullable GuiBook.AdvancementCache advancementCache) {
      return (int)Math.ceil((double)((float)(this.getPageCount(advancementCache) - 1) / 2.0F)) + 1;
   }

   public String getItemAction(ItemStackData item) {
      for(ItemStackData.ItemLink link : this.itemLinks) {
         if (item.id.equals(link.item.id) && (!link.damageSensitive || item.damage == link.item.damage)) {
            return link.action;
         }
      }

      return "";
   }

   public List<SectionData> getVisibleSections(GuiBook.AdvancementCache advancementCache) {
      List<SectionData> visible = new ArrayList();

      for(SectionData section : this.sections) {
         if (section.isUnlocked(advancementCache) || !section.hideWhenLocked) {
            visible.add(section);
         }
      }

      return visible;
   }

   public String translate(String string) {
      String out = (String)this.strings.get(string);
      return out != null ? out : string;
   }

   public void openGui(@Nullable ItemStack item) {
      if (!this.initialized) {
         this.load();
      }

      if (Minecraft.getMinecraft().player != null) {
         Minecraft.getMinecraft().displayGuiScreen(new GuiBook(this, item));
      }

   }

   public void addRepository(BookRepository repository) {
      if (repository != null && !this.repositories.contains(repository)) {
         this.repositories.add(repository);
      }

   }

   public void addTransformer(BookTransformer transformer) {
      if (transformer != null && !this.transformers.contains(transformer)) {
         this.transformers.add(transformer);
      }

   }
}
