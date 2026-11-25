package slimeknights.mantle.client.book.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import slimeknights.mantle.client.book.BookLoader;
import slimeknights.mantle.client.book.repository.ModuleFileRepository;

public class SectionDataModule extends SectionData {
   public String module = "";

   public SectionDataModule() {
   }

   protected ArrayList<PageData> getPages(String data) {
      if (this.source instanceof ModuleFileRepository) {
         Predicate<String> manager = ((ModuleFileRepository)this.source).getManager();
         return new ArrayList((Collection)Arrays.stream(BookLoader.GSON.fromJson(data, PageDataModule[].class)).filter((page) -> {
            return page.module.isEmpty() || manager.test(page.module);
         }).collect(Collectors.toList()));
      } else {
         return super.getPages(data);
      }
   }
}
