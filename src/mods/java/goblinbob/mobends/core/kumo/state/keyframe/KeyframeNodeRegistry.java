package goblinbob.mobends.core.kumo.state.keyframe;

import goblinbob.mobends.core.kumo.state.IKumoInstancingContext;
import goblinbob.mobends.core.kumo.state.INodeState;
import goblinbob.mobends.core.kumo.state.template.MalformedKumoTemplateException;
import goblinbob.mobends.core.kumo.state.template.keyframe.KeyframeNodeTemplate;
import goblinbob.mobends.core.kumo.state.template.keyframe.MovementKeyframeNodeTemplate;
import goblinbob.mobends.core.kumo.state.template.keyframe.StandardKeyframeNodeTemplate;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public class KeyframeNodeRegistry {
   public static final KeyframeNodeRegistry INSTANCE = new KeyframeNodeRegistry();
   private Map<String, KeyframeNodeRegistry.RegistryEntry<?>> registry = new HashMap();

   private KeyframeNodeRegistry() {
      this.register("core:standard", StandardKeyframeNode::new, StandardKeyframeNodeTemplate.class);
      this.register("core:movement", MovementKeyframeNode::new, MovementKeyframeNodeTemplate.class);
   }

   public <T extends KeyframeNodeTemplate> void register(String key, IKeyframeNodeFactory<?, T> factory, Class<T> templateType) {
      this.registry.put(key, new KeyframeNodeRegistry.RegistryEntry(factory, templateType));
   }

   @Nullable
   public Type getTemplateClass(String key) {
      return this.registry.containsKey(key) ? ((KeyframeNodeRegistry.RegistryEntry)this.registry.get(key)).nodeType : null;
   }

   public <T extends KeyframeNodeTemplate> INodeState createFromTemplate(IKumoInstancingContext context, T template) throws MalformedKumoTemplateException {
      String type = template.getType();
      if (type == null) {
         throw new MalformedKumoTemplateException("No type was specified for KeyframeNode.");
      } else if (this.registry.containsKey(type)) {
         KeyframeNodeRegistry.RegistryEntry<T> entry = (KeyframeNodeRegistry.RegistryEntry)this.registry.get(type);
         return this.createFromTemplate(context, entry, template);
      } else {
         throw new MalformedKumoTemplateException(String.format("A non-existent KeyframeNode type was specified: %s", type));
      }
   }

   private <T extends KeyframeNodeTemplate> INodeState createFromTemplate(IKumoInstancingContext context, KeyframeNodeRegistry.RegistryEntry<T> entry, T template) throws MalformedKumoTemplateException {
      if (!entry.nodeType.equals(template.getClass())) {
         throw new MalformedKumoTemplateException(String.format("The KeyframeNode registry holds a wrong entry for '%s'", template.getType()));
      } else {
         return entry.factory.createKeyframeNode(context, template);
      }
   }

   private static class RegistryEntry<T extends KeyframeNodeTemplate> {
      public IKeyframeNodeFactory<?, T> factory;
      public Class<T> nodeType;

      RegistryEntry(IKeyframeNodeFactory<?, T> factory, Class<T> nodeType) {
         this.factory = factory;
         this.nodeType = nodeType;
      }
   }
}
