package xzeroair.trinkets.capabilities;

import javax.annotation.Nullable;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class CapabilityProviderBase<Handler extends CapabilityBase<Handler, E>, E> implements ICapabilitySerializable<NBTBase> {
   protected final Capability<Handler> capability;
   protected final EnumFacing facing;
   protected final Handler handler;

   public CapabilityProviderBase(Capability<Handler> capability, @Nullable Handler handler) {
      this(capability, (EnumFacing)null, handler);
   }

   public CapabilityProviderBase(Capability<Handler> capability, @Nullable EnumFacing facing, @Nullable Handler handler) {
      this.capability = capability;
      this.facing = facing;
      this.handler = handler;
   }

   public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return capability == this.getCapability();
   }

   public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      return (T)(capability == this.getCapability() ? this.getCapability().cast(this.getInstance()) : null);
   }

   public NBTBase serializeNBT() {
      return this.getCapability().writeNBT(this.getInstance(), this.getFacing());
   }

   public void deserializeNBT(NBTBase nbt) {
      this.getCapability().readNBT(this.getInstance(), this.getFacing(), nbt);
   }

   public final Capability<Handler> getCapability() {
      return this.capability;
   }

   @Nullable
   public EnumFacing getFacing() {
      return this.facing;
   }

   public Handler getInstance() {
      return this.handler;
   }
}
