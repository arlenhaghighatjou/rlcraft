package slimeknights.mantle.typesafe.config.impl;

import slimeknights.mantle.typesafe.config.ConfigValue;

interface Container extends ConfigValue {
   AbstractConfigValue replaceChild(AbstractConfigValue var1, AbstractConfigValue var2);

   boolean hasDescendant(AbstractConfigValue var1);
}
