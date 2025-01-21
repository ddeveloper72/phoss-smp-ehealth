/*
 * Copyright (C) 2014-2025 Philip Helger and contributors
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
package com.helger.phoss.smp.domain.servicegroup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.peppolid.IParticipantIdentifier;
import com.helger.peppolid.factory.IIdentifierFactory;
import com.helger.peppolid.peppol.PeppolIdentifierHelper;
import com.helger.phoss.smp.domain.SMPMetaManager;
import com.helger.phoss.smp.exception.SMPServerException;
import com.helger.phoss.smp.mock.SMPServerTestRule;
import com.helger.photon.security.CSecurity;

/**
 * Test class for class {@link ISMPServiceGroupManager}.
 *
 * @author Philip Helger
 */
public final class ISMPServiceGroupManagerFuncTest
{
  @Rule
  public final TestRule m_aTestRule = new SMPServerTestRule ();

  @Test
  public void testBasic () throws SMPServerException
  {
    final IIdentifierFactory aIDFactory = SMPMetaManager.getIdentifierFactory ();
    final IParticipantIdentifier aPI1 = aIDFactory.createParticipantIdentifier (PeppolIdentifierHelper.DEFAULT_PARTICIPANT_SCHEME,
                                                                                "9999:junittest1");
    final IParticipantIdentifier aPI2 = aIDFactory.createParticipantIdentifier (PeppolIdentifierHelper.DEFAULT_PARTICIPANT_SCHEME,
                                                                                "9999:junittest2");
    final String sSG1 = SMPServiceGroup.createSMPServiceGroupID (aPI1);
    final String sSG2 = SMPServiceGroup.createSMPServiceGroupID (aPI2);
    final String sOwner1ID = CSecurity.USER_ADMINISTRATOR_ID;
    final String sOwner2ID = CSecurity.USER_ADMINISTRATOR_ID + "2";
    final String sExtension = "<ext val=\"a\" />";

    final ISMPServiceGroupManager aSGMgr = SMPMetaManager.getServiceGroupMgr ();
    assertNotNull (aSGMgr);
    try
    {
      // Check empty state
      final long nCount = aSGMgr.getSMPServiceGroupCount ();
      assertEquals (nCount, aSGMgr.getAllSMPServiceGroups ().size ());
      assertFalse (aSGMgr.containsSMPServiceGroupWithID (aPI1));
      assertFalse (aSGMgr.containsSMPServiceGroupWithID (aPI2));
      assertNull (aSGMgr.getSMPServiceGroupOfID (aPI1));
      assertNull (aSGMgr.getSMPServiceGroupOfID (aPI2));
      assertEquals (0, aSGMgr.getAllSMPServiceGroupsOfOwner (sOwner1ID).size ());
      assertEquals (0, aSGMgr.getAllSMPServiceGroupsOfOwner (sOwner2ID).size ());
      assertTrue (aSGMgr.deleteSMPServiceGroupNoEx (aPI1, true).isUnchanged ());
      assertTrue (aSGMgr.deleteSMPServiceGroupNoEx (aPI2, true).isUnchanged ());
      assertTrue (aSGMgr.updateSMPServiceGroupNoEx (aPI1, sOwner1ID, sExtension).isUnchanged ());
      assertTrue (aSGMgr.updateSMPServiceGroupNoEx (aPI2, sOwner1ID, sExtension).isUnchanged ());
      assertTrue (aSGMgr.updateSMPServiceGroupNoEx (aPI1, sOwner2ID, sExtension).isUnchanged ());
      assertTrue (aSGMgr.updateSMPServiceGroupNoEx (aPI2, sOwner2ID, sExtension).isUnchanged ());

      // Register first and check state
      ISMPServiceGroup aSG1 = aSGMgr.createSMPServiceGroup (sOwner1ID, aPI1, sExtension, true);
      assertNotNull (aSG1);
      assertEquals (aPI1, aSG1.getParticipantIdentifier ());
      assertEquals (sSG1, aSG1.getID ());
      assertEquals (sOwner1ID, aSG1.getOwnerID ());
      assertEquals (sExtension, aSG1.getExtensions ().getFirstExtensionXMLString ().trim ());

      // Check manager state
      assertEquals (nCount + 1, aSGMgr.getSMPServiceGroupCount ());
      assertEquals (nCount + 1, aSGMgr.getAllSMPServiceGroups ().size ());
      assertTrue (aSGMgr.getAllSMPServiceGroups ().contains (aSG1));
      assertTrue (aSGMgr.containsSMPServiceGroupWithID (aPI1));
      assertFalse (aSGMgr.containsSMPServiceGroupWithID (aPI2));
      assertEquals (aSG1, aSGMgr.getSMPServiceGroupOfID (aPI1));
      assertNull (aSGMgr.getSMPServiceGroupOfID (aPI2));
      assertEquals (1, aSGMgr.getAllSMPServiceGroupsOfOwner (sOwner1ID).size ());
      assertTrue (aSGMgr.getAllSMPServiceGroupsOfOwner (sOwner1ID).contains (aSG1));
      assertEquals (0, aSGMgr.getAllSMPServiceGroupsOfOwner (sOwner2ID).size ());

      // change owner
      assertTrue (aSGMgr.updateSMPServiceGroup (aPI1, sOwner1ID, sExtension).isUnchanged ());
      assertTrue (aSGMgr.updateSMPServiceGroup (aPI1, sOwner2ID, sExtension).isChanged ());
      assertEquals (0, aSGMgr.getAllSMPServiceGroupsOfOwner (sOwner1ID).size ());
      assertEquals (1, aSGMgr.getAllSMPServiceGroupsOfOwner (sOwner2ID).size ());
      assertTrue (aSGMgr.getAllSMPServiceGroupsOfOwner (sOwner2ID).contains (aSG1));
      assertTrue (aSGMgr.updateSMPServiceGroupNoEx (aPI2, sOwner1ID, sExtension).isUnchanged ());
      assertTrue (aSGMgr.updateSMPServiceGroupNoEx (aPI2, sOwner2ID, sExtension).isUnchanged ());
      aSG1 = aSGMgr.getSMPServiceGroupOfID (aPI1);
      assertEquals (sOwner2ID, aSG1.getOwnerID ());

      final ISMPServiceGroup aSG2 = aSGMgr.createSMPServiceGroup (sOwner2ID, aPI2, sExtension, true);
      assertNotNull (aSG2);
      assertEquals (aPI2, aSG2.getParticipantIdentifier ());
      assertEquals (sSG2, aSG2.getID ());
      assertEquals (sOwner2ID, aSG2.getOwnerID ());
      assertEquals (sExtension, aSG2.getExtensions ().getFirstExtensionXMLString ().trim ());

      // Check manager state
      assertEquals (nCount + 2, aSGMgr.getSMPServiceGroupCount ());
      assertEquals (nCount + 2, aSGMgr.getAllSMPServiceGroups ().size ());
      assertTrue (aSGMgr.getAllSMPServiceGroups ().contains (aSG1));
      assertTrue (aSGMgr.getAllSMPServiceGroups ().contains (aSG2));
      assertTrue (aSGMgr.containsSMPServiceGroupWithID (aPI1));
      assertTrue (aSGMgr.containsSMPServiceGroupWithID (aPI2));
      assertEquals (aSG1, aSGMgr.getSMPServiceGroupOfID (aPI1));
      assertEquals (aSG2, aSGMgr.getSMPServiceGroupOfID (aPI2));
      assertEquals (0, aSGMgr.getAllSMPServiceGroupsOfOwner (sOwner1ID).size ());
      assertEquals (2, aSGMgr.getAllSMPServiceGroupsOfOwner (sOwner2ID).size ());
      assertTrue (aSGMgr.getAllSMPServiceGroupsOfOwner (sOwner2ID).contains (aSG1));
      assertTrue (aSGMgr.getAllSMPServiceGroupsOfOwner (sOwner2ID).contains (aSG2));

      // delete SG1
      assertTrue (aSGMgr.deleteSMPServiceGroupNoEx (aPI1, true).isChanged ());
      assertTrue (aSGMgr.deleteSMPServiceGroupNoEx (aPI1, true).isUnchanged ());

      // Check manager state
      assertEquals (nCount + 1, aSGMgr.getSMPServiceGroupCount ());
      assertEquals (nCount + 1, aSGMgr.getAllSMPServiceGroups ().size ());
      assertTrue (aSGMgr.getAllSMPServiceGroups ().contains (aSG2));
      assertFalse (aSGMgr.containsSMPServiceGroupWithID (aPI1));
      assertTrue (aSGMgr.containsSMPServiceGroupWithID (aPI2));
      assertNull (aSGMgr.getSMPServiceGroupOfID (aPI1));
      assertEquals (aSG2, aSGMgr.getSMPServiceGroupOfID (aPI2));
      assertEquals (0, aSGMgr.getAllSMPServiceGroupsOfOwner (sOwner1ID).size ());
      assertEquals (1, aSGMgr.getAllSMPServiceGroupsOfOwner (sOwner2ID).size ());
      assertTrue (aSGMgr.getAllSMPServiceGroupsOfOwner (sOwner2ID).contains (aSG2));

      // Finally delete SG2
      assertTrue (aSGMgr.deleteSMPServiceGroupNoEx (aPI2, true).isChanged ());
      assertTrue (aSGMgr.deleteSMPServiceGroupNoEx (aPI2, true).isUnchanged ());

      // Check empty state
      assertEquals (nCount, aSGMgr.getSMPServiceGroupCount ());
      assertEquals (nCount, aSGMgr.getAllSMPServiceGroups ().size ());
      assertFalse (aSGMgr.containsSMPServiceGroupWithID (aPI1));
      assertFalse (aSGMgr.containsSMPServiceGroupWithID (aPI2));
      assertNull (aSGMgr.getSMPServiceGroupOfID (aPI1));
      assertNull (aSGMgr.getSMPServiceGroupOfID (aPI2));
      assertEquals (0, aSGMgr.getAllSMPServiceGroupsOfOwner (sOwner1ID).size ());
      assertEquals (0, aSGMgr.getAllSMPServiceGroupsOfOwner (sOwner2ID).size ());
      assertTrue (aSGMgr.deleteSMPServiceGroupNoEx (aPI1, true).isUnchanged ());
      assertTrue (aSGMgr.deleteSMPServiceGroupNoEx (aPI2, true).isUnchanged ());
      assertTrue (aSGMgr.updateSMPServiceGroupNoEx (aPI1, sOwner1ID, sExtension).isUnchanged ());
      assertTrue (aSGMgr.updateSMPServiceGroupNoEx (aPI2, sOwner1ID, sExtension).isUnchanged ());
      assertTrue (aSGMgr.updateSMPServiceGroupNoEx (aPI1, sOwner2ID, sExtension).isUnchanged ());
      assertTrue (aSGMgr.updateSMPServiceGroupNoEx (aPI2, sOwner2ID, sExtension).isUnchanged ());
    }
    finally
    {
      // Don't care about the result
      aSGMgr.deleteSMPServiceGroupNoEx (aPI1, true);
      aSGMgr.deleteSMPServiceGroupNoEx (aPI2, true);
    }
  }
}
