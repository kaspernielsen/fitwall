FROM java:openjdk-8-jdk

ADD bin/ww/* /app/bin/ww/

EXPOSE 8000

CMD exec java -classpath /app/bin/ ww.Ddd
