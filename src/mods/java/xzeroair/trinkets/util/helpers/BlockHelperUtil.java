package xzeroair.trinkets.util.helpers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.TreeMap;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import xzeroair.trinkets.network.NetworkHandler;

public class BlockHelperUtil {
   public BlockHelperUtil() {
   }

   public static TreeMap<Double, Vec3d> findAll(Vec3d origin, World world, AxisAlignedBB aabb, BiPredicate<IBlockState, BlockPos> predicate) {
      TreeMap<Double, Vec3d> collection = new TreeMap();
      if (predicate != null) {
         int i = MathHelper.floor(aabb.minX);
         int j = MathHelper.floor(aabb.maxX + 1.0D);
         int k = MathHelper.floor(aabb.minY);
         int l = MathHelper.floor(aabb.maxY + 1.0D);
         int i1 = MathHelper.floor(aabb.minZ);
         int j1 = MathHelper.floor(aabb.maxZ + 1.0D);

         for(int k1 = i; k1 < j; ++k1) {
            for(int l1 = k; l1 < l; ++l1) {
               for(int i2 = i1; i2 < j1; ++i2) {
                  Vec3d pos = new Vec3d((double)k1, (double)l1, (double)i2);
                  BlockPos bPos = new BlockPos(pos);
                  IBlockState state = world.getBlockState(bPos);
                  if (predicate.test(state, bPos)) {
                     double blockDist = pos.distanceTo(origin);
                     collection.put(Double.valueOf(blockDist), pos);
                  }
               }
            }
         }
      }

      return collection;
   }

   public static BlockPos findFirst(Vec3d origin, World world, AxisAlignedBB aabb, BiPredicate<IBlockState, BlockPos> predicate) {
      if (predicate != null) {
         int i = MathHelper.floor(aabb.minX);
         int j = MathHelper.floor(aabb.maxX + 1.0D);
         int k = MathHelper.floor(aabb.minY);
         int l = MathHelper.floor(aabb.maxY + 1.0D);
         int i1 = MathHelper.floor(aabb.minZ);
         int j1 = MathHelper.floor(aabb.maxZ + 1.0D);

         for(int k1 = i; k1 < j; ++k1) {
            for(int l1 = k; l1 < l; ++l1) {
               for(int i2 = i1; i2 < j1; ++i2) {
                  Vec3d pos = new Vec3d((double)k1, (double)l1, (double)i2);
                  BlockPos bPos = new BlockPos(pos);
                  IBlockState state = world.getBlockState(bPos);
                  if (predicate.test(state, bPos)) {
                     return bPos;
                  }
               }
            }
         }
      }

      return new BlockPos(origin);
   }

