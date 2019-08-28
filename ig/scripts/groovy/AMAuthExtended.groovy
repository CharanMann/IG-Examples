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
 * IG-Examples: Created by Charan Mann on 2019-08-28 , 10:01.
 */
import org.forgerock.http.protocol.Request
import org.forgerock.http.protocol.Response
import org.forgerock.http.protocol.Status
import org.forgerock.util.AsyncFunction
import org.forgerock.util.promise.Promises

import static org.forgerock.http.protocol.Response.newResponsePromise

/*
 * Note: Latest IG versions such as version 5+ provide some to this functionality OOTB: https://backstage.forgerock.com/docs/ig/6.5/reference/#SingleSignOnFilter
 *
 * This Groovy script performs following functions:
 * 1. Redirects user to AM login URL in case invalid or no AM cookie is present in the request
 * 2. (Optional) Retrieves specified user profile attributes and sets as HTTP headers
 * 3. (Optional) Retrieves specified session attributes and sets as HTTP headers
 *
 * This script requires these arguments: amUrl(String), amAuthUrl(String), profileAttributes([String]), sessionAttributes([String]).
 * profileAttributes and sessionAttributes are optional arguments.
 *
 * Sample:
 *       {
          "comment": "Scripted filter for AM Authentication redirect",
          "name": "AM Authentication redirect (and Attributes retrieval)",
          "type": "ScriptableFilter",
          "config": {
            "type": "application/x-groovy",
            "file": "AMAuthExtended.groovy",
            "args": {
              "amUrl": "${frConfigs.amUrl}",
              "amAuthUrl": "${frConfigs.amUrl}?realm=${frConfigs.amRealm}",
              "profileAttributes": [
                "uid",
                "mail"
              ],
              "sessionAttributes": [
                "am.protected.user.employeeNumber"
              ]
            }
          },
          "capture": "all"
        }
 */

/**
 * Creates Redirect response
 */
def getRedirectResponse() {
    logger.info("Returning redirect to AM Login URL")
    Response redirectResponse = new Response(Status.FOUND)
    redirectResponse.entity.json = [code: 302, message: "Redirect to AM Login URL"];
    String redirectURI = "${amAuthUrl}&goto=${contexts.router.originalUri.string}"
    redirectResponse.headers["Location"] = redirectURI

    // Need to wrap the response object in a promise
    return newResponsePromise(redirectResponse)
}

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

