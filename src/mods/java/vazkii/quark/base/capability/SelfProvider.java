package vazkii.quark.base.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;

public class SelfProvider<V> implements ICapabilityProvider {
   private final Capability<V> capability;
   private final V self;

   public SelfProvider(Capability<V> capability, V self) {
      this.capability = capability;
      this.self = self;
   }

   public static <V> SelfProvider<V> provide(Capability<V> capability, Object self) {
      return new SelfProvider<V>(capability, self);
   }

   public static <V> void attachItem(ResourceLocation location, Capability<V> capability, AttachCapabilitiesEvent<ItemStack> event) {
      event.addCapability(location, provide(capability, ((ItemStack)event.getObject()).getItem()));
   }

   public static <V, C extends ICapabilityProvider> void attach(ResourceLocation location, Capability<V> capability, AttachCapabilitiesEvent<C> event) {
      event.addCapability(location, provide(capability, event.getObject()));
   }

   public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
      return capability == this.capability;
   }

   @Nullable
   public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
      return (T)(capability == this.capability ? this.capability.cast(this.self) : null);
   }
}
