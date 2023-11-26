# zzuli_gplt

这是zzuli 17 级学长写的代码，祖传代码然后到这里来了，鉴于之后的学弟学妹还有可能使用这套代码，所以整理了一下，希望你们能云得到（
如果你有任何好的建议，欢迎提交pr进行修改~

## 环境准备

操作系统使用的是 linux 服务器，（本次配置的时候使用的是 debian，但其实其他的也可以的）
部署的时候是将前端后端都放在了同一个服务器里面，redis,mysql都放在了同一个服务器里面。
使用xshell连接数据库进行操作

最后部署的位置大约是
```
/root/zzuliAcm/py: 放置此项目中的py文件
/root/zzuliAcm/zzuli_jar:放置本项目中后端打包成的jar包文件
/var/www/html: 放置前面打包成的文件
```

别问为什么用root用户（

### 软件安装

```shell
sudo apt install lrzsz # 传输文件
sudo apt install redis # redis
sudo apt install mysql-server # 上网进行搜索，安装8左右的就可以了，本次使用的时候是8.29
sudo apt install openjdk-17-jdk # java
```

### 后端

因为代码太屎山了，所有我们把后端的代码写死了，你需要手动修改，然后更改成你服务器的地址，然后才能使用。
然后打包成jar包，放置在上述文件中，完成全部之后，运行即可

```shell
java -jar zzuliAcm.jar # 即可
```

注意这里会会相关的报错，别急，只要发请求之后能有计算相关的信息，那么就可以了！


