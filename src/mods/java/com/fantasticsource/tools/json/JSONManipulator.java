package com.fantasticsource.tools.json;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class JSONManipulator {
   public JSONManipulator() {
   }

   private static void removeCNPCBankData() {
      try {
         File file = new File("playerdata");
         ArrayList<String> strings = new ArrayList();
         int braceCount = 0;

         for(File f : file.listFiles()) {
            boolean deleting = false;
            strings.clear();
            BufferedReader reader = new BufferedReader(new FileReader(f));

            String s;
            while((s = reader.readLine()) != null) {
               if (s.contains("BankData")) {
                  deleting = true;
                  strings.add(s);
                  braceCount = 1;
               } else if (deleting) {
                  if (s.contains("[")) {
                     ++braceCount;
                  }

                  if (s.contains("]")) {
                     --braceCount;
                  }

                  if (braceCount == 0) {
                     deleting = false;
                     strings.add(s);
                  }
               } else {
                  strings.add(s);
               }
            }

            reader.close();
            BufferedWriter writer = new BufferedWriter(new FileWriter(f));

            for(String s2 : strings) {
               writer.write(s2 + "\n");
            }

            writer.close();
         }
      } catch (IOException var13) {
         var13.printStackTrace();
      }

   }

   private static void removeCNPCQuestData() {
      try {
         File file = new File("playerdata");
         ArrayList<String> strings = new ArrayList();
         int braceCount = 0;

         for(File f : file.listFiles()) {
            boolean deleting = false;
            strings.clear();
            BufferedReader reader = new BufferedReader(new FileReader(f));

            String s;
            while((s = reader.readLine()) != null) {
               if (!s.contains("QuestData") && !s.contains("DialogData")) {
                  if (deleting) {
                     if (s.contains("[")) {
                        ++braceCount;
                     }

                     if (s.contains("]")) {
                        --braceCount;
                     }

                     if (braceCount == 0) {
                        deleting = false;
                        strings.add(s);
                     }
                  } else {
                     strings.add(s);
                  }
               } else {
                  deleting = true;
                  strings.add(s);
                  braceCount = 1;
               }
            }

            reader.close();
            BufferedWriter writer = new BufferedWriter(new FileWriter(f));

            for(String s2 : strings) {
               writer.write(s2 + "\n");
            }

            writer.close();
         }
      } catch (IOException var13) {
         var13.printStackTrace();
      }

   }
}
