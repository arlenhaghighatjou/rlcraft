package goblinbob.mobends.core.kumo.state.template;

import goblinbob.mobends.core.kumo.state.IKumoValidationContext;
import goblinbob.mobends.core.kumo.state.LayerType;

public class LayerTemplate {
   private LayerType type;

   public LayerTemplate() {
   }

   public LayerType getLayerType() {
      return this.type;
   }

   public void validate(IKumoValidationContext context) throws MalformedKumoTemplateException {
   }
}
