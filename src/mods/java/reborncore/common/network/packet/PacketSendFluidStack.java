package reborncore.common.network.packet;

import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import reborncore.client.containerBuilder.builder.BuiltContainer;
import reborncore.client.gui.builder.GuiBase;
import reborncore.common.network.ExtendedPacketBuffer;
import reborncore.common.network.INetworkPacket;

public class PacketSendFluidStack implements INetworkPacket<PacketSendFluidStack> {
   int id;
   FluidStack value;
   String container;

   public PacketSendFluidStack() {
   }

   public PacketSendFluidStack(int id, FluidStack value) {
      this.id = id;
      this.value = value;
   }

   public PacketSendFluidStack(int id, FluidStack value, Container container) {
      this.id = id;
      this.value = value;
      this.container = container.getClass().getName();
   }

   public void writeData(ExtendedPacketBuffer buffer) throws IOException {
      buffer.writeInt(this.id);
      buffer.writeFluidStack(this.value);
      buffer.writeInt(this.container.length());
      buffer.writeString(this.container);
   }

   public void readData(ExtendedPacketBuffer buffer) throws IOException {
      this.id = buffer.readInt();
      this.value = buffer.readFluidStack();
      this.container = buffer.readString(buffer.readInt());
   }

   public void processData(PacketSendFluidStack message, MessageContext context) {
      this.handle();
   }

   @SideOnly(Side.CLIENT)
   public void handle() {
      GuiScreen gui = Minecraft.getMinecraft().currentScreen;
      if (gui instanceof GuiBase) {
         BuiltContainer container = ((GuiBase)gui).container;
         if (container != null) {
            container.handleFluidStack(this.id, this.value);
         }
      }

   }
}
