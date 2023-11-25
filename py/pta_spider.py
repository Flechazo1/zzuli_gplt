# !/usr/bin/python
# coding=UTF-8

import requests
import json
import sys
import os

curPath = os.path.abspath(os.path.dirname(__file__))
rootPath = os.path.split(curPath)[0]
sys.path.append(rootPath)


def get_session(PTASession):
    session = requests.Session()
    cookies = {
        # 朱会东老师的账号
        # "PTASession": "4f71e43e-f678-4f9e-87ba-e403a0b7a09f",
        "PTASession": "3a9e6672-5bbe-4297-b84f-1df68935e03f",
        "JSESSIONID": "9C79748AECA217847009CDF6B155484C"
    }

    headers = {
        "Content-Type": "application/json;charset=UTF-8",
        # "Accept-Encoding": "gzip, deflate, br",
        "Accept-Language": "cn-ZH",
        "Accept": "application/json;charset=UTF-8",
        "User-Agent": "Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0 Mobile/15E148 Safari/604.1"
    }
    session.cookies.update(cookies)
    session.headers.update(headers)

    return session


def get_rank(url, session):

    res = session.get(url=url)
    rank = json.loads(res.text)

    # 将json写入文件中
    with open("rank.json", "w", encoding="UTF-8") as f:
        json.dump(rank, f)


if __name__ == "__main__":
    if len(sys.argv) == 1 or len(sys.argv) == 0:
        print("请至少输入'PTA_session' 和 'problem_set_id', 'after/before/not', 'id/no id', 默认情况下 'limit' = 50")
        quit()

    # init 参数
    PTA_session = sys.argv[1]
    problem_set_id = sys.argv[2]
    after_or_before = sys.argv[3]
    ID = sys.argv[4]
    url = f"https://pintia.cn/api/problem-sets/{problem_set_id}/submissions?show_all=true"
    # 判断是向前还是向后，或者默认情况下查询。
    if not ID.__eq__("no id"):
        if after_or_before.__eq__("after"):
            url += "&after=" + ID
        elif after_or_before.__eq__("before"):
            url += "&before=" + ID
        else:
            # 如果不是先前，也不是向后，那么参数可能是 乱写的...
            print("now, your params maybe have error...")
            # 回显 ID参数信息
            ID = "null"
            quit()
    else:
        # 现在id = no id，但是为了回显 ID参数信息，我们把它设置成 null 最好。
        ID = "null"
    #print("url::::" + url)
    # 返回多少个数据
    limit = "not limit"
    if len(sys.argv) == 6 and\
            not sys.argv[5].__eq__(limit):
        limit = sys.argv[5]
        url += "&limit=" + limit

    print("current_params:")
    print("PTA_session: " + PTA_session + ", problem_set_id: " + problem_set_id
          + "\nafter_or_before: " + after_or_before + ", ID: " + ID + ", limit: " + str(limit))
    session = get_session(PTA_session)

    # 比赛的提交情况
    get_rank(url, session)
    session.close()
