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

import ga4gh.Reads.CigarUnit.Operation;

import htsjdk.samtools.CigarOperator;

import org.bdgenomics.convert.AbstractConverter;
import org.bdgenomics.convert.ConversionException;
import org.bdgenomics.convert.ConversionStringency;

import org.slf4j.Logger;

/**
 * Convert htsjdk CigarOperator to GA4GH CigarUnit Operation.
 */
@Immutable
final class CigarOperatorToOperation extends AbstractConverter<CigarOperator, Operation> {

    /**
     * Convert htsjdk CigarOperator to GA4GH CigarUnit Operation.
     */
    CigarOperatorToOperation() {
        super(CigarOperator.class, Operation.class);
    }


    @Override
    public Operation convert(final CigarOperator cigarOperator,
                             final ConversionStringency stringency,
                             final Logger logger) throws ConversionException {

        if (cigarOperator == null) {
            warnOrThrow(cigarOperator, "must not be null", null, stringency, logger);
            return null;
        }
        switch (cigarOperator) {
            case M: return Operation.ALIGNMENT_MATCH;
            case I: return Operation.INSERT;
            case D: return Operation.DELETE;
            case N: return Operation.SKIP;
            case S: return Operation.CLIP_SOFT;
            case H: return Operation.CLIP_HARD;
            case P: return Operation.PAD;
            case EQ: return Operation.SEQUENCE_MATCH;
            case X: return Operation.SEQUENCE_MISMATCH;
            default: {
                warnOrThrow(cigarOperator, "could not match operator", null, stringency, logger);
                return null;
            }
        }
    }
}
