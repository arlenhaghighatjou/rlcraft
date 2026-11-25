package net.blay09.mods.waystones.network.message;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import javax.annotation.Nullable;
import net.blay09.mods.waystones.WarpMode;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageTeleportToWaystone implements IMessage {
   private WaystoneEntry waystone;
   private WarpMode warpMode;
   private EnumHand hand;
   private WaystoneEntry fromWaystone;

   public MessageTeleportToWaystone() {
   }

   public MessageTeleportToWaystone(WaystoneEntry waystone, WarpMode warpMode, EnumHand hand, @Nullable WaystoneEntry fromWaystone) {
      this.waystone = waystone;
      this.warpMode = warpMode;
      this.hand = hand;
      this.fromWaystone = fromWaystone;
   }

   public void fromBytes(ByteBuf buf) {
      this.waystone = WaystoneEntry.read(buf);
      this.warpMode = WarpMode.values()[buf.readByte()];
      this.hand = this.warpMode != WarpMode.WARP_SCROLL && this.warpMode != WarpMode.WARP_STONE ? EnumHand.MAIN_HAND : EnumHand.values()[buf.readByte()];
      if (this.warpMode == WarpMode.WAYSTONE) {
         this.fromWaystone = WaystoneEntry.read(buf);
      }

   }

   public void toBytes(ByteBuf buf) {
      this.waystone.write(buf);
      buf.writeByte(this.warpMode.ordinal());
      if (this.warpMode != WarpMode.WARP_SCROLL && this.warpMode != WarpMode.WARP_STONE) {
         if (this.warpMode == WarpMode.WAYSTONE) {
            this.fromWaystone.write(buf);
         }
      } else {
         buf.writeByte(this.hand.ordinal());
      }

   }

   public WaystoneEntry getWaystone() {
      return this.waystone;
   }

   @Nullable
   public WaystoneEntry getFromWaystone() {
      return this.fromWaystone;
   }

   public WarpMode getWarpMode() {
      return this.warpMode;
   }

   public EnumHand getHand() {
      return this.hand;
   }
}
