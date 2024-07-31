#!/bin/bash

print_usage() {
  echo "Usage: $0 {--clean|--clean-deep|--build|--build-start|--start|--start-dev|--down|--down-clean|--down-clean-deep}"
}

if [ $# -eq 0 ];  then
  print_usage
  exit 1
fi

case "$1" in
  --clean)
    ./gradlew clean
    ;;
  --clean-deep)
    ./gradlew clean &&
    rm -rf buzzy-webapp/dist &&
    rm -rf buzzy-webapp/node_modules
    ;;
  --build)
    ./gradlew clean build
    ;;
  --build-start)
    ./gradlew clean build &&
    docker compose up -d --build buzzy_webapp
    ;;
  --start)
    docker compose up -d --build buzzy_webapp
    ;;
  --start-dev)
    docker compose up -d --build buzzy_sso_db buzzy_api_db buzzy_redis buzzy_kafka buzzy_kafka_zookeeper
    ;;
  --down)
    docker compose down
    ;;
  --down-clean)
    read -p "Are you sure you want to remove the containers and its data? (y/n): " confirm
    if [ "$confirm" == "y" ]; then
      docker compose down &&
      docker volume rm buzzy_buzzy_sso_db_data buzzy_buzzy_redis_data buzzy_buzzy_kafka_zookeeper_data buzzy_buzzy_api_db_data
    fi
    ;;
  --down-clean-deep)
    read -p "Are you sure you want to remove the containers and its data, build, dist and node_modules? (y/n): " confirm
    if [ "$confirm" == "y" ]; then
      docker compose down &&
      docker volume rm buzzy_buzzy_sso_db_data buzzy_buzzy_redis_data buzzy_buzzy_kafka_zookeeper_data buzzy_buzzy_api_db_data
    fi
    ;;
  *)
    print_usage
    exit 1
    ;;
esac
