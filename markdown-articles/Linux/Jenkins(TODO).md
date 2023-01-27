```markdown
# 创建Jenkins工作空间,修改目录读写权限
mkdir -p /opt/docker/jenkins_home
chmod 777 /opt/docker/jenkins_home

# 运行
docker run -d -p 8081:8080 -p 8082:50000 -v /etc/localtime:/etc/localtime -v /opt/docker/jenkins_home:/var/jenkins_home --name jenkins jenkins/jenkins

# 访问，需要密码
http://{部署Jenkins所在服务IP}:8081

# 修改 hudson.model.UpdateCenter.xml 配置文件,修改url
vi /opt/docker/jenkins_home/hudson.model.UpdateCenter.xml
# 修改为如下url,这是清华大学官方镜像，方便下载插件
https://mirrors.tuna.tsinghua.edu.cn/jenkins/updates/update-center.json

# 查看密码，上面启动Jenkins时将容器目录映射到宿主机，这里查看宿主机文件即可
cat /opt/docker/jenkins_home/secrets/initialAdminPassword

# 输入密码进入后，会让选择安装推荐的插件，选完后等他下，然后要创建管理员账号，这里我创建为
root
root@123

# 忘记密码了,找到工作空间目录下 config.xml 配置文件
找到 <useSecurity>true</useSecurity> 值改为false,重启Jenkins

# 插件安装
# Manage Jenkins -》 Manage Plugins
Publish Over SSH
Gitlab
Build With Parameters 输入框式的参数
Persistent Parameter 下拉框式的参数
Role-based Authorization Strategy

```



