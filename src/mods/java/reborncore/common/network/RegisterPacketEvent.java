package reborncore.common.network;

import java.lang.reflect.Constructor;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class RegisterPacketEvent extends Event {
   public RegisterPacketEvent() {
   }

   public void registerPacket(Class<? extends INetworkPacket> packet, Side processingSide) {
      int id = getNextID(packet);
      if (packet.getName() == INetworkPacket.class.getName()) {
         throw new RuntimeException("Cannot register a INetworkPacket, please register a child of this");
      } else {
         boolean hasEmptyConstructor = false;

         for(Constructor constructor : packet.getConstructors()) {
            if (constructor.getParameterCount() == 0) {
               hasEmptyConstructor = true;
            }
         }

         if (!hasEmptyConstructor) {
            throw new RuntimeException("The packet " + packet.getName() + " does not have an empty constructor");
         } else {
            NetworkManager.registerPacket(packet, processingSide);
         }
      }
   }

   public static int getNextID(Class<? extends INetworkPacket> packet) {
      SimpleNetworkWrapper wrapper = NetworkManager.getWrapperForPacket(packet);
      return wrapper == null ? 0 : NetworkManager.getNextIDForWrapper(wrapper);
   }
}
