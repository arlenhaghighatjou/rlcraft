package hunternif.mc.atlas.client;

import hunternif.mc.atlas.util.SaveData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TextureSetMap extends SaveData {
   private static final TextureSetMap INSTANCE = new TextureSetMap();
   private final Map<String, TextureSet> map = new HashMap();

   public TextureSetMap() {
   }

   public static TextureSetMap instance() {
      return INSTANCE;
   }

   public void register(TextureSet set) {
      TextureSet old = (TextureSet)this.map.put(set.name, set);
      if (!set.equals(old)) {
         this.markDirty();
      }

   }

   public TextureSet createAndRegister(ResourceLocation... textures) {
      TextureSet set = new TextureSet(EaglercraftUUID.randomUUID().toString(), textures);
      this.register(set);
      return set;
   }

   public TextureSet getByName(String name) {
      return (TextureSet)this.map.get(name);
   }

   public TextureSet getByNameNonNull(String name) {
      TextureSet set = this.getByName(name);
      return set == null ? TextureSet.TEST : set;
   }

   public boolean isRegistered(String name) {
      return this.map.containsKey(name);
   }

   public Collection<TextureSet> getAllTextureSets() {
      return this.map.values();
   }

   Collection<TextureSet> getAllNonStandardTextureSets() {
      List<TextureSet> list = new ArrayList(this.map.size());

      for(TextureSet set : this.map.values()) {
         if (!set.isStandard) {
            list.add(set);
         }
      }

      return list;
   }
}
