package crafttweaker.runtime;

import java.util.Iterator;

public interface IScriptProvider {
   Iterator<IScriptIterator> getScripts();
}
