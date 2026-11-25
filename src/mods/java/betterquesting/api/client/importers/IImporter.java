package betterquesting.api.client.importers;

import betterquesting.api.questing.IQuestDatabase;
import betterquesting.api.questing.IQuestLineDatabase;
import java.io.File;
import java.io.FileFilter;

public interface IImporter {
   String getUnlocalisedName();

   String getUnlocalisedDescription();

   FileFilter getFileFilter();

   void loadFiles(IQuestDatabase var1, IQuestLineDatabase var2, File[] var3);
}
