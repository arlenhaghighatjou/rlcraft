package crafttweaker.runtime.events;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.network.NetworkSide;
import crafttweaker.runtime.ScriptLoader;

/** @deprecated */
@Deprecated
public class CrTLoadingStartedEvent extends CrTLoaderLoadingEvent.Started {
   /** @deprecated */
   @Deprecated
   public CrTLoadingStartedEvent(String loaderName, boolean isSyntaxCommand, NetworkSide networkSide) {
      super(CraftTweakerAPI.tweaker.getOrCreateLoader(loaderName), networkSide, isSyntaxCommand);
   }

   public CrTLoadingStartedEvent(ScriptLoader loader, boolean isSyntaxCommand, NetworkSide networkSide) {
      super(loader, networkSide, isSyntaxCommand);
   }

   public String getLoaderName() {
      return this.getLoader().getMainName();
   }

   public ScriptLoader getLoader() {
      return super.getLoader();
   }

   public NetworkSide getNetworkSide() {
      return super.getNetworkSide();
   }

   public boolean isSyntaxCommand() {
      return super.isSyntaxCommand();
   }
}
