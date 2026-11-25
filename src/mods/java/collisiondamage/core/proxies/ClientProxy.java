package collisiondamage.core.proxies;

public class ClientProxy extends CommonProxy {
   public ClientProxy() {
   }

   public boolean isClient() {
      return true;
   }

   public void registerHandlers() {
      super.registerHandlers();
   }
}
