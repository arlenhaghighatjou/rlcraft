package com.ferreusveritas.dynamictrees.proxy;

import com.ferreusveritas.dynamictrees.ModBlocks;
import com.ferreusveritas.dynamictrees.ModItems;
import com.ferreusveritas.dynamictrees.ModTrees;
import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.client.ModelHelper;
import com.ferreusveritas.dynamictrees.api.treedata.ILeavesProperties;
import com.ferreusveritas.dynamictrees.api.treedata.ITreePart;
import com.ferreusveritas.dynamictrees.blocks.BlockBonsaiPot;
import com.ferreusveritas.dynamictrees.blocks.BlockBranchCactus;
import com.ferreusveritas.dynamictrees.blocks.BlockDynamicLeaves;
import com.ferreusveritas.dynamictrees.blocks.BlockRooty;
import com.ferreusveritas.dynamictrees.blocks.BlockTrunkShell;
import com.ferreusveritas.dynamictrees.blocks.LeavesPaging;
import com.ferreusveritas.dynamictrees.blocks.LeavesPropertiesJson;
import com.ferreusveritas.dynamictrees.blocks.MimicProperty;
import com.ferreusveritas.dynamictrees.client.BlockColorMultipliers;
import com.ferreusveritas.dynamictrees.client.QuadManipulator;
import com.ferreusveritas.dynamictrees.client.TextureUtils;
import com.ferreusveritas.dynamictrees.entities.EntityFallingTree;
import com.ferreusveritas.dynamictrees.event.BlockBreakAnimationClientHandler;
import com.ferreusveritas.dynamictrees.event.ModelBakeEventListener;
import com.ferreusveritas.dynamictrees.event.TextureGenerationHandler;
import com.ferreusveritas.dynamictrees.items.DendroPotion;
import com.ferreusveritas.dynamictrees.models.loaders.ModelLoaderBlockBranchBasic;
import com.ferreusveritas.dynamictrees.models.loaders.ModelLoaderBlockBranchCactus;
import com.ferreusveritas.dynamictrees.models.loaders.ModelLoaderBlockBranchThick;
import com.ferreusveritas.dynamictrees.models.loaders.ModelLoaderBlockSurfaceRoot;
import com.ferreusveritas.dynamictrees.models.loaders.ModelLoaderSapling;
import com.ferreusveritas.dynamictrees.render.RenderFallingTree;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.trees.TreeFamily;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap.Builder;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
   public ClientProxy() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      this.registerJsonColorMultipliers();
      this.registerClientEventHandlers();
      this.registerEntityRenderers();
   }

   public void init(FMLInitializationEvent event) {
      super.init(event);
      this.registerColorHandlers();
      MinecraftForge.EVENT_BUS.register(BlockBreakAnimationClientHandler.instance);
   }

   public void postInit() {
      super.postInit();
      this.discoverWoodColors();
      LeavesPropertiesJson.postInitClient();
   }

   private void discoverWoodColors() {
      Function<ResourceLocation, EaglerTextureAtlasSprite> bakedTextureGetter = (location) -> {
         return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
      };

      for(TreeFamily family : (List)Species.REGISTRY.getValuesCollection().stream().map((s) -> {
         return s.getFamily();
      }).distinct().collect(Collectors.toList())) {
         family.woodColor = 16773550;
         if (family != TreeFamily.NULLFAMILY) {
            IBlockState state = family.getPrimitiveLog();
            if (state.getBlock() != Blocks.AIR) {
               IModel model = QuadManipulator.getModelForState(state);
               ResourceLocation resloc = QuadManipulator.getModelTexture(model, bakedTextureGetter, state, EnumFacing.UP);
               if (resloc != null) {
                  TextureUtils.PixelBuffer pixbuf = new TextureUtils.PixelBuffer((EaglerTextureAtlasSprite)bakedTextureGetter.apply(resloc));
                  int u = pixbuf.w / 16;
                  TextureUtils.PixelBuffer center = new TextureUtils.PixelBuffer(u * 8, u * 8);
                  pixbuf.blit(center, u * -8, u * -8);
                  family.woodColor = center.averageColor();
               }
            }
         }
      }

   }

   public void cleanUp() {
      super.cleanUp();
      BlockColorMultipliers.cleanUp();
   }

   public void registerModels() {
      LeavesPropertiesJson.resolveAll();
      ModelLoader.setCustomStateMapper(ModBlocks.blockRootyDirt, (new Builder()).ignore(new IProperty[]{BlockRooty.LIFE}).build());
      ModelLoader.setCustomStateMapper(ModBlocks.blockRootyDirtSpecies, (new Builder()).ignore(new IProperty[]{BlockRooty.LIFE}).build());
      ModelLoader.setCustomStateMapper(ModBlocks.blockRootySand, (new Builder()).ignore(new IProperty[]{BlockRooty.LIFE}).build());

      for(DendroPotion.DendroPotionType type : DendroPotion.DendroPotionType.values()) {
         ModelHelper.regModel(ModItems.dendroPotion, type.getIndex());
      }

      ModelHelper.regModel(ModItems.dirtBucket);
      ModelHelper.regModel(ModItems.treeStaff);

      for(TreeFamily tree : ModTrees.baseFamilies) {
         ModelHelper.regModel(tree.getDynamicBranch());
         ModelHelper.regModel(tree.getCommonSpecies().getSeed());
         ModelHelper.regModel(tree);
      }

      ModelHelper.setGenericStateMapper(ModBlocks.blockDynamicSapling, new ModelResourceLocation(new ResourceLocation("dynamictrees", "sapling"), ""));
      ModelLoader.setCustomStateMapper(ModBlocks.blockTrunkShell, (new Builder()).ignore(new IProperty[]{BlockTrunkShell.COREDIR}).build());
      ModelLoader.setCustomStateMapper(ModTrees.dynamicCactus.getDynamicBranch(), (new Builder()).ignore(new IProperty[]{BlockBranchCactus.TRUNK, BlockBranchCactus.ORIGIN}).build());
      ModelHelper.regModel(ModTrees.dynamicCactus.getDynamicBranch());
      ModelHelper.regModel(ModTrees.dynamicCactus.getCommonSpecies().getSeed());
      ModelHelper.regModel(((Species)Species.REGISTRY.getValue(new ResourceLocation("dynamictrees", "apple"))).getSeed());
      LeavesPaging.setStateMappers();
      ModelLoaderRegistry.registerLoader(new ModelLoaderBlockBranchBasic());
      ModelLoaderRegistry.registerLoader(new ModelLoaderBlockBranchCactus());
      ModelLoaderRegistry.registerLoader(new ModelLoaderBlockBranchThick());
      ModelLoaderRegistry.registerLoader(new ModelLoaderBlockSurfaceRoot());
      ModelLoaderRegistry.registerLoader(new ModelLoaderSapling());
   }

   private boolean isValid(IBlockAccess access, BlockPos pos) {
      return access != null && pos != null;
   }

   public void registerColorHandlers() {
      int white = -1;
      int magenta = 16711935;
      BlockColors blockColors = Minecraft.getMinecraft().getBlockColors();
      blockColors.registerBlockColorHandler((state, world, pos, tintIndex) -> {
         if (state.getBlock() instanceof BlockRooty) {
            BlockRooty blockRooty = (BlockRooty)state.getBlock();
            switch(tintIndex) {
            case 0:
               IBlockState muse = blockRooty.getMimic(world, pos);
               if (!(muse instanceof MimicProperty.IMimic)) {
                  return blockColors.colorMultiplier(muse, world, pos, tintIndex);
               }
            case 1:
               return blockRooty.rootColor(state, world, pos);
            default:
               return -1;
            }
         } else {
            return -1;
         }
      }, new Block[]{ModBlocks.blockRootyDirt, ModBlocks.blockRootyDirtSpecies, ModBlocks.blockRootySand, ModBlocks.blockRootyDirtFake});
      ModelHelper.regColorHandler(ModBlocks.blockDynamicSapling, (state, access, pos, tintIndex) -> {
         return this.isValid(access, pos) ? ModBlocks.blockDynamicSapling.getSpecies(access, pos, state).saplingColorMultiplier(state, access, pos, tintIndex) : -1;
      });
      ModelHelper.regColorHandler(ModBlocks.blockBonsaiPot, (state, access, pos, tintIndex) -> {
         return this.isValid(access, pos) && state.getBlock() instanceof BlockBonsaiPot ? ModBlocks.blockBonsaiPot.getSpecies(access, pos).saplingColorMultiplier(state, access, pos, tintIndex) : -1;
      });
      ModelHelper.regColorHandler(ModItems.dendroPotion, (stack, tint) -> {
         return ModItems.dendroPotion.getColor(stack, tint);
      });
      ModelHelper.regColorHandler(ModItems.treeStaff, (stack, tint) -> {
         return ModItems.treeStaff.getColor(stack, tint);
      });

      for(BlockDynamicLeaves leaves : LeavesPaging.getLeavesMapForModId("dynamictrees").values()) {
         ModelHelper.regColorHandler(leaves, (state, worldIn, pos, tintIndex) -> {
            return TreeHelper.isLeaves(state.getBlock()) ? ((BlockDynamicLeaves)state.getBlock()).getProperties(state).foliageColorMultiplier(state, worldIn, pos) : 16711935;
         });
      }

   }

   public void registerJsonColorMultipliers() {
      BlockColorMultipliers.register("birch", (state, worldIn, pos, tintIndex) -> {
         return ColorizerFoliage.getFoliageColorBirch();
      });
      BlockColorMultipliers.register("spruce", (state, worldIn, pos, tintIndex) -> {
         return ColorizerFoliage.getFoliageColorPine();
      });
   }

   public void registerClientEventHandlers() {
      MinecraftForge.EVENT_BUS.register(new ModelBakeEventListener());
      MinecraftForge.EVENT_BUS.register(TextureGenerationHandler.class);
   }

   public void registerEntityRenderers() {
      RenderingRegistry.registerEntityRenderingHandler(EntityFallingTree.class, new RenderFallingTree.Factory());
   }

   public int getFoliageColor(ILeavesProperties leavesProperties, World world, IBlockState blockState, BlockPos pos) {
      return leavesProperties.foliageColorMultiplier(blockState, world, pos);
   }

   public void addDustParticle(World world, double fx, double fy, double fz, double mx, double my, double mz, IBlockState blockState, float r, float g, float b) {
      if (world.isRemote) {
         Particle particle = Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(EnumParticleTypes.BLOCK_DUST.getParticleID(), fx, fy, fz, mx, my, mz, new int[]{Block.getStateId(blockState)});
         particle.setRBGColorF(r, g, b);
      }

   }

   public void spawnParticle(World world, EnumParticleTypes particleType, double x, double y, double z, double mx, double my, double mz) {
      if (world.isRemote) {
         world.spawnParticle(particleType, x, y, z, mx, my, mz, new int[0]);
      }

   }

   public void crushLeavesBlock(World world, BlockPos pos, IBlockState blockState, Entity entity) {
      if (world.isRemote) {
         EaglercraftRandom random = world.rand;
         ITreePart treePart = TreeHelper.getTreePart(blockState);
         if (treePart instanceof BlockDynamicLeaves) {
            BlockDynamicLeaves leaves = (BlockDynamicLeaves)treePart;
            ILeavesProperties leavesProperties = leaves.getProperties(blockState);
            int color = this.getFoliageColor(leavesProperties, world, blockState, pos);
            float r = (float)(color >> 16 & 255) / 255.0F;
            float g = (float)(color >> 8 & 255) / 255.0F;
            float b = (float)(color & 255) / 255.0F;

            for(int dz = 0; dz < 8; ++dz) {
               for(int dy = 0; dy < 8; ++dy) {
                  for(int dx = 0; dx < 8; ++dx) {
                     if (random.nextInt(8) == 0) {
                        double fx = (double)pos.getX() + (double)dx / 8.0D;
                        double fy = (double)pos.getY() + (double)dy / 8.0D;
                        double fz = (double)pos.getZ() + (double)dz / 8.0D;
                        this.addDustParticle(world, fx, fy, fz, 0.0D, (double)random.nextFloat() * entity.motionY, 0.0D, blockState, r, g, b);
                     }
                  }
               }
            }
         }
      }

   }
}
