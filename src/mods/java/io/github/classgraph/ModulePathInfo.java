package io.github.classgraph;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import nonapi.io.github.classgraph.utils.JarUtils;
import nonapi.io.github.classgraph.utils.Join;

public class ModulePathInfo {
   public final Set<String> modulePath = new LinkedHashSet();
   public final Set<String> addModules = new LinkedHashSet();
   public final Set<String> patchModules = new LinkedHashSet();
   public final Set<String> addExports = new LinkedHashSet();
   public final Set<String> addOpens = new LinkedHashSet();
   public final Set<String> addReads = new LinkedHashSet();
   private final List<Set<String>> fields;
   private static final List<String> argSwitches = Arrays.asList("--module-path=", "--add-modules=", "--patch-module=", "--add-exports=", "--add-opens=", "--add-reads=");
   private static final List<Character> argPartSeparatorChars = Arrays.asList(File.pathSeparatorChar, ',', '\u0000', '\u0000', '\u0000', '\u0000');

   public ModulePathInfo() {
      this.fields = Arrays.asList(this.modulePath, this.addModules, this.patchModules, this.addExports, this.addOpens, this.addReads);

      for(String arg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
         for(int i = 0; i < this.fields.size(); ++i) {
            String argSwitch = (String)argSwitches.get(i);
            if (arg.startsWith(argSwitch)) {
               String argParam = arg.substring(argSwitch.length());
               Set<String> argField = (Set)this.fields.get(i);
               char sepChar = ((Character)argPartSeparatorChars.get(i)).charValue();
               if (sepChar == 0) {
                  argField.add(argParam);
               } else {
                  for(String argPart : JarUtils.smartPathSplit(argParam, sepChar)) {
                     argField.add(argPart);
                  }
               }
            }
         }
      }

   }

   public String toString() {
      StringBuilder buf = new StringBuilder(1024);
      if (!this.modulePath.isEmpty()) {
         buf.append("--module-path=");
         buf.append(Join.join(File.pathSeparator, this.modulePath));
      }

      if (!this.addModules.isEmpty()) {
         if (buf.length() > 0) {
            buf.append(' ');
         }

         buf.append("--add-modules=");
         buf.append(Join.join(",", this.addModules));
      }

      for(String patchModulesEntry : this.patchModules) {
         if (buf.length() > 0) {
            buf.append(' ');
         }

         buf.append("--patch-module=");
         buf.append(patchModulesEntry);
      }

      for(String addExportsEntry : this.addExports) {
         if (buf.length() > 0) {
            buf.append(' ');
         }

         buf.append("--add-exports=");
         buf.append(addExportsEntry);
      }

      for(String addOpensEntry : this.addOpens) {
         if (buf.length() > 0) {
            buf.append(' ');
         }

         buf.append("--add-opens=");
         buf.append(addOpensEntry);
      }

      for(String addReadsEntry : this.addReads) {
         if (buf.length() > 0) {
            buf.append(' ');
         }

         buf.append("--add-reads=");
         buf.append(addReadsEntry);
      }

      return buf.toString();
   }
}
