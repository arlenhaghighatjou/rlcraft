package vazkii.quark.world.world;

import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.fml.common.IWorldGenerator;
import vazkii.quark.base.handler.DimensionConfig;
import vazkii.quark.world.entity.EntityPirate;
import vazkii.quark.world.feature.PirateShips;

public class PirateShipGenerator implements IWorldGenerator {
   private final DimensionConfig dims;

   public PirateShipGenerator(DimensionConfig dims) {
      this.dims = dims;
   }

   public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
      if (world instanceof WorldServer && this.dims.canSpawnHere(world)) {
         WorldServer sWorld = (WorldServer)world;
         int x = chunkX * 16 + random.nextInt(16);
         int z = chunkZ * 16 + random.nextInt(16);
         BlockPos xzPos = new BlockPos(x, 1, z);
         Biome biome = world.getBiomeForCoordsBody(xzPos);
         if ((biome == Biomes.OCEAN || biome == Biomes.DEEP_OCEAN) && random.nextInt(PirateShips.rarity) == 0) {
            BlockPos pos = getTopLiquidBlock(world, new BlockPos(x, 0, z));
            IBlockState state = world.getBlockState(pos.down());
            if (state.getBlock() != Blocks.WATER) {
               return;
            }

            pos = new BlockPos(pos.getX(), pos.getY() - 3, pos.getZ());
            generateShipAt(sWorld, random, pos);
         }

      }
   }

   public static void generateShipAt(WorldServer world, Random random, BlockPos pos) {
      MinecraftServer server = world.getMinecraftServer();
      Template template = world.getStructureTemplateManager().getTemplate(server, PirateShips.SHIP_STRUCTURE);
      PlacementSettings settings = new PlacementSettings();
      settings.setRotation(Rotation.values()[random.nextInt(Rotation.values().length)]);
      BlockPos size = template.getSize();

      for(int x = 0; x < size.getX(); ++x) {
         for(int y = 0; y < size.getY(); ++y) {
            for(int z = 0; z < size.getZ(); ++z) {
               BlockPos checkPos = pos.add(Template.transformedBlockPos(settings, new BlockPos(x, y, z)));
               IBlockState checkState = world.getBlockState(checkPos);
               if (!checkState.getBlock().isAir(checkState, world, checkPos) && checkState.getBlock() != Blocks.WATER) {
                  return;
               }
            }
         }
      }

      template.addBlocksToWorld(world, pos, settings);
      Map<BlockPos, String> dataBlocks = template.getDataBlocks(pos, settings);

      for(Entry<BlockPos, String> entry : dataBlocks.entrySet()) {
         String[] tokens = ((String)entry.getValue()).split(" ");
         if (tokens.length == 0) {
            return;
         }

         BlockPos dataPos = (BlockPos)entry.getKey();
         String var13 = tokens[0];
         byte var14 = -1;
         switch(var13.hashCode()) {
         case -1745938725:
            if (var13.equals("sword_pirate")) {
               var14 = 2;
            }
            break;
         case -1367713539:
            if (var13.equals("cannon")) {
               var14 = 4;
            }
            break;
         case -151640108:
            if (var13.equals("captain_pirate")) {
               var14 = 0;
            }
            break;
         case 93922247:
            if (var13.equals("booty")) {
               var14 = 3;
            }
            break;
         case 2113994350:
            if (var13.equals("bow_pirate")) {
               var14 = 1;
            }
         }

         switch(var14) {
         case 0:
            EntityPirate pirate = new EntityPirate(world);
            pirate.setPosition((double)dataPos.getX() + 0.5D, (double)dataPos.getY(), (double)dataPos.getZ() + 0.5D);
            pirate.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(Items.IRON_CHESTPLATE));
            pirate.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.DIAMOND_SWORD));
            pirate.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(PirateShips.pirate_hat));
            EntityParrot parrot = new EntityParrot(world);
            parrot.setPosition((double)dataPos.getX() + 0.5D, (double)dataPos.getY(), (double)dataPos.getZ() + 0.5D);
            parrot.setVariant(world.rand.nextInt(5));
            world.spawnEntity(pirate);
            world.spawnEntity(parrot);
            parrot.startRiding(pirate);
            break;
         case 1:
            EntityPirate pirate = new EntityPirate(world);
            pirate.setPosition((double)dataPos.getX() + 0.5D, (double)dataPos.getY(), (double)dataPos.getZ() + 0.5D);
            pirate.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
            pirate.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(PirateShips.pirate_hat));
            world.spawnEntity(pirate);
            world.setBlockState(dataPos, Blocks.AIR.getDefaultState());
            break;
         case 2:
            EntityPirate pirate = new EntityPirate(world);
            pirate.setPosition((double)dataPos.getX() + 0.5D, (double)dataPos.getY(), (double)dataPos.getZ() + 0.5D);
            pirate.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
            pirate.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(PirateShips.pirate_hat));
            world.spawnEntity(pirate);
            world.setBlockState(dataPos, Blocks.AIR.getDefaultState());
            break;
         case 3:
            float chance = tokens.length == 3 ? 1.0F : 0.75F;
            if (random.nextFloat() > chance) {
               world.setBlockState(dataPos, Blocks.AIR.getDefaultState());
            } else {
               String chestOrientation = tokens[1];
               EnumFacing chestFacing = EnumFacing.byName(chestOrientation);
               if (chestFacing == null) {
                  chestFacing = EnumFacing.NORTH;
               }

               chestFacing = settings.getRotation().rotate(chestFacing);
               IBlockState chestState = Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, chestFacing);
               world.setBlockState(dataPos, chestState);
               TileEntity tile = world.getTileEntity(dataPos);
               if (tile instanceof TileEntityLockableLoot) {
                  ((TileEntityLockableLoot)tile).setLootTable(PirateShips.PIRATE_CHEST_LOOT_TABLE, random.nextLong());
               }
            }
            break;
         case 4:
            String dispenserOrientation = tokens[1];
            EnumFacing dispenserFacing = EnumFacing.byName(dispenserOrientation);
            if (dispenserFacing == null) {
               dispenserFacing = EnumFacing.NORTH;
            }

            dispenserFacing = settings.getRotation().rotate(dispenserFacing);
            IBlockState dispenserState = Blocks.DISPENSER.getDefaultState().withProperty(BlockDispenser.FACING, dispenserFacing);
            world.setBlockState(dataPos, dispenserState);
            TileEntityDispenser dispenser = (TileEntityDispenser)world.getTileEntity(dataPos);
            if (dispenser != null) {
               dispenser.setInventorySlotContents(random.nextInt(9), new ItemStack(Items.FIRE_CHARGE, 2 + random.nextInt(4)));
            }
         }
      }

   }

   private static BlockPos getTopLiquidBlock(World world, BlockPos pos) {
      Chunk chunk = world.getChunkFromBlockCoords(pos);

      BlockPos checkPos;
      BlockPos nextPos;
      for(checkPos = new BlockPos(pos.getX(), chunk.getTopFilledSegment() + 16, pos.getZ()); checkPos.getY() >= 0; checkPos = nextPos) {
         nextPos = checkPos.down();
         IBlockState state = chunk.getBlockState(nextPos);
         if (state.getBlock() instanceof BlockLiquid) {
            break;
         }
      }

      return checkPos;
   }
}
