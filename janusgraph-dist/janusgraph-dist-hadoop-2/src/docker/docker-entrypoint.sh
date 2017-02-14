#!/bin/bash

# default command argument allows easily changing default behavior of container
if [ "$1" = 'janus-server' ]; then

    # copy the template
    cp /root/janusgraph-template.properties /root/janusgraph.properties

    # replace all instances of ${ENV_VARIABLE} with their value in the configuration files
    perl -pi.bak -e 's/\$\{([_A-Z]+)\}/$ENV{$1}/e' /root/gremlin-server.yaml
    perl -pi.bak -e 's/\$\{([_A-Z]+)\}/$ENV{$1}/e' /root/janusgraph.properties

    /root/append-storage.sh
    /root/append-index.sh

    exec /root/janus-server/bin/gremlin-server.sh /root/gremlin-server.yaml
fi

exec "$@"
