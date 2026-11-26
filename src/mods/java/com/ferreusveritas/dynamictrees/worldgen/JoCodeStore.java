package com.ferreusveritas.dynamictrees.worldgen;

import com.ferreusveritas.dynamictrees.util.JsonHelper;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import com.ferreusveritas.dynamictrees.trees.Species;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.util.math.MathHelper;

public class JoCodeStore {
   ArrayList<ArrayList<JoCode>> store = new ArrayList(7);
   Species species;

   public JoCodeStore(Species species) {
      this.species = species;

      for(int i = 0; i < 7; ++i) {
         this.store.add(new ArrayList());
      }

   }

   protected ArrayList<JoCode> getListForRadius(int radius) {
      radius = MathHelper.clamp(radius, 2, 8);
      return (ArrayList)this.store.get(radius - 2);
   }

   public void addCodesFromFile(Species species, String filename) {
      try {
         Logger.getLogger("dynamictrees").log(Level.CONFIG, "Loading Tree Codes for species \"" + species + "\" from file: " + filename);
         InputStream stream = EagRuntime.getResourceStream("/" + JsonHelper.resourcePath(filename));
         if (stream == null) {
            throw new FileNotFoundException(filename);
         }

         InputStreamReader streamReader = new InputStreamReader(stream, StandardCharsets.UTF_8);
         BufferedReader readIn = new BufferedReader(streamReader);

         String line;
         while((line = readIn.readLine()) != null) {
            if (line.length() >= 3 && line.charAt(0) != '#') {
               String[] split = line.split(":");
               this.addCode(species, Integer.valueOf(split[0]).intValue(), split[1]);
            }
         }
      } catch (FileNotFoundException var8) {
         Logger.getLogger("dynamictrees").log(Level.WARNING, "No JoCode file found for species \"" + species + "\" at location: " + filename);
      } catch (UnsupportedEncodingException var9) {
         var9.printStackTrace();
      } catch (IOException var10) {
         var10.printStackTrace();
      }

   }

   public void addCode(Species species, int radius, String code) {
      JoCode joCode = species.getJoCode(code).setCareful(false);
      this.getListForRadius(radius).add(joCode);
   }

   public JoCode getRandomCode(int radius, EaglercraftRandom rand) {
      ArrayList<JoCode> list = this.getListForRadius(radius);
      return !list.isEmpty() ? (JoCode)list.get(rand.nextInt(list.size())) : null;
   }
}
