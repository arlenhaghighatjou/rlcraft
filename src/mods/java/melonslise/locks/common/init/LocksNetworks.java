package melonslise.locks.common.init;

import melonslise.locks.Locks;
import melonslise.locks.common.network.LocksGuiHandler;
import melonslise.locks.common.network.toclient.AddLockablePacket;
import melonslise.locks.common.network.toclient.CheckPinResultPacket;
import melonslise.locks.common.network.toclient.ConfigSyncPacket;
import melonslise.locks.common.network.toclient.RemoveLockablePacket;
import melonslise.locks.common.network.toclient.UpdateLockablePacket;
import melonslise.locks.common.network.toserver.CheckPinPacket;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public final class LocksNetworks {
   public static final SimpleNetworkWrapper MAIN = NetworkRegistry.INSTANCE.newSimpleChannel("locks");

   private LocksNetworks() {
   }

   public static void register() {
      NetworkRegistry.INSTANCE.registerGuiHandler(Locks.instance, new LocksGuiHandler());
      MAIN.registerMessage(new AddLockablePacket.Handler(), AddLockablePacket.class, AddLockablePacket::new, 0, Side.CLIENT);
      MAIN.registerMessage(new RemoveLockablePacket.Handler(), RemoveLockablePacket.class, RemoveLockablePacket::new, 1, Side.CLIENT);
      MAIN.registerMessage(new UpdateLockablePacket.Handler(), UpdateLockablePacket.class, UpdateLockablePacket::new, 2, Side.CLIENT);
      MAIN.registerMessage(new CheckPinPacket.Handler(), CheckPinPacket.class, CheckPinPacket::new, 3, Side.SERVER);
      MAIN.registerMessage(new CheckPinResultPacket.Handler(), CheckPinResultPacket.class, CheckPinResultPacket::new, 4, Side.CLIENT);
      MAIN.registerMessage(new ConfigSyncPacket.Handler(), ConfigSyncPacket.class, ConfigSyncPacket::new, 5, Side.CLIENT);
   }
}
