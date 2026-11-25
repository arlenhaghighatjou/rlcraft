package crafttweaker.runtime.providers;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.runtime.IScriptIterator;
import crafttweaker.runtime.IScriptProvider;
import crafttweaker.util.FileUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class ScriptProviderMemory implements IScriptProvider {
   private final Map<String, ScriptProviderMemory.MemoryModule> modules = new HashMap();

   public ScriptProviderMemory(byte[] scripts) {
      try {
         InflaterInputStream inflater = new InflaterInputStream(new ByteArrayInputStream(scripts));
         DataInputStream inflaterData = new DataInputStream(inflater);

         for(String moduleName = inflaterData.readUTF(); moduleName.length() > 0; moduleName = inflaterData.readUTF()) {
            List<ScriptProviderMemory.MemoryFile> files = new ArrayList();

            for(String fileName = inflaterData.readUTF(); fileName.length() > 0; fileName = inflaterData.readUTF()) {
               byte[] data = new byte[inflaterData.readInt()];
               inflaterData.readFully(data);
               files.add(new ScriptProviderMemory.MemoryFile(fileName, data));
            }

            this.modules.put(moduleName, new ScriptProviderMemory.MemoryModule(moduleName, files));
         }

         inflaterData.close();
      } catch (IOException var8) {
         CraftTweakerAPI.logError("Could not load transmitted scripts: " + var8.getMessage());
      }

   }

   public static byte[] collect(IScriptProvider provider) {
      ByteArrayOutputStream output = new ByteArrayOutputStream();

      try {
         DeflaterOutputStream deflater = new DeflaterOutputStream(output);
         DataOutputStream deflaterData = new DataOutputStream(deflater);
         Set<String> executed = new HashSet();
         Iterator<IScriptIterator> scripts = provider.getScripts();

         while(scripts.hasNext()) {
            IScriptIterator script = (IScriptIterator)scripts.next();
            if (!executed.contains(script.getGroupName())) {
               executed.add(script.getGroupName());
               deflaterData.writeUTF(script.getGroupName());

               while(script.next()) {
                  String name = script.getName();
                  byte[] data = FileUtil.read(script.open());
                  if (data.length != 0) {
                     deflaterData.writeUTF(name);
                     deflaterData.writeInt(data.length);
                     deflaterData.write(data);
                  }
               }

               deflaterData.writeUTF("");
            }
         }

         deflaterData.writeUTF("");
         deflater.close();
      } catch (IOException var9) {
         CraftTweakerAPI.logError("Could not collect scripts: " + var9.getMessage());
      }

      return output.toByteArray();
   }

   public Iterator<IScriptIterator> getScripts() {
      return new ScriptProviderMemory.ProviderIterator();
   }

   private static class MemoryFile {
      private final String name;
      private final byte[] data;

      public MemoryFile(String name, byte[] data) {
         this.name = name;
         this.data = data;
      }
   }

   private static class MemoryModule {
      private final String name;
      private final List<ScriptProviderMemory.MemoryFile> data;

      public MemoryModule(String name, List<ScriptProviderMemory.MemoryFile> data) {
         this.name = name;
         this.data = data;
      }
   }

   private class ProviderIterator implements Iterator<IScriptIterator> {
      private final Iterator<ScriptProviderMemory.MemoryModule> baseIterator;

      private ProviderIterator() {
         this.baseIterator = ScriptProviderMemory.this.modules.values().iterator();
      }

      public boolean hasNext() {
         return this.baseIterator.hasNext();
      }

      public IScriptIterator next() {
         return new ScriptProviderMemory.ScriptIterator((ScriptProviderMemory.MemoryModule)this.baseIterator.next());
      }

      public void remove() {
         throw new UnsupportedOperationException("Not supported yet.");
      }
   }

   private static class ScriptIterator implements IScriptIterator {
      private final ScriptProviderMemory.MemoryModule module;
      private final Iterator<ScriptProviderMemory.MemoryFile> files;
      private ScriptProviderMemory.MemoryFile current;

      public ScriptIterator(ScriptProviderMemory.MemoryModule module) {
         this.module = module;
         this.files = module.data.iterator();
         this.current = null;
      }

      private ScriptIterator(ScriptProviderMemory.MemoryModule module, Iterator<ScriptProviderMemory.MemoryFile> files, ScriptProviderMemory.MemoryFile current) {
         this.module = module;
         this.files = files;
         this.current = current;
      }

      public String getGroupName() {
         return this.module.name;
      }

      public boolean next() {
         if (this.files.hasNext()) {
            this.current = (ScriptProviderMemory.MemoryFile)this.files.next();
            return true;
         } else {
            return false;
         }
      }

      public String getName() {
         return this.current.name;
      }

      public InputStream open() {
         return new ByteArrayInputStream(this.current.data);
      }

      public IScriptIterator copyCurrent() {
         return new ScriptProviderMemory.ScriptIterator(this.module, this.files, this.current);
      }
   }
}
