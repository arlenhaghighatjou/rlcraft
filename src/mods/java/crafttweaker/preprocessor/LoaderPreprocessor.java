package crafttweaker.preprocessor;

import crafttweaker.runtime.ScriptFile;
import java.util.HashSet;
import java.util.Set;

public class LoaderPreprocessor extends PreprocessorActionBase {
   private static final String PREPROCESSOR_NAME = "loader";
   private String[] loaderNames;

   public LoaderPreprocessor(String fileName, String preprocessorLine, int lineIndex) {
      super(fileName, preprocessorLine, lineIndex);
      String substring = preprocessorLine.substring(7).trim();
      String[] arr = substring.split("\\s+");
      Set<String> set = new HashSet();

      for(String s : arr) {
         String trim = s.trim();
         if (!trim.isEmpty()) {
            set.add(trim);
         }
      }

      this.loaderNames = (String[])set.toArray(new String[set.size()]);
   }

   public void executeActionOnFind(ScriptFile scriptFile) {
      scriptFile.setLoaderNames(this.loaderNames);
   }

   public String getPreprocessorName() {
      return "loader";
   }
}
