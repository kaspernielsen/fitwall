FROM java:openjdk-11-jdk

ADD bin/* /app/bin/

EXPOSE 8000

CMD exec java -classpath /app/bin/ reverseproxy.ReverseProxy
