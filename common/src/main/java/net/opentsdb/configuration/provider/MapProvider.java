// This file is part of OpenTSDB.
// Copyright (C) 2018  The OpenTSDB Authors.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package net.opentsdb.configuration.provider;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import net.opentsdb.configuration.Configuration;
import net.opentsdb.configuration.ConfigurationOverride;

/**
 * Simple provider that bootstraps programatically from a properties
 * object or hash map.
 * 
 * @since 3.0
 */
public class MapProvider implements Provider {
  private static final Logger LOG = LoggerFactory.getLogger(MapProvider.class);
  
  public static final String SOURCE = MapProvider.class.getSimpleName();
  
  /** The map we copy into. */
  private final Map<String, String> properties;
  
  /**
   * Ctor copying from a map.
   * @param properties A non-null map object.
   */
  public MapProvider(final Map<String, String> properties) {
    this.properties = Maps.newHashMap(properties);
  }
  
  /**
   * Ctor copying from a Properties object.
   * @param properties A non-null properties object.
   */
  public MapProvider(final Properties properties) {
    this.properties = Maps.newHashMapWithExpectedSize(properties.size());
    for (final Object key : properties.keySet()) {
      if (!(key instanceof String)) {
        LOG.debug("Dropping non-string key: " + key);
        continue;
      }
      this.properties.put((String) key, properties.getProperty((String) key));
    }
  }
  
  @Override
  public void close() throws IOException {
    // no-op
  }

  @Override
  public void run(final Timeout arg0) throws Exception {
    // no-op
  }

  @Override
  public ConfigurationOverride getSetting(final String key) {
    final String value = properties.get(key);
    if (key == null) {
      return null;
    }
    return ConfigurationOverride.newBuilder()
        .setSource(SOURCE)
        .setValue(value)
        .build();
  }

  @Override
  public String source() {
    return SOURCE;
  }

  @Override
  public void reload() {
    // no-op
  }

  @Override
  public long lastReload() {
    return 0;
  }

  @Override
  public ProviderFactory factory() {
    return new MapProviderFactory();
  }

  private class MapProviderFactory implements ProviderFactory {

    @Override
    public void close() throws IOException {
      // no-op
    }

    @Override
    public Provider newInstance(final Configuration config, 
                                final HashedWheelTimer timer,
                                final Set<String> reload_keys) {
      return MapProvider.this;
    }

    @Override
    public boolean isReloadable() {
      return false;
    }

    @Override
    public String description() {
      return "A provider generated by passing a Properties object or "
          + "map to the Configuration constructor.";
    }

    @Override
    public String simpleName() {
      return SOURCE;
    }
    
  }
}
