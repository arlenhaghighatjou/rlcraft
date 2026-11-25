package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationaryArchitect;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrol;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasite;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nullable;
import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockInfestedRemain extends BlockBush {
   protected static final AxisAlignedBB TALL_GRASS_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D);
   public static final PropertyInteger SOURCE = PropertyInteger.create("source", 0, 1);

   public BlockInfestedRemain(String name) {
      super(Material.VINE);
      this.setRegistryName(name);
      this.setUnlocalizedName("srparasites." + name);
      this.setSoundType(SoundType.PLANT);
      this.setTickRandomly(true);
      this.setDefaultState(this.blockState.getBaseState().withProperty(SOURCE, Integer.valueOf(0)));
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return TALL_GRASS_AABB;
   }

   public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
      return this.getMetaFromState(state) == 1 ? worldIn.getBlockState(pos.down()).isFullBlock() : this.canSustainBush(worldIn.getBlockState(pos.down()));
   }

   protected boolean canSustainBush(IBlockState state) {
      return state.getBlock() == SRPBlocks.optionalDirt || state.getBlock() == SRPBlocks.optionalRub;
   }

   public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
      return true;
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      if (!worldIn.isRemote) {
         if (worldIn.getDifficulty() == EnumDifficulty.PEACEFUL) {
            return;
         }

         if (!worldIn.getBlockState(pos.down()).isFullCube()) {
            worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
            return;
         }

         if (rand.nextDouble() < 0.5D) {
            if (worldIn.getBiome(pos) instanceof BiomeParasite) {
               ParasiteEventWorld.spreadBiomeBlockStain(worldIn, pos, rand);
               return;
            }

            int heart = ParasiteEventWorld.canBiomeStillExist(worldIn, pos, true);
            if (heart > 0) {
               ParasiteEventWorld.SpreadBiome(worldIn, pos, heart);
            }
         }

         if (!SRPConfig.allowMobs) {
            return;
         }

         if (SRPConfigSystems.rsResidueY <= pos.getY()) {
            if (SRPConfigSystems.useEvolution) {
               int chance = this.getEvoResidue(SRPWorldData.get(worldIn).getEvolutionPhase());
               if (chance <= 0) {
                  return;
               }

               if (rand.nextInt(chance) == 0) {
                  if (SRPConfigSystems.rsSkyResidue && !worldIn.canSeeSky(new BlockPos((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D))) {
                     return;
                  }

                  if (ParasiteEventEntity.entityChunkCount(worldIn, pos, EntityPStationaryArchitect.class) >= SRPConfigSystems.rsMaxVenkrolChunk) {
                     return;
                  }

                  AxisAlignedBB axisalignedbb = (new AxisAlignedBB((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1))).grow((double)(SRPConfigMobs.venkrolsiiiRange - 1), (double)SRPConfigMobs.venkrolsiiiRangeY, (double)(SRPConfigMobs.venkrolsiiiRange - 1));
                  List<EntityPStationaryArchitect> moblist = worldIn.getEntitiesWithinAABB(EntityPStationaryArchitect.class, axisalignedbb);
                  if (moblist.isEmpty()) {
                     EntityVenkrol entityOut = new EntityVenkrol(worldIn);
                     entityOut.setPosition((double)pos.getX() + 0.5D, (double)(pos.getY() + 1), (double)pos.getZ() + 0.5D);
                     worldIn.spawnEntity(entityOut);
                     if (SRPConfigSystems.rsSounds) {
                        entityOut.playSound(SRPSounds.VENKROLSI, 4.0F, 1.0F);
                     }
                  }
               }
            } else {
               if (SRPConfigSystems.rsVenkrolChance <= 0) {
                  return;
               }

               if (rand.nextInt(SRPConfigSystems.rsVenkrolChance) == 0) {
                  if (SRPConfigSystems.rsSkyResidue && !worldIn.canSeeSky(new BlockPos((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D))) {
                     return;
                  }

                  ClassInheritanceMultiMap<Entity>[] a = worldIn.getChunkFromBlockCoords(pos).getEntityLists();
                  int v = 0;

                  for(int i = 0; i < a.length; ++i) {
                     if (a[i] != null) {
                        Object[] b = a[i].toArray();

                        for(int x = 0; x < b.length; ++x) {
                           if (b[x] != null) {
                              if (b[x] instanceof EntityPStationaryArchitect) {
                                 ++v;
                              }

                              if (v >= SRPConfigSystems.rsMaxVenkrolChunk) {
                                 return;
                              }
                           }
                        }
                     }
                  }

                  AxisAlignedBB axisalignedbb = (new AxisAlignedBB((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1))).grow((double)(SRPConfigMobs.venkrolsiiiRange - 1), (double)SRPConfigMobs.venkrolsiiiRangeY, (double)(SRPConfigMobs.venkrolsiiiRange - 1));
                  List<EntityPStationaryArchitect> moblist = worldIn.getEntitiesWithinAABB(EntityPStationaryArchitect.class, axisalignedbb);
                  if (moblist.isEmpty()) {
                     EntityVenkrol entityOut = new EntityVenkrol(worldIn);
                     entityOut.setPosition((double)pos.getX() + 0.5D, (double)(pos.getY() + 1), (double)pos.getZ() + 0.5D);
                     if (!entityOut.world.getCollisionBoxes(entityOut, entityOut.getEntityBoundingBox()).isEmpty()) {
                        entityOut.setDead();
                        return;
                     }

                     worldIn.spawnEntity(entityOut);
                     if (SRPConfigSystems.rsSounds) {
                        entityOut.playSound(SRPSounds.VENKROLSI, 4.0F, 1.0F);
                     }
                  }
               }
            }
         }
      }

   }

   private int getEvoResidue(byte in) {
      int q = -5;
      switch(in) {
      case 1:
         q = SRPConfigSystems.phaseResidueOne;
         break;
      case 2:
         q = SRPConfigSystems.phaseResidueTwo;
         break;
      case 3:
         q = SRPConfigSystems.phaseResidueThree;
         break;
      case 4:
         q = SRPConfigSystems.phaseResidueFour;
         break;
      case 5:
         q = SRPConfigSystems.phaseResidueFive;
         break;
      case 6:
         q = SRPConfigSystems.phaseResidueSix;
         break;
      case 7:
         q = SRPConfigSystems.phaseResidueSeven;
         break;
      case 8:
         q = SRPConfigSystems.phaseResidueEight;
      }

      return q;
   }

   public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
      if (!worldIn.isRemote) {
         if (worldIn.rand.nextDouble() < 0.5D && entityIn.ticksExisted % 20 != 0) {
            return;
         }

         if (!(entityIn instanceof EntityParasiteBase) && entityIn instanceof EntityLivingBase) {
            EntityLivingBase target = (EntityLivingBase)entityIn;
            if (!target.isPotionActive(SRPPotions.COTH_E)) {
               if (target.isPotionActive(SRPPotions.EPEL_E)) {
                  if (worldIn.rand.nextDouble() >= SRPConfigSystems.epelChanceCOTH) {
                     target.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
                  }
               } else {
                  target.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
               }
            }
         }
      }

      super.onEntityCollidedWithBlock(worldIn, pos, state, entityIn);
   }

   public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune) {
      return null;
   }

   public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
      super.harvestBlock(worldIn, player, pos, state, te, stack);
   }

   public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
      return false;
   }

   public boolean canUseBonemeal(World worldIn, EaglercraftRandom rand, BlockPos pos, IBlockState state) {
      return false;
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{SOURCE});
   }

   public int getMetaFromState(IBlockState state) {
      return ((Integer)state.getValue(SOURCE)).intValue();
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(SOURCE, Integer.valueOf(meta & 1));
   }
}