// Check if AM session cookie is present
if (null != request.cookies['iPlanetDirectoryPro']) {
    String amCookie = request.cookies['iPlanetDirectoryPro'][0].value

    // Perform cookie validation and get uid
    logger.info("iPlanetDirectoryPro cookie found, performing validation")

    Request validation = new Request()
    validation.uri = "${amUrl}/json/sessions?tokenId=${amCookie}&_action=validate"
    validation.method = "POST"
    validation.headers.put("Accept-API-Version", "resource=1.1")
    validation.headers.put("Content-Type", "application/json")

    return http.send(context, validation)
    // when there will be a response available ...
    // Need to use 'thenAsync' instead of 'then' because we'll return another promise, not directly a response
            .thenAsync({ validationResponse ->
                logger.info("Token Validation Response : ${validationResponse.entity.json}")
                def data = validationResponse.entity.json
                def isTokenValid = data['valid']
                def uid = data['uid']

                // If cookie validation succeeds and has valid uid
                if (isTokenValid && null != uid) {

                    def profileAttributesPresent = binding.hasVariable("profileAttributes") && !(profileAttributes.empty)
                    def sessionAttributesPresent = binding.hasVariable("sessionAttributes") && !(sessionAttributes.empty)

                    if (!profileAttributesPresent && !sessionAttributesPresent) {
                        // In case no profile or session attributes retrieval is required.
                        logger.info("No profile or session attributes retrieval required, invoking next handler")
                        // Call the next handler
                        return next.handle(context, request)
                    }

                    def pResult
                    def sResult

                    // Process profile attributes
                    if (profileAttributesPresent) {
                        // Retrieving user profile attributes
                        logger.info("Retrieving user profile attributes: ${profileAttributes} for user: ${uid}")
                        Request pAttributes = new Request()
                        pAttributes.uri = "${amUrl}/json/${frConfigs.amRealm}/users/${uid}"
                        pAttributes.headers.put("iPlanetDirectoryPro", amCookie)
                        pAttributes.method = "GET"
                        def pAttrMap = [:]

                        pResult = http.send(context, pAttributes)
                                .then { pAttributesResponse ->
                                    profileAttributes.each { pName ->
                                        def pAttrs = pAttributesResponse.entity.json
                                        def pValues = pAttrs[pName]
                                        logger.info("Retrieved user profile attribute values: ${pValues} for attribute name: ${pName}")

                                        // Check if some attribute is present for specified name
                                        if (null != pValues) {
                                            def pAttrValue = pValues[0]

                                            logger.info("Adding following entry in profile attribute map-> ${pName} : ${pAttrValue}")
                                            pAttrMap.put(pName, pAttrValue)
                                        }
                                    }

                                    pAttributesResponse.close()
                                    return pAttrMap
                                }
                    }

                    // Process session attributes
                    if (sessionAttributesPresent) {
                        // Retrieving session attributes
                        logger.info("Retrieving session attributes: ${sessionAttributes} for user: ${uid}")
                        Request sAttributes = new Request()
                        sAttributes.uri = "${amUrl}/json/sessions/?_action=getSessionProperties"
                        sAttributes.headers.put("iPlanetDirectoryPro", amCookie)
                        sAttributes.headers.put("Content-Type", "application/json")
                        sAttributes.headers.put("Accept-API-Version", "resource=3.1, protocol=1.0")
                        sAttributes.headers.put("Accept", "application/json")
                        sAttributes.method = "POST"
                        def sAttrMap = [:]

                        // Create session attribute string
                        def sAttrString
                        sessionAttributes.each { sName ->
                            sAttrString = sName + ','
                        }

                        if (null != sAttrString) {
                            sAttrString = sAttrString.reverse().drop(1).reverse()
                            sAttributes.entity.json = [properties: [sAttrString]]
                        }

                        sResult = http.send(context, sAttributes)
                                .then { sAttributesResponse ->
                                    sessionAttributes.each { sName ->
                                        def sAttrs = sAttributesResponse.entity.json
                                        def sValues = sAttrs[sName]
                                        logger.info("Retrieved session attribute values: ${sValues} for attribute name: ${sName}")

                                        // Check if some attribute is present for specified name
                                        if (null != sValues) {
                                            def sAttrValue = sValues

                                            logger.info("Adding following entry in session attribute map-> ${sName} : ${sAttrValue}")
                                            sAttrMap.put(sName, sAttrValue)
                                        }
                                    }

                                    sAttributesResponse.close()
                                    return sAttrMap
                                }
                    }

                    // Create list of promises
                    List promiseList = [];
                    if (profileAttributesPresent) {
                        promiseList.add(pResult)
                    }

                    if (sessionAttributesPresent) {
                        promiseList.add(sResult)
                    }

                    // When both user and session profile results are available, call next handler
                    return Promises.when(promiseList)
                            .thenAsync({ attrList ->
                                // attrList is List<Map<String, String>>
                                attrList.each { attrMap ->
                                    attrMap.each { attrName, attrValue ->

                                        // Set the attributes in headers of the original request
                                        // Security tip: These header values can be encrypted by a symmetric key shared between OpenIG and protected application
                                        logger.info("Setting HTTP header: ${attrName}, value: ${attrValue}")
                                        request.headers.add(attrName, attrValue)
                                    }
                                }

                                // Call next handler with updated request
                                return next.handle(context, request)
                            } as AsyncFunction)
                }

                logger.info("Token validation failed")
                return getRedirectResponse()
            } as AsyncFunction)
} else {
    logger.info("No iPlanetDirectoryPro cookie present")
    return getRedirectResponse()
}