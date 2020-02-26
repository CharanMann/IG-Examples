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
 * IG-Examples: Created by Charan Mann on 2/25/20 , 5:14 PM.
 */

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class JWTBuilder {

    public static void main(String[] args) throws Exception {

        FileInputStream is = new FileInputStream("./ig/config/secrets/IG_keystore.p12");
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        String password = "password";
        char[] passwd = password.toCharArray();
        keystore.load(is, passwd);
        String alias = "test-key";
        Key key = keystore.getKey(alias, passwd);

        System.out.println(generateJwtToken(((PrivateKey)key)));
    }

    public static String generateJwtToken(PrivateKey privateKey) {
        String token = Jwts.builder().setSubject("testUser1")
                .setExpiration(new Date(2021, 1, 1))
                .setIssuer("idp@example.com")
                .setAudience("client1")
                .setIssuedAt(new Date())
                .claim("scope", new String[]{"uid", "mail"})
                .setId("1223243434123421341")
                // RS256 with privateKey
                .signWith(SignatureAlgorithm.RS256, privateKey).compact();
        return token;
    }

}
