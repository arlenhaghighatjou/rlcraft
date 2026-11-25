package rustic.common.items;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.ItemFluidContainer;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack.SwapEmpty;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.client.models.FluidBottleModel;
import rustic.common.blocks.fluids.FluidBooze;
import rustic.common.blocks.fluids.FluidDrinkable;
import rustic.common.blocks.fluids.ModFluids;
import rustic.core.Rustic;

public class ItemFluidBottle extends ItemFluidContainer {
   public static List<Fluid> VALID_FLUIDS = new ArrayList();
   public static final String FLUID_NBT_KEY = "Fluid";
   private final ItemStack empty;

   public static void addFluid(Fluid fluid) {
      VALID_FLUIDS.add(fluid);
   }

   public ItemFluidBottle() {
      super(1000);
      this.setRegistryName("fluid_bottle");
      this.setUnlocalizedName("rustic.fluid_bottle");
      this.setCreativeTab(Rustic.farmingTab);
      GameRegistry.findRegistry(Item.class).register(this);
      this.empty = new ItemStack(Items.GLASS_BOTTLE);
   }

   @SideOnly(Side.CLIENT)
   public void initModel() {
      ItemMeshDefinition meshDefinition = new ItemMeshDefinition() {
         public ModelResourceLocation getModelLocation(@Nonnull ItemStack stack) {
            return FluidBottleModel.LOCATION;
         }
      };
      ModelLoader.setCustomMeshDefinition(this, meshDefinition);
      ModelLoader.registerItemVariants(this, new ResourceLocation[]{FluidBottleModel.LOCATION});
   }

   @SideOnly(Side.CLIENT)
   public ItemStack getDefaultInstance() {
      NBTTagCompound nbt = (new FluidStack(ModFluids.OLIVE_OIL, 1000)).writeToNBT(new NBTTagCompound());
      ItemStack stack = super.getDefaultInstance();
      NBTTagCompound tag = new NBTTagCompound();
      tag.setTag("Fluid", nbt);
      stack.setTagCompound(nbt);
      return stack;
   }

   @Nonnull
   public ItemStack getFilledBottle(Fluid fluid) {
      ItemStack bottle = new ItemStack(this);
      if (VALID_FLUIDS.contains(fluid) && FluidRegistry.getFluidName(fluid) != null) {
         NBTTagCompound fluidTag = new NBTTagCompound();
         FluidStack fluidStack = new FluidStack(fluid, this.getCapacity());
         fluidStack.writeToNBT(fluidTag);
         NBTTagCompound tag = new NBTTagCompound();
         tag.setTag("Fluid", fluidTag);
         bottle.setTagCompound(tag);
      }

      return bottle;
   }

   public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
      EntityPlayer entityplayer = entityLiving instanceof EntityPlayer ? (EntityPlayer)entityLiving : null;
      FluidStack fluidstack = this.getFluid(stack);
      Fluid fluid = fluidstack.getFluid() != null ? fluidstack.getFluid() : null;
      if (!worldIn.isRemote && fluid instanceof FluidDrinkable && entityplayer != null) {
         ((FluidDrinkable)fluid).onDrank(worldIn, entityplayer, stack, fluidstack);
      }

      if (entityplayer != null) {
         entityplayer.addStat(StatList.getObjectUseStats(this));
      }

      if (entityplayer == null || !entityplayer.capabilities.isCreativeMode) {
         stack.shrink(1);
      }

      if (entityplayer == null || !entityplayer.capabilities.isCreativeMode) {
         if (stack.isEmpty()) {
            return new ItemStack(Items.GLASS_BOTTLE);
         }

         if (entityplayer != null) {
            entityplayer.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
         }
      }

