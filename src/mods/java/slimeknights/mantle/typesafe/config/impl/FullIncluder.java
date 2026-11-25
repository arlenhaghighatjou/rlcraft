package slimeknights.mantle.typesafe.config.impl;

import slimeknights.mantle.typesafe.config.ConfigIncluder;
import slimeknights.mantle.typesafe.config.ConfigIncluderClasspath;
import slimeknights.mantle.typesafe.config.ConfigIncluderFile;
import slimeknights.mantle.typesafe.config.ConfigIncluderURL;

interface FullIncluder extends ConfigIncluder, ConfigIncluderFile, ConfigIncluderURL, ConfigIncluderClasspath {
}
