package com.yyon.grapplinghook.items;

import com.yyon.grapplinghook.CommonProxyClass;
import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.vec;
import com.yyon.grapplinghook.controllers.grappleController;
import java.util.List;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class repeller extends Item {
   public repeller() {
      this.maxStackSize = 1;
      this.setFull3D();
      this.setUnlocalizedName("repeller");
      this.setMaxDamage(500);
      this.setCreativeTab(grapplemod.tabGrapplemod);
      MinecraftForge.EVENT_BUS.register(this);
   }

   public int getMaxItemUseDuration(ItemStack par1ItemStack) {
      return 72000;
   }

   public void dorightclick(ItemStack stack, World worldIn, EntityPlayer player) {
      if (worldIn.isRemote) {
         int playerid = player.getEntityId();
         if (grapplemod.controllers.containsKey(Integer.valueOf(playerid)) && ((grappleController)grapplemod.controllers.get(Integer.valueOf(playerid))).controllerid != grapplemod.AIRID) {
            grappleController controller = (grappleController)grapplemod.controllers.get(Integer.valueOf(playerid));
            controller.unattach();
         } else {
            grapplemod.proxy.createControl(grapplemod.REPELID, -1, playerid, worldIn, new vec(0.0D, 0.0D, 0.0D), (BlockPos)null, (GrappleCustomization)null);
         }
      }

   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
      ItemStack itemStackIn = playerIn.getHeldItem(hand);
      this.dorightclick(itemStackIn, worldIn, playerIn);
      return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);
   }

   public EnumAction getItemUseAction(ItemStack par1ItemStack) {
      return EnumAction.NONE;
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag par4) {
      list.add("Player is repelled by nearby blocks");
      list.add("Can be used with ender staff");
      list.add("");
      list.add(grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindUseItem) + " - Turn on");
      list.add(grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindUseItem) + " again - Turn off");
      list.add(grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindSneak) + " - Slow down");
      list.add(grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindForward) + ", " + grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindLeft) + ", " + grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindBack) + ", " + grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindRight) + " - Move");
   }
}
