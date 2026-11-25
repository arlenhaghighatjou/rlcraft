package com.teamacronymcoders.contenttweaker.api.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class CreativeTabsResourceList extends ResourceList<CreativeTabs> {
   public CreativeTabsResourceList() {
      super(CreativeTabs.class, new ArrayList());
   }

   public void addResource(String name, CreativeTabs resource) {
   }

   public CreativeTabs getResource(String name) {
      CreativeTabs resourceCreativeTab = null;
      CreativeTabs[] creativeTabs = CreativeTabs.CREATIVE_TAB_ARRAY;
      Iterator<CreativeTabs> creativeTabsIterable = Arrays.stream(creativeTabs).iterator();

      while(creativeTabsIterable.hasNext() && resourceCreativeTab == null) {
         CreativeTabs creativeTab = (CreativeTabs)creativeTabsIterable.next();
         String tabLabel = (String)ReflectionHelper.getPrivateValue(CreativeTabs.class, creativeTab, new String[]{"tabLabel", "tabLabel"});
         if (name.equalsIgnoreCase(tabLabel)) {
            resourceCreativeTab = creativeTab;
         }
      }

      return resourceCreativeTab;
   }

   public Collection<CreativeTabs> getAllResources() {
      return Arrays.asList(CreativeTabs.CREATIVE_TAB_ARRAY);
   }

   public List<String> getAllNames() {
      return (List)Arrays.stream(CreativeTabs.CREATIVE_TAB_ARRAY).map((creativeTab) -> {
         return ReflectionHelper.getPrivateValue(CreativeTabs.class, creativeTab, new String[]{"tabLabel", "tabLabel"}).toString();
      }).collect(Collectors.toList());
   }
}
