## 1. 清除Rabbitmq中已存在的消息队列及其数据

    # 清除消息队列及其数据命令
    rabbitmqctl stop_app
    rabbitmqctl reset
    rabbitmqctl start_app
    
    # 查看队列信息
    rabbitmqctl list_queues
