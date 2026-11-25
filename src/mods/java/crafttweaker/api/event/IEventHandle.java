package crafttweaker.api.event;

import crafttweaker.annotations.ZenRegister;
import java.io.Closeable;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("crafttweaker.event.IEventHandler")
@ZenRegister
public interface IEventHandle extends Closeable {
   @ZenMethod
   void close();
}
