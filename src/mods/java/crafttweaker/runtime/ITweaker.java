package crafttweaker.runtime;

import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.network.NetworkSide;
import crafttweaker.preprocessor.PreprocessorManager;
import crafttweaker.runtime.events.CrTLoaderLoadingEvent;
import crafttweaker.runtime.events.CrTScriptLoadingEvent;
import crafttweaker.util.IEventHandler;
import java.util.List;
import stanhebben.zenscript.annotations.ZenClass;

@ZenClass("crafttweaker.ICraftTweaker")
@ZenRegister
public interface ITweaker {
   void apply(IAction var1);

   void setScriptProvider(IScriptProvider var1);

   void load();

   boolean loadScript(boolean var1, String var2);

   void loadScript(boolean var1, ScriptLoader var2);

   List<IAction> getActions();

   void enableDebug();

   PreprocessorManager getPreprocessorManager();

   NetworkSide getNetworkSide();

   void setNetworkSide(NetworkSide var1);

   void registerLoadStartedEvent(IEventHandler<CrTLoaderLoadingEvent.Started> var1);

   void registerLoadFinishedEvent(IEventHandler<CrTLoaderLoadingEvent.Finished> var1);

   void registerLoadAbortedEvent(IEventHandler<CrTLoaderLoadingEvent.Aborted> var1);

   void registerScriptLoadPreEvent(IEventHandler<CrTScriptLoadingEvent.Pre> var1);

   void registerScriptLoadPostEvent(IEventHandler<CrTScriptLoadingEvent.Post> var1);

   List<ScriptLoader> getLoaders();

   ScriptLoader getOrCreateLoader(String... var1);
}
