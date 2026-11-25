package me.ichun.mods.ichunutil.common.core.config;

import com.google.common.base.Splitter;
import com.google.common.collect.Ordering;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;
import me.ichun.mods.ichunutil.client.gui.window.element.IIdentifiable;
import me.ichun.mods.ichunutil.client.gui.window.element.IListable;
import me.ichun.mods.ichunutil.client.keybind.KeyBind;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.core.config.annotations.ConfigProp;
import me.ichun.mods.ichunutil.common.core.config.annotations.IntBool;
import me.ichun.mods.ichunutil.common.core.config.annotations.IntMinMax;
import me.ichun.mods.ichunutil.common.core.config.annotations.StringValues;
import me.ichun.mods.ichunutil.common.core.config.types.Colour;
import me.ichun.mods.ichunutil.common.core.config.types.NestedIntArray;
import me.ichun.mods.ichunutil.common.packet.mod.PacketSession;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;

public abstract class ConfigBase implements Comparable<ConfigBase>, IIdentifiable, IListable {
   private Configuration config;
   private boolean setup;
   public TreeMap<ConfigBase.CategoryInfo, ArrayList<ConfigBase.PropInfo>> categories = new TreeMap(Ordering.natural());
   public HashMap<Field, Object> session = new HashMap();
   public HashMap<Field, Object> configScreen = new HashMap();
   public HashSet<Field> requiresRestart = new HashSet();
   public HashSet<Field> sessionProp = new HashSet();
   public HashSet<String> propsToReveal = new HashSet();

   public ConfigBase(File file) {
      this.config = new Configuration(file);
      this.config.load();
   }

   public abstract String getModId();

   public abstract String getModName();

   public int compareTo(ConfigBase cfg) {
      return this.getModName().compareTo(cfg.getModName());
   }

   public boolean equals(Object o) {
      return o.getClass() == this.getClass() && ((ConfigBase)o).getModName().equals(this.getModName());
   }

   public String getIdentifier() {
      return this.getModName();
   }

   public String getName() {
      return this.getModName();
   }

   public boolean localizable() {
      return false;
   }

   public void read() {
      this.readFields(false);
      this.setCategoryComments();
   }

   public void readFields(boolean write) {
      Field[] fields = this.getClass().getDeclaredFields();

      for(Field field : fields) {
         field.setAccessible(true);
         if (!Modifier.isTransient(field.getModifiers()) && field.isAnnotationPresent(ConfigProp.class)) {
            this.readProperty(field, write);
         }
      }

   }

