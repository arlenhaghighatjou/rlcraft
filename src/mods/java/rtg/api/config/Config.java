package rtg.api.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.minecraftforge.common.config.Configuration;
import org.apache.commons.lang3.ArrayUtils;
import rtg.api.config.property.ConfigProperty;
import rtg.api.config.property.ConfigPropertyArray;
import rtg.api.config.property.ConfigPropertyBoolean;
import rtg.api.config.property.ConfigPropertyFloat;
import rtg.api.config.property.ConfigPropertyInteger;
import rtg.api.config.property.ConfigPropertyString;
import rtg.api.util.Logger;

public abstract class Config {
   static final String BLOCKSTATE_HELP = Configuration.NEW_LINE + "Syntax : <ResourceLocation> [<IProperty name> = <value>, <IProperty name> = <value>, ...]" + Configuration.NEW_LINE + "Example: minecraft:stone[variant=diorite], or minecraft:stained_glass_pane[color=pink,north=true,east=false,south=true,west=false]" + Configuration.NEW_LINE + "For a list of property names and values, see: https://minecraft.gamepedia.com/Block_states";
   private final File configFile;
   protected List<ConfigProperty> properties = new ArrayList();
   private Configuration config;

   protected Config(@Nonnull File configFile) {
      this.configFile = configFile;
   }

   private void addProp(ConfigProperty property) {
      for(ConfigProperty prop : this.properties) {
         if (prop.getName().contentEquals(property.getName())) {
            this.removeProp(property.getName());
            break;
         }
      }

      this.properties.add(property);
   }

   private void removeProp(String name) {
      for(int i = 0; i < this.properties.size(); ++i) {
         if (((ConfigProperty)this.properties.get(i)).getName().contentEquals(name)) {
            this.properties.remove(i);
            return;
         }
      }

   }

   public boolean hasProperty(ConfigProperty prop) {
      return this.properties.stream().anyMatch((property) -> {
         return property.getCategory().contentEquals(prop.getCategory()) && property.getName().contentEquals(prop.getName());
      });
   }

   public final ConfigPropertyBoolean addProperty(ConfigPropertyBoolean property) {
      this.addProp(property);
      return property;
   }

   public final ConfigPropertyFloat addProperty(ConfigPropertyFloat property) {
      this.addProp(property);
      return property;
   }

   public final ConfigPropertyInteger addProperty(ConfigPropertyInteger property) {
      this.addProp(property);
      return property;
   }

   public final ConfigPropertyString addProperty(ConfigPropertyString property) {
      this.addProp(property);
      return property;
   }

   public final ConfigPropertyArray.ConfigPropertyArrayInteger addProperty(ConfigPropertyArray.ConfigPropertyArrayInteger property) {
      this.addProp(property);
      return property;
   }

   public final ConfigPropertyArray.ConfigPropertyArrayDouble addProperty(ConfigPropertyArray.ConfigPropertyArrayDouble property) {
      this.addProp(property);
      return property;
   }

   public final ConfigPropertyArray.ConfigPropertyArrayString addProperty(ConfigPropertyArray.ConfigPropertyArrayString property) {
      this.addProp(property);
      return property;
   }

   public void loadConfig() {
      this.config = new Configuration(this.configFile);

      try {
         this.config.load();

         for(ConfigProperty prop : this.properties) {
            switch(prop.getType()) {
            case INTEGER:
               ConfigPropertyInteger propInt = (ConfigPropertyInteger)prop;
               propInt.set(this.config.getInt(propInt.getName(), propInt.getCategory(), propInt.valueInt, propInt.minValueInt, propInt.maxValueInt, propInt.getDescription()));
               break;
            case INTEGER_ARRAY:
               ConfigPropertyArray.ConfigPropertyArrayInteger propIntArray = (ConfigPropertyArray.ConfigPropertyArrayInteger)prop;
               propIntArray.set(ArrayUtils.toObject(this.config.get(propIntArray.getName(), propIntArray.getCategory(), propIntArray.getPrimitives(), propIntArray.getDescription(), ((Integer)propIntArray.getMinValue()).intValue(), ((Integer)propIntArray.getMaxValue()).intValue()).getIntList()));
               break;
            case FLOAT:
               ConfigPropertyFloat propFloat = (ConfigPropertyFloat)prop;
               propFloat.set(this.config.getFloat(propFloat.getName(), propFloat.getCategory(), propFloat.valueFloat, propFloat.minValueFloat, propFloat.maxValueFloat, propFloat.getDescription()));
               break;
            case DOUBLE_ARRAY:
               ConfigPropertyArray.ConfigPropertyArrayDouble propDoubleArray = (ConfigPropertyArray.ConfigPropertyArrayDouble)prop;
               propDoubleArray.set(ArrayUtils.toObject(this.config.get(propDoubleArray.getName(), propDoubleArray.getCategory(), propDoubleArray.getPrimitives(), propDoubleArray.getDescription(), ((Double)propDoubleArray.getMinValue()).doubleValue(), ((Double)propDoubleArray.getMaxValue()).doubleValue()).getDoubleList()));
               break;
            case BOOLEAN:
               ConfigPropertyBoolean propBool = (ConfigPropertyBoolean)prop;
               propBool.set(this.config.getBoolean(propBool.getName(), propBool.getCategory(), propBool.valueBoolean, propBool.getDescription()));
               break;
            case STRING:
               ConfigPropertyString propString = (ConfigPropertyString)prop;
               propString.set(this.config.getString(propString.getName(), propString.getCategory(), propString.valueString, propString.getDescription()));
               break;
            case STRING_ARRAY:
               ConfigPropertyArray.ConfigPropertyArrayString propStringArray = (ConfigPropertyArray.ConfigPropertyArrayString)prop;
               propStringArray.set(this.config.get(propStringArray.getName(), propStringArray.getCategory(), (String[])propStringArray.getValues(), propStringArray.getDescription()).getStringList());
            }
         }
      } catch (Exception var13) {
         Logger.error("RTG had a problem loading config: {}", this.configFile);
      } finally {
         if (this.config.hasChanged()) {
            this.config.save();
         }

      }

   }

   public Configuration getConfig() {
      return this.config;
   }
}
