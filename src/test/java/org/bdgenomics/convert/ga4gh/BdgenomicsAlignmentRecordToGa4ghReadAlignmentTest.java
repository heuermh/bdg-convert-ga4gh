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

import org.bdgenomics.formats.avro.AlignmentRecord;
import org.junit.Before;
import org.junit.Test;

import org.bdgenomics.convert.Converter;
import org.bdgenomics.convert.ConversionException;
import org.bdgenomics.convert.ConversionStringency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

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
                .setRecordGroupName("rg")
                .setContigName("myCtg");


        if (!nullQuality) {
            builder.setQual(qual); // no typo, we just don't care
        }

        return builder;
    }


    @Test
    public void testConvert() {
        org.bdgenomics.formats.avro.AlignmentRecord adamRead = makeRead(10L, "10M", "10", 10,0,false).build();

        System.out.println("Here is adamRead: " + adamRead.toString());
        System.out.println("Here is alignmentConverter: " + alignmentConverter.toString());

        ga4gh.Reads.ReadAlignment gaRead = alignmentConverter.convert(adamRead, ConversionStringency.STRICT, logger);


        //example assertion from different class
        /*
        assertEquals(ga4gh.Common.Strand.POS_STRAND, strandConverter.convert(org.bdgenomics.formats.avro.Strand.FORWARD, ConversionStringency.STRICT, logger));
        */

    }



}









