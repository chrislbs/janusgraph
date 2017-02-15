# use java8 base image
FROM tenxtech/java:8

ENV JANUS_CONFIG_FILE=/root/janusgraph-cassandra.properties \
    JANUS_STORAGE_HOSTNAME=cassandra-node \
    JANUS_BIND_HOST=0.0.0.0

# install unzip
RUN apt-get update && \
    apt-get install -y unzip

ADD gremlin-server.yaml /root/gremlin-server.yaml
ADD janusgraph-cassandra.properties /root/janusgraph-cassandra.properties

# TODO: rename this file from source build to prevent version specification
ADD janusgraph-0.1.0-SNAPSHOT-hadoop2.zip /root/janusgraph.zip

WORKDIR /root

RUN unzip janusgraph.zip && \
    mv janusgraph-0.1.0-SNAPSHOT-hadoop2 janus && \
    apt-get remove -y unzip && \
    apt-get clean

ADD docker-entrypoint.sh docker-entrypoint.sh
RUN chmod u+x docker-entrypoint.sh

EXPOSE 8182

ENTRYPOINT [ "./docker-entrypoint.sh" ]
CMD [ "janus" ]
