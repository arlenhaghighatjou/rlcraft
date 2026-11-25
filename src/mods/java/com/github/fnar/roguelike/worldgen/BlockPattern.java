package com.github.fnar.roguelike.worldgen;

import greymerk.roguelike.worldgen.BlockBrush;
import greymerk.roguelike.worldgen.Coord;
import greymerk.roguelike.worldgen.Direction;
import greymerk.roguelike.worldgen.WorldEditor;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BlockPattern {
   private final WorldEditor worldEditor;
   private final Map<Character, BlockBrush> blockBrushMap;
   private final List<List<List<BlockBrush>>> patternOfBrushes;
   private String sliceDelimiter = "\n\n";
   private String rowDelimiter = "\n";
   private String blockDelimiter = " ";

   public BlockPattern(WorldEditor worldEditor, String pattern, Map<Character, BlockBrush> blockBrushMap) {
      this.worldEditor = worldEditor;
      this.blockBrushMap = blockBrushMap;
      this.patternOfBrushes = this.parseSlices(pattern);
   }

   public void stroke(Coord origin, Direction facing) {
      this.stroke(origin, facing, (brush, rowCursor) -> {
         brush.stroke(this.worldEditor, rowCursor);
      });
   }

   public void stroke(Coord origin, Direction facing, boolean fillAir, boolean fillSolid) {
      BiConsumer<BlockBrush, Coord> stroke = (brush, rowCursor) -> {
         brush.stroke(this.worldEditor, rowCursor, fillAir, fillSolid);
      };
      this.stroke(origin, facing, stroke);
   }

   private void stroke(Coord origin, Direction facing, BiConsumer<BlockBrush, Coord> stroke) {
      Coord cursor = origin.copy();

      for(List<List<BlockBrush>> slice : this.patternOfBrushes) {
         Coord sliceCursor = cursor.copy();

         for(List<BlockBrush> row : slice) {
            Coord rowCursor = sliceCursor.copy();

            for(BlockBrush brush : row) {
               stroke.accept(brush, rowCursor);
               rowCursor.translate(facing.right());
            }

            sliceCursor.translate(facing.back());
         }

         cursor.up();
      }

   }

   private List<List<List<BlockBrush>>> parseSlices(String pattern) {
      String[] slices = pattern.split(this.sliceDelimiter);
      return (List)Arrays.stream(slices).map(this::parseSlice).collect(Collectors.toList());
   }

   private List<List<BlockBrush>> parseSlice(String slicePattern) {
      String[] rows = slicePattern.split(this.rowDelimiter);
      return (List)Arrays.stream(rows).map(this::parseRow).collect(Collectors.toList());
   }

   private List<BlockBrush> parseRow(String row) {
      if (row.isEmpty()) {
         return Collections.emptyList();
      } else {
         Stream var10000 = Arrays.stream(row.split(this.blockDelimiter)).map((str) -> {
            return str.charAt(0);
         });
         Map var10001 = this.blockBrushMap;
         this.blockBrushMap.getClass();
         return (List)var10000.map(var10001::get).collect(Collectors.toList());
      }
   }

   public void setSliceDelimiter(String sliceDelimiter) {
      this.sliceDelimiter = sliceDelimiter;
   }

   public void setRowDelimiter(String rowDelimiter) {
      this.rowDelimiter = rowDelimiter;
   }

   public void setBlockDelimiter(String blockDelimiter) {
      this.blockDelimiter = blockDelimiter;
   }
}
