# IG-Examples 

Various IG Examples <br />

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
   * Test1: Initiate SAML flow: https://docapp-ig.example.net:8443/saml/SPInitiatedSSO?RelayState=${urlEncodeQueryParameterNameOrValue(contexts.router.originalUri)}&binding=HTTP-POST&NameIDFormat=transient
2. IG split():
   * Enabled Route(s): 20-split.json
   * Test1: http://ig5.example.com:9000/splitTest/t1/t2/t3. Nothing returned in property splitting due to [OPENIG-1999](https://bugster.forgerock.org/jira/browse/OPENIG-1999)
3. Throttle:
   * Enabled Route(s): 30-tx-throttle.json
   * Test:
    ```
    $ curl -v -s -I -L -H 'action: throttle' http://ig5.example.com:9000/history/emp1/\[01-10000\] > throttleTest.txt 2>&1
    $ grep "< HTTP/1.1" throttleTest.txt | sort | uniq -c
      6 < HTTP/1.1 404 Not Found
    9994 < HTTP/1.1 429 Too Many Requests
    ```


* * *

Copyright © 2017 ForgeRock, AS.

The contents of this file are subject to the terms of the Common Development and
Distribution License (the License). You may not use this file except in compliance with the
License.

You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
specific language governing permission and limitations under the License.

When distributing Covered Software, include this CDDL Header Notice in each file and include
the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
Header, with the fields enclosed by brackets [] replaced by your own identifying
information: "Portions copyright [year] [name of copyright owner]".

Portions Copyrighted 2017 Charan Mann
