janusgraph-docker
==========

Objective
-----------------------------

1. Start a Docker image to run the Cassandra database.
2. Build Janus Graph from source, create a docker image, and run the Janus Graph server (stacked on Cassandra) in a second docker image.

Prerequisites
-----------------------------
1. Ensure Docker is installed on your host.
2. Ensure Oracle Java jdk8 is installed on your host.
3. Ensure Maven is installed on your host (sometimes but not always bundled with jdk8)

Building Docker Image for Janus Graph
-----------------------------

During app development we will build Janus Graph from source.you

Generally speaking we are using the 'tenx' branch in bitbucket.

Make sure there is a 'janusgraph-docker' directory just below the git repository root. If it is missing, you have checked out the wrong branch.

Call Maven to build the Janus Graph db from source:

`mvn clean install -Pjanusgraph-release -Dgpg.skip=true -DskipTests=true`

Maven can be run from either the root of the JanusGraph repository (the parent of the 
janusgraph-docker directory) or the janusgraph-docker directory.  Running from the root of the 
repository is recommended.  Running from janusgraph-docker requires that JanusGraph's jars be
available on either Sonatype, Maven Central, or your local Maven repository (~/.m2/repository/) 
depending on whether you're building a SNAPSHOT or a release tag.

When Maven succeeds to build JanusGraph, it should create a docker image with this name: `registry.prod.auction.local:5000/janusgraph-server`

(Optional) The existence of this docker image you can confirm by running `docker images`:

    host-123:janusgraph username$ docker images
    REPOSITORY                                           TAG                 IMAGE ID            CREATED             SIZE
    registry.prod.auction.local:5000/janusgraph-server   0.1.0-SNAPSHOT      407e8f2b24d9        About an hour ago   969 MB
    registry.prod.auction.local:5000/janusgraph-server   latest              407e8f2b24d9        About an hour ago   969 MB
    <none>                                               <none>              18f4254eb0dc        3 hours ago         735 MB
    <none>                                               <none>              ab9441b77783        3 hours ago         758 MB
    <none>                                               <none>              207183181233        7 days ago          129 MB
    cassandra                                            3.9                 2da6dfd682cb        4 weeks ago         386 MB

Environment Variables
---------------------

Setting these environment variables is optional; the default values should work in most cases.

| VARIABLE                 | DESCRIPTION                                                             |
|--------------------------|-------------------------------------------------------------------------|
| `JANUS_CONFIG_FILE`      | The properties configuration file representing the graph                |
| `JANUS_STORAGE_HOSTNAME` | The hostname of the storage engine that the janus graph server is using |
| `JANUS_BIND_HOST`        | The hostname to bind the janus server to                                |


Running Against Dockerized Cassandra
------------------------------------

Start a cassandra container in the background. You *must* change the first path after -v `/path/to/local/data/directory` 
to the name of a directory which will be used to store the Cassandra persistent data. For example (on a Mac OS host),
`/Users/username/janus_graph_data_dir` works. Docker will ensure this directory is created on the host machine and
is mounted read-write inside the docker container (which is itself a virtual machine running Linux).

    docker run -d --name cassandra-node \
        -v /Users/username/janus_graph_data_dir:/var/lib/cassandra \
        -e CASSANDRA_START_RPC=true \
        -p 7000:7000 -p 7001:7001 -p 7199:7199 -p 9042:9042 -p 9160:9160 \
        cassandra:3.9
        
Run a linked janus server container in the background

    docker run -d \
        --name janus-server \
        --link cassandra-node \
        -p 8182:8182 \
        registry.prod.auction.local:5000/janusgraph-server

(Optional) confirm that both containers are running by calling `docker container list`:

    host-123:janusgraph userid$ docker container list
    CONTAINER ID        IMAGE                                                COMMAND                  CREATED             STATUS              PORTS                                                                                                      NAMES
    2964ac793db5        registry.prod.auction.local:5000/janusgraph-server   "./docker-entrypoi..."   About an hour ago   Up 17 minutes       0.0.0.0:8182->8182/tcp                                                                                     janus-server
    20810658870d        cassandra:3.9                                        "/docker-entrypoin..."   About an hour ago   Up 55 minutes       0.0.0.0:7000-7001->7000-7001/tcp, 0.0.0.0:7199->7199/tcp, 0.0.0.0:9042->9042/tcp, 0.0.0.0:9160->9160/tcp   cassandra-node

