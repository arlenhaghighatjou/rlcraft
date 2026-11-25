package slimeknights.mantle.configurate.hocon;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.regex.Pattern;
import slimeknights.mantle.configurate.ConfigurationNode;
import slimeknights.mantle.configurate.ConfigurationOptions;
import slimeknights.mantle.configurate.commented.CommentedConfigurationNode;
import slimeknights.mantle.configurate.commented.SimpleCommentedConfigurationNode;
import slimeknights.mantle.configurate.loader.AbstractConfigurationLoader;
import slimeknights.mantle.configurate.loader.CommentHandler;
import slimeknights.mantle.configurate.loader.CommentHandlers;
import slimeknights.mantle.typesafe.config.Config;
import slimeknights.mantle.typesafe.config.ConfigFactory;
import slimeknights.mantle.typesafe.config.ConfigList;
import slimeknights.mantle.typesafe.config.ConfigObject;
import slimeknights.mantle.typesafe.config.ConfigOrigin;
import slimeknights.mantle.typesafe.config.ConfigOriginFactory;
import slimeknights.mantle.typesafe.config.ConfigParseOptions;
import slimeknights.mantle.typesafe.config.ConfigRenderOptions;
import slimeknights.mantle.typesafe.config.ConfigValue;
import slimeknights.mantle.typesafe.config.ConfigValueFactory;

public class HoconConfigurationLoader extends AbstractConfigurationLoader<CommentedConfigurationNode> {
   public static final Pattern CRLF_MATCH = Pattern.compile("\r\n?");
   private final ConfigRenderOptions render;
   private final ConfigParseOptions parse;
   private static final ConfigOrigin CONFIGURATE_ORIGIN = ConfigOriginFactory.newSimple("configurate-hocon");
   private static final Constructor<? extends ConfigValue> CONFIG_OBJECT_CONSTRUCTOR;
   private static final Constructor<? extends ConfigValue> CONFIG_LIST_CONSTRUCTOR;

   public static HoconConfigurationLoader.Builder builder() {
      return new HoconConfigurationLoader.Builder();
   }

   private HoconConfigurationLoader(HoconConfigurationLoader.Builder build) {
      super(build, new CommentHandler[]{CommentHandlers.HASH, CommentHandlers.DOUBLE_SLASH});
      this.render = build.getRenderOptions();
      this.parse = build.getParseOptions();
   }

   public void loadInternal(CommentedConfigurationNode node, BufferedReader reader) throws IOException {
      Config hoconConfig = ConfigFactory.parseReader(reader, this.parse);
      hoconConfig = hoconConfig.resolve();

      for(Entry<String, ConfigValue> ent : hoconConfig.root().entrySet()) {
         this.readConfigValue((ConfigValue)ent.getValue(), node.getNode(ent.getKey()));
      }

   }

   private void readConfigValue(ConfigValue value, CommentedConfigurationNode node) {
      if (!value.origin().comments().isEmpty()) {
         node.setComment(CRLF_MATCH.matcher(Joiner.on('\n').join(value.origin().comments())).replaceAll(""));
      }

      switch(value.valueType()) {
      case OBJECT:
         if (((ConfigObject)value).isEmpty()) {
            node.setValue(ImmutableMap.of());
            break;
         } else {
            for(Entry<String, ConfigValue> ent : ((ConfigObject)value).entrySet()) {
               this.readConfigValue((ConfigValue)ent.getValue(), node.getNode(ent.getKey()));
            }

            return;
         }
      case LIST:
         List<ConfigValue> values = (ConfigList)value;

         for(int i = 0; i < values.size(); ++i) {
            this.readConfigValue((ConfigValue)values.get(i), node.getNode(i));
         }

         return;
      case NULL:
         return;
      default:
         node.setValue(value.unwrapped());
      }

   }

   protected void saveInternal(ConfigurationNode node, Writer writer) throws IOException {
      if (!node.hasMapChildren()) {
         if (node.getValue() == null) {
            writer.write(SYSTEM_LINE_SEPARATOR);
         } else {
            throw new IOException("HOCON cannot write nodes not in map format!");
         }
      } else {
         ConfigValue value = this.fromValue(node);
         String renderedValue = value.render(this.render);
         writer.write(renderedValue);
      }
   }

