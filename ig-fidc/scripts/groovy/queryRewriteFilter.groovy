import org.forgerock.http.protocol.Form

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
 * IG-Examples: Created by Charan Mann on 12/23/20 , 10:24 AM.
 */

/*
 * Rewrites the query params. Note: RelayState needs to be last parameter in SAML URL
 *
 * This script requires this argument: mappings (a rewrite map of key:value pairs where key is parameter name and value is a map of replacements).
 *         "mappings": {
 *               "metaAlias": {
 *                  "/tenant1/sp": "/bravo/tenant1",
 *                  "/tenant2/sp": "/bravo/tenant2"
 *                }
 *              }
 *
 */
def queryParams = request.uri.query
logger.info("Original request uri: ${request.uri}")

//Get RelayState
def relayState
int relayStateIndex = queryParams.indexOf('RelayState')

// If RelayState parameter exists
if (relayStateIndex != -1) {
    relayState = queryParams.substring(relayStateIndex - 1, queryParams.length())
    queryParams = queryParams - relayState
    logger.info("Query params after removing relayState: ${queryParams}")
}

// If query params and mappings are not null
if (queryParams && mappings) {
    Form form = new Form()
    form.fromFormString(queryParams)

    form.each { paramName, paramValues ->
        def rewriteParamMap = mappings[paramName]

        // Proceed only if the replacement map is present for this parameter, such as {"/tenant1/sp": "/bravo/tenant1"}
        if (rewriteParamMap) {

            paramValues.each { paramValue ->
                String rewriteParamValue = rewriteParamMap[paramValue]

                // Proceed only if the replacement param value is present, such as "/bravo/tenant1"
                if (rewriteParamValue) {
                    logger.info("Replacing query param: ${paramName} with ${rewriteParamValue}")

                    // Remove old value and add new value to param list
                    paramValues.remove(paramValue)
                    paramValues << rewriteParamValue
                }
            }

        }
    }
    logger.info("Updated query params: ${form}")

    if (relayStateIndex != -1) {
        logger.info("Adding RelayState back to uri: ${relayState}")
        request.uri.query = form.toQueryString() + relayState
    } else {
        request.uri.query = form.toQueryString()
    }

    logger.info("Updated request uri: ${request.uri}")
}

// Invoke the next handler
return next.handle(context, request)

