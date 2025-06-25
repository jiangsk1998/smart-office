FROM openjdk:17-jdk-alpine

# 设置工作目录
WORKDIR /app

# 拷贝本地JAR
ARG JAR_FILE=target/project_manager-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 暴露端口
EXPOSE 9001

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]