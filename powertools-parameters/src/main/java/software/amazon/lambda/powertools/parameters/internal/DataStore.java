/*
 * Copyright 2020 Amazon.com, Inc. or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package software.amazon.lambda.powertools.parameters.internal;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Internal store used to cache parameters
 */
public class DataStore {

    private final ConcurrentHashMap<String, ValueNode> store;

    public DataStore() {
        this.store = new ConcurrentHashMap<>();
    }

    static class ValueNode {
        public final Object value;
        public final long time;

        public ValueNode(Object value, long time){
            this.value = value;
            this.time = time;
        }
    }

    public void put(String key, Object value, long time){
        store.put(key, new ValueNode(value, time));
    }

    public void remove(String Key){
        store.remove(Key);
    }

    public void clear(){
        store.clear();
    }

    public Object get(String key) {
        return store.containsKey(key)?store.get(key).value:null;
    }

    public boolean hasNotExpired(String key) {
        boolean hasNotExpired = store.containsKey(key) && store.get(key).time >= Instant.now().toEpochMilli();
        // Auto-clean if the parameter has expired
        if (!hasNotExpired) {
            remove(key);
        }
        return hasNotExpired;
    }

}
