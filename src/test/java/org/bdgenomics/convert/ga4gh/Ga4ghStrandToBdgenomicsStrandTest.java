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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import org.bdgenomics.convert.Converter;
import org.bdgenomics.convert.ConversionException;
import org.bdgenomics.convert.ConversionStringency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test for Ga4ghStrandToBdgenomicsStrand.
 */
public final class Ga4ghStrandToBdgenomicsStrandTest {
    private final Logger logger = LoggerFactory.getLogger(Ga4ghStrandToBdgenomicsStrandTest.class);
    private Converter<ga4gh.Common.Strand, org.bdgenomics.formats.avro.Strand> strandConverter;

    @Before
    public void setUp() {
        strandConverter = new Ga4ghStrandToBdgenomicsStrand();
    }

    @Test
    public void testConstructor() {
        assertNotNull(strandConverter);
    }

    @Test(expected=ConversionException.class)
    public void testConvertNullStrict() {
        strandConverter.convert(null, ConversionStringency.STRICT, logger);
    }

    @Test
    public void testConvertNullLenient() {
        assertNull(strandConverter.convert(null, ConversionStringency.LENIENT, logger));
    }

    @Test
    public void testConvertNullSilent() {
        assertNull(strandConverter.convert(null, ConversionStringency.SILENT, logger));
    }

    @Test
    public void testConvert() {
        assertEquals(org.bdgenomics.formats.avro.Strand.FORWARD, strandConverter.convert(ga4gh.Common.Strand.POS_STRAND, ConversionStringency.STRICT, logger));
        assertEquals(org.bdgenomics.formats.avro.Strand.REVERSE, strandConverter.convert(ga4gh.Common.Strand.NEG_STRAND, ConversionStringency.STRICT, logger));
        assertEquals(org.bdgenomics.formats.avro.Strand.INDEPENDENT, strandConverter.convert(ga4gh.Common.Strand.UNRECOGNIZED, ConversionStringency.STRICT, logger));
        assertEquals(org.bdgenomics.formats.avro.Strand.UNKNOWN, strandConverter.convert(ga4gh.Common.Strand.STRAND_UNSPECIFIED, ConversionStringency.STRICT, logger));
    }
}
