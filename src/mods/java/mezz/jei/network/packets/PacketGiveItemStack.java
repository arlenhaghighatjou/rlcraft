package mezz.jei.network.packets;

import java.io.IOException;
import mezz.jei.network.IPacketId;
import mezz.jei.network.PacketIdServer;
import mezz.jei.util.CommandUtilServer;
import mezz.jei.util.GiveMode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

public class PacketGiveItemStack extends PacketJei {
   private final ItemStack itemStack;
   private final GiveMode giveMode;

   public PacketGiveItemStack(ItemStack itemStack, GiveMode giveMode) {
      this.itemStack = itemStack;
      this.giveMode = giveMode;
   }

   public IPacketId getPacketId() {
      return PacketIdServer.GIVE_ITEM;
   }

   public void writePacketData(PacketBuffer buf) {
      NBTTagCompound nbt = this.itemStack.serializeNBT();
      buf.writeCompoundTag(nbt);
      buf.writeEnumValue(this.giveMode);
   }

   public static void readPacketData(PacketBuffer buf, EntityPlayer player) throws IOException {
      if (player instanceof EntityPlayerMP) {
         EntityPlayerMP sender = (EntityPlayerMP)player;
         NBTTagCompound itemStackSerialized = buf.readCompoundTag();
         if (itemStackSerialized != null) {
            GiveMode giveMode = (GiveMode)buf.readEnumValue(GiveMode.class);
            ItemStack itemStack = new ItemStack(itemStackSerialized);
            if (!itemStack.isEmpty()) {
               CommandUtilServer.executeGive(sender, itemStack, giveMode);
            }
         }
      }

   }
}
