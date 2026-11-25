package reborncore.common.recipes;

public interface IUpgradeHandler {
   void resetSpeedMulti();

   double getSpeedMultiplier();

   void addPowerMulti(double var1);

   void resetPowerMulti();

   double getPowerMultiplier();

   double getEuPerTick(double var1);

   void addSpeedMulti(double var1);
}
