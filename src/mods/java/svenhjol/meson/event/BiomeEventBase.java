package svenhjol.meson.event;

import net.minecraft.block.BlockDoor;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.terraingen.BiomeEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;

public class BiomeEventBase extends Event {
   public BiomeEventBase() {
   }

   @HasResult
   public static class GetVillageDoor extends BiomeEvent {
      private final BlockDoor door;
      private BlockDoor replacement;

      public GetVillageDoor(Biome biome, BlockDoor door) {
         super(biome);
         this.door = door;
      }

      public BlockDoor getOriginal() {
         return this.door;
      }

      public BlockDoor getReplacement() {
         return this.replacement;
      }

      public void setReplacement(BlockDoor replacement) {
         this.replacement = replacement;
      }
   }
}
