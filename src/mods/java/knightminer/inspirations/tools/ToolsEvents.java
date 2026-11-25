package knightminer.inspirations.tools;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.network.InspirationsNetwork;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.shared.InspirationsShared;
import knightminer.inspirations.tools.item.ItemWaypointCompass;
import net.minecraft.block.Block;
import net.minecraft.block.BlockVine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentThorns;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.oredict.OreDictionary;

public class ToolsEvents {
   public ToolsEvents() {
   }

   @SubscribeEvent
   public static void lockAndUnlock(RightClickBlock event) {
      if (Config.enableLock) {
         EntityPlayer player = event.getEntityPlayer();
         ItemStack stack = player.getHeldItem(event.getHand());
         if (!stack.isEmpty() && stack.getItem() == InspirationsShared.materials) {
            int meta = stack.getMetadata();
            boolean isLock = meta == InspirationsShared.lock.getItemDamage();
            if (isLock || meta == InspirationsShared.key.getItemDamage()) {
               TileEntity te = event.getWorld().getTileEntity(event.getPos());
               if (te instanceof ILockableContainer) {
                  ILockableContainer lockable = (ILockableContainer)te;
                  if (isLock) {
                     if (lockable.isLocked()) {
                        player.sendStatusMessage(new TextComponentTranslation(Util.prefix("lock.fail.locked"), new Object[0]), true);
                     } else if (!stack.hasDisplayName()) {
                        player.sendStatusMessage(new TextComponentTranslation(Util.prefix("lock.fail.blank"), new Object[0]), true);
                     } else {
                        lockable.setLockCode(new LockCode(stack.getDisplayName()));
                        lockable.markDirty();
                        if (!player.capabilities.isCreativeMode) {
                           stack.shrink(1);
                        }

                        player.sendStatusMessage(new TextComponentTranslation(Util.prefix("lock.success"), new Object[0]), true);
                     }

                     event.setCanceled(true);
                     event.setCancellationResult(EnumActionResult.SUCCESS);
                  } else if (player.isSneaking()) {
                     if (lockable.isLocked()) {
                        if (stack.hasDisplayName() && stack.getDisplayName().equals(lockable.getLockCode().getLock())) {
                           LockCode code = lockable.getLockCode();
                           lockable.setLockCode(LockCode.EMPTY_CODE);
                           lockable.markDirty();
                           ItemHandlerHelper.giveItemToPlayer(player, InspirationsShared.lock.copy().setStackDisplayName(code.getLock()));
                           player.sendStatusMessage(new TextComponentTranslation(Util.prefix("unlock.success"), new Object[0]), true);
                        } else {
                           player.sendStatusMessage(new TextComponentTranslation(Util.prefix("unlock.fail.no_match"), new Object[0]), true);
                        }
                     } else {
                        player.sendStatusMessage(new TextComponentTranslation(Util.prefix("unlock.fail.unlocked"), new Object[0]), true);
                     }

                     event.setCanceled(true);
                     event.setCancellationResult(EnumActionResult.SUCCESS);
                  }
               }
            }

         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void vineBreakEvent(BreakEvent event) {
      if (Config.harvestHangingVines) {
         if (!event.isCanceled()) {
            World world = event.getWorld();
            if (!world.isRemote) {
               EntityPlayer player = event.getPlayer();
               if (!player.capabilities.isCreativeMode) {
                  Block block = event.getState().getBlock();
                  if (block instanceof BlockVine) {
                     ItemStack shears = player.getHeldItemMainhand();
                     Item item = shears.getItem();
                     if (item instanceof ItemShears || item.getToolClasses(shears).contains("shears")) {
                        BlockPos pos = event.getPos().down();
                        BlockVine vine = (BlockVine)block;
                        IBlockState state = world.getBlockState(pos);

                        int count;
                        for(count = 0; state.getBlock() == block && vine.isShearable(shears, world, pos) && !vineCanStay(vine, world, state, pos); state = world.getBlockState(pos)) {
                           ++count;

                           for(ItemStack stack : vine.onSheared(shears, world, pos, 0)) {
                              Block.spawnAsEntity(world, pos, stack);
                           }

                           pos = pos.down();
                        }

                        for(int i = 0; i < count; ++i) {
                           pos = pos.up();
                           world.setBlockToAir(pos);
                        }

                     }
                  }
               }
            }
         }
      }
   }

   private static boolean vineCanStay(BlockVine vine, World world, IBlockState state, BlockPos pos) {
      for(EnumFacing side : Plane.HORIZONTAL) {
         if (((Boolean)state.getValue(BlockVine.getPropertyFor(side))).booleanValue() && vine.canAttachTo(world, pos, side.getOpposite())) {
            return true;
         }
      }

      return false;
   }

   @SubscribeEvent
   public static void dropMelon(HarvestDropsEvent event) {
      if (Config.shearsReclaimMelons && event.getState().getBlock() == Blocks.MELON_BLOCK) {
         EntityPlayer player = event.getHarvester();
         if (player != null && !player.capabilities.isCreativeMode) {
            ItemStack shears = player.getHeldItemMainhand();
            Item item = shears.getItem();
            if (item instanceof ItemShears || item.getToolClasses(shears).contains("shears")) {
               List<ItemStack> drops = event.getDrops();
               Iterator<ItemStack> iterator = drops.iterator();
               boolean foundMelon = false;

               while(iterator.hasNext()) {
                  ItemStack stack = (ItemStack)iterator.next();
                  if (stack.getItem() == Items.MELON) {
                     if (!foundMelon) {
                        stack.setCount(9);
                        foundMelon = true;
                     } else {
                        iterator.remove();
                     }
                  }
               }

            }
         }
      }
   }

   @SubscribeEvent
   public static void dropExtraSapling(HarvestDropsEvent event) {
      if (Config.enableCrook) {
         IBlockState state = event.getState();
         Block block = state.getBlock();
         World world = event.getWorld();
         if (block.isLeaves(state, world, event.getPos())) {
            EntityPlayer player = event.getHarvester();
            if (player != null && !player.capabilities.isCreativeMode) {
               ItemStack crook = player.getHeldItemMainhand();
               Item item = crook.getItem();
               if (Config.hoeCrook) {
                  if (!(item instanceof ItemHoe) && !item.getToolClasses(crook).contains("hoe")) {
                     return;
                  }
               } else if (!item.getToolClasses(crook).contains("crook")) {
                  return;
               }

               if (!world.isRemote && Config.hoeCrook) {
                  crook.damageItem(1, player);
               }

               Random rand = world.rand;
               ItemStack sapling = new ItemStack(block.getItemDropped(state, rand, event.getFortuneLevel()), 1, block.damageDropped(state));
               List<ItemStack> drops = event.getDrops();

               for(ItemStack stack : drops) {
                  if (OreDictionary.itemMatches(sapling, stack, false)) {
                     return;
                  }
               }

               if (rand.nextInt(Config.crookChance) == 0) {
                  drops.add(sapling);
               }

            }
         }
      }
   }

   @SubscribeEvent
   public static void breakLeavesFast(BreakSpeed event) {
      if (Config.hoeCrook) {
         EntityPlayer player = event.getEntityPlayer();
         if (player != null && !player.capabilities.isCreativeMode) {
            IBlockState state = event.getState();
            if (state.getBlock().isLeaves(state, player.getEntityWorld(), event.getPos())) {
               ItemStack stack = player.getHeldItemMainhand();
               Item item = stack.getItem();
               if (item instanceof ItemHoe || item.getToolClasses(stack).contains("hoe")) {
                  event.setNewSpeed(2.0F);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void setWaypoint(RightClickBlock event) {
      ItemStack stack = event.getItemStack();
      Item item = stack.getItem();
      if (ItemWaypointCompass.isWaypointCompass(stack)) {
         World world = event.getWorld();
         BlockPos pos = event.getPos();
         TileEntity te = world.getTileEntity(pos);
         if (te instanceof TileEntityBeacon && ((TileEntityBeacon)te).isComplete) {
            if (!world.isRemote) {
               ItemStack newStack = new ItemStack(InspirationsTools.waypointCompass, 1, stack.getMetadata());
               ItemWaypointCompass.setNBT(newStack, world, pos);
               if (stack.hasDisplayName()) {
                  newStack.setStackDisplayName(stack.getDisplayName());
               }

               stack.shrink(1);
               EntityPlayer player = event.getEntityPlayer();
               if (stack.isEmpty()) {
                  player.setHeldItem(event.getHand(), newStack);
               } else {
                  ItemHandlerHelper.giveItemToPlayer(player, newStack);
               }
            }

            event.setCanceled(true);
            event.setCancellationResult(EnumActionResult.SUCCESS);
         }

      }
   }

   @SubscribeEvent
   public static void onShieldHit(LivingAttackEvent event) {
      if (Config.moreShieldEnchantments) {
         EntityLivingBase target = event.getEntityLiving();
         if (!target.world.isRemote && target.isActiveItemStackBlocking()) {
            ItemStack stack = target.getActiveItemStack();
            int thorns = EnchantmentHelper.getEnchantmentLevel(Enchantments.THORNS, stack);
            int fire = EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_ASPECT, stack);
            int knockback = EnchantmentHelper.getEnchantmentLevel(Enchantments.KNOCKBACK, stack);
            if (thorns != 0 || fire != 0 || knockback != 0) {
               DamageSource source = event.getSource();
               Entity attacker = source.getImmediateSource();
               if (attacker != null && !target.isEntityInvulnerable(source) && target.canBlockDamageSource(source)) {
                  if (thorns > 0 && EnchantmentThorns.shouldHit(thorns, target.world.rand)) {
                     attacker.attackEntityFrom(DamageSource.causeThornsDamage(target), (float)EnchantmentThorns.getDamage(thorns, target.world.rand));
                     stack.damageItem(1, target);
                  }

                  if (fire > 0) {
                     attacker.setFire(fire * 4);
                  }

                  if (knockback > 0) {
                     if (attacker instanceof EntityLivingBase) {
                        ((EntityLivingBase)attacker).knockBack(target, (float)knockback * 0.5F, (double)MathHelper.sin(target.rotationYaw * 0.017453292F), (double)(-MathHelper.cos(target.rotationYaw * 0.017453292F)));
                        if (attacker instanceof EntityPlayerMP) {
                           InspirationsNetwork.sendPacket(attacker, new SPacketEntityVelocity(attacker));
                        }
                     } else {
                        attacker.addVelocity((double)(-MathHelper.sin(target.rotationYaw * 0.017453292F) * (float)knockback * 0.5F), 0.1D, (double)(MathHelper.cos(target.rotationYaw * 0.017453292F) * (float)knockback * 0.5F));
                     }
                  }
               }

            }
         }
      }
   }
}
