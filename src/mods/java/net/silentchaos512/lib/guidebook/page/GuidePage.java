package net.silentchaos512.lib.guidebook.page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.lib.guidebook.GuideBook;
import net.silentchaos512.lib.guidebook.IGuideChapter;
import net.silentchaos512.lib.guidebook.IGuidePage;
import net.silentchaos512.lib.guidebook.internal.GuiGuideBase;

public class GuidePage implements IGuidePage {
   protected final HashMap<String, String> textReplacements;
   protected final GuideBook book;
   protected final int key;
   private final int priority;
   private final List<ItemStack> itemsForPage;
   private final List<FluidStack> fluidsForPage;
   protected IGuideChapter chapter;
   protected boolean hasNoText;

   public GuidePage(GuideBook book, int key) {
      this(book, key, 0);
   }

   public GuidePage(GuideBook book, int key, int priority) {
      this.textReplacements = new HashMap();
      this.itemsForPage = new ArrayList();
      this.fluidsForPage = new ArrayList();
      this.book = book;
      this.key = key;
      this.priority = priority;
   }

   public void getItemStacksForPage(List<ItemStack> list) {
      list.addAll(this.itemsForPage);
   }

   public void getFluidStacksForPage(List<FluidStack> list) {
      list.addAll(this.fluidsForPage);
   }

   public IGuideChapter getChapter() {
      return this.chapter;
   }

   public void setChapter(IGuideChapter chapter) {
      this.chapter = chapter;
   }

   @SideOnly(Side.CLIENT)
   public String getInfoText() {
      if (this.hasNoText) {
         return null;
      } else {
         String base = this.book.i18n.translate(this.getLocalizationKey());
         return this.doTextReplacements(base);
      }
   }

   protected String doTextReplacements(String base) {
      base = base.replaceAll("<vimp>", TextFormatting.DARK_RED + "" + TextFormatting.UNDERLINE);
      base = base.replaceAll("<imp>", TextFormatting.DARK_GREEN + "");
      base = base.replaceAll("<item>", TextFormatting.BLUE + "");
      base = base.replaceAll("<r>", TextFormatting.BLACK + "");
      base = base.replaceAll("<n>", "\n");
      base = base.replaceAll("<i>", TextFormatting.ITALIC + "");

      for(Entry<String, String> entry : this.textReplacements.entrySet()) {
         base = base.replaceAll((String)entry.getKey(), (String)entry.getValue());
      }

      return base;
   }

   @SideOnly(Side.CLIENT)
   protected String getLocalizationKey() {
      return "guide." + this.book.getModId() + ".chapter." + this.chapter.getIdentifier() + ".text" + this.key;
   }

   @SideOnly(Side.CLIENT)
   public void mouseClicked(GuiGuideBase gui, int mouseX, int mouseY, int mouseButton) {
   }

   @SideOnly(Side.CLIENT)
   public void mouseReleased(GuiGuideBase gui, int mouseX, int mouseY, int state) {
   }

   @SideOnly(Side.CLIENT)
   public void mouseClickMove(GuiGuideBase gui, int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
   }

   @SideOnly(Side.CLIENT)
   public void actionPerformed(GuiGuideBase gui, GuiButton button) {
   }

   @SideOnly(Side.CLIENT)
   public void initGui(GuiGuideBase gui, int startX, int startY) {
   }

   @SideOnly(Side.CLIENT)
   public void updateScreen(GuiGuideBase gui, int startX, int startY, int pageTimer) {
   }

   @SideOnly(Side.CLIENT)
   public void drawScreenPre(GuiGuideBase gui, int startX, int startY, int mouseX, int mouseY, float partialTicks) {
   }

   @SideOnly(Side.CLIENT)
   public void drawScreenPost(GuiGuideBase gui, int startX, int startY, int mouseX, int mouseY, float partialTicks) {
   }

   public boolean shouldBeOnLeftSide() {
      return (this.chapter.getPageIndex(this) + 1) % 2 != 0;
   }

   public String getIdentifier() {
      return this.chapter.getIdentifier() + "." + this.chapter.getPageIndex(this);
   }

   public String getWebLink() {
      return "";
   }

   public GuidePage setNoText() {
      this.hasNoText = true;
      return this;
   }

   public GuidePage addFluidToPage(Fluid fluid) {
      this.fluidsForPage.add(new FluidStack(fluid, 1));
      return this;
   }

   public GuidePage addItemsToPage(Block... blocks) {
      for(Block block : blocks) {
         this.addItemsToPage(new ItemStack(block));
      }

      return this;
   }

   public GuidePage addItemsToPage(ItemStack... stacks) {
      Collections.addAll(this.itemsForPage, stacks);
      return this;
   }

   public GuidePage addTextReplacement(String key, String value) {
      this.textReplacements.put(key, value);
      return this;
   }

   public GuidePage addTextReplacement(String key, float value) {
      return this.addTextReplacement(key, Float.toString(value));
   }

   public GuidePage addTextReplacement(String key, int value) {
      return this.addTextReplacement(key, Integer.toString(value));
   }

   public int getSortingPriority() {
      return this.priority;
   }
}
