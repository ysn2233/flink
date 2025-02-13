/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.streaming.api.graph;

import org.apache.flink.runtime.jobgraph.JobVertexID;

import java.util.Map;

/** Interface for different implementations of generating hashes over a stream graph. */
public interface StreamGraphHasher {

    /**
     * Returns a map with a hash for each {@link StreamNode} of the {@link StreamGraph}. The hash is
     * used as the {@link JobVertexID} in order to identify nodes across job submissions if they
     * didn't change.
     */
    Map<Integer, byte[]> traverseStreamGraphAndGenerateHashes(StreamGraph streamGraph);

    /**
     * Generates a hash for {@link StreamNode} with the specified stream node id in the {@link
     * StreamGraph}. This hash is stored in the provided map and can be used to uniquely identify
     * the {@link StreamNode} across job submissions, assuming its configuration remains unchanged.
     */
    boolean generateHashesByStreamNodeId(
            int streamNodeId, StreamGraph streamGraph, Map<Integer, byte[]> hashes);
}
