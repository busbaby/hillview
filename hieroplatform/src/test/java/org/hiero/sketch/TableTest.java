package org.hiero.sketch;

import org.hiero.sketch.table.*;
import org.hiero.sketch.table.api.IColumn;
import org.hiero.sketch.table.api.IMembershipSet;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hiero.sketch.DoubleArrayTest.generateDoubleArray;
import static org.hiero.sketch.IntArrayTest.generateIntArray;
import static org.hiero.sketch.IntArrayTest.getRandIntArray;
import static org.junit.Assert.assertNotNull;

public class TableTest {

    public static List<Table> SplitTable(Table bigTable, int fragmentSize) {
        int tableSize = bigTable.members.getSize();
        int numTables = (tableSize / fragmentSize) + 1;
        List<Table> tableList = new ArrayList<Table>(numTables);
        int start = 0;
        while (start < tableSize){
            int thisFragSize = Math.min(fragmentSize, tableSize - start);
            IMembershipSet members = new SparseMembership(start, thisFragSize);
            tableList.add(bigTable.compress(members));
            start += fragmentSize;
        }
        return tableList;
    }

    public static Table getIntTable(final int size, final int numCols) {
        final IColumn[] columns = new IColumn[numCols];
        double exp = 1.0/numCols;
        final int range =  5*((int)Math.pow(size, exp));
        for (int i = 0; i < numCols; i++) {
            final String colName = "Column" + String.valueOf(i);
            columns[i] = getRandIntArray(size, range, colName);
        }
        final Schema mySchema = new Schema();
        for (int i = 0; i < numCols; i++) {
            mySchema.append(columns[i].getDescription());
        }
        final FullMembership full = new FullMembership(size);
        return new Table(mySchema, columns, full);
    }

    public static Table getRepIntTable(final int size, final int numCols) {
        final IColumn[] columns = new IColumn[numCols];
        double exp = 0.8/numCols;
        final int range =  ((int)Math.pow(size, exp));
        for (int i = 0; i < numCols; i++) {
            final String colName = "Column" + String.valueOf(i);
            columns[i] = getRandIntArray(size, range, colName);
        }
        final Schema mySchema = new Schema();
        for (int i = 0; i < numCols; i++) {
            mySchema.append(columns[i].getDescription());
        }
        final FullMembership full = new FullMembership(size);
        return new Table(mySchema, columns, full);
    }

    @Test
    public void getTableTest(){
        final Table leftTable = getIntTable(100, 2);
        //System.out.print(leftTable.toLongString());
    }

    @Test
    public void columnCompressTest() {
        final int size = 100;
        final IntArrayColumn col = generateIntArray(size);
        final FullMembership FM = new FullMembership(size);
        final LazyMembership PMD = new LazyMembership(FM, row -> (row % 2) == 0);
        final IColumn smallCol = col.compress(PMD);
        assertNotNull(smallCol);
    }

    @Test
    public void tableTest0() {
        final int size = 100;
        final int numCols =2;
        final IColumn[] columns = new IColumn[numCols];
        columns[0] = generateIntArray(size);
        columns[1] = generateDoubleArray(size);
        final Schema mySchema = new Schema();
        for (int i=0; i< numCols; i++) {
            mySchema.append(columns[i].getDescription());
        }
        final FullMembership full = new FullMembership(size);
        final LazyMembership partial = new LazyMembership(full, row -> (row % 2) == 0);
        final Table myTable = new Table(mySchema, columns, partial);
        //assertEquals(myTable.toString(), "Table, 2 columns, 50 rows");
        final Table smallTable = myTable.compress();
        //assertEquals(smallTable.toString(), "Table, 2 columns, 50 rows");
    }

    @Test
    public void tableTest1() {
        final int size = 100;
        final int numCols =2;
        final IColumn[] columns = new IColumn[numCols];
        columns[0] = generateIntArray(size);
        columns[1] = generateDoubleArray(size);
        final Schema mySchema = new Schema();
        for (int i=0; i< numCols; i++) {
            mySchema.append(columns[i].getDescription());
        }
        final FullMembership full = new FullMembership(size);
        final LazyMembership partial = new
                LazyMembership(full, row -> (row % 2) == 0);
        final Table myTable = new Table(mySchema, columns, partial);
        //assertEquals(myTable.toString(), "Table, 2 columns, 50 rows");
        final HashSubSchema filter = new HashSubSchema();
        filter.add(columns[1].getDescription().name);
        final Table smallTable = myTable.compress(filter, partial);
        //assertEquals(smallTable.toString(), "Table, 1 columns, 50 rows");
    }
}