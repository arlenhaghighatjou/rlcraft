package goblinbob.mobends.core.kumo.state;

import goblinbob.mobends.core.Core;
import goblinbob.mobends.core.kumo.state.keyframe.KeyframeLayerState;
import goblinbob.mobends.core.kumo.state.template.DriverLayerTemplate;
import goblinbob.mobends.core.kumo.state.template.LayerTemplate;
import goblinbob.mobends.core.kumo.state.template.MalformedKumoTemplateException;
import goblinbob.mobends.core.kumo.state.template.keyframe.KeyframeLayerTemplate;

public interface ILayerState {
   void start(IKumoContext var1);

   void update(IKumoContext var1, float var2) throws MalformedKumoTemplateException;

   static ILayerState createFromTemplate(IKumoInstancingContext context, LayerTemplate template) throws MalformedKumoTemplateException {
      switch(template.getLayerType()) {
      case KEYFRAME:
         return KeyframeLayerState.createFromTemplate(context, (KeyframeLayerTemplate)template);
      case DRIVER:
         return new DriverLayerState((DriverLayerTemplate)template);
      default:
         Core.LOG.warning(String.format("Unknown layer type was specified in state template: %d", template.getLayerType().ordinal()));
         return null;
      }
   }
}
