package com.oblivioussp.spartanshields.item;

import cofh.redstoneflux.api.IEnergyContainerItem;
import com.oblivioussp.spartanshields.client.gui.CreativeTabsSS;
import com.oblivioussp.spartanshields.util.ConfigHandler;
import com.oblivioussp.spartanshields.util.EnergyCapabilityProviderItem;
import com.oblivioussp.spartanshields.util.EnumPowerUnit;
import com.oblivioussp.spartanshields.util.StringHelper;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Interface(
   iface = "cofh.redstoneflux.api.IEnergyContainerItem",
   modid = "redstoneflux"
)
public class ItemShieldPoweredFE extends ItemShieldBase implements IDamageShield, IItemPoweredFE, IEnergyContainerItem {
   protected int energyCapacity;
   protected int maxEnergyReceive;
   protected String modName;
   protected EnumPowerUnit preferredEnergyUnit;

   public ItemShieldPoweredFE(String unlocName, int capacity, int maxReceive, String modName, EnumPowerUnit preferredUnit) {
      super(unlocName);
      this.energyCapacity = capacity;
      this.maxEnergyReceive = maxReceive;
      this.modName = modName;
      this.preferredEnergyUnit = preferredUnit;
      if (!Loader.isModLoaded("redstoneflux")) {
         this.preferredEnergyUnit = EnumPowerUnit.ForgeEnergy;
      }

      this.setCreativeTab(CreativeTabsSS.TAB_SS);
      this.addPropertyOverride(new ResourceLocation("disabled"), new IItemPropertyGetter() {
         @SideOnly(Side.CLIENT)
         public float apply(ItemStack stack, World worldIn, EntityLivingBase entityIn) {
            if (!stack.hasTagCompound()) {
               stack.setTagCompound(new NBTTagCompound());
            }

            return stack.getTagCompound().getInteger("Energy") <= 0 ? 1.0F : 0.0F;
         }
      });
   }

