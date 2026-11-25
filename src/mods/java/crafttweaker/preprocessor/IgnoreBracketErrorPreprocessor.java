package crafttweaker.preprocessor;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.runtime.ScriptFile;

public class IgnoreBracketErrorPreprocessor extends PreprocessorActionBase {
   public static final String PREPROCESSOR_NAME = "ignoreBracketError";

   public IgnoreBracketErrorPreprocessor(String fileName, String preprocessorLine, int lineIndex) {
      super(fileName, preprocessorLine, lineIndex);
   }

   public void executeActionOnFind(ScriptFile scriptFile) {
      CraftTweakerAPI.logInfo("IgnoreBracketErrorPreprocessor found in " + scriptFile + ", ignoring errors in that file");
      scriptFile.setIgnoreBracketErrors(true);
   }

   public String getPreprocessorName() {
      return "ignoreBracketError";
   }
}