   public void readProperty(Field field, boolean write) {
      ConfigProp propInfo = (ConfigProp)field.getAnnotation(ConfigProp.class);
      if ((!propInfo.hidden() || this.propsToReveal.contains(field.getName())) && (!propInfo.side().isClient() || !FMLCommonHandler.instance().getSide().isServer())) {
         Class clz = field.getType();
         ConfigBase.CategoryInfo info = new ConfigBase.CategoryInfo(clz.equals(KeyBind.class) ? "keybind" : (propInfo.module().isEmpty() ? propInfo.category() : "module." + propInfo.module()));
         ArrayList<ConfigBase.PropInfo> fields = null;

         for(Entry<ConfigBase.CategoryInfo, ArrayList<ConfigBase.PropInfo>> e : this.categories.entrySet()) {
            ConfigBase.CategoryInfo inf = (ConfigBase.CategoryInfo)e.getKey();
            if (inf.category.equals(info.category)) {
               info = inf;
               fields = (ArrayList)e.getValue();
               break;
            }
         }

         if (fields == null) {
            fields = new ArrayList();
            this.categories.put(info, fields);
         }

         String comment = propInfo.comment().equals("undefined") ? I18n.translateToLocal(this.getModId().toLowerCase() + ".config.prop." + field.getName() + ".comment") : I18n.translateToLocal(propInfo.comment());
         if (Splitter.on(".").splitToList(comment).size() >= 2 && !comment.contains(" ") && !this.setup) {
            iChunUtil.LOGGER.warn("Config property " + field.getName() + " from mod " + this.getModName() + " may not be localized!");
         }

         String commentLocal = I18n.translateToLocal(comment);
         List cms = Splitter.on("\\n").splitToList(commentLocal);
         String cm = "";

         for(int ll = 0; ll < cms.size(); ++ll) {
            cm = cm + cms.get(ll);
            if (ll != cms.size() - 1) {
               cm = cm + "\n";
            }
         }

         comment = cm;

         try {
            String category = !propInfo.module().isEmpty() ? "module." + propInfo.module() + "." + propInfo.category() : propInfo.category();
            if (clz.equals(Integer.TYPE)) {
               int min = Integer.MIN_VALUE;
               int max = Integer.MAX_VALUE;
               if (field.isAnnotationPresent(IntMinMax.class)) {
                  IntMinMax minMax = (IntMinMax)field.getAnnotation(IntMinMax.class);
                  min = minMax.min();
                  max = minMax.max();
               } else if (field.isAnnotationPresent(IntBool.class)) {
                  min = 0;
                  max = 1;
               }

               if (write) {
                  this.config.get(category, field.getName(), field.getInt(this)).set(field.getInt(this));
                  this.config.get(category, field.getName(), field.getInt(this)).setComment(comment);
               } else {
                  field.set(this, Integer.valueOf(this.config.getInt(field.getName(), category, field.getInt(this), min, max, comment)));
               }
            } else if (clz.equals(int[].class)) {
               int min = Integer.MIN_VALUE;
               int max = Integer.MAX_VALUE;
               if (field.isAnnotationPresent(IntMinMax.class)) {
                  IntMinMax minMax = (IntMinMax)field.getAnnotation(IntMinMax.class);
                  min = minMax.min();
                  max = minMax.max();
               }

               if (write) {
                  this.config.get(category, field.getName(), (int[])field.get(this)).set((int[])field.get(this));
                  this.config.get(category, field.getName(), (int[])field.get(this)).setComment(comment);
               } else {
                  field.set(this, this.config.get(category, field.getName(), (int[])field.get(this), comment, min, max).getIntList());
               }
            } else if (clz.equals(NestedIntArray.class)) {
               NestedIntArray nestedIntArray = (NestedIntArray)field.get(this);
               int min = Integer.MIN_VALUE;
               int max = Integer.MAX_VALUE;
               int nMin = Integer.MIN_VALUE;
               int nMax = Integer.MAX_VALUE;
               if (field.isAnnotationPresent(IntMinMax.class)) {
                  IntMinMax minMax = (IntMinMax)field.getAnnotation(IntMinMax.class);
                  min = minMax.min();
                  max = minMax.max();
                  nMin = minMax.nestedMin();
                  nMax = minMax.nestedMax();
               }

               if (write) {
                  this.config.get(category, field.getName(), nestedIntArray.serialize()).set(nestedIntArray.serialize());
                  this.config.get(category, field.getName(), nestedIntArray.serialize()).setComment(comment);
               } else {
                  nestedIntArray.deserialize(this.config.getString(field.getName(), category, nestedIntArray.serialize(), comment), min, max, nMin, nMax);
               }
            } else if (clz.equals(Colour.class)) {
               Colour clr = (Colour)field.get(this);
               if (write) {
                  this.config.get(category, field.getName(), clr.serialize()).set(clr.serialize());
                  this.config.get(category, field.getName(), clr.serialize()).setComment(comment);
               } else {
                  clr.deserialize(this.config.getString(field.getName(), category, ((Colour)field.get(this)).serialize(), comment));
               }
            } else if (clz.equals(String.class)) {
               if (write) {
                  this.config.get(category, field.getName(), (String)field.get(this)).set((String)field.get(this));
                  this.config.get(category, field.getName(), (String)field.get(this)).setComment(comment);
               } else if (field.isAnnotationPresent(StringValues.class)) {
                  StringValues minMax = (StringValues)field.getAnnotation(StringValues.class);
                  field.set(this, this.config.getString(field.getName(), category, (String)field.get(this), comment, minMax.values()));
               } else {
                  field.set(this, this.config.getString(field.getName(), category, (String)field.get(this), comment));
               }
            } else if (clz.equals(String[].class)) {
               if (write) {
                  this.config.get(category, field.getName(), (String[])field.get(this)).set((String[])field.get(this));
                  this.config.get(category, field.getName(), (String[])field.get(this)).setComment(comment);
               } else if (field.isAnnotationPresent(StringValues.class)) {
                  StringValues minMax = (StringValues)field.getAnnotation(StringValues.class);
                  field.set(this, this.config.getStringList(field.getName(), category, (String[])field.get(this), comment, minMax.values()));
               } else {
                  field.set(this, this.config.getStringList(field.getName(), category, (String[])field.get(this), comment));
               }
            } else {
               if (!clz.equals(KeyBind.class)) {
                  return;
               }

               KeyBind bind = (KeyBind)field.get(this);
               if (write) {
                  ConfigHandler.configKeybind.get("keybinds", this.getModId().toLowerCase() + "." + field.getName(), bind.serialize()).set(bind.serialize());
                  ConfigHandler.configKeybind.get("keybinds", this.getModId().toLowerCase() + "." + field.getName(), bind.serialize()).setComment(comment);
               } else {
                  bind.deserialize(ConfigHandler.configKeybind.getString(this.getModId().toLowerCase() + "." + field.getName(), "keybinds", bind.serialize(), comment));
                  bind = iChunUtil.proxy.registerKeyBind(bind, (KeyBind)null);
                  field.set(this, bind);
               }
            }

            ConfigBase.PropInfo propInfo1 = new ConfigBase.PropInfo(!propInfo.nameOverride().isEmpty() ? propInfo.nameOverride() : I18n.translateToLocal(this.getModId().toLowerCase() + ".config.prop." + field.getName() + ".name"), comment, field);
            if (!fields.contains(propInfo1)) {
               fields.add(propInfo1);
            }

            if (!propInfo.changeable()) {
               this.requiresRestart.add(field);
            }

            if (propInfo.useSession()) {
               this.sessionProp.add(field);
            }
         } catch (Exception var18) {
            ;
         }

      }
   }

