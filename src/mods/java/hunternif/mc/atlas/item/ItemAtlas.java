package hunternif.mc.atlas.item;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.core.TileInfo;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.network.client.IntDimensionUpdatePacket;
import hunternif.mc.atlas.network.client.ShortDimensionUpdatePacket;
import hunternif.mc.atlas.network.client.TilesPacket;
import hunternif.mc.atlas.util.MathUtil;
import java.util.ArrayList;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemAtlas extends Item {
   static final String WORLD_ATLAS_DATA_ID = "aAtlas";

   public ItemAtlas() {
      this.setUnlocalizedName("antiqueAtlas");
      this.setHasSubtypes(true);
   }

   public String getItemStackDisplayName(ItemStack stack) {
      return super.getItemStackDisplayName(stack) + " #" + stack.getItemDamage();
   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer playerIn, EnumHand hand) {
      ItemStack stack = playerIn.getHeldItem(hand);
      if (world.isRemote) {
         AntiqueAtlasMod.proxy.openAtlasGUI(stack);
      }

      return new ActionResult(EnumActionResult.SUCCESS, stack);
   }

   public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isEquipped) {
      AtlasData data = AntiqueAtlasMod.atlasData.getAtlasData(stack, world);
      if (data != null && entity instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entity;
         if (!world.isRemote && !data.isSyncedOnPlayer(player) && !data.isEmpty()) {
            data.syncOnPlayer(stack.getItemDamage(), player);
         }

         MarkersData markers = AntiqueAtlasMod.markersData.getMarkersData(stack, world);
         if (!world.isRemote && !markers.isSyncedOnPlayer(player) && !markers.isEmpty()) {
            markers.syncOnPlayer(stack.getItemDamage(), player);
         }

         ArrayList<TileInfo> newTiles = data.updateMapAroundPlayer(player);
         if (!world.isRemote && newTiles.size() > 0) {
            boolean useInt = newTiles.stream().anyMatch((t) -> {
               return MathUtil.exceedsShort(t.x, t.z);
            });
            TilesPacket packet = (TilesPacket)(useInt ? new IntDimensionUpdatePacket(stack.getItemDamage(), player.dimension) : new ShortDimensionUpdatePacket(stack.getItemDamage(), player.dimension));

            for(TileInfo t : newTiles) {
               packet.addTile(t.x, t.z, t.biome);
            }

            PacketDispatcher.sendToAll(packet);
         }

      }
   }
}
