package com.alcatrazescapee.notreepunching.common.items;

import com.alcatrazescapee.alcatrazcore.item.tool.ItemToolCore;
import com.alcatrazescapee.alcatrazcore.util.CoreHelpers;
import com.alcatrazescapee.notreepunching.common.ModMaterials;
import com.alcatrazescapee.notreepunching.common.recipe.KnifeRecipe;
import com.alcatrazescapee.notreepunching.common.recipe.ModRecipes;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

@ParametersAreNonnullByDefault
public class ItemKnife extends ItemToolCore {
   public ItemKnife(ToolMaterial material) {
      super(material, Math.max(1.0F, material.getAttackDamage() - 1.5F), -2.4F);
   }

   @Nonnull
   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      EnumHand handOther = handIn == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
      ItemStack knifeStack = playerIn.getHeldItem(handIn);
      ItemStack cutStack = playerIn.getHeldItem(handOther);
      KnifeRecipe recipe = (KnifeRecipe)ModRecipes.KNIFE.get(cutStack);
      if (recipe == null) {
         return super.onItemRightClick(worldIn, playerIn, handIn);
      } else {
         if (!worldIn.isRemote) {
            for(ItemStack stack : recipe.getOutput()) {
               stack = stack.copy();
               if (!playerIn.addItemStackToInventory(stack)) {
                  CoreHelpers.dropItemInWorldExact(worldIn, playerIn.getPosition(), stack);
               }
            }

            if (!playerIn.isCreative()) {
               knifeStack.damageItem(3, playerIn);
               playerIn.setHeldItem(handOther, recipe.consumeInput(cutStack));
            }
         }

         worldIn.playSound(playerIn, playerIn.getPosition(), SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.PLAYERS, 1.0F, 1.0F);
         return new ActionResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
      }
   }

   public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase entity, EnumHand hand) {
      if (!entity.world.isRemote && entity instanceof IShearable && this.toolMaterial != ModMaterials.TOOL_FLINT) {
         IShearable target = (IShearable)entity;
         BlockPos pos = new BlockPos(entity.posX, entity.posY, entity.posZ);
         if (target.isShearable(stack, entity.world, pos)) {
            List<ItemStack> drops = target.onSheared(stack, entity.world, pos, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack));
            EaglercraftRandom rand = new EaglercraftRandom();

            for(ItemStack stack1 : drops) {
               if (stack1 != null && !stack1.isEmpty()) {
                  EntityItem entityItem = entity.entityDropItem(stack1, 1.0F);
                  if (entityItem != null) {
                     entityItem.motionY += (double)(rand.nextFloat() * 0.05F);
                     entityItem.motionX += (double)((rand.nextFloat() - rand.nextFloat()) * 0.1F);
                     entityItem.motionZ += (double)((rand.nextFloat() - rand.nextFloat()) * 0.1F);
                  }
               }
            }

            stack.damageItem(1, entity);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
      return enchantment.type == EnumEnchantmentType.BREAKABLE || enchantment.type == EnumEnchantmentType.WEAPON;
   }
}
