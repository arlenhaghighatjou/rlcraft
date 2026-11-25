package gigaherz.toolbelt.belt;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.UnmodifiableIterator;
import gigaherz.toolbelt.ToolBelt;
import gigaherz.toolbelt.customslots.ExtensionSlotItemHandler;
import gigaherz.toolbelt.customslots.IExtensionContainer;
import gigaherz.toolbelt.customslots.IExtensionSlot;
import gigaherz.toolbelt.customslots.IExtensionSlotItem;
import gigaherz.toolbelt.customslots.example.RpgEquipment;
import gigaherz.toolbelt.shadow.commons.ItemRegistered;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.items.IItemHandler;

@Interface(
   modid = "baubles",
   iface = "baubles.api.IBauble"
)
public class ItemToolBelt extends ItemRegistered implements IBauble, IExtensionSlotItem {
   @CapabilityInject(IItemHandler.class)
   public static Capability<IItemHandler> ITEM_HANDLER;
   @CapabilityInject(IExtensionSlotItem.class)
   public static Capability<IExtensionSlotItem> EXTENSION_SLOT_ITEM;
   public static final ImmutableSet<ResourceLocation> BELT_SLOT_LIST = ImmutableSet.of(RpgEquipment.BELT);
   public static int[] xpCost = new int[]{3, 5, 8, 12, 15, 20, 30};

   public ItemToolBelt(String name) {
      super(name);
      this.maxStackSize = 1;
      this.setCreativeTab(CreativeTabs.TOOLS);
   }

   public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      if (hand != EnumHand.MAIN_HAND) {
         return EnumActionResult.PASS;
      } else {
         player.openGui(ToolBelt.instance, 0, worldIn, hand.ordinal(), 0, 0);
         return EnumActionResult.SUCCESS;
      }
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
      if (hand != EnumHand.MAIN_HAND) {
         return new ActionResult(EnumActionResult.PASS, playerIn.getHeldItem(hand));
      } else {
         playerIn.openGui(ToolBelt.instance, 0, worldIn, hand.ordinal(), 0, 0);
         return new ActionResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(hand));
      }
   }

   public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced) {
      super.addInformation(stack, world, tooltip, advanced);
      int size = getSlotsCount(stack);
      tooltip.add(I18n.format("text.toolbelt.tooltip", new Object[]{size - 2, size}));
   }

   @Method(
      modid = "baubles"
   )
   public BaubleType getBaubleType(ItemStack itemStack) {
      return BaubleType.BELT;
   }

   @Nonnull
   public ImmutableSet<ResourceLocation> getAcceptableSlots(@Nonnull ItemStack stack) {
      return BELT_SLOT_LIST;
   }

   public boolean willAutoSync(ItemStack itemstack, EntityLivingBase player) {
      return true;
   }

   public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
      this.tickAllSlots(itemstack, player);
   }

   public void onWornTick(ItemStack itemstack, IExtensionSlot slot) {
      this.tickAllSlots(itemstack, slot.getContainer().getOwner());
   }

   public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
      if (entityIn instanceof EntityLivingBase) {
         this.tickAllSlots(stack, (EntityLivingBase)entityIn);
      }

   }

   public ICapabilityProvider initCapabilities(final ItemStack stack, NBTTagCompound nbt) {
      return new ICapabilityProvider() {
         final ToolBeltInventory itemHandler = new ToolBeltInventory(stack);

         public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
            if (capability == ItemToolBelt.ITEM_HANDLER) {
               return true;
            } else {
               return capability == ItemToolBelt.EXTENSION_SLOT_ITEM;
            }
         }

         @Nullable
         public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
            if (capability == ItemToolBelt.ITEM_HANDLER) {
               return (T)this.itemHandler;
            } else {
               return (T)(capability == ItemToolBelt.EXTENSION_SLOT_ITEM ? ItemToolBelt.this : null);
            }
         }
      };
   }

   public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
      return !ItemStack.areItemsEqual(oldStack, newStack);
   }

   public static int getSlotsCount(ItemStack stack) {
      int size = 2;
      NBTTagCompound nbt = stack.getTagCompound();
      if (nbt != null) {
         size = MathHelper.clamp(nbt.getInteger("Size"), 2, 9);
      }

      return size;
   }

   public static void setSlotsCount(ItemStack stack, int newSize) {
      NBTTagCompound nbt = stack.getTagCompound();
      if (nbt == null) {
         nbt = new NBTTagCompound();
         nbt.setTag("Items", new NBTTagList());
      }

      nbt.setInteger("Size", newSize);
      stack.setTagCompound(nbt);
   }

   public static int getUpgradeXP(ItemStack stack) {
      int slots = getSlotsCount(stack);
      if (slots >= 9) {
         return -1;
      } else {
         return slots < 2 ? 1 : xpCost[slots - 2];
      }
   }

   public static ItemStack upgrade(ItemStack stack) {
      int slots = getSlotsCount(stack);
      if (slots >= 9) {
         return stack.copy();
      } else {
         stack = stack.copy();
         setSlotsCount(stack, slots + 1);
         return stack;
      }
   }

   public ItemStack of(int upgradeLevel) {
      if (upgradeLevel >= 0 && upgradeLevel < 9) {
         ItemStack stack = new ItemStack(this);
         setSlotsCount(stack, upgradeLevel + 2);
         return stack;
      } else {
         return ItemStack.EMPTY;
      }
   }

   private void tickAllSlots(ItemStack source, EntityLivingBase player) {
      ItemToolBelt.BeltExtensionContainer container = new ItemToolBelt.BeltExtensionContainer(source, player);
      UnmodifiableIterator var4 = container.getSlots().iterator();

      while(var4.hasNext()) {
         IExtensionSlot slot = (IExtensionSlot)var4.next();
         ((ExtensionSlotItemHandler)slot).onWornTick();
      }

   }

   public static class BeltExtensionContainer implements IExtensionContainer {
      private static final ResourceLocation SLOT_TYPE = new ResourceLocation("toolbelt", "pocket");
      private final ToolBeltInventory inventory;
      private final EntityLivingBase owner;
      private final ImmutableList<IExtensionSlot> slots;

      public BeltExtensionContainer(ItemStack source, EntityLivingBase owner) {
         this.inventory = (ToolBeltInventory)source.getCapability(ItemToolBelt.ITEM_HANDLER, (EnumFacing)null);
         this.owner = owner;
         ExtensionSlotItemHandler[] slots = new ExtensionSlotItemHandler[this.inventory.getSlots()];

         for(int i = 0; i < this.inventory.getSlots(); ++i) {
            slots[i] = new ExtensionSlotItemHandler(this, SLOT_TYPE, this.inventory, i) {
               public boolean canEquip(@Nonnull ItemStack stack) {
                  return BeltExtensionContainer.this.inventory.canInsertItem(this.slot, stack);
               }
            };
         }

         this.slots = ImmutableList.copyOf(slots);
      }

      @Nonnull
      public EntityLivingBase getOwner() {
         return this.owner;
      }

      @Nonnull
      public ImmutableList<IExtensionSlot> getSlots() {
         return this.slots;
      }

      public void onContentsChanged(IExtensionSlot slot) {
      }
   }
}
