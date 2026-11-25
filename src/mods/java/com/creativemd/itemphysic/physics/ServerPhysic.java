package com.creativemd.itemphysic.physics;

import com.creativemd.itemphysic.ItemDummyContainer;
import com.google.common.base.Optional;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class ServerPhysic {
   public static Random rand = new Random();
   public static DataParameter<Optional<ItemStack>> ITEM = null;
   public static ThreadLocal<Fluid> fluid = new ThreadLocal();
   private static Method markVelocityChanged = ReflectionHelper.findMethod(Entity.class, "markVelocityChanged", "markVelocityChanged", new Class[0]);
   private static Field health = ReflectionHelper.findField(EntityItem.class, new String[]{"health", "health"});
   private static Field fire = ReflectionHelper.findField(Entity.class, new String[]{"fire", "fire"});
   public static Field age = ReflectionHelper.findField(EntityItem.class, new String[]{"age", "age"});
   private static Method getFlag = ReflectionHelper.findMethod(Entity.class, "getFlag", "getFlag", new Class[]{Integer.TYPE});

   public ServerPhysic() {
   }

   public static boolean updatePre(EntityItem item) {
      ItemStack stack = item.getItem();
      float f = 0.98F;
      fluid.set(CommonPhysic.getFluid(item));
      if (fluid.get() == null) {
         return item.hasNoGravity();
      } else {
         double density = (double)((Fluid)fluid.get()).getDensity() / 1000.0D;
         double speed = -1.0D / density * 0.01D;
         if (ItemDummyContainer.CONFIG.general.swimmingItems.canPass(stack)) {
            speed = 0.05D;
         }

         double speedreduction = (speed - item.motionY) / 2.0D;
         double maxSpeedReduction = 0.05D;
         if (speedreduction < -maxSpeedReduction) {
            speedreduction = -maxSpeedReduction;
         }

         if (speedreduction > maxSpeedReduction) {
            speedreduction = maxSpeedReduction;
         }

         item.motionY += speedreduction;
         f = (float)(1.0D / density / 1.2D);
         return true;
      }
   }

   public static void updateBurn(EntityItem item) {
      if (item.world.getBlockState(new BlockPos(item)).getMaterial() == Material.LAVA && ItemDummyContainer.CONFIG.general.burningItems.canPass(item.getItem()) && !ItemDummyContainer.CONFIG.general.undestroyableItems.canPass(item.getItem())) {
         item.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4F, 2.0F + rand.nextFloat() * 0.4F);

         for(int i = 0; i < 100; ++i) {
            item.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, item.posX, item.posY, item.posZ, (double)rand.nextFloat() * 0.1D - 0.05D, 0.2D * rand.nextDouble(), (double)rand.nextFloat() * 0.1D - 0.05D, new int[0]);
         }
      }

      if (ItemDummyContainer.CONFIG.general.enableIgniting && !item.world.isRemote && item.onGround && Math.random() <= 0.1D && ItemDummyContainer.CONFIG.general.ignitingItems.canPass(item.getItem())) {
         IBlockState state = item.world.getBlockState((new BlockPos(item)).down());
         if (state.getMaterial().getCanBurn() && item.world.getBlockState(new BlockPos(item)).getMaterial().isReplaceable()) {
            item.world.setBlockState(new BlockPos(item), Blocks.FIRE.getDefaultState());
         }
      }

   }

   public static void updatePost(EntityItem item) {
      if (ItemDummyContainer.CONFIG.general.swimmingItems.canPass(item.getItem())) {
         int i = MathHelper.floor(item.posX);
         int j = MathHelper.floor(item.posY);
         int k = MathHelper.floor(item.posZ);
         BlockPos pos = new BlockPos(i, j, k);
         IBlockState state = item.world.getBlockState(pos);
         Block block = state.getBlock();
         Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
         if (fluid == null && block instanceof IFluidBlock) {
            fluid = ((IFluidBlock)block).getFluid();
         } else if (block instanceof BlockLiquid) {
            fluid = FluidRegistry.WATER;
         }

         if (fluid != null) {
            item.motionX /= (double)fluid.getDensity() / 950.0D * 1.5D;
            item.motionZ /= (double)fluid.getDensity() / 950.0D * 1.5D;
         }
      }

      if (fluid.get() == null) {
         item.motionY *= 0.98D;
         if (item.onGround) {
            item.motionY *= -0.5D;
         }
      } else {
         item.motionX /= (double)((Fluid)fluid.get()).getDensity() / 950.0D;
         item.motionZ /= (double)((Fluid)fluid.get()).getDensity() / 950.0D;
      }

      if (ItemDummyContainer.CONFIG.general.despawnItem != -1 && item.lifespan == 6000 && item.lifespan != ItemDummyContainer.CONFIG.general.despawnItem) {
         item.lifespan = ItemDummyContainer.CONFIG.general.despawnItem;
      }

   }

   public static void updateFallState(EntityItem item, double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
      if (onGroundIn && item.fallDistance > 0.0F && ItemDummyContainer.CONFIG.general.fallSounds) {
         item.playSound(SoundEvents.BLOCK_CLOTH_FALL, Math.min(1.0F, item.fallDistance / 10.0F), (float)Math.random() * 1.0F + 1.0F);
      }

   }

   public static boolean onCollideWithPlayer(EntityItem item, EntityPlayer par1EntityPlayer) {
      if (!ItemDummyContainer.CONFIG.pickup.customPickup || par1EntityPlayer.isSneaking() && ItemDummyContainer.CONFIG.pickup.pickupWhenSneaking) {
         return item.world.isRemote || item.cannotPickup();
      } else {
         return true;
      }
   }

   public static void onCollideWithPlayer(EntityItem item, EntityPlayer player, boolean needsSneak) {
      if (!ItemDummyContainer.CONFIG.pickup.customPickup || !needsSneak || player.isSneaking() && ItemDummyContainer.CONFIG.pickup.pickupWhenSneaking) {
         if (!item.world.isRemote) {
            if (!ItemDummyContainer.CONFIG.pickup.customPickup && item.cannotPickup()) {
               return;
            }

            ItemStack itemstack = item.getItem();
            int i = itemstack.getCount();
            int hook = ForgeEventFactory.onItemPickup(item, player);
            if (hook < 0) {
               return;
            }

            ItemStack clone = itemstack.copy();
            if ((!item.cannotPickup() || ItemDummyContainer.CONFIG.pickup.customPickup) && (item.getOwner() == null || item.lifespan - item.getAge() <= 200 || item.getOwner().equals(player.getName())) && (hook == 1 || i <= 0 || player.inventory.addItemStackToInventory(itemstack) || clone.getCount() > item.getItem().getCount())) {
               clone.setCount(clone.getCount() - item.getItem().getCount());
               FMLCommonHandler.instance().firePlayerItemPickupEvent(player, item, clone);
               if (itemstack.isEmpty()) {
                  player.onItemPickup(item, i);
                  item.setDead();
                  itemstack.setCount(i);
               }

               player.addStat(StatList.getObjectsPickedUpStats(itemstack.getItem()), i);
            }
         }

      }
   }

   public static boolean processInitialInteract(EntityItem item, EntityPlayer player, ItemStack stack, EnumHand hand) {
      if (ItemDummyContainer.CONFIG.pickup.customPickup) {
         onCollideWithPlayer(item, player, false);
         return true;
      } else {
         return false;
      }
   }

   public static boolean attackEntityFrom(EntityItem item, DamageSource source, float amount) {
      if (item.isEntityInvulnerable(source)) {
         return false;
      } else if (!item.getItem().isEmpty() && ItemDummyContainer.CONFIG.general.undestroyableItems.canPass(item.getItem())) {
         return false;
      } else if (source == DamageSource.LAVA | source == DamageSource.ON_FIRE | source == DamageSource.IN_FIRE && !ItemDummyContainer.CONFIG.general.burningItems.canPass(item.getItem())) {
         return false;
      } else if (source == DamageSource.CACTUS) {
         return false;
      } else {
         try {
            markVelocityChanged.invoke(item);
         } catch (Exception var5) {
            var5.printStackTrace();
         }

         try {
            health.setInt(item, (int)((float)health.getInt(item) - amount));
            if (health.getInt(item) <= 0) {
               item.setDead();
            }
         } catch (Exception var4) {
            var4.printStackTrace();
         }

         return false;
      }
   }

   public static boolean isItemBurning(EntityItem item) {
      boolean flag = item.world != null && item.world.isRemote;

      try {
         if (item.isImmuneToFire() || fire.getInt(item) <= 0 && (!flag || !((Boolean)getFlag.invoke(item, Integer.valueOf(0))).booleanValue())) {
            return false;
         }
      } catch (Exception var3) {
         var3.printStackTrace();
      }

      return ItemDummyContainer.CONFIG.general.burningItems.canPass(item.getItem());
   }
}
