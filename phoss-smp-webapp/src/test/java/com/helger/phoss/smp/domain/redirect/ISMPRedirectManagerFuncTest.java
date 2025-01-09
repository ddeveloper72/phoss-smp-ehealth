/*
 * Copyright (C) 2014-2024 Philip Helger and contributors
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.phoss.smp.domain.redirect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.peppolid.IDocumentTypeIdentifier;
import com.helger.peppolid.IParticipantIdentifier;
import com.helger.peppolid.factory.PeppolIdentifierFactory;
import com.helger.phoss.smp.domain.SMPMetaManager;
import com.helger.phoss.smp.domain.servicegroup.ISMPServiceGroup;
import com.helger.phoss.smp.domain.servicegroup.ISMPServiceGroupManager;
import com.helger.phoss.smp.exception.SMPServerException;
import com.helger.phoss.smp.mock.SMPServerTestRule;
import com.helger.photon.security.CSecurity;

/**
 * Test class for class {@link ISMPRedirectManager}.
 *
 * @author Philip Helger
 */
public final class ISMPRedirectManagerFuncTest
{
  @Rule
  public final TestRule m_aTestRule = new SMPServerTestRule ();

  @Test
  public void testRedirect () throws SMPServerException
  {
    final String sUserID = CSecurity.USER_ADMINISTRATOR_ID;
    if (SMPMetaManager.getInstance ().getBackendConnectionState ().isFalse ())
    {
      // Failed to get DB connection. E.g. MySQL down or misconfigured.
      return;
    }

    final IParticipantIdentifier aPI1 = PeppolIdentifierFactory.INSTANCE.createParticipantIdentifierWithDefaultScheme ("9999:junittest1");
    assertNotNull (aPI1);

    final IParticipantIdentifier aPI2 = PeppolIdentifierFactory.INSTANCE.createParticipantIdentifierWithDefaultScheme ("9999:junittest2");
    assertNotNull (aPI2);

    final IDocumentTypeIdentifier aDocTypeID = PeppolIdentifierFactory.INSTANCE.createDocumentTypeIdentifierWithDefaultScheme ("junit::testdoc##ext::1.0");
    assertNotNull (aDocTypeID);

    final ISMPServiceGroupManager aSGMgr = SMPMetaManager.getServiceGroupMgr ();
    final ISMPServiceGroup aSG = aSGMgr.createSMPServiceGroup (sUserID, aPI1, null, true);
    assertNotNull (aSG);
    try
    {
      final ISMPRedirectManager aRedirectMgr = SMPMetaManager.getRedirectMgr ();

      // Create new one
      ISMPRedirect aRedirect = aRedirectMgr.createOrUpdateSMPRedirect (aPI1,
                                                                       aDocTypeID,
                                                                       "target",
                                                                       "suid",
                                                                       null,
                                                                       "<extredirect />");
      assertNotNull (aRedirect);
      assertSame (aPI1, aRedirect.getServiceGroupParticipantIdentifier ());
      assertTrue (aDocTypeID.hasSameContent (aRedirect.getDocumentTypeIdentifier ()));
      assertEquals ("target", aRedirect.getTargetHref ());
      assertEquals ("suid", aRedirect.getSubjectUniqueIdentifier ());
      assertEquals ("<extredirect />", aRedirect.getExtensions ().getFirstExtensionXMLString ().trim ());
      final long nCount = aRedirectMgr.getSMPRedirectCount ();

      // Update existing
      aRedirect = aRedirectMgr.createOrUpdateSMPRedirect (aPI1,
                                                          aDocTypeID,
                                                          "target2",
                                                          "suid2",
                                                          null,
                                                          "<extredirect2 />");
      assertNotNull (aRedirect);
      assertSame (aPI1, aRedirect.getServiceGroupParticipantIdentifier ());
      assertTrue (aDocTypeID.hasSameContent (aRedirect.getDocumentTypeIdentifier ()));
      assertEquals ("target2", aRedirect.getTargetHref ());
      assertEquals ("suid2", aRedirect.getSubjectUniqueIdentifier ());
      assertEquals ("<extredirect2 />", aRedirect.getExtensions ().getFirstExtensionXMLString ().trim ());
      assertEquals (nCount, aRedirectMgr.getSMPRedirectCount ());

      // Add second one
      final ISMPServiceGroup aSG2 = aSGMgr.createSMPServiceGroup (sUserID, aPI2, null, true);
      assertNotNull (aSG2);
      try
      {
        aRedirect = aRedirectMgr.createOrUpdateSMPRedirect (aPI2,
                                                            aDocTypeID,
                                                            "target2",
                                                            "suid2",
                                                            null,
                                                            "<extredirect2 />");
        assertNotNull (aRedirect);
        assertSame (aPI2, aRedirect.getServiceGroupParticipantIdentifier ());
        assertTrue (aDocTypeID.hasSameContent (aRedirect.getDocumentTypeIdentifier ()));
        assertEquals ("target2", aRedirect.getTargetHref ());
        assertEquals ("suid2", aRedirect.getSubjectUniqueIdentifier ());
        assertEquals ("<extredirect2 />", aRedirect.getExtensions ().getFirstExtensionXMLString ().trim ());
        assertEquals (nCount + 1, aRedirectMgr.getSMPRedirectCount ());

        // Cleanup
        assertTrue (aRedirectMgr.deleteAllSMPRedirectsOfServiceGroup (aPI2).isChanged ());
        assertEquals (nCount, aRedirectMgr.getSMPRedirectCount ());
        assertTrue (aRedirectMgr.deleteAllSMPRedirectsOfServiceGroup (aPI1).isChanged ());
        assertEquals (nCount - 1, aRedirectMgr.getSMPRedirectCount ());
        assertTrue (aSGMgr.deleteSMPServiceGroupNoEx (aPI2, true).isChanged ());
        assertTrue (aSGMgr.deleteSMPServiceGroupNoEx (aPI1, true).isChanged ());
      }
      finally
      {
        aSGMgr.deleteSMPServiceGroupNoEx (aPI2, true);
      }
    }
    finally
    {
      aSGMgr.deleteSMPServiceGroupNoEx (aPI1, true);
    }
  }
}
