package ivorius.reccomplex.random.item;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonPrimitive;
import ivorius.ivtoolkit.tools.NBTTagLists;
import ivorius.reccomplex.random.Person;
import ivorius.reccomplex.random.Poem;
import java.util.Arrays;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import org.apache.commons.lang3.text.WordUtils;

public class Book {
   public static final int LINES_PER_PAGE = 12;
   public static final int CHARS_PER_LINE = 18;

   public Book() {
   }

   public static ItemStack any(EaglercraftRandom random) {
      return random.nextFloat() < 0.5F ? generic(random) : poem(random);
   }

   public static ItemStack generic(EaglercraftRandom random) {
      ItemStack stack = new ItemStack(Items.BOOK);
      String bookName = Person.chaoticName(random, random.nextFloat() < 0.8F);
      stack.setStackDisplayName(bookName);
      return stack;
   }

   public static ItemStack poem(EaglercraftRandom random) {
      ItemStack stack = new ItemStack(Items.WRITTEN_BOOK);
      Person author = Person.randomHuman(random, random.nextFloat() < 0.9F);
      Poem poem = Poem.randomPoem(random, Integer.valueOf(32), author);
      stack.setTagInfo("pages", NBTTagLists.write((List)bookPages(poem.getText()).stream().map(Book::toJSON).map(NBTTagString::new).collect(Collectors.toList())));
      stack.setTagInfo("author", new NBTTagString(author.getFullName()));
      stack.setTagInfo("title", new NBTTagString(poem.getTitle()));
      return stack;
   }

   public static String toJSON(String text) {
      return (new Gson()).toJson(new JsonPrimitive(text));
   }

   public static List<String> bookPages(String text) {
      List<String> lines = (List)Arrays.stream(text.split("\n")).map((line) -> {
         return line + "\n";
      }).flatMap((line) -> {
         return Arrays.stream(WordUtils.wrap(line, 18, "\r", true).replaceAll("\r", " \r").split("\r"));
      }).collect(Collectors.toList());
      return (List)Lists.partition(lines, 12).stream().map((pageLines) -> {
         return (String)pageLines.stream().reduce("", (l, r) -> {
            return l + r;
         });
      }).collect(Collectors.toList());
   }
}
