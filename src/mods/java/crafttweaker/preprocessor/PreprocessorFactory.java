package crafttweaker.preprocessor;

@FunctionalInterface
public interface PreprocessorFactory<R extends IPreprocessor> {
   R createPreprocessor(String var1, String var2, int var3);
}
