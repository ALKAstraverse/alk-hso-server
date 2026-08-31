#!/bin/bash
set -e

echo "Waiting for MySQL to be ready..."

until bash -c "echo > /dev/tcp/10.0.1.182/3306" 2>/dev/null; do
  echo "MySQL not ready yet, waiting..."
  sleep 5
done

echo "MySQL is ready! Starting HSO Server..."
exec java -Xms512m -Xmx1g -jar target/HSO_Re_2-1.0-jar-with-dependencies.jar
