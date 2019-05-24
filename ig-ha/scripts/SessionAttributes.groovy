/*
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
 * Copyright 2016 Charan Mann
 * Portions Copyrighted 2016 ForgeRock AS
 *
 * OpenIG-SampleCompany: Created by Charan Mann on 4/124/16 , 10:22 AM.
 */

/*
 * Groovy script for set Session attributes
 *
 * This script requires these arguments: Session attributes to set
 */

logger.info("Setting Session attribute openIGHome: " + openIGHome)
session.put("openIGHome", openIGHome)

// Iterate over session attributes
for (sessionAttr in sessionAttributes.split()) {
    logger.info("Parsing attribute: " + sessionAttr)
    def (attrName, attrValue) = sessionAttr.tokenize(':')

    // Set the attributes in Session
    logger.info("Setting Session attribute: " + attrName + " ,value: " + attrValue)
    session.put(attrName, attrValue)
}

// Call the next handler. This returns when the request has been handled.
return next.handle(context, request)
