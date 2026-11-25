package com.lycanitesmobs.core.item.equipment.features;

import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.helpers.JSONHelper;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDirt.DirtType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.event.ForgeEventFactory;

public class HarvestEquipmentFeature extends EquipmentFeature {
   private static final Set<Block> SPADE_HARVEST = Sets.newHashSet(new Block[]{Blocks.CLAY, Blocks.DIRT, Blocks.FARMLAND, Blocks.GRASS, Blocks.GRAVEL, Blocks.MYCELIUM, Blocks.SAND, Blocks.SNOW, Blocks.SNOW_LAYER, Blocks.SOUL_SAND, Blocks.GRASS_PATH, Blocks.CONCRETE_POWDER});
   private static final Set<Block> PICKAXE_HARVEST = Sets.newHashSet(new Block[]{Blocks.ACTIVATOR_RAIL, Blocks.COAL_ORE, Blocks.COBBLESTONE, Blocks.DETECTOR_RAIL, Blocks.DIAMOND_BLOCK, Blocks.DIAMOND_ORE, Blocks.DOUBLE_STONE_SLAB, Blocks.GOLDEN_RAIL, Blocks.GOLD_BLOCK, Blocks.GOLD_ORE, Blocks.ICE, Blocks.IRON_BLOCK, Blocks.IRON_ORE, Blocks.LAPIS_BLOCK, Blocks.LAPIS_ORE, Blocks.LIT_REDSTONE_ORE, Blocks.MOSSY_COBBLESTONE, Blocks.NETHERRACK, Blocks.PACKED_ICE, Blocks.RAIL, Blocks.REDSTONE_ORE, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.STONE, Blocks.STONE_SLAB, Blocks.STONE_BUTTON, Blocks.STONE_PRESSURE_PLATE});
   private static final Set<Block> AXE_HARVEST = Sets.newHashSet(new Block[]{Blocks.PLANKS, Blocks.BOOKSHELF, Blocks.LOG, Blocks.LOG2, Blocks.CHEST, Blocks.PUMPKIN, Blocks.LIT_PUMPKIN, Blocks.MELON_BLOCK, Blocks.LADDER, Blocks.WOODEN_BUTTON, Blocks.WOODEN_PRESSURE_PLATE});
   public String harvestType;
   public String harvestShape = "block";
   public float harvestSpeed = 1.0F;
   public int harvestLevel = 3;
   public Vec3i harvestRange = new Vec3i(0, 0, 0);

   public HarvestEquipmentFeature() {
   }

   public void loadFromJSON(JsonObject json) {
      super.loadFromJSON(json);
      this.harvestType = json.get("harvestType").getAsString();
      if (json.has("harvestType")) {
         this.harvestType = json.get("harvestType").getAsString();
      }

      if (json.has("harvestSpeed")) {
         this.harvestSpeed = json.get("harvestSpeed").getAsFloat();
      }

      if (json.has("harvestLevel")) {
         this.harvestLevel = json.get("harvestLevel").getAsInt();
      }

      if (json.has("harvestShape")) {
         this.harvestShape = json.get("harvestShape").getAsString();
      }

      this.harvestRange = JSONHelper.getVec3i(json, "harvestRange");
   }

   public String getDescription(ItemStack itemStack, int level) {
      return !this.isActive(itemStack, level) ? null : LanguageManager.translate("equipment.feature." + this.featureType) + " " + this.getSummary(itemStack, level);
   }

   public String getSummary(ItemStack itemStack, int level) {
      if (!this.isActive(itemStack, level)) {
         return null;
      } else {
         String summary = LanguageManager.translate("equipment.harvest.type." + this.harvestType);
         if (this.harvestRange.distanceSq(new Vec3i(0, 0, 0)) > 0.0D) {
            summary = summary + " (" + LanguageManager.translate("equipment.harvest.shape." + this.harvestShape);
            summary = summary + " " + this.getHarvestRangeString(level);
            summary = summary + ")";
         }

         return summary;
      }
   }

   public String getHarvestRangeString(int level) {
      String harvestRangeString = "" + (this.harvestRange.getX() * 2 + 1);
      harvestRangeString = harvestRangeString + "x" + (this.harvestRange.getY() + 1);
      harvestRangeString = harvestRangeString + "x" + (this.harvestRange.getZ() + 1);
      return harvestRangeString;
   }

