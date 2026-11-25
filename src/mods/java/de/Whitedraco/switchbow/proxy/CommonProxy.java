package de.Whitedraco.switchbow.proxy;

import de.Whitedraco.switchbow.Config.WriteLoadConfig;
import java.io.IOException;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class CommonProxy {
   public CommonProxy() {
   }

   public void regIcons() {
   }

   public void registerItemModel(Item item) {
   }

   public void SpawnParticle(World worldIn, double posx, double posy, double posz, double motionx, double motiony, double motionz, double EntityPosx, double EntityPosz, int Winkel) {
   }

   public void SpawnParticleFreeze(double posX, double posY, double posZ, double d0, double d1, double d2) {
   }

   public void registerRenderers() {
   }

   public void readConfig() {
      try {
         WriteLoadConfig.startConfigs();
      } catch (IOException var2) {
         var2.printStackTrace();
      }

   }

   public void SpawnPodestParticle(World init, double posX, double posY, double posZ, double d0, double d1, double d2) {
   }
}
