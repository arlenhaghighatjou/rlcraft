package slimeknights.mantle.config;

import com.google.common.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import slimeknights.mantle.configurate.ConfigurationNode;
import slimeknights.mantle.configurate.ConfigurationOptions;
import slimeknights.mantle.configurate.commented.CommentedConfigurationNode;
import slimeknights.mantle.configurate.hocon.HoconConfigurationLoader;
import slimeknights.mantle.configurate.loader.AtomicFiles;
import slimeknights.mantle.configurate.loader.ConfigurationLoader;
import slimeknights.mantle.configurate.objectmapping.ObjectMappingException;
import slimeknights.mantle.configurate.objectmapping.Setting;
import slimeknights.mantle.configurate.objectmapping.serialize.ConfigSerializable;
import slimeknights.mantle.configurate.objectmapping.serialize.TypeSerializer;
import slimeknights.mantle.configurate.objectmapping.serialize.TypeSerializers;

public abstract class AbstractConfigFile implements Serializable {
   private static boolean initialized = false;
   File file;
   ConfigurationLoader<CommentedConfigurationNode> loader;
   private boolean needsSaving;
   @Setting("_VERSION")
   private int configVersion;

   public AbstractConfigFile() {
      this.needsSaving = true;
      this.file = null;
      this.loader = null;
   }

   public AbstractConfigFile(File configFolder, String name) {
      this(new File(configFolder, name + ".cfg"));
   }

   public AbstractConfigFile(File configFile) {
      this.needsSaving = true;
      configFile.getParentFile().mkdirs();
      this.file = configFile;
      this.loader = ((HoconConfigurationLoader.Builder)HoconConfigurationLoader.builder().setFile(this.file)).build();
   }

   public CommentedConfigurationNode load() throws IOException {
      return this.loader.load(ConfigurationOptions.defaults().setShouldCopyDefaults(true));
   }

   public void save(ConfigurationNode node) throws IOException {
      this.loader.save(node);
   }

   public String getName() {
      return this.file.getName();
   }

   public abstract void insertDefaults();

   protected abstract int getConfigVersion();

   void setConfigVersion() {
      if (this.configVersion != this.getConfigVersion()) {
         this.configVersion = this.getConfigVersion();
         this.setNeedsSaving();
      }

   }

   public AbstractConfigFile loadFromPacket(byte[] packetData) {
      ConfigurationLoader<CommentedConfigurationNode> packetDataLoader = ((HoconConfigurationLoader.Builder)((HoconConfigurationLoader.Builder)HoconConfigurationLoader.builder().setSource(() -> {
         return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(packetData)));
      })).setSink(AtomicFiles.createAtomicWriterFactory(this.file.toPath(), StandardCharsets.UTF_8))).build();

      try {
         CommentedConfigurationNode node = packetDataLoader.load(ConfigurationOptions.defaults().setShouldCopyDefaults(true));

         try {
            return (AbstractConfigFile)node.getValue(TypeToken.of(this.getClass()));
         } catch (ObjectMappingException var5) {
            var5.printStackTrace();
         }
      } catch (IOException var6) {
         var6.printStackTrace();
      }

      return null;
   }

   public static List<Field> getAllFields(List<Field> fields, Class<?> type) {
      if (type != AbstractConfigFile.class) {
         fields.addAll(Arrays.asList(type.getDeclaredFields()));
         if (type.getSuperclass() != null && AbstractConfigFile.class.isAssignableFrom(type.getSuperclass())) {
            fields = getAllFields(fields, type.getSuperclass());
         }
      }

      return fields;
   }

   public byte[] getPacketData() {
      try {
         return Files.readAllBytes(this.file.toPath());
      } catch (IOException var2) {
         var2.printStackTrace();
         return null;
      }
   }

   public boolean sync(AbstractConfigFile other) {
      return this.sync(other, this);
   }

   public boolean sync(Object other, Object that) {
      if (other.getClass() != that.getClass()) {
         return false;
      } else {
         List<Field> fieldsToProcess = new ArrayList();
         getAllFields(fieldsToProcess, that.getClass());

         for(Field field : fieldsToProcess) {
            this.syncField(other, that, field);
         }

         return this.needsSaving();
      }
   }

   private void syncField(Object other, Object that, Field field) {
      try {
         if (Modifier.isTransient(field.getModifiers())) {
            return;
         }

         if (Modifier.isStatic(field.getModifiers())) {
            return;
         }

         if (!field.isAccessible()) {
            field.setAccessible(true);
         }

         Object original = field.get(that);
         Object remote = field.get(other);
         if (field.getType().isAnnotationPresent(ConfigSerializable.class)) {
            this.sync(remote, original);
         } else if (!original.equals(remote)) {
            field.set(that, remote);
            this.setNeedsSaving();
         }
      } catch (IllegalAccessException var6) {
         var6.printStackTrace();
      }

   }

   public void setNeedsSaving() {
      this.needsSaving = true;
   }

   public boolean needsSaving() {
      return this.needsSaving;
   }

   public void clearNeedsSaving() {
      this.needsSaving = false;
   }

   public static void init() {
      if (!initialized) {
         TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(Block.class), new AbstractConfigFile.RegistrySerializer<Block>() {
            IForgeRegistry<Block> getRegistry() {
               return ForgeRegistries.BLOCKS;
            }
         });
         TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(Item.class), new AbstractConfigFile.RegistrySerializer<Item>() {
            IForgeRegistry<Item> getRegistry() {
               return ForgeRegistries.ITEMS;
            }
         });
         TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(BlockMeta.class), BlockMeta.SERIALIZER);
         initialized = true;
      }
   }

   private abstract static class RegistrySerializer<T extends IForgeRegistryEntry<T>> implements TypeSerializer<T> {
      private RegistrySerializer() {
      }

      abstract IForgeRegistry<T> getRegistry();

      public T deserialize(TypeToken<?> typeToken, ConfigurationNode configurationNode) throws ObjectMappingException {
         return (T)this.getRegistry().getValue(new ResourceLocation(configurationNode.getString()));
      }

      public void serialize(TypeToken<?> typeToken, T t, ConfigurationNode configurationNode) throws ObjectMappingException {
         configurationNode.setValue(t.getRegistryName());
      }
   }
}
