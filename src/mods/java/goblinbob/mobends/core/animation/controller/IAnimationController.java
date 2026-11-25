package goblinbob.mobends.core.animation.controller;

import goblinbob.mobends.core.data.EntityData;
import java.util.Collection;
import javax.annotation.Nullable;

public interface IAnimationController<T extends EntityData<?>> {
   @Nullable
   Collection<String> perform(T var1);
}
