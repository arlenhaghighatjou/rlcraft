package moddy10.craftablechainmail.util;

public final class Reference {
   public static final String MODID = "craftablechainmail";
   public static final String PROXY_CLIENT = "moddy10.craftablechainmail.proxy.ClientProxy";
   public static final String PROXY_COMMON = "moddy10.craftablechainmail.proxy.CommonProxy";

   private Reference() {
      throw new RuntimeException("No " + this.getClass().getName() + " instance for you!");
   }
}
