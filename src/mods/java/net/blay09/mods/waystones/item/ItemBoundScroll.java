package net.blay09.mods.waystones.item;

import java.util.List;
import javax.annotation.Nullable;
import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.BlockWaystone;
import net.blay09.mods.waystones.block.TileWaystone;
import net.blay09.mods.waystones.client.ClientWaystones;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBoundScroll extends Item implements IResetUseOnDamage {
   public static final String name = "bound_scroll";
   public static final ResourceLocation registryName = new ResourceLocation("waystones", "bound_scroll");

   public ItemBoundScroll() {
      this.setCreativeTab(Waystones.creativeTab);
      this.setUnlocalizedName(registryName.toString());
   }

   public int getMaxItemUseDuration(ItemStack itemStack) {
      return WaystoneConfig.general.warpScrollUseTime;
   }

   public EnumAction getItemUseAction(ItemStack itemStack) {
      return Waystones.proxy.isVivecraftInstalled() ? EnumAction.NONE : EnumAction.BOW;
   }

   private void setBoundTo(ItemStack itemStack, @Nullable WaystoneEntry entry) {
      NBTTagCompound tagCompound = itemStack.getTagCompound();
      if (tagCompound == null) {
         tagCompound = new NBTTagCompound();
         itemStack.setTagCompound(tagCompound);
      }

      if (entry != null) {
         tagCompound.setTag("WaystonesBoundTo", entry.writeToNBT());
      } else {
         tagCompound.removeTag("WaystonesBoundTo");
      }

   }

   @Nullable
   protected WaystoneEntry getBoundTo(EntityPlayer player, ItemStack itemStack) {
      NBTTagCompound tagCompound = itemStack.getTagCompound();
      return tagCompound != null ? WaystoneEntry.read(tagCompound.getCompoundTag("WaystonesBoundTo")) : null;
   }

   public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
      ItemStack heldItem = player.getHeldItem(hand);
      TileEntity tileEntity = world.getTileEntity(pos);
      if (tileEntity instanceof TileWaystone) {
         TileWaystone tileWaystone = ((TileWaystone)tileEntity).getParent();
         if (ClientWaystones.getKnownWaystone(tileWaystone.getWaystoneName()) == null) {
            ((BlockWaystone)Waystones.blockWaystone).activateWaystone(player, world, tileWaystone);
         }

         if (!world.isRemote) {
            ItemStack boundItem = heldItem.getCount() == 1 ? heldItem : heldItem.splitStack(1);
            this.setBoundTo(boundItem, new WaystoneEntry(tileWaystone));
            if (boundItem != heldItem && !player.addItemStackToInventory(boundItem)) {
               player.dropItem(boundItem, false);
            }

            player.sendStatusMessage(new TextComponentTranslation("waystones:scrollBound", new Object[]{tileWaystone.getWaystoneName()}), true);
         }

         Waystones.proxy.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, pos, 2.0F);
         return EnumActionResult.SUCCESS;
      } else {
         return EnumActionResult.PASS;
      }
   }

   public ItemStack onItemUseFinish(ItemStack itemStack, World world, EntityLivingBase entity) {
      if (!world.isRemote && entity instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entity;
         WaystoneEntry boundTo = this.getBoundTo(player, itemStack);
         if (boundTo != null) {
            double distance = entity.getDistance((double)boundTo.getPos().getX(), (double)boundTo.getPos().getY(), (double)boundTo.getPos().getZ());
            if (distance <= 2.0D) {
               return itemStack;
            }

            TileWaystone tileWaystone = WaystoneManager.getWaystoneInWorld(boundTo);
            if (tileWaystone != null) {
               ((BlockWaystone)Waystones.blockWaystone).activateWaystone(player, world, tileWaystone);
               if (WaystoneManager.teleportToWaystone(player, boundTo) && !player.capabilities.isCreativeMode) {
                  itemStack.shrink(1);
               }
            }
         }
      }

      return itemStack;
   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      ItemStack itemStack = player.getHeldItem(hand);
      WaystoneEntry boundTo = this.getBoundTo(player, itemStack);
      if (boundTo != null) {
         if (!player.isHandActive() && world.isRemote) {
            Waystones.proxy.playSound(SoundEvents.BLOCK_PORTAL_TRIGGER, new BlockPos(player.posX, player.posY, player.posZ), 2.0F);
         }

         if (Waystones.proxy.isVivecraftInstalled()) {
            this.onItemUseFinish(itemStack, world, player);
         } else {
            player.setActiveHand(hand);
         }

         return new ActionResult(EnumActionResult.SUCCESS, itemStack);
      } else {
         player.sendStatusMessage(new TextComponentTranslation("waystones:scrollNotBound", new Object[0]), true);
         return new ActionResult(EnumActionResult.FAIL, itemStack);
      }
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack itemStack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
      EntityPlayer player = Minecraft.getMinecraft().player;
      if (player != null) {
         WaystoneEntry lastEntry = this.getBoundTo(player, itemStack);
         if (lastEntry != null) {
            tooltip.add(TextFormatting.GRAY + I18n.format("tooltip.waystones:boundTo", new Object[]{TextFormatting.DARK_AQUA + lastEntry.getName()}));
         } else {
            tooltip.add(TextFormatting.GRAY + I18n.format("tooltip.waystones:boundTo", new Object[]{I18n.format("tooltip.waystones:none", new Object[0])}));
         }

      }
   }
}
