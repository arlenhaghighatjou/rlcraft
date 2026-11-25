package crafttweaker.preprocessor;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.runtime.ScriptFile;

public class ProfilePreprocessor extends PreprocessorActionBase {
   private static final String PREPROCESSOR_NAME = "profile";

   public ProfilePreprocessor(String fileName, String preprocessorLine, int lineIndex) {
      super(fileName, preprocessorLine, lineIndex);
   }

   public void executeActionOnFind(ScriptFile scriptFile) {
      CraftTweakerAPI.profile = true;
   }

   public String getPreprocessorName() {
      return "profile";
   }
}
