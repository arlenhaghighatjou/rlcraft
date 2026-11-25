package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.item.ItemBlockVariant;
import com.dhanantry.scapeandrunparasites.network.SRPPacketParticle;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class BlockEvolutionLure extends BlockBase implements IMetaName {
   public static final PropertyEnum<BlockEvolutionLure.EnumType> VARIANT = PropertyEnum.create("variant", BlockEvolutionLure.EnumType.class);

   public BlockEvolutionLure(Material material, String name, float hardness, boolean creative, boolean tickRandom) {
      super(material, name, hardness, creative, tickRandom);
      this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockEvolutionLure.EnumType.ONE));
   }

   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      if (!worldIn.isRemote && SRPConfigSystems.useEvolution) {
         ItemStack head = new ItemStack(playerIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).getItem());
         if (head.getItem() != Items.AIR) {
            return false;
         }

         if (this.checkBlocks(worldIn, pos)) {
            return false;
         }

         SRPWorldData data = SRPWorldData.get(worldIn);
         if (data.getEvolutionPhase() <= -1) {
            return false;
         }

         switch((BlockEvolutionLure.EnumType)state.getValue(VARIANT)) {
         case EIGHT:
            data.setTotalKills(-SRPConfigSystems.luredValueEight, true, worldIn, true);
            playerIn.sendStatusMessage(new TextComponentTranslation("message.srparasites.lure", new Object[0]), true);
            break;
         case FIVE:
            data.setTotalKills(-SRPConfigSystems.luredValueFive, true, worldIn, true);
            playerIn.sendStatusMessage(new TextComponentTranslation("message.srparasites.lure", new Object[0]), true);
            break;
         case FOUR:
            data.setTotalKills(-SRPConfigSystems.luredValueFour, true, worldIn, true);
            playerIn.sendStatusMessage(new TextComponentTranslation("message.srparasites.lure", new Object[0]), true);
            break;
         case ONE:
            data.setTotalKills(-SRPConfigSystems.luredValueOne, true, worldIn, true);
            playerIn.sendStatusMessage(new TextComponentTranslation("message.srparasites.lure", new Object[0]), true);
            break;
         case SEVEN:
            data.setTotalKills(-SRPConfigSystems.luredValueSeven, true, worldIn, true);
            playerIn.sendStatusMessage(new TextComponentTranslation("message.srparasites.lure", new Object[0]), true);
            break;
         case SIX:
            data.setTotalKills(-SRPConfigSystems.luredValueSix, true, worldIn, true);
            playerIn.sendStatusMessage(new TextComponentTranslation("message.srparasites.lure", new Object[0]), true);
            break;
         case THREE:
            data.setTotalKills(-SRPConfigSystems.luredValueThree, true, worldIn, true);
            playerIn.sendStatusMessage(new TextComponentTranslation("message.srparasites.lure", new Object[0]), true);
            break;
         case TWO:
            data.setTotalKills(-SRPConfigSystems.luredValueTwo, true, worldIn, true);
            playerIn.sendStatusMessage(new TextComponentTranslation("message.srparasites.lure", new Object[0]), true);
         }

         worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());

         for(int i = 0; i <= 3; ++i) {
            SRPMain.network.sendToAll(new SRPPacketParticle((double)pos.getX(), (double)(pos.getY() + 1), (double)pos.getZ(), 0.5F, 0.5F, (byte)2));
         }
      }

      return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
   }

   public int damageDropped(IBlockState state) {
      return this.getMetaFromState(state);
   }

   public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
      for(BlockEvolutionLure.EnumType variant : BlockEvolutionLure.EnumType.values()) {
         items.add(new ItemStack(this, 1, variant.ordinal()));
      }

   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(VARIANT, BlockEvolutionLure.EnumType.values()[meta]);
   }

   public int getMetaFromState(IBlockState state) {
      return ((BlockEvolutionLure.EnumType)state.getValue(VARIANT)).ordinal();
   }

   public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
      return new ItemStack(Item.getItemFromBlock(this), 1, this.getMetaFromState(world.getBlockState(pos)));
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{VARIANT});
   }

   public Enum[] getVariants() {
      return BlockEvolutionLure.EnumType.values();
   }

   public ItemBlock getItemBlock() {
      return new ItemBlockVariant(this);
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
   }

   private boolean checkBlocks(World worldIn, BlockPos pos) {
      if (worldIn.getBlockState(pos.north(3).east(3)).getBlock() == SRPBlocks.ParasiteStain && worldIn.getBlockState(pos.north(3).west(3)).getBlock() == SRPBlocks.ParasiteStain && worldIn.getBlockState(pos.south(3).east(3)).getBlock() == SRPBlocks.ParasiteStain && worldIn.getBlockState(pos.south(3).west(3)).getBlock() == SRPBlocks.ParasiteStain) {
         System.out.println("aaass");
         return true;
      } else {
         return false;
      }
   }

   public static enum EnumType implements IStringSerializable {
      ONE,
      TWO,
      THREE,
      FOUR,
      FIVE,
      SIX,
      SEVEN,
      EIGHT;

      private EnumType() {
      }

      public String getName() {
         return this.name().toLowerCase();
      }

      public String toString() {
         return this.getName();
      }
   }
}
