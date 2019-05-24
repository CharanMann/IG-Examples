/*
 * Copyright © 2018 ForgeRock, AS.
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
 * Portions Copyrighted 2018 Charan Mann
 *
 * IG-Examples: Created by Charan Mann on 4/5/18 , 8:37 PM.
 */


import io.jsonwebtoken.Jwts

import static io.jsonwebtoken.SignatureAlgorithm.HS256

def key = "2193872103019283092174917"

def token = Jwts.builder()
        .setSubject("$subject")
        .signWith(HS256, key)
        .compact()

def name = Jwts.parser()
        .setSigningKey(key)
        .parseClaimsJws(token)
        .getBody()
        .getSubject()

logger.info("Key: $key")
logger.info("Token: $token")
logger.info("Name: $name")

attributes.jwtToken = token

// Call the next handler. This returns when the request has been handled.
return next.handle(context, request)


