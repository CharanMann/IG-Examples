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
 * IG-Examples: Created by Charan Mann on 12/2/19 , 12:06 PM.
 */

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.xml.bind.DatatypeConverter;

/**
 * Sample JwT Verifier using symmetric keys
 */
public class JWTVerifier {

    private static String HMAC_512_KEY = "F96C65B430BE65516085D9B36C5DA72DB99568EEE30C75BD1FEF5BF18A8C68F58F0C6A1FCD0E1645E76EA284CF75B61A9D487D8FC53B4C8F20D38E332FB7D2DD";

    private static String SIGNED_JWT = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJlbWFpbCI6InVzZXIuNjY2QGV4YW1wbGUuY29tIiwiZW1wbG95ZWVOdW1iZXIiOiI2NjYifQ.PiGu7vSd8JKK9ImEB6dHmbycZ5OgFbjAmAehW12VVfVXjaTcKoU8U0fUqqAFvv9p-uS05xmpG-bGH21plpIZFA";

    public static void main(String[] args) {

        JWTVerifier jwtVerifier = new JWTVerifier();
        Claims claims = jwtVerifier.decodeJWT(SIGNED_JWT);

        System.out.println("Jwt Body: " + claims);
    }

    public Claims decodeJWT(String jwt) {

        //This line will throw an exception if it is not a signed JWS (as expected)
        return Jwts.parser()
                .setSigningKey(DatatypeConverter.parseHexBinary(HMAC_512_KEY))
                .parseClaimsJws(jwt).getBody();
    }

}