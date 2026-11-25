package goblinbob.mobends.core.kumo.state;

import goblinbob.mobends.core.animation.keyframe.KeyframeAnimation;
import goblinbob.mobends.core.kumo.state.template.MalformedKumoTemplateException;
import goblinbob.mobends.core.kumo.state.template.keyframe.KeyframeNodeTemplate;
import java.util.List;

public interface INodeState {
   Iterable<ConnectionState> getConnections();

   KeyframeAnimation getAnimation();

   float getProgress();

   boolean isAnimationFinished();

   void parseConnections(List<INodeState> var1, KeyframeNodeTemplate var2) throws MalformedKumoTemplateException;

   void start(IKumoContext var1);

   void update(IKumoContext var1, float var2);
}
