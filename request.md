# request

使用`postman`爬取相关的信息。运行开启之后，使用`postman`发送`请求一次`即可。
地址的话改成部署服务器的地址。

## 1

`每次发送请求的时候要改一下后面的数字(problem id)`
```text
GET
http://101.132.72.74:8080/api/config/problems/init/1601181376764964864
Accept: application/json
```

## 2

```text
GET
http://101.132.72.74:8080/api/config/info
Accept: application/json
```

## 3 

修改全局的 session 和 problemsid, 修改后会自动去执行py脚本爬取题目集信息,`这里的seeion和problemid也要进行修改`
```text
POST
http://101.132.72.74:8080/api/config/info/update
Content-Type: application/json
{
  "session":"4f71e43e-f678-4f9e-87ba-e403a0b7a09f",
  "problemid": "1601181376764964864"
}
```

## 4

新创建一个比赛, 接收json格式，rankName, isIcpc, rankProblemId三者不能为null

`problemid和sessionid都要改成比赛时候的id`
```text
POST
http://101.132.72.74:8080/api/result/completion
Content-Type: application/json
{
    "rankName": "2021年郑轻新生团体程序设计天梯赛",
    "isIcpc": "1",
    "rankProblemId": "1601181376764964864",
    "sessionId":"3a9e6672-5bbe-4297-b84f-1df68935e03f"
}
```

## 5

开启爬取数据的定时任务  

```text
GET
http://101.132.72.74:8080/api/config/job/problems
Accept: application/json
```

## 6
开启 或 关闭计算分数的定时任务 

```text 
GET
http://101.132.72.74:8080/api/config/job/rank
Accept: application/json
```

## 7
开启或关闭 icpc 赛制的爬取榜单任务  

```text
GET
http://101.132.72.74:8080/api/config/job/icpc/rank
Accept: application/json
```

## 8
比赛时少爬的数据，现在给他补一下  

```text
GET
http://101.132.72.74:8080/api/config/fixScore
Accept: application/json
```

## 9

获取团队排名 

```text 
GET
http://101.132.72.74:8080/api/team/getTeamScore
Accept: application/json
```

## 10
获取团队成员分数信息详情

```text
GET
http://101.132.72.74:8080/api/team/getTeamScoreDetil/11
Accept: application/json
```

## other
保存当前比赛的 排行榜信息，stands信息，以及题目集信息
如果是/info,那么是添加并且保存当前比赛，如果是/info/1 是保存当前结果到 id为1的比赛
```text
POST
http://101.132.72.74:8080/api/result/info/
Content-Type: application/x-www-form-urlencoded
```

```text
GET
http://101.132.72.74:8080/api/excel/team/export
Accept: application/json
```

```text
GET
http://101.132.72.74:8080/api/result/completion
Accept: application/json
```

修改基础，进阶和高阶，三个档位的标准划分值

```text
POST
http://101.132.72.74:8080/api/config/stands/update
Content-Type: application/json
{
  "primaryscore": 100,
  "advancescore":100
}
```