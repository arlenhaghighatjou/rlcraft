package crafttweaker.runtime.providers;

import crafttweaker.runtime.IScriptIterator;
import crafttweaker.runtime.IScriptProvider;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ScriptProviderCascade implements IScriptProvider {
   private final IScriptProvider[] providers;

   public ScriptProviderCascade(IScriptProvider... providers) {
      this.providers = providers;
   }

   public Iterator<IScriptIterator> getScripts() {
      return new ScriptProviderCascade.MyScripterator();
   }

   private class MyScripterator implements Iterator<IScriptIterator> {
      private final Set<String> executed = new HashSet();
      private int currentIndex;
      private Iterator<IScriptIterator> current;
      private IScriptIterator currentValue;

      public MyScripterator() {
         this.currentIndex = ScriptProviderCascade.this.providers.length - 1;
         this.currentIndex = ScriptProviderCascade.this.providers.length - 1;
         this.current = ScriptProviderCascade.this.providers[this.currentIndex].getScripts();
         this.advance();
      }

      public boolean hasNext() {
         return this.currentIndex >= 0;
      }

      public IScriptIterator next() {
         IScriptIterator result = this.currentValue;
         this.executed.add(result.getGroupName());
         this.advance();
         return result;
      }

      private void advance() {
         while(true) {
            if (!this.current.hasNext()) {
               --this.currentIndex;
               if (this.currentIndex < 0) {
                  return;
               }

               this.current = ScriptProviderCascade.this.providers[this.currentIndex].getScripts();
            } else {
               if (this.currentIndex >= 0) {
                  this.currentValue = (IScriptIterator)this.current.next();
                  if (this.executed.contains(this.currentValue.getGroupName()) && this.hasNext()) {
                     continue;
                  }
               }

               return;
            }
         }
      }

      public void remove() {
         throw new UnsupportedOperationException("Not supported yet.");
      }
   }
}
