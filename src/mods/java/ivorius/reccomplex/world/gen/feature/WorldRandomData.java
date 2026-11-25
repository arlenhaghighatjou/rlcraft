package ivorius.reccomplex.world.gen.feature;

import ivorius.ivtoolkit.tools.NBTTagLists;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;

public class WorldRandomData extends WorldSavedData {
   private static final String IDENTIFIER = "reccomplex-random";
   private final Set<String> showedWorldStatus;

   public WorldRandomData(String id) {
      super(id);
      this.showedWorldStatus = new HashSet();
   }

   public WorldRandomData() {
      this("reccomplex-random");
   }

   public static WorldRandomData get(World world) {
      WorldRandomData data = (WorldRandomData)world.loadData(WorldRandomData.class, "reccomplex-random");
      if (data == null) {
         data = new WorldRandomData();
         world.setData(data.mapName, data);
      }

      return data;
   }

   public boolean postWorldStatus(String player) {
      boolean add = this.showedWorldStatus.add(player);
      if (add) {
         this.setDirty(true);
      }

      return add;
   }

   public void readFromNBT(NBTTagCompound compound) {
      this.showedWorldStatus.clear();

      for(NBTBase nbt : NBTTagLists.nbtBases(compound.getTagList("showedWorldStatus", 8))) {
         this.showedWorldStatus.add(((NBTTagString)nbt).getString());
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      NBTTagLists.writeTo(compound, "showedWorldStatus", (List)this.showedWorldStatus.stream().map(NBTTagString::new).collect(Collectors.toList()));
      return compound;
   }
}
