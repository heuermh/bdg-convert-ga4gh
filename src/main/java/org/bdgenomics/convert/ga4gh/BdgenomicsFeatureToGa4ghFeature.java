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
import org.bdgenomics.convert.Converter;
import org.bdgenomics.convert.ConversionException;
import org.bdgenomics.convert.ConversionStringency;

import org.slf4j.Logger;

/**
 * Convert bdg-formats Feature to GA4GH Feature.
 */
@Immutable
final class BdgenomicsFeatureToGa4ghFeature extends AbstractConverter<org.bdgenomics.formats.avro.Feature, ga4gh.SequenceAnnotations.Feature> {
    /** Convert bdg-formats Feature.featureType as String to GA4GH OntologyTerm. */
    private final Converter<String, ga4gh.Common.OntologyTerm> featureTypeConverter;
    /** Convert bdg-formats Strand to GA4GH Strand. */
    private final Converter<org.bdgenomics.formats.avro.Strand, ga4gh.Common.Strand> strandConverter;

    /**
     * Convert bdg-formats Feature to GA4GH Feature.
     *
     * @param featureTypeConverter feature type converter, must not be null
     * @param strandConverter strand converter, must not be null
     */
    BdgenomicsFeatureToGa4ghFeature(final Converter<String, ga4gh.Common.OntologyTerm> featureTypeConverter,
                                    final Converter<org.bdgenomics.formats.avro.Strand, ga4gh.Common.Strand> strandConverter) {
        super(org.bdgenomics.formats.avro.Feature.class, ga4gh.SequenceAnnotations.Feature.class);
        checkNotNull(featureTypeConverter);
        checkNotNull(strandConverter);
        this.featureTypeConverter = featureTypeConverter;
        this.strandConverter = strandConverter;
    }


    @Override
    public ga4gh.SequenceAnnotations.Feature convert(final org.bdgenomics.formats.avro.Feature feature,
                                                     final ConversionStringency stringency,
                                                     final Logger logger) throws ConversionException {

        if (feature == null) {
            warnOrThrow(feature, "must not be null", null, stringency, logger);
            return null;
        }

        return ga4gh.SequenceAnnotations.Feature.newBuilder()
            .setStart(feature.getStart())
            .setEnd(feature.getEnd())
            .setStrand(strandConverter.convert(feature.getStrand(), stringency, logger))
            .setReferenceName(feature.getContigName())
            .setFeatureType(featureTypeConverter.convert(feature.getFeatureType(), stringency, logger))
            .build();
    }
}
