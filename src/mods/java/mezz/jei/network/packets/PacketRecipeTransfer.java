package mezz.jei.network.packets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import mezz.jei.network.IPacketId;
import mezz.jei.network.PacketIdServer;
import mezz.jei.transfer.BasicRecipeTransferHandlerServer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

public class PacketRecipeTransfer extends PacketJei {
   public final Map<Integer, Integer> recipeMap;
   public final List<Integer> craftingSlots;
   public final List<Integer> inventorySlots;
   private final boolean maxTransfer;
   private final boolean requireCompleteSets;

   public PacketRecipeTransfer(Map<Integer, Integer> recipeMap, List<Integer> craftingSlots, List<Integer> inventorySlots, boolean maxTransfer, boolean requireCompleteSets) {
      this.recipeMap = recipeMap;
      this.craftingSlots = craftingSlots;
      this.inventorySlots = inventorySlots;
      this.maxTransfer = maxTransfer;
      this.requireCompleteSets = requireCompleteSets;
   }

   public IPacketId getPacketId() {
      return PacketIdServer.RECIPE_TRANSFER;
   }

   public void writePacketData(PacketBuffer buf) {
      buf.writeVarInt(this.recipeMap.size());

      for(Entry<Integer, Integer> recipeMapEntry : this.recipeMap.entrySet()) {
         buf.writeVarInt(((Integer)recipeMapEntry.getKey()).intValue());
         buf.writeVarInt(((Integer)recipeMapEntry.getValue()).intValue());
      }

      buf.writeVarInt(this.craftingSlots.size());

      for(Integer craftingSlot : this.craftingSlots) {
         buf.writeVarInt(craftingSlot.intValue());
      }

      buf.writeVarInt(this.inventorySlots.size());

      for(Integer inventorySlot : this.inventorySlots) {
         buf.writeVarInt(inventorySlot.intValue());
      }

      buf.writeBoolean(this.maxTransfer);
      buf.writeBoolean(this.requireCompleteSets);
   }

   public static void readPacketData(PacketBuffer buf, EntityPlayer player) {
      int recipeMapSize = buf.readVarInt();
      Map<Integer, Integer> recipeMap = new HashMap(recipeMapSize);

      for(int i = 0; i < recipeMapSize; ++i) {
         int slotIndex = buf.readVarInt();
         int recipeItem = buf.readVarInt();
         recipeMap.put(Integer.valueOf(slotIndex), Integer.valueOf(recipeItem));
      }

      int craftingSlotsSize = buf.readVarInt();
      List<Integer> craftingSlots = new ArrayList(craftingSlotsSize);

      for(int i = 0; i < craftingSlotsSize; ++i) {
         int slotIndex = buf.readVarInt();
         craftingSlots.add(Integer.valueOf(slotIndex));
      }

      int inventorySlotsSize = buf.readVarInt();
      List<Integer> inventorySlots = new ArrayList(inventorySlotsSize);

      for(int i = 0; i < inventorySlotsSize; ++i) {
         int slotIndex = buf.readVarInt();
         inventorySlots.add(Integer.valueOf(slotIndex));
      }

      boolean maxTransfer = buf.readBoolean();
      boolean requireCompleteSets = buf.readBoolean();
      BasicRecipeTransferHandlerServer.setItems(player, recipeMap, craftingSlots, inventorySlots, maxTransfer, requireCompleteSets);
   }
}
