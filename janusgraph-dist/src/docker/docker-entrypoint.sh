#!/bin/bash

# default command argument allows easily changing default behavior of container
if [ "$1" = 'janus' ]; then

    # replace all instances of ${ENV_VARIABLE} with their value in the configuration files
    perl -pi.bak -e 's/\$\{([_A-Z]+)\}/$ENV{$1}/e' gremlin-server.yaml
    perl -pi.bak -e 's/\$\{([_A-Z]+)\}/$ENV{$1}/e' janusgraph-cassandra.properties

    exec janus/bin/gremlin-server.sh /root/gremlin-server.yaml
fi

exec "$@"
