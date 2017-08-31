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

import java.util.Arrays;
import java.util.List;

import ga4gh.Reads.CigarUnit.Operation;

import htsjdk.samtools.CigarOperator;

import org.bdgenomics.convert.ConversionException;
import org.bdgenomics.convert.ConversionStringency;
import org.bdgenomics.convert.Converter;

import org.junit.Before;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test for CigarOperatorToOperation.
 */
public final class CigarOperatorToOperationTest {
    private final Logger logger = LoggerFactory.getLogger(CigarOperatorToOperationTest.class);
    private Converter<CigarOperator, Operation> operatorConverter;

    @Before
    public void setUp() {
        operatorConverter = new CigarOperatorToOperation();
    }

    @Test
    public void testConstructor() {
        assertNotNull(operatorConverter);
    }

    @Test(expected=ConversionException.class)
    public void testConvertNullStrict() {
        operatorConverter.convert(null, ConversionStringency.STRICT, logger);
    }

    @Test
    public void testConvertNullLenient() {
        assertNull(operatorConverter.convert(null, ConversionStringency.LENIENT, logger));
    }

    @Test
    public void testConvertNullSilent() {
        assertNull(operatorConverter.convert(null, ConversionStringency.SILENT, logger));
    }

    @Test
    public void testConvert() {
        assertEquals(Operation.ALIGNMENT_MATCH, operatorConverter.convert(CigarOperator.M, ConversionStringency.STRICT, logger));
        assertEquals(Operation.INSERT, operatorConverter.convert(CigarOperator.I, ConversionStringency.STRICT, logger));
        assertEquals(Operation.DELETE, operatorConverter.convert(CigarOperator.D, ConversionStringency.STRICT, logger));
        assertEquals(Operation.SKIP, operatorConverter.convert(CigarOperator.N, ConversionStringency.STRICT, logger));
        assertEquals(Operation.CLIP_SOFT, operatorConverter.convert(CigarOperator.S, ConversionStringency.STRICT, logger));
        assertEquals(Operation.CLIP_HARD, operatorConverter.convert(CigarOperator.H, ConversionStringency.STRICT, logger));
        assertEquals(Operation.PAD, operatorConverter.convert(CigarOperator.P, ConversionStringency.STRICT, logger));
        assertEquals(Operation.SEQUENCE_MATCH, operatorConverter.convert(CigarOperator.EQ, ConversionStringency.STRICT, logger));
        assertEquals(Operation.SEQUENCE_MISMATCH, operatorConverter.convert(CigarOperator.X, ConversionStringency.STRICT, logger));
    }
}
