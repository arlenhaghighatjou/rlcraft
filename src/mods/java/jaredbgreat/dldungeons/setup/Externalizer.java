package jaredbgreat.dldungeons.setup;

import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class Externalizer {
   private BufferedReader instream;
   private BufferedWriter outstream;
   private VFile2 outFile;
   private static final String BASE_LOCATION = "/jaredbgreat/dldungeons/res/themes/";
   private static String outDirectory;
   private static final String[] THEMES = new String[]{"common.cfg", "continentalShelf.cfg", "dank.cfg", "desert.cfg", "frozen.cfg", "jungle.cfg", "mesa.cfg", "nether.cfg", "oceanic.cfg", "template.cfg", "urban.cfg", "villagelike.cfg", "volcanic.cfg"};
   private static final String[] BLOCKS = new String[]{"mixedcobble.json", "mixedbricks1.json", "mixedbricks2.json", "mixedbricks3.json", "crumbling_walls1.json", "crumbling_walls2.json"};

   public Externalizer(String outDir) {
      outDirectory = outDir;
   }

   public void copyOut(String name) {
      this.copyOut(name, name);
   }

   public void copyOut(String name, String outloc) {
      try {
         this.outFile = new VFile2(outDirectory + outloc);
         if (this.outFile.exists()) {
            return;
         }

         this.instream = new BufferedReader(new InputStreamReader(EagRuntime.getRequiredResourceStream("/dldungeonsjbg/jaredbgreat/dldungeons/res/themes/" + name), StandardCharsets.UTF_8));
         this.outstream = new BufferedWriter(new OutputStreamWriter(this.outFile.getOutputStream(), StandardCharsets.UTF_8));
         String line;
         if (this.instream != null && this.outstream != null) {
            while((line = this.instream.readLine()) != null) {
               this.outstream.write(line + "\n");
            }
         } else {
            System.err.println("[DLDUNGEONS] Error! Failed to write theme file " + this.outFile);
         }

         if (this.instream != null) {
            this.instream.close();
         }

         if (this.outstream != null) {
            this.outstream.close();
         }
      } catch (IOException var4) {
         System.err.println("[DLDUNGEONS] Error! Failed to write theme file " + this.outFile);
      }

   }

   public boolean forceOut(String name) {
      return this.forceOut(name, name);
   }

   public boolean forceOut(String name, String outloc) {
      boolean result = true;

      try {
         this.outFile = new VFile2(outDirectory + outloc);
         if (this.outFile.exists()) {
            this.outFile.delete();
         }

         this.instream = new BufferedReader(new InputStreamReader(EagRuntime.getRequiredResourceStream("/dldungeonsjbg/jaredbgreat/dldungeons/res/themes/" + name), StandardCharsets.UTF_8));
         this.outstream = new BufferedWriter(new OutputStreamWriter(this.outFile.getOutputStream(), StandardCharsets.UTF_8));
         String line;
         if (this.instream != null && this.outstream != null) {
            while((line = this.instream.readLine()) != null) {
               this.outstream.write(line + "\n");
            }
         } else {
            System.err.println("[DLDUNGEONS] Error! Failed to write theme file " + this.outFile);
            result = false;
         }

         if (this.instream != null) {
            this.instream.close();
         }

         if (this.outstream != null) {
            this.outstream.close();
         }
      } catch (IOException var5) {
         System.err.println("[DLDUNGEONS] Error! Failed to write theme file " + this.outFile);
         result = false;
         var5.printStackTrace();
      }

      return result;
   }

   public void makeThemes() {
      for(String name : THEMES) {
         System.out.println("[DLDUNGEONS] Installing file " + outDirectory + name);
         this.copyOut(name);
      }

   }

   public void makeBlocks() {
      for(String name : BLOCKS) {
         System.out.println("[DLDUNGEONS] Installing file " + outDirectory + name);
         this.copyOut(name);
      }

   }

   public void forceThemes() {
      for(String name : THEMES) {
         System.out.println("[DLDUNGEONS] Installing file " + outDirectory + name);
         this.forceOut(name);
      }

   }

   public void forceBlocks() {
      for(String name : BLOCKS) {
         System.out.println("[DLDUNGEONS] Installing file " + outDirectory + name);
         this.forceOut(name);
      }

   }

   public void makeChestCfg() {
      System.out.println("[DLDUNGEONS] Installing files " + outDirectory + "chests.cfg");
      this.copyOut("nbt.cfg");
      this.copyOut("chests.cfg");
      this.copyOut("oceanic_chests.cfg", "SpecialChests/oceanic_chests.cfg");
   }

   public void forceChestCfg() {
      System.out.println("[DLDUNGEONS] Installing files " + outDirectory + "chests.cfg");
      this.forceOut("chests.cfg");
      this.forceOut("SpecialChests/oceanic_chests.cfg");
   }

   public void makeNBTCfg() {
      System.out.println("[DLDUNGEONS] Installing files " + outDirectory + "nbt.cfg and ");
      this.copyOut("nbt.cfg");
   }

   public void forceNBTCfg() {
      System.out.println("[DLDUNGEONS] Installing files " + outDirectory + "nbt.cfg and ");
      this.forceOut("nbt.cfg");
   }
}
