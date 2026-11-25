package goblinbob.mobends.core.kumo.state;

import goblinbob.mobends.core.kumo.state.template.DriverLayerTemplate;
import goblinbob.mobends.core.kumo.state.template.keyframe.KeyframeLayerTemplate;
import java.lang.reflect.Type;

public enum LayerType {
   KEYFRAME(KeyframeLayerTemplate.class),
   DRIVER(DriverLayerTemplate.class);

   private Type templateType;

   private LayerType(Type templateType) {
      this.templateType = templateType;
   }

   public Type getTemplateType() {
      return this.templateType;
   }
}
