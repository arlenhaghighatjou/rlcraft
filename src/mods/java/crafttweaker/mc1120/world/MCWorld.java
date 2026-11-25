package crafttweaker.mc1120.world;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.block.IBlock;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.data.IData;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.util.Position3f;
import crafttweaker.api.world.IBiome;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IExplosion;
import crafttweaker.api.world.IRayTraceResult;
import crafttweaker.api.world.IVector3d;
import crafttweaker.api.world.IWorld;
import crafttweaker.api.world.IWorldInfo;
import crafttweaker.api.world.IWorldProvider;
import crafttweaker.mc1120.data.NBTConverter;
import crafttweaker.mc1120.util.MCPosition3f;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import stanhebben.zenscript.annotations.Optional;

public class MCWorld extends MCBlockAccess implements IWorld {
   private final World world;

   public MCWorld(World world) {
      super(world);
      this.world = world;
   }

   public int getBrightness(int x, int y, int z) {
      return this.world.getLight(new BlockPos(x, y, z));
   }

   public int getBrightness(IBlockPos pos) {
      return this.world.getLight((BlockPos)pos.getInternal());
   }

   public IBlock getBlock(int x, int y, int z) {
      return CraftTweakerMC.getBlock(this.world, x, y, z);
   }

   public IBlock getBlock(IBlockPos pos) {
      return CraftTweakerMC.getBlock(this.world, pos.getX(), pos.getY(), pos.getZ());
   }

   public IBiome getBiome(Position3f position) {
      return this.getBiome(position.asBlockPos());
   }

   public IBiome getBiome(IBlockPos position) {
      return new MCBiome(this.world.getBiome((BlockPos)position.getInternal()));
   }

   public IWorldInfo getWorldInfo() {
      return new MCWorldInfo(this.world.getWorldInfo());
   }

   public boolean isRemote() {
      return this.world.isRemote;
   }

   public boolean isRaining() {
      return this.world.isRaining();
   }

   public boolean isDayTime() {
      return this.world.isDaytime();
   }

   public boolean isSurfaceWorld() {
      return this.world.provider.isSurfaceWorld();
   }

   public long getWorldTime() {
      return this.world.getTotalWorldTime();
   }

   public int getMoonPhase() {
      return this.world.provider.getMoonPhase(this.getWorldTime());
   }

   public int getDimension() {
      return this.world.provider.getDimension();
   }

   public String getDimensionType() {
      return this.world.provider.getDimensionType().getName();
   }

   public String getWorldType() {
      return this.world.getWorldType().getName();
   }

   public Object getInternal() {
      return this.world;
   }

   public boolean spawnEntity(IEntity entity) {
      return this.world.spawnEntity(CraftTweakerMC.getEntity(entity));
   }

   public void removeEntity(IEntity entity) {
      this.world.removeEntity(CraftTweakerMC.getEntity(entity));
   }

   public IRayTraceResult rayTraceBlocks(IVector3d begin, IVector3d ray, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
      return CraftTweakerMC.getIRayTraceResult(this.world.rayTraceBlocks(CraftTweakerMC.getVec3d(begin), CraftTweakerMC.getVec3d(ray), stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock));
   }

   public List<IEntity> getEntitiesInArea(Position3f start, @Optional Position3f end) {
      if (end == null) {
         end = new MCPosition3f(start.getX() + 1.0F, start.getY() + 1.0F, start.getZ() + 1.0F);
      }

      return (List)this.world.getEntitiesWithinAABBExcludingEntity((Entity)null, new AxisAlignedBB((BlockPos)start.asBlockPos().getInternal(), (BlockPos)end.asBlockPos().getInternal())).stream().map(CraftTweakerMC::getIEntity).collect(Collectors.toList());
   }

   public boolean setBlockState(IBlockState state, IBlockPos pos) {
      return this.world.setBlockState((BlockPos)pos.getInternal(), (net.minecraft.block.state.IBlockState)state.getInternal());
   }

   public boolean setBlockState(IBlockState state, IData tileEntityData, IBlockPos pos) {
      boolean placed = this.world.setBlockState((BlockPos)pos.getInternal(), (net.minecraft.block.state.IBlockState)state.getInternal());
      if (tileEntityData != null) {
         NBTTagCompound tileEntityNBT = (NBTTagCompound)NBTConverter.from(tileEntityData);
         if (!tileEntityNBT.hasNoTags()) {
            TileEntity tileEntity = this.world.getTileEntity((BlockPos)pos.getInternal());
            if (tileEntity != null) {
               NBTTagCompound currentNBT = tileEntity.writeToNBT(new NBTTagCompound());
               NBTTagCompound originalNBT = currentNBT.copy();
               currentNBT.merge(tileEntityNBT.copy());
               currentNBT.setInteger("x", pos.getX());
               currentNBT.setInteger("y", pos.getY());
               currentNBT.setInteger("z", pos.getZ());
               if (!currentNBT.equals(originalNBT)) {
                  tileEntity.readFromNBT(currentNBT);
                  tileEntity.markDirty();
               }
            } else {
               CraftTweakerAPI.logInfo("No tile entity found when placing block.");
            }
         }
      }

      return placed;
   }

   public IWorldProvider getProvider() {
      return new MCWorldProvider(this.world.provider);
   }

   public IItemStack getPickedBlock(IBlockPos pos, IRayTraceResult rayTraceResult, IPlayer player) {
      BlockPos blockPos = CraftTweakerMC.getBlockPos(pos);
      net.minecraft.block.state.IBlockState state = this.world.getBlockState(blockPos);
      ItemStack stack = state.getBlock().getPickBlock(state, CraftTweakerMC.getRayTraceResult(rayTraceResult), this.world, blockPos, CraftTweakerMC.getPlayer(player));
      return CraftTweakerMC.getIItemStack(stack);
   }

   public IExplosion createExplosion(IEntity exploder, double x, double y, double z, float strength, boolean causesFire, boolean damagesTerrain) {
      return CraftTweakerMC.getIExplosion(new Explosion(this.world, CraftTweakerMC.getEntity(exploder), x, y, z, strength, causesFire, damagesTerrain));
   }

   public IExplosion performExplosion(IEntity exploder, double x, double y, double z, float strength, boolean causesFire, boolean damagesTerrain) {
      return CraftTweakerMC.getIExplosion(this.world.newExplosion(CraftTweakerMC.getEntity(exploder), x, y, z, strength, causesFire, damagesTerrain));
   }

   public IExplosion performExplosion(IExplosion explosion) {
      if (ForgeEventFactory.onExplosionStart(this.world, CraftTweakerMC.getExplosion(explosion))) {
         return explosion;
      } else {
         explosion.doExplosionA();
         explosion.doExplosionB(true);
         return explosion;
      }
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         if (!super.equals(o)) {
            return false;
         } else {
            MCWorld mcWorld = (MCWorld)o;
            return Objects.equals(this.world, mcWorld.world);
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{super.hashCode(), this.world});
   }
}
