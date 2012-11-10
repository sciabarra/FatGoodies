#!/bin/bash
cp=$(ls lib/csdt-client-*)
for i in lib/*.jar ; do cp="$cp:$i" ; done
java -cp "$cp" com.fatwire.csdt.client.main.CSDT "$@" 
