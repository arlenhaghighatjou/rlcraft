package ivorius.reccomplex.capability;

import io.netty.buffer.ByteBuf;
import ivorius.ivtoolkit.blocks.BlockPositions;
import ivorius.ivtoolkit.network.IvNetworkHelperServer;
import ivorius.ivtoolkit.network.PartialUpdateHandler;
import ivorius.ivtoolkit.tools.NBTCompoundObject;
import ivorius.reccomplex.RecurrentComplex;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class CapabilitySelection implements NBTCompoundObject, PartialUpdateHandler, SelectionOwner {
   public static final String CAPABILITY_KEY = "rc_selection";
   @CapabilityInject(CapabilitySelection.class)
   public static Capability<CapabilitySelection> CAPABILITY;
   private boolean hasChanges;
   public BlockPos selectedPoint1;
   public BlockPos selectedPoint2;

   @Nullable
   public static CapabilitySelection get(ICapabilityProvider provider, @Nullable EnumFacing facing) {
      return (CapabilitySelection)provider.getCapability(CAPABILITY, (EnumFacing)null);
   }

   public CapabilitySelection() {
   }

   public CapabilitySelection(BlockPos selectedPoint1, BlockPos selectedPoint2) {
      this.selectedPoint1 = selectedPoint1;
      this.selectedPoint2 = selectedPoint2;
   }

   @Nullable
   public BlockPos getSelectedPoint1() {
      return this.selectedPoint1;
   }

   public void setSelectedPoint1(@Nullable BlockPos pos) {
      this.selectedPoint1 = pos;
      this.hasChanges = true;
   }

   @Nullable
   public BlockPos getSelectedPoint2() {
      return this.selectedPoint2;
   }

   public void setSelectedPoint2(@Nullable BlockPos pos) {
      this.selectedPoint2 = pos;
      this.hasChanges = true;
   }

   public void sendSelectionToClients(Entity entity) {
      if (!entity.world.isRemote && !RecurrentComplex.isLite()) {
         IvNetworkHelperServer.sendEEPUpdatePacket(entity, "rc_selection", (EnumFacing)null, "selection", RecurrentComplex.network, new Object[0]);
      }

   }

   public void writeToNBT(NBTTagCompound compound) {
      BlockPositions.writeToNBT("selectedPoint1", this.selectedPoint1, compound);
      BlockPositions.writeToNBT("selectedPoint2", this.selectedPoint2, compound);
   }

   public void readFromNBT(NBTTagCompound compound) {
      this.selectedPoint1 = BlockPositions.readFromNBT("selectedPoint1", compound);
      this.selectedPoint2 = BlockPositions.readFromNBT("selectedPoint2", compound);
      this.hasChanges = true;
   }

   public void update(Entity entity) {
      if (this.hasChanges) {
         this.sendChanges(entity);
      }

   }

   public void sendChanges(Entity entity) {
      this.hasChanges = false;
      this.sendSelectionToClients(entity);
   }

   public void writeUpdateData(ByteBuf buffer, String context, Object... params) {
      if ("selection".equals(context)) {
         BlockPositions.maybeWriteToBuffer(this.selectedPoint1, buffer);
         BlockPositions.maybeWriteToBuffer(this.selectedPoint2, buffer);
      }

   }

   public void readUpdateData(ByteBuf buffer, String context) {
      if ("selection".equals(context)) {
         this.selectedPoint1 = BlockPositions.maybeReadFromBuffer(buffer);
         this.selectedPoint2 = BlockPositions.maybeReadFromBuffer(buffer);
      }

   }
}
