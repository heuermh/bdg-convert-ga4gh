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
 * Convert GA4GH OntologyTerm to bdg-formats OntologyTerm.
 */
@Immutable
final class Ga4ghOntologyTermToBdgenomicsOntologyTerm extends AbstractConverter<ga4gh.Common.OntologyTerm, org.bdgenomics.formats.avro.OntologyTerm> {

    /**
     * Convert GA4GH OntologyTerm to bdg-formats OntologyTerm.
     */
    Ga4ghOntologyTermToBdgenomicsOntologyTerm() {
        super(ga4gh.Common.OntologyTerm.class, org.bdgenomics.formats.avro.OntologyTerm.class);
    }


    @Override
    public org.bdgenomics.formats.avro.OntologyTerm convert(final ga4gh.Common.OntologyTerm ontologyTerm,
                                                            final ConversionStringency stringency,
                                                            final Logger logger) throws ConversionException {

        if (ontologyTerm == null) {
            warnOrThrow(ontologyTerm, "must not be null", null, stringency, logger);
            return null;
        }

        return org.bdgenomics.formats.avro.OntologyTerm.newBuilder()
            .build();
    }
}
