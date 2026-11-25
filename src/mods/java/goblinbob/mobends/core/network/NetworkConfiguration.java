package goblinbob.mobends.core.network;

import net.minecraft.client.Minecraft;

public class NetworkConfiguration {
   public static NetworkConfiguration instance = new NetworkConfiguration();
   private final SharedConfig sharedConfig = new SharedConfig();
   private final SharedProperty<Boolean> modelScalingAllowed;
   private final SharedProperty<Boolean> bendsPacksAllowed;
   private final SharedProperty<Boolean> movementLimited;

   public NetworkConfiguration() {
      this.sharedConfig.addProperty(this.modelScalingAllowed = new SharedBooleanProp("modelScalingAllowed", false, "Does the server allow scaling of the player model more than the normal size?"));
      this.sharedConfig.addProperty(this.bendsPacksAllowed = new SharedBooleanProp("bendsPacksAllowed", true, "Does the server allow the use of bends packs?"));
      this.sharedConfig.addProperty(this.movementLimited = new SharedBooleanProp("movementLimited", true, "Does the server limit excessive bends pack transformation?"));
   }

   public void onWorldJoin() {
      this.modelScalingAllowed.setValue(Boolean.valueOf(Minecraft.getMinecraft().isSingleplayer()));
      this.bendsPacksAllowed.setValue(Boolean.valueOf(true));
      this.movementLimited.setValue(Boolean.valueOf(!Minecraft.getMinecraft().isSingleplayer()));
   }

   public SharedConfig getSharedConfig() {
      return this.sharedConfig;
   }

   public boolean isModelScalingAllowed() {
      return ((Boolean)this.modelScalingAllowed.getValue()).booleanValue();
   }

   public boolean areBendsPacksAllowed() {
      return ((Boolean)this.bendsPacksAllowed.getValue()).booleanValue();
   }

   public boolean isMovementLimited() {
      return ((Boolean)this.movementLimited.getValue()).booleanValue();
   }
}
