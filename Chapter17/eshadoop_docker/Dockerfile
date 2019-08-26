# docker build --network packt -t eshadoop .
# docker run --rm --network packt --name eshadoop -it eshadoop /usr/app/commands.sh
FROM ubuntu:16.04

RUN echo 'debconf debconf/frontend select Noninteractive' | debconf-set-selections

# Install OpenJDK 8
RUN apt-get update && \
    apt-get install -y apt-utils && \
    apt-get install -y openjdk-8-jdk

# Install Python
RUN apt-get update && \
    apt-get install -y software-properties-common && \
    apt-get install -y python-software-properties

RUN add-apt-repository ppa:deadsnakes/ppa && \
    apt-get update && \
    apt-get install -y python3.6 && \
    apt-get install -y python3.6-dev && \
    apt-get install -y python3-pip && \
    apt-get install -y python3.6-venv 

RUN python3.6 -m pip install pip --upgrade && \
    apt-get install -y wget && \
    apt-get install -y curl 

RUN rm -rf /var/lib/apt/lists/* 
    
WORKDIR /usr/app/
COPY . .

RUN pip install --no-cache-dir -r requirements.txt

RUN cd /usr/local/lib/python3.6/dist-packages/pyspark/jars && \
    wget http://central.maven.org/maven2/org/elasticsearch/elasticsearch-spark-20_2.11/7.0.0/elasticsearch-spark-20_2.11-7.0.0.jar

RUN ["chmod", "+x", "/usr/app/commands.sh"]
ENTRYPOINT ["/usr/app/commands.sh"]