   public String getToolType() {
      return this.harvestType;
   }

   public boolean canHarvestBlock(IBlockState blockState) {
      Block block = blockState.getBlock();
      Material material = blockState.getMaterial();
      if (material != Material.IRON && material != Material.ANVIL && material != Material.ROCK && !PICKAXE_HARVEST.contains(block)) {
         if (material != Material.WOOD && !AXE_HARVEST.contains(block)) {
            if (material != Material.PLANTS && material != Material.VINE) {
               if (material != Material.WEB && material != Material.LEAVES) {
                  if (material == Material.CLOTH) {
                     return this.harvestType.equalsIgnoreCase("shears");
                  } else if (material != Material.GROUND && material != Material.SAND && material != Material.GRASS && material != Material.SNOW && material != Material.CRAFTED_SNOW && !SPADE_HARVEST.contains(block)) {
                     if (material != Material.CORAL && material != Material.GOURD) {
                        return block == Blocks.TRIPWIRE ? this.harvestType.equalsIgnoreCase("shears") : false;
                     } else {
                        return this.harvestType.equalsIgnoreCase("sword");
                     }
                  } else {
                     return this.harvestType.equalsIgnoreCase("shovel");
                  }
               } else {
                  return this.harvestType.equalsIgnoreCase("sword") || this.harvestType.equalsIgnoreCase("shears");
               }
            } else {
               return this.harvestType.equalsIgnoreCase("axe") || this.harvestType.equalsIgnoreCase("sword") || this.harvestType.equalsIgnoreCase("shears");
            }
         } else {
            return this.harvestType.equalsIgnoreCase("axe");
         }
      } else {
         return this.harvestType.equalsIgnoreCase("pickaxe");
      }
   }

   public float getHarvestSpeed(IBlockState blockState) {
      return !this.canHarvestBlock(blockState) ? 0.0F : this.harvestSpeed * this.getHarvestMultiplier(blockState);
   }

   public float getHarvestMultiplier(IBlockState blockState) {
      Material material = blockState.getMaterial();
      if (material == Material.WEB) {
         return 10.0F;
      } else {
         return (material == Material.LEAVES || material == Material.VINE) && this.harvestType.equalsIgnoreCase("shears") ? 10.0F : 1.0F;
      }
   }

