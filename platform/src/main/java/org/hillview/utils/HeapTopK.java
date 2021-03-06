/*
 * Copyright (c) 2017 VMware Inc. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
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

package org.hillview.utils;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntRBTreeMap;
import it.unimi.dsi.fastutil.objects.Object2IntSortedMap;

import javax.annotation.Nullable;
import java.util.*;
/**
 * HeapTopK implements the TopK Interface as a HashMap, and only sorts it when asked to return topK.
 * Seems to be slower that the TreeMap implementation
 * Possible reason: Membership and max finding are slower.
 */
public class HeapTopK<T> implements ITopK<T> {
    private final int maxSize;
    private int size;
    private final Object2IntOpenHashMap<T> data;
    @Nullable
    private T cutoff; /* max value that currently belongs to Top K. */
    private final Comparator<T> greater;

    public HeapTopK(final int maxSize, final Comparator<T> greater) {
        if (maxSize > 0)
            this.maxSize = maxSize;
        else
            throw new IllegalArgumentException("Size should be positive");
        this.size = 0;
        this.greater = greater;
        this.data = new Object2IntOpenHashMap<T>();
    }

    @Override
    public Object2IntSortedMap<T> getTopK() {
        final Object2IntSortedMap<T> finalMap = new Object2IntRBTreeMap<>(this.greater);
        finalMap.putAll(this.data);
        return finalMap;
    }

    @Override
    public void push(final T newVal) {
        if (this.size == 0) {
            this.size += 1;
            this.data.put(newVal, 1); // Add newVal to Top K
            this.cutoff = newVal;
            return;
        }
        final int gt = this.greater.compare(newVal, this.cutoff);
        if (gt <= 0) {
            if (this.data.containsKey(newVal)) { //Already in Top K, increase count
                final int count = this.data.getInt(newVal) + 1;
                this.data.put(newVal, count);
            } else { // Add a new key to Top K
                this.data.put(newVal, 1);
                if (this.size >= this.maxSize) { // Remove the largest key, compute the new largest key
                    this.data.removeInt(this.cutoff);
                    this.cutoff = Collections.max(this.data.keySet(), this.greater);
                } else
                    this.size += 1;
            }
        }
        else {   //gt equals 1
            if (this.size < this.maxSize) { // Only case where newVal needs to be added
                this.size += 1;
                this.data.put(newVal, 1); // Add newVal to Top K
            }
        }
    }
}