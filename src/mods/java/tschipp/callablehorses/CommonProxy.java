package tschipp.callablehorses;

import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import tschipp.callablehorses.client.gui.GuiHandler;
import tschipp.callablehorses.common.WhistleSounds;
import tschipp.callablehorses.common.capabilities.horseowner.HorseOwner;
import tschipp.callablehorses.common.capabilities.horseowner.HorseOwnerStorage;
import tschipp.callablehorses.common.capabilities.horseowner.IHorseOwner;
import tschipp.callablehorses.common.capabilities.storedhorse.HorseStorage;
import tschipp.callablehorses.common.capabilities.storedhorse.IStoredHorse;
import tschipp.callablehorses.common.capabilities.storedhorse.StoredHorse;
import tschipp.callablehorses.network.HorseCapSyncPacket;
import tschipp.callablehorses.network.OwnerSyncShowStatsPacket;
import tschipp.callablehorses.common.worlddata.StoredHorsesWorldData;
import tschipp.callablehorses.network.PressKeyPacket;

public class CommonProxy {
   public CommonProxy() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      CallableHorses.network = NetworkRegistry.INSTANCE.newSimpleChannel("CallableHorses");
      CallableHorses.network.registerMessage(new PressKeyPacket(), PressKeyPacket.class, PressKeyPacket::new, 0, Side.SERVER);
      CallableHorses.network.registerMessage(new HorseCapSyncPacket(), HorseCapSyncPacket.class, HorseCapSyncPacket::new, 1, Side.CLIENT);
      CallableHorses.network.registerMessage(new OwnerSyncShowStatsPacket(), OwnerSyncShowStatsPacket.class, OwnerSyncShowStatsPacket::new, 2, Side.CLIENT);
      WhistleSounds.registerSounds();
      MapStorage.storageProviders.put(StoredHorsesWorldData.class, StoredHorsesWorldData::new);
   }

   public void init(FMLInitializationEvent event) {
      CapabilityManager.INSTANCE.register(IHorseOwner.class, new HorseOwnerStorage(), HorseOwner::new);
      CapabilityManager.INSTANCE.register(IStoredHorse.class, new HorseStorage(), StoredHorse::new);
      NetworkRegistry.INSTANCE.registerGuiHandler(CallableHorses.instance, new GuiHandler());
   }

   public void postInit(FMLPostInitializationEvent e) {
   }
}
