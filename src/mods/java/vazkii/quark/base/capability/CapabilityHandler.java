package vazkii.quark.base.capability;

import java.util.concurrent.Callable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.api.ICustomEnchantColor;
import vazkii.quark.api.ICustomSorting;
import vazkii.quark.api.IDropoffManager;
import vazkii.quark.api.IPistonCallback;
import vazkii.quark.api.capability.IBoatBanner;
import vazkii.quark.api.capability.IEnchantColorProvider;
import vazkii.quark.api.capability.ISearchHandler;

@EventBusSubscriber(
   modid = "quark"
)
public class CapabilityHandler {
   private static final ResourceLocation PISTON_CALLBACK = new ResourceLocation("quark", "piston");
   private static final ResourceLocation ENCHANT_COLOR = new ResourceLocation("quark", "glint");
   private static final ResourceLocation SEARCH_HANDLER = new ResourceLocation("quark", "search");
   private static final ResourceLocation DROPOFF_MANAGER = new ResourceLocation("quark", "dropoff");
   private static final ResourceLocation SORTING_HANDLER = new ResourceLocation("quark", "sort");

   public CapabilityHandler() {
   }

   public static void register() {
      registerLambda(IEnchantColorProvider.class, () -> {
         return -1;
      });
      registerLambda(IPistonCallback.class, () -> {
      });
      registerLambda(ISearchHandler.class, (query, matcher, search) -> {
         return false;
      });
      registerLambda(IDropoffManager.class, (player) -> {
         return false;
      });
      register(IBoatBanner.class, BoatBanner::new);
      register(ICustomSorting.class, DummySorting::new);
   }

   private static <T> void registerLambda(Class<T> clazz, T provider) {
      register(clazz, () -> {
         return (void)provider;
      });
   }

   private static <T> void register(Class<T> clazz, Callable<T> provider) {
      CapabilityManager.INSTANCE.register(clazz, new CapabilityHandler.CapabilityFactory(), provider);
   }

   @SubscribeEvent
   public static void attachItemCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
      Item item = ((ItemStack)event.getObject()).getItem();
      if (item instanceof ICustomEnchantColor) {
         event.addCapability(ENCHANT_COLOR, new EnchantColorWrapper((ItemStack)event.getObject()));
      }

      if (item instanceof ISearchHandler) {
         event.addCapability(SEARCH_HANDLER, new SearchWrapper((ItemStack)event.getObject()));
      }

      if (item instanceof ICustomSorting) {
         SelfProvider.attachItem(SORTING_HANDLER, ICustomSorting.CAPABILITY, event);
      }

   }

   @SubscribeEvent
   public static void attachTileCapabilities(AttachCapabilitiesEvent<TileEntity> event) {
      if (event.getObject() instanceof IPistonCallback) {
         SelfProvider.attach(PISTON_CALLBACK, IPistonCallback.CAPABILITY, event);
      }

      if (event.getObject() instanceof IDropoffManager) {
         SelfProvider.attach(DROPOFF_MANAGER, IDropoffManager.CAPABILITY, event);
      }

   }

   private static class CapabilityFactory<T> implements IStorage<T> {
      private CapabilityFactory() {
      }

      public NBTBase writeNBT(Capability<T> capability, T instance, EnumFacing side) {
         return instance instanceof INBTSerializable ? ((INBTSerializable)instance).serializeNBT() : null;
      }

      public void readNBT(Capability<T> capability, T instance, EnumFacing side, NBTBase nbt) {
         if (nbt instanceof NBTTagCompound) {
            ((INBTSerializable)instance).deserializeNBT(nbt);
         }

      }
   }
}
