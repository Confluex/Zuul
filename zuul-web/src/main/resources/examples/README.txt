These files contain examples of externalized settings for your particular environment. Copy them somewhere into the
classpath of your application server and make required modifications:

    # permanent
    cp zuul-data-config.properties $TOMCAT_HOME/lib

    -or-

    # could be overwritten on accident if you aren't careful
    cp zuul-data-config.properties ../


More Info:

https://github.com/mcantrell/zuul/wiki/Installation
