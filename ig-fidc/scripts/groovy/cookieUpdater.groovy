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
 * IG-Examples:: Created by Charan Mann on 12/22/20 , 11:21 AM.
 */

/*
 * Updates specified cookie's domain value
 *
 * This script requires these arguments: cookieName, domain
 */

import org.forgerock.http.header.SetCookieHeader
import org.forgerock.http.protocol.Cookie
import org.forgerock.http.protocol.Response

next.handle(context, request).thenOnResult({ Response resp ->

    List<Cookie> resCookies = SetCookieHeader.valueOf(resp).getCookies()
    logger.info("Searching for cookie: ${cookieName} in response cookies: " + resCookies)

    for (cookie in resCookies) {
        if (cookie.name == cookieName) {
            // If matching cookie is found, then update the domain
            logger.info("Matching SSO cookie ${cookie} found, changing domain to ${domain}" )
            cookie.setDomain(domain)

            // Update the cookie headers
            resp.headers['Set-Cookie'] = new SetCookieHeader(resCookies)
        }
    }
})

