package com.alcatrazescapee.notreepunching.common.items;

import com.alcatrazescapee.alcatrazcore.client.IModelProvider;
import com.alcatrazescapee.notreepunching.ModConfig;
import com.alcatrazescapee.notreepunching.NoTreePunching;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStackSimple;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;

@ParametersAreNonnullByDefault
public class ItemCeramicBucket extends UniversalBucket implements IModelProvider {
   public ItemCeramicBucket() {
      super(1000, ItemStack.EMPTY, false);
      this.setMaxStackSize(1);
   }

   @SideOnly(Side.CLIENT)
   public void registerModel() {
      ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.getRegistryName().toString(), "inventory"));
   }

   public void getSubItems(@Nullable CreativeTabs tab, NonNullList<ItemStack> subItems) {
      if (this.isInCreativeTab(tab)) {
         subItems.add(new ItemStack(this));

         for(Fluid fluid : FluidRegistry.getRegisteredFluids().values()) {
            FluidStack fs = new FluidStack(fluid, this.getCapacity());
            ItemStack stack = new ItemStack(this);
            IFluidHandlerItem fluidHandler = (IFluidHandlerItem)stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, (EnumFacing)null);
            if (fluidHandler != null && fluidHandler.fill(fs, true) == fs.amount) {
               ItemStack filled = fluidHandler.getContainer();
               subItems.add(filled);
            }
         }

      }
   }

   @Nonnull
   public String getItemStackDisplayName(@Nonnull ItemStack stack) {
      FluidStack fluidStack = this.getFluid(stack);
      return fluidStack == null ? I18n.translateToLocal(this.getUnlocalizedName() + ".name") : I18n.translateToLocalFormatted(this.getUnlocalizedName() + ".filled.name", new Object[]{fluidStack.getLocalizedName()});
   }

   @Nonnull
   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      ItemStack heldItem = player.getHeldItem(hand);
      FluidStack fluidStack = this.getFluid(heldItem);
      if (fluidStack != null) {
         ActionResult<ItemStack> result = super.onItemRightClick(world, player, hand);
         if (result.getType() == EnumActionResult.PASS) {
            player.setActiveHand(hand);
            return new ActionResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
         } else {
            return result;
         }
      } else {
         RayTraceResult target = this.rayTrace(world, player, true);
         if (target != null && target.typeOfHit == Type.BLOCK) {
            BlockPos pos = target.getBlockPos();
            ItemStack singleBucket = heldItem.copy();
            singleBucket.setCount(1);
            FluidActionResult filledResult = FluidUtil.tryPickUpFluid(singleBucket, player, world, pos, target.sideHit);
            if (filledResult.isSuccess()) {
               ItemStack filledBucket = filledResult.result;
               if (player.capabilities.isCreativeMode) {
                  return new ActionResult(EnumActionResult.SUCCESS, heldItem);
               } else {
                  heldItem.shrink(1);
                  if (heldItem.isEmpty()) {
                     return new ActionResult(EnumActionResult.SUCCESS, filledBucket);
                  } else {
                     ItemHandlerHelper.giveItemToPlayer(player, filledBucket);
                     return new ActionResult(EnumActionResult.SUCCESS, heldItem);
                  }
               }
            } else {
               return new ActionResult(EnumActionResult.PASS, heldItem);
            }
         } else {
            return new ActionResult(EnumActionResult.PASS, heldItem);
         }
      }
   }

   @Nonnull
   public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
      FluidStack fluidStack = this.getFluid(stack);
      if (fluidStack != null) {
         if (!worldIn.isRemote) {
            Fluid fluid = fluidStack.getFluid();
            if (fluid != null) {
               String var6 = fluid.getName();
               byte var7 = -1;
               switch(var6.hashCode()) {
               case 3314400:
                  if (var6.equals("lava")) {
                     var7 = 1;
                  }
                  break;
               case 3351579:
                  if (var6.equals("milk")) {
                     var7 = 0;
                  }
               }

               switch(var7) {
               case 0:
                  entityLiving.curePotionEffects(stack);
                  break;
               case 1:
                  NoTreePunching.getLog().info("You fool! Why are you drinking lava!");
               }
            }
         }

         return this.getEmpty();
      } else {
         return stack;
      }
   }

   @Nullable
   public FluidStack getFluid(ItemStack stack) {
      IFluidHandler cap = (IFluidHandler)stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, (EnumFacing)null);
      return cap instanceof ItemCeramicBucket.FluidHandlerCeramicBucket ? ((ItemCeramicBucket.FluidHandlerCeramicBucket)cap).getFluid() : null;
   }

   @Nonnull
   public ItemStack getEmpty() {
      return new ItemStack(this);
   }

   @Nonnull
   public EnumAction getItemUseAction(ItemStack stack) {
      return EnumAction.DRINK;
   }

   public int getMaxItemUseDuration(ItemStack stack) {
      return 32;
   }

   public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
      return new ItemCeramicBucket.FluidHandlerCeramicBucket(stack, 1000);
   }

   private static class FluidHandlerCeramicBucket extends FluidHandlerItemStackSimple {
      public FluidHandlerCeramicBucket(@Nonnull ItemStack container, int capacity) {
         super(container, capacity);
      }

      public boolean canFillFluidType(FluidStack fluid) {
         for(String s : ModConfig.GENERAL.ceramicBucketValidFluids) {
            if (s.equals(fluid.getFluid().getName())) {
               return true;
            }
         }

         return false;
      }
   }
}
