package baubles.common.event;

import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandlerItem {
   private static ResourceLocation capabilityResourceLocation = new ResourceLocation("baubles", "bauble_cap");

   public EventHandlerItem() {
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void itemCapabilityAttach(AttachCapabilitiesEvent<ItemStack> event) {
      final ItemStack stack = (ItemStack)event.getObject();
      if (!stack.isEmpty() && stack.getItem() instanceof IBauble && !stack.hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, (EnumFacing)null) && !event.getCapabilities().values().stream().anyMatch((c) -> {
         return (void)c.hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, (EnumFacing)null);
      })) {
         event.addCapability(capabilityResourceLocation, new ICapabilityProvider() {
            public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
               return capability == BaublesCapabilities.CAPABILITY_ITEM_BAUBLE;
            }

            @Nullable
            public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
               return (T)(capability == BaublesCapabilities.CAPABILITY_ITEM_BAUBLE ? BaublesCapabilities.CAPABILITY_ITEM_BAUBLE.cast((IBauble)stack.getItem()) : null);
            }
         });
      }
   }
}
