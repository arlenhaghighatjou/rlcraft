package stanhebben.zenscript;

import java.util.StringJoiner;
import stanhebben.zenscript.impl.GenericCompileEnvironment;
import stanhebben.zenscript.impl.GenericErrorLogger;
import stanhebben.zenscript.impl.GenericRegistry;

public class ZenMain {
   public ZenMain() {
   }

   public static void println(String s) {
      System.out.println(s);
   }

   public static void main(String[] args) {
      IZenCompileEnvironment environment = new GenericCompileEnvironment();
      IZenErrorLogger errorLogger = new GenericErrorLogger(System.err);
      GenericRegistry genericRegistry = new GenericRegistry(environment, errorLogger);
      genericRegistry.registerGlobal("println", genericRegistry.getStaticFunction(ZenMain.class, "println", String.class));

      try {
         StringJoiner builder = new StringJoiner("\n");
         builder.add("var myTestFun as function(string)void = function(s as string) as void {println(s);};");
         builder.add("myTestFun('Hello World');");
         builder.add("function myAcceptFun(consumer as function(string)void) as void {");
         builder.add("   consumer('hello from inside myAcceptFun');");
         builder.add("}");
         builder.add("myAcceptFun(myTestFun);");
         builder.add("myAcceptFun(function(s as string) as void {println('Another thing: ' ~ s);});");
         String script = builder.toString();
         ZenModule module = ZenModule.compileScriptString(script, "test.zs", environment, ZenMain.class.getClassLoader());
         Runnable runnable = module.getMain();
         if (runnable != null) {
            runnable.run();
         }
      } catch (Throwable var8) {
         genericRegistry.getErrorLogger().error("Error executing: test.zs: " + var8.getMessage(), var8);
      }

   }
}
