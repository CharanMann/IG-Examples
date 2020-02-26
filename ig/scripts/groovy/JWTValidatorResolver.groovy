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

import org.forgerock.http.oauth2.AccessTokenException
import org.forgerock.http.oauth2.AccessTokenInfo
import org.forgerock.json.jose.jws.SignedJwt
import org.forgerock.json.jose.jws.SigningManager
import org.forgerock.json.jose.jws.handlers.SigningHandler
import org.forgerock.json.jose.jwt.JwtClaimsSet
import org.forgerock.openig.secrets.SecretsUtils
import org.forgerock.openig.tools.JwtUtil
import org.forgerock.secrets.Purpose
import org.forgerock.secrets.keys.VerificationKey

import static org.forgerock.json.JsonValueFunctions.setOf
import static org.forgerock.openig.tools.JwtUtil.SCOPE

def asAccessTokenInfo(String token, JwtClaimsSet jwtClaimsSet) {
    long expirationTime = jwtClaimsSet.getExpirationTime().getTime()

    // Convert claims to JSON
    def json = jwtClaimsSet.toJsonValue()
    new AccessTokenInfo(json, token, json.get(SCOPE).as(setOf(String.class)), expirationTime)
}

// Retrieve Key from secret provider
logger.info("Keystore from heap {}", secretsProvider)

def sp = Purpose.purpose(secretId, VerificationKey.class)
VerificationKey verificationKey = secretsProvider.getActiveSecret(sp).get();
logger.info("Verification Key details {}", SecretsUtils.exportAsKey(verificationKey))

SigningManager signingManager = new SigningManager()
SigningHandler handler = signingManager.newVerificationHandler(SecretsUtils.exportAsKey(verificationKey))

// Signature validation
SignedJwt jwt = JwtUtil.reconstructJwt(token, SignedJwt.class)

try {
    if (jwt.verify(handler)) {
        logger.info("Signature verification passed")

        // Return AccessTokenInfo
        asAccessTokenInfo(token, jwt.claimsSet)
    } else {
        throw new AccessTokenException("JWT content does not match signature")
    }
} catch (Exception e) {
    throw new AccessTokenException("Scripted resolver failed: ", e)
}