   public void setCategoryComments() {
      for(Entry<ConfigBase.CategoryInfo, ArrayList<ConfigBase.PropInfo>> e : this.categories.entrySet()) {
         ConfigBase.CategoryInfo info = (ConfigBase.CategoryInfo)e.getKey();
         String cat = info.category;
         String comment;
         if (!cat.equals("general") && !cat.equals("gameplay") && !cat.equals("globalOptions") && !cat.equals("serverOptions") && !cat.equals("clientOnly") && !cat.equals("keybind") && !cat.equals("block")) {
            info.name = I18n.translateToLocal(this.getModId().toLowerCase() + ".config.cat." + cat + ".name");
            comment = I18n.translateToLocal(this.getModId().toLowerCase() + ".config.cat." + cat + ".comment");
         } else {
            info.name = I18n.translateToLocal(String.format("ichunutil.config.cat.%s.name", cat));
            comment = I18n.translateToLocal(String.format("ichunutil.config.cat.%s.comment", cat));
         }

         info.comment = comment;
         this.config.setCategoryComment(cat, comment);
      }

   }

   public void storeSession() {
      for(Field field : this.sessionProp) {
         try {
            field.setAccessible(true);
            this.session.put(field, field.get(this));
         } catch (Exception var4) {
            ;
         }
      }

   }

   public void resetSession() {
      for(Entry<Field, Object> e : this.session.entrySet()) {
         try {
            ((Field)e.getKey()).setAccessible(true);
            ((Field)e.getKey()).set(this, e.getValue());
         } catch (Exception var4) {
            ;
         }
      }

   }

   public void sendPlayerSession(EntityPlayer player) {
      iChunUtil.channel.sendTo(new PacketSession(this), player);
   }

   public void updateSessionToAllPlayers() {
      iChunUtil.channel.sendToAll(new PacketSession(this));
   }

   public void onReceiveSession() {
   }

   public void onSessionChange(Field field, Object original) {
   }

   public void enterConfigScreen() {
      for(Field field : this.sessionProp) {
         try {
            field.setAccessible(true);
            this.configScreen.put(field, field.get(this));
         } catch (Exception var4) {
            ;
         }
      }

      this.resetSession();
   }

   public void exitConfigScreen() {
      this.storeSession();

      for(Entry<Field, Object> e : this.configScreen.entrySet()) {
         try {
            ((Field)e.getKey()).setAccessible(true);
            ((Field)e.getKey()).set(this, e.getValue());
         } catch (Exception var4) {
            ;
         }
      }

      this.configScreen.clear();
   }

   public void reveal(String... toReveal) {
      boolean add = false;

      for(String s : toReveal) {
         add = this.propsToReveal.add(s) || add;
      }

      if (add) {
         this.read();
         this.save();
      }

   }

   public void onConfigChange(Field field, Object original) {
   }

   public void setup() {
      this.setup = true;
      this.save(false);
   }

   public void save(boolean readFields) {
      if (readFields) {
         this.readFields(true);
      }

      if (this.setup && this.config.hasChanged()) {
         this.config.save();
      }

   }

   public void save() {
      this.save(true);
   }

   public boolean hasSetup() {
      return this.setup;
   }

   public class CategoryInfo implements Comparable<ConfigBase.CategoryInfo>, IIdentifiable, IListable {
      public final String category;
      public String name;
      public String comment;

      public CategoryInfo(String cat) {
         this.category = this.name = cat;
      }

      public int compareTo(ConfigBase.CategoryInfo cfg) {
         return this.category.compareTo(cfg.category);
      }

      public String getIdentifier() {
         return this.category;
      }

      public String getName() {
         return this.name;
      }

      public boolean localizable() {
         return false;
      }
   }

   public class PropInfo implements Comparable<ConfigBase.PropInfo> {
      public final String name;
      public final String comment;
      public final Field field;

      public PropInfo(String name, String comment, Field field) {
         this.name = name;
         this.comment = comment;
         this.field = field;
      }

      public boolean equals(Object o) {
         if (!(o instanceof ConfigBase.PropInfo)) {
            return false;
         } else {
            ConfigBase.PropInfo cfg = (ConfigBase.PropInfo)o;
            return this.name.equals(cfg.name) && this.comment.equals(cfg.comment) && this.field.equals(cfg.field);
         }
      }

      public int compareTo(ConfigBase.PropInfo cfg) {
         return this.name.compareTo(cfg.name);
      }
   }
}
