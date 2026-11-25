package com.alcatrazescapee.notreepunching.common.items;

import com.alcatrazescapee.alcatrazcore.item.tool.ItemToolCore;
import com.alcatrazescapee.alcatrazcore.util.CoreHelpers;
import com.alcatrazescapee.notreepunching.ModConfig;
import com.alcatrazescapee.notreepunching.client.particle.ParticleManager;
import com.alcatrazescapee.notreepunching.common.blocks.ModBlocks;
import com.alcatrazescapee.notreepunching.common.tile.TileFirePit;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@ParametersAreNonnullByDefault
public class ItemFireStarter extends ItemToolCore {
   public ItemFireStarter() {
      super(ToolMaterial.WOOD, 0.5F, -2.5F);
      this.setMaxDamage(10);
   }

   @Nonnull
   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      playerIn.setActiveHand(handIn);
      return new ActionResult(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
   }

   @Nonnull
   public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
      EntityPlayer player = (EntityPlayer)entityLiving;
      if (player != null) {
         RayTraceResult result = this.rayTrace(worldIn, player, false);
         if (result == null) {
            return stack;
         }

         if (result.typeOfHit == Type.BLOCK) {
            BlockPos pos = result.getBlockPos();
            if (!worldIn.isRemote) {
               stack.damageItem(1, entityLiving);
               List<EntityItem> entities = worldIn.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos.up(), pos.add(1, 2, 1)));
               int sticks = 3;
               int logs = 1;
               int thatch = 1;

               for(EntityItem drop : entities) {
                  ItemStack dropStack = drop.getItem();
                  if (CoreHelpers.doesStackMatchOre(dropStack, "kindling")) {
                     thatch -= dropStack.getCount();
                  } else if (CoreHelpers.doesStackMatchOre(dropStack, "logWood")) {
                     logs -= dropStack.getCount();
                  } else if (CoreHelpers.doesStackMatchOre(dropStack, "stickWood")) {
                     sticks -= dropStack.getCount();
                  }
               }

               if (sticks <= 0 && logs <= 0 && thatch <= 0) {
                  worldIn.setBlockState(pos.up(), ModBlocks.FIRE_PIT.getDefaultState());
                  TileFirePit te = (TileFirePit)CoreHelpers.getTE(worldIn, pos.up(), TileFirePit.class);
                  if (te != null) {
                     te.light(true);
                  }

                  entities.forEach(Entity::setDead);
               } else if ((double)itemRand.nextFloat() < ModConfig.BALANCE.fireStarterFireStartChance) {
                  worldIn.setBlockState(pos.up(), Blocks.FIRE.getDefaultState());
               }
            }
         }
      }

      return stack;
   }

   @Nonnull
   public EnumAction getItemUseAction(ItemStack stack) {
      return EnumAction.BOW;
   }

   public int getMaxItemUseDuration(ItemStack stack) {
      return 30;
   }

   @SideOnly(Side.CLIENT)
   public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
      if ((double)itemRand.nextFloat() < 0.3D) {
         World world = player.getEntityWorld();
         EntityPlayer player1 = (EntityPlayer)player;
         RayTraceResult result = this.rayTrace(world, player1, false);
         if (result != null && result.typeOfHit == Type.BLOCK) {
            Vec3d v = result.hitVec;
            ParticleManager.generateFireStarterSmoke(world, v);
         }
      }

   }

   public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
      return enchantment.type == EnumEnchantmentType.BREAKABLE;
   }
}
