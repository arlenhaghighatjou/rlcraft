package com.ferreusveritas.dynamictrees.entities;

import com.ferreusveritas.dynamictrees.ModBlocks;
import com.ferreusveritas.dynamictrees.ModConfigs;
import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.blocks.BlockRooty;
import com.ferreusveritas.dynamictrees.entities.animation.AnimationHandlerData;
import com.ferreusveritas.dynamictrees.entities.animation.AnimationHandlers;
import com.ferreusveritas.dynamictrees.entities.animation.IAnimationHandler;
import com.ferreusveritas.dynamictrees.models.IModelTracker;
import com.ferreusveritas.dynamictrees.models.ModelTrackerCacheEntityFallingTree;
import com.ferreusveritas.dynamictrees.util.BlockBounds;
import com.ferreusveritas.dynamictrees.util.BranchDestructionData;
import com.ferreusveritas.dynamictrees.util.CoordUtils;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityFallingTree extends Entity implements IModelTracker {
   public static final DataParameter<NBTTagCompound> voxelDataParameter = EntityDataManager.createKey(EntityFallingTree.class, DataSerializers.COMPOUND_TAG);
   protected List<ItemStack> payload = new ArrayList(0);
   protected BranchDestructionData destroyData = new BranchDestructionData();
   protected Vec3d geomCenter = Vec3d.ZERO;
   protected Vec3d massCenter = Vec3d.ZERO;
   protected AxisAlignedBB normAABB = new AxisAlignedBB(BlockPos.ORIGIN);
   protected AxisAlignedBB renderNormAABB = new AxisAlignedBB(BlockPos.ORIGIN);
   protected boolean clientBuilt = false;
   protected boolean firstUpdate = true;
   public boolean landed = false;
   public EntityFallingTree.DestroyType destroyType = EntityFallingTree.DestroyType.HARVEST;
   public boolean onFire = false;
   protected AxisAlignedBB renderBB = new AxisAlignedBB(BlockPos.ORIGIN);
   public static IAnimationHandler AnimHandlerFall = AnimationHandlers.falloverAnimationHandler;
   public static IAnimationHandler AnimHandlerDrop = AnimationHandlers.defaultAnimationHandler;
   public static IAnimationHandler AnimHandlerBurn = AnimationHandlers.defaultAnimationHandler;
   public static IAnimationHandler AnimHandlerFling = AnimationHandlers.defaultAnimationHandler;
   public static IAnimationHandler AnimHandlerBlast = AnimationHandlers.blastAnimationHandler;
   public IAnimationHandler currentAnimationHandler = AnimationHandlers.voidAnimationHandler;
   public AnimationHandlerData animationHandlerData = null;

   public EntityFallingTree(World worldIn) {
      super(worldIn);
      this.setSize(1.0F, 1.0F);
   }

   public boolean isClientBuilt() {
      return this.clientBuilt;
   }

   public EntityFallingTree setData(BranchDestructionData destroyData, List<ItemStack> payload, EntityFallingTree.DestroyType destroyType) {
      this.destroyData = destroyData;
      if (destroyData.getNumBranches() == 0) {
         System.err.println("Warning: Tried to create a EntityFallingTree with no branch blocks. This shouldn't be possible.");
         (new Exception()).printStackTrace();
         this.setDead();
         return this;
      } else {
         BlockPos cutPos = destroyData.cutPos;
         this.payload = payload;
         this.destroyType = destroyType;
         this.onFire = destroyType == EntityFallingTree.DestroyType.FIRE;
         this.posX = (double)cutPos.getX() + 0.5D;
         this.posY = (double)cutPos.getY();
         this.posZ = (double)cutPos.getZ() + 0.5D;
         int numBlocks = destroyData.getNumBranches();
         this.geomCenter = new Vec3d(0.0D, 0.0D, 0.0D);
         double totalMass = 0.0D;

         for(int index = 0; index < destroyData.getNumBranches(); ++index) {
            BlockPos relPos = destroyData.getBranchRelPos(index);
            int radius = destroyData.getBranchRadius(index);
            float mass = (float)(radius * radius * 64) / 4096.0F;
            totalMass += (double)mass;
            Vec3d relVec = new Vec3d((double)relPos.getX(), (double)relPos.getY(), (double)relPos.getZ());
            this.geomCenter = this.geomCenter.add(relVec);
            this.massCenter = this.massCenter.add(relVec.scale((double)mass));
         }

         this.geomCenter = this.geomCenter.scale(1.0D / (double)numBlocks);
         this.massCenter = this.massCenter.scale(1.0D / totalMass);
         this.setVoxelData(this.buildVoxelData(destroyData));
         return this;
      }
   }

   public NBTTagCompound buildVoxelData(BranchDestructionData destroyData) {
      NBTTagCompound tag = new NBTTagCompound();
      destroyData.writeToNBT(tag);
      tag.setDouble("geomx", this.geomCenter.x);
      tag.setDouble("geomy", this.geomCenter.y);
      tag.setDouble("geomz", this.geomCenter.z);
      tag.setDouble("massx", this.massCenter.x);
      tag.setDouble("massy", this.massCenter.y);
      tag.setDouble("massz", this.massCenter.z);
      tag.setInteger("destroytype", this.destroyType.ordinal());
      tag.setBoolean("onfire", this.onFire);
      return tag;
   }

   public void setupFromNBT(NBTTagCompound tag) {
      this.destroyData = new BranchDestructionData(tag);
      if (this.destroyData.getNumBranches() == 0) {
         this.setDead();
      }

      this.destroyType = EntityFallingTree.DestroyType.values()[tag.getInteger("destroytype")];
      this.geomCenter = new Vec3d(tag.getDouble("geomx"), tag.getDouble("geomy"), tag.getDouble("geomz"));
      this.massCenter = new Vec3d(tag.getDouble("massx"), tag.getDouble("massy"), tag.getDouble("massz"));
      this.setEntityBoundingBox(this.buildAABBFromDestroyData(this.destroyData).offset(this.posX, this.posY, this.posZ));
      this.renderBB = this.renderNormAABB.offset(this.posX, this.posY, this.posZ);
      this.onFire = tag.getBoolean("onfire");
   }

   public void buildClient() {
      NBTTagCompound tag = this.getVoxelData();
      if (tag.hasKey("species")) {
         this.setupFromNBT(tag);
         this.clientBuilt = true;
      } else {
         System.out.println("Error: No species tag has been set");
      }

      BlockBounds renderBounds = new BlockBounds(this.destroyData.cutPos);
      if (this.destroyType == EntityFallingTree.DestroyType.VOID) {
         for(BlockPos absPos : Iterables.concat(this.destroyData.getPositions(BranchDestructionData.PosType.BRANCHES), this.destroyData.getPositions(BranchDestructionData.PosType.LEAVES))) {
            IBlockState state = this.world.getBlockState(absPos);
            if (TreeHelper.isTreePart(state)) {
               this.world.setBlockState(absPos, ModBlocks.blockStates.air, 0);
               renderBounds.union(absPos);
            }
         }
      } else {
         for(BlockPos absPos : Iterables.concat(this.destroyData.getPositions(BranchDestructionData.PosType.BRANCHES), this.destroyData.getPositions(BranchDestructionData.PosType.LEAVES))) {
            this.world.setBlockState(absPos, ModBlocks.blockStates.air, 0);
            renderBounds.union(absPos);
         }
      }

      this.cleanupShellBlocks(this.destroyData);
      this.world.markBlockRangeForRenderUpdate(renderBounds.getMin(), renderBounds.getMax());
   }

   protected void cleanupShellBlocks(BranchDestructionData destroyData) {
      BlockPos cutPos = destroyData.cutPos;

      for(int i = 0; i < destroyData.getNumBranches(); ++i) {
         if (destroyData.getBranchRadius(i) > 8) {
            BlockPos pos = destroyData.getBranchRelPos(i).add(cutPos);

            for(CoordUtils.Surround dir : CoordUtils.Surround.values()) {
               BlockPos dPos = pos.add(dir.getOffset());
               if (this.world.getBlockState(dPos).getBlock() == ModBlocks.blockTrunkShell) {
                  this.world.setBlockToAir(dPos);
               }
            }
         }
      }

   }

   public AxisAlignedBB getRenderBoundingBox() {
      return this.renderBB;
   }

   public AxisAlignedBB buildAABBFromDestroyData(BranchDestructionData destroyData) {
      this.normAABB = new AxisAlignedBB(BlockPos.ORIGIN);

      for(BlockPos relPos : destroyData.getPositions(BranchDestructionData.PosType.BRANCHES, false)) {
         this.normAABB = this.normAABB.union(new AxisAlignedBB(relPos));
      }

      double height = this.normAABB.maxY - this.normAABB.minY;
      double width = MathHelper.absMax(this.normAABB.maxX - this.normAABB.minX, this.normAABB.maxZ - this.normAABB.minZ);
      double grow = Math.max(0.0D, height - width / 2.0D) + 2.0D;
      this.renderNormAABB = this.normAABB.grow(grow + 4.0D, 4.0D, grow + 4.0D);
      return this.normAABB;
   }

   public BranchDestructionData getDestroyData() {
      return this.destroyData;
   }

   public List<ItemStack> getPayload() {
      return this.payload;
   }

   public Vec3d getGeomCenter() {
      return this.geomCenter;
   }

   public Vec3d getMassCenter() {
      return this.massCenter;
   }

   public void setPosition(double x, double y, double z) {
      this.posX = x;
      this.posY = y;
      this.posZ = z;
      this.setEntityBoundingBox(this.normAABB != null ? this.normAABB.offset(this.posX, this.posY, this.posZ) : new AxisAlignedBB(BlockPos.ORIGIN));
      this.renderBB = this.renderNormAABB != null ? this.renderNormAABB.offset(this.posX, this.posY, this.posZ) : new AxisAlignedBB(BlockPos.ORIGIN);
   }

   public void onEntityUpdate() {
      super.onEntityUpdate();
      if (this.world.isRemote && !this.clientBuilt) {
         this.buildClient();
         if (this.isDead) {
            return;
         }
      }

      if (!this.world.isRemote && this.firstUpdate) {
         this.updateNeighbors();
      }

      this.handleMotion();
      this.setEntityBoundingBox(this.normAABB.offset(this.posX, this.posY, this.posZ));
      this.renderBB = this.renderNormAABB.offset(this.posX, this.posY, this.posZ);
      if (this.shouldDie()) {
         this.dropPayLoad();
         this.setDead();
         this.modelCleanup();
      }

      this.firstUpdate = false;
   }

   protected void updateNeighbors() {
      HashSet<BlockPos> destroyed = new HashSet();
      HashSet<BlockPos> toUpdate = new HashSet();
      Iterables.concat(this.destroyData.getPositions(BranchDestructionData.PosType.BRANCHES), this.destroyData.getPositions(BranchDestructionData.PosType.LEAVES)).forEach((pos) -> {
         destroyed.add(pos);
      });

      for(BlockPos d : destroyed) {
         for(EnumFacing dir : EnumFacing.values()) {
            BlockPos dPos = d.offset(dir);
            if (!destroyed.contains(dPos)) {
               toUpdate.add(dPos);
            }
         }
      }

      toUpdate.forEach((pos) -> {
         this.world.neighborChanged(pos, Blocks.AIR, pos);
      });
   }

   protected IAnimationHandler selectAnimationHandler() {
      return ModConfigs.enableFallingTrees ? this.destroyData.species.selectAnimationHandler(this) : AnimationHandlers.voidAnimationHandler;
   }

   public IAnimationHandler defaultAnimationHandler() {
      if (this.destroyType != EntityFallingTree.DestroyType.VOID && this.destroyType != EntityFallingTree.DestroyType.ROOT) {
         if (this.destroyType == EntityFallingTree.DestroyType.BLAST) {
            return AnimHandlerBlast;
         } else if (this.destroyType == EntityFallingTree.DestroyType.FIRE) {
            return AnimHandlerBurn;
         } else if (this.getDestroyData().cutDir == EnumFacing.DOWN) {
            return this.getMassCenter().y >= 1.0D ? AnimHandlerFall : AnimHandlerFling;
         } else {
            return AnimHandlerDrop;
         }
      } else {
         return AnimationHandlers.voidAnimationHandler;
      }
   }

   @SideOnly(Side.CLIENT)
   public void modelCleanup() {
      ModelTrackerCacheEntityFallingTree.cleanupModels(this.world, this);
   }

   public void handleMotion() {
      if (this.firstUpdate) {
         this.currentAnimationHandler = this.selectAnimationHandler();
         this.currentAnimationHandler.initMotion(this);
      } else {
         this.currentAnimationHandler.handleMotion(this);
      }

   }

   public void dropPayLoad() {
      if (!this.world.isRemote) {
         this.currentAnimationHandler.dropPayload(this);
      }

   }

   public boolean shouldDie() {
      return this.ticksExisted > 20 && this.currentAnimationHandler.shouldDie(this);
   }

   @SideOnly(Side.CLIENT)
   public boolean shouldRender() {
      return this.currentAnimationHandler.shouldRender(this);
   }

   public static void standardDropLogsPayload(EntityFallingTree entity) {
      World world = entity.world;
      if (!world.isRemote) {
         BlockPos cutPos = entity.getDestroyData().cutPos;
         entity.getPayload().forEach((i) -> {
            spawnItemAsEntity(world, cutPos, i);
         });
      }

   }

   public static void standardDropLeavesPayLoad(EntityFallingTree entity) {
      World world = entity.world;
      if (!world.isRemote) {
         BlockPos cutPos = entity.getDestroyData().cutPos;
         entity.getDestroyData().leavesDrops.forEach((bis) -> {
            Block.spawnAsEntity(world, cutPos.add(bis.pos), bis.stack);
         });
      }

   }

   public static void spawnItemAsEntity(World worldIn, BlockPos pos, ItemStack stack) {
      if (!worldIn.isRemote && !stack.isEmpty() && worldIn.getGameRules().getBoolean("doTileDrops") && !worldIn.restoringBlockSnapshots) {
         EntityItem entityitem = new EntityItem(worldIn, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, stack);
         entityitem.motionX = 0.0D;
         entityitem.motionY = 0.0D;
         entityitem.motionZ = 0.0D;
         entityitem.setDefaultPickupDelay();
         worldIn.spawnEntity(entityitem);
      }

   }

   protected void entityInit() {
      this.getDataManager().register(voxelDataParameter, new NBTTagCompound());
   }

   public void cleanupRootyDirt() {
      if (!this.world.isRemote) {
         BlockPos rootPos = this.getDestroyData().cutPos.down();
         IBlockState belowState = this.world.getBlockState(rootPos);
         if (TreeHelper.isRooty(belowState)) {
            BlockRooty rootyBlock = (BlockRooty)belowState.getBlock();
            rootyBlock.doDecay(this.world, rootPos, belowState, this.getDestroyData().species);
         }
      }

   }

   public void setVoxelData(NBTTagCompound tag) {
      this.setEntityBoundingBox(this.buildAABBFromDestroyData(this.destroyData).offset(this.posX, this.posY, this.posZ));
      this.renderBB = this.renderNormAABB.offset(this.posX, this.posY, this.posZ);
      this.getDataManager().set(voxelDataParameter, tag);
   }

   public NBTTagCompound getVoxelData() {
      return (NBTTagCompound)this.getDataManager().get(voxelDataParameter);
   }

   protected void readEntityFromNBT(NBTTagCompound compound) {
      NBTTagCompound vox = (NBTTagCompound)compound.getTag("vox");
      this.setupFromNBT(vox);
      this.setVoxelData(vox);
      if (compound.hasKey("payload")) {
         for(NBTBase tag : (NBTTagList)compound.getTag("payload")) {
            if (tag instanceof NBTTagCompound) {
               NBTTagCompound compTag = (NBTTagCompound)tag;
               this.payload.add(new ItemStack(compTag));
            }
         }
      }

   }

   protected void writeEntityToNBT(NBTTagCompound compound) {
      compound.setTag("vox", this.getVoxelData());
      if (!this.payload.isEmpty()) {
         NBTTagList list = new NBTTagList();

         for(ItemStack stack : this.payload) {
            list.appendTag(stack.serializeNBT());
         }

         compound.setTag("payload", list);
      }

   }

   public static EntityFallingTree dropTree(World world, BranchDestructionData destroyData, List<ItemStack> woodDropList, EntityFallingTree.DestroyType destroyType) {
      if (!world.isRemote) {
         EntityFallingTree entity = (new EntityFallingTree(world)).setData(destroyData, woodDropList, destroyType);
         if (entity.isEntityAlive()) {
            world.spawnEntity(entity);
         }

         return entity;
      } else {
         return null;
      }
   }

   public static enum DestroyType {
      VOID,
      HARVEST,
      BLAST,
      FIRE,
      ROOT;

      private DestroyType() {
      }
   }
}
