janusgraph-dist
==========

Building zip archives
-----------------------------

Run `mvn clean install -Pjanusgraph-release -Dgpg.skip=true
-DskipTests=true`.  This command can be run from either the root of
the JanusGraph repository (the parent of the janusgraph-dist directory) or the
janusgraph-dist directory.  Running from the root of the repository is
recommended.  Running from janusgraph-dist requries that JanusGraph's jars be
available on either Sonatype, Maven Central, or your local Maven
repository (~/.m2/repository/) depending on whether you're building a
SNAPSHOT or a release tag.

This command writes one archive:

* janusgraph-dist/janusgraph-dist-hadoop-2/target/janusgraph-$VERSION-hadoop2.zip

It's also possible to leave off the `-DskipTests=true`.  However, in
the absence of `-DskipTests=true`, the -Pjanusgraph-release argument
causes janusgraph-dist to run several automated integration tests of the
zipfiles and the script files they contain.  These tests require unzip
and expect, and they'll start and stop Cassandra, ES, and HBase in the
course of their execution.

Building documentation
----------------------

To convert the AsciiDoc sources in $JANUSGRAPH_REPO_ROOT/docs/ to chunked
and single-page HTML, run `mvn package -pl janusgraph-dist -am
-DskipTests=true -Dgpg.skip=true` from the directory containing
janusgraph-dist.  If the JanusGraph artifacts are already installed in the local
Maven repo from previous invocations of `mvn install` in the root of
the clone, then `cd janusgraph-dist && mvn package` is also sufficient.

The documentation output appears in:

* janusgraph-dist/target/docs/chunk/
* janusgraph-dist/target/docs/single/

Building deb/rpm packages
-------------------------

Requires:

* a platform that can run shell scripts (e.g. Linux, Mac OS X, or
  Windows with Cygwin)

* the Aurelius public package GPG signing key

Run `mvn -N -Ppkg-tools install` in the janusgraph-dist module.  This writes
three folders to the root of the janusgraph repository:

* debian
* pkgcommon
* redhat

The debian and redhat folders contain platform-specific packaging
conttrol and payoad files.  The pkgcommon folder contains shared
payload and helper scripts.

To build the .deb and .rpm packages:

* (cd to the repository root)
* `pkgcommon/bin/build-all.sh`

To delete the packaging scripts from the root of the repository, run
`mvn -N -Ppkg-tools clean` from the janusgraph-dist module.

Building Docker Image for Janus Graph
-----------------------------

During app development we will build Janus Graph from source.

Generally speaking we are using the 'tenx' branch in bitbucket.

Before building the docker image, make sure to build the zip archive from above.

Call Maven to build the Janus Graph Docker Image ignoring the janusgraph-dist-hadoop-2 submodule

`mvn -pl "\!janusgraph-dist-hadoop-2" -Pjanusgraph-docker -DskipTests=true docker:build`

When Maven succeeds to build JanusGraph, it should create a docker image with this name: `tenxtech/janusgraph`

(Optional) The existence of this docker image you can confirm by running `docker images`:

    host-123:janusgraph username$ docker images
    REPOSITORY                                           TAG                 IMAGE ID            CREATED             SIZE
    tenxtech/janusgraph                                  0.1.0-SNAPSHOT      407e8f2b24d9        About an hour ago   969 MB
    tenxtech/janusgraph                                  latest              407e8f2b24d9        About an hour ago   969 MB
    cassandra                                            3.9                 2da6dfd682cb        4 weeks ago         386 MB
    
    
Call Maven to push the Janus Graph Docker Image to docker-hub

`mvn -pl "\!janusgraph-dist-hadoop-2" -Pjanusgraph-docker -DskipTests=true docker:push`
    
To do this, you must have a docker-hub <servers> section in your ~/.m2/settings.xml file:

```
<servers>
    <server>
    <id>docker-hub</id>
    <username>{docker-hub-username}</username>
    <password>{docker-hub-password}</password>
    <configuration>
        <email>{docker-hub-email}</email>
    </configuration>
    </server>
</servers>
```

You'll also need write access to the tenxtech/janusgraph docker hub image

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
```/Users/`whoami`/janus_graph_data_dir``` works. Docker will ensure this directory is created on the host machine and
is mounted read-write inside the docker container (which is itself a virtual machine running Linux).

    docker run -d --name cassandra-node \
        -v /Users/username/janus_graph_data_dir:/var/lib/cassandra \
        -e CASSANDRA_START_RPC=true \
        -p 7000:7000 -p 7001:7001 -p 7199:7199 -p 9042:9042 -p 9160:9160 \
        cassandra:3.9
        
*WARNING* do not run these back to back ... Cassandra needs a minute or two to start. If not, Janus Server will come up in a non-working state.
        
Run a linked janus server container in the background

    docker run -d \
        --name janus-server \
        --link cassandra-node \
        -p 8182:8182 \
        tenxtech/janusgraph

(Optional) confirm that both containers are running by calling `docker container list`:

    host-123:janusgraph userid$ docker container list
    CONTAINER ID        IMAGE                                                COMMAND                  CREATED             STATUS              PORTS                                                                                                      NAMES
    2964ac793db5        tenxtech/janusgraph                                  "./docker-entrypoi..."   About an hour ago   Up 17 minutes       0.0.0.0:8182->8182/tcp                                                                                     janus-server
    20810658870d        cassandra:3.9                                        "/docker-entrypoin..."   About an hour ago   Up 55 minutes       0.0.0.0:7000-7001->7000-7001/tcp, 0.0.0.0:7199->7199/tcp, 0.0.0.0:9042->9042/tcp, 0.0.0.0:9160->9160/tcp   cassandra-node
    
(Optional) Hard restart of janus graph server and cassandra node

    docker stop janus-server
    docker stop cassandra-node
    docker rm janus-server
    docker rm cassandra-node
    rm -rf ~/janus_graph_data_dir
    # execute the docker run commands to start cassandra-node and janus-server ...   
    

Gollum-site is no longer required
---------------------------------

Previous versions of janusgraph-dist needed a companion module called
janusgraph-site, which in turn required the gollum-site binary to be
command on the local system.  This is no longer required now that the
docs have moved from the GitHub wiki to AsciiDoc files stored in the
repo.  The AsciiDoc files are converted to HTML using a DocBook-based
toolchain completely managed by maven.
