package crafttweaker.preprocessor;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.runtime.ScriptFile;
import crafttweaker.util.SuppressErrorFlag;

public class IKWIDPreprocessor extends PreprocessorActionBase {
   private static final String PREPROCESSOR_NAME = "ikwid";

   public IKWIDPreprocessor(String fileName, String preprocessorLine, int lineIndex) {
      super(fileName, preprocessorLine, lineIndex);
   }

   public void executeActionOnFind(ScriptFile scriptFile) {
      CraftTweakerAPI.logInfo("ikwid preprocessor is found in " + scriptFile.getEffectiveName() + ", warnings and errors aren't printed to the players' chat.");
      CraftTweakerAPI.setSuppressErrorFlag(SuppressErrorFlag.ALL);
   }

   public String getPreprocessorName() {
      return "ikwid";
   }
}
