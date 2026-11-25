package gigaherz.toolbelt.customslots;

import com.google.common.collect.ImmutableSet;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class CapabilityExtensionSlotItem {
   public static final ResourceLocation ANY_SLOT = new ResourceLocation("forge:any");
   public static final ImmutableSet<ResourceLocation> ANY_SLOT_LIST = ImmutableSet.of(ANY_SLOT);
   @CapabilityInject(IExtensionSlotItem.class)
   public static Capability<IExtensionSlotItem> INSTANCE = null;

   public CapabilityExtensionSlotItem() {
   }

   public static void register() {
      CapabilityManager.INSTANCE.register(IExtensionSlotItem.class, new CapabilityExtensionSlotItem.Storage(), CapabilityExtensionSlotItem.DefaultImplementation.class);
   }

   private static class DefaultImplementation implements IExtensionSlotItem {
      private DefaultImplementation() {
      }
   }

   static class Storage implements IStorage<IExtensionSlotItem> {
      Storage() {
      }

      @Nullable
      public NBTBase writeNBT(Capability<IExtensionSlotItem> capability, IExtensionSlotItem instance, EnumFacing side) {
         return null;
      }

      public void readNBT(Capability<IExtensionSlotItem> capability, IExtensionSlotItem instance, EnumFacing side, NBTBase nbt) {
      }
   }
}
