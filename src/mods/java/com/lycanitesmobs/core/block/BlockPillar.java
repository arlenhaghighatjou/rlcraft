package com.lycanitesmobs.core.block;

import com.google.common.collect.UnmodifiableIterator;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.info.ModInfo;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockPillar extends BlockBase {
   public static final PropertyEnum<Axis> AXIS = PropertyEnum.create("axis", Axis.class);

   public BlockPillar(Material material, ModInfo group, String name) {
      super(material, group, name);
      this.setDefaultState(this.blockState.getBaseState().withProperty(AXIS, Axis.Y));
   }

   public String getLocalizedName() {
      return LanguageManager.translate(this.getUnlocalizedName() + ".name");
   }

   public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag advanced) {
      tooltip.add(this.getDescription(stack, world));
   }

   public String getDescription(ItemStack itemStack, World world) {
      return "§a" + LanguageManager.translate(this.getUnlocalizedName() + ".description");
   }

   public BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{AXIS});
   }

   protected ItemStack getSilkTouchDrop(IBlockState state) {
      return new ItemStack(Item.getItemFromBlock(this));
   }

   public IBlockState getStateFromMeta(int meta) {
      Axis enumfacing$axis = Axis.Y;
      int i = meta & 12;
      if (i == 4) {
         enumfacing$axis = Axis.X;
      } else if (i == 8) {
         enumfacing$axis = Axis.Z;
      }

      return this.getDefaultState().withProperty(AXIS, enumfacing$axis);
   }

   public int getMetaFromState(IBlockState state) {
      int i = 0;
      Axis enumfacing$axis = (Axis)state.getValue(AXIS);
      if (enumfacing$axis == Axis.X) {
         i |= 4;
      } else if (enumfacing$axis == Axis.Z) {
         i |= 8;
      }

      return i;
   }

   public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(AXIS, facing.getAxis());
   }

   public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
      IBlockState state = world.getBlockState(pos);
      UnmodifiableIterator var5 = state.getProperties().keySet().iterator();

      while(var5.hasNext()) {
         IProperty<?> prop = (IProperty)var5.next();
         if (prop.getName().equals("axis")) {
            world.setBlockState(pos, state.cycleProperty(prop));
            return true;
         }
      }

      return false;
   }

   public IBlockState withRotation(IBlockState state, Rotation rot) {
      switch(rot) {
      case COUNTERCLOCKWISE_90:
      case CLOCKWISE_90:
         switch((Axis)state.getValue(AXIS)) {
         case X:
            return state.withProperty(AXIS, Axis.Z);
         case Z:
            return state.withProperty(AXIS, Axis.X);
         default:
            return state;
         }
      default:
         return state;
      }
   }
}
