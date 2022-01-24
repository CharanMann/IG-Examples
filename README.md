# IG-Examples

Various IG Examples <br />

Disclaimer of Liability :
=========================
The sample code described herein is provided on an "as is" basis, without warranty of any kind, to the fullest extent
permitted by law. ForgeRock does not warrant or guarantee the individual success developers may have in implementing the
sample code on their development platforms or in production configurations.

ForgeRock does not warrant, guarantee or make any representations regarding the use, results of use, accuracy,
timeliness or completeness of any data or information relating to the sample code. ForgeRock disclaims all warranties,
expressed or implied, and in particular, disclaims all warranties of merchantability, and warranties related to the
code, or any service or software related thereto.

ForgeRock shall not be liable for any direct, indirect or consequential damages or costs of any type arising out of any
action taken by you or others related to the sample code.

Pre-requisites :
================

* Versions used for this project: IG 5.0, AM 5.1.1

1. AM has been installed and configured. Example routes in this example use AM realm '/employees'.

IG Configuration:
=====================

1.

IG Examples testing:
=========================

1. IG as SP with ADFS as IdP:
    * Enabled Route(s): 10-adfs-docapp.json
    * ADFS configuration: Check ADFS folder
    * Test1: Initiate SAML
      flow: https://docapp-ig.example.net:8443/saml/SPInitiatedSSO?RelayState=${urlEncodeQueryParameterNameOrValue(contexts.router.originalUri)}&binding=HTTP-POST&NameIDFormat=transient
2. IG split():
    * Enabled Route(s): 20-split.json
    * Test1: http://ig5.example.com:9000/splitTest/t1/t2/t3. Nothing returned in property splitting due
      to [OPENIG-1999](https://bugster.forgerock.org/jira/browse/OPENIG-1999)
3. Throttle:
    * Enabled Route(s): 30-tx-throttle.json
    * Test:
    ```
    $ curl -v -s -I -L -H 'action: throttle' http://ig5.example.com:9000/history/emp1/\[01-10000\] > throttleTest.txt 2>&1
    $ grep "< HTTP/1.1" throttleTest.txt | sort | uniq -c
      6 < HTTP/1.1 404 Not Found
    9994 < HTTP/1.1 429 Too Many Requests
    ```
4. OIDC RP:
    * Enabled Route(s): 30-tx-throttle.json
    * Test: http://ig55.example.com:9292/home/id_token
5. OpenIG-OpenAM PEP for REST APIs
    * Enabled Route(s): 06-pep-apis.json
    * Disabled Route(s): None
    * Test1: Get TxHistory for all users: curl -X GET -H "X-OpenAM-Username: empAdmin" -H "X-OpenAM-Password:
      Passw0rd" "http://apis-ig.example.net:9002/txHistory/all". Result: Should return transaction history for all users
    * Test2: Get TxHistory for all users using unauthorized account: curl -X GET -H "X-OpenAM-Username: emp1" -H "
      X-OpenAM-Password: Passw0rd" "http://apis-ig.example.net:9002/txHistory/all". Result: Should return authorization
      failed.
6. OpenIG-OAuth2 RS:
    * Enabled Route(s): 10-oauth2rs-apis.json
    * Disabled Route(s): None
    * Test1: Acquire OAuth Access token by using OAuth Resource Owner Password Credentials flow : curl -X POST -H "
      Authorization: BASIC ZW1wbG95ZWVBcHA6cGFzc3dvcmQ=" -H "Content-Type: application/x-www-form-urlencoded" -d '
      grant_type=password&username=emp1&password=Passw0rd&scope=uid
      mail' "http://openam.example.com:18080/openam/oauth2/employees/access_token" <br />
      Get TxHistory for specified user: curl -X GET -H "Authorization: Bearer
      a04b0596-9ed7-4e7e-bd36-4008d901bcd2" "http://apis-ig.example.net:9002/history/emp1". Result: Should return
      transaction history for specified user.
    * Test2: Get TxHistory for specified user using invalid OAuth Access token: curl -X GET -H "Authorization: Bearer
      a04b0596-9ed7-4e7e-bd36-qqqqqqqq" "http://apis-ig.example.net:9002/history/emp1" -v. Result: Should return
      error: "The access token provided is expired, revoked, malformed, or invalid for other reasons.".
7. isTokenValid AM legacy:
    * Enabled Route(s): 20-am-isTokenValid.json
    * Disabled Route(s): None
    * Test1: Legacy call with valid SSO token : curl -X POST http://ig55.example.com:9292/openam/identity/isTokenValid
      -H 'Content-Type: application/x-www-form-urlencoded' -d 'tokenid=<SSOTokenId>'. <br />
      boolean=true

* * *

The contents of this file are subject to the terms of the Common Development and Distribution License (the License). You
may not use this file except in compliance with the License.

You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the specific language governing
permission and limitations under the License.

When distributing Covered Software, include this CDDL Header Notice in each file and include the License file at
legal/CDDLv1.0.txt. If applicable, add the following below the CDDL Header, with the fields enclosed by brackets []
replaced by your own identifying information: "Portions copyright [year] [name of copyright owner]".

Copyright 2018 ForgeRock AS.

Portions Copyrighted 2018 Charan Mann
