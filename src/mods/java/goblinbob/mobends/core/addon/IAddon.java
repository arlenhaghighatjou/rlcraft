package goblinbob.mobends.core.addon;

public interface IAddon {
   void registerContent(AddonAnimationRegistry var1);

   String getDisplayName();

   default void onRenderTick(float partialTicks) {
   }

   default void onClientTick() {
   }

   default void onRefresh() {
   }
}
