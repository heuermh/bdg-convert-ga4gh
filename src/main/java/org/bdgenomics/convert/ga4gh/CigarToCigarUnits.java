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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import ga4gh.Reads.CigarUnit;
import ga4gh.Reads.CigarUnit.Operation;
import ga4gh.Reads.ReadAlignment;

import htsjdk.samtools.Cigar;
import htsjdk.samtools.CigarElement;
import htsjdk.samtools.CigarOperator;

import org.bdgenomics.convert.AbstractConverter;
import org.bdgenomics.convert.Converter;
import org.bdgenomics.convert.ConversionException;
import org.bdgenomics.convert.ConversionStringency;

import org.slf4j.Logger;

/**
 * Convert htsjdk Cigar to a list of GA4GH CigarUnits.
 */
@Immutable
final class CigarToCigarUnits extends AbstractConverter<Cigar, List<CigarUnit>> {
    /** Convert htsjdk CigarOperator to GA4GH CigarUnit Operation. */
    private final Converter<CigarOperator, Operation> operatorConverter;

    /**
     * Convert htsjdk Cigar to a list of GA4GH CigarUnits.
     *
     * @param operatorConverter ciger operator converter, must not be null
     */
    CigarToCigarUnits(final Converter<CigarOperator, Operation> operatorConverter) {
        super(Cigar.class, List.class);
        checkNotNull(operatorConverter);
        this.operatorConverter = operatorConverter;
    }


    @Override
    public List<CigarUnit> convert(final Cigar cigar,
                                   final ConversionStringency stringency,
                                   final Logger logger) throws ConversionException {

        if (cigar == null) {
            warnOrThrow(cigar, "must not be null", null, stringency, logger);
            return null;
        }
        if (cigar.isEmpty()) {
            return Collections.<CigarUnit>emptyList();
        }

        List<CigarUnit> cigarUnits = new ArrayList<CigarUnit>(cigar.numCigarElements());
        for (CigarElement cigarElement : cigar) {
            cigarUnits.add(CigarUnit.newBuilder()
                .setOperationLength(cigarElement.getLength())
                .setOperation(operatorConverter.convert(cigarElement.getOperator(), stringency, logger))
                .build());
        }
        return cigarUnits;
    }
}
