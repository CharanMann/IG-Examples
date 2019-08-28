/*
 * Copyright © 2019 ForgeRock, AS.
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
 * Portions Copyrighted 2019 Charan Mann
 *
 * IG-Examples: Created by Charan Mann on 2019-08-27 , 15:27.
 */

/*
 * Groovy script for retrieving user profile attributes and adding in AttributesContext 
 *
 * This script requires these arguments: profileAttributes, amUrl
 */
import org.forgerock.http.protocol.Request
import org.forgerock.util.AsyncFunction

if (!(profileAttributes.empty)) {

    // Retrieving user profile attributes
    logger.info("Using iPlanetDirectoryPro cookie: ${contexts.ssoToken.value}")
    logger.info("Retrieving user profile attributes: ${profileAttributes} for user: ${contexts.ssoToken.info.uid}")
    Request attributesRequest = new Request()
    attributesRequest.uri = "${amUrl}/users/${contexts.ssoToken.info.uid}"
    attributesRequest.headers.put('iPlanetDirectoryPro', contexts.ssoToken.value)
    attributesRequest.method = "GET"

    return http.send(context, attributesRequest)
            .thenAsync({ attributesResponse ->
                profileAttributes.each { name ->
                    def attrs = attributesResponse.entity.json
                    def values = attrs[name]
                    logger.info("Retrieved user profile attribute values: ${values} for attribute name: ${name}")

                    // Check if some attribute is present for specified name
                    if (null != values) {
                        def attrValue = values[0]

                        // Set the attributes in headers of the original request
                        // Security tip: These header values can be encrypted by a symmetric key shared between OpenIG and protected application
                        logger.info("Adding Attribute: ${name}, value: ${attrValue}")
                        attributes."${name}" = attrValue
                    }
                }

                attributesResponse.close()
                // Call the next handler with the modified request
                // That returns a new promise without blocking the current flow of execution
                return next.handle(context, request)
            } as AsyncFunction)
} else {
    logger.info("No attributes retrieval required, invoking next handler")
    // Call the next handler
    return next.handle(context, request)
}






