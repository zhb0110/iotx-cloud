#FROM ubuntu:latest
#LABEL authors="10107"
#
#ENTRYPOINT ["top", "-b"]


FROM nginx:1.23-alpine

# 复制配置文件
COPY nginx.conf /etc/nginx/nginx.conf

# 复制前端静态文件（假设在 dist 目录）
COPY dist /usr/share/nginx/html

# 复制 SSL 证书（提前放入 ssl 文件夹）
COPY ssl /etc/nginx/ssl

EXPOSE 80
EXPOSE 443