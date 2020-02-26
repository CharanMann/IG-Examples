import org.forgerock.http.oauth2.AccessTokenException

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
 * IG-Examples: Created by Charan Mann on 2/25/20 , 11:45 AM
 */

/*
 * Delegate resolver invoking different access resolvers
 *
 * This script requires these arguments: statelessAccessTokenResolver, scriptableAccessTokenResolver
 */

logger.info('Invoking Stateless Access Token Resolver')

def accessTokenInfo = statelessAccessTokenResolver.resolve(context, token)
        .thenCatchAsync({ e1 ->
            logger.info("Stateless Access Token Resolver failed: " + e1.getMessage())

            logger.info('Invoking Scriptable Access Token Resolver')
            accessTokenInfo = scriptableAccessTokenResolver.resolve(context, token)
                    .thenCatchAsync({ e2 ->
                        throw new AccessTokenException("Scriptable Access Token Resolver failed: " + e2.getMessage())
                    })
        })

return accessTokenInfo













