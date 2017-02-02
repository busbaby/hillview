package org.hiero.sketch.table;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.hiero.sketch.table.api.ContentsKind;

import java.util.ArrayList;

/**
 * A column of doubles that can grow in size.
 */
public class DoubleListColumn
        extends BaseListColumn implements IDoubleColumn {
    @NonNull
    private final ArrayList<double[]> segments;

    public DoubleListColumn(@NonNull final ColumnDescription desc) {
        super(desc);
        if (desc.kind != ContentsKind.Double)
            throw new IllegalArgumentException("Unexpected column kind " + desc.kind);
        this.segments = new ArrayList<double []>();
    }

    @Override
    public double getDouble(final int rowIndex) {
        final int segmentId = rowIndex >> this.LogSegmentSize;
        final int localIndex = rowIndex & this.SegmentMask;
        return this.segments.get(segmentId)[localIndex];
    }

    public void append(final double value) {
        final int segmentId = this.size >> this.LogSegmentSize;
        final int localIndex = this.size & this.SegmentMask;
        if (this.segments.size() <= segmentId) {
            this.segments.add(new double[this.SegmentSize]);
            this.growMissing();
        }
        this.segments.get(segmentId)[localIndex] = value;
        this.size++;
    }
}