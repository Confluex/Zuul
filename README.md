[![Build Status](https://secure.travis-ci.org/mcantrell/Zuul.png?branch=master)](http://travis-ci.org/mcantrell/Zuul)

# Zuul the Gatekeeper of Goze

Web application for securely managing and accessing configuration for webapps.

Project Goals:

* Asymmetrical encryption of sensitive fields (assuming I can enhance the Jasypt project)
 - shared private key if not
* RESTful interface for application to grab settings from a URL
 - Java Properties File for use with something like Spring's URLResource and PropertyPlaceholderConfigurer
 - JSON and XML
 - Maybe .NET web.configs
* Separation of configuration by environment (dev/qa/prod)
* OpenID authentication with popular providers
* Provision API keys and authorization for applications
* Client APIs with caching for offline development or Zuul down status :-)


# License

   Copyright 2012 Mike Cantrell

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License. 
   
   