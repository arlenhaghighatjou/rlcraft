package cursedflames.bountifulbaubles.item;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import cursedflames.bountifulbaubles.BountifulBaubles;
import cursedflames.bountifulbaubles.capability.CLEnergyStorage;
import cursedflames.bountifulbaubles.item.base.AGenericItemBauble;
import cursedflames.bountifulbaubles.util.Config;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemRingFlywheel extends AGenericItemBauble {
   @CapabilityInject(IEnergyStorage.class)
   static Capability<IEnergyStorage> ENERGY_STORAGE_CAPABILITY = null;
   Property rfCapacity;

   public ItemRingFlywheel(String name, int defaultCapacity) {
      super(name, BountifulBaubles.TAB);
      BountifulBaubles.registryHelper.addItemModel(this);
      this.addPropertyOverride(new ResourceLocation("bountifulbaubles", "charge"), new IItemPropertyGetter() {
         @SideOnly(Side.CLIENT)
         public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
            IEnergyStorage storage = (IEnergyStorage)stack.getCapability(ItemRingFlywheel.ENERGY_STORAGE_CAPABILITY, (EnumFacing)null);
            return storage == null ? 0.0F : (float)storage.getEnergyStored() / (float)storage.getMaxEnergyStored();
         }
      });
      Property unsynced = BountifulBaubles.config.addPropInt(this.getRegistryName() + ".charge", "Items", "Maximum energy stored in a " + this.getRegistryName(), defaultCapacity, Config.EnumPropSide.SYNCED, 1, Integer.MAX_VALUE);
      unsynced.setRequiresWorldRestart(true);
      this.rfCapacity = BountifulBaubles.config.getSyncedProperty(this.getRegistryName() + ".charge");
   }

   public BaubleType getBaubleType(ItemStack arg0) {
      return BaubleType.RING;
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      super.addInformation(stack, worldIn, tooltip, flagIn);
      IEnergyStorage e = (IEnergyStorage)stack.getCapability(ENERGY_STORAGE_CAPABILITY, (EnumFacing)null);
      if (e != null) {
         int energy = e.getEnergyStored();
         int max = e.getMaxEnergyStored();
         String color = energy == 0 ? "\ufffd4" : (energy < max / 4 ? "\ufffdc" : (energy < max / 2 ? "\ufffde" : "\ufffda"));
         tooltip.add(color + String.valueOf(energy) + "/" + max + "RF");
      }

   }

   public NBTTagCompound getNBTShareTag(ItemStack stack) {
      NBTTagCompound tag = super.getNBTShareTag(stack);
      IEnergyStorage e = (IEnergyStorage)stack.getCapability(ENERGY_STORAGE_CAPABILITY, (EnumFacing)null);
      if (tag == null) {
         tag = new NBTTagCompound();
      }

      if (e != null) {
         tag.setInteger("energyAmount", e.getEnergyStored());
      }

      return tag;
   }

   public boolean showDurabilityBar(ItemStack stack) {
      IEnergyStorage e = (IEnergyStorage)stack.getCapability(ENERGY_STORAGE_CAPABILITY, (EnumFacing)null);
      return e != null && e.getEnergyStored() < e.getMaxEnergyStored();
   }

   public double getDurabilityForDisplay(ItemStack stack) {
      IEnergyStorage e = (IEnergyStorage)stack.getCapability(ENERGY_STORAGE_CAPABILITY, (EnumFacing)null);
      return e == null ? 1.0D : 1.0D - (double)e.getEnergyStored() / (double)e.getMaxEnergyStored();
   }

   public int getRGBDurabilityForDisplay(ItemStack stack) {
      return 13635600;
   }

   public boolean isDamageable() {
      return false;
   }

   public void onWornTick(ItemStack stack, EntityLivingBase player) {
      this.chargeItems(stack, player);
   }

   private void chargeItems(ItemStack stack, Entity entity) {
      if (entity instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entity;

         for(ItemStack stack1 : player.inventory.mainInventory) {
            if (this.tryChargeItem(stack1, stack)) {
               return;
            }
         }

         for(ItemStack stack1 : player.inventory.armorInventory) {
            if (this.tryChargeItem(stack1, stack)) {
               return;
            }
         }

         for(ItemStack stack1 : player.inventory.offHandInventory) {
            if (this.tryChargeItem(stack1, stack)) {
               return;
            }
         }

         IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);

         for(int i = 0; i < baubles.getSlots(); ++i) {
            if (this.tryChargeItem(baubles.getStackInSlot(i), stack)) {
               return;
            }
         }

      }
   }

   private boolean tryChargeItem(ItemStack stack, ItemStack flyWheel) {
      IEnergyStorage e = (IEnergyStorage)flyWheel.getCapability(ENERGY_STORAGE_CAPABILITY, (EnumFacing)null);
      if (e != null && e.getEnergyStored() != 0) {
         if (!(stack.getItem() instanceof ItemRingFlywheel) && stack.hasCapability(ENERGY_STORAGE_CAPABILITY, (EnumFacing)null)) {
            IEnergyStorage e2 = (IEnergyStorage)stack.getCapability(ENERGY_STORAGE_CAPABILITY, (EnumFacing)null);
            int transfer = Math.min(e.extractEnergy(Integer.MAX_VALUE, true), e2.receiveEnergy(Integer.MAX_VALUE, true));
            e.extractEnergy(transfer, false);
            e2.receiveEnergy(transfer, false);
         }

         return false;
      } else {
         return true;
      }
   }

   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> stacks) {
      if (this.isInCreativeTab(tab)) {
         stacks.add(new ItemStack(this));
         ItemStack charged = new ItemStack(this);
         IEnergyStorage e = (IEnergyStorage)charged.getCapability(ENERGY_STORAGE_CAPABILITY, (EnumFacing)null);
         if (e != null) {
            ((CLEnergyStorage)e).setEnergyStored(e.getMaxEnergyStored());
            stacks.add(charged);
         }
      }

   }

   public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound capability) {
      return new ItemRingFlywheel.Provider(stack, capability);
   }

   public static class Provider implements ICapabilitySerializable<NBTTagCompound> {
      public static final ResourceLocation NAME = new ResourceLocation("bountifulbaubles", "energy_storage");
      private final IEnergyStorage cap;

      public Provider(ItemStack stack, NBTTagCompound capability) {
         int capacity = ((ItemRingFlywheel)stack.getItem()).rfCapacity.getInt();
         this.cap = new CLEnergyStorage(capacity, capacity / 100, capacity / 100, 0);
         this.deserializeNBT(capability);
      }

      public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
         return capability == ItemRingFlywheel.ENERGY_STORAGE_CAPABILITY;
      }

      public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
         return (T)(capability == ItemRingFlywheel.ENERGY_STORAGE_CAPABILITY ? this.cap : null);
      }

      public NBTTagCompound serializeNBT() {
         NBTTagCompound nbt = new NBTTagCompound();
         nbt.setInteger("amount", this.cap.getEnergyStored());
         return nbt;
      }

      public void deserializeNBT(NBTTagCompound nbt) {
         if (nbt != null && nbt.hasKey("amount")) {
            ((CLEnergyStorage)this.cap).setEnergyStored(nbt.getInteger("amount"));
         }

      }
   }
}
