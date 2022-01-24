/*
 * Copyright © 2016 ForgeRock, AS.
 *
 * This is unsupported code made available by ForgeRock for community development subject to the license detailed below.
 * The code is provided on an "as is" basis, without warranty of any kind, to the fullest extent permitted by law.
 *
 * ForgeRock does not warrant or guarantee the individual success developers may have in implementing the code on their
 * development platforms or in production configurations.
 *
 * ForgeRock does not warrant, guarantee or make any representations regarding the use, results of use, accuracy, timeliness
 * or completeness of any data or information relating to the alpha release of unsupported code. ForgeRock disclaims all
 * warranties, expressed or implied, and in particular, disclaims all warranties of merchantability, and warranties related
 * to the code, or any service or software related thereto.
 *
 * ForgeRock shall not be liable for any direct, indirect or consequential damages or costs of any type arising out of any
 * action taken by you or others related to the code.
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License (the License).
 * You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the License at https://forgerock.org/cddlv1-0/. See the License for the specific language governing
 * permission and limitations under the License.
 *
 * Portions Copyrighted 2016 Charan Mann
 *
 * OpenIG-SampleCompany: Created by Charan Mann on 4/1/16 , 6:12 AM.
 */

/*
 * Groovy script for OpenAM basic authentication
 *
 * This script requires these arguments: userId, password, openamUrl
 */
import org.forgerock.http.protocol.Request
import org.forgerock.http.protocol.Response
import org.forgerock.http.protocol.Status
import org.forgerock.util.AsyncFunction

import static org.forgerock.http.protocol.Response.newResponsePromise

/**
 * Creates unauthorized error
 */
def getUnauthorizedError() {
    logger.info("Returning Unauthorized error")
    Response errResponse = new Response(Status.UNAUTHORIZED)
    errResponse.entity.json = [code: 401, reason: "Unauthorized", message: "Authentication Failed"];

    // Need to wrap the response object in a promise
    return newResponsePromise(errResponse)
}

/**
 * Set tokenId in request header and call next handler
 * @param tokenId
 */
def callNextHandler(tokenId) {
    // Set the tokenId in attributes
    attributes.tokenId = tokenId

    // Call the next handler. This returns when the request has been handled.
    return next.handle(context, request)
}

def performOpenAMAuthn() {

    String userId = request.headers['X-OpenAM-Username'].values[0]
    String password = request.headers['X-OpenAM-Password'].values[0]

    // Invoke OpenAM authentication
    Request authenticate = new Request()
    authenticate.uri = "${openamUrl}/json/realms/root/realms/${realm}/authenticate"
    authenticate.headers.put('X-OpenAM-Username', userId)
    authenticate.headers.put('X-OpenAM-Password', password)
    authenticate.method = "POST"

    logger.info("Authenticating user: ${userId} ,using AM endpoint: ${authenticate.uri}")

    return http.send(context, authenticate)
    // when there will be a response available ...
    // Need to use 'thenAsync' instead of 'then' because we'll return another promise, not directly a response
            .thenAsync({ authenticateResponse ->
                logger.info("User Authentication Response : ${authenticateResponse.entity.json}")
                data = authenticateResponse.entity.json
                def tokenId = data['tokenId']

                // If cookie validation succeeds and has valid uid
                if (null != tokenId) {
                    logger.info("User Authentication Successful, Invoking next handler")

                    return callNextHandler(tokenId)
                } else {
                    logger.info("User Authentication Failed")
                    // In case of any failure like authentication failure, server exception etc, return UNAUTHORIZED status. This can be modified to return specific response status for different failures.
                    // No need to call next.handle() as we want to terminate handing here
                    return getUnauthorizedError()
                }
            } as AsyncFunction)
}

// Check if OpenAM session cookie is present
if (null != request.cookies['iPlanetDirectoryPro']) {
    String openAMCookie = request.cookies['iPlanetDirectoryPro'][0].value

    // Perform cookie validation and get uid
    logger.info("iPlanetDirectoryPro cookie found, performing validation")

    Request validation = new Request()
    validation.uri = "${openamUrl}/json/sessions?_action=validate"
    validation.method = "POST"
    validation.entity.json = ['tokenId': openAMCookie]

    return http.send(context, validation)
    // when there will be a response available ...
    // Need to use 'thenAsync' instead of 'then' because we'll return another promise, not directly a response
            .thenAsync({ validationResponse ->
                logger.info("Token Validation Response : ${validationResponse.entity.json}")
                def data = validationResponse.entity.json
                def isTokenValid = data['valid']

                // If cookie validation succeeds
                if (isTokenValid) {
                    logger.info("Valid OpenAM session, skipping authentication")

                    return callNextHandler(openAMCookie)
                } else {
                    logger.info("Invalid OpenAM session, Proceeding to perform OpenAM Basic Authentication")

                    return performOpenAMAuthn()
                }
            } as AsyncFunction)
} else {
    return performOpenAMAuthn()
}


