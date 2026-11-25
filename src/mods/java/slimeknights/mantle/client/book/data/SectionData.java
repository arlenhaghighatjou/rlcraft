package slimeknights.mantle.client.book.data;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.resources.IResource;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.client.book.BookLoader;
import slimeknights.mantle.client.book.data.content.ContentError;
import slimeknights.mantle.client.book.data.element.ImageData;
import slimeknights.mantle.client.book.repository.BookRepository;
import slimeknights.mantle.client.gui.book.GuiBook;

@SideOnly(Side.CLIENT)
public class SectionData implements IDataItem {
   public String name;
   public ImageData icon;
   public Set<String> requirements;
   public boolean hideWhenLocked;
   public String data;
   public transient int unnamedPageCounter;
   public transient BookData parent;
   public transient BookRepository source;
   public transient ArrayList<PageData> pages;

   public SectionData() {
      this(false);
   }

   public SectionData(boolean custom) {
      this.name = null;
      this.icon = new ImageData();
      this.requirements = Sets.newHashSet();
      this.hideWhenLocked = false;
      this.data = "";
      this.unnamedPageCounter = 0;
      this.pages = new ArrayList();
      if (custom) {
         this.data = "no-load";
      }

   }

   public String translate(String string) {
      return this.parent.translate(string);
   }

   public void load() {
      if (this.name == null) {
         this.name = "section" + this.parent.unnamedSectionCounter++;
      }

      this.name = this.name.toLowerCase();
      if (!this.data.equals("no-load")) {
         IResource pagesInfo = this.source.getResource(this.source.getResourceLocation(this.data));
         if (pagesInfo != null) {
            String data = this.source.resourceToString(pagesInfo);
            if (!data.isEmpty()) {
               try {
                  this.pages = this.getPages(data);
               } catch (Exception var5) {
                  this.pages = new ArrayList();
                  PageData pdError = new PageData(true);
                  pdError.name = "errorrenous";
                  pdError.content = new ContentError("Failed to load section " + this.name + ".", var5);
                  this.pages.add(pdError);
                  var5.printStackTrace();
               }
            }
         }
      }

      for(PageData page : this.pages) {
         page.parent = this;
         page.source = this.source;
         page.load();
      }

      this.icon.location = this.source.getResourceLocation(this.icon.file, true);
   }

   protected ArrayList<PageData> getPages(String data) {
      return new ArrayList(Arrays.asList(BookLoader.GSON.fromJson(data, PageData[].class)));
   }

   public void update(@Nullable GuiBook.AdvancementCache advancementCache) {
   }

   public String getTitle() {
      String title = (String)this.parent.strings.get(this.name);
      return title == null ? this.name : title;
   }

   public int getPageCount() {
      return this.pages.size();
   }

   public boolean isUnlocked(GuiBook.AdvancementCache advancementCache) {
      if (advancementCache != null && this.requirements != null && this.requirements.size() != 0) {
         for(String achievement : this.requirements) {
            if (!requirementSatisfied(achievement, advancementCache)) {
               return false;
            }
         }

         return true;
      } else {
         return true;
      }
   }

   public static boolean requirementSatisfied(String requirement, GuiBook.AdvancementCache advancementCache) {
      if (advancementCache == null) {
         return true;
      } else {
         AdvancementProgress progress = advancementCache.getProgress(requirement);
         return progress != null && progress.isDone();
      }
   }
}
