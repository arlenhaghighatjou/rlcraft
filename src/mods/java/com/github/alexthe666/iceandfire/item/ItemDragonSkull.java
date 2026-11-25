package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.StatCollector;
import com.github.alexthe666.iceandfire.entity.EntityDragonSkull;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDragonSkull extends Item implements ICustomRendered {
   public ItemDragonSkull() {
      this.maxStackSize = 1;
      this.setHasSubtypes(true);
      this.setCreativeTab(IceAndFire.TAB);
      this.setUnlocalizedName("iceandfire.dragon_skull");
      this.setRegistryName("iceandfire", "dragon_skull");
   }

   public void onCreated(ItemStack itemStack, World world, EntityPlayer player) {
      itemStack.setTagCompound(new NBTTagCompound());
   }

   @SideOnly(Side.CLIENT)
   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
      if (tab == this.getCreativeTab()) {
         items.add(new ItemStack(this, 1, 0));
         items.add(new ItemStack(this, 1, 1));
      }

   }

   public void onUpdate(ItemStack stack, World world, Entity entity, int f, boolean f1) {
      if (stack.getTagCompound() == null) {
         stack.setTagCompound(new NBTTagCompound());
         stack.getTagCompound().setInteger("Stage", 4);
         stack.getTagCompound().setInteger("DragonAge", 75);
      }

   }

   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      String iceorfire = stack.getMetadata() == 0 ? "dragon.fire" : "dragon.ice";
      tooltip.add(StatCollector.translateToLocal(iceorfire));
      if (stack.getTagCompound() != null) {
         tooltip.add(StatCollector.translateToLocal("dragon.stage") + stack.getTagCompound().getInteger("Stage"));
      }

   }

   public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      ItemStack stack = player.getHeldItem(hand);
      if (stack.getTagCompound() != null) {
         EntityDragonSkull skull = new EntityDragonSkull(worldIn);
         skull.setType(stack.getMetadata());
         skull.setStage(stack.getTagCompound().getInteger("Stage"));
         skull.setDragonAge(stack.getTagCompound().getInteger("DragonAge"));
         BlockPos offset = pos.offset(side, 1);
         skull.setLocationAndAngles((double)offset.getX() + 0.5D, (double)offset.getY(), (double)offset.getZ() + 0.5D, 0.0F, 0.0F);
         float yaw = player.rotationYaw;
         if (side != EnumFacing.UP) {
            yaw = player.getHorizontalFacing().getHorizontalAngle();
         }

         skull.setYaw(yaw);
         if (!worldIn.isRemote) {
            worldIn.spawnEntity(skull);
         }

         if (!player.capabilities.isCreativeMode) {
            stack.shrink(1);
            if (stack.isEmpty()) {
               player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY);
            } else {
               player.inventory.setInventorySlotContents(player.inventory.currentItem, stack);
            }
         }
      }

      return EnumActionResult.SUCCESS;
   }
}