   public void onBlockDestroyed(World world, IBlockState harvestedBlockState, BlockPos harvestedPos, EntityLivingBase livingEntity) {
      if (livingEntity != null && !livingEntity.isSneaking()) {
         EnumFacing facingH = livingEntity.getHorizontalFacing();
         EnumFacing facingLat = facingH.rotateY();
         EnumFacing facing = facingH;
         if (livingEntity.rotationPitch > 45.0F) {
            facing = EnumFacing.DOWN;
         } else if (livingEntity.rotationPitch < -45.0F) {
            facing = EnumFacing.UP;
         }

         Vec3i[][] selectionRanges = new Vec3i[3][2];
         int lon = 0;
         int lat = 1;
         int vert = 2;
         int min = 0;
         int max = 1;
         selectionRanges[lon][min] = new Vec3i(Math.min(0, this.harvestRange.getZ() * facing.getFrontOffsetX()), Math.min(0, this.harvestRange.getZ() * facing.getFrontOffsetY()), Math.min(0, this.harvestRange.getZ() * facing.getFrontOffsetZ()));
         selectionRanges[lon][max] = new Vec3i(Math.max(0, this.harvestRange.getZ() * facing.getFrontOffsetX()), Math.max(0, this.harvestRange.getZ() * facing.getFrontOffsetY()), Math.max(0, this.harvestRange.getZ() * facing.getFrontOffsetZ()));
         selectionRanges[lat][min] = new Vec3i(this.harvestRange.getX() * -Math.abs(facingLat.getFrontOffsetX()), this.harvestRange.getX() * -Math.abs(facingLat.getFrontOffsetY()), this.harvestRange.getX() * -Math.abs(facingLat.getFrontOffsetZ()));
         selectionRanges[lat][max] = new Vec3i(this.harvestRange.getX() * Math.abs(facingLat.getFrontOffsetX()), this.harvestRange.getX() * Math.abs(facingLat.getFrontOffsetY()), this.harvestRange.getX() * Math.abs(facingLat.getFrontOffsetZ()));
         if (facing != EnumFacing.DOWN && facing != EnumFacing.UP) {
            int vertOffset = this.harvestRange.getY() != 0 ? -1 : 0;
            selectionRanges[vert][min] = new Vec3i(0, vertOffset, 0);
            selectionRanges[vert][max] = new Vec3i(0, this.harvestRange.getY() + vertOffset, 0);
         } else {
            selectionRanges[vert][min] = new Vec3i((double)((float)(this.harvestRange.getY() * -Math.abs(facingH.getFrontOffsetX())) * 0.5F), (double)((float)(this.harvestRange.getY() * -Math.abs(facingH.getFrontOffsetY())) * 0.5F), (double)((float)(this.harvestRange.getY() * -Math.abs(facingH.getFrontOffsetZ())) * 0.5F));
            selectionRanges[vert][max] = new Vec3i((double)((float)(this.harvestRange.getY() * Math.abs(facingH.getFrontOffsetX())) * 0.5F), (double)((float)(this.harvestRange.getY() * Math.abs(facingH.getFrontOffsetY())) * 0.5F), (double)((float)(this.harvestRange.getY() * Math.abs(facingH.getFrontOffsetZ())) * 0.5F));
         }

         if (!this.harvestShape.equalsIgnoreCase("block") && !this.harvestShape.equalsIgnoreCase("random")) {
            if (this.harvestShape.equalsIgnoreCase("cross")) {
               for(int longX = selectionRanges[lon][min].getX(); longX <= selectionRanges[lon][max].getX(); ++longX) {
                  for(int longY = selectionRanges[lon][min].getY(); longY <= selectionRanges[lon][max].getY(); ++longY) {
                     for(int longZ = selectionRanges[lon][min].getZ(); longZ <= selectionRanges[lon][max].getZ(); ++longZ) {
                        for(int latX = selectionRanges[lat][min].getX(); latX <= selectionRanges[lat][max].getX(); ++latX) {
                           for(int latY = selectionRanges[lat][min].getY(); latY <= selectionRanges[lat][max].getY(); ++latY) {
                              for(int latZ = selectionRanges[lat][min].getZ(); latZ <= selectionRanges[lat][max].getZ(); ++latZ) {
                                 BlockPos destroyPos = harvestedPos.add(longX, longY, longZ).add(latX, latY, latZ);
                                 if (this.shouldHarvestBlock(world, harvestedBlockState, harvestedPos, destroyPos)) {
                                    world.destroyBlock(destroyPos, true);
                                 }
                              }
                           }
                        }

                        for(int vertX = selectionRanges[vert][min].getX(); vertX <= selectionRanges[vert][max].getX(); ++vertX) {
                           for(int vertY = selectionRanges[vert][min].getY(); vertY <= selectionRanges[vert][max].getY(); ++vertY) {
                              for(int vertZ = selectionRanges[vert][min].getZ(); vertZ <= selectionRanges[vert][max].getZ(); ++vertZ) {
                                 BlockPos destroyPos = harvestedPos.add(longX, longY, longZ).add(vertX, vertY, vertZ);
                                 if (this.shouldHarvestBlock(world, harvestedBlockState, harvestedPos, destroyPos)) {
                                    world.destroyBlock(destroyPos, true);
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }

         } else {
            boolean random = this.harvestShape.equalsIgnoreCase("random");

            for(int longX = selectionRanges[lon][min].getX(); longX <= selectionRanges[lon][max].getX(); ++longX) {
               for(int longY = selectionRanges[lon][min].getY(); longY <= selectionRanges[lon][max].getY(); ++longY) {
                  for(int longZ = selectionRanges[lon][min].getZ(); longZ <= selectionRanges[lon][max].getZ(); ++longZ) {
                     for(int latX = selectionRanges[lat][min].getX(); latX <= selectionRanges[lat][max].getX(); ++latX) {
                        for(int latY = selectionRanges[lat][min].getY(); latY <= selectionRanges[lat][max].getY(); ++latY) {
                           for(int latZ = selectionRanges[lat][min].getZ(); latZ <= selectionRanges[lat][max].getZ(); ++latZ) {
                              for(int vertX = selectionRanges[vert][min].getX(); vertX <= selectionRanges[vert][max].getX(); ++vertX) {
                                 for(int vertY = selectionRanges[vert][min].getY(); vertY <= selectionRanges[vert][max].getY(); ++vertY) {
                                    for(int vertZ = selectionRanges[vert][min].getZ(); vertZ <= selectionRanges[vert][max].getZ(); ++vertZ) {
                                       BlockPos destroyPos = harvestedPos.add(longX, longY, longZ).add(latX, latY, latZ).add(vertX, vertY, vertZ);
                                       if (this.shouldHarvestBlock(world, harvestedBlockState, harvestedPos, destroyPos) && (!random || world.rand.nextBoolean())) {
                                          world.destroyBlock(destroyPos, true);
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }

         }
      }
   }

   public boolean shouldHarvestBlock(World world, IBlockState harvestedBlockState, BlockPos harvestedPos, BlockPos targetPos) {
      if (harvestedPos.equals(targetPos)) {
         return false;
      } else {
         IBlockState targetBlockState = world.getBlockState(targetPos);
         if (targetBlockState.getBlock() != Blocks.REDSTONE_ORE && targetBlockState.getBlock() != Blocks.LIT_REDSTONE_ORE || harvestedBlockState.getBlock() != Blocks.REDSTONE_ORE && harvestedBlockState.getBlock() != Blocks.LIT_REDSTONE_ORE) {
            if (targetBlockState.getBlock() != harvestedBlockState.getBlock()) {
               return false;
            } else {
               return world.getTileEntity(targetPos) != null ? false : this.canHarvestBlock(world.getBlockState(targetPos));
            }
         } else {
            return this.canHarvestBlock(world.getBlockState(targetPos));
         }
      }
   }

   public boolean onBlockUsed(World world, EntityPlayer player, BlockPos pos, ItemStack itemStack, EnumFacing facing) {
      if (!"hoe".equals(this.harvestType)) {
         return false;
      } else if (!player.canPlayerEdit(pos.offset(facing), facing, itemStack)) {
         return false;
      } else {
         world.getBlockState(pos);
         int hook = ForgeEventFactory.onHoeUse(itemStack, player, world, pos);
         if (hook != 0) {
            return false;
         } else {
            IBlockState iblockstate = world.getBlockState(pos);
            Block block = iblockstate.getBlock();
            if (facing != EnumFacing.DOWN && world.isAirBlock(pos.up())) {
               if (block == Blocks.GRASS || block == Blocks.GRASS_PATH) {
                  this.hoeBlock(itemStack, player, world, pos, Blocks.FARMLAND.getDefaultState());
                  return true;
               }

               if (block == Blocks.DIRT) {
                  switch((DirtType)iblockstate.getValue(BlockDirt.VARIANT)) {
                  case DIRT:
                     this.hoeBlock(itemStack, player, world, pos, Blocks.FARMLAND.getDefaultState());
                     return true;
                  case COARSE_DIRT:
                     this.hoeBlock(itemStack, player, world, pos, Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, DirtType.DIRT));
                     return true;
                  }
               }
            }

            return false;
         }
      }
   }

   protected void hoeBlock(ItemStack stack, EntityPlayer player, World worldIn, BlockPos pos, IBlockState state) {
      worldIn.playSound(player, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
      if (!worldIn.isRemote) {
         worldIn.setBlockState(pos, state, 11);
      }

   }

   public boolean onEntityInteraction(EntityPlayer player, EntityLivingBase entity, ItemStack itemStack) {
      if ("shears".equals(this.harvestType) && !player.getEntityWorld().isRemote) {
         if (!(entity instanceof IShearable)) {
            return false;
         } else {
            IShearable target = (IShearable)entity;
            BlockPos pos = new BlockPos(entity.posX, entity.posY, entity.posZ);
            if (target.isShearable(itemStack, entity.world, pos)) {
               List<ItemStack> drops = target.onSheared(itemStack, entity.world, pos, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, itemStack));
               EaglercraftRandom rand = new EaglercraftRandom();

               for(ItemStack stack : drops) {
                  EntityItem ent = entity.entityDropItem(stack, 1.0F);
                  ent.motionY += (double)(rand.nextFloat() * 0.05F);
                  ent.motionX += (double)((rand.nextFloat() - rand.nextFloat()) * 0.1F);
                  ent.motionZ += (double)((rand.nextFloat() - rand.nextFloat()) * 0.1F);
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }
}