   @SideOnly(Side.CLIENT)
   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
      if (this.isInCreativeTab(tab)) {
         ItemStack fullShield = new ItemStack(this);
         fullShield.setTagCompound(new NBTTagCompound());
         fullShield.getTagCompound().setInteger("Energy", this.energyCapacity);
         subItems.add(new ItemStack(this));
         subItems.add(fullShield);
      }

   }

   public void setDamage(ItemStack stack, int damage) {
      super.setDamage(stack, 0);
   }

   public double getDurabilityForDisplay(ItemStack stack) {
      if (!stack.hasTagCompound()) {
         stack.setTagCompound(new NBTTagCompound());
      }

      int energy = stack.getTagCompound().getInteger("Energy");
      return ((double)this.energyCapacity - (double)energy) / (double)this.energyCapacity;
   }

   public int getRGBDurabilityForDisplay(ItemStack stack) {
      return 6927359;
   }

   public int getMaxDamage() {
      return this.energyCapacity;
   }

   public boolean showDurabilityBar(ItemStack stack) {
      if (!stack.hasTagCompound()) {
         stack.setTagCompound(new NBTTagCompound());
      }

      int energy = stack.getTagCompound().getInteger("Energy");
      return energy < this.energyCapacity;
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      if (!Loader.isModLoaded(this.modName) && !ConfigHandler.forceDisableUncraftableTooltips) {
         tooltip.add(TextFormatting.RED + StringHelper.translateString("cantCraftMissingMods", "tooltip"));
         tooltip.add(TextFormatting.RED + StringHelper.translateString(this.modName.toLowerCase(), "mod"));
      }

      tooltip.add(StringHelper.translateFormattedString(this.preferredEnergyUnit.getCapacityUnlocalised(), "tooltip", MathHelper.floor((float)this.getFEStored(stack) * this.preferredEnergyUnit.getEnergyScaleToRF()), MathHelper.floor((float)this.getFECapacity(stack) * this.preferredEnergyUnit.getEnergyScaleToRF())));
      tooltip.add(StringHelper.translateFormattedString(this.preferredEnergyUnit.getEnergyPerDamageUnlocalised(), "tooltip", MathHelper.floor((float)(ConfigHandler.damageToFEMultiplier * 2) * this.preferredEnergyUnit.getEnergyScaleToRF())));
      tooltip.add(StringHelper.translateString("feShield.desc", "tooltip"));
      tooltip.add("");
      this.addShieldBashTooltip(stack, worldIn, tooltip, flagIn);
   }

   public EnumAction getItemUseAction(ItemStack stack) {
      return this.getFEStored(stack) > 0 ? EnumAction.BLOCK : EnumAction.NONE;
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
      playerIn.setActiveHand(hand);
      ItemStack stack = playerIn.getHeldItem(hand);
      if (this.getFEStored(stack) > 0) {
         return new ActionResult(EnumActionResult.SUCCESS, stack);
      } else {
         if (worldIn.isRemote) {
            playerIn.sendMessage(new TextComponentString(TextFormatting.YELLOW + StringHelper.translateString("poweredShieldBlockFail", "message")));
         }

         return new ActionResult(EnumActionResult.FAIL, stack);
      }
   }

   public void damageShield(ItemStack shieldStack, EntityPlayer player, Entity attacker, float damage) {
      int energyToUse = MathHelper.floor(damage * (float)ConfigHandler.damageToRFMultiplier);
      if (!shieldStack.hasTagCompound()) {
         shieldStack.setTagCompound(new NBTTagCompound());
      }

      int currentEnergy = shieldStack.getTagCompound().getInteger("Energy");
      int energyRemoved = Math.min(energyToUse, currentEnergy);
      currentEnergy = currentEnergy - energyRemoved;
      shieldStack.getTagCompound().setInteger("Energy", currentEnergy);
      if (currentEnergy == 0) {
         player.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8F, 0.8F + player.world.rand.nextFloat() * 0.4F);
      }

   }

   public ItemShieldPoweredFE setCapacity(int capacity) {
      this.energyCapacity = capacity;
      return this;
   }

   public ItemShieldPoweredFE setMaxReceive(int maxReceive) {
      this.maxEnergyReceive = maxReceive;
      return this;
   }

   public int receiveFE(ItemStack container, int maxReceive, boolean simulate) {
      if (!container.hasTagCompound()) {
         container.setTagCompound(new NBTTagCompound());
      }

      int energy = container.getTagCompound().getInteger("Energy");
      int energyReceived = Math.min(this.energyCapacity - energy, Math.min(this.maxEnergyReceive, maxReceive));
      if (!simulate) {
         energy = energy + energyReceived;
         container.getTagCompound().setInteger("Energy", energy);
      }

      return energyReceived;
   }

   public int extractFE(ItemStack container, int maxExtract, boolean simulate) {
      return 0;
   }

   public int getFEStored(ItemStack container) {
      return container.getTagCompound() != null && container.getTagCompound().hasKey("Energy") ? container.getTagCompound().getInteger("Energy") : 0;
   }

   public int getFECapacity(ItemStack container) {
      return this.energyCapacity;
   }

   public boolean canExtractFE(ItemStack stack) {
      return false;
   }

   public boolean canReceiveFE(ItemStack stack) {
      return true;
   }

   public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
      return new EnergyCapabilityProviderItem(stack, this);
   }

   public int receiveEnergy(ItemStack stack, int maxReceive, boolean simulate) {
      return this.receiveFE(stack, maxReceive, simulate);
   }

   public int extractEnergy(ItemStack stack, int maxExtract, boolean simulate) {
      return this.extractFE(stack, maxExtract, simulate);
   }

   public int getEnergyStored(ItemStack stack) {
      return this.getFEStored(stack);
   }

   public int getMaxEnergyStored(ItemStack stack) {
      return this.getFECapacity(stack);
   }
}
