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
 * Groovy script for custom token creation and validation
 *
 * This script requires these arguments: profileAttributes, amUrl
 */
import org.forgerock.http.protocol.Form
import org.forgerock.http.protocol.Request
import org.forgerock.http.protocol.Response
import org.forgerock.http.protocol.Status
import org.forgerock.util.AsyncFunction

import static org.forgerock.http.protocol.Response.newResponsePromise

/**
 * Creates unauthorized error
 */
def getUnauthorizedError() {
    logger.info("Returning UNAUTHORIZED error")
    Response errResponse = new Response(Status.UNAUTHORIZED)
    errResponse.entity.json = [code: 401, reason: "Unauthorized", message: "Authentication Failed"];

    // Need to wrap the response object in a promise
    return newResponsePromise(errResponse)
}

logger.info("Using API key: ${apiKey}")
logger.info("Using Token generator: ${tokenGenURL}")
Request tokenGenerator = new Request()
tokenGenerator.uri = "${tokenGenURL}/apikey/${apiKey}"
tokenGenerator.method = "POST"

return http.send(context, tokenGenerator)
        .thenAsync({ tokenResponse ->
            def response = tokenResponse.entity.json
            def token = response.access_token
            logger.info("Retrieved token: ${token}")

            if (token) {
                logger.info("Adding token in attributes for the next filter")

                // Adding token in attributes for JWT filter
                attributes.token = token

                // Adding token in query parameter also
                logger.info("Adding token in query param")
                Form form = new Form()
                form.add("token", token)
                form.appendRequestQuery(request)
            } else {
                return getUnauthorizedError()
            }

            tokenResponse.close()
            // Call the next handler with the modified request
            // That returns a new promise without blocking the current flow of execution
            return next.handle(context, request)
        } as AsyncFunction)






