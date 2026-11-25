package net.blay09.mods.waystones.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.blay09.mods.waystones.GlobalWaystones;
import net.blay09.mods.waystones.WarpMode;
import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.client.ClientWaystones;
import net.blay09.mods.waystones.util.WaystoneActivatedEvent;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatBasic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class BlockWaystone extends BlockContainer {
   public static final String name = "waystone";
   public static final ResourceLocation registryName = new ResourceLocation("waystones", "waystone");
   public static final PropertyDirection FACING = BlockHorizontal.FACING;
   public static final PropertyBool BASE = PropertyBool.create("base");
   private static final StatBase WAYSTONE_ACTIVATED = (new StatBasic("stat.waystones:waystonesActivated", new TextComponentTranslation("stat.waystones:waystonesActivated", new Object[0]))).registerStat();

   public BlockWaystone() {
      super(Material.ROCK);
      this.setRegistryName("waystone");
      this.setUnlocalizedName(registryName.toString());
      this.setHardness(5.0F);
      this.setResistance(2000.0F);
      this.setCreativeTab(Waystones.creativeTab);
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{FACING, BASE});
   }

   public int getMetaFromState(IBlockState state) {
      int meta = ((EnumFacing)state.getValue(FACING)).getIndex();
      if (((Boolean)state.getValue(BASE)).booleanValue()) {
         meta |= 8;
      }

      return meta;
   }

   public IBlockState getStateFromMeta(int meta) {
      EnumFacing facing = EnumFacing.getFront(meta & 7);
      if (facing.getAxis() == Axis.Y) {
         facing = EnumFacing.NORTH;
      }

      boolean isBase = (meta & 8) > 0;
      return this.getDefaultState().withProperty(FACING, facing).withProperty(BASE, Boolean.valueOf(isBase));
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   @Nullable
   public TileEntity createNewTileEntity(World world, int metadata) {
      return new TileWaystone(!((Boolean)this.getStateFromMeta(metadata).getValue(BASE)).booleanValue());
   }

   public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World world, BlockPos pos) {
      if (WaystoneConfig.general.creativeModeOnly && !player.capabilities.isCreativeMode) {
         return -1.0F;
      } else {
         TileWaystone tileWaystone = this.getTileWaystone(world, pos);
         if (tileWaystone != null && !player.capabilities.isCreativeMode) {
            if (tileWaystone.wasGenerated() && WaystoneConfig.general.disallowBreakingGenerated) {
               return -1.0F;
            }

            if (tileWaystone.isGlobal() && !WaystoneConfig.general.allowEveryoneGlobal) {
               return -1.0F;
            }
         }

         return super.getPlayerRelativeBlockHardness(state, player, world, pos);
      }
   }

   public boolean canPlaceBlockAt(World world, BlockPos pos) {
      Block blockBelow = world.getBlockState(pos.down()).getBlock();
      if (blockBelow == this) {
         return false;
      } else {
         Block blockAbove = world.getBlockState(pos.up(2)).getBlock();
         return blockAbove != this && super.canPlaceBlockAt(world, pos) && world.getBlockState(pos.up()).getBlock().isReplaceable(world, pos.up());
      }
   }

   public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
      EnumFacing facing = EnumFacing.getDirectionFromEntityLiving(pos, placer);
      if (facing.getAxis() == Axis.Y) {
         facing = EnumFacing.NORTH;
      }

      return this.getDefaultState().withProperty(FACING, facing).withProperty(BASE, Boolean.valueOf(true));
   }

   public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      world.setBlockState(pos.up(), this.getDefaultState().withProperty(BASE, Boolean.valueOf(false)));
      if (placer instanceof EntityPlayer && (!WaystoneConfig.general.creativeModeOnly || ((EntityPlayer)placer).capabilities.isCreativeMode)) {
         TileWaystone tileWaystone = (TileWaystone)world.getTileEntity(pos);
         if (tileWaystone != null) {
            tileWaystone.setOwner((EntityPlayer)placer);
            tileWaystone.setWasGenerated(false);
            Waystones.proxy.openWaystoneSettings((EntityPlayer)placer, new WaystoneEntry(tileWaystone.getParent()), false);
         }
      }

   }

   public void breakBlock(World world, BlockPos pos, IBlockState state) {
      TileWaystone tileWaystone = this.getTileWaystone(world, pos);
      if (tileWaystone != null) {
         TileWaystone tileWaystoneExact = (TileWaystone)world.getTileEntity(pos);
         if (tileWaystoneExact != null && tileWaystoneExact.isDummy()) {
            tileWaystoneExact.setWasGenerated(tileWaystone.wasGenerated());
         }

         WaystoneEntry entry = new WaystoneEntry(tileWaystone);
         if (tileWaystone.isGlobal()) {
            GlobalWaystones.get(world).removeGlobalWaystone(entry);
         }

         for(EntityPlayer player : world.getEntitiesWithinAABB(EntityPlayer.class, (new AxisAlignedBB(pos)).grow(64.0D, 64.0D, 64.0D))) {
            WaystoneManager.removePlayerWaystone(player, entry);
            WaystoneManager.sendPlayerWaystones(player);
         }
      }

      super.breakBlock(world, pos, state);
      if (world.getBlockState(pos.up()).getBlock() == this) {
         world.setBlockToAir(pos.up());
      } else if (world.getBlockState(pos.down()).getBlock() == this) {
         world.setBlockToAir(pos.down());
      }

   }

   public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
      TileWaystone tileWaystone = (TileWaystone)te;
      if (tileWaystone == null || !tileWaystone.wasGenerated() || WaystoneConfig.general.dropGenerated) {
         super.harvestBlock(world, player, pos, state, te, stack);
      }
   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      if (!player.isSneaking() || !player.capabilities.isCreativeMode && WaystoneConfig.general.creativeModeOnly) {
         TileWaystone tileWaystone = this.getTileWaystone(world, pos);
         if (tileWaystone == null) {
            return true;
         } else {
            WaystoneEntry knownWaystone = world.isRemote ? ClientWaystones.getKnownWaystone(tileWaystone.getWaystoneName()) : null;
            if (knownWaystone != null) {
               Waystones.proxy.openWaystoneSelection(player, WarpMode.WAYSTONE, EnumHand.MAIN_HAND, knownWaystone);
            } else {
               this.activateWaystone(player, world, tileWaystone);
            }

            return true;
         }
      } else {
         TileWaystone tileWaystone = this.getTileWaystone(world, pos);
         if (tileWaystone == null) {
            return true;
         } else {
            if (!world.isRemote) {
               if (WaystoneConfig.general.restrictRenameToOwner && !tileWaystone.isOwner(player)) {
                  player.sendStatusMessage(new TextComponentTranslation("waystones:notTheOwner", new Object[0]), true);
                  return true;
               }

               if (tileWaystone.isGlobal() && !player.capabilities.isCreativeMode && !WaystoneConfig.general.allowEveryoneGlobal) {
                  player.sendStatusMessage(new TextComponentTranslation("waystones:creativeRequired", new Object[0]), true);
                  return true;
               }
            }

            Waystones.proxy.openWaystoneSettings(player, new WaystoneEntry(tileWaystone.getParent()), false);
            return true;
         }
      }
   }

   public void activateWaystone(EntityPlayer player, World world, TileWaystone tileWaystone) {
      BlockPos pos = tileWaystone.getPos();
      if (!world.isRemote) {
         WaystoneEntry waystone = new WaystoneEntry(tileWaystone);
         if (!WaystoneManager.checkAndUpdateWaystone(player, waystone)) {
            TextComponentString nameComponent = new TextComponentString(tileWaystone.getWaystoneName());
            nameComponent.getStyle().setColor(TextFormatting.WHITE);
            TextComponentTranslation chatComponent = new TextComponentTranslation("waystones:activatedWaystone", new Object[]{nameComponent});
            chatComponent.getStyle().setColor(TextFormatting.YELLOW);
            player.sendMessage(chatComponent);
            player.addStat(WAYSTONE_ACTIVATED);
            WaystoneManager.addPlayerWaystone(player, waystone);
            WaystoneManager.sendPlayerWaystones(player);
         }

         if (WaystoneConfig.general.setSpawnPoint) {
            IBlockState state = world.getBlockState(pos);
            EnumFacing blockFacing = (EnumFacing)state.getValue(FACING);
            player.setSpawnPoint(new BlockPos(tileWaystone.getPos().offset(blockFacing)), true);
         }

         world.updateObservingBlocksAt(pos, this);
      } else {
         MinecraftForge.EVENT_BUS.post(new WaystoneActivatedEvent(tileWaystone.getWaystoneName(), tileWaystone.getPos(), tileWaystone.getWorld().provider.getDimension()));
         Waystones.proxy.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, pos, 1.0F);

         for(int i = 0; i < 32; ++i) {
            world.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, (double)pos.getX() + 0.5D + (world.rand.nextDouble() - 0.5D) * 2.0D, (double)(pos.getY() + 3), (double)pos.getZ() + 0.5D + (world.rand.nextDouble() - 0.5D) * 2.0D, 0.0D, -5.0D, 0.0D, new int[0]);
            world.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, (double)pos.getX() + 0.5D + (world.rand.nextDouble() - 0.5D) * 2.0D, (double)(pos.getY() + 4), (double)pos.getZ() + 0.5D + (world.rand.nextDouble() - 0.5D) * 2.0D, 0.0D, -5.0D, 0.0D, new int[0]);
         }
      }

   }

   public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
      if (!WaystoneConfig.client.disableParticles && rand.nextFloat() < 0.75F) {
         TileWaystone tileWaystone = this.getTileWaystone(world, pos);
         if (tileWaystone == null) {
            return;
         }

         if (ClientWaystones.getKnownWaystone(tileWaystone.getWaystoneName()) != null) {
            world.spawnParticle(EnumParticleTypes.PORTAL, (double)pos.getX() + 0.5D + (rand.nextDouble() - 0.5D) * 1.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D + (rand.nextDouble() - 0.5D) * 1.5D, 0.0D, 0.0D, 0.0D, new int[0]);
            world.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, (double)pos.getX() + 0.5D + (rand.nextDouble() - 0.5D) * 1.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D + (rand.nextDouble() - 0.5D) * 1.5D, 0.0D, 0.0D, 0.0D, new int[0]);
         }
      }

   }

   @Nullable
   public TileWaystone getTileWaystone(World world, BlockPos pos) {
      TileWaystone tileWaystone = (TileWaystone)world.getTileEntity(pos);
      return tileWaystone != null ? tileWaystone.getParent() : null;
   }

   public boolean hasCustomBreakingProgress(IBlockState state) {
      return true;
   }
}
