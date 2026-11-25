package stanhebben.zenscript;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import stanhebben.zenscript.compiler.ClassNameGenerator;
import stanhebben.zenscript.compiler.EnvironmentClass;
import stanhebben.zenscript.compiler.EnvironmentGlobal;
import stanhebben.zenscript.compiler.EnvironmentMethod;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.compiler.ZenClassWriter;
import stanhebben.zenscript.definitions.ParsedFunction;
import stanhebben.zenscript.definitions.ParsedFunctionArgument;
import stanhebben.zenscript.definitions.ParsedGlobalValue;
import stanhebben.zenscript.definitions.zenclasses.ParsedZenClass;
import stanhebben.zenscript.expression.partial.PartialScriptReference;
import stanhebben.zenscript.statements.Statement;
import stanhebben.zenscript.statements.StatementReturn;
import stanhebben.zenscript.symbols.SymbolArgument;
import stanhebben.zenscript.symbols.SymbolGlobalValue;
import stanhebben.zenscript.symbols.SymbolScriptReference;
import stanhebben.zenscript.symbols.SymbolZenClass;
import stanhebben.zenscript.symbols.SymbolZenStaticMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.StringUtil;
import stanhebben.zenscript.util.ZenTypeUtil;

public class ZenModule {
   public static final Map<String, byte[]> classes = new HashMap();
   public static final Map<String, Class> loadedClasses = new HashMap();
   private final ZenModule.MyClassLoader classLoader;

   public ZenModule(Map<String, byte[]> clazzes, ClassLoader baseClassLoader) {
      classes.putAll(clazzes);
      this.classLoader = new ZenModule.MyClassLoader(baseClassLoader);
   }

