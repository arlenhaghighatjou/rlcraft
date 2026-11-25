package com.fantasticsource.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class StringList {
   private String filename;
   private ArrayList<String> strings = new ArrayList(10);

   public StringList(String filename) {
      this.filename = filename;
      this.load();
   }

   public void save() {
      (new File("data/")).mkdir();
      File f = new File("data/" + this.filename + ".txt");

      try {
         FileWriter fw = new FileWriter(f);

         for(String string : this.strings) {
            fw.write(string + "\r\n");
         }

         fw.flush();
         fw.close();
      } catch (IOException var5) {
         var5.printStackTrace();
      }

   }

   public void load() {
      try {
         BufferedReader fileReader = new BufferedReader(new FileReader("data/" + this.filename + ".txt"));

         try {
            for(String line = fileReader.readLine(); line != null; line = fileReader.readLine()) {
               this.strings.add(line);
            }

            fileReader.close();
         } catch (IOException var3) {
            var3.printStackTrace();
         }
      } catch (FileNotFoundException var4) {
         ;
      }

      this.save();
   }

   public String get(int index) {
      return (String)this.strings.get(index);
   }

   public int indexOf(String string) {
      for(int i = 0; i < this.size(); ++i) {
         if (this.get(i).equals(string)) {
            return i;
         }
      }

      return -1;
   }

   public boolean contains(String string) {
      return this.indexOf(string) > -1;
   }

   public int size() {
      return this.strings.size();
   }

   public void add(String string) {
      this.strings.add(string);
      this.save();
   }

   public boolean remove(String string) {
      int i = this.strings.indexOf(string);
      if (i > -1) {
         this.remove(i);
         return true;
      } else {
         return false;
      }
   }

   public void remove(int index) {
      this.strings.remove(index);
      this.save();
   }
}
