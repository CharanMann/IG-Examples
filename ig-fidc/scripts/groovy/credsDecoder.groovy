/*
 * Copyright © 2023 ForgeRock, AS.
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
 * IG-Examples: Created by Charan Mann on 01/06/23 , 11:21 AM.
 */

/*
 * Retrieves credentials from the Authorization header
 *
 */
String authorizationHeader = request.getHeaders().getFirst("Authorization");
String authzPrefix = "Bearer"
String oauthAuthPrefix = "Basic"

if (authorizationHeader == null && !authorizationHeader.startsWithIgnoreCase("Bearer ")) {
    // No credentials provided, reply that they are needed.
    Response response = new Response(Status.UNAUTHORIZED);
    response.getHeaders().put("WWW-Authenticate", "Basic realm=\"" + realm + "\"");
    return newResultPromise(response);
}

updatedAuthorizationHeader = authorizationHeader.replace(authzPrefix, oauthAuthPrefix)
logger.info("Updated Authorizaion header: ${updatedAuthorizationHeader}")

logger.info("Attribute context before: " + attributes)
attributes.updatedAuthorizationHeader = updatedAuthorizationHeader
logger.info("Attribute context after: " + attributes)

//authorizationHeader = authorizationHeader.substring(authzPrefix.length())
//logger.info("Original Authorizaion header: ${authorizationHeader}")
//
//authorizationHeaderDecoded = new String(Base64.decode(authorizationHeader.getBytes(Charset.defaultCharset())), Charset.defaultCharset())
//logger.info("Decoded Authorizaion header: ${authorizationHeaderDecoded}")
//String[] creds = authorizationHeaderDecoded.split(':')
//
//logger.info("String array: ${creds}")
//
//// Set creds in attributes
//attributes.creds = [:]
//attributes.creds.username = creds[0]
//attributes.creds.password = creds[1]
//
//logger.info("username: " + attributes.creds.username)
//logger.info("password: " + attributes.creds.password)

return next.handle(context, request);