package jaredbgreat.dldungeons.builder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.function.Consumer;
import net.minecraft.world.World;

public class BlockFamily implements IBlockPlacer {
   private static final Map<String, BlockFamily> FAMILIES = new HashMap();
   private final String name;
   private final IBlockPlacer[] blocks;
   private static EaglercraftRandom random;

   private BlockFamily(String name, List<IBlockPlacer> theBlocks) {
      this.name = name;
      this.blocks = (IBlockPlacer[])theBlocks.toArray(new IBlockPlacer[theBlocks.size()]);
      random = new EaglercraftRandom();
   }

   public static void setRadnom(EaglercraftRandom r) {
      random = r;
   }

   public String getName() {
      return this.name;
   }

   public void placeNoMeta(World world, int x, int y, int z) {
      this.blocks[random.nextInt(this.blocks.length)].placeNoMeta(world, x, y, z);
   }

   public void place(World world, int x, int y, int z) {
      this.blocks[random.nextInt(this.blocks.length)].place(world, x, y, z);
   }

   public boolean equals(Object other) {
      return !(other instanceof BlockFamily) ? false : this.name.equals(((BlockFamily)other).name);
   }

   public int hashCode() {
      return this.name.hashCode();
   }

   public static BlockFamily makeBlockFamily(String json) {
      JsonParser parser = new JsonParser();
      JsonObject data = parser.parse(json).getAsJsonObject();
      final List<IBlockPlacer> blocks = new LinkedList();
      if (data.has("name")) {
         String name = "$" + data.get("name").getAsString();
         if (data.has("blocks")) {
            data.get("blocks").getAsJsonArray().forEach(new Consumer<JsonElement>() {
               public void accept(JsonElement t) {
                  int id = RegisteredBlock.add(t.getAsString());
                  blocks.add(RegisteredBlock.getPlacer(id));
               }
            });
            if (blocks.isEmpty()) {
               throw new RuntimeException("DLDungeonsJBG tried to load BlockFamily empty block list!");
            } else {
               BlockFamily out = new BlockFamily(name, blocks);
               FAMILIES.put(name, out);
               return out;
            }
         } else {
            throw new RuntimeException("DLDungeonsJBG tried to load BlockFamily with no blocks!");
         }
      } else {
         throw new RuntimeException("DLDungeonsJBG tried to load BlockFamily with no Name!");
      }
   }

   public static BlockFamily getBlockFamily(String name) {
      return (BlockFamily)FAMILIES.get(name);
   }

   public Object getContents() {
      return this.blocks;
   }
}
