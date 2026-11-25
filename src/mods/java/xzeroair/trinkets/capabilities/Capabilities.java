package xzeroair.trinkets.capabilities;

import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import xzeroair.trinkets.capabilities.InventoryContainerCapability.ITrinketContainerHandler;
import xzeroair.trinkets.capabilities.InventoryContainerCapability.TrinketContainerProvider;
import xzeroair.trinkets.capabilities.InventoryContainerCapability.TrinketContainerStorage;
import xzeroair.trinkets.capabilities.TileEntityCap.TileEntityProperties;
import xzeroair.trinkets.capabilities.Trinket.TrinketProperties;
import xzeroair.trinkets.capabilities.Vip.VipStatus;
import xzeroair.trinkets.capabilities.magic.MagicStats;
import xzeroair.trinkets.capabilities.race.EntityProperties;
import xzeroair.trinkets.capabilities.statushandler.StatusHandler;
import xzeroair.trinkets.container.TrinketContainerHandler;

public class Capabilities {
   @CapabilityInject(TileEntityProperties.class)
   public static Capability<TileEntityProperties> TILE_ENTITY_PROPERTIES;
   @CapabilityInject(StatusHandler.class)
   public static Capability<StatusHandler> STATUS_HANDLER;
   @CapabilityInject(EntityProperties.class)
   public static Capability<EntityProperties> ENTITY_PROPERTIES;
   @CapabilityInject(TrinketProperties.class)
   public static Capability<TrinketProperties> ITEM_TRINKET;
   @CapabilityInject(VipStatus.class)
   public static Capability<VipStatus> VIP_STATUS;
   @CapabilityInject(MagicStats.class)
   public static Capability<MagicStats> ENTITY_MAGIC;

   public Capabilities() {
   }

   public static void init() {
      registerNewCap(ITrinketContainerHandler.class, TrinketContainerStorage.storage, new Capabilities.TrinketContainerFactory());
      registerNewCap(EntityProperties.class, new CapabilityStorage());
      registerNewCap(MagicStats.class, new CapabilityStorage());
      registerNewCap(StatusHandler.class, new CapabilityStorage());
      registerNewCap(VipStatus.class, new CapabilityStorage());
      registerNewCap(TrinketProperties.class, new CapabilityStorage());
      registerNewCap(TileEntityProperties.class, new CapabilityStorage());
   }

   public static TileEntityProperties getTEProperties(TileEntity tileEntity) {
      return (TileEntityProperties)getCapabilityWithConsumer(tileEntity, TILE_ENTITY_PROPERTIES, (Consumer)null);
   }

   public static TileEntityProperties getTEProperties(TileEntity tileEntity, Consumer<TileEntityProperties> consumer) {
      return (TileEntityProperties)getCapabilityWithConsumer(tileEntity, TILE_ENTITY_PROPERTIES, consumer);
   }

   public static <R> R getTEProperties(TileEntity tileEntity, R ret, BiFunction<TileEntityProperties, R, R> func) {
      return (R)getCapabilityWithReturn(tileEntity, TILE_ENTITY_PROPERTIES, ret, func);
   }

   public static StatusHandler getStatusHandler(Entity entity) {
      return (StatusHandler)getCapabilityWithConsumer(entity, STATUS_HANDLER, (Consumer)null);
   }

   public static StatusHandler getStatusHandler(Entity entity, Consumer<StatusHandler> consumer) {
      return (StatusHandler)getCapabilityWithConsumer(entity, STATUS_HANDLER, consumer);
   }

   public static <R> R getStatusHandler(Entity entity, R ret, BiFunction<StatusHandler, R, R> func) {
      return (R)getCapabilityWithReturn(entity, STATUS_HANDLER, ret, func);
   }

   public static EntityProperties getEntityProperties(Entity entity) {
      return (EntityProperties)getCapabilityWithConsumer(entity, ENTITY_PROPERTIES, (Consumer)null);
   }

   public static EntityProperties getEntityProperties(Entity entity, Consumer<EntityProperties> consumer) {
      return (EntityProperties)getCapabilityWithConsumer(entity, ENTITY_PROPERTIES, consumer);
   }

   public static <R> R getEntityProperties(Entity entity, R ret, BiFunction<EntityProperties, R, R> func) {
      return (R)getCapabilityWithReturn(entity, ENTITY_PROPERTIES, ret, func);
   }

   /** @deprecated */
   @Deprecated
   public static EntityProperties getEntityRace(Entity entity) {
      return getEntityProperties(entity);
   }

