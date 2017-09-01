/**
 * Licensed to Big Data Genomics (BDG) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The BDG licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bdgenomics.convert.ga4gh;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import ga4gh.Reads.CigarUnit;
import ga4gh.Reads.CigarUnit.Operation;

import htsjdk.samtools.Cigar;
import htsjdk.samtools.CigarOperator;
import htsjdk.samtools.TextCigarCodec;

import org.bdgenomics.convert.ConversionException;
import org.bdgenomics.convert.ConversionStringency;
import org.bdgenomics.convert.Converter;

import org.junit.Before;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test for CigarToCigarUnits.
 */
public final class CigarToCigarUnitsTest {
    private final Logger logger = LoggerFactory.getLogger(CigarToCigarUnitsTest.class);
    private Converter<CigarOperator, Operation> operatorConverter;
    private Converter<Cigar, List<CigarUnit>> cigarConverter;

    @Before
    public void setUp() {
        operatorConverter = new CigarOperatorToOperation();
        cigarConverter = new CigarToCigarUnits(operatorConverter);
    }

    @Test
    public void testConstructor() {
        assertNotNull(cigarConverter);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullOperatorConverter() {
        new CigarToCigarUnits(null);
    }

    @Test(expected=ConversionException.class)
    public void testConvertNullStrict() {
        cigarConverter.convert(null, ConversionStringency.STRICT, logger);
    }

    @Test
    public void testConvertNullLenient() {
        assertNull(cigarConverter.convert(null, ConversionStringency.LENIENT, logger));
    }

    @Test
    public void testConvertNullSilent() {
        assertNull(cigarConverter.convert(null, ConversionStringency.SILENT, logger));
    }

    @Test
    public void testEmpty() {
        Cigar empty = new Cigar();
        List<CigarUnit> cigarUnits = cigarConverter.convert(empty, ConversionStringency.STRICT, logger);
        assertNotNull(cigarUnits);
        assertTrue(cigarUnits.isEmpty());
    }

    @Test
    public void testConvert() {
        Cigar cigar = TextCigarCodec.decode("10M1I10H");

        CigarUnit match = CigarUnit.newBuilder()
            .setOperationLength(10)
            .setOperation(Operation.ALIGNMENT_MATCH)
            .build();
        CigarUnit insert = CigarUnit.newBuilder()
            .setOperationLength(1)
            .setOperation(Operation.INSERT)
            .build();
        CigarUnit clip = CigarUnit.newBuilder()
            .setOperationLength(10)
            .setOperation(Operation.CLIP_HARD)
            .build();
        List<CigarUnit> expected = Arrays.asList(match, insert, clip);

        assertEquals(expected, cigarConverter.convert(cigar, ConversionStringency.STRICT, logger));
    }
}