   public static void compileScripts(String mainFileName, List<ZenParsedFile> scripts, IEnvironmentGlobal environmentGlobal, boolean debug) {
      ClassWriter clsMain = new ZenClassWriter(2);
      clsMain.visitSource(mainFileName, (String)null);
      clsMain.visit(52, 1, "__ZenMain__", (String)null, ZenTypeUtil.internal(Object.class), new String[]{ZenTypeUtil.internal(Runnable.class)});
      MethodOutput mainRun = new MethodOutput(clsMain, 1, "run", "()V", (String)null, (String[])null);
      mainRun.start();

      for(ZenParsedFile script : scripts) {
         ClassWriter clsScript = new ZenClassWriter(2);
         clsScript.visitSource(script.getFileName(), (String)null);
         EnvironmentClass environmentScript = new EnvironmentClass(clsScript, script.getEnvironment());
         clsScript.visit(52, 1, script.getClassName().replace('.', '/'), (String)null, ZenTypeUtil.internal(Object.class), new String[]{ZenTypeUtil.internal(Runnable.class)});
         if (!script.getClasses().isEmpty()) {
            for(Entry<String, ParsedZenClass> entry : script.getClasses().entrySet()) {
               environmentScript.putValue((String)entry.getKey(), new SymbolZenClass(((ParsedZenClass)entry.getValue()).type), ((ParsedZenClass)entry.getValue()).position);
            }
         }

         if (!script.getGlobals().isEmpty()) {
            MethodOutput clinit = new MethodOutput(clsScript, 8, "<clinit>", "()V", (String)null, (String[])null);
            EnvironmentMethod clinitEnvironment = new EnvironmentMethod(clinit, environmentScript);
            clinit.start();

            for(Entry<String, ParsedGlobalValue> entry : script.getGlobals().entrySet()) {
               ParsedGlobalValue value = (ParsedGlobalValue)entry.getValue();
               if (value.isGlobal()) {
                  environmentGlobal.putValue((String)entry.getKey(), new SymbolGlobalValue(value, clinitEnvironment), value.getPosition());
               } else {
                  environmentScript.putValue((String)entry.getKey(), new SymbolGlobalValue(value, clinitEnvironment), value.getPosition());
               }
            }

            clinit.ret();
            clinit.end();
         }

         if (!script.getFunctions().isEmpty() || !script.getGlobals().isEmpty() || !script.getClasses().isEmpty()) {
            String fileName = script.getFileName();
            if (fileName.startsWith("scripts.zip" + File.separator)) {
               fileName = fileName.substring(12);
            }

            String[] splitName = fileName.replaceAll("\\.zip", "").split("\\.|\\" + File.separator);
            PartialScriptReference reference = SymbolScriptReference.getOrCreateReference(environmentGlobal);
            if (splitName.length != 0) {
               reference.addScriptOrDirectory(environmentScript, (String[])Arrays.copyOfRange(splitName, 0, splitName.length - 1));
            }
         }

         for(Entry<String, ParsedFunction> function : script.getFunctions().entrySet()) {
            ParsedFunction fn = (ParsedFunction)function.getValue();
            environmentScript.putValue((String)function.getKey(), new SymbolZenStaticMethod(script.getClassName(), fn.getName(), fn.getSignature(), fn.getArguments(), fn.getReturnType()), fn.getPosition());
         }

         for(Entry<String, ParsedFunction> function : script.getFunctions().entrySet()) {
            ParsedFunction fn = (ParsedFunction)function.getValue();
            String signature = fn.getSignature();
            MethodOutput methodOutput = new MethodOutput(clsScript, 9, (String)function.getKey(), signature, (String)null, (String[])null);
            EnvironmentMethod methodEnvironment = new EnvironmentMethod(methodOutput, environmentScript);
            List<ParsedFunctionArgument> arguments = ((ParsedFunction)function.getValue()).getArguments();
            int i = 0;

            for(int j = 0; i < arguments.size(); ++i) {
               ParsedFunctionArgument argument = (ParsedFunctionArgument)arguments.get(i);
               methodEnvironment.putValue(argument.getName(), new SymbolArgument(i + j, argument.getType()), fn.getPosition());
               if (argument.getType().isLarge()) {
                  ++j;
               }
            }

            methodOutput.start();
            Statement[] statements = fn.getStatements();

            for(Statement statement : statements) {
               statement.compile(methodEnvironment);
            }

            if (((ParsedFunction)function.getValue()).getReturnType() != ZenType.VOID) {
               if (statements.length > 0 && statements[statements.length - 1] instanceof StatementReturn) {
                  if (((StatementReturn)statements[statements.length - 1]).getExpression() != null) {
                     fn.getReturnType().defaultValue(fn.getPosition()).compile(true, methodEnvironment);
                     methodOutput.returnType(fn.getReturnType().toASMType());
                  }
               } else {
                  fn.getReturnType().defaultValue(fn.getPosition()).compile(true, methodEnvironment);
                  methodOutput.returnType(fn.getReturnType().toASMType());
               }
            } else if (statements.length == 0 || !(statements[statements.length - 1] instanceof StatementReturn)) {
               methodOutput.ret();
            }

            methodOutput.end();
         }

         if (script.getStatements().size() > 0) {
            MethodOutput scriptOutput = new MethodOutput(clsScript, 9, "__script__", "()V", (String)null, (String[])null);
            IEnvironmentMethod functionMethod = new EnvironmentMethod(scriptOutput, environmentScript);
            scriptOutput.start();

            for(Statement statement : script.getStatements()) {
               statement.compile(functionMethod);
            }

            scriptOutput.ret();
            scriptOutput.end();
            mainRun.invokeStatic(script.getClassName().replace('.', '/'), "__script__", "()V");
         }

         clsScript.visitEnd();
         environmentGlobal.putClass(script.getClassName(), clsScript.toByteArray());
      }

      mainRun.ret();
      mainRun.end();
      clsMain.visitEnd();
      MethodVisitor constructor = clsMain.visitMethod(1, "<init>", "()V", (String)null, (String[])null);
      constructor.visitCode();
      constructor.visitVarInsn(25, 0);
      constructor.visitMethodInsn(183, "java/lang/Object", "<init>", "()V", false);
      constructor.visitInsn(177);
      constructor.visitMaxs(0, 0);
      constructor.visitEnd();
      if (debug) {
         try {
            File outputDir = new File("generated");
            outputDir.mkdir();

            for(String className : environmentGlobal.getClassNames()) {
               File outputFile = new File(outputDir, className.replace('.', '/') + ".class");
               if (!outputFile.getParentFile().exists()) {
                  outputFile.getParentFile().mkdirs();
               }

               FileOutputStream output = new FileOutputStream(outputFile);
               output.write(environmentGlobal.getClass(className));
               output.close();
            }
         } catch (IOException var22) {
            var22.printStackTrace();
         }
      }

      environmentGlobal.putClass("__ZenMain__", clsMain.toByteArray());
   }

   public static ZenModule compileScriptFile(File single, IZenCompileEnvironment environment, ClassLoader baseClassLoader) throws IOException {
      Map<String, byte[]> classes = new HashMap();
      ClassNameGenerator nameGen = new ClassNameGenerator();
      EnvironmentGlobal environmentGlobal = new EnvironmentGlobal(environment, classes, nameGen);
      String filename = single.getName();
      String className = extractClassName(filename);
      FileInputStream input = new FileInputStream(single);
      Reader reader = new InputStreamReader(new BufferedInputStream(input));
      ZenTokener parser = new ZenTokener(reader, environment, filename, false);
      ZenParsedFile file = new ZenParsedFile(filename, className, parser, environmentGlobal);
      reader.close();
      List<ZenParsedFile> files = new ArrayList();
      files.add(file);
      compileScripts(filename, files, environmentGlobal, false);
      generateDebug(classes);
      return new ZenModule(classes, baseClassLoader);
   }

