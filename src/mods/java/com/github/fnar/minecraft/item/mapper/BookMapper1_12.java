package com.github.fnar.minecraft.item.mapper;

import greymerk.roguelike.treasure.loot.Book;
import java.util.Optional;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.ITextComponent.Serializer;

public class BookMapper1_12 extends BaseItemMapper1_12<Book> {
   public BookMapper1_12() {
   }

   public Class<Book> getClazz() {
      return Book.class;
   }

   public ItemStack map(Book book) {
      ItemStack itemStack = new ItemStack(Items.WRITTEN_BOOK, 1);
      NBTTagList nbtPages = new NBTTagList();

      for(String page : book.getPages()) {
         ITextComponent text = new TextComponentString(page);
         String json = Serializer.componentToJson(text);
         NBTTagString nbtPage = new NBTTagString(json);
         nbtPages.appendTag(nbtPage);
      }

      itemStack.setTagInfo("pages", nbtPages);
      itemStack.setTagInfo("author", new NBTTagString((String)Optional.ofNullable(book.getAuthor()).orElse("Anonymous")));
      itemStack.setTagInfo("title", new NBTTagString((String)Optional.ofNullable(book.getTitle()).orElse("Book")));
      return itemStack;
   }
}
