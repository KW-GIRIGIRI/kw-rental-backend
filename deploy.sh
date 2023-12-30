#!/bin/bash

# 8080 포트 프로세스 실행 중 체크
lsof -i TCP -P | grep 8080

# 8080 포트 실행 중에 따라 타겟 포트 선정
if [ $? -eq 0 ]; then
  TARGET_PORT=8081
  STOP_PORT=8080
else
  TARGET_PORT=8080
  STOP_PORT=8081
fi

# 배포 대상 포트에 jar를 실행
PROFILE=$1
# 배포 대상 포트에 jar를 실행
nohup java -jar \
-Dspring.profiles.active=${PROFILE} \
-Dserver.port=${TARGET_PORT} \
/home/ubuntu/build/libs/kw-rental-0.0.1-SNAPSHOT.jar &

sleep 10

# 5회 헬스 체크
TRIAL=0

while true; do
  if [ $TRIAL -eq 5 ]; then
    echo 'Health Check failed 5 times'
    exit 1
    break
  fi

  curl http://localhost:${TARGET_PORT}
  if [ $? -eq 0 ]; then
    echo 'Health Check successed'
    break;
  fi
  sleep 5
  TRIAL=$((TRIAL + 1))
done

# nginx proxy 포트 변경
sudo sed -i "s/proxy_pass http:127.0.0.1:${STOP_PORT}/proxy_pass http:127.0.0.1:${TARGET_PORT}/" /etc/nginx/sites-available/default
echo "NGINX config updated"

# 구버전 포트 프로세스 종료
fuser -k ${STOP_PORT}/tcp
echo "killed $STOP_PORT port"

# 배포 완료
echo "Deploy complete"