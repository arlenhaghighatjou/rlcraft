package xzeroair.trinkets.blocks.tileentities;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.capabilities.TileEntityCap.TileEntityProperties;

public class TileEntityMoonRose extends TileEntity implements ITickable {
   public TileEntityMoonRose() {
   }

   public void update() {
      if (this.world != null) {
         Capabilities.getTEProperties(this, TileEntityProperties::onUpdate);
      }

   }

   public NBTTagCompound getUpdateTag() {
      return (NBTTagCompound)Capabilities.getTEProperties(this, super.getUpdateTag(), TileEntityProperties::saveToNBT);
   }

   public void handleUpdateTag(NBTTagCompound tag) {
      super.handleUpdateTag(tag);
      Capabilities.getTEProperties(this, (prop) -> {
         prop.loadFromNBT(tag);
      });
   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      return (NBTTagCompound)Capabilities.getTEProperties(this, super.writeToNBT(compound), TileEntityProperties::saveToNBT);
   }

   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      Capabilities.getTEProperties(this, (prop) -> {
         prop.loadFromNBT(compound);
      });
   }
}
