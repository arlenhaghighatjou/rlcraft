package crafttweaker.socket;

public class SingleError {
   public String fileName;
   public int line;
   public int offset;
   public String explanation;
   public SingleError.Level level;

   public SingleError(String fileName, int line, int offset, String explanation, SingleError.Level level) {
      this.fileName = fileName;
      this.line = line;
      this.offset = offset;
      this.explanation = explanation == null ? "NO MESSAGE PROVIDED" : explanation;
      this.level = level;
   }

   public String toString() {
      return "SingleError{fileName='" + this.fileName + '\'' + ", line=" + this.line + ", offset=" + this.offset + ", explanation='" + this.explanation + '\'' + ", level=" + this.level + '}';
   }

   public static enum Level {
      INFO,
      WARN,
      ERROR;

      private Level() {
      }
   }
}
