package com.dhanantry.scapeandrunparasites.world.gen.structure;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.state.IBlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class WorldGenStructure extends WorldGenerator {
   public static String structureName;

   public WorldGenStructure(String name) {
      structureName = name;
   }

   public boolean generate(World worldIn, EaglercraftRandom rand, BlockPos position) {
      generate(worldIn, position);
      return true;
   }

   public static void generate(World world, BlockPos pos) {
      MinecraftServer mcServer = world.getMinecraftServer();
      WorldServer worldServer = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0);
      TemplateManager manager = worldServer.getStructureTemplateManager();
      ResourceLocation location = new ResourceLocation("srparasites", structureName);
      Template template = manager.get(mcServer, location);
      if (template != null) {
         IBlockState state = world.getBlockState(pos);
         world.notifyBlockUpdate(pos, state, state, 3);
         PlacementSettings setting = (new PlacementSettings()).setChunk((ChunkPos)null).setIgnoreEntities(false).setIgnoreStructureBlock(false).setMirror(Mirror.NONE).setRotation(Rotation.NONE);
         template.addBlocksToWorldChunk(world, pos, setting);
      }

   }
}
