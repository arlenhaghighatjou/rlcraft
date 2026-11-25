package ivorius.reccomplex.world.gen.feature.structure.schematics;

import ivorius.ivtoolkit.blocks.BlockArea;
import ivorius.ivtoolkit.blocks.BlockStates;
import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.ivtoolkit.transform.Mover;
import ivorius.ivtoolkit.transform.PosTransformer;
import ivorius.ivtoolkit.util.IvStreams;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.temp.RCMover;
import ivorius.reccomplex.temp.RCPosTransformer;
import ivorius.reccomplex.utils.accessor.RCAccessorEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SchematicFile {
   public final List<NBTTagCompound> entityCompounds = new ArrayList();
   public final List<NBTTagCompound> tileEntityCompounds = new ArrayList();
   public short width;
   public short height;
   public short length;
   public Short weOriginX;
   public Short weOriginY;
   public Short weOriginZ;
   public IBlockState[] blockStates;

   public SchematicFile() {
   }

   public SchematicFile(short width, short height, short length) {
      this.width = width;
      this.height = height;
      this.length = length;
      this.blockStates = new IBlockState[width * height * length];
   }

   public SchematicFile(NBTTagCompound tagCompound) throws SchematicFile.UnsupportedSchematicFormatException {
      DataFixer fixer = DataFixesManager.createFixer();
      String materials = tagCompound.getString("Materials");
      if (!materials.equals("Alpha")) {
         throw new SchematicFile.UnsupportedSchematicFormatException(materials);
      } else {
         this.width = tagCompound.getShort("Width");
         this.height = tagCompound.getShort("Height");
         this.length = tagCompound.getShort("Length");
         if (tagCompound.hasKey("WEOriginX", 2)) {
            this.weOriginX = tagCompound.getShort("WEOriginX");
         }

         if (tagCompound.hasKey("WEOriginY", 2)) {
            this.weOriginY = tagCompound.getShort("WEOriginY");
         }

         if (tagCompound.hasKey("WEOriginZ", 2)) {
            this.weOriginZ = tagCompound.getShort("WEOriginZ");
         }

         byte[] metadatas = tagCompound.getByteArray("Data");
         byte[] blockIDs = tagCompound.getByteArray("Blocks");
         byte[] addBlocks = tagCompound.getByteArray("AddBlocks");
         SchematicMapping schematicMapping = tagCompound.hasKey("SchematicaMapping", 10) ? new SchematicMapping(tagCompound.getCompoundTag("SchematicaMapping")) : null;
         this.blockStates = new IBlockState[blockIDs.length];

         for(int i = 0; i < blockIDs.length; ++i) {
            int blockID = blockIDs[i] & 255;
            if (addBlocks.length >= (blockIDs.length + 1) / 2) {
               boolean lowerNybble = (i & 1) == 0;
               blockID |= lowerNybble ? (addBlocks[i >> 1] & 15) << 8 : (addBlocks[i >> 1] & 240) << 4;
            }

            int metadata = metadatas[i];

            try {
               Block block = schematicMapping != null ? schematicMapping.blockFromID(blockID) : Block.getBlockById(blockID);
               this.blockStates[i] = BlockStates.fromMetadata(block, metadata);
            } catch (Exception var12) {
               RecurrentComplex.logger.error("Invalid metadata in schematic file: " + blockID + ":" + metadata, var12);
               this.blockStates[i] = Blocks.AIR.getDefaultState();
            }
         }

         NBTTagList entities = tagCompound.getTagList("Entities", 10);

         for(int i = 0; i < entities.tagCount(); ++i) {
            this.entityCompounds.add(fixer.process(FixTypes.ENTITY, entities.getCompoundTagAt(i)));
         }

         NBTTagList tileEntities = tagCompound.getTagList("TileEntities", 10);

         for(int i = 0; i < tileEntities.tagCount(); ++i) {
            this.tileEntityCompounds.add(fixer.process(FixTypes.BLOCK_ENTITY, tileEntities.getCompoundTagAt(i)));
         }

      }
   }

   public int getBlockIndex(BlockPos pos) {
      return pos.getX() + (pos.getY() * this.length + pos.getZ()) * this.width;
   }

   public IBlockState getBlockState(BlockPos coord) {
      return coord.getX() >= 0 && coord.getY() >= 0 && coord.getZ() >= 0 && coord.getX() < this.width && coord.getY() < this.height && coord.getZ() < this.length ? this.blockStates[this.getBlockIndex(coord)] : Blocks.AIR.getDefaultState();
   }

   public boolean setBlockState(BlockPos coord, IBlockState state) {
      if (coord.getX() >= 0 && coord.getY() >= 0 && coord.getZ() >= 0 && coord.getX() < this.width && coord.getY() < this.height && coord.getZ() < this.length) {
         this.blockStates[this.getBlockIndex(coord)] = state;
         return true;
      } else {
         return false;
      }
   }

   public boolean shouldRenderSide(BlockPos coord, EnumFacing side) {
      return !this.getBlockState(coord.offset(side)).isOpaqueCube();
   }

   public void generate(World world, BlockPos pos) {
      this.generate(world, pos, AxisAlignedTransform2D.ORIGINAL);
   }

   public void generate(World world, BlockPos origin, AxisAlignedTransform2D transform) {
      int[] areaSize = new int[]{this.width, this.height, this.length};
      Map<BlockPos, TileEntity> tileEntities = new HashMap();

      for(NBTTagCompound tileTagCompound : this.tileEntityCompounds) {
         BlockPos src = RCMover.getTileEntityPos(tileTagCompound);
         BlockPos dest = transform.apply(src, areaSize).add(origin);
         tileTagCompound = RCMover.setTileEntityPos(tileTagCompound, dest);
         TileEntity tileEntity = TileEntity.create(world, tileTagCompound);
         if (tileEntity != null) {
            tileEntities.put(src, tileEntity);
         }
      }

      for(int pass = 0; pass < 2; ++pass) {
         for(BlockPos sourcePos : this.area()) {
            int index = this.getBlockIndex(sourcePos);
            IBlockState blockState = PosTransformer.transformBlockState(this.blockStates[index], transform);
            if (blockState != null && this.getPass(blockState) == pass) {
               BlockPos worldPos = origin.add(transform.apply(sourcePos, areaSize));
               world.setBlockState(worldPos, blockState, 2);
               TileEntity tileEntity = (TileEntity)tileEntities.get(sourcePos);
               if (tileEntity != null) {
                  world.setBlockState(worldPos, blockState, 2);
                  RCPosTransformer.transformAdditionalData(tileEntity, transform, areaSize);
                  RCMover.moveAdditionalData(tileEntity, origin);
                  world.setTileEntity(worldPos, tileEntity);
                  tileEntity.updateContainingBlockInfo();
               }
            }
         }
      }

      for(NBTTagCompound entityCompound : this.entityCompounds) {
         Entity entity = EntityList.createEntityFromNBT(entityCompound, world);
         if (entity != null) {
            RCAccessorEntity.setEntityUniqueID(entity, UUID.randomUUID());
            PosTransformer.transformEntityPos(entity, transform, areaSize);
            Mover.moveEntity(entity, origin);
            world.spawnEntity(entity);
         }
      }

   }

   @Nonnull
   public BlockArea area() {
      return BlockArea.areaFromSize(BlockPos.ORIGIN, new int[]{this.width, this.height, this.length});
   }

   private int getPass(IBlockState blockState) {
      return !blockState.isNormalCube() && blockState.getMaterial() != Material.AIR ? 1 : 0;
   }

   public void writeToNBT(NBTTagCompound tagCompound) {
      tagCompound.setString("Materials", "Alpha");
      tagCompound.setShort("Width", this.width);
      tagCompound.setShort("Height", this.height);
      tagCompound.setShort("Length", this.length);
      if (this.weOriginX != null) {
         tagCompound.setShort("WEOriginX", this.weOriginX.shortValue());
      }

      if (this.weOriginY != null) {
         tagCompound.setShort("WEOriginY", this.weOriginY.shortValue());
      }

      if (this.weOriginZ != null) {
         tagCompound.setShort("WEOriginZ", this.weOriginZ.shortValue());
      }

      tagCompound.setByteArray("Data", IvStreams.toByteArray(Stream.of(this.blockStates).mapToInt(BlockStates::toMetadata)));
      byte[] blockIDs = new byte[this.blockStates.length];
      byte[] addBlocks = new byte[(this.blockStates.length + 1) / 2];
      SchematicMapping schematicMapping = new SchematicMapping();

      for(int i = 0; i < this.blockStates.length; ++i) {
         Block block = this.blockStates[i].getBlock();
         int blockID = this.getBlockID(block);
         schematicMapping.putBlock(blockID, block);
         blockIDs[i] = (byte)(blockID & 255);
         boolean lowerNybble = (i & 1) == 0;
         addBlocks[i >> 1] |= lowerNybble ? (byte)(blockID >> 8 & 15) : (byte)(blockID >> 4 & 240);
      }

      tagCompound.setByteArray("Blocks", blockIDs);
      tagCompound.setByteArray("AddBlocks", addBlocks);
      tagCompound.setTag("SchematicaMapping", schematicMapping.writeToNBT());
      NBTTagList entities = new NBTTagList();
      this.entityCompounds.forEach(entities::appendTag);
      tagCompound.setTag("Entities", entities);
      NBTTagList tileEntitites = new NBTTagList();
      this.tileEntityCompounds.forEach(entities::appendTag);
      tagCompound.setTag("TileEntities", tileEntitites);
   }

   private int getBlockID(Block block) {
      return Block.REGISTRY.getIDForObject(block);
   }

   public static class UnsupportedSchematicFormatException extends Exception {
      public final String format;

      public UnsupportedSchematicFormatException(Throwable cause, String format) {
         super("Unsupported Schematic Format: " + format, cause);
         this.format = format;
      }

      public UnsupportedSchematicFormatException(String format) {
         this.format = format;
      }
   }
}
