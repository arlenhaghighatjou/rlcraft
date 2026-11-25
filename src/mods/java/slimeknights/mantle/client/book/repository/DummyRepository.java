package slimeknights.mantle.client.book.repository;

import java.util.List;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.client.book.data.SectionData;

public class DummyRepository extends BookRepository {
   public DummyRepository() {
   }

   public List<SectionData> getSections() {
      return null;
   }

   public ResourceLocation getResourceLocation(String path, boolean safe) {
      return null;
   }

   public IResource getResource(ResourceLocation loc) {
      return null;
   }

   public boolean resourceExists(ResourceLocation location) {
      return false;
   }

   public String resourceToString(IResource resource, boolean skipCommments) {
      return null;
   }
}
