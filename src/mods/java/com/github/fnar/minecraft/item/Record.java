package com.github.fnar.minecraft.item;

import java.util.Random;

public class Record extends RldBaseItem {
   private Record.Song song;

   public Record() {
   }

   public static Record newRecord() {
      return new Record();
   }

   public ItemType getItemType() {
      return ItemType.RECORD;
   }

   public Record withSong(Record.Song song) {
      this.song = song;
      return this;
   }

   public Record.Song getSong() {
      return this.song;
   }

   public static enum Song {
      THIRTEEN,
      CAT,
      BLOCKS,
      CHIRP,
      FAR,
      MALL,
      MELLOHI,
      STAL,
      STRAD,
      WARD,
      ELEVEN,
      WAIT;

      private Song() {
      }

      public static Record.Song chooseRandom(Random random) {
         return values()[random.nextInt(values().length)];
      }

      public Record asItem() {
         return Record.newRecord().withSong(this);
      }
   }
}
