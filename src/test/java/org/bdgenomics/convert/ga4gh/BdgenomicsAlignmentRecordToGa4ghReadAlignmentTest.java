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

import ga4gh.Common;
import org.bdgenomics.formats.avro.AlignmentRecord;
import org.junit.Before;
import org.junit.Test;

import org.bdgenomics.convert.Converter;
import org.bdgenomics.convert.ConversionException;
import org.bdgenomics.convert.ConversionStringency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Unit test for BdgenomicsStrandToGa4ghStrand.
 */
public final class BdgenomicsAlignmentRecordToGa4ghReadAlignmentTest {
    private final Logger logger = LoggerFactory.getLogger(BdgenomicsAlignmentRecordToGa4ghReadAlignmentTest.class);
    private Converter<org.bdgenomics.formats.avro.AlignmentRecord, ga4gh.Reads.ReadAlignment> alignmentConverter;

    @Before
    public void setUp() {
        alignmentConverter = new BdgenomicsAlignmentRecordToGa4ghReadAlignment();
    }

    @Test
    public void testConstructor() {
        assertNotNull(alignmentConverter);
    }


    AlignmentRecord.Builder makeRead(Long start, String cigar, String mdtag, int length, int id, Boolean nullQuality) {
       String sequence =  String.join("", Collections.nCopies(length,"A"));
       String qual = String.join("", Collections.nCopies(length,"*"));
       AlignmentRecord.Builder builder = AlignmentRecord.newBuilder()
                .setReadName("read" + String.valueOf(id))
                .setStart(start)
                .setReadMapped(true)
                .setCigar(cigar)
                .setSequence(sequence)
                .setReadNegativeStrand(false)
                .setMapq(60)
                .setMismatchingPositions(mdtag)
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


        if (!nullQuality) {
            builder.setQual(qual);
        }

        return builder;
    }


    @Test
    public void testConvert() {
        org.bdgenomics.formats.avro.AlignmentRecord adamRead = makeRead(10L, "10M", "10", 10,0,false).build();

        System.out.println("Here is adamRead: " + adamRead.toString());
        System.out.println("Here is alignmentConverter: " + alignmentConverter.toString() + " conversion: " + ConversionStringency.STRICT.toString() + " logger: " + logger.toString());

        ga4gh.Reads.ReadAlignment gaRead = alignmentConverter.convert(adamRead, ConversionStringency.STRICT, logger);

        assertEquals(10L, gaRead.getAlignment().getPosition().getPosition());
        assertEquals( "myCtg", gaRead.getAlignment().getPosition().getReferenceName());
        assertEquals(Common.Strand.POS_STRAND, gaRead.getAlignment().getPosition().getStrand());
        assertEquals("rg1", gaRead.getReadGroupId());
        assertEquals("read0", gaRead.getFragmentName());
        assertEquals(false, gaRead.getImproperPlacement());
        assertEquals(false, gaRead.getDuplicateFragment());
        assertEquals(false,gaRead.getFailedVendorQualityChecks());
        assertEquals(false,gaRead.getSecondaryAlignment());
        assertEquals(false, gaRead.getSupplementaryAlignment());
        assertEquals("myCtg", gaRead.getNextMatePosition().getReferenceName());
        assertEquals(2, gaRead.getNumberReads());
        assertEquals(200L, gaRead.getFragmentLength());
        assertEquals("AAAAAAAAAA", gaRead.getAlignedSequence());

        List<Integer> temp = new ArrayList<Integer>(Arrays.asList(9, 9, 9, 9, 9, 9, 9, 9, 9, 9));
        assertEquals(temp, gaRead.getAlignedQualityList());

    }

    @Test
    public void testJSON() {
        org.bdgenomics.formats.avro.AlignmentRecord adamRead = makeRead(10L, "10M", "10", 10,0,false).build();

        System.out.println("Here is adamRead: " + adamRead.toString());
        System.out.println("Here is alignmentConverter: " + alignmentConverter.toString() + " conversion: " + ConversionStringency.STRICT.toString() + " logger: " + logger.toString());

        ga4gh.Reads.ReadAlignment gaRead = alignmentConverter.convert(adamRead, ConversionStringency.STRICT, logger);

        List<ga4gh.Reads.ReadAlignment> gaReads = new ArrayList<ga4gh.Reads.ReadAlignment>();
        gaReads.add(gaRead);

        ga4gh.ReadServiceOuterClass.SearchReadsResponse response = ga4gh.ReadServiceOuterClass.SearchReadsResponse.newBuilder().addAllAlignments(gaReads).build();

        try {
            String json = com.google.protobuf.util.JsonFormat.printer().print(response).replaceAll("\\s+","");
            assertEquals("{\"alignments\":[{\"readGroupId\":\"rg1\",\"fragmentName\":\"read0\",\"numberReads\":2,\"fragmentLength\":200,\"alignment\":{\"position\":{\"referenceName\":\"myCtg\",\"position\":\"10\",\"strand\":\"POS_STRAND\"},\"mappingQuality\":60,\"cigar\":[{\"operation\":\"ALIGNMENT_MATCH\",\"operationLength\":\"10\"}]},\"alignedSequence\":\"AAAAAAAAAA\",\"alignedQuality\":[9,9,9,9,9,9,9,9,9,9],\"nextMatePosition\":{\"referenceName\":\"myCtg\",\"position\":\"100\",\"strand\":\"POS_STRAND\"}}]}", json);
        } catch(com.google.protobuf.InvalidProtocolBufferException e) {
            System.err.println("Error throws com.google.protobuf.InvalidProtocolBufferException");
        }

    }



}









