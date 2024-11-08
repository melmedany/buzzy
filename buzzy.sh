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
    read -p "Are you sure you want to remove dist and node_modules? (Y/n): " -r confirm
    confirm=${confirm:-Y}
    if [[ $confirm =~ ^[Yy]$ ]]; then
      rm -rf buzzy-webapp/dist &&
      rm -rf buzzy-webapp/node_modules
    fi
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
    docker compose up -d --build buzzy_sso_db buzzy_api_db buzzy_redis buzzy_kafka_ui buzzy_kafka
    ;;
  --down)
    docker compose down
    ;;
  --down-clean)
    read -p "Are you sure you want to remove the containers and its data? (Y/n): " -r confirm
    confirm=${confirm:-Y}
    if [[ $confirm =~ ^[Yy]$ ]]; then
      docker compose down &&
      docker volume rm buzzy_buzzy_sso_db_data buzzy_buzzy_redis_data buzzy_buzzy_kafka_data buzzy_buzzy_api_db_data
    fi
    ;;
  --down-clean-deep)
    read -p "Are you sure you want to remove the containers and its data, build, dist and node_modules? (Y/n): " -r confirm
    confirm=${confirm:-Y}
    if [[ $confirm =~ ^[Yy]$ ]]; then
      docker compose down &&
    ./gradlew clean &&
    rm -rf buzzy-webapp/dist &&
    rm -rf buzzy-webapp/node_modules &&
    docker volume rm buzzy_buzzy_sso_db_data buzzy_buzzy_redis_data buzzy_buzzy_kafka_data buzzy_buzzy_api_db_data
    fi
    ;;
  *)
    print_usage
    exit 1
    ;;
esac
