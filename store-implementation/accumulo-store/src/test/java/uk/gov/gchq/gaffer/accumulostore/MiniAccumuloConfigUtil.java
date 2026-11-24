/*
 * Copyright 2026 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.gchq.gaffer.accumulostore;

import org.apache.accumulo.minicluster.MiniAccumuloConfig;
import org.apache.accumulo.miniclusterImpl.MiniAccumuloConfigImpl;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilities for configuring a {@link MiniAccumuloConfig}.
 */
public final class MiniAccumuloConfigUtil {

    private MiniAccumuloConfigUtil() {
        // Private constructor to prevent instantiation.
    }

    /**
     * Makes the MiniAccumuloCluster ZooKeeper compatible with ZooKeeper 3.5+. Accumulo 2.0
     * relies on the "ruok" four letter word, which is now disabled by default, and the
     * ZooKeeper AdminServer would otherwise bind to port 8080.
     *
     * @param config the config to update
     * @return the updated config
     */
    public static MiniAccumuloConfig configureZooKeeper(final MiniAccumuloConfig config) {
        try {
            final Method getImpl = MiniAccumuloConfig.class.getDeclaredMethod("getImpl");
            getImpl.setAccessible(true);
            final MiniAccumuloConfigImpl impl = (MiniAccumuloConfigImpl) getImpl.invoke(config);
            final Map<String, String> systemProperties = new HashMap<>(impl.getSystemProperties());
            systemProperties.put("zookeeper.4lw.commands.whitelist", "*");
            systemProperties.put("zookeeper.admin.enableServer", "false");
            impl.setSystemProperties(systemProperties);
            return config;
        } catch (final ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to configure MiniAccumuloCluster ZooKeeper", e);
        }
    }
}
