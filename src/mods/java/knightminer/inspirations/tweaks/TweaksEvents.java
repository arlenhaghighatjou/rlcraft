package knightminer.inspirations.tweaks;

import java.util.List;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.network.InspirationsNetwork;
import knightminer.inspirations.common.network.MilkablePacket;
import knightminer.inspirations.library.ItemMetaKey;
import knightminer.inspirations.shared.InspirationsShared;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDirt.DirtType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.items.ItemHandlerHelper;

public class TweaksEvents {
   public TweaksEvents() {
   }

   @SubscribeEvent
   public static void unsaddlePig(EntityInteract event) {
      if (Config.enablePigDesaddle) {
         EntityPlayer player = event.getEntityPlayer();
         ItemStack stack = player.getHeldItem(event.getHand());
         if (player.isSneaking() && stack.isEmpty()) {
            Entity target = event.getTarget();
            if (target instanceof EntityPig) {
               EntityPig pig = (EntityPig)target;
               if (pig.getSaddled()) {
                  pig.setSaddled(false);
                  pig.world.playSound(player, pig.posX, pig.posY, pig.posZ, SoundEvents.ENTITY_PIG_SADDLE, SoundCategory.NEUTRAL, 0.5F, 1.0F);
                  ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(Items.SADDLE), player.inventory.currentItem);
                  event.setCanceled(true);
               }
            }
         }

      }
   }

   @SubscribeEvent
   public static void extraBonemeal(BonemealEvent event) {
      if (Config.bonemealMushrooms || Config.bonemealDeadBush || Config.bonemealGrassSpread || Config.bonemealMyceliumSpread) {
         World world = event.getWorld();
         if (!world.isRemote) {
            BlockPos pos = event.getPos();
            IBlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            if ((!Config.bonemealMushrooms || block != Blocks.MYCELIUM) && (!Config.bonemealDeadBush || block != Blocks.SAND)) {
               if ((Config.bonemealGrassSpread || Config.bonemealMyceliumSpread) && block == Blocks.DIRT && state.getValue(BlockDirt.VARIANT) == DirtType.DIRT && bonemealDirt(world, pos)) {
                  event.setResult(Result.ALLOW);
               }
            } else {
               bonemealPlants(block, world, pos);
               event.setResult(Result.ALLOW);
            }

         }
      }
   }

   private static void bonemealPlants(Block base, World world, BlockPos pos) {
      BlockPos up = pos.up();
      BlockBush bush = Blocks.DEADBUSH;
      IBlockState state = bush.getDefaultState();
      boolean isMycelium = base == Blocks.MYCELIUM;

      for(int i = 0; i < 128; ++i) {
         BlockPos next = up;
         int j = 0;

         while(true) {
            if (j >= i / 16) {
               if (world.isAirBlock(next) && world.rand.nextInt(128) == 0) {
                  if (isMycelium) {
                     bush = world.rand.nextInt(2) == 0 ? Blocks.RED_MUSHROOM : Blocks.BROWN_MUSHROOM;
                     state = bush.getDefaultState();
                  }

                  if (bush.canBlockStay(world, next, state)) {
                     world.setBlockState(next, state);
                  }
               }
               break;
            }

            next = next.add(world.rand.nextInt(3) - 1, (world.rand.nextInt(3) - 1) * world.rand.nextInt(3) / 2, world.rand.nextInt(3) - 1);
            if (world.getBlockState(next.down()).getBlock() != base || world.getBlockState(next).isNormalCube()) {
               break;
            }

            ++j;
         }
      }

   }

   private static boolean bonemealDirt(World world, BlockPos pos) {
      if (world.getLightFromNeighbors(pos.up()) < 9) {
         return false;
      } else {
         int grass = 0;
         int mycelium = 0;

         for(EnumFacing side : EnumFacing.HORIZONTALS) {
            BlockPos offset = pos.offset(side);
            IBlockState state = world.getBlockState(offset);
            Block block = state.getBlock();
            if (block.isAir(state, world, pos)) {
               state = world.getBlockState(offset.down());
               block = state.getBlock();
            } else if (block != Blocks.GRASS && block != Blocks.MYCELIUM) {
               state = world.getBlockState(offset.up());
               block = state.getBlock();
            }

            if (Config.bonemealGrassSpread && block == Blocks.GRASS) {
               ++grass;
            } else if (Config.bonemealMyceliumSpread && block == Blocks.MYCELIUM) {
               ++mycelium;
            }
         }

         if (grass == 0 && mycelium == 0) {
            return false;
         } else if (world.rand.nextInt(5) > Math.max(grass, mycelium) - 1) {
            return true;
         } else {
            if (grass == mycelium && world.rand.nextBoolean()) {
               ++mycelium;
            }

            world.setBlockState(pos, grass >= mycelium ? Blocks.GRASS.getDefaultState() : Blocks.MYCELIUM.getDefaultState());
            return true;
         }
      }
   }

   @SubscribeEvent
   public static void dropHeartbeet(HarvestDropsEvent event) {
      if (Config.enableHeartbeet) {
         IBlockState state = event.getState();
         Block block = state.getBlock();
         if (block == Blocks.BEETROOTS && block instanceof BlockCrops && ((BlockCrops)block).isMaxAge(state)) {
            int rolls = event.getFortuneLevel() + 2;
            if (rolls > 6) {
               rolls = 6;
            }

            List<ItemStack> drops = event.getDrops();

            for(ItemStack stack : drops) {
               if (stack.getItem() == Items.BEETROOT) {
                  for(int i = 0; i < rolls; ++i) {
                     if (event.getWorld().rand.nextInt(Config.heartbeetChance) == 0) {
                        stack.shrink(1);
                        if (stack.isEmpty()) {
                           drops.remove(stack);
                        }

                        drops.add(InspirationsShared.heartbeet.copy());
                        return;
                     }
                  }
               }
            }

         }
      }
   }

   @SubscribeEvent
   public static void dropCarrotsPotatos(HarvestDropsEvent event) {
      if (Config.nerfCarrotPotatoDrops) {
         IBlockState state = event.getState();
         Block block = state.getBlock();
         if ((block == Blocks.CARROTS || block == Blocks.POTATOES) && block instanceof BlockCrops && !((BlockCrops)block).isMaxAge(state)) {
            event.getDrops().clear();
            event.getDrops().add(new ItemStack(block == Blocks.CARROTS ? InspirationsTweaks.carrotSeeds : InspirationsTweaks.potatoSeeds));
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void onFall(LivingFallEvent event) {
      if (Config.lilypadBreakFall) {
         if (event.getDistance() >= 4.0F) {
            EntityLivingBase entity = event.getEntityLiving();
            World world = entity.getEntityWorld();
            if (!world.isRemote) {
               Vec3d vec = entity.getPositionVector();
               if (vec.y % 1.0D <= 0.09375D) {
                  BlockPos blockPos = entity.getPosition();
                  BlockPos[] posList = new BlockPos[4];
                  int i = 0;
                  posList[i++] = blockPos;
                  double x = vec.x % 1.0D;
                  if (x < 0.0D) {
                     ++x;
                  }

                  double z = vec.z % 1.0D;
                  if (z < 0.0D) {
                     ++z;
                  }

                  if (x > 0.7D) {
                     posList[i++] = blockPos.east();
                  } else if (x < 0.3D) {
                     posList[i++] = blockPos.west();
                  }

                  if (z > 0.7D) {
                     posList[i++] = blockPos.south();
                     if (i == 3) {
                        posList[i++] = posList[1].south();
                     }
                  } else if (z < 0.3D) {
                     posList[i++] = blockPos.north();
                     if (i == 3) {
                        posList[i++] = posList[1].north();
                     }
                  }

                  boolean safe = false;

                  for(BlockPos pos : posList) {
                     if (pos != null && world.getBlockState(pos).getBlock() == Blocks.WATERLILY) {
                        world.destroyBlock(pos, true);
                        safe = true;
                     }
                  }

                  if (safe) {
                     event.setDistance(0.0F);
                  }

               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void milkCow(EntityInteract event) {
      if (Config.milkCooldown) {
         Entity target = event.getTarget();
         if (target instanceof EntityCow && !((EntityCow)target).isChild()) {
            ItemStack stack = event.getEntityPlayer().getHeldItem(event.getHand());
            if (Config.milkContainers.contains(new ItemMetaKey(stack))) {
               NBTTagCompound tags = target.getEntityData();
               if (tags.getShort("milk_cooldown") > 0) {
                  event.setCancellationResult(EnumActionResult.PASS);
                  event.setCanceled(true);
               } else {
                  tags.setShort("milk_cooldown", Config.milkCooldownTime);
                  if (!event.getWorld().isRemote) {
                     InspirationsNetwork.sendToClients(event.getWorld(), target.getPosition(), new MilkablePacket(target, false));
                  }
               }
            }

         }
      }
   }
}
