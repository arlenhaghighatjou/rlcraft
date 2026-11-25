package svenhjol.charm.world.block;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.BlockEndPortal;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import svenhjol.charm.world.feature.EndPortalRunes;
import svenhjol.charm.world.message.MessagePortalInteract;
import svenhjol.charm.world.tile.TileRunePortal;
import svenhjol.meson.handler.NetworkHandler;
import svenhjol.meson.helper.PlayerHelper;
import svenhjol.meson.iface.IMesonBlock;

public class BlockRunePortal extends BlockEndPortal implements IMesonBlock {
   public BlockRunePortal() {
      super(Material.PORTAL);
      this.setResistance(6000000.0F);
      this.setHardness(-1.0F);
      this.setBlockUnbreakable();
      this.register(this.getName());
   }

   public String getModId() {
      return "charm";
   }

   public String getName() {
      return "rune_portal";
   }

   public Class<? extends TileEntity> getTileEntityClass() {
      return TileRunePortal.class;
   }

   public TileRunePortal getTileEntity(World world, BlockPos pos) {
      TileEntity tile = world.getTileEntity(pos);
      return tile instanceof TileRunePortal ? (TileRunePortal)tile : null;
   }

   public void setPortal(World world, BlockPos pos, BlockPos portal) {
      TileRunePortal tile = this.getTileEntity(world, pos);
      if (tile != null) {
         tile.portal = portal;
      }

   }

   public BlockPos getPortal(World world, BlockPos pos) {
      TileRunePortal tile = this.getTileEntity(world, pos);
      return tile != null ? tile.portal : null;
   }

   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return new TileRunePortal();
   }

   public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
      return false;
   }

   public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
      if (!worldIn.isRemote && !entityIn.isRiding() && !entityIn.isBeingRidden() && entityIn.isNonBoss() && entityIn.getEntityBoundingBox().intersects(state.getBoundingBox(worldIn, pos).offset(pos))) {
         BlockPos thisPortal = this.getPortal(worldIn, pos);
         if (thisPortal != null) {
            BlockPos foundPortal = EndPortalRunes.findPortal(worldIn, thisPortal);
            if (entityIn instanceof EntityPlayer) {
               EntityPlayer player = (EntityPlayer)entityIn;
               BlockPos teleportTo = foundPortal == null ? thisPortal : foundPortal;
               PlayerHelper.teleportPlayer(player, teleportTo.add(-2, 1, 0), 0);
               MessagePortalInteract message;
               if (foundPortal != null) {
                  message = new MessagePortalInteract(foundPortal, 2);
               } else {
                  player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 120, 4));
                  message = new MessagePortalInteract(thisPortal, 0);
               }

               NetworkHandler.INSTANCE.sendTo(message, (EntityPlayerMP)player);
            } else {
               entityIn.setDead();
            }
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(IBlockState state, World worldIn, BlockPos pos, EaglercraftRandom rand) {
      float f = rand.nextFloat();
      EnumParticleTypes particle;
      if (f < 0.5F) {
         particle = EnumParticleTypes.ENCHANTMENT_TABLE;
      } else {
         particle = EnumParticleTypes.SMOKE_NORMAL;
      }

      double d0 = (double)((float)pos.getX() + rand.nextFloat());
      double d1 = (double)((float)pos.getY() + 1.0F);
      double d2 = (double)((float)pos.getZ() + rand.nextFloat());
      worldIn.spawnParticle(particle, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
   }

   public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
      return ItemStack.EMPTY;
   }
}
