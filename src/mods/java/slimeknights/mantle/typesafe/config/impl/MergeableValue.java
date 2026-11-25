package slimeknights.mantle.typesafe.config.impl;

import slimeknights.mantle.typesafe.config.ConfigMergeable;
import slimeknights.mantle.typesafe.config.ConfigValue;

interface MergeableValue extends ConfigMergeable {
   ConfigValue toFallbackValue();
}
