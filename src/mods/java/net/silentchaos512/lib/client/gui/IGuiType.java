package net.silentchaos512.lib.client.gui;

import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IGuiType {
   int getID();

   Object getMod();

   @Nullable
   Class<? extends TileEntity> getTileEntityClass();

   @Nullable
   Container getContainer(@Nullable TileEntity var1, EntityPlayer var2, int var3);

   @Nullable
   GuiScreen getGuiScreen(@Nullable TileEntity var1, EntityPlayer var2, int var3);

   default void open(EntityPlayer player, World world, BlockPos pos) {
      player.openGui(this.getMod(), this.getID(), world, pos.getX(), pos.getY(), pos.getZ());
   }

   default void open(EntityPlayer player, World world, EnumHand hand) {
      this.open(player, world, intFromHand(hand));
   }

   default void open(EntityPlayer player, World world, int subtype) {
      player.openGui(this.getMod(), this.getID(), world, subtype, 0, 0);
   }

   default boolean tileEntityMatches(@Nullable TileEntity tile) {
      Class<? extends TileEntity> tileClass = this.getTileEntityClass();
      return tileClass == null || tileClass.isInstance(tile);
   }

   static EnumHand handFromInt(int k) {
      return k == 0 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
   }

   static int intFromHand(EnumHand hand) {
      return hand == EnumHand.MAIN_HAND ? 0 : 1;
   }
}
