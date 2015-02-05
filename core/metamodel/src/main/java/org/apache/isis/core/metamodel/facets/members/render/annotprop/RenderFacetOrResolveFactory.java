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

package org.apache.isis.core.metamodel.facets.members.render.annotprop;

import java.util.Properties;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Resolve;
import org.apache.isis.core.commons.config.IsisConfiguration;
import org.apache.isis.core.commons.config.IsisConfigurationAware;
import org.apache.isis.core.metamodel.facetapi.FacetHolder;
import org.apache.isis.core.metamodel.facetapi.FacetUtil;
import org.apache.isis.core.metamodel.facetapi.FeatureType;
import org.apache.isis.core.metamodel.facetapi.MetaModelValidatorRefiner;
import org.apache.isis.core.metamodel.facets.Annotations;
import org.apache.isis.core.metamodel.facets.ContributeeMemberFacetFactory;
import org.apache.isis.core.metamodel.facets.FacetFactoryAbstract;
import org.apache.isis.core.metamodel.facets.members.render.RenderFacet;
import org.apache.isis.core.metamodel.specloader.validator.MetaModelValidatorComposite;
import org.apache.isis.core.metamodel.specloader.validator.MetaModelValidatorForDeprecatedAnnotation;

public class RenderFacetOrResolveFactory extends FacetFactoryAbstract
        implements ContributeeMemberFacetFactory, MetaModelValidatorRefiner, IsisConfigurationAware {

    private final MetaModelValidatorForDeprecatedAnnotation renderValidator = new MetaModelValidatorForDeprecatedAnnotation(Render.class);
    private final MetaModelValidatorForDeprecatedAnnotation resolveValidator = new MetaModelValidatorForDeprecatedAnnotation(Resolve.class);

    public RenderFacetOrResolveFactory() {
        super(FeatureType.MEMBERS);
    }

    @Override
    public void process(final ProcessMethodContext processMethodContext) {
        
        RenderFacet renderFacet = createFromMetadataPropertiesIfPossible(processMethodContext);
        if(renderFacet == null) {
            renderFacet = renderValidator.invalidIfPresent(createFromRenderAnnotationIfPossible(processMethodContext));
        }
        if(renderFacet == null) {
            renderFacet = resolveValidator.invalidIfPresent(createFromResolveAnnotationIfPossible(processMethodContext));
        }

        // no-op if null
        FacetUtil.addFacet(renderFacet);
    }

    @Override
    public void process(ProcessContributeeMemberContext processMemberContext) {
        RenderFacet renderFacet = createFromMetadataPropertiesIfPossible(processMemberContext);
        // no-op if null
        FacetUtil.addFacet(renderFacet);
    }

    private static RenderFacet createFromMetadataPropertiesIfPossible(
            final ProcessContextWithMetadataProperties<? extends FacetHolder> pcwmp) {
        
        final FacetHolder holder = pcwmp.getFacetHolder();
        
        final Properties properties = pcwmp.metadataProperties("render");
        return properties != null ? new RenderFacetProperties(properties, holder) : null;
    }

    private static RenderFacet createFromRenderAnnotationIfPossible(final ProcessMethodContext processMethodContext) {
        final Render renderAnnotation = Annotations.getAnnotation(processMethodContext.getMethod(), Render.class);
        return renderAnnotation == null ? null : new RenderFacetAnnotation(processMethodContext.getFacetHolder(), renderAnnotation.value());
    }

    // @Render was originally called @Resolve, so look for that annotation instead.
    @SuppressWarnings("deprecation")
    private static RenderFacet createFromResolveAnnotationIfPossible(final ProcessMethodContext processMethodContext) {
        final org.apache.isis.applib.annotation.Resolve resolveAnnotation = 
        Annotations.getAnnotation(processMethodContext.getMethod(), org.apache.isis.applib.annotation.Resolve.class);
        return resolveAnnotation == null ? null : new RenderFacetViaResolveAnnotation(processMethodContext.getFacetHolder(), resolveAnnotation.value());
    }


    @Override
    public void refineMetaModelValidator(final MetaModelValidatorComposite metaModelValidator, final IsisConfiguration configuration) {
        metaModelValidator.add(renderValidator);
        metaModelValidator.add(resolveValidator);
    }

    @Override
    public void setConfiguration(final IsisConfiguration configuration) {
        renderValidator.setConfiguration(configuration);
        resolveValidator.setConfiguration(configuration);
    }

}
