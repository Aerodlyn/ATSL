#!/bin/bash

cd src/main/java/io/github/aerodlyn/atsl/

java -jar /usr/local/lib/antlr-4.7.1-complete.jar -visitor ATSL.g4
