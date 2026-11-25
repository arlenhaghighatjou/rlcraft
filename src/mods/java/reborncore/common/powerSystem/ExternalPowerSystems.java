package reborncore.common.powerSystem;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import reborncore.RebornCore;
import reborncore.api.power.ExternalPowerManager;
import reborncore.common.powerSystem.forge.ForgePowerItemManager;
import reborncore.common.registration.IRegistryFactory;
import reborncore.common.registration.RebornRegistry;
import reborncore.common.registration.RegistryTarget;

@IRegistryFactory.RegistryFactory
public class ExternalPowerSystems implements IRegistryFactory {
   public static List<ExternalPowerManager> externalPowerHandlerList = new ArrayList();

   public ExternalPowerSystems() {
   }

   public static boolean isPoweredItem(ItemStack stack) {
      return externalPowerHandlerList.stream().anyMatch((externalPowerManager) -> {
         return externalPowerManager.isPoweredItem(stack);
      });
   }

   public static void dischargeItem(TilePowerAcceptor tilePowerAcceptor, ItemStack stack) {
      externalPowerHandlerList.stream().filter((externalPowerManager) -> {
         return externalPowerManager.isPoweredItem(stack);
      }).forEach((externalPowerManager) -> {
         externalPowerManager.dischargeItem(tilePowerAcceptor, stack);
      });
   }

   public static void chargeItem(TilePowerAcceptor tilePowerAcceptor, ItemStack stack) {
      externalPowerHandlerList.stream().filter((externalPowerManager) -> {
         return externalPowerManager.isPoweredItem(stack);
      }).forEach((externalPowerManager) -> {
         externalPowerManager.chargeItem(tilePowerAcceptor, stack);
      });
   }

   public static boolean isPoweredTile(TileEntity tileEntity, EnumFacing facing) {
      return externalPowerHandlerList.stream().anyMatch((externalPowerManager) -> {
         return externalPowerManager.isPoweredTile(tileEntity, facing);
      });
   }

   public static void chargeItem(ForgePowerItemManager powerAcceptor, ItemStack stack) {
      externalPowerHandlerList.stream().filter((externalPowerManager) -> {
         return externalPowerManager.isPoweredItem(stack);
      }).forEach((externalPowerManager) -> {
         externalPowerManager.chargeItem(powerAcceptor, stack);
      });
   }

   public static void requestEnergyFromArmor(ForgePowerItemManager powerAcceptor, EntityLivingBase entity) {
      externalPowerHandlerList.forEach((externalPowerManager) -> {
         externalPowerManager.requestEnergyFromArmor(powerAcceptor, entity);
      });
   }

   public void handleClass(Class<?> clazz) {
      if (this.isPowerManager(clazz)) {
         try {
            ExternalPowerManager powerManager = (ExternalPowerManager)clazz.newInstance();
            externalPowerHandlerList.add(powerManager);
            RebornCore.logHelper.info("Loaded power manager from: " + powerManager.getClass().getSimpleName());
         } catch (IllegalAccessException | InstantiationException var3) {
            throw new RuntimeException("Failed to register compat module", var3);
         }
      }

   }

   private boolean isPowerManager(Class<?> clazz) {
      for(Class<?> iface : clazz.getInterfaces()) {
         if (iface == ExternalPowerManager.class) {
            return true;
         }
      }

      return false;
   }

   public Class<? extends Annotation> getAnnotation() {
      return RebornRegistry.class;
   }

   public List<RegistryTarget> getTargets() {
      return Collections.singletonList(RegistryTarget.CLASS);
   }
}