   public static boolean isBlockNearby(World world, AxisAlignedBB aabb, BiPredicate<IBlockState, BlockPos> predicate) {
      if (predicate != null) {
         int i = MathHelper.floor(aabb.minX);
         int j = MathHelper.floor(aabb.maxX + 1.0D);
         int k = MathHelper.floor(aabb.minY);
         int l = MathHelper.floor(aabb.maxY + 1.0D);
         int i1 = MathHelper.floor(aabb.minZ);
         int j1 = MathHelper.floor(aabb.maxZ + 1.0D);

         for(int k1 = i; k1 < j; ++k1) {
            for(int l1 = k; l1 < l; ++l1) {
               for(int i2 = i1; i2 < j1; ++i2) {
                  Vec3d pos = new Vec3d((double)k1, (double)l1, (double)i2);
                  BlockPos bPos = new BlockPos(pos);
                  IBlockState state = world.getBlockState(bPos);
                  if (predicate.test(state, bPos)) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   public static boolean canToolHarvestBlock(ItemStack stack, IBlockState state) {
      return canToolHarvestBlock(stack, state, 0);
   }

   public static boolean canToolHarvestBlock(ItemStack stack, IBlockState state, int bonusToolLevel) {
      Block block = state.getBlock();
      if (state.getMaterial().isToolNotRequired()) {
         return true;
      } else {
         String type = block.getHarvestTool(state);
         int level = block.getHarvestLevel(state);
         return stack.getItem().getHarvestLevel(stack, type, (EntityPlayer)null, state) + bonusToolLevel >= level;
      }
   }

   public static boolean canPlayerHarvestBlock(@Nonnull Block block, @Nonnull EntityPlayer player, ItemStack harvestTool, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
      return canPlayerHarvestBlock(block, player, harvestTool, world, pos, 0);
   }

   public static boolean canPlayerHarvestBlock(@Nonnull Block block, @Nonnull EntityPlayer player, ItemStack harvestTool, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, int bonusToolLevel) {
      IBlockState state = world.getBlockState(pos);
      state = state.getBlock().getActualState(state, world, pos);
      if (state.getMaterial().isToolNotRequired()) {
         return true;
      } else {
         String tool = block.getHarvestTool(state);
         if (tool != null && !harvestTool.isEmpty()) {
            int toolLevel = harvestTool.getItem().getHarvestLevel(harvestTool, tool, player, state) + bonusToolLevel;
            if (toolLevel < 0) {
               return player.canHarvestBlock(state);
            } else {
               return toolLevel >= block.getHarvestLevel(state);
            }
         } else {
            return player.canHarvestBlock(state);
         }
      }
   }

   public static boolean isToolEffective(ItemStack stack, IBlockState state) {
      for(String type : stack.getItem().getToolClasses(stack)) {
         if (type != null && type.equals(state.getBlock().getHarvestTool(state))) {
            return true;
         }
      }

      return state.getBlock().getHarvestTool(state) == null;
   }

   public static float getEntityDigSpeed(EntityLivingBase entity, ItemStack itemstack, IBlockState state, BlockPos pos) {
      return getEntityDigSpeed(entity, itemstack, state, pos, true);
   }

   public static float getEntityDigSpeed(EntityLivingBase entity, ItemStack itemstack, IBlockState state, BlockPos pos, boolean callDigSpeedEvent) {
      float f = 1.0F;
      if (!itemstack.isEmpty()) {
         f *= itemstack.getDestroySpeed(state);
      }

      if (f > 1.0F) {
         int i = EnchantmentHelper.getEfficiencyModifier(entity);
         if (i > 0 && !itemstack.isEmpty()) {
            f += (float)(i * i + 1);
         }
      }

      if (entity.isPotionActive(MobEffects.HASTE)) {
         f *= 1.0F + (float)(entity.getActivePotionEffect(MobEffects.HASTE).getAmplifier() + 1) * 0.2F;
      }

      if (entity.isPotionActive(MobEffects.MINING_FATIGUE)) {
         float f1;
         switch(entity.getActivePotionEffect(MobEffects.MINING_FATIGUE).getAmplifier()) {
         case 0:
            f1 = 0.3F;
            break;
         case 1:
            f1 = 0.09F;
            break;
         case 2:
            f1 = 0.0027F;
            break;
         case 3:
         default:
            f1 = 8.1E-4F;
         }

         f *= f1;
      }

      if (entity.isInsideOfMaterial(Material.WATER) && !EnchantmentHelper.getAquaAffinityModifier(entity)) {
         f /= 5.0F;
      }

      if (!entity.onGround) {
         f /= 5.0F;
      }

      if (callDigSpeedEvent && entity instanceof EntityPlayer) {
         f = ForgeEventFactory.getBreakSpeed((EntityPlayer)entity, state, f, pos);
      }

      return f < 0.0F ? 0.0F : f;
   }

   public static ImmutableList<BlockPos> getBlockList(ItemStack stack, World world, EntityPlayer player, BlockPos origin, int width, int height, int depth) {
      return getBlockList(stack, world, player, origin, width, height, depth, -1, (Predicate)null);
   }

   public static ImmutableList<BlockPos> getBlockList(ItemStack stack, World world, EntityPlayer player, BlockPos origin, int width, int height, int depth, Predicate<BlockPos> predicate) {
      return getBlockList(stack, world, player, origin, width, height, depth, -1, predicate);
   }

   public static ImmutableList<BlockPos> getBlockList(ItemStack stack, World world, EntityPlayer player, BlockPos origin, int width, int height, int depth, int distance) {
      return getBlockList(stack, world, player, origin, width, height, depth, distance, (Predicate)null);
   }

   public static ImmutableList<BlockPos> getBlockList(ItemStack stack, World world, EntityPlayer player, BlockPos origin, int width, int height, int depth, int distance, Predicate<BlockPos> predicate) {
      IBlockState state = world.getBlockState(origin);
      if (state.getMaterial() != Material.AIR && canToolHarvestBlock(stack, state)) {
         RayTraceResult targetPoint = RayTraceHelper.rayTrace(world, player, true);
         if (targetPoint == null || !origin.equals(targetPoint.getBlockPos())) {
            targetPoint = RayTraceHelper.rayTrace(world, player, false);
            if (targetPoint == null || !origin.equals(targetPoint.getBlockPos())) {
               return ImmutableList.of();
            }
         }

         BlockPos start = origin;
         int x;
         int y;
         int z;
         switch(targetPoint.sideHit) {
         case DOWN:
         case UP:
            Vec3i vec = player.getHorizontalFacing().getDirectionVec();
            x = vec.getX() * height + vec.getZ() * width;
            y = targetPoint.sideHit.getAxisDirection().getOffset() * -depth;
            z = vec.getX() * width + vec.getZ() * height;
            start = origin.add(-x / 2, 0, -z / 2);
            if (x % 2 == 0) {
               if (x > 0 && targetPoint.hitVec.x - (double)targetPoint.getBlockPos().getX() > 0.5D) {
                  start = start.add(1, 0, 0);
               } else if (x < 0 && targetPoint.hitVec.x - (double)targetPoint.getBlockPos().getX() < 0.5D) {
                  start = start.add(-1, 0, 0);
               }
            }

            if (z % 2 == 0) {
               if (z > 0 && targetPoint.hitVec.z - (double)targetPoint.getBlockPos().getZ() > 0.5D) {
                  start = start.add(0, 0, 1);
               } else if (z < 0 && targetPoint.hitVec.z - (double)targetPoint.getBlockPos().getZ() < 0.5D) {
                  start = start.add(0, 0, -1);
               }
            }
            break;
         case NORTH:
         case SOUTH:
            x = width;
            y = height;
            z = targetPoint.sideHit.getAxisDirection().getOffset() * -depth;
            start = origin.add(-width / 2, -height / 2, 0);
            if (width % 2 == 0 && targetPoint.hitVec.x - (double)targetPoint.getBlockPos().getX() > 0.5D) {
               start = start.add(1, 0, 0);
            }

            if (height % 2 == 0 && targetPoint.hitVec.y - (double)targetPoint.getBlockPos().getY() > 0.5D) {
               start = start.add(0, 1, 0);
            }
            break;
         case WEST:
         case EAST:
            x = targetPoint.sideHit.getAxisDirection().getOffset() * -depth;
            y = height;
            z = width;
            start = origin.add(0, -height / 2, -width / 2);
            if (height % 2 == 0 && targetPoint.hitVec.y - (double)targetPoint.getBlockPos().getY() > 0.5D) {
               start = start.add(0, 1, 0);
            }

            if (width % 2 == 0 && targetPoint.hitVec.z - (double)targetPoint.getBlockPos().getZ() > 0.5D) {
               start = start.add(0, 0, 1);
            }
            break;
         default:
            z = 0;
            y = 0;
            x = 0;
         }

         Builder<BlockPos> builder = ImmutableList.builder();

         for(int xp = start.getX(); xp != start.getX() + x; xp += x / MathHelper.abs(x)) {
            for(int yp = start.getY(); yp != start.getY() + y; yp += y / MathHelper.abs(y)) {
               for(int zp = start.getZ(); zp != start.getZ() + z; zp += z / MathHelper.abs(z)) {
                  if ((xp != origin.getX() || yp != origin.getY() || zp != origin.getZ()) && (distance <= 0 || MathHelper.abs(xp - origin.getX()) + MathHelper.abs(yp - origin.getY()) + MathHelper.abs(zp - origin.getZ()) <= distance)) {
                     BlockPos pos = new BlockPos(xp, yp, zp);
                     if ((predicate == null || predicate.test(pos)) && isToolEffective(stack, world.getBlockState(pos))) {
                        builder.add(pos);
                     }
                  }
               }
            }
         }

         return builder.build();
      } else {
         return ImmutableList.of();
      }
   }

   public static boolean canBreakBlock(ItemStack stack, World world, EntityPlayer player, BlockPos harvestedPos, BlockPos targetPos) {
      return canBreakBlock(stack, world, player, harvestedPos, targetPos, 0);
   }

   public static boolean canBreakBlock(ItemStack stack, World world, EntityPlayer player, BlockPos harvestedPos, BlockPos targetPos, int bonusToolLevel) {
      if (world.isAirBlock(targetPos)) {
         return false;
      } else {
         IBlockState state = world.getBlockState(targetPos);
         Block block = state.getBlock();
         if (!isToolEffective(stack, state)) {
            return false;
         } else {
            IBlockState refState = world.getBlockState(harvestedPos);
            float refStrength = ForgeHooks.blockStrength(refState, player, world, harvestedPos);
            float strength = ForgeHooks.blockStrength(state, player, world, targetPos);
            boolean tooSlow = refStrength / strength > 10.0F;
            if (canPlayerHarvestBlock(block, player, stack, world, targetPos, bonusToolLevel) && !tooSlow) {
               if (player.capabilities.isCreativeMode) {
                  block.onBlockHarvested(world, targetPos, state, player);
                  if (block.removedByPlayer(state, world, targetPos, player, false)) {
                     block.onBlockDestroyedByPlayer(world, targetPos, state);
                  }

                  if (!world.isRemote) {
                     NetworkHandler.sendPacket(player, new SPacketBlockChange(world, targetPos));
                  }

                  return false;
               } else {
                  return true;
               }
            } else {
               return false;
            }
         }
      }
   }

   public static void breakBlock(EntityPlayer entity, ItemStack harvestTool, World world, IBlockState harvestedBlockState, BlockPos harvestedPos, BlockPos targetPos) {
      breakBlock(entity, harvestTool, world, harvestedBlockState, harvestedPos, targetPos, true);
   }

   public static void breakBlock(EntityPlayer entity, ItemStack harvestTool, World world, IBlockState harvestedBlockState, BlockPos harvestedPos, BlockPos targetPos, boolean callBreakEvent) {
      breakBlock(entity, harvestTool, world, harvestedBlockState, harvestedPos, targetPos, callBreakEvent, 0);
   }

   public static void breakBlock(EntityPlayer entity, ItemStack harvestTool, World world, IBlockState harvestedBlockState, BlockPos harvestedPos, BlockPos targetPos, boolean callBreakEvent, int bonusToolLevel) {
      breakBlock(entity, harvestTool, world, harvestedBlockState, harvestedPos, targetPos, callBreakEvent, bonusToolLevel, (Function)null);
   }

   public static void breakBlock(EntityPlayer entity, ItemStack harvestTool, World world, IBlockState harvestedBlockState, BlockPos harvestedPos, BlockPos targetPos, boolean callBreakEvent, int bonusToolLevel, Function<Integer, Integer> handleXP) {
      if (canBreakBlock(harvestTool, world, entity, harvestedPos, targetPos, bonusToolLevel)) {
         IBlockState state = world.getBlockState(targetPos);
         Block block = state.getBlock();
         harvestTool.onBlockDestroyed(world, state, targetPos, entity);
         if (!world.isRemote) {
            int xp = block.getExpDrop(state, world, targetPos, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, harvestTool));
            if (callBreakEvent) {
               xp = ForgeHooks.onBlockBreakEvent(world, ((EntityPlayerMP)entity).interactionManager.getGameType(), (EntityPlayerMP)entity, targetPos);
            }

            if (handleXP != null) {
               xp = ((Integer)handleXP.apply(Integer.valueOf(xp))).intValue();
            }

            if (xp == -1) {
               return;
            }

            TileEntity tileEntity = world.getTileEntity(targetPos);
            if (block.removedByPlayer(state, world, targetPos, entity, true)) {
               block.onBlockDestroyedByPlayer(world, targetPos, state);
               block.harvestBlock(world, entity, targetPos, state, tileEntity, harvestTool);
               block.dropXpOnBlockBreak(world, targetPos, xp);
            }

            NetworkHandler.sendPacket(entity, new SPacketBlockChange(world, targetPos));
         } else {
            world.playBroadcastSound(2001, targetPos, Block.getStateId(state));
            if (block.removedByPlayer(state, world, targetPos, entity, true)) {
               block.onBlockDestroyedByPlayer(world, targetPos, state);
            }

            harvestTool.onBlockDestroyed(world, state, targetPos, entity);
            if (harvestTool.getCount() == 0 && harvestTool == entity.getHeldItemMainhand()) {
               ForgeEventFactory.onPlayerDestroyItem(entity, harvestTool, EnumHand.MAIN_HAND);
               entity.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
            }

            NetHandlerPlayClient netHandlerPlayClient = Minecraft.getMinecraft().getConnection();

            assert netHandlerPlayClient != null;

            netHandlerPlayClient.sendPacket(new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, targetPos, Minecraft.getMinecraft().objectMouseOver.sideHit));
         }

      }
   }
}
