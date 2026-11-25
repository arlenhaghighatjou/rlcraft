package com.oblivioussp.spartanweaponry.item;

import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import com.oblivioussp.spartanweaponry.api.WeaponProperties;
import com.oblivioussp.spartanweaponry.util.ConfigHandler;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemParryingDagger extends ItemWeaponBase implements IBlockingWeapon {
   public ItemParryingDagger(String unlocName, ToolMaterialEx material) {
      super(unlocName, material, ConfigHandler.damageBaseParryingDagger, ConfigHandler.damageMultiplierParryingDagger, (double)ConfigHandler.speedParryingDagger, WeaponProperties.BLOCK_MELEE);
      this.displayName = "parrying_dagger_custom";
      this.addPropertyOverride(new ResourceLocation("blocking"), new IItemPropertyGetter() {
         @SideOnly(Side.CLIENT)
         public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
            return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F;
         }
      });
   }

   public ItemParryingDagger(String unlocName, String externalModId, ToolMaterialEx material) {
      this(unlocName, material);
      this.modId = externalModId;
   }

   /** @deprecated */
   @Deprecated
   public ItemParryingDagger(String unlocName, String externalModId, ToolMaterialEx material, float weaponDamage) {
      this(unlocName, externalModId, material);
   }

   public EnumAction getItemUseAction(ItemStack stack) {
      return EnumAction.BLOCK;
   }

   public int getMaxItemUseDuration(ItemStack stack) {
      return 72000;
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      ItemStack heldStack = playerIn.getHeldItem(handIn);
      if (playerIn.isSneaking()) {
         return ActionResult.newResult(EnumActionResult.FAIL, heldStack);
      } else {
         playerIn.setActiveHand(handIn);
         return ActionResult.newResult(EnumActionResult.SUCCESS, heldStack);
      }
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      super.addInformation(stack, worldIn, tooltip, flagIn);
   }

   public boolean canBlockProjectiles() {
      return false;
   }

   public boolean canBlockMelee() {
      return true;
   }
}
