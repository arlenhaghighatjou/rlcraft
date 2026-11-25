package com.mujmajnkraft.bettersurvival.items;

import com.mujmajnkraft.bettersurvival.config.ForgeConfigHandler;
import com.mujmajnkraft.bettersurvival.init.ModPotions;
import com.mujmajnkraft.bettersurvival.recipe.CrushingRecipe;
import java.util.List;
import net.minecraft.block.BlockGlass;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemHammer extends ItemCustomWeapon {
   public final int stunduration;

   public ItemHammer(ToolMaterial material) {
      super(material, ForgeConfigHandler.weapons.hammerDmgMod, ForgeConfigHandler.weapons.hammerSpd);
      this.setRegistryName("mujmajnkraftsbettersurvival", "item" + material.name().toLowerCase() + "hammer");
      this.setUnlocalizedName(material.name().toLowerCase() + "hammer");
      this.stunduration = 50;
   }

   public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      if (hand == EnumHand.OFF_HAND) {
         return EnumActionResult.FAIL;
      } else {
         float d = Math.min(super.getAttackDamage(), 9.0F) + 1.0F;
         double xd = 1.0D;
         double yd = 1.0D;
         double zd = 1.0D;
         if (facing != EnumFacing.UP && facing != EnumFacing.DOWN) {
            if (facing != EnumFacing.EAST && facing != EnumFacing.WEST) {
               if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH) {
                  xd += 9.0D;
                  yd += 9.0D;
               }
            } else {
               yd += 9.0D;
               zd += 9.0D;
            }
         } else {
            xd += 9.0D;
            zd += 9.0D;
         }

         for(EntityLivingBase entitylivingbase : playerIn.getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, (new AxisAlignedBB(pos)).grow(xd, yd, zd))) {
            if (entitylivingbase != playerIn && entitylivingbase.getDistanceSq(pos) < (double)(2.0F * d) && entitylivingbase.onGround) {
               entitylivingbase.knockBack(playerIn, 0.4F, (double)MathHelper.sin(playerIn.rotationYaw * 0.017453292F), (double)(-MathHelper.cos(playerIn.rotationYaw * 0.017453292F)));
               entitylivingbase.attackEntityFrom(DamageSource.causePlayerDamage(playerIn), (float)((double)(this.getAttackDamage() / 2.0F + 1.5F) * (1.0D - entitylivingbase.getDistanceSq(pos) / 20.0D)));
               PotionEffect potioneffectIn = new PotionEffect(ModPotions.stun, this.stunduration);
               entitylivingbase.addPotionEffect(potioneffectIn);
            }
         }

         for(int x = -5; x < 6; ++x) {
            if ((facing != EnumFacing.WEST && facing != EnumFacing.EAST || x == 0) && playerIn.capabilities.allowEdit) {
               for(float y = -5.0F; y < 6.0F; ++y) {
                  if (facing != EnumFacing.UP && facing != EnumFacing.DOWN || y == 0.0F) {
                     for(int z = -5; z < 6; ++z) {
                        if ((facing != EnumFacing.NORTH && facing != EnumFacing.SOUTH || z == 0) && (float)(x * x) + y * y + (float)(z * z) <= 2.0F * d) {
                           boolean particles = (float)(x * x) + y * y + (float)(z * z) >= 2.0F * (d - 2.0F);
                           BlockPos position = pos.add((double)x, (double)y, (double)z);
                           if (worldIn.getBlockState(position.offset(facing)).getBlock() instanceof BlockGlass) {
                              worldIn.destroyBlock(position.offset(facing), true);
                           }

                           if (worldIn.getBlockState(position.offset(facing.getOpposite())).getBlock() instanceof BlockGlass) {
                              worldIn.destroyBlock(position.offset(facing.getOpposite()), true);
                           }

                           if (worldIn.getBlockState(position).getBlock() instanceof BlockGlass) {
                              worldIn.destroyBlock(position, true);
                           }

                           if (worldIn.getBlockState(position.offset(facing)).isSideSolid(worldIn, position, EnumFacing.UP)) {
                              BlockPos pos2 = position.offset(facing);
                              if (!worldIn.getBlockState(pos2.offset(facing)).isSideSolid(worldIn, pos2, EnumFacing.UP)) {
                                 CrushingRecipe.Crush(playerIn, pos2, particles);
                              }
                           } else if (!worldIn.getBlockState(position).isSideSolid(worldIn, position, EnumFacing.UP)) {
                              BlockPos pos2 = position.offset(facing.getOpposite());
                              CrushingRecipe.Crush(playerIn, pos2, particles);
                           } else {
                              CrushingRecipe.Crush(playerIn, position, particles);
                           }
                        }
                     }
                  }
               }
            }
         }

         if (!playerIn.isCreative()) {
            playerIn.getHeldItem(hand).damageItem(10, playerIn);
         }

         worldIn.playSound((EntityPlayer)null, pos, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
         if (!playerIn.capabilities.isCreativeMode) {
            playerIn.getCooldownTracker().setCooldown(this, 200);
         }

         return EnumActionResult.SUCCESS;
      }
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      super.addInformation(stack, worldIn, tooltip, flagIn);
      String s = I18n.format("mujmajnkraftsbettersurvival.hammer.desc", new Object[0]);
      tooltip.add(TextFormatting.AQUA + s);
   }

   public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
      target.knockBack(attacker, 0.5F, (double)MathHelper.sin(attacker.rotationYaw * 0.017453292F), (double)(-MathHelper.cos(attacker.rotationYaw * 0.017453292F)));
      return super.hitEntity(stack, target, attacker);
   }
}
