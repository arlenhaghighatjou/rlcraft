package me.ichun.mods.ichunutil.common.module.tabula.formats;

import java.io.File;
import java.util.HashMap;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.module.tabula.formats.types.ImportTabula;
import me.ichun.mods.ichunutil.common.module.tabula.formats.types.Importer;
import me.ichun.mods.ichunutil.common.module.tabula.project.ProjectInfo;
import org.apache.commons.io.FilenameUtils;

public class ImportList {
   public static final ImportTabula tabulaImporterInstance = new ImportTabula();
   public static final HashMap<String, Importer> compatibleFormats = new HashMap<String, Importer>() {
      {
         this.put("tcn", ImportList.tabulaImporterInstance);
         this.put("tc2", ImportList.tabulaImporterInstance);
         this.put("tbl", ImportList.tabulaImporterInstance);
      }
   };

   public ImportList() {
   }

   public static boolean isFileSupported(File file) {
      return !file.isDirectory() && compatibleFormats.containsKey(FilenameUtils.getExtension(file.getName()));
   }

   public static ProjectInfo createProjectFromFile(File file) {
      if (compatibleFormats.containsKey(FilenameUtils.getExtension(file.getName()))) {
         Importer importer = (Importer)compatibleFormats.get(FilenameUtils.getExtension(file.getName()));

         try {
            ProjectInfo projectInfo = importer.createProjectInfo(file);
            projectInfo.projVersion = importer.getProjectVersion();
            projectInfo.repair();
            return projectInfo;
         } catch (Exception var3) {
            iChunUtil.LOGGER.warn("Error creating Project for format " + FilenameUtils.getExtension(file.getName()) + " for file " + file.getAbsolutePath() + " by importer " + importer);
            return null;
         }
      } else {
         return null;
      }
   }

   public static boolean registerImporter(String format, Importer importer) {
      if (compatibleFormats.containsKey(format)) {
         return false;
      } else {
         compatibleFormats.put(format, importer);
         return true;
      }
   }
}