   public static TrinketProperties getTrinketProperties(ItemStack stack) {
      return (TrinketProperties)getCapabilityWithConsumer(stack, ITEM_TRINKET, (Consumer)null);
   }

   public static TrinketProperties getTrinketProperties(ItemStack stack, Consumer<TrinketProperties> consumer) {
      return (TrinketProperties)getCapabilityWithConsumer(stack, ITEM_TRINKET, consumer);
   }

   public static <R> R getTrinketProperties(ItemStack stack, R ret, BiFunction<TrinketProperties, R, R> func) {
      return (R)getCapabilityWithReturn(stack, ITEM_TRINKET, ret, func);
   }

   public static VipStatus getVipStatus(Entity entity) {
      return (VipStatus)getCapabilityWithConsumer(entity, VIP_STATUS, (Consumer)null);
   }

   public static VipStatus getVipStatus(Entity entity, Consumer<VipStatus> consumer) {
      return (VipStatus)getCapabilityWithConsumer(entity, VIP_STATUS, consumer);
   }

   public static <R> R getVipStatus(Entity entity, R ret, BiFunction<VipStatus, R, R> func) {
      return (R)getCapabilityWithReturn(entity, VIP_STATUS, ret, func);
   }

   public static MagicStats getMagicStats(Entity entity) {
      return (MagicStats)getCapabilityWithConsumer(entity, ENTITY_MAGIC, (Consumer)null);
   }

   public static MagicStats getMagicStats(Entity entity, Consumer<MagicStats> consumer) {
      return (MagicStats)getCapabilityWithConsumer(entity, ENTITY_MAGIC, consumer);
   }

   public static <R> R getMagicStats(Entity entity, R ret, BiFunction<MagicStats, R, R> func) {
      return (R)getCapabilityWithReturn(entity, ENTITY_MAGIC, ret, func);
   }

   @Nullable
   public static <C> C getCapability(ICapabilitySerializable object, Capability<C> capability) {
      return (C)(object != null ? object.getCapability(capability, (EnumFacing)null) : null);
   }

   @Nullable
   public static <C> C getCapabilityWithConsumer(ICapabilitySerializable object, Capability<C> capability, Consumer<C> consumer) {
      C cap = (C)getCapability(object, capability);
      if (cap != null) {
         if (consumer != null) {
            try {
               consumer.accept(cap);
            } catch (Exception var5) {
               var5.printStackTrace();
            }
         }

         return cap;
      } else {
         return (C)null;
      }
   }

   @Nullable
   public static <C, R> R getCapabilityWithReturn(ICapabilitySerializable object, Capability<C> capability, R ret, BiFunction<C, R, R> func) {
      C cap = (C)getCapability(object, capability);
      if (cap != null && func != null) {
         try {
            return (R)func.apply(cap, ret);
         } catch (Exception var6) {
            var6.printStackTrace();
         }
      }

      return ret;
   }

   public static ITrinketContainerHandler getTrinketContainer(Entity entity) {
      return (ITrinketContainerHandler)getCapabilityWithConsumer(entity, TrinketContainerProvider.containerCap, (Consumer)null);
   }

   public static ITrinketContainerHandler getTrinketContainer(Entity entity, Consumer<ITrinketContainerHandler> consumer) {
      return (ITrinketContainerHandler)getCapabilityWithConsumer(entity, TrinketContainerProvider.containerCap, consumer);
   }

   public static <R> R getTrinketContainer(Entity entity, R ret, BiFunction<ITrinketContainerHandler, R, R> func) {
      return (R)getCapabilityWithReturn(entity, TrinketContainerProvider.containerCap, ret, func);
   }

   private static <T> void registerNewCap(Class<T> type) {
      registerNewCap(type, new IStorage<T>() {
         public NBTBase writeNBT(Capability<T> capability, T instance, EnumFacing side) {
            throw new UnsupportedOperationException();
         }

         public void readNBT(Capability<T> capability, T instance, EnumFacing side, NBTBase nbt) {
            throw new UnsupportedOperationException();
         }
      });
   }

   private static <T> void registerNewCap(Class<T> type, IStorage<T> storage) {
      registerNewCap(type, storage, () -> {
         return (void)null;
      });
   }

   private static <T> void registerNewCap(Class<T> type, IStorage<T> storage, Callable<? extends T> factory) {
      CapabilityManager.INSTANCE.register(type, storage, factory);
   }

   private static class TrinketContainerFactory implements Callable<ITrinketContainerHandler> {
      private TrinketContainerFactory() {
      }

      public ITrinketContainerHandler call() throws Exception {
         return new TrinketContainerHandler();
      }
   }
}
