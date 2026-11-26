package jaredbgreat.dldungeons.builder;

import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import jaredbgreat.dldungeons.api.DLDEvent;
import jaredbgreat.dldungeons.util.debug.Logging;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public final class RegisteredBlock extends AbstractBlock {
   private final String id;
   private final IBlockPlacer block;
   public static final Block quartz = Blocks.QUARTZ_BLOCK;
   public static final Block lapis = Blocks.LAPIS_BLOCK;
   public static final Block water = Blocks.WATER;
   public static final Block air = Blocks.AIR;
   public static final ArrayList<RegisteredBlock> registry = new ArrayList();
   private static HashSet<String> names = new HashSet();

   private static Item getItem(String in) {
      return (Item)Item.REGISTRY.getObject(new ResourceLocation(in));
   }

   private RegisteredBlock(String id) throws NoSuchElementException {
      names.add(id);
      this.id = id;
      this.block = DBlock.makeDBlock(id);
      if (this.block.toString().contains("minecraft:air") && !id.contains("minecraft:air")) {
         String error = "[DLDUNGEONS] ERROR! Block read as \"" + id + "\" parsed into an air block!";
         Logging.logError(error);
         throw new NoSuchElementException(error);
      }
   }

   public static void listDBlocks(VFile2 listsDir) {
      ArrayList<String> namel = new ArrayList();
      names.forEach((namex) -> {
         namel.add(namex);
      });
      Collections.sort(namel);
      VFile2 itemlist = new VFile2(listsDir, "DBlocks.txt");

      try {
         BufferedWriter outstream = new BufferedWriter(new OutputStreamWriter(itemlist.getOutputStream(), StandardCharsets.UTF_8));

         for(String name : namel) {
            outstream.write(name);
            outstream.newLine();
         }

         if (outstream != null) {
            outstream.close();
         }
      } catch (IOException var6) {
         System.err.println("Error: Could not write file blocks.txt");
         var6.printStackTrace();
      }

   }

   private RegisteredBlock(BlockFamily family) throws NoSuchElementException {
      this.id = family.getName();
      this.block = family;
   }

   public static void place(World world, int x, int y, int z, int block) {
      if (!isProtectedBlock(world, x, y, z)) {
         ((RegisteredBlock)registry.get(block)).place(world, x, y, z);
      }

   }

   public static int add(String id) throws NoSuchElementException {
      if (id.startsWith("$")) {
         return add(BlockFamily.getBlockFamily(id));
      } else {
         RegisteredBlock block = new RegisteredBlock(id);
         if (!registry.contains(block)) {
            registry.add(block);
         }

         return registry.indexOf(block);
      }
   }

   public static int add(BlockFamily family) throws NoSuchElementException {
      RegisteredBlock blocks = new RegisteredBlock(family);
      if (!registry.contains(blocks)) {
         registry.add(blocks);
      }

      return registry.indexOf(blocks);
   }

   public static RegisteredBlock get(int id) throws NoSuchElementException {
      return (RegisteredBlock)registry.get(id);
   }

   public static IBlockPlacer getPlacer(int id) throws NoSuchElementException {
      return (IBlockPlacer)registry.get(id);
   }

   public static boolean placeBlock(World world, int x, int y, int z, Block block) {
      if (isProtectedBlock(world, x, y, z)) {
         return false;
      } else {
         BlockPos pos = new BlockPos(x, y, z);
         if (MinecraftForge.TERRAIN_GEN_BUS.post(new DLDEvent.PlaceBlock(world, pos, block))) {
            return false;
         } else {
            world.setBlockState(pos, block.getDefaultState());
            return true;
         }
      }
   }

   public static void placeBlock(World world, int x, int y, int z, Block block, int a, int b) {
      if (!isProtectedBlock(world, x, y, z)) {
         world.setBlockState(new BlockPos(x, y, z), block.getStateFromMeta(a));
      }
   }

   public static void deleteBlock(World world, int x, int y, int z) {
      if (!isProtectedBlock(world, x, y, z)) {
         world.setBlockToAir(new BlockPos(x, y, z));
      }
   }

   public static void deleteBlock(World world, int x, int y, int z, boolean flooded) {
      if (!isProtectedBlock(world, x, y, z)) {
         if (flooded) {
            placeBlock(world, x, y, z, water);
         } else {
            world.setBlockToAir(new BlockPos(x, y, z));
         }

      }
   }

   public static void deleteBlock(World world, int x, int y, int z, int block) {
      if (!isProtectedBlock(world, x, y, z)) {
         if (block > 0) {
            ((RegisteredBlock)registry.get(block)).place(world, x, y, z);
         } else {
            world.setBlockToAir(new BlockPos(x, y, z));
         }

      }
   }

   public static void placeChest(World world, int x, int y, int z) {
      placeBlock(world, x, y, z, chest);
   }

   public static void placeSpawner(World world, int x, int y, int z, String mob) {
      BlockPos pos = new BlockPos(x, y, z);
      if (!MinecraftForge.TERRAIN_GEN_BUS.post(new DLDEvent.BeforePlaceSpawner(world, pos, mob))) {
         if (!isProtectedBlock(world, x, y, z)) {
            if (placeBlock(world, x, y, z, spawner)) {
               TileEntityMobSpawner theSpawner = (TileEntityMobSpawner)world.getTileEntity(pos);
               MobSpawnerBaseLogic logic = theSpawner.getSpawnerBaseLogic();
               NBTTagCompound spawnData = new NBTTagCompound();
               spawnData.setString("id", mob);
               logic.setNextSpawnData(new WeightedSpawnerEntity(1, spawnData));
            }
         }
      }
   }

   public static boolean isGroundBlock(World world, int x, int y, int z) {
      IBlockState bs = world.getBlockState(new BlockPos(x, y, z));
      Material mat = bs.getMaterial();
      return mat == Material.GRASS || mat == Material.IRON || mat == Material.GROUND || mat == Material.SAND || mat == Material.ROCK || mat == Material.CLAY || y < 0;
   }

   public boolean equals(Object other) {
      return !(other instanceof RegisteredBlock) ? false : this.block.equals(((RegisteredBlock)other).block);
   }

   public int hashCode() {
      return this.block.hashCode();
   }

   public void placeNoMeta(World world, int x, int y, int z) {
      this.block.placeNoMeta(world, x, y, z);
   }

   public void place(World world, int x, int y, int z) {
      this.block.place(world, x, y, z);
   }

   public Object getContents() {
      return this.block;
   }
}
