package com.endertech.minecraft.forge.versions;

import com.endertech.common.CommonArray;
import com.endertech.common.CommonString;
import com.endertech.minecraft.forge.units.UnitId;
import java.util.EnumMap;

public class ForgeJoiner {
   private ForgeJoiner.Delimiter delimiter;
   private final EnumMap<GameVersions, String[]> mapWords;

   public ForgeJoiner(String... words) {
      this(ForgeJoiner.Delimiter.none(), words);
   }

   public ForgeJoiner(ForgeJoiner.Delimiter delimiter, String... words) {
      this.mapWords = new EnumMap(GameVersions.class);
      this.delimiter = delimiter;

      for(GameVersions version : GameVersions.values()) {
         this.mapWords.put(version, words);
      }

   }

   public static ForgeJoiner withRegNameDelimiters(String... words) {
      return new ForgeJoiner(ForgeJoiner.Delimiter.regName(), words);
   }

   public ForgeJoiner withDelimiter(ForgeJoiner.Delimiter delimiter) {
      this.delimiter = delimiter;
      return this;
   }

   public ForgeJoiner setFor(GameVersions version, String... words) {
      this.mapWords.put(version, words);
      return this;
   }

   public ForgeJoiner oreDictFormatFor(GameVersions version) {
      this.capitalizeWordsFor(version);
      String[] words = (String[])this.mapWords.get(version);
      if (words.length > 0) {
         words[0] = CommonString.decapitalizeFirstChar(words[0]);
      }

      this.delimiter.setFor(version, "");
      return this;
   }

   public ForgeJoiner reverseWordsOrderFor(GameVersions version) {
      String[] words = (String[])this.mapWords.get(version);
      CommonArray.reverse(words);
      return this;
   }

   public ForgeJoiner capitalizeWordsFor(GameVersions version) {
      String[] words = (String[])this.mapWords.get(version);
      this.mapWords.put(version, UnitId.capitalizeWords(words));
      return this;
   }

   public ForgeJoiner.Delimiter getDelimiter() {
      return this.delimiter;
   }

   protected String toString(GameVersions version) {
      String delimiter = this.delimiter.toString(version);
      String[] words = (String[])this.mapWords.get(version);
      return UnitId.joinWords(delimiter, words);
   }

   public String toString() {
      return this.toString(GameVersions.getCurrent());
   }

   public static class Delimiter {
      public static final String NONE = "";
      public static final String REG_NAME = "_";
      private final EnumMap<GameVersions, String> mapDelimiters = new EnumMap(GameVersions.class);

      public Delimiter(String delimiter) {
         for(GameVersions version : GameVersions.values()) {
            this.mapDelimiters.put(version, delimiter);
         }

      }

      public static ForgeJoiner.Delimiter none() {
         return new ForgeJoiner.Delimiter("");
      }

      public static ForgeJoiner.Delimiter regName() {
         return new ForgeJoiner.Delimiter("_");
      }

      public ForgeJoiner.Delimiter setFor(GameVersions version, String delimiter) {
         this.mapDelimiters.put(version, delimiter);
         return this;
      }

      public ForgeJoiner.Delimiter setForOlderThan(GameVersions version, String delimiter) {
         for(GameVersions ver : GameVersions.values()) {
            if (ver.ordinal() < version.ordinal() && ver != GameVersions.DEFAULT) {
               this.setFor(ver, delimiter);
            }
         }

         return this;
      }

      public ForgeJoiner.Delimiter setForNewerThan(GameVersions version, String delimiter) {
         for(GameVersions ver : GameVersions.values()) {
            if (ver.ordinal() > version.ordinal() && ver != GameVersions.DEFAULT) {
               this.setFor(ver, delimiter);
            }
         }

         return this;
      }

      public ForgeJoiner.Delimiter setNoneFor(GameVersions version) {
         return this.setFor(version, "");
      }

      protected String toString(GameVersions version) {
         return (String)this.mapDelimiters.get(version);
      }

      public String toString() {
         return this.toString(GameVersions.getCurrent());
      }
   }
}
