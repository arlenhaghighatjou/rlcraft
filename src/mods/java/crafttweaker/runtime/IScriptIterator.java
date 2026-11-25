package crafttweaker.runtime;

import java.io.IOException;
import java.io.InputStream;

public interface IScriptIterator {
   String getGroupName();

   boolean next();

   String getName();

   InputStream open() throws IOException;

   IScriptIterator copyCurrent();
}
