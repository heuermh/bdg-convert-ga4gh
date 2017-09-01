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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.protobuf.util.JsonFormat;

import ga4gh.Common.Strand;

import ga4gh.Reads.CigarUnit;
import ga4gh.Reads.CigarUnit.Operation;
import ga4gh.Reads.ReadAlignment;

import ga4gh.ReadServiceOuterClass.SearchReadsResponse;

import htsjdk.samtools.Cigar;
import htsjdk.samtools.CigarOperator;

import org.bdgenomics.convert.ConversionException;
import org.bdgenomics.convert.ConversionStringency;
import org.bdgenomics.convert.Converter;

import org.bdgenomics.formats.avro.AlignmentRecord;

import org.junit.Before;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test for AlignmentRecordToReadAlignment.
 */
public final class AlignmentRecordToReadAlignmentTest {
    private final Logger logger = LoggerFactory.getLogger(AlignmentRecordToReadAlignmentTest.class);
    private Converter<CigarOperator, Operation> operatorConverter;
    private Converter<Cigar, List<CigarUnit>> cigarConverter;
    private Converter<AlignmentRecord, ReadAlignment> alignmentConverter;

    private AlignmentRecord.Builder alignmentBuilder;

    @Before
    public void setUp() {
        operatorConverter = new CigarOperatorToOperation();
        cigarConverter = new CigarToCigarUnits(operatorConverter);
        alignmentConverter = new AlignmentRecordToReadAlignment(cigarConverter);

        alignmentBuilder = AlignmentRecord.newBuilder()
            .setReadName("read0")
            .setStart(10L)
            .setReadMapped(true)
            .setCigar("10M")
            .setSequence("AAAAAAAAAA")
            .setQual("**********")
            .setReadNegativeStrand(false)
            .setMapq(60)
            .setMismatchingPositions("10")
            .setOldPosition(12L)
            .setOldCigar("2^AAA3")
            .setRecordGroupName("rg1")
            .setContigName("myCtg")
            .setProperPair(true)
            .setDuplicateRead(false)
            .setFailedVendorQualityChecks(false)
            .setSecondaryAlignment(false)
            .setSupplementaryAlignment(false)
            .setMateContigName("myCtg")
            .setMateNegativeStrand(false)
            .setMateAlignmentStart(100L)
            .setMateMapped(true)
            .setReadPaired(true)
            .setInferredInsertSize(200L);
    }

