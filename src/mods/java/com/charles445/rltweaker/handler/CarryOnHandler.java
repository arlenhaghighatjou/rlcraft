package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.CarryOnReflect;
import com.charles445.rltweaker.reflect.QuarkReflect;
import com.charles445.rltweaker.util.CompatUtil;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.StackTraceUtil;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CarryOnHandler {
   boolean quarkLoaded = false;
   CarryOnReflect reflector;
   @Nullable
   QuarkReflect reflectorQuark;

   public CarryOnHandler() {
      try {
         this.reflector = new CarryOnReflect();
         this.quarkLoaded = Loader.isModLoaded("quark");

         try {
            if (this.quarkLoaded) {
               this.reflectorQuark = new QuarkReflect();
            }
         } catch (Exception var2) {
            RLTweaker.logger.error("Failed to setup Quark for Carryon");
            ErrorUtil.logSilent("CarryOn Quark Reflector Initialization");
         }

         if (ModConfig.server.carryon.tileEntityDropSafetyCheck) {
            CompatUtil.wrapSpecificHandler("COItemDropped", (x$0) -> {
               new CarryOnHandler.COItemDropped(x$0);
            }, "tschipp.carryon.common.event.ItemEvents", "onItemDropped");
         }

         MinecraftForge.EVENT_BUS.register(this);
      } catch (Exception var3) {
         RLTweaker.logger.error("Failed to setup CarryOnHandler!", var3);
         ErrorUtil.logSilent("CarryOn Critical Setup Failure");
         if (var3 instanceof CriticalException) {
            throw new RuntimeException(var3);
         }
      }

   }

   @SubscribeEvent
   public void onPlaceEvent(PlaceEvent event) {
      if (StackTraceUtil.stackTraceHasClassOfMethod("tschipp.carryon.common.item.ItemTile", "onItemUse", "onItemUse")) {
         BlockSnapshot snapshot = event.getBlockSnapshot();
         ResourceLocation registry = snapshot.getRegistryName();
         World world = event.getWorld();
         EntityPlayer player = event.getPlayer();
         if (this.quarkLoaded && this.reflectorQuark != null && registry.getResourceDomain().equals("quark")) {
            this.handlePlacedQuarkBlock(event, snapshot, registry, world, player);
         }
      }

   }

   public void handlePlacedQuarkBlock(PlaceEvent event, BlockSnapshot snapshot, ResourceLocation registry, World world, EntityPlayer player) {
      Block block = snapshot.getReplacedBlock().getBlock();
      BlockPos pos = snapshot.getPos();
      if (ModConfig.server.carryon.quarkChestFix && this.reflectorQuark.isBlockCustomChest(block)) {
         try {
            ItemStack stack = event.getItemInHand();
            if (stack.hasTagCompound()) {
               NBTTagCompound compound = stack.getTagCompound();
               if (compound.hasKey("tileData")) {
                  NBTTagCompound data = compound.getCompoundTag("tileData");
                  Object type = this.reflectorQuark.getChestTypeFromString(data.getString("type"));
                  if (type != null) {
                     int chestCount = 0;
                     chestCount = chestCount + this.getChestAdjacentCount(block, type, world, pos.north());
                     chestCount = chestCount + this.getChestAdjacentCount(block, type, world, pos.south());
                     chestCount = chestCount + this.getChestAdjacentCount(block, type, world, pos.east());
                     chestCount = chestCount + this.getChestAdjacentCount(block, type, world, pos.west());
                     if (chestCount >= 1) {
                        event.setCanceled(true);
                     }
                  }
               }
            }
         } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var13) {
            var13.printStackTrace();
            ErrorUtil.logSilent("CarryOn Quark BlockCustomChest");
         }
      }

   }

   private int getChestAdjacentCount(Block block, Object type, World world, BlockPos adjacent) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      if (this.checkChestMatch(block, type, world, adjacent)) {
         return this.reflectorQuark.isDoubleChest(block, world, adjacent, type) ? 2 : 1;
      } else {
         return 0;
      }
   }

   private boolean checkChestMatch(Block block, Object type, World world, BlockPos pos) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return world.getBlockState(pos).getBlock() == block && this.reflectorQuark.getCustomChestType(block, world, pos) == type;
   }

   public class COItemDropped {
      private IEventListener handler;

      public COItemDropped(IEventListener handler) {
         this.handler = handler;
         MinecraftForge.EVENT_BUS.register(this);
      }

      @SubscribeEvent(
         priority = EventPriority.HIGH
      )
      public void onItemDropped(EntityJoinWorldEvent event) {
         Vec3d relocate = this.itemDropRoutine(event);
         this.handler.invoke(event);
         if (relocate != null) {
            event.getEntity().setPosition(relocate.x, relocate.y, relocate.z);
         }

      }

      @Nullable
      public Vec3d itemDropRoutine(EntityJoinWorldEvent event) {
         if (!ModConfig.server.carryon.tileEntityDropSafetyCheck) {
            return null;
         } else {
            try {
               if (!(event.getEntity() instanceof EntityItem)) {
                  return null;
               } else {
                  EntityItem entityItem = (EntityItem)event.getEntity();
                  ItemStack stack = entityItem.getItem();
                  Item item = stack.getItem();
                  if (!CarryOnHandler.this.reflector.isItemTile(item)) {
                     return null;
                  } else if (!CarryOnHandler.this.reflector.hasTileData(stack)) {
                     return null;
                  } else {
                     Vec3d vec = entityItem.getPositionVector();
                     BlockPos pos = entityItem.getPosition();
                     World world = event.getWorld();
                     Block itemTileBlock = CarryOnHandler.this.reflector.getItemTileBlock(stack);
                     if (this.isChangeable(world, pos, itemTileBlock)) {
                        return null;
                     } else {
                        for(EnumFacing facing : EnumFacing.VALUES) {
                           BlockPos newPos = pos.offset(facing);
                           if (this.trySetChangeable(world, newPos, itemTileBlock, entityItem)) {
                              RLTweaker.logger.debug("CarryOn tile intercepted for safety purposes... " + pos.toString());
                              return vec;
                           }
                        }

                        RLTweaker.logger.debug("CarryOn tile routine is about to fail, intercepting... " + pos.toString());

                        for(int i = 0; i < 16; ++i) {
                           for(int j = 0; j < 128; ++j) {
                              for(int k = 0; k < 16; ++k) {
                                 if (this.trySetChangeable(world, pos.add(i, j, k), itemTileBlock, entityItem)) {
                                    return vec;
                                 }

                                 if (this.trySetChangeable(world, pos.add(i, -j, k), itemTileBlock, entityItem)) {
                                    return vec;
                                 }

                                 if (this.trySetChangeable(world, pos.add(-i, j, k), itemTileBlock, entityItem)) {
                                    return vec;
                                 }

                                 if (this.trySetChangeable(world, pos.add(-i, -j, k), itemTileBlock, entityItem)) {
                                    return vec;
                                 }

                                 if (this.trySetChangeable(world, pos.add(i, j, -k), itemTileBlock, entityItem)) {
                                    return vec;
                                 }

                                 if (this.trySetChangeable(world, pos.add(i, -j, -k), itemTileBlock, entityItem)) {
                                    return vec;
                                 }

                                 if (this.trySetChangeable(world, pos.add(-i, j, -k), itemTileBlock, entityItem)) {
                                    return vec;
                                 }

                                 if (this.trySetChangeable(world, pos.add(-i, -j, -k), itemTileBlock, entityItem)) {
                                    return vec;
                                 }
                              }
                           }
                        }

                        RLTweaker.logger.error("CarryOn tile was unable to find safe place to be");
                        ErrorUtil.logSilent("CarryOn COItemDropped No Safe Place");
                        return null;
                     }
                  }
               }
            } catch (Exception var14) {
               ErrorUtil.logSilent("CarryOn COItemDropped Exception");
               return null;
            }
         }
      }

      private boolean trySetChangeable(World world, BlockPos newPos, Block itemTileBlock, EntityItem entityItem) {
         if (this.isChangeable(world, newPos, itemTileBlock)) {
            RLTweaker.logger.debug("CarryOn tile is being set to: " + newPos.toString());
            entityItem.setPosition((double)newPos.getX(), (double)newPos.getY(), (double)newPos.getZ());
            return true;
         } else {
            return false;
         }
      }

      private boolean isChangeable(World world, BlockPos pos, Block itemTileBlock) {
         return world.getBlockState(pos).getBlock().isReplaceable(world, pos) && itemTileBlock.canPlaceBlockAt(world, pos) && this.testQuarkChestQuickly(world, pos, itemTileBlock);
      }

      private boolean testQuarkChestQuickly(World world, BlockPos pos, Block itemTileBlock) {
         if (CarryOnHandler.this.reflectorQuark != null && ModConfig.server.carryon.quarkChestFix && CarryOnHandler.this.reflectorQuark.isBlockCustomChest(itemTileBlock)) {
            if (world.getBlockState(pos.north()).getBlock() == itemTileBlock) {
               return false;
            }

            if (world.getBlockState(pos.east()).getBlock() == itemTileBlock) {
               return false;
            }

            if (world.getBlockState(pos.south()).getBlock() == itemTileBlock) {
               return false;
            }

            if (world.getBlockState(pos.west()).getBlock() == itemTileBlock) {
               return false;
            }
         }

         return true;
      }
   }
}
