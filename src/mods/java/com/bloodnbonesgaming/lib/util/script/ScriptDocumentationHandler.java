package com.bloodnbonesgaming.lib.util.script;

import com.bloodnbonesgaming.lib.BNBGamingLib;
import com.bloodnbonesgaming.lib.util.FileHelper;
import com.google.common.collect.Sets;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Set;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;

public class ScriptDocumentationHandler {
   private static Set<ASMData> scriptDocs;

   public ScriptDocumentationHandler() {
   }

   public static void setScriptDocs(ASMDataTable asmTable) {
      scriptDocs = Sets.newHashSet(asmTable.getAll(ScriptClassDocumentation.class.getCanonicalName()));
   }

   public static void copyDocumentationFolder(Class<?> classInJar, String destination) {
      if (BNBGamingLib.proxy.client()) {
         FileHelper.copyDirectoryFromJar(classInJar, "/documentation/", destination);
      }

   }

   public static void printAnnotatedDocumentation(String modDirectory) {
      if (BNBGamingLib.proxy.client()) {
         for(ASMData data : scriptDocs) {
            try {
               Class<?> asmClass = Class.forName(data.getClassName());
               if (asmClass.getName().startsWith(modDirectory)) {
                  ScriptClassDocumentation classAnn = (ScriptClassDocumentation)asmClass.getAnnotation(ScriptClassDocumentation.class);
                  String printString = "";
                  String classExplaination = classAnn.classExplaination();
                  if (!classExplaination.isEmpty()) {
                     printString = printString.concat(lineWrap(classExplaination) + System.lineSeparator());
                     printString = printString.concat(System.lineSeparator());
                     printString = printString.concat(System.lineSeparator());
                     printString = printString.concat(System.lineSeparator());
                  }

                  Constructor[] constructors = asmClass.getConstructors();

                  for(Constructor constructor : constructors) {
                     if (constructor.isAnnotationPresent(ScriptMethodDocumentation.class)) {
                        ScriptMethodDocumentation anno = (ScriptMethodDocumentation)constructor.getDeclaredAnnotation(ScriptMethodDocumentation.class);
                        String usage = anno.usage();
                        String notes = anno.notes();
                        String defaultValues = anno.defaultValues();
                        String args;
                        if (constructor.isAnnotationPresent(ScriptArgs.class)) {
                           ScriptArgs argsAnno = (ScriptArgs)constructor.getDeclaredAnnotation(ScriptArgs.class);
                           ArgType[] argTypes = argsAnno.args();
                           String temp = "";

                           for(int i = 0; i < argTypes.length; ++i) {
                              if (i > 0) {
                                 temp = temp.concat(", ");
                              }

                              temp = temp.concat(argTypes[i].toString());
                           }

                           args = temp;
                        } else {
                           args = anno.args();
                        }

                        String methodString = "Constructor:" + System.lineSeparator();
                        if (args != null && !args.isEmpty()) {
                           methodString = methodString.concat("    Arguments:" + System.lineSeparator());
                           methodString = methodString.concat("        " + args + System.lineSeparator());
                        }

                        if (usage != null && !usage.isEmpty()) {
                           methodString = methodString.concat("    Argument Usage:" + System.lineSeparator());
                           methodString = methodString.concat("        " + usage + System.lineSeparator());
                        }

                        if (defaultValues != null && !defaultValues.isEmpty()) {
                           methodString = methodString.concat("    Default Values:" + System.lineSeparator());
                           methodString = methodString.concat("        " + defaultValues + System.lineSeparator());
                        }

                        if (notes != null && !notes.isEmpty()) {
                           methodString = methodString.concat("    Notes:" + System.lineSeparator());
                           methodString = methodString.concat("        " + notes + System.lineSeparator());
                        }

                        methodString = methodString.concat(System.lineSeparator());
                        printString = printString.concat(methodString);
                     }
                  }

                  Method[] methods = asmClass.getMethods();

                  for(Method method : methods) {
                     if (method.isAnnotationPresent(ScriptMethodDocumentation.class)) {
                        ScriptMethodDocumentation anno = (ScriptMethodDocumentation)method.getAnnotation(ScriptMethodDocumentation.class);
                        String usage = anno.usage();
                        String notes = anno.notes();
                        String defaultValues = anno.defaultValues();
                        String args;
                        if (method.isAnnotationPresent(ScriptArgs.class)) {
                           ScriptArgs argsAnno = (ScriptArgs)method.getAnnotation(ScriptArgs.class);
                           ArgType[] argTypes = argsAnno.args();
                           String temp = "";

                           for(int i = 0; i < argTypes.length; ++i) {
                              if (i > 0) {
                                 temp = temp.concat(", ");
                              }

                              temp = temp.concat(argTypes[i].toString());
                           }

                           args = temp;
                        } else {
                           args = anno.args();
                        }

                        String methodString = method.getName() + ":" + System.lineSeparator();
                        if (args != null && !args.isEmpty()) {
                           methodString = methodString.concat("    Arguments:" + System.lineSeparator());
                           methodString = methodString.concat("        " + args + System.lineSeparator());
                        }

                        if (usage != null && !usage.isEmpty()) {
                           methodString = methodString.concat("    Usage:" + System.lineSeparator());
                           methodString = methodString.concat("        " + usage + System.lineSeparator());
                        }

                        if (defaultValues != null && !defaultValues.isEmpty()) {
                           methodString = methodString.concat("    Default Values:" + System.lineSeparator());
                           methodString = methodString.concat("        " + defaultValues + System.lineSeparator());
                        }

                        if (notes != null && !notes.isEmpty()) {
                           methodString = methodString.concat("    Notes:" + System.lineSeparator());
                           methodString = methodString.concat("        " + notes + System.lineSeparator());
                        }

                        methodString = methodString.concat(System.lineSeparator());
                        printString = printString.concat(methodString);
                     }
                  }

                  if (!printString.isEmpty()) {
                     FileHelper.writeText(classAnn.documentationFile() + ".txt", printString);
                  }
               }
            } catch (ClassNotFoundException var22) {
               var22.printStackTrace();
            }
         }
      }

   }

   public static String lineWrap(String input) {
      StringBuilder builder = new StringBuilder(input);
      int nextWrap = 80;

      while(builder.length() > nextWrap) {
         int index = builder.indexOf(" ", nextWrap);
         if (index == -1) {
            nextWrap = builder.length();
         } else {
            nextWrap = index + System.lineSeparator().length() + 81;
            builder.insert(index + 1, System.lineSeparator());
         }
      }

      return builder.toString();
   }
}
