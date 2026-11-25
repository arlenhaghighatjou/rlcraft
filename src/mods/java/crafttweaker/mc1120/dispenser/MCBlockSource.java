package crafttweaker.mc1120.dispenser;

import crafttweaker.api.block.IBlockState;
import crafttweaker.api.dispenser.IBlockSource;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IFacing;
import crafttweaker.api.world.IWorld;
import net.minecraft.block.BlockDispenser;
import net.minecraft.util.EnumFacing;

public class MCBlockSource implements IBlockSource {
   private final net.minecraft.dispenser.IBlockSource internal;

   public MCBlockSource(net.minecraft.dispenser.IBlockSource internal) {
      this.internal = internal;
   }

   public double getX() {
      return this.internal.getX();
   }

   public double getY() {
      return this.internal.getY();
   }

   public double getZ() {
      return this.internal.getZ();
   }

   public IWorld getWorld() {
      return CraftTweakerMC.getIWorld(this.internal.getWorld());
   }

   public IBlockState getBlockState() {
      return CraftTweakerMC.getBlockState(this.internal.getBlockState());
   }

   public IBlockPos getPos() {
      return CraftTweakerMC.getIBlockPos(this.internal.getBlockPos());
   }

   public IFacing getFacing() {
      return CraftTweakerMC.getIFacing((EnumFacing)this.internal.getBlockState().getValue(BlockDispenser.FACING));
   }

   public Object getInternal() {
      return this.internal;
   }
}
