janusgraph-docker
==========

Building Docker Images
-----------------------------

Run `mvn clean install -Pjanusgraph-release -Dgpg.skip=true -DskipTests=true`.  
This command can be run from either the root of the JanusGraph repository (the parent of the 
janusgraph-docker directory) or the janusgraph-docker directory.  Running from the root of the 
repository is recommended.  Running from janusgraph-docker requires that JanusGraph's jars be
available on either Sonatype, Maven Central, or your local Maven repository (~/.m2/repository/) 
depending on whether you're building a SNAPSHOT or a release tag.

This command creates the following docker image:

* registry.prod.auction.local:5000/janusgraph-server

Environment Variables
---------------------

| VARIABLE                 | DESCRIPTION                                                             |
|--------------------------|-------------------------------------------------------------------------|
| `JANUS_CONFIG_FILE`      | The properties configuration file representing the graph                |
| `JANUS_STORAGE_HOSTNAME` | The hostname of the storage engine that the janus graph server is using |
| `JANUS_BIND_HOST`        | The hostname to bind the janus server to                                |


Running Against Dockerized Cassandra
------------------------------------

Start a cassandra container in the background

    docker run -d --name cassandra-node \
        -v /path/to/local/data/directory:/var/lib/cassandra \
        -e CASSANDRA_START_RPC=true \
        -p 7000:7000 -p 7001:7001 -p 7199:7199 -p 9042:9042 -p 9160:9160 \
        cassandra:3.9
        
Run a linked janus server container in the background

    docker run -d \
        --name janus-server \
        --link cassandra-node \
        -p 8182:8182 \
        registry.prod.auction.local:5000/janusgraph-server