    @Test
    public void testConstructor() {
        assertNotNull(alignmentConverter);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullCigarConverter() {
        new AlignmentRecordToReadAlignment(null);
    }

    @Test(expected=ConversionException.class)
    public void testConvertNullStrict() {
        alignmentConverter.convert(null, ConversionStringency.STRICT, logger);
    }

    @Test
    public void testConvertNullLenient() {
        assertNull(alignmentConverter.convert(null, ConversionStringency.LENIENT, logger));
    }

    @Test
    public void testConvertNullSilent() {
        assertNull(alignmentConverter.convert(null, ConversionStringency.SILENT, logger));
    }

    @Test
    public void testConvert() {
        AlignmentRecord alignment = alignmentBuilder.build();
        ReadAlignment readAlignment = alignmentConverter.convert(alignment, ConversionStringency.STRICT, logger);
        assertEquals(10L, readAlignment.getAlignment().getPosition().getPosition());
        assertEquals( "myCtg", readAlignment.getAlignment().getPosition().getReferenceName());
        assertEquals(Strand.POS_STRAND, readAlignment.getAlignment().getPosition().getStrand());
        assertEquals("rg1", readAlignment.getReadGroupId());
        assertEquals("read0", readAlignment.getFragmentName());
        assertEquals(false, readAlignment.getImproperPlacement());
        assertEquals(false, readAlignment.getDuplicateFragment());
        assertEquals(false,readAlignment.getFailedVendorQualityChecks());
        assertEquals(false,readAlignment.getSecondaryAlignment());
        assertEquals(false, readAlignment.getSupplementaryAlignment());
        assertEquals("myCtg", readAlignment.getNextMatePosition().getReferenceName());
        assertEquals(2, readAlignment.getNumberReads());
        assertEquals(200L, readAlignment.getFragmentLength());
        assertEquals("AAAAAAAAAA", readAlignment.getAlignedSequence());
        assertEquals(Arrays.asList(9, 9, 9, 9, 9, 9, 9, 9, 9, 9), readAlignment.getAlignedQualityList());
    }

    @Test
    public void testConvertImproperPlacement() {
        AlignmentRecord improperPlacement = alignmentBuilder.setProperPair(false).build();
        ReadAlignment readAlignment = alignmentConverter.convert(improperPlacement, ConversionStringency.STRICT, logger);
        assertTrue(readAlignment.getImproperPlacement());
    }

    @Test
    public void testConvertUnpairedRead() {
        AlignmentRecord unpairedRead = alignmentBuilder
            .setReadPaired(false)
            .clearMateContigName()
            .clearMateAlignmentStart()
            .setMateMapped(false)
            .build();
        ReadAlignment readAlignment = alignmentConverter.convert(unpairedRead, ConversionStringency.STRICT, logger);
        assertEquals(1, readAlignment.getNumberReads());
        // unexpected, position is not null, and reference name is empty
        assertEquals("", readAlignment.getNextMatePosition().getReferenceName());
    }

    @Test
    public void testConvertUnalignedRead() {
        AlignmentRecord unalignedRead = alignmentBuilder
            .setReadMapped(false)
            .clearContigName()
            .clearStart()
            .clearCigar()
            .build();
        ReadAlignment readAlignment = alignmentConverter.convert(unalignedRead, ConversionStringency.STRICT, logger);
        assertEquals(0, readAlignment.getAlignment().getCigarCount());
        // unexpected, alignment and position are not null, and reference name is empty
        assertEquals("", readAlignment.getAlignment().getPosition().getReferenceName());
    }

    @Test
    public void testConvertMissingRecordGroup() {
        AlignmentRecord missingRecordGroup = alignmentBuilder.clearRecordGroupName().build();
        ReadAlignment readAlignment = alignmentConverter.convert(missingRecordGroup, ConversionStringency.STRICT, logger);
        assertEquals("1", readAlignment.getReadGroupId());
    }

    @Test
    public void testConvertNullInferredInsertSize() {
        AlignmentRecord nullInferredInsertSize = alignmentBuilder.clearInferredInsertSize().build();
        ReadAlignment readAlignment = alignmentConverter.convert(nullInferredInsertSize, ConversionStringency.STRICT, logger);
        assertEquals(0, readAlignment.getFragmentLength());
    }

    @Test
    public void testConvertMateNegativeStrand() {
        AlignmentRecord mateNegativeStrand = alignmentBuilder.setMateNegativeStrand(true).build();
        ReadAlignment readAlignment = alignmentConverter.convert(mateNegativeStrand, ConversionStringency.STRICT, logger);
        assertEquals(Strand.NEG_STRAND, readAlignment.getNextMatePosition().getStrand());
    }

    @Test
    public void testConvertEmptyQual() {
        AlignmentRecord emptyQual = alignmentBuilder.clearQual().build();
        ReadAlignment readAlignment = alignmentConverter.convert(emptyQual, ConversionStringency.STRICT, logger);
        assertNotNull(readAlignment.getAlignedQualityList());
        assertTrue(readAlignment.getAlignedQualityList().isEmpty());
    }

    @Test
    public void testConvertMappedNegativeStrand() {
        AlignmentRecord mappedNegativeStrand = alignmentBuilder.setReadNegativeStrand(true).build();
        ReadAlignment readAlignment = alignmentConverter.convert(mappedNegativeStrand, ConversionStringency.STRICT, logger);
        assertEquals(Strand.NEG_STRAND, readAlignment.getAlignment().getPosition().getStrand());
    }

    @Test(expected=ConversionException.class)
    public void testConvertIllegalCigarStrict() {
        AlignmentRecord illegalCigar = alignmentBuilder.setCigar("10").build();
        alignmentConverter.convert(illegalCigar, ConversionStringency.STRICT, logger);
    }

    @Test
    public void testConvertIllegalCigarLenient() {
        AlignmentRecord illegalCigar = alignmentBuilder.setCigar("10").build();
        ReadAlignment readAlignment = alignmentConverter.convert(illegalCigar, ConversionStringency.LENIENT, logger);
        assertEquals(0, readAlignment.getAlignment().getCigarCount());
        assertNotNull(readAlignment.getAlignment().getCigarList());
        assertTrue(readAlignment.getAlignment().getCigarList().isEmpty());
    }

    @Test
    public void testConvertIllegalCigarSilent() {
        AlignmentRecord illegalCigar = alignmentBuilder.setCigar("10").build();
        ReadAlignment readAlignment = alignmentConverter.convert(illegalCigar, ConversionStringency.SILENT, logger);
        assertEquals(0, readAlignment.getAlignment().getCigarCount());
        assertNotNull(readAlignment.getAlignment().getCigarList());
        assertTrue(readAlignment.getAlignment().getCigarList().isEmpty());
    }

    @Test
    public void testJson() throws Exception {
        AlignmentRecord alignment = alignmentBuilder.build();
        ReadAlignment readAlignment = alignmentConverter.convert(alignment, ConversionStringency.STRICT, logger);

        SearchReadsResponse response = SearchReadsResponse.newBuilder()
            .addAllAlignments(Arrays.asList(readAlignment))
            .build();

        String json = JsonFormat.printer().print(response).replaceAll("\\s+","");
        assertEquals("{\"alignments\":[{\"readGroupId\":\"rg1\",\"fragmentName\":\"read0\",\"numberReads\":2,\"fragmentLength\":200,\"alignment\":{\"position\":{\"referenceName\":\"myCtg\",\"position\":\"10\",\"strand\":\"POS_STRAND\"},\"mappingQuality\":60,\"cigar\":[{\"operation\":\"ALIGNMENT_MATCH\",\"operationLength\":\"10\"}]},\"alignedSequence\":\"AAAAAAAAAA\",\"alignedQuality\":[9,9,9,9,9,9,9,9,9,9],\"nextMatePosition\":{\"referenceName\":\"myCtg\",\"position\":\"100\",\"strand\":\"POS_STRAND\"}}]}", json);
    }
}
