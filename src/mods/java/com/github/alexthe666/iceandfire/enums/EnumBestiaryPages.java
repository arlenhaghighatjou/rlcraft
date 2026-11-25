package com.github.alexthe666.iceandfire.enums;

import com.github.alexthe666.iceandfire.item.ItemBestiary;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public enum EnumBestiaryPages {
   INTRODUCTION(2),
   FIREDRAGON(4),
   FIREDRAGONEGG(1),
   ICEDRAGON(4),
   ICEDRAGONEGG(1),
   TAMEDDRAGONS(3),
   MATERIALS(2),
   ALCHEMY(0),
   VILLAGERS(0),
   HIPPOGRYPH(1),
   GORGON(1),
   PIXIE(1),
   CYCLOPS(2),
   SIREN(2),
   HIPPOCAMPUS(2),
   DEATHWORM(3),
   COCKATRICE(2),
   STYMPHALIANBIRD(1),
   TROLL(2),
   MYRMEX(4),
   AMPHITHERE(2),
   SEASERPENT(2);

   public int pages;

   private EnumBestiaryPages(int pages) {
      this.pages = pages;
   }

   public static List<Integer> toList(int[] containedpages) {
      List<Integer> intList = new ArrayList();

      for(int containedpage : containedpages) {
         intList.add(Integer.valueOf(containedpage));
      }

      return intList;
   }

   public static int[] fromList(List<Integer> containedpages) {
      int[] pages = new int[containedpages.size()];

      for(int i = 0; i < pages.length; ++i) {
         pages[i] = ((Integer)containedpages.get(i)).intValue();
      }

      return pages;
   }

   public static List<EnumBestiaryPages> containedPages(List<Integer> pages) {
      Iterator<Integer> itr = pages.iterator();
      List<EnumBestiaryPages> list = new ArrayList();

      while(itr.hasNext()) {
         list.add(values()[((Integer)itr.next()).intValue()]);
      }

      return list;
   }

   public static boolean hasAllPages(ItemStack book) {
      List<EnumBestiaryPages> allPages = new ArrayList();

      for(int i = 0; i < values().length; ++i) {
         allPages.add(values()[i]);
      }

      List<EnumBestiaryPages> pages = containedPages(toList(book.getTagCompound().getIntArray("Pages")));
      Iterator var3 = allPages.iterator();
      if (var3.hasNext()) {
         EnumBestiaryPages page = (EnumBestiaryPages)var3.next();
         return !pages.contains(page);
      } else {
         return false;
      }
   }

   public static List<Integer> enumToInt(List<EnumBestiaryPages> pages) {
      Iterator<EnumBestiaryPages> itr = pages.iterator();
      List<Integer> list = new ArrayList();

      while(itr.hasNext()) {
         list.add(Integer.valueOf(values()[((EnumBestiaryPages)itr.next()).ordinal()].ordinal()));
      }

      return list;
   }

   public static EnumBestiaryPages getRand() {
      return values()[(new Random()).nextInt(values().length)];
   }

   public static void addRandomPage(ItemStack book) {
      if (book.getItem() instanceof ItemBestiary) {
         List<EnumBestiaryPages> list = possiblePages(book);
         if (list != null && !list.isEmpty()) {
            addPage((EnumBestiaryPages)list.get((new Random()).nextInt(list.size())), book);
         }
      }

   }

   public static List<EnumBestiaryPages> possiblePages(ItemStack book) {
      if (!(book.getItem() instanceof ItemBestiary)) {
         return null;
      } else {
         NBTTagCompound tag = book.getTagCompound();
         List<EnumBestiaryPages> allPages = new ArrayList();

         for(EnumBestiaryPages page : values()) {
            allPages.add(page);
         }

         List<EnumBestiaryPages> containedPages = containedPages(toList(tag.getIntArray("Pages")));
         List<EnumBestiaryPages> possiblePages = new ArrayList();

         for(EnumBestiaryPages page : allPages) {
            if (!containedPages.contains(page)) {
               possiblePages.add(page);
            }
         }

         return possiblePages;
      }
   }

   public static void addPage(EnumBestiaryPages page, ItemStack book) {
      if (book.getItem() instanceof ItemBestiary) {
         NBTTagCompound tag = book.getTagCompound();
         List<EnumBestiaryPages> enumlist = containedPages(toList(tag.getIntArray("Pages")));
         if (!enumlist.contains(page)) {
            enumlist.add(page);
         }

         tag.setIntArray("Pages", fromList(enumToInt(enumlist)));
      }

   }
}
