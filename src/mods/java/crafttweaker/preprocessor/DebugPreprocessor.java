package crafttweaker.preprocessor;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.runtime.ScriptFile;

public class DebugPreprocessor extends PreprocessorActionBase {
   public static final String PREPROCESSOR_NAME = "debug";

   public DebugPreprocessor(String fileName, String preprocessorLine, int lineIndex) {
      super(fileName, preprocessorLine, lineIndex);
   }

   public void executeActionOnFind(ScriptFile scriptFile) {
      CraftTweakerAPI.logInfo("Debug Preprocessor found in " + scriptFile + ", enabling debugmode");
      scriptFile.setDebugEnabled(true);
   }

   public String getPreprocessorName() {
      return "debug";
   }
}
