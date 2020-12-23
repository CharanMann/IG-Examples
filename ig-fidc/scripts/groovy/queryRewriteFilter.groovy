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
 * IG-Examples:: Created by Charan Mann on 12/23/20 , 10:24 AM.
 */

/*
 * Rewrites the query params
 *
 * This script requires these arguments: mappings
 */
def params = request.uri.query
logger.info("Original request uri: ${request.uri}")

mappings.each { key, value ->
    // Replace if key is param of query params
    if (params.indexOf(key) != -1) {
        logger.info("Replacing query param: ${key} with ${value}")
        params = params.replace(key, value)
    }
}
logger.info("Updated query params: ${params}")

request.uri.query = params
logger.info("Updated request uri: ${request.uri}")

// Call the next handler.
return next.handle(context, request)

