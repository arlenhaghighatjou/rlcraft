package com.oblivioussp.spartanweaponry.item;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.render.IRenderBauble;
import baubles.api.render.IRenderBauble.Helper;
import baubles.api.render.IRenderBauble.RenderType;
import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.capability.CapabilityProviderQuiver;
import com.oblivioussp.spartanweaponry.client.model.ModelQuiverBase;
import com.oblivioussp.spartanweaponry.util.StringHelper;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiScreen;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.common.Optional.InterfaceList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

@InterfaceList({@Interface(
   iface = "baubles.api.IBauble",
   modid = "baubles"
), @Interface(
   iface = "baubles.api.render.IRenderBauble",
   modid = "baubles"
)})
public abstract class ItemQuiverBase extends ItemSW implements IBauble, IRenderBauble {
   public static final String NBT_SIZE = "size";
   public static final String NBT_CURRENT_AMMO = "currentAmmo";
   public static final String NBT_TOTAL_AMMO = "totalAmmo";
   public static final String NBT_AMMO_COLLECT = "ammoCollect";
   public static final String NBT_OFFHAND_MOVED = "offhandMoved";
   public static final String NBT_ITEM_ID = "id";
   public static final String NBT_ITEM_SLOT = "slot";
   public static final String NBT_CLIENT_INVENTORY = "ClientInventory";
   @SideOnly(Side.CLIENT)
   protected ModelQuiverBase model;
   protected int guiIdQuiver = -1;
   protected int arrowSlots = 4;

   public ItemQuiverBase(String unlocName, int inventorySize) {
      super(unlocName);
      this.setMaxStackSize(1);
      this.arrowSlots = inventorySize;
      this.addPropertyOverride(new ResourceLocation("arrow"), new IItemPropertyGetter() {
         public float apply(ItemStack stack, World worldIn, EntityLivingBase entityIn) {
            return (float)ItemQuiverBase.this.getArrowCount(stack);
         }
      });
   }

   protected int getArrowCount(ItemStack stack) {
      int arrows = 0;
      if (!stack.hasTagCompound()) {
         stack.setTagCompound(new NBTTagCompound());
      }

      NBTTagList list = stack.getTagCompound().getTagList("ClientInventory", 10);

      for(int i = 0; i < list.tagCount(); ++i) {
         ItemStack item = new ItemStack(list.getCompoundTagAt(i));
         if (!item.isEmpty()) {
            ++arrows;
         }
      }

      if (this.arrowSlots == 9) {
         if (arrows > 5) {
            arrows = 5;
         }
      } else if (arrows > 3) {
         arrows = 3;
      }

      return arrows;
   }

   public int getInventorySlots() {
      return this.arrowSlots;
   }

   @Nonnull
   public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
      return new CapabilityProviderQuiver(stack, this.getInventorySlots());
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      ItemStack heldItem = playerIn.getHeldItem(handIn);
      if (!worldIn.isRemote) {
         if (!playerIn.isSneaking() && this.guiIdQuiver != -1) {
            if (!heldItem.hasTagCompound()) {
               heldItem.setTagCompound(new NBTTagCompound());
            }

            IItemHandler handler = (IItemHandler)heldItem.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null);
            if (!(handler instanceof ItemStackHandler)) {
               return new ActionResult(EnumActionResult.FAIL, heldItem);
            }

            ItemQuiverBase.SlotType slotType = handIn == EnumHand.MAIN_HAND ? ItemQuiverBase.SlotType.MAIN_HAND : ItemQuiverBase.SlotType.OFF_HAND;
            playerIn.openGui(ModSpartanWeaponry.instance, this.guiIdQuiver, worldIn, slotType.ordinal(), -1, 0);
            return new ActionResult(EnumActionResult.SUCCESS, heldItem);
         }

