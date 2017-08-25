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

import ga4gh.Common.OntologyTerm;

import org.bdgenomics.convert.AbstractConverter;
import org.bdgenomics.convert.ConversionException;
import org.bdgenomics.convert.ConversionStringency;

import org.slf4j.Logger;

/**
 * Convert bdg-formats Feature.featureType as String to GA4GH OntologyTerm.
 */
@Immutable
final class StringToOntologyTerm extends AbstractConverter<String, OntologyTerm> {

    /**
     * Convert bdg-formats Feature.featureType as String to GA4GH OntologyTerm.
     */
    StringToOntologyTerm() {
        super(String.class, OntologyTerm.class);
    }


    @Override
    public OntologyTerm convert(final String featureType,
                                final ConversionStringency stringency,
                                final Logger logger) throws ConversionException {

        if (featureType == null) {
            warnOrThrow(featureType, "must not be null", null, stringency, logger);
            return null;
        }

        return OntologyTerm.newBuilder()
            .setTermId(featureType)
            .build();
    }
}
