/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 * Copyright 2014-2015 ForgeRock AS.
 */

package org.forgerock.openam.entitlement.rest.model.json;

import com.iplanet.sso.SSOTokenManager;
import com.sun.identity.entitlement.EntitlementException;
import org.forgerock.json.JsonValue;
import org.forgerock.json.resource.ActionRequest;
import org.forgerock.services.context.Context;
import org.forgerock.services.context.ClientContext;
import org.forgerock.openam.rest.RealmContext;
import org.forgerock.openam.rest.resource.SubjectContext;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.security.auth.Subject;
import java.util.HashMap;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * Unit test for {@link TreePolicyRequest}.
 *
 * @since 12.0.0
 */
public class TreePolicyRequestTest {

    @Mock
    private SubjectContext subjectContext;
    @Mock
    private ActionRequest actionRequest;
    @Mock
    private SSOTokenManager tokenManager;

    private Subject restSubject;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        restSubject = new Subject();
    }

    @Test
    public void shouldConstructBatchPolicyRequest() throws EntitlementException {
        // Given...
        given(subjectContext.getCallerSubject()).willReturn(restSubject);

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("resource", "/resource/a");
        given(actionRequest.getContent()).willReturn(JsonValue.json(properties));

        // When...
        Context context = buildContextStructure("/abc");
        TreePolicyRequest request = TreePolicyRequest.getTreePolicyRequest(context, actionRequest, tokenManager);

        // Then...
        assertThat(request).isNotNull();
        assertThat(request.getResource()).isEqualTo("/resource/a");

        verify(subjectContext).getCallerSubject();
        verify(actionRequest, times(2)).getContent();
        verifyNoMoreInteractions(subjectContext, actionRequest);
    }

    @Test(expectedExceptions = EntitlementException.class)
    public void shouldRejectNullResources() throws EntitlementException {
        // Given...
        Map<String, Object> properties = new HashMap<String, Object>();
        given(actionRequest.getContent()).willReturn(JsonValue.json(properties));
        given(subjectContext.getCallerSubject()).willReturn(restSubject);

        // When...
        Context context = buildContextStructure("/abc");
        TreePolicyRequest.getTreePolicyRequest(context, actionRequest, tokenManager);
    }

    @Test(expectedExceptions = EntitlementException.class)
    public void shouldRejectEmptyResources() throws EntitlementException {
        // Given...
        Map<String, Object> properties = new HashMap<String, Object>();
        given(actionRequest.getContent()).willReturn(JsonValue.json(properties));
        given(subjectContext.getCallerSubject()).willReturn(restSubject);

        // When...
        Context context = buildContextStructure("/abc");
        TreePolicyRequest.getTreePolicyRequest(context, actionRequest, tokenManager);
    }

    private Context buildContextStructure(final String realm) {
        return ClientContext.newInternalClientContext(new RealmContext(subjectContext));
    }

}
