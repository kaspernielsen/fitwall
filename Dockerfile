FROM openjdk:11-jre-slim

ADD bin/reverseproxy/* /app/bin/reverseproxy/

EXPOSE 8000

CMD exec java -classpath /app/bin/ reverseproxy.ReverseProxy