         if (playerIn.isSneaking()) {
            if (!heldItem.hasTagCompound()) {
               heldItem.setTagCompound(new NBTTagCompound());
            }

            boolean ammoCollect = !heldItem.getTagCompound().getBoolean("ammoCollect");
            heldItem.getTagCompound().setBoolean("ammoCollect", ammoCollect);
            String collectStatus = ammoCollect ? "enabled" : "disabled";
            TextFormatting collectColour = ammoCollect ? TextFormatting.GREEN : TextFormatting.RED;
            String message = StringHelper.translateFormattedString("ammoCollectToggle", "message", "spartanweaponry", collectColour + StringHelper.translateString(collectStatus, "tooltip"));
            playerIn.sendStatusMessage(new TextComponentString(message), true);
            return new ActionResult(EnumActionResult.FAIL, heldItem);
         }
      }

      return new ActionResult(EnumActionResult.PASS, heldItem);
   }

   public boolean getShareTag() {
      return true;
   }

   public NBTTagCompound getNBTShareTag(ItemStack stack) {
      IItemHandler handler = (IItemHandler)stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null);
      NBTBase capTag = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.getStorage().writeNBT(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, handler, (EnumFacing)null);
      NBTTagCompound tag = super.getNBTShareTag(stack);
      if (tag == null) {
         tag = new NBTTagCompound();
      }

      if (capTag != null) {
         tag.setTag("ClientInventory", capTag);
      }

      return tag;
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      this.addQuiverInventoryTooltip(stack, tooltip);
   }

   @SideOnly(Side.CLIENT)
   protected void addQuiverInventoryTooltip(ItemStack stack, List<String> tooltip) {
      if (!stack.hasTagCompound()) {
         stack.setTagCompound(new NBTTagCompound());
      }

      int itemCount = 0;
      tooltip.add(TextFormatting.DARK_AQUA + StringHelper.translateFormattedString("quiver_capacity", "tooltip", "spartanweaponry", TextFormatting.GRAY + "" + this.arrowSlots));
      boolean ammoCollect = stack.getTagCompound().getBoolean("ammoCollect");
      String collectStatus = ammoCollect ? "enabled" : "disabled";
      TextFormatting statusColour = ammoCollect ? TextFormatting.GREEN : TextFormatting.RED;
      tooltip.add(TextFormatting.DARK_AQUA + StringHelper.translateFormattedString("quiver_collect_status", "tooltip", "spartanweaponry", statusColour + StringHelper.translateString(collectStatus, "tooltip")));
      tooltip.add(TextFormatting.DARK_AQUA + StringHelper.translateString("quiver_contains", "tooltip"));
      NBTTagList list = stack.getTagCompound().getTagList("ClientInventory", 10);

      for(int i = 0; i < list.tagCount(); ++i) {
         ItemStack slotStack = new ItemStack(list.getCompoundTagAt(i));
         if (!slotStack.isEmpty()) {
            if (itemCount >= 2 && !GuiScreen.isShiftKeyDown()) {
               if (itemCount == 2) {
                  tooltip.add(TextFormatting.DARK_GRAY + StringHelper.translateFormattedString("quiver_show_contents", "tooltip", "spartanweaponry", TextFormatting.AQUA + "SHIFT" + TextFormatting.DARK_GRAY));
               }
            } else {
               tooltip.add("  " + StringHelper.translateFormattedString("quiver_ammo", "tooltip", "spartanweaponry", slotStack.getCount(), TextFormatting.AQUA + "" + slotStack.getDisplayName()));
            }

            ++itemCount;
         }
      }

      if (itemCount == 0) {
         tooltip.add("  " + StringHelper.translateString("quiver_none", "tooltip"));
      }

   }

   public BaubleType getBaubleType(ItemStack itemstack) {
      return BaubleType.BODY;
   }

   public boolean willAutoSync(ItemStack itemstack, EntityLivingBase player) {
      return true;
   }

   public void onPlayerBaubleRender(ItemStack stack, EntityPlayer player, RenderType type, float partialTicks) {
      if (type == RenderType.BODY) {
         int arrowsToRender = this.getArrowCount(stack);
         if (this.model == null) {
            this.model = this.initModel();
         }

         this.model.setArrowsToRender(arrowsToRender);
         if (Loader.isModLoaded("mobends")) {
            Render<AbstractClientPlayer> render = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(player);
            if (render instanceof RenderPlayer) {
               RenderPlayer renderPlayer = (RenderPlayer)render;
               renderPlayer.getMainModel().bipedBody.postRender(0.0625F);
            }

            if (player.isSneaking()) {
               GlStateManager.translate(0.0F, 0.49F, -0.0875F);
            } else {
               GlStateManager.translate(0.0F, 0.3F, 0.0F);
            }
         } else {
            Helper.rotateIfSneaking(player);
            GlStateManager.translate(0.0F, 0.3F, 0.0F);
         }

         Minecraft.getMinecraft().getTextureManager().bindTexture(this.getTexture());
         this.model.render(0.0625F);
      }

   }

   public int getGuiId() {
      return this.guiIdQuiver;
   }

   public abstract ModelQuiverBase initModel();

   public abstract ResourceLocation getTexture();

   public abstract boolean isAmmoValid(ItemStack var1, ItemStack var2);

   public static enum SlotType {
      UNDEFINED,
      MAIN_HAND,
      OFF_HAND,
      HOTBAR,
      BAUBLE;

      private SlotType() {
      }
   }
}
