package net.silentchaos512.lib.guidebook;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.guidebook.entry.GuideEntry;
import net.silentchaos512.lib.guidebook.entry.GuideEntryAllItems;
import net.silentchaos512.lib.guidebook.gui.GuiGuide;
import net.silentchaos512.lib.util.I18nHelper;

public abstract class GuideBook {
   protected final String modId;
   protected final List<IGuideEntry> entries = Lists.newArrayList();
   protected final List<IGuideChapter> chapters = Lists.newArrayList();
   protected final List<IGuidePage> pagesWithItemOrFluidData = Lists.newArrayList();
   protected ResourceLocation resourceGui;
   protected ResourceLocation resourceGadgets;
   public GuideEntry entryAllAndSearch = (new GuideEntryAllItems(this, "allAndSearch")).setImportant();
   public final I18nHelper i18n;
   public int edition = -1;
   public boolean showPatreonButton = false;
   @SideOnly(Side.CLIENT)
   public GuiGuide lastViewedPage;

   public GuideBook(String modId, I18nHelper i18n) {
      this.modId = modId;
      this.i18n = i18n;
      this.showPatreonButton = modId.equals("silentgems");
      this.resourceGui = new ResourceLocation("silentlib", "textures/gui/gui_guide.png");
      this.resourceGadgets = new ResourceLocation("silentlib", "textures/gui/gui_guide_gadgets.png");
   }

   public final void preInit() {
      this.initEntries();
   }

   public final void postInit() {
      this.initChapters();
      int countChapter = 0;
      int countPage = 0;
      int countInfo = 0;

      for(IGuideEntry entry : this.entries) {
         for(IGuideChapter chapter : entry.getAllChapters()) {
            if (!this.chapters.contains(chapter)) {
               this.chapters.add(chapter);
               ++countChapter;

               for(IGuidePage page : chapter.getAllPages()) {
                  ++countPage;
                  List<ItemStack> items = new ArrayList();
                  page.getItemStacksForPage(items);
                  List<FluidStack> fluids = new ArrayList();
                  page.getFluidStacksForPage(fluids);
                  if ((!items.isEmpty() || !fluids.isEmpty()) && !this.pagesWithItemOrFluidData.contains(page)) {
                     this.pagesWithItemOrFluidData.add(page);
                     ++countInfo;
                  }
               }
            }
         }
      }

      this.entries.sort(Comparator.comparing(IGuideEntry::getSortingPriority, Comparator.reverseOrder()));
      this.chapters.sort(Comparator.comparing(IGuideChapter::getSortingPriority, Comparator.reverseOrder()));
      this.pagesWithItemOrFluidData.sort(Comparator.comparing(IGuidePage::getSortingPriority, Comparator.reverseOrder()));
      SilentLib.LOGGER.info("Guide book for mod \"{}\" initialized!", this.modId);
      SilentLib.LOGGER.info("    Entries:  {}", Integer.valueOf(this.entries.size()));
      SilentLib.LOGGER.info("    Chapters: {}", Integer.valueOf(countChapter));
      SilentLib.LOGGER.info("    Pages:    {}", Integer.valueOf(countPage));
      SilentLib.LOGGER.info("    Info:     {}", Integer.valueOf(countInfo));
   }

   public abstract void initEntries();

   public abstract void initChapters();

   public abstract String[] getQuotes();

   @Nonnull
   public String selectQuote(Random rand) {
      String[] quotes = this.getQuotes();
      return quotes.length > 0 ? quotes[rand.nextInt(quotes.length)] : "";
   }

   @SideOnly(Side.CLIENT)
   @Nullable
   public abstract GuiScreen getConfigScreen(GuiScreen var1);

   @SideOnly(Side.CLIENT)
   @Nullable
   public abstract GuiScreen getAchievementScreen(GuiScreen var1);

   public ResourceLocation getResourceGui() {
      return this.resourceGui;
   }

   public ResourceLocation getResourceGadgets() {
      return this.resourceGadgets;
   }

   public String getEditionString(EntityPlayer player) {
      String str = this.getEditionNumberString();
      return this.i18n.translate("guide.silentlib.edition", str);
   }

   protected String getEditionNumberString() {
      if (this.edition >= 11 && this.edition <= 13) {
         return this.edition + "th";
      } else {
         int mod10 = this.edition % 10;
         String str = this.edition + (mod10 == 1 ? "st" : (mod10 == 2 ? "nd" : (mod10 == 3 ? "rd" : "th")));
         return str;
      }
   }

   public String getModId() {
      return this.modId;
   }

   public List<IGuideChapter> getChapters() {
      return this.chapters;
   }

   public List<IGuideEntry> getEntries() {
      return this.entries;
   }

   public List<IGuidePage> getPagesWithItemOrFluidData() {
      return this.pagesWithItemOrFluidData;
   }

   public void addEntry(IGuideEntry entry) {
      this.entries.add(entry);
   }
}
