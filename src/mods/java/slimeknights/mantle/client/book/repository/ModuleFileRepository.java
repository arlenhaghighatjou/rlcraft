package slimeknights.mantle.client.book.repository;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import slimeknights.mantle.client.book.BookLoader;
import slimeknights.mantle.client.book.data.SectionData;
import slimeknights.mantle.client.book.data.SectionDataModule;
import slimeknights.mantle.pulsar.control.PulseManager;

public class ModuleFileRepository extends FileRepository {
   private Predicate<String> manager;

   public ModuleFileRepository(PulseManager manager, String location) {
      super(location);
      this.manager = manager::isPulseLoaded;
   }

   public ModuleFileRepository(String location) {
      super(location);
      this.manager = PulseManager::isPulseLoadedGlobal;
   }

   public List<SectionData> getSections() {
      return (List)Arrays.stream(BookLoader.GSON.fromJson(this.resourceToString(this.getResource(this.getResourceLocation("index.json"))), SectionDataModule[].class)).filter((section) -> {
         return section.module.isEmpty() || this.manager.test(section.module);
      }).collect(Collectors.toList());
   }

   public Predicate<String> getManager() {
      return this.manager;
   }
}
