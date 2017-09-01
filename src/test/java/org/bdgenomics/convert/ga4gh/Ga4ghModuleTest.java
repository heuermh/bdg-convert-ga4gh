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

import static org.junit.Assert.assertNotNull;

import java.util.List;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Guice;

import ga4gh.Reads.CigarUnit;
import ga4gh.Reads.CigarUnit.Operation;
import ga4gh.Reads.ReadAlignment;

import htsjdk.samtools.Cigar;
import htsjdk.samtools.CigarOperator;

import org.junit.Before;
import org.junit.Test;

import org.bdgenomics.convert.Converter;
import org.bdgenomics.convert.ConversionStringency;

import org.bdgenomics.convert.bdgenomics.BdgenomicsModule;

import org.bdgenomics.formats.avro.AlignmentRecord;

/**
 * Unit test for Ga4ghModule.
 */
public final class Ga4ghModuleTest {
    private Ga4ghModule module;

    @Before
    public void setUp() {
        module = new Ga4ghModule();
    }

    @Test
    public void testConstructor() {
        assertNotNull(module);
    }

    @Test
    public void testGa4ghModule() {
        Injector injector = Guice.createInjector(module, new BdgenomicsModule(), new TestModule());
        Target target = injector.getInstance(Target.class);
        assertNotNull(target.getBdgenomicsFeatureToGa4ghFeature());
        assertNotNull(target.getBdgenomicsOntologyTermToGa4ghOntologyTerm());
        assertNotNull(target.getFeatureTypeToOntologyTerm());
        assertNotNull(target.getGa4ghOntologyTermToBdgenomicsOntologyTerm());
        assertNotNull(target.getBdgenomicsStrandToGa4ghStrand());
        assertNotNull(target.getGa4ghStrandToBdgenomicsStrand());
        assertNotNull(target.getCigarOperatorToOperation());
        assertNotNull(target.getCigarToCigarUnits());
        assertNotNull(target.getAlignmentRecordToReadAlignment());
    }

    /**
     * Injection target.
     */
    static class Target {
        Converter<org.bdgenomics.formats.avro.Feature, ga4gh.SequenceAnnotations.Feature> bdgenomicsFeatureToGa4ghFeature;
        Converter<org.bdgenomics.formats.avro.OntologyTerm, ga4gh.Common.OntologyTerm> bdgenomicsOntologyTermToGa4ghOntologyTerm;
        Converter<ga4gh.Common.OntologyTerm, org.bdgenomics.formats.avro.OntologyTerm> ga4ghOntologyTermToBdgenomicsOntologyTerm;
        Converter<org.bdgenomics.formats.avro.Strand, ga4gh.Common.Strand> bdgenomicsStrandToGa4ghStrand;
        Converter<String, ga4gh.Common.OntologyTerm> featureTypeToOntologyTerm;
        Converter<ga4gh.Common.Strand, org.bdgenomics.formats.avro.Strand> ga4ghStrandToBdgenomicsStrand;
        Converter<CigarOperator, Operation> cigarOperatorToOperation;
        Converter<Cigar, List<CigarUnit>> cigarToCigarUnits;
        Converter<AlignmentRecord, ReadAlignment> alignmentRecordToReadAlignment;

        @Inject
        Target(final Converter<org.bdgenomics.formats.avro.Feature, ga4gh.SequenceAnnotations.Feature> bdgenomicsFeatureToGa4ghFeature,
               final Converter<org.bdgenomics.formats.avro.OntologyTerm, ga4gh.Common.OntologyTerm> bdgenomicsOntologyTermToGa4ghOntologyTerm,
               final Converter<String, ga4gh.Common.OntologyTerm> featureTypeToOntologyTerm,
               final Converter<ga4gh.Common.OntologyTerm, org.bdgenomics.formats.avro.OntologyTerm> ga4ghOntologyTermToBdgenomicsOntologyTerm,
               final Converter<org.bdgenomics.formats.avro.Strand, ga4gh.Common.Strand> bdgenomicsStrandToGa4ghStrand,
               final Converter<ga4gh.Common.Strand, org.bdgenomics.formats.avro.Strand> ga4ghStrandToBdgenomicsStrand,
               final Converter<CigarOperator, Operation> cigarOperatorToOperation,
               final Converter<Cigar, List<CigarUnit>> cigarToCigarUnits,
               final Converter<AlignmentRecord, ReadAlignment> alignmentRecordToReadAlignment) {

            this.bdgenomicsFeatureToGa4ghFeature = bdgenomicsFeatureToGa4ghFeature;
            this.bdgenomicsOntologyTermToGa4ghOntologyTerm = bdgenomicsOntologyTermToGa4ghOntologyTerm;
            this.featureTypeToOntologyTerm = featureTypeToOntologyTerm;
            this.ga4ghOntologyTermToBdgenomicsOntologyTerm = ga4ghOntologyTermToBdgenomicsOntologyTerm;
            this.bdgenomicsStrandToGa4ghStrand = bdgenomicsStrandToGa4ghStrand;
            this.ga4ghStrandToBdgenomicsStrand = ga4ghStrandToBdgenomicsStrand;
            this.cigarOperatorToOperation = cigarOperatorToOperation;
            this.cigarToCigarUnits = cigarToCigarUnits;
            this.alignmentRecordToReadAlignment = alignmentRecordToReadAlignment;
        }

        Converter<org.bdgenomics.formats.avro.Feature, ga4gh.SequenceAnnotations.Feature> getBdgenomicsFeatureToGa4ghFeature() {
            return bdgenomicsFeatureToGa4ghFeature;
        }

        Converter<org.bdgenomics.formats.avro.OntologyTerm, ga4gh.Common.OntologyTerm> getBdgenomicsOntologyTermToGa4ghOntologyTerm() {
            return bdgenomicsOntologyTermToGa4ghOntologyTerm;
        }

        Converter<String, ga4gh.Common.OntologyTerm> getFeatureTypeToOntologyTerm() {
            return featureTypeToOntologyTerm;
        }

        Converter<ga4gh.Common.OntologyTerm, org.bdgenomics.formats.avro.OntologyTerm> getGa4ghOntologyTermToBdgenomicsOntologyTerm() {
            return ga4ghOntologyTermToBdgenomicsOntologyTerm;
        }

        Converter<org.bdgenomics.formats.avro.Strand, ga4gh.Common.Strand> getBdgenomicsStrandToGa4ghStrand() {
            return bdgenomicsStrandToGa4ghStrand;
        }

        Converter<ga4gh.Common.Strand, org.bdgenomics.formats.avro.Strand> getGa4ghStrandToBdgenomicsStrand() {
            return ga4ghStrandToBdgenomicsStrand;
        }

        Converter<CigarOperator, Operation> getCigarOperatorToOperation() {
            return cigarOperatorToOperation;
        }

        Converter<Cigar, List<CigarUnit>> getCigarToCigarUnits() {
            return cigarToCigarUnits;
        }

        Converter<AlignmentRecord, ReadAlignment> getAlignmentRecordToReadAlignment() {
            return alignmentRecordToReadAlignment;
        }

    }

    /**
     * Test module.
     */
    class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(Target.class);
        }
    }
}
