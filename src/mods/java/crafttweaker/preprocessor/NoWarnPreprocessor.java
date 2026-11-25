package crafttweaker.preprocessor;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.runtime.ScriptFile;
import crafttweaker.util.SuppressErrorFlag;

public class NoWarnPreprocessor extends PreprocessorActionBase {
   public static final String PREPROCESSOR_NAME = "nowarn";

   public NoWarnPreprocessor(String fileName, String preprocessorLine, int lineIndex) {
      super(fileName, preprocessorLine, lineIndex);
   }

   public void executeActionOnFind(ScriptFile scriptFile) {
      CraftTweakerAPI.logInfo("nowarn preprocessor is found in " + scriptFile.getEffectiveName() + ", warnings aren't printed to the players' chat.");
      CraftTweakerAPI.setSuppressErrorFlag(SuppressErrorFlag.ONLY_WARNINGS);
   }

   public String getPreprocessorName() {
      return "nowarn";
   }
}
