package crafttweaker.preprocessor;

import crafttweaker.runtime.ScriptFile;

public abstract class PreprocessorActionBase implements IPreprocessor {
   protected String preprocessorLine;
   protected String fileName;
   protected int lineIndex;

   public PreprocessorActionBase(String fileName, String preprocessorLine, int lineIndex) {
      this.fileName = fileName;
      this.preprocessorLine = preprocessorLine;
      this.lineIndex = lineIndex;
   }

   public void executeActionOnFind(ScriptFile scriptFile) {
   }

   public void executeActionOnFinish(ScriptFile scriptFile) {
   }

   public String getPreprocessorLine() {
      return this.preprocessorLine;
   }

   public String getFileName() {
      return this.fileName;
   }

   public int getLineIndex() {
      return this.lineIndex;
   }
}
