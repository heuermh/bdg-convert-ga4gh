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
 * Unit test for BdgenomicsFeatureToGa4ghFeature.
 */
public final class BdgenomicsFeatureToGa4ghFeatureTest {
    private final Logger logger = LoggerFactory.getLogger(BdgenomicsFeatureToGa4ghFeatureTest.class);
    private Converter<String, ga4gh.Common.OntologyTerm> featureTypeConverter;
    private Converter<org.bdgenomics.formats.avro.Strand, ga4gh.Common.Strand> strandConverter;
    private Converter<org.bdgenomics.formats.avro.Feature, ga4gh.SequenceAnnotations.Feature> featureConverter;

    @Before
    public void setUp() {
        featureTypeConverter = new StringToOntologyTerm();
        strandConverter = new BdgenomicsStrandToGa4ghStrand();
        featureConverter = new BdgenomicsFeatureToGa4ghFeature(featureTypeConverter, strandConverter);
    }

    @Test
    public void testConstructor() {
        assertNotNull(featureConverter);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullFeatureTypeConverter() {
        new BdgenomicsFeatureToGa4ghFeature(null, strandConverter);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullStrandConverter() {
        new BdgenomicsFeatureToGa4ghFeature(featureTypeConverter, null);
    }

    @Test(expected=ConversionException.class)
    public void testConvertNullStrict() {
        featureConverter.convert(null, ConversionStringency.STRICT, logger);
    }

    @Test
    public void testConvertNullLenient() {
        assertNull(featureConverter.convert(null, ConversionStringency.LENIENT, logger));
    }

    @Test
    public void testConvertNullSilent() {
        assertNull(featureConverter.convert(null, ConversionStringency.SILENT, logger));
    }

    @Test
    public void testConvert() {
        ga4gh.SequenceAnnotations.Feature expected = ga4gh.SequenceAnnotations.Feature.newBuilder()
            .setReferenceName("1")
            .setStart(0L)
            .setEnd(42L)
            .setStrand(ga4gh.Common.Strand.POS_STRAND)
            .setFeatureType(ga4gh.Common.OntologyTerm.newBuilder().setTermId("exon").build())
            .build();

        org.bdgenomics.formats.avro.Feature feature = org.bdgenomics.formats.avro.Feature.newBuilder()
            .setContigName("1")
            .setStart(0L)
            .setEnd(42L)
            .setStrand(org.bdgenomics.formats.avro.Strand.FORWARD)
            .setFeatureType("exon")
            .build();

        assertEquals(expected, featureConverter.convert(feature, ConversionStringency.STRICT, logger));
    }
}
