/*******************************************************************************
 *     ___                  _   ____  ____
 *    / _ \ _   _  ___  ___| |_|  _ \| __ )
 *   | | | | | | |/ _ \/ __| __| | | |  _ \
 *   | |_| | |_| |  __/\__ \ |_| |_| | |_) |
 *    \__\_\\__,_|\___||___/\__|____/|____/
 *
 *  Copyright (c) 2014-2019 Appsicle
 *  Copyright (c) 2019-2020 QuestDB
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

package io.questdb.griffin.engine.functions.eq;

import io.questdb.griffin.AbstractGriffinTest;
import io.questdb.griffin.SqlException;
import org.junit.Test;

public class EqLong256StrFunctionFactoryTest extends AbstractGriffinTest {
    @Test
    public void testLong256Decode1() throws Exception {
        assertQuery(
                "rnd_long256\n0x9f9b2131d49fcd1d6b8139815c50d3410010cde812ce60ee0010a928bb8b9650\n",
                "xxxx where rnd_long256='0x9f9b2131d49fcd1d6b8139815c50d3410010cde812ce60ee0010a928bb8b9650'",
                "create table xxxx as (select rnd_long256() from long_sequence(200));",
                null,
                true);
    }

    @Test
    public void testLong256Decode2() throws Exception {
        assertQuery(
                "rnd_long256\n0x9f9b2131d49fcd1d6b8139815c50d3410010cde812ce60ee0010a928bb8b9650\n",
                "xxxx where rnd_long256!='0x056'",
                "create table xxxx as (select rnd_long256() from long_sequence(1));",
                null,
                true);
    }

    @Test(expected = SqlException.class)
    public void testLong256GarbageDecode1() throws Exception {
        assertQuery(
                "rnd_long256\n0x9f9b2131d49fcd1d6b8139815c50d3410010cde812ce60ee0010a928bb8b9650\n",
                "xxxx where rnd_long256!='0xG56'",
                "create table xxxx as (select rnd_long256() from long_sequence(1));",
                null,
                true);
    }
}
