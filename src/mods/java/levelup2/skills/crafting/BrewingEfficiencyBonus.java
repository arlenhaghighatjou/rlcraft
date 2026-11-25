package levelup2.skills.crafting;

import levelup2.api.IProcessor;
import levelup2.capability.CapabilityBrewingStand;
import levelup2.capability.PlayerCapability;
import levelup2.skills.BaseSkill;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BrewingEfficiencyBonus extends BaseSkill {
   public BrewingEfficiencyBonus() {
   }

   public String getSkillName() {
      return "levelup:brewingspeed";
   }

   public byte getSkillType() {
      return 1;
   }

   public boolean hasSubscription() {
      return true;
   }

   public ItemStack getRepresentativeStack() {
      return new ItemStack(Items.BREWING_STAND);
   }

   @SubscribeEvent
   public void registerStandCapability(AttachCapabilitiesEvent<TileEntity> evt) {
      if (evt.getObject() instanceof TileEntityBrewingStand) {
         final TileEntityBrewingStand stand = (TileEntityBrewingStand)evt.getObject();
         evt.addCapability(new ResourceLocation("levelup", "furnacemods"), new ICapabilitySerializable<NBTTagCompound>() {
            IProcessor instance = new CapabilityBrewingStand(stand);

            public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
               return capability == PlayerCapability.MACHINE_PROCESSING;
            }

            public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
               return (T)(capability == PlayerCapability.MACHINE_PROCESSING ? PlayerCapability.MACHINE_PROCESSING.cast(this.instance) : null);
            }

            public NBTTagCompound serializeNBT() {
               return (NBTTagCompound)PlayerCapability.MACHINE_PROCESSING.getStorage().writeNBT(PlayerCapability.MACHINE_PROCESSING, this.instance, (EnumFacing)null);
            }

            public void deserializeNBT(NBTTagCompound tag) {
               PlayerCapability.MACHINE_PROCESSING.getStorage().readNBT(PlayerCapability.MACHINE_PROCESSING, this.instance, (EnumFacing)null, tag);
            }
         });
      }

   }
}
