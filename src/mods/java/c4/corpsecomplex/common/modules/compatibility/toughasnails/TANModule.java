package c4.corpsecomplex.common.modules.compatibility.toughasnails;

import c4.corpsecomplex.common.Module;
import c4.corpsecomplex.network.NetworkHandler;
import c4.corpsecomplex.network.TANMessage;
import java.util.ArrayList;
import java.util.Collections;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import toughasnails.api.temperature.Temperature;
import toughasnails.api.temperature.TemperatureHelper;
import toughasnails.api.thirst.ThirstHelper;

public class TANModule extends Module {
   private static final String MOD_ID = "toughasnails";
   private static boolean keepThirst;
   private static boolean keepHydration;
   private static boolean keepTemperature;
   private static int minThirst;
   private static int maxThirst;
   private static boolean cfgEnabled;

   @SubscribeEvent
   @Method(
      modid = "toughasnails"
   )
   public void onPlayerRespawnBegin(Clone e) {
      if (e.isWasDeath() && !e.getEntityPlayer().getEntityWorld().isRemote) {
         EntityPlayer player = e.getEntityPlayer();
         EntityPlayer oldPlayer = e.getOriginal();
         restoreStats(player, oldPlayer);
      }
   }

   @SubscribeEvent
   @Method(
      modid = "toughasnails"
   )
   public void onPlayerRespawnFinish(PlayerRespawnEvent evt) {
      if (!evt.player.world.isRemote) {
         updateClientTAN(evt.player);
      }

   }

   public TANModule() {
      super("Tough as Nails", "Customize keeping Tough as Nails attributes on respawn");
   }

   public void loadModuleConfig() {
      this.setCategoryComment();
      cfgEnabled = this.getBool("Enable TAN Module", false, "Set to true to enable Tough as Nails module", false);
      keepThirst = this.getBool("Keep Thirst Level", false, "Set to true to retain thirst level on respawn", false);
      keepHydration = this.getBool("Keep Hydration Level", false, "Set to true to retain hydration level on respawn", false);
      keepTemperature = this.getBool("Keep Temperature Level", false, "Set to true to retain temperature level on respawn", false);
      minThirst = this.getInt("Minimum Thirst Level", 6, 0, 20, "Lowest amount of thirst you can respawn with", false);
      maxThirst = this.getInt("Maximum Thirst Level", 20, minThirst, 20, "Maximum amount of thirst you can respawn with", false);
   }

   public void initPropOrder() {
      this.propOrder = new ArrayList(Collections.singletonList("Enable TAN Module"));
   }

   public void setEnabled() {
      this.enabled = cfgEnabled;
   }

   @Method(
      modid = "toughasnails"
   )
   private static void restoreStats(EntityPlayer player, EntityPlayer oldPlayer) {
      int thirst = ThirstHelper.getThirstData(oldPlayer).getThirst();
      int temp = TemperatureHelper.getTemperatureData(oldPlayer).getTemperature().getRawValue();
      float hydration = ThirstHelper.getThirstData(oldPlayer).getHydration();
      if (keepThirst) {
         restoreThirst(player, thirst);
      } else {
         restoreThirst(player, 20);
      }

      if (keepHydration) {
         restoreHydration(player, hydration);
      }

      if (keepTemperature) {
         restoreTemperature(player, temp);
      }

   }

   @Method(
      modid = "toughasnails"
   )
   private static void updateClientTAN(EntityPlayer player) {
      int thirst = ThirstHelper.getThirstData(player).getThirst();
      int temp = TemperatureHelper.getTemperatureData(player).getTemperature().getRawValue();
      NetworkHandler.INSTANCE.sendTo(new TANMessage(thirst, temp), (EntityPlayerMP)player);
   }

   @Method(
      modid = "toughasnails"
   )
   private static void restoreThirst(EntityPlayer player, int thirst) {
      ThirstHelper.getThirstData(player).setThirst(Math.max(minThirst, Math.min(maxThirst, thirst)));
   }

   @Method(
      modid = "toughasnails"
   )
   private static void restoreHydration(EntityPlayer player, float hydration) {
      ThirstHelper.getThirstData(player).setHydration(hydration);
   }

   @Method(
      modid = "toughasnails"
   )
   private static void restoreTemperature(EntityPlayer player, int temp) {
      TemperatureHelper.getTemperatureData(player).setTemperature(new Temperature(temp));
   }
}
