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

import org.bdgenomics.convert.AbstractConverter;
import org.bdgenomics.convert.ConversionException;
import org.bdgenomics.convert.ConversionStringency;

import org.slf4j.Logger;

/**
 * Convert bdg-formats Strand to GA4GH Strand.
 */
@Immutable
final class BdgenomicsStrandToGa4ghStrand extends AbstractConverter<org.bdgenomics.formats.avro.Strand, ga4gh.Common.Strand> {

    /**
     * Convert bdg-formats Strand to GA4GH Strand.
     */
    BdgenomicsStrandToGa4ghStrand() {
        super(org.bdgenomics.formats.avro.Strand.class, ga4gh.Common.Strand.class);
    }


    @Override
    public ga4gh.Common.Strand convert(final org.bdgenomics.formats.avro.Strand strand,
                                       final ConversionStringency stringency,
                                       final Logger logger) throws ConversionException {

        if (strand == null) {
            warnOrThrow(strand, "must not be null", null, stringency, logger);
            return null;
        }

        if (strand.equals(org.bdgenomics.formats.avro.Strand.FORWARD)) {
            return ga4gh.Common.Strand.POS_STRAND;
        }
        else if (strand.equals(org.bdgenomics.formats.avro.Strand.REVERSE)) {
            return ga4gh.Common.Strand.NEG_STRAND;
        }
        // note UNRECOGNIZED is no longer present on git head
        else if (strand.equals(org.bdgenomics.formats.avro.Strand.INDEPENDENT)) {
            return ga4gh.Common.Strand.UNRECOGNIZED;
        }
        return ga4gh.Common.Strand.STRAND_UNSPECIFIED;
    }
}
