package ivorius.reccomplex.capability;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import ivorius.ivtoolkit.network.IvNetworkHelperServer;
import ivorius.ivtoolkit.network.PartialUpdateHandler;
import ivorius.ivtoolkit.tools.NBTCompoundObject;
import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.operation.Operation;
import ivorius.reccomplex.operation.OperationRegistry;
import ivorius.reccomplex.utils.RCPacketBuffer;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class RCEntityInfo implements NBTCompoundObject, PartialUpdateHandler {
   public static final String CAPABILITY_KEY = "structureEntityInfo";
   @CapabilityInject(RCEntityInfo.class)
   public static Capability<RCEntityInfo> CAPABILITY;
   private boolean hasChanges;
   private Operation.PreviewType previewType = Operation.PreviewType.SHAPE;
   public Operation danglingOperation;
   public boolean showGrid = false;
   private NBTTagCompound cachedExportStructureBlockDataNBT;
   private NBTTagCompound worldDataClipboard;

   public RCEntityInfo() {
   }

   @Nullable
   public static RCEntityInfo get(Object object, @Nullable EnumFacing facing) {
      if (object instanceof RCEntityInfo) {
         return (RCEntityInfo)object;
      } else {
         return object instanceof ICapabilityProvider ? (RCEntityInfo)((ICapabilityProvider)object).getCapability(CAPABILITY, facing) : null;
      }
   }

   public Operation.PreviewType getPreviewType() {
      return RecurrentComplex.isLite() ? Operation.PreviewType.NONE : this.previewType;
   }

   public void setPreviewType(Operation.PreviewType previewType) {
      this.previewType = previewType;
   }

   public void sendPreviewTypeToClients(Entity entity) {
      if (!entity.world.isRemote && !RecurrentComplex.isLite()) {
         IvNetworkHelperServer.sendEEPUpdatePacket(entity, "structureEntityInfo", (EnumFacing)null, "previewType", RecurrentComplex.network, new Object[0]);
      }

   }

   public void sendOperationToClients(Entity entity) {
      if (!entity.world.isRemote && !RecurrentComplex.isLite()) {
         IvNetworkHelperServer.sendEEPUpdatePacket(entity, "structureEntityInfo", (EnumFacing)null, "operation", RecurrentComplex.network, new Object[0]);
      }

   }

   public void sendOptionsToClients(Entity entity) {
      if (!entity.world.isRemote && !RecurrentComplex.isLite()) {
         IvNetworkHelperServer.sendEEPUpdatePacket(entity, "structureEntityInfo", (EnumFacing)null, "options", RecurrentComplex.network, new Object[0]);
      }

   }

   public NBTTagCompound getCachedExportStructureBlockDataNBT() {
      return this.cachedExportStructureBlockDataNBT;
   }

   public void setCachedExportStructureBlockDataNBT(NBTTagCompound cachedExportStructureBlockDataNBT) {
      this.cachedExportStructureBlockDataNBT = cachedExportStructureBlockDataNBT;
   }

   public NBTTagCompound getWorldDataClipboard() {
      return this.worldDataClipboard;
   }

   public void setWorldDataClipboard(NBTTagCompound worldDataClipboard) {
      this.worldDataClipboard = worldDataClipboard;
   }

   public void queueOperation(Operation operation, Entity owner) {
      this.danglingOperation = operation;
      this.sendOperationToClients(owner);
   }

   public boolean performOperation(WorldServer world, Entity owner) {
      if (this.danglingOperation != null) {
         this.danglingOperation.perform(world);
         this.danglingOperation = null;
         this.sendOperationToClients(owner);
         return true;
      } else {
         return false;
      }
   }

   public boolean cancelOperation(World world, Entity owner) {
      if (this.danglingOperation != null) {
         this.danglingOperation = null;
         this.sendOperationToClients(owner);
         return true;
      } else {
         return false;
      }
   }

   public void writeToNBT(NBTTagCompound compound) {
      compound.setString("previewType", this.previewType.key);
      if (RCConfig.savePlayerCache) {
         if (this.danglingOperation != null) {
            compound.setTag("danglingOperation", OperationRegistry.writeOperation(this.danglingOperation));
         }

         if (this.worldDataClipboard != null) {
            compound.setTag("worldDataClipboard", this.worldDataClipboard);
         }
      }

      compound.setBoolean("showGrid", this.showGrid);
   }

   public void readFromNBT(NBTTagCompound compound) {
      this.previewType = Operation.PreviewType.findOrDefault(compound.getString("previewType"), Operation.PreviewType.SHAPE);
      if (RCConfig.savePlayerCache) {
         if (compound.hasKey("danglingOperation", 10)) {
            this.danglingOperation = OperationRegistry.readOperation(compound.getCompoundTag("danglingOperation"));
         }

         if (compound.hasKey("worldDataClipboard", 10)) {
            this.worldDataClipboard = compound.getCompoundTag("worldDataClipboard");
         }
      }

      this.showGrid = compound.getBoolean("showGrid");
      this.hasChanges = true;
   }

   public void update(Entity entity) {
      if (this.danglingOperation != null) {
         this.danglingOperation.update(entity.world, entity.ticksExisted);
         if (this.danglingOperation.checkDead(entity)) {
            this.danglingOperation = null;
            this.hasChanges = true;
         }
      }

      if (this.hasChanges) {
         this.sendChanges(entity);
      }

   }

   public void sendChanges(Entity entity) {
      this.hasChanges = false;
      this.sendPreviewTypeToClients(entity);
      this.sendOperationToClients(entity);
      this.sendOptionsToClients(entity);
   }

   public void writeUpdateData(ByteBuf buffer, String context, Object... params) {
      if ("previewType".equals(context)) {
         ByteBufUtils.writeUTF8String(buffer, this.previewType.key);
      } else if ("operation".equals(context)) {
         OperationRegistry.writeBestPreview(buffer, this.danglingOperation);
      } else if ("options".equals(context)) {
         buffer.writeBoolean(this.showGrid);
      }

   }

   public void readUpdateData(ByteBuf buffer, String context) {
      if ("previewType".equals(context)) {
         this.previewType = Operation.PreviewType.findOrDefault(ByteBufUtils.readUTF8String(buffer), Operation.PreviewType.SHAPE);
      } else if ("operation".equals(context)) {
         try {
            NBTTagCompound tag = (new RCPacketBuffer(buffer)).readBigTag();
            this.danglingOperation = tag != null ? OperationRegistry.readOperation(tag) : null;
         } catch (Exception var4) {
            RecurrentComplex.logger.warn("Error reading operation tag", var4);
            this.danglingOperation = null;
         }
      } else if ("options".equals(context)) {
         this.showGrid = buffer.readBoolean();
      }

   }
}
