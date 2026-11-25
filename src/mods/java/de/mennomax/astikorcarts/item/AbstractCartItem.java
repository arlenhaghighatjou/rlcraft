package de.mennomax.astikorcarts.item;

import de.mennomax.astikorcarts.entity.AbstractDrawn;
import de.mennomax.astikorcarts.init.ModCreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;

public abstract class AbstractCartItem extends Item {
   public AbstractCartItem(String name) {
      this.setRegistryName("astikorcarts", name);
      this.setUnlocalizedName(this.getRegistryName().toString());
      this.setCreativeTab(ModCreativeTabs.astikor);
      this.setMaxStackSize(1);
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      Vec3d vec3d = new Vec3d(playerIn.posX, playerIn.posY + (double)playerIn.getEyeHeight(), playerIn.posZ);
      Vec3d lookVec = playerIn.getLookVec();
      Vec3d vec3d1 = new Vec3d(lookVec.x * 5.0D + vec3d.x, lookVec.y * 5.0D + vec3d.y, lookVec.z * 5.0D + vec3d.z);
      RayTraceResult result = worldIn.rayTraceBlocks(vec3d, vec3d1, false);
      if (result != null && result.typeOfHit == Type.BLOCK) {
         if (!worldIn.isRemote) {
            AbstractDrawn cart = this.newCart(worldIn);
            cart.setPosition(result.hitVec.x, result.hitVec.y, result.hitVec.z);
            cart.rotationYaw = (playerIn.rotationYaw + 180.0F) % 360.0F;
            worldIn.spawnEntity(cart);
            if (!playerIn.capabilities.isCreativeMode) {
               itemstack.shrink(1);
            }

            playerIn.addStat(StatList.getObjectUseStats(this));
         }

         return new ActionResult(EnumActionResult.PASS, itemstack);
      } else {
         return new ActionResult(EnumActionResult.FAIL, itemstack);
      }
   }

   public abstract AbstractDrawn newCart(World var1);
}
