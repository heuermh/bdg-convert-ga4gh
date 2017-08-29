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

import javax.annotation.concurrent.Immutable;

import ga4gh.Common;
import ga4gh.Reads;
import org.bdgenomics.convert.AbstractConverter;
import org.bdgenomics.convert.Converter;
import org.bdgenomics.convert.ConversionException;
import org.bdgenomics.convert.ConversionStringency;

import org.bdgenomics.formats.avro.AlignmentRecord;
import htsjdk.samtools.Cigar;
import htsjdk.samtools.CigarOperator;
import htsjdk.samtools.TextCigarCodec;
import htsjdk.samtools.ValidationStringency;
import htsjdk.samtools.CigarElement;



import org.slf4j.Logger;

import java.util.*;

/**
 * Convert bdg-formats AlignmentRecord to GA4GH ReadAlignment.
 */
@Immutable
final class BdgenomicsAlignmentRecordToGa4ghReadAlignment extends AbstractConverter<AlignmentRecord, Reads.ReadAlignment> {

    /**
     * Convert bdg-format AlignmentRecord to GA4GH ReadAlignment.
     */
    BdgenomicsAlignmentRecordToGa4ghReadAlignment() { super(AlignmentRecord.class, Reads.ReadAlignment.class); }

    private List<ga4gh.Reads.CigarUnit> convertCigar(String cigarString) {
        if (cigarString == null) {
            return Collections.emptyList();
        }
        else {
            htsjdk.samtools.Cigar cigar = htsjdk.samtools.TextCigarCodec.decode(cigarString);

            List result = new ArrayList<ga4gh.Reads.CigarUnit>();
            for( CigarElement element : cigar.getCigarElements() ) {
                Reads.CigarUnit.Builder cuBuilder = ga4gh.Reads.CigarUnit.newBuilder();
                cuBuilder.setOperationLength(element.getLength());
                if(element.getOperator() == CigarOperator.M)  cuBuilder.setOperation(Reads.CigarUnit.Operation.ALIGNMENT_MATCH);
                if(element.getOperator() == CigarOperator.I)  cuBuilder.setOperation(Reads.CigarUnit.Operation.INSERT);
                if(element.getOperator() == CigarOperator.D)  cuBuilder.setOperation(Reads.CigarUnit.Operation.DELETE);
                if(element.getOperator() == CigarOperator.N)  cuBuilder.setOperation(Reads.CigarUnit.Operation.SKIP);
                if(element.getOperator() == CigarOperator.S)  cuBuilder.setOperation(Reads.CigarUnit.Operation.CLIP_SOFT);
                if(element.getOperator() == CigarOperator.H)  cuBuilder.setOperation(Reads.CigarUnit.Operation.CLIP_HARD);
                if(element.getOperator() == CigarOperator.P)  cuBuilder.setOperation(Reads.CigarUnit.Operation.PAD);
                if(element.getOperator() == CigarOperator.EQ)  cuBuilder.setOperation(Reads.CigarUnit.Operation.SEQUENCE_MATCH);
                if(element.getOperator() == CigarOperator.X)  cuBuilder.setOperation(Reads.CigarUnit.Operation.SEQUENCE_MISMATCH);

                result.add(cuBuilder.build());

            }

            return result;
        }

    }

    @Override
    public ga4gh.Reads.ReadAlignment convert(final AlignmentRecord alignmentRecord,
                                       final ConversionStringency stringency,
                                       final Logger logger) throws ClassCastException {

        ga4gh.Reads.ReadAlignment.Builder builder  = ga4gh.Reads.ReadAlignment.newBuilder();
        String rgName = alignmentRecord.getRecordGroupName();


        if(!rgName.isEmpty()) {
            builder.setReadGroupId(rgName);

        }
        else {
            builder.setReadGroupId("1");
        }

        builder.setFragmentName(alignmentRecord.getReadName());
        builder.setImproperPlacement(!alignmentRecord.getProperPair());
        builder.setDuplicateFragment(alignmentRecord.getDuplicateRead());
        builder.setFailedVendorQualityChecks(alignmentRecord.getFailedVendorQualityChecks());
        builder.setSecondaryAlignment(alignmentRecord.getSecondaryAlignment());
        builder.setSupplementaryAlignment(alignmentRecord.getSupplementaryAlignment());
        // /*
        if (alignmentRecord.getMateContigName() != null) {

            Common.Strand strand;
            if(alignmentRecord.getMateNegativeStrand())
                strand = Common.Strand.NEG_STRAND;
            else
                strand = Common.Strand.POS_STRAND;


            builder.setNextMatePosition(ga4gh.Common.Position.newBuilder().setReferenceName(alignmentRecord.getMateContigName())
                    .setPosition(alignmentRecord.getMateAlignmentStart())
                    .setStrand(strand)
                    .build()
                  );
        }

        Boolean paired = alignmentRecord.getReadPaired();
        if(paired)
            builder.setNumberReads(2);
        else
            builder.setNumberReads(1);

        builder.setReadNumber(alignmentRecord.getReadInFragment());


        if(alignmentRecord.getInferredInsertSize() != null) {
            builder.setFragmentLength(alignmentRecord.getInferredInsertSize().intValue());
        }

        builder.setAlignedSequence(alignmentRecord.getSequence());


        List<Integer> quallist = new ArrayList<>();
        for( char c : alignmentRecord.getQual().toCharArray()) {
            int x = ((int) c) - 33;
            quallist.add(x);
        }

        builder.addAllAlignedQuality(quallist);

        if( alignmentRecord.getReadMapped() ) {
            ga4gh.Reads.LinearAlignment.Builder laBuilder = ga4gh.Reads.LinearAlignment.newBuilder();
            Long start = alignmentRecord.getStart();
            String contig = alignmentRecord.getContigName();
            Boolean reverse = alignmentRecord.getReadNegativeStrand();


            Common.Strand strand;
            if(reverse)
                strand = Common.Strand.NEG_STRAND;
            else
                strand = Common.Strand.POS_STRAND;

            laBuilder.setPosition(ga4gh.Common.Position.newBuilder()
                    .setReferenceName(contig)
                    .setPosition(start.intValue())
                    .setStrand(strand).build());

            // set mapq
            laBuilder.setMappingQuality(alignmentRecord.getMapq());

            // convert cigar
            laBuilder.addAllCigar(convertCigar(alignmentRecord.getCigar() ) );

            // build and attach
            builder.setAlignment(laBuilder.build());

        }

        return builder.build();


    }


}
