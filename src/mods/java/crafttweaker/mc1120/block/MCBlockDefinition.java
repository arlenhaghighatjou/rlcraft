package crafttweaker.mc1120.block;

import crafttweaker.api.block.IBlockDefinition;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.creativetabs.ICreativeTab;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IBlockAccess;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IExplosion;
import crafttweaker.api.world.IFacing;
import crafttweaker.api.world.IWorld;
import crafttweaker.mc1120.creativetabs.MCCreativeTab;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MCBlockDefinition implements IBlockDefinition {
   private final Block block;

   public MCBlockDefinition(Block block) {
      this.block = block;
   }

   public Block getInternalBlock() {
      return this.block;
   }

   public String getId() {
      return this.block.getRegistryName().toString();
   }

   public String getDisplayName() {
      return this.block.getLocalizedName();
   }

   public Object getInternal() {
      return this.block;
   }

   public void setLightOpacity(int lightOpacity) {
      this.block.setLightOpacity(lightOpacity);
   }

   public float getLightOpacity() {
      return (float)this.block.getLightOpacity(this.block.getDefaultState());
   }

   public float getLightOpacity(IBlockState state) {
      return (float)this.block.getLightOpacity(CraftTweakerMC.getBlockState(state));
   }

   public float getLightOpacity(IBlockState state, IWorld world, IBlockPos pos) {
      return (float)this.block.getLightOpacity(CraftTweakerMC.getBlockState(state), CraftTweakerMC.getWorld(world), CraftTweakerMC.getBlockPos(pos));
   }

   public void setLightLevel(float lightLevel) {
      this.block.setLightLevel(lightLevel);
   }

   public float getLightLevel() {
      return (float)this.block.getLightValue(this.block.getDefaultState());
   }

   public float getLightLevel(IBlockState state) {
      return (float)this.block.getLightValue(CraftTweakerMC.getBlockState(state));
   }

   public float getLightLevel(IBlockState state, IWorld world, IBlockPos pos) {
      return (float)this.block.getLightValue(CraftTweakerMC.getBlockState(state), CraftTweakerMC.getWorld(world), CraftTweakerMC.getBlockPos(pos));
   }

   public void setResistance(float resistance) {
      this.block.setResistance(resistance * 5.0F / 3.0F);
   }

   public float getResistance() {
      return this.block.getExplosionResistance((Entity)null);
   }

   public float getResistance(IWorld world, IBlockPos pos, @Nullable IEntity entity, @Nullable IExplosion explosion) {
      return this.block.getExplosionResistance(CraftTweakerMC.getWorld(world), CraftTweakerMC.getBlockPos(pos), CraftTweakerMC.getEntity(entity), CraftTweakerMC.getExplosion(explosion));
   }

   public void setHardness(float hardness) {
      this.block.setHardness(hardness);
   }

   public float getHardness() {
      return this.block.blockHardness;
   }

   public void setUnbreakable() {
      this.block.setBlockUnbreakable();
   }

   public boolean getTickRandomly() {
      return this.block.getTickRandomly();
   }

   public void setTickRandomly(boolean tickRandomly) {
      this.block.setTickRandomly(tickRandomly);
   }

   public void setHarvestLevel(String toolClass, int level) {
      this.setHarvestLevel(toolClass, level, (IBlockState)null);
   }

   public void setHarvestLevel(String toolClass, int level, @Nullable IBlockState state) {
      if (state == null) {
         this.block.setHarvestLevel(toolClass, level);
      } else {
         this.block.setHarvestLevel(toolClass, level, CraftTweakerMC.getBlockState(state));
      }

   }

   public int getHarvestLevel() {
      return this.block.getHarvestLevel(this.block.getDefaultState());
   }

   public int getHarvestLevel(IBlockState state) {
      return this.block.getHarvestLevel(CraftTweakerMC.getBlockState(state));
   }

   public String getHarvestTool() {
      return (String)Optional.ofNullable(this.block.getHarvestTool(this.block.getDefaultState())).orElse("");
   }

   public String getHarvestTool(IBlockState state) {
      return (String)Optional.ofNullable(this.block.getHarvestTool(CraftTweakerMC.getBlockState(state))).orElse("");
   }

   public int tickRate(IWorld world) {
      return this.block.tickRate((World)world.getInternal());
   }

   public boolean canPlaceBlockOnSide(IWorld world, IBlockPos pos, IFacing facing) {
      return this.block.canPlaceBlockOnSide((World)world.getInternal(), (BlockPos)pos.getInternal(), (EnumFacing)facing.getInternal());
   }

   public boolean canPlaceBlockAt(IWorld world, IBlockPos pos) {
      return this.block.canPlaceBlockAt((World)world.getInternal(), (BlockPos)pos.getInternal());
   }

   public boolean canSpawnInBlock() {
      return this.block.canSpawnInBlock();
   }

   public String getUnlocalizedName() {
      return this.block.getUnlocalizedName();
   }

   public ICreativeTab getCreativeTabToDisplayOn() {
      return MCCreativeTab.getICreativeTab(this.block.getCreativeTabToDisplayOn());
   }

   public void setCreativeTab(ICreativeTab creativeTab) {
      this.block.setCreativeTab(CraftTweakerMC.getCreativeTabs(creativeTab));
   }

   public IBlockState getDefaultState() {
      return new MCBlockState(this.block.getDefaultState());
   }

   public float getSlipperiness(IBlockState state, IBlockAccess access, IBlockPos pos, IEntity entity) {
      return this.block.getSlipperiness((net.minecraft.block.state.IBlockState)state.getInternal(), (net.minecraft.world.IBlockAccess)access.getInternal(), (BlockPos)pos.getInternal(), (Entity)entity.getInternal());
   }

   public void setDefaultSlipperiness(float defaultSlipperiness) {
      this.block.setDefaultSlipperiness(defaultSlipperiness);
   }

   public IBlockState getStateFromMeta(int meta) {
      return new MCBlockState(this.block.getStateFromMeta(meta));
   }

   public boolean isToolEffective(String type, IBlockState state) {
      return this.block.isToolEffective(type, CraftTweakerMC.getBlockState(state));
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MCBlockDefinition that = (MCBlockDefinition)o;
         return Objects.equals(this.block, that.block);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.block});
   }
}
