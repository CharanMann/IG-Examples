/*
 * Copyright © 2020 ForgeRock, AS.
 *
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
 * IG-Examples: Created by Charan Mann on 2/20/20 , 11:21 AM
 */

/*
 * Groovy script for validating JWT tokens
 *
 * This script requires these arguments: secretsProvider, secretId
 */

import org.forgerock.secrets.*
import org.forgerock.secrets.keys.*
import org.forgerock.openig.secrets.*

/**
 * Invoke delegate
 */
def invokeDelegate() {
    // Call delegate.
    return delegate.resolve(context, token)
}

logger.info("Keystore from heap {}", secretsProvider)

def sp = Purpose.purpose(secretId,  VerificationKey.class)
def signingSecret = secretsProvider.getActive(sp).get();
logger.info("Signing Key details {}", SecretsUtils.exportAsKey(signingSecret)) // gets back a java.security.Key


invokeDelegate()



