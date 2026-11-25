package crafttweaker.preprocessor;

import crafttweaker.runtime.ScriptFile;

public class NoRunPreprocessor extends PreprocessorActionBase {
   private static final String PREPROCESSOR_NAME = "norun";

   public NoRunPreprocessor(String fileName, String preprocessorLine, int lineIndex) {
      super(fileName, preprocessorLine, lineIndex);
   }

   public void executeActionOnFind(ScriptFile scriptFile) {
      scriptFile.setParsingBlocked(true);
      scriptFile.setCompileBlocked(true);
      scriptFile.setExecutionBlocked(true);
   }

   public String getPreprocessorName() {
      return "norun";
   }
}