   private ConfigValue fromValue(ConfigurationNode node) {
      ConfigValue ret;
      if (node.hasMapChildren()) {
         Map<String, ConfigValue> children = node.getOptions().getMapFactory().<String, ConfigValue>create();

         for(Entry<Object, ? extends ConfigurationNode> ent : node.getChildrenMap().entrySet()) {
            children.put(String.valueOf(ent.getKey()), this.fromValue((ConfigurationNode)ent.getValue()));
         }

         ret = this.newConfigObject(children);
      } else if (node.hasListChildren()) {
         List<ConfigValue> children = new ArrayList();

         for(ConfigurationNode ent : node.getChildrenList()) {
            children.add(this.fromValue(ent));
         }

         ret = this.newConfigList(children);
      } else {
         ret = ConfigValueFactory.fromAnyRef(node.getValue(), "configurate-hocon");
      }

      if (node instanceof CommentedConfigurationNode) {
         CommentedConfigurationNode commentedNode = (CommentedConfigurationNode)node;
         ret = (ConfigValue)commentedNode.getComment().map((comment) -> {
            return finalRet.withOrigin(finalRet.origin().withComments(LINE_SPLITTER.splitToList(comment)));
         }).orElse(ret);
      }

      return ret;
   }

   ConfigValue newConfigObject(Map<String, ConfigValue> vals) {
      try {
         return (ConfigValue)CONFIG_OBJECT_CONSTRUCTOR.newInstance(CONFIGURATE_ORIGIN, vals);
      } catch (IllegalAccessException | InvocationTargetException | InstantiationException var3) {
         throw new RuntimeException(var3);
      }
   }

   ConfigValue newConfigList(List<ConfigValue> vals) {
      try {
         return (ConfigValue)CONFIG_LIST_CONSTRUCTOR.newInstance(CONFIGURATE_ORIGIN, vals);
      } catch (IllegalAccessException | InvocationTargetException | InstantiationException var3) {
         throw new RuntimeException(var3);
      }
   }

   public CommentedConfigurationNode createEmptyNode(ConfigurationOptions options) {
      options = options.setAcceptedTypes(ImmutableSet.of(Map.class, List.class, Double.class, Long.class, Integer.class, Boolean.class, new Class[]{String.class, Number.class}));
      return SimpleCommentedConfigurationNode.root(options);
   }

   static {
      Class<? extends ConfigValue> objectClass;
      Class<? extends ConfigValue> listClass;
      try {
         objectClass = Class.forName("slimeknights.mantle.typesafe.config.impl.SimpleConfigObject").asSubclass(ConfigValue.class);
         listClass = Class.forName("slimeknights.mantle.typesafe.config.impl.SimpleConfigList").asSubclass(ConfigValue.class);
      } catch (ClassNotFoundException var4) {
         throw new ExceptionInInitializerError(var4);
      }

      try {
         CONFIG_OBJECT_CONSTRUCTOR = objectClass.getDeclaredConstructor(ConfigOrigin.class, Map.class);
         CONFIG_OBJECT_CONSTRUCTOR.setAccessible(true);
         CONFIG_LIST_CONSTRUCTOR = listClass.getDeclaredConstructor(ConfigOrigin.class, List.class);
         CONFIG_LIST_CONSTRUCTOR.setAccessible(true);
      } catch (NoSuchMethodException var3) {
         throw new ExceptionInInitializerError(var3);
      }
   }

   public static class Builder extends AbstractConfigurationLoader.Builder<HoconConfigurationLoader.Builder> {
      private ConfigRenderOptions render = ConfigRenderOptions.defaults().setOriginComments(false).setJson(false);
      private ConfigParseOptions parse = ConfigParseOptions.defaults();

      protected Builder() {
      }

      public ConfigRenderOptions getRenderOptions() {
         return this.render;
      }

      public ConfigParseOptions getParseOptions() {
         return this.parse;
      }

      public HoconConfigurationLoader.Builder setRenderOptions(ConfigRenderOptions options) {
         this.render = options;
         return this;
      }

      public HoconConfigurationLoader.Builder setParseOptions(ConfigParseOptions options) {
         this.parse = options;
         return this;
      }

      public HoconConfigurationLoader build() {
         return new HoconConfigurationLoader(this);
      }
   }
}
