package com.alcatrazescapee.notreepunching.integration.guide.utils;

import amerifrance.guideapi.api.IPage;
import amerifrance.guideapi.api.impl.Entry;
import amerifrance.guideapi.api.impl.abstraction.CategoryAbstract;
import amerifrance.guideapi.api.impl.abstraction.EntryAbstract;
import amerifrance.guideapi.page.PageText;
import net.minecraft.util.ResourceLocation;

public class CategoryBuilder {
   private CategoryAbstract rootCategory;
   private EntryAbstract lastEntry;

   public CategoryBuilder(CategoryAbstract rootCategory) {
      this.rootCategory = rootCategory;
   }

   public CategoryBuilder addEntry(String name) {
      this.lastEntry = new Entry(name);
      this.rootCategory.addEntry(this.lastEntry.name, this.lastEntry);
      return this;
   }

   public CategoryBuilder addPage(IPage page) {
      this.lastEntry.addPage(page);
      return this;
   }

   public CategoryBuilder addPage(ResourceLocation recipeLookup) {
      this.lastEntry.addPage(new PageIRecipeLazy(recipeLookup));
      return this;
   }

   public CategoryBuilder addPage(String text) {
      this.lastEntry.addPage(new PageText(text));
      return this;
   }

   public CategoryAbstract build() {
      return this.rootCategory;
   }
}
