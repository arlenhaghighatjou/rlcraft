package crafttweaker.preprocessor;

import crafttweaker.runtime.ScriptFile;

public interface IPreprocessor {
   String getPreprocessorName();

   void executeActionOnFind(ScriptFile var1);

   void executeActionOnFinish(ScriptFile var1);

   String getPreprocessorLine();

   String getFileName();

   int getLineIndex();
}
