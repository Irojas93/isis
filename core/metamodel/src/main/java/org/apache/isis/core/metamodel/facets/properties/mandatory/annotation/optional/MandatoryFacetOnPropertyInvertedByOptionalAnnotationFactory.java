/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.apache.isis.core.metamodel.facets.properties.mandatory.annotation.optional;

import java.lang.reflect.Method;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.core.commons.config.IsisConfiguration;
import org.apache.isis.core.commons.config.IsisConfigurationAware;
import org.apache.isis.core.metamodel.facetapi.FeatureType;
import org.apache.isis.core.metamodel.facetapi.MetaModelValidatorRefiner;
import org.apache.isis.core.metamodel.facets.Annotations;
import org.apache.isis.core.metamodel.facets.FacetFactoryAbstract;
import org.apache.isis.core.metamodel.facets.FacetedMethod;
import org.apache.isis.core.metamodel.specloader.validator.MetaModelValidatorComposite;
import org.apache.isis.core.metamodel.specloader.validator.MetaModelValidatorForDeprecatedAnnotation;

public class MandatoryFacetOnPropertyInvertedByOptionalAnnotationFactory extends FacetFactoryAbstract implements MetaModelValidatorRefiner, IsisConfigurationAware {

    private final MetaModelValidatorForDeprecatedAnnotation validator = new MetaModelValidatorForDeprecatedAnnotation(Optional.class);


    public MandatoryFacetOnPropertyInvertedByOptionalAnnotationFactory() {
        super(FeatureType.PROPERTIES_ONLY);
    }

    @Override
    public void process(final ProcessMethodContext processMethodContext) {
        final Method method = processMethodContext.getMethod();
        final Optional annotation = Annotations.getAnnotation(method, Optional.class);
        if(annotation == null) {
            return;
        }
        final FacetedMethod facetHolder = processMethodContext.getFacetHolder();

        validator.addFacet(
                MandatoryFacetOnPropertyInvertedByOptionalAnnotation.create(annotation, method, facetHolder));
    }

    @Override
    public void refineMetaModelValidator(final MetaModelValidatorComposite metaModelValidator, final IsisConfiguration configuration) {
        metaModelValidator.add(validator);
    }

    @Override
    public void setConfiguration(final IsisConfiguration configuration) {
        validator.setConfiguration(configuration);
    }

}
