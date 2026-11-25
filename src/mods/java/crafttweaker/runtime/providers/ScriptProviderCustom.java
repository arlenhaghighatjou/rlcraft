package crafttweaker.runtime.providers;

import crafttweaker.runtime.IScriptIterator;
import crafttweaker.runtime.IScriptProvider;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ScriptProviderCustom implements IScriptProvider {
   private static final Charset UTF8 = Charset.forName("UTF-8");
   private final String moduleName;
   private final List<ScriptProviderCustom.CustomScript> scripts;

   public ScriptProviderCustom(String moduleName) {
      this.moduleName = moduleName;
      this.scripts = new ArrayList();
   }

   public void add(String name, byte[] content) {
      this.scripts.add(new ScriptProviderCustom.CustomScript(name, content));
   }

   public void add(String name, String content) {
      this.add(name, content.getBytes(UTF8));
   }

   public Iterator<IScriptIterator> getScripts() {
      return Collections.singleton(new ScriptProviderCustom.CustomScriptIterator()).iterator();
   }

   private static class CustomScript {
      private final String name;
      private final byte[] content;

      public CustomScript(String name, byte[] content) {
         this.name = name;
         this.content = content;
      }
   }

   private class CustomScriptIterator implements IScriptIterator {
      private final Iterator<ScriptProviderCustom.CustomScript> iterator;
      private ScriptProviderCustom.CustomScript current;

      public CustomScriptIterator() {
         this.iterator = ScriptProviderCustom.this.scripts.iterator();
      }

      private CustomScriptIterator(ScriptProviderCustom.CustomScript current) {
         this.iterator = ScriptProviderCustom.this.scripts.iterator();
         this.current = current;
      }

      public String getGroupName() {
         return ScriptProviderCustom.this.moduleName;
      }

      public boolean next() {
         if (this.iterator.hasNext()) {
            this.current = (ScriptProviderCustom.CustomScript)this.iterator.next();
            return true;
         } else {
            return false;
         }
      }

      public String getName() {
         return this.current == null ? "zzNullzz" : this.current.name;
      }

      public InputStream open() {
         return new ByteArrayInputStream(this.current.content);
      }

      public IScriptIterator copyCurrent() {
         return ScriptProviderCustom.this.new CustomScriptIterator(this.current);
      }
   }
}
