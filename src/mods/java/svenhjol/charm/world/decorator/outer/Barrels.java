package svenhjol.charm.world.decorator.outer;

import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmLootTables;
import svenhjol.charm.crafting.feature.Barrel;
import svenhjol.charm.crafting.feature.Composter;
import svenhjol.charm.crafting.tile.TileBarrel;
import svenhjol.charm.world.compat.FutureMcBlocks;
import svenhjol.charm.world.compat.ItemHandlerLootTableFiller;
import svenhjol.charm.world.feature.VillageDecorations;
import svenhjol.meson.decorator.MesonOuterDecorator;

public class Barrels extends MesonOuterDecorator {
   public Barrels(World world, BlockPos pos, EaglercraftRandom rand, List<ChunkPos> chunks) {
      super(world, pos, rand, chunks);
   }

   public void generate() {
      if (Charm.hasFeature(Barrel.class) || Charm.hasFeature(Composter.class)) {
         ResourceLocation loot = CharmLootTables.VILLAGE_FARMER;
         int max = 1;

         for(int i = 0; i < max; ++i) {
            if ((double)this.rand.nextFloat() >= VillageDecorations.barrelsWeight) {
               int xx = this.rand.nextInt(16) + 8;
               int zz = this.rand.nextInt(16) + 8;
               BlockPos current = this.world.getHeight(this.pos.add(xx, 0, zz));
               boolean airAbove = this.world.getBlockState(current) == Blocks.AIR.getDefaultState();
               boolean grassBelow = this.world.getBlockState(current.offset(EnumFacing.DOWN)) == Blocks.GRASS.getDefaultState();
               if (airAbove && grassBelow) {
                  if (this.rand.nextFloat() < 0.5F) {
                     if (Charm.hasFeature(Barrel.class)) {
                        this.world.setBlockState(current, Barrel.block.getDefaultState());
                        TileEntity tile = this.world.getTileEntity(current);
                        if (tile instanceof TileBarrel) {
                           ((TileBarrel)tile).setLootTable(loot);
                        }
                     } else if (FutureMcBlocks.barrel != null) {
                        this.world.setBlockState(current, FutureMcBlocks.barrel.getDefaultState());
                        if (this.world.isRemote) {
                           TileEntity tile = this.world.getTileEntity(current);
                           if (tile != null) {
                              IItemHandler capability = (IItemHandler)tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null);
                              if (capability != null) {
                                 ItemHandlerLootTableFiller.fillWithLoot(capability, this.world, loot, 0);
                              }
                           }
                        }
                     }
                  } else if (Charm.hasFeature(Composter.class)) {
                     this.world.setBlockState(current, Composter.composter.getDefaultState());
                  } else if (FutureMcBlocks.composter != null) {
                     this.world.setBlockState(current, FutureMcBlocks.composter.getDefaultState());
                  }
               }
            }
         }

      }
   }
}