   public static ZenModule compileScriptString(String script, String name, IZenCompileEnvironment environment, ClassLoader baseClassLoader) throws IOException {
      Map<String, byte[]> classes = new HashMap();
      ClassNameGenerator nameGen = new ClassNameGenerator();
      EnvironmentGlobal environmentGlobal = new EnvironmentGlobal(environment, classes, nameGen);
      String className = extractClassName(name);
      StringReader reader = new StringReader(script);
      ZenTokener parser = new ZenTokener(reader, environment, name, false);
      ZenParsedFile file = new ZenParsedFile(name, className, parser, environmentGlobal);
      reader.close();
      List<ZenParsedFile> files = new ArrayList();
      files.add(file);
      compileScripts(name, files, environmentGlobal, false);
      generateDebug(classes);
      return new ZenModule(classes, baseClassLoader);
   }

   public static ZenModule compileZip(File file, String subdir, IZenCompileEnvironment environment, ClassLoader baseClassLoader) throws IOException {
      Map<String, byte[]> classes = new HashMap();
      ClassNameGenerator nameGen = new ClassNameGenerator();
      EnvironmentGlobal environmentGlobal = new EnvironmentGlobal(environment, classes, nameGen);
      List<ZenParsedFile> files = new ArrayList();
      ZipFile zipFile = new ZipFile(file);
      Enumeration<? extends ZipEntry> entries = zipFile.entries();

      while(entries.hasMoreElements()) {
         ZipEntry entry = (ZipEntry)entries.nextElement();
         if (entry.getName().startsWith(subdir) && !entry.getName().equals(subdir)) {
            String filename = entry.getName().substring(subdir.length());
            String className = extractClassName(filename);
            Reader reader = new InputStreamReader(new BufferedInputStream(zipFile.getInputStream(entry)));
            ZenTokener parser = new ZenTokener(reader, environment, filename, false);
            ZenParsedFile pfile = new ZenParsedFile(filename, className, parser, environmentGlobal);
            files.add(pfile);
            reader.close();
         }
      }

      String filename = file.getName();
      compileScripts(filename, files, environmentGlobal, true);
      return new ZenModule(classes, baseClassLoader);
   }

   private static void generateDebug(Map<String, byte[]> classes) throws IOException {
      File outputDir = new File("generated");
      outputDir.mkdir();

      for(Entry<String, byte[]> entry : classes.entrySet()) {
         File outputFile = new File(outputDir, ((String)entry.getKey()).replace('.', '/') + ".class");
         if (!outputFile.getParentFile().exists()) {
            outputFile.getParentFile().mkdirs();
         }

         FileOutputStream output = new FileOutputStream(outputFile);
         output.write((byte[])entry.getValue());
         output.close();
      }

   }

   public static String extractClassName(String filename) {
      filename = filename.replace('\\', '/');
      if (filename.startsWith("/")) {
         filename = filename.substring(1);
      }

      int lastDot = filename.lastIndexOf(46);
      if (lastDot > 0) {
         filename = filename.substring(0, lastDot);
      }

      filename = filename.replace(".", "_");
      filename = filename.replace(" ", "_");
      int lastSlash = filename.lastIndexOf(47);
      String dir;
      String name;
      if (lastSlash > 0) {
         dir = filename.substring(0, lastSlash);
         name = filename.substring(lastSlash + 1);
      } else {
         name = filename;
         dir = "";
      }

      return (dir.length() > 0 ? dir.replace('/', '\\') + "\\" : "") + StringUtil.capitalize(name);
   }

   public Runnable getMain() {
      try {
         return (Runnable)this.classLoader.loadClass("__ZenMain__").newInstance();
      } catch (IllegalAccessException | ClassNotFoundException | InstantiationException var2) {
         return null;
      }
   }

   private class MyClassLoader extends ClassLoader {
      private MyClassLoader(ClassLoader baseClassLoader) {
         super(baseClassLoader);
      }

      public Class<?> findClass(String name) throws ClassNotFoundException {
         if (ZenModule.loadedClasses.containsKey(name)) {
            return (Class)ZenModule.loadedClasses.get(name);
         } else if (ZenModule.classes.containsKey(name)) {
            byte[] bytes = (byte[])ZenModule.classes.get(name);
            if ("__ZenMain__".equals(name)) {
               return this.defineClass(name, bytes, 0, bytes.length);
            } else {
               ZenModule.loadedClasses.put(name, this.defineClass(name, bytes, 0, bytes.length));
               return (Class)ZenModule.loadedClasses.get(name);
            }
         } else {
            return super.findClass(name);
         }
      }

      public Class<?> loadClass(String name) throws ClassNotFoundException {
         if (ZenModule.loadedClasses.containsKey(name)) {
            return (Class)ZenModule.loadedClasses.get(name);
         } else if (ZenModule.classes.containsKey(name)) {
            byte[] bytes = (byte[])ZenModule.classes.get(name);
            if ("__ZenMain__".equals(name)) {
               return this.defineClass(name, bytes, 0, bytes.length);
            } else {
               ZenModule.loadedClasses.put(name, this.defineClass(name, bytes, 0, bytes.length));
               return (Class)ZenModule.loadedClasses.get(name);
            }
         } else {
            return super.loadClass(name);
         }
      }
   }
}
