package ivorius.ivtoolkit.world;

import ivorius.ivtoolkit.blocks.IvTileEntityHelper;
import ivorius.ivtoolkit.tools.IvWorldData;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Biomes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface MockWorld extends IBlockAccess {
   static MockWorld.Real of(World world) {
      return new MockWorld.Real(world);
   }

   World asWorld();

   boolean setBlockState(@Nonnull BlockPos var1, @Nonnull IBlockState var2, int var3);

   @Nonnull
   IBlockState getBlockState(@Nonnull BlockPos var1);

   @Nullable
   TileEntity getTileEntity(@Nonnull BlockPos var1);

   void setTileEntity(@Nonnull BlockPos var1, @Nullable TileEntity var2);

   List<Entity> getEntities(AxisAlignedBB var1, @Nullable Predicate<? super Entity> var2);

   boolean addEntity(Entity var1);

   boolean removeEntity(Entity var1);

   Random rand();

   default boolean setBlockState(BlockPos coord, IBlockState block) {
      return this.setBlockState(coord, block, 3);
   }

   @SideOnly(Side.CLIENT)
   default int getCombinedLight(BlockPos pos, int lightValue) {
      return 0;
   }

   default boolean isAirBlock(BlockPos pos) {
      IBlockState state = this.getBlockState(pos);
      return state.getBlock().isAir(state, this, pos);
   }

   @SideOnly(Side.CLIENT)
   default Biome getBiome(BlockPos pos) {
      return Biomes.DEFAULT;
   }

   default int getStrongPower(BlockPos pos, EnumFacing direction) {
      return 0;
   }

   @SideOnly(Side.CLIENT)
   default WorldType getWorldType() {
      return WorldType.CUSTOMIZED;
   }

   default boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
      return this.getBlockState(pos).isSideSolid(this, pos, side);
   }

   public static class Cache extends MockWorld.Real {
      public WorldCache cache;

      public Cache(WorldCache cache) {
         super(cache.world);
         this.cache = cache;
      }

      public boolean setBlockState(@Nonnull BlockPos pos, @Nonnull IBlockState state, int flags) {
         return this.cache.setBlockState(pos, state, flags);
      }

      @Nonnull
      public IBlockState getBlockState(@Nonnull BlockPos pos) {
         return this.cache.getBlockState(pos);
      }
   }

   public static class Real implements MockWorld {
      public World world;

      public Real(World world) {
         this.world = world;
      }

      public World asWorld() {
         return this.world;
      }

      public boolean setBlockState(@Nonnull BlockPos pos, @Nonnull IBlockState state, int flags) {
         return this.world.setBlockState(pos, state, flags);
      }

      @Nonnull
      public IBlockState getBlockState(@Nonnull BlockPos pos) {
         return this.world.getBlockState(pos);
      }

      public TileEntity getTileEntity(@Nonnull BlockPos pos) {
         return this.world.getTileEntity(pos);
      }

      public void setTileEntity(@Nonnull BlockPos pos, TileEntity tileEntity) {
         this.world.setTileEntity(pos, tileEntity);
      }

      public List<Entity> getEntities(AxisAlignedBB bounds, @Nullable Predicate<? super Entity> predicate) {
         return this.world.getEntitiesInAABBexcluding((Entity)null, bounds, predicate != null ? predicate::test : null);
      }

      public boolean addEntity(Entity entity) {
         return this.world.spawnEntity(entity);
      }

      public boolean removeEntity(Entity entity) {
         this.world.removeEntity(entity);
         return true;
      }

      public Random rand() {
         return this.world.rand;
      }

      public int getCombinedLight(BlockPos pos, int lightValue) {
         return this.world.getCombinedLight(pos, lightValue);
      }

      public Biome getBiome(BlockPos pos) {
         return this.world.getBiome(pos);
      }

      public int getStrongPower(BlockPos pos, EnumFacing direction) {
         return this.world.getStrongPower(pos, direction);
      }

      public WorldType getWorldType() {
         return this.world.getWorldType();
      }
   }

   public static class VirtualWorldException extends RuntimeException {
      public VirtualWorldException() {
      }
   }

   public static class WorldData implements MockWorld {
      public IvWorldData worldData;
      public Random random = new Random();

      public WorldData(IvWorldData worldData) {
         this.worldData = worldData;
      }

      public static boolean isAt(@Nonnull BlockPos pos, NBTTagCompound nbt) {
         return pos.getX() == nbt.getInteger("x") && pos.getY() == nbt.getInteger("y") && pos.getZ() == nbt.getInteger("z");
      }

      public World asWorld() {
         throw new MockWorld.VirtualWorldException();
      }

      public boolean setBlockState(@Nonnull BlockPos pos, @Nonnull IBlockState state, int flags) {
         this.worldData.blockCollection.setBlockState(pos, state);
         return true;
      }

      @Nonnull
      public IBlockState getBlockState(@Nonnull BlockPos pos) {
         return this.worldData.blockCollection.getBlockState(pos);
      }

      public TileEntity getTileEntity(@Nonnull BlockPos pos) {
         for(NBTTagCompound nbt : this.worldData.tileEntities) {
            if (isAt(pos, nbt)) {
               return TileEntity.create(IvTileEntityHelper.getAnyWorld(), nbt);
            }
         }

         return null;
      }

      public void setTileEntity(@Nonnull BlockPos pos, TileEntity tileEntity) {
         this.worldData.tileEntities.removeIf((nbt) -> {
            return isAt(pos, nbt);
         });
         this.worldData.tileEntities.add(tileEntity.writeToNBT(new NBTTagCompound()));
      }

      public List<Entity> getEntities(AxisAlignedBB bounds, @Nullable Predicate<? super Entity> predicate) {
         return (List)this.worldData.entities.stream().filter((nbt) -> {
            NBTTagList pos = nbt.getTagList("Pos", 6);
            return bounds.contains(new Vec3d(pos.getDoubleAt(0), pos.getDoubleAt(1), pos.getDoubleAt(2)));
         }).map((nbt) -> {
            return EntityList.createEntityFromNBT(nbt, IvTileEntityHelper.getAnyWorld());
         }).filter(predicate).collect(Collectors.toList());
      }

      public boolean addEntity(Entity entity) {
         this.removeEntity(entity);
         return this.worldData.entities.add(entity.writeToNBT(new NBTTagCompound()));
      }

      public boolean removeEntity(Entity entity) {
         return this.worldData.entities.removeIf((nbt) -> {
            return entity.getUniqueID().equals(nbt.getUniqueId("UUID"));
         });
      }

      public Random rand() {
         return this.random;
      }
   }
}
