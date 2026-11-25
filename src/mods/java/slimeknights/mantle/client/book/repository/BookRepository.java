package slimeknights.mantle.client.book.repository;

import java.util.List;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.client.book.data.SectionData;

public abstract class BookRepository {
   public static final BookRepository DUMMY = new DummyRepository();

   public BookRepository() {
   }

   public abstract List<SectionData> getSections();

   public ResourceLocation getResourceLocation(String path) {
      return this.getResourceLocation(path, false);
   }

   public abstract ResourceLocation getResourceLocation(String var1, boolean var2);

   public abstract IResource getResource(ResourceLocation var1);

   public boolean resourceExists(String location) {
      return this.resourceExists(new ResourceLocation(location));
   }

   public abstract boolean resourceExists(ResourceLocation var1);

   public String resourceToString(IResource resource) {
      return this.resourceToString(resource, true);
   }

   public abstract String resourceToString(IResource var1, boolean var2);
}
