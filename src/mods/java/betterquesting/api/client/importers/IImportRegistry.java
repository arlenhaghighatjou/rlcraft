package betterquesting.api.client.importers;

import java.util.List;

public interface IImportRegistry {
   void registerImporter(IImporter var1);

   List<IImporter> getImporters();
}
