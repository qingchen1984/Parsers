# Parsers
Travis Build - [![Build Status](https://travis-ci.org/thakurvivek/Parsers.svg?branch=master)](https://travis-ci.org/thakurvivek/Parsers)

Local Build params from behind proxy: 
unix:
./gradlew build -Dhttp.proxyHost=$PROXY_HOST -Dhttp.proxyPort=$PROXY_PORT -Dhttps.proxyHost=$PROXY_HOST -Dhttps.proxyPort=$PROXY_PORT -Dhttp.nonProxyHosts="localhost|artifactory.*.com" -Dhttps.nonProxyHosts="localhost|artifactory.*.com"

win:
./gradlew build -Dhttp.proxyHost=%PROXY_HOST% -Dhttp.proxyPort=%PROXY_PORT% -Dhttps.proxyHost=%PROXY_HOST% -Dhttps.proxyPort=%PROXY_PORT% -Dhttp.nonProxyHosts="localhost|artifactory.*.com" -Dhttps.nonProxyHosts="localhost|artifactory.*.com"
