package mezz.jei.network.packets;

import mezz.jei.JustEnoughItems;
import mezz.jei.network.IPacketId;
import mezz.jei.network.PacketIdServer;
import mezz.jei.startup.ProxyCommon;
import mezz.jei.util.CommandUtilServer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

public class PacketRequestCheatPermission extends PacketJei {
   public PacketRequestCheatPermission() {
   }

   public IPacketId getPacketId() {
      return PacketIdServer.CHEAT_PERMISSION_REQUEST;
   }

   public void writePacketData(PacketBuffer buf) {
   }

   public static void readPacketData(PacketBuffer buf, EntityPlayer player) {
      if (player instanceof EntityPlayerMP) {
         EntityPlayerMP sender = (EntityPlayerMP)player;
         boolean hasPermission = CommandUtilServer.hasPermission(sender, new ItemStack(Items.NETHER_STAR, 64));
         PacketCheatPermission packetCheatPermission = new PacketCheatPermission(hasPermission);
         ProxyCommon proxy = JustEnoughItems.getProxy();
         proxy.sendPacketToClient(packetCheatPermission, sender);
      }

   }
}
