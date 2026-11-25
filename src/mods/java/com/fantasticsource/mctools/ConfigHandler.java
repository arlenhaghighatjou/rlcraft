package com.fantasticsource.mctools;

import com.fantasticsource.fantasticlib.FantasticLib;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {
   protected final String modid;
   protected final File file;
   protected final ArrayList<String> lines = new ArrayList();
   protected final ArrayList<String> comments = new ArrayList();
   protected Configuration config;
   protected BufferedReader reader;
   protected BufferedWriter writer;

   public ConfigHandler(String modid) throws IllegalAccessException {
      this.modid = modid;
      this.config = MCTools.getConfig(modid);
      this.file = this.config.getConfigFile();
   }

   public ConfigHandler load() throws IOException {
      this.reader = new BufferedReader(new FileReader(this.file));

      for(String line = this.reader.readLine(); line != null; line = this.reader.readLine()) {
         int index = line.indexOf("#");
         if (index > -1) {
            this.lines.add(line.substring(0, index));
            this.comments.add(line.substring(index));
         } else {
            this.lines.add(line);
            this.comments.add("");
         }
      }

      this.reader.close();
      return this;
   }

   public ConfigHandler save() throws IOException {
      this.writer = new BufferedWriter(new FileWriter(this.file));

      for(int i = 0; i < this.lines.size(); ++i) {
         this.writer.write((String)this.lines.get(i) + (String)this.comments.get(i) + (i < this.lines.size() - 1 ? "\r\n" : ""));
      }

      this.writer.close();
      return this;
   }

   public ConfigHandler sync() throws IllegalAccessException {
      MCTools.reloadConfig(this.modid);
      this.config = MCTools.getConfig(this.modid);
      return this;
   }

   public ConfigHandler removeCategory(String path) throws IllegalAccessException {
      String[] targetNodes = path.split("[.]");
      ArrayList<String> currentNodes = new ArrayList();

      for(int i = 0; i < this.lines.size(); ++i) {
         String line = (String)this.lines.get(i);
         if (!line.trim().equals("")) {
            boolean parentFound = true;
            if (currentNodes.size() != targetNodes.length) {
               parentFound = false;
            } else {
               for(int i2 = 0; i2 < targetNodes.length; ++i2) {
                  if (!((String)currentNodes.get(i2)).equals(targetNodes[i2])) {
                     parentFound = false;
                     break;
                  }
               }
            }

            if (parentFound) {
               this.removeCategory(i - 1);
               if (FantasticLib.isClient) {
                  MCTools.removeLangKey(((String)currentNodes.get(currentNodes.size() - 1)).toLowerCase());
               }
               break;
            }

            if (line.contains("}")) {
               currentNodes.remove(currentNodes.size() - 1);
            } else if (line.contains("{")) {
               String s = line.substring(0, line.indexOf("{")).trim();
               int index = s.indexOf(34);
               int lastIndex = s.lastIndexOf(34);
               if (index > -1 && lastIndex != index) {
                  s = s.substring(index + 1, lastIndex);
               }

               currentNodes.add(s);
            }
         }
      }

      return this;
   }

   public ConfigHandler addCategory(String path) throws IllegalAccessException {
      String[] targetNodes = path.split("[.]");
      ArrayList<String> currentNodes = new ArrayList();

      for(int i = 0; i < this.lines.size(); ++i) {
         String line = (String)this.lines.get(i);
         if (!line.trim().equals("")) {
            boolean parentFound = true;
            if (currentNodes.size() != targetNodes.length - 1) {
               parentFound = false;
            } else {
               for(int i2 = 0; i2 < targetNodes.length - 1; ++i2) {
                  if (!((String)currentNodes.get(i2)).equals(targetNodes[i2])) {
                     parentFound = false;
                     break;
                  }
               }
            }

            if (parentFound) {
               this.addCategory(i, targetNodes[targetNodes.length - 1]);
               if (FantasticLib.isClient) {
                  MCTools.addLangKey(targetNodes[targetNodes.length - 1].toLowerCase(), targetNodes[targetNodes.length - 1]);
               }
               break;
            }

            if (line.contains("}")) {
               currentNodes.remove(currentNodes.size() - 1);
            } else if (line.contains("{")) {
               String s = line.substring(0, line.indexOf("{")).trim();
               int index = s.indexOf(34);
               int lastIndex = s.lastIndexOf(34);
               if (index > -1 && lastIndex != index) {
                  s = s.substring(index + 1, lastIndex);
               }

               currentNodes.add(s);
            }
         }
      }

      return this;
   }

   public ConfigHandler addProperty(String path, Object value) {
      String[] targetNodes = path.split("[.]");
      ArrayList<String> currentNodes = new ArrayList();

      for(int i = 0; i < this.lines.size(); ++i) {
         String line = (String)this.lines.get(i);
         if (!line.trim().equals("")) {
            boolean parentFound = true;
            if (currentNodes.size() != targetNodes.length - 1) {
               parentFound = false;
            } else {
               for(int i2 = 0; i2 < targetNodes.length - 1; ++i2) {
                  if (!((String)currentNodes.get(i2)).equals(targetNodes[i2])) {
                     parentFound = false;
                     break;
                  }
               }
            }

            if (parentFound) {
               this.addProperty(i, targetNodes[targetNodes.length - 1], value);
               break;
            }

            if (line.contains("}")) {
               currentNodes.remove(currentNodes.size() - 1);
            } else if (line.contains("{")) {
               String s = line.substring(0, line.indexOf("{")).trim();
               int index = s.indexOf(34);
               int lastIndex = s.lastIndexOf(34);
               if (index > -1 && lastIndex != index) {
                  s = s.substring(index + 1, lastIndex);
               }

               currentNodes.add(s);
            }
         }
      }

      return this;
   }

   protected void removeCategory(int index) {
      int parens = 1;
      this.lines.remove(index);
      this.comments.remove(index);

      while(parens > 0) {
         this.comments.remove(index);
         String line = (String)this.lines.remove(index);
         if (line.contains("{")) {
            ++parens;
         } else if (line.contains("}")) {
            --parens;
         }
      }

   }

   protected void addCategory(int index, String name) {
      this.lines.add(index++, name + " {");
      this.comments.add("");
      this.lines.add(index++, "}");
      this.comments.add("");
   }

   protected void addProperty(int index, String name, Object value) {
      if (value.getClass() == String.class) {
         this.lines.add(index++, "S:" + name + "=" + value);
         this.comments.add("");
      }

   }
}
