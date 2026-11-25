package stanhebben.zenscript.compiler;

public class ClassNameGenerator {
   private String prefix;
   private int counter;

   public ClassNameGenerator() {
      this("ZenClass");
   }

   public ClassNameGenerator(String prefix) {
      this.counter = 0;
      this.prefix = prefix;
   }

   public String getPrefix() {
      return this.prefix;
   }

   public void setPrefix(String prefix) {
      this.prefix = prefix;
   }

   public String generate() {
      return this.prefix + this.counter++;
   }

   public String generate(String customPrefix) {
      return customPrefix + this.counter++;
   }

   public String generateWithMiddleName(String customMiddleName) {
      return this.prefix + customMiddleName + this.counter++;
   }
}