      return stack;
   }

   public int getMaxItemUseDuration(ItemStack stack) {
      return 32;
   }

   public EnumAction getItemUseAction(ItemStack stack) {
      return this.getFluid(stack) != null && this.getFluid(stack).getFluid() != null && this.getFluid(stack).getFluid() instanceof FluidDrinkable ? EnumAction.DRINK : EnumAction.NONE;
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      playerIn.setActiveHand(handIn);
      return new ActionResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
   }

   @Nullable
   public FluidStack getFluid(@Nonnull ItemStack container) {
      return container.hasTagCompound() && container.getTagCompound().hasKey("Fluid") ? FluidStack.loadFluidStackFromNBT(container.getTagCompound().getCompoundTag("Fluid")) : null;
   }

   public int getCapacity() {
      return this.capacity;
   }

   public boolean hasContainerItem(@Nonnull ItemStack stack) {
      return true;
   }

   @Nonnull
   public ItemStack getContainerItem(@Nonnull ItemStack itemStack) {
      return this.empty;
   }

   @Nonnull
   public String getItemStackDisplayName(@Nonnull ItemStack stack) {
      FluidStack fluidStack = this.getFluid(stack);
      if (fluidStack == null) {
         return this.empty.getDisplayName();
      } else {
         String unloc = this.getUnlocalizedName(stack);
         return I18n.translateToLocalFormatted(unloc + ".name", new Object[]{fluidStack.getLocalizedName()});
      }
   }

   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
      if (this.isInCreativeTab(tab)) {
         for(Fluid fluid : VALID_FLUIDS) {
            ItemStack stack = this.getFilledBottle(fluid);
            if (fluid instanceof FluidBooze && stack.hasTagCompound() && stack.getTagCompound().hasKey("Fluid")) {
               NBTTagCompound fluidTag = stack.getTagCompound().getCompoundTag("Fluid");
               if (!fluidTag.hasKey("Tag")) {
                  fluidTag.setTag("Tag", new NBTTagCompound());
               }

               if (!fluidTag.getCompoundTag("Tag").hasKey("Quality")) {
                  fluidTag.getCompoundTag("Tag").setFloat("Quality", 0.75F);
               }
            }

            subItems.add(stack);
         }
      }

   }

   public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt) {
      SwapEmpty handler = new SwapEmpty(stack, this.empty, this.capacity) {
         public boolean canFillFluidType(FluidStack fluidstack) {
            return ItemFluidBottle.VALID_FLUIDS.contains(fluidstack.getFluid());
         }

         public int fill(FluidStack resource, boolean doFill) {
            if (resource != null && resource.amount >= 1000 && this.getFluid() == null && this.canFillFluidType(resource)) {
               if (doFill) {
                  this.setFluid(resource.copy());
               }

               return 1000;
            } else {
               return 0;
            }
         }

         protected void setFluid(@Nullable FluidStack fluid) {
            if (fluid == null) {
               this.container = new ItemStack(Items.GLASS_BOTTLE);
            } else {
               this.container = new ItemStack(ModItems.FLUID_BOTTLE);
               FluidStack fs = fluid.copy();
               fs.amount = 1000;
               NBTTagCompound fluidTag = fs.writeToNBT(new NBTTagCompound());
               NBTTagCompound tag = new NBTTagCompound();
               tag.setTag("Fluid", fluidTag);
               this.container.setTagCompound(tag);
            }

         }

         public FluidStack drain(FluidStack resource, boolean doDrain) {
            return resource != null && resource.amount >= 1000 ? super.drain(resource, doDrain) : null;
         }

         public FluidStack drain(int maxDrain, boolean doDrain) {
            return maxDrain < 1000 ? null : super.drain(maxDrain, doDrain);
         }

         @Nonnull
         public ItemStack getContainer() {
            FluidStack contained = this.getFluid();
            return contained != null && contained.getFluid() != null && contained.amount > 0 ? this.container : new ItemStack(Items.GLASS_BOTTLE);
         }

         @Nullable
         public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            return (T)(capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY ? this : null);
         }
      };
      return (T)handler;
   }
}
