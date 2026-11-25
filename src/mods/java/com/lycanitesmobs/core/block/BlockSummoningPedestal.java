package com.lycanitesmobs.core.block;

import com.lycanitesmobs.GuiHandler;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.tileentity.TileEntityBase;
import com.lycanitesmobs.core.tileentity.TileEntitySummoningPedestal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockSummoningPedestal extends BlockBase implements ITileEntityProvider {
   public static final PropertyEnum PROPERTY_OWNER = PropertyEnum.create("owner", BlockSummoningPedestal.EnumSummoningPedestal.class);

   public BlockSummoningPedestal(ModInfo group) {
      super(Material.IRON);
      this.group = group;
      this.blockName = "summoningpedestal";
      this.setup();
      this.setHardness(5.0F);
      this.setResistance(10.0F);
      this.setHarvestLevel("pickaxe", 2);
      this.setSoundType(SoundType.METAL);
      this.hasTileEntity = true;
      this.setCreativeTab(LycanitesMobs.blocksTab);
      this.setDefaultState(this.getBlockState().getBaseState().withProperty(PROPERTY_OWNER, BlockSummoningPedestal.EnumSummoningPedestal.NONE));
   }

   public BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{PROPERTY_OWNER});
   }

   public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
      super.onBlockAdded(world, pos, state);
   }

   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof TileEntitySummoningPedestal) {
         TileEntitySummoningPedestal tileEntitySummoningPedestal = (TileEntitySummoningPedestal)tileentity;
         tileEntitySummoningPedestal.setOwner(placer);
         if (placer instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)placer;
            ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
            if (playerExt != null) {
               tileEntitySummoningPedestal.setSummonSet(playerExt.getSelectedSummonSet());
            }
         }
      }

   }

   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      if (tileEntity != null && tileEntity instanceof TileEntityBase) {
         ((TileEntityBase)tileEntity).onRemove();
      }

      super.breakBlock(worldIn, pos, state);
      worldIn.removeTileEntity(pos);
   }

   public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int eventID, int eventParam) {
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      return tileEntity != null && tileEntity.receiveClientEvent(eventID, eventParam);
   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (!world.isRemote && playerIn != null && playerIn.getEntityWorld() != null) {
         playerIn.openGui(LycanitesMobs.instance, GuiHandler.GuiType.TILEENTITY.id, playerIn.getEntityWorld(), pos.getX(), pos.getY(), pos.getZ());
      }

      return true;
   }

   public TileEntity createNewTileEntity(World world, int metadata) {
      return new TileEntitySummoningPedestal();
   }

   public static void setState(BlockSummoningPedestal.EnumSummoningPedestal owner, World worldIn, BlockPos pos) {
      IBlockState iblockstate = worldIn.getBlockState(pos);
      TileEntity tileentity = worldIn.getTileEntity(pos);
      worldIn.setBlockState(pos, iblockstate.getBlock().getBlockState().getBaseState().withProperty(PROPERTY_OWNER, owner), 3);
      if (tileentity != null) {
         tileentity.validate();
         worldIn.setTileEntity(pos, tileentity);
      }

   }

   public static enum EnumSummoningPedestal implements IStringSerializable {
      NONE("none"),
      CLIENT("client"),
      PLAYER("player");

      private String name;

      private EnumSummoningPedestal(String name) {
         this.name = name;
      }

      public String getName() {
         return this.name;
      }
   }
}
