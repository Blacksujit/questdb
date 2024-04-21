/*******************************************************************************
 *     ___                  _   ____  ____
 *    / _ \ _   _  ___  ___| |_|  _ \| __ )
 *   | | | | | | |/ _ \/ __| __| | | |  _ \
 *   | |_| | |_| |  __/\__ \ |_| |_| | |_) |
 *    \__\_\\__,_|\___||___/\__|____/|____/
 *
 *  Copyright (c) 2014-2019 Appsicle
 *  Copyright (c) 2019-2024 QuestDB
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 ******************************************************************************/

package io.questdb.griffin.engine.functions.groupby;

import io.questdb.cairo.ArrayColumnTypes;
import io.questdb.cairo.ColumnType;
import io.questdb.cairo.map.MapValue;
import io.questdb.cairo.sql.Function;
import io.questdb.cairo.sql.Record;
import io.questdb.griffin.engine.functions.GroupByFunction;
import io.questdb.griffin.engine.functions.LongFunction;
import io.questdb.griffin.engine.functions.UnaryFunction;
import io.questdb.std.Numbers;
import org.jetbrains.annotations.NotNull;

public class FirstLongGroupByFunction extends LongFunction implements GroupByFunction, UnaryFunction {
    protected final Function arg;
    protected int valueIndex;

    public FirstLongGroupByFunction(@NotNull Function arg) {
        this.arg = arg;
    }

    @Override
    public void computeFirst(MapValue mapValue, Record record, long rowId) {
        mapValue.putLong(valueIndex, rowId);
        mapValue.putLong(valueIndex + 1, arg.getLong(record));
    }

    @Override
    public void computeNext(MapValue mapValue, Record record, long rowId) {
        // empty
    }

    @Override
    public Function getArg() {
        return this.arg;
    }

    @Override
    public long getLong(Record rec) {
        return rec.getLong(valueIndex + 1);
    }

    @Override
    public String getName() {
        return "first";
    }

    @Override
    public int getValueIndex() {
        return valueIndex;
    }

    @Override
    public void initValueIndex(int valueIndex) {
        this.valueIndex = valueIndex;
    }

    @Override
    public void initValueTypes(ArrayColumnTypes columnTypes) {
        this.valueIndex = columnTypes.getColumnCount();
        columnTypes.add(ColumnType.LONG); // row id
        columnTypes.add(ColumnType.LONG); // value
    }

    @Override
    public boolean isReadThreadSafe() {
        return UnaryFunction.super.isReadThreadSafe();
    }

    @Override
    public void merge(MapValue destValue, MapValue srcValue) {
        long srcRowId = srcValue.getLong(valueIndex);
        long destRowId = destValue.getLong(valueIndex);
        if (srcRowId != Numbers.LONG_NULL && (srcRowId < destRowId || destRowId == Numbers.LONG_NULL)) {
            destValue.putLong(valueIndex, srcRowId);
            destValue.putLong(valueIndex + 1, srcValue.getLong(valueIndex + 1));
        }
    }

    @Override
    public void setLong(MapValue mapValue, long value) {
        // This method is used to define interpolated points and to init
        // an empty value, so it's ok to reset the row id field here.
        mapValue.putLong(valueIndex, Numbers.LONG_NULL);
        mapValue.putLong(valueIndex + 1, value);
    }

    @Override
    public void setNull(MapValue mapValue) {
        setLong(mapValue, Numbers.LONG_NULL);
    }

    @Override
    public boolean supportsParallelism() {
        return UnaryFunction.super.supportsParallelism();
    }
}
