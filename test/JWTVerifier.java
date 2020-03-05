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

    private static String SIGNED_JWT = "eyJ0eXAiOiJKV1QiLCJ6aXAiOiJOT05FIiwia2lkIjoid1UzaWZJSWFMT1VBUmVSQi9GRzZlTTFQMVFNPSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJ1c2VyLjg4IiwiY3RzIjoiT0FVVEgyX0dSQU5UX1NFVCIsImF1dGhfbGV2ZWwiOjAsImF1ZGl0VHJhY2tpbmdJZCI6IjJhNWI1ZWRkLTk0NjctNDI2Yi1iYTRjLTNjZTZiZDhkMWEwMi0xNTcyNDIiLCJpc3MiOiJodHRwOi8vYW02NTEuZXhhbXBsZS5jb206ODA4Ni9hbS9vYXV0aDIvZW1wbG95ZWVzIiwidG9rZW5OYW1lIjoiYWNjZXNzX3Rva2VuIiwidG9rZW5fdHlwZSI6IkJlYXJlciIsImF1dGhHcmFudElkIjoiN1hORlNlMDNRYndtM2E5T3BqOTN1OXl4UTJJLlFldWhWVmptaW5WSVZlQXo4dkpNZTVqOWhxSSIsImF1ZCI6Im15Q2xpZW50SUQiLCJuYmYiOjE1ODIyMzc5NDIsImdyYW50X3R5cGUiOiJwYXNzd29yZCIsInNjb3BlIjpbInVpZCIsIm1haWwiXSwiYXV0aF90aW1lIjoxNTgyMjM3OTQyLCJyZWFsbSI6Ii9lbXBsb3llZXMiLCJleHAiOjE1ODIyNDE1NDIsImlhdCI6MTU4MjIzNzk0MiwiZXhwaXJlc19pbiI6MzYwMCwianRpIjoiN1hORlNlMDNRYndtM2E5T3BqOTN1OXl4UTJJLmowMGdnY0tHOUdaVi1sVGgydmhJeWh6dnFYSSJ9.DF5Qi5rohOGLCU14bof3uAKKFlqK6xDBGLYi1r3TBYxK9sbzYqXwPqpWlCg3-ztp2vm1PhBCl8fpW3gIJ6EHOZFsxymxo-qSskgoiWqgBG1_OzL57x_Tg23eCktaE5jqjbVFEVijdHhBzuZhLJjAUseHtpzZMnfVJGWP1iPdaVis9RGcXpaDJInHqunZMtRoF1NOlMZs9duSbhXTHMyb_6on_YAMhJsQ3DaZwwUWXuhhNdg6LZ72y72N9XFf-6paHJ47Vu_U39oYm2Ry6vnirMlQv-rc6qGaz_dR3L_lKOxy-eFj2jBVmlwN8phhQXZzCd7u9VoeWWyDw8dfNMi_ww";

    public static void main(String[] args) {

        JWTVerifier jwtVerifier = new JWTVerifier();
        Claims claims = jwtVerifier.decodeJWT(SIGNED_JWT);

        System.out.println("Jwt Body: " + claims);
        System.out.println("Jwt Subject: " + claims.getSubject());
        System.out.println("Jwt IssuedAt: " + claims.getIssuedAt());
        System.out.println("Jwt Expiration: " + claims.getExpiration());
    }

    public Claims decodeJWT(String jwt) {

        //This line will throw an exception if it is not a signed JWS (as expected)
        return Jwts.parserBuilder()
                .setSigningKey(DatatypeConverter.parseHexBinary(HMAC_512_KEY))
                .build().parseClaimsJws(jwt).getBody();
    }

}