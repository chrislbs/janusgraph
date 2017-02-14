FROM openjdk:8

ARG SERVER_BASE

ENV JANUS_STORAGE_BACKEND=inmemory \
    JANUS_STORAGE_BATCH_LOADING=false \
    JANUS_STORAGE_BUFFER_SIZE=1024 \
    JANUS_STORAGE_CONNECTION_TIMEOUT="10000" \
    JANUS_STORAGE_HOSTNAME="" \
    JANUS_STORAGE_PAGE_SIZE=100 \
    JANUS_STORAGE_PARALLEL_BACKEND_OPS=true \
    JANUS_STORAGE_PASSWORD="" \
    JANUS_STORAGE_PORT="" \
    JANUS_STORAGE_READ_ONLY=false \
    JANUS_STORAGE_READ_TIME="10000" \
    JANUS_STORAGE_SETUP_WAIT="60000" \
    JANUS_STORAGE_TRANSACTIONS=true \
    JANUS_STORAGE_USERNAME="" \
    JANUS_STORAGE_WRITE_TIME="100000" \
    JANUS_BIND_HOST=0.0.0.0 \
    JANUS_BIND_PORT=8182 \
    JANUS_WORKER_POOL_SIZE=1 \
    JANUS_GREMLIN_POOL_SIZE=8

ADD gremlin-server.yaml /root/gremlin-server.yaml
ADD janusgraph.properties /root/janusgraph-template.properties
ADD docker-entrypoint.sh /root/docker-entrypoint.sh
ADD append-util.sh /root/append-util.sh
ADD append-storage.sh /root/append-storage.sh
ADD append-index.sh /root/append-index.sh
ADD ${SERVER_BASE}.zip /root/${SERVER_BASE}.zip

RUN apt-get update && \
    apt-get install -y unzip && \
    unzip /root/${SERVER_BASE}.zip -d /root && \
    mv /root/${SERVER_BASE} /root/janus-server && \
    cd /root && \
    chmod u+x docker-entrypoint.sh append-index.sh append-storage.sh append-util.sh && \
    apt-get remove -y unzip && \
    apt-get clean

EXPOSE 8182

ENTRYPOINT [ "/root/docker-entrypoint.sh" ]
CMD [ "janus-server" ]
