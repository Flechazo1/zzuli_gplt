# !/usr/bin/python
# coding=UTF-8
import random
from time import sleep

import redis
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
         #"PTASession": "4f71e43e-f678-4f9e-87ba-e403a0b7a09f",
        "PTASession": "3a9e6672-5bbe-4297-b84f-1df68935e03f",
        "JSESSIONID": "9C79748AECA217847009CDF6B155484C"
    }

    headers = {
        "Content-Type": "application/json;charset=UTF-8",
        "Accept-Language": "cn-ZH",
        # "Accept - Encoding": "gzip, deflate, br",
        "Accept": "application/json;charset=UTF-8",
        "User-Agent": "Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0 Mobile/15E148 Safari/604.1"
    }
    session.cookies.update(cookies)
    session.headers.update(headers)

    return session


def get_ranking(PTA_session, problem_set_id):
    url = f"https://pintia.cn/api/problem-sets/{problem_set_id}/rankings?page=0&limit=100"
    session = get_session(PTA_session)
    res = session.get(url)
    ranking_json = json.loads(res.text)
    # print(ranking_json)
    ranking_total = int(ranking_json.get("total"))
    need_number = 0
    if ranking_total % 100 == 0:
        need_number = int(ranking_total / 100) - 1
    else:
        need_number = int(ranking_total / 100)

    ranking_list = list(ranking_json.get("commonRankings").get("commonRankings"))
    label_tuple = ranking_json.get("commonRankings").get("labelByIndexTuple")
    for i in range(need_number):
        url = f"https://pintia.cn/api/problem-sets/{problem_set_id}/rankings?page={i + 1}&limit=100"
        # print(url)
        res = session.get(url)
        if res.status_code != 200:
            return
        ranking_json = json.loads(res.text)
        ranking_item_list = ranking_json.get("commonRankings").get("commonRankings")
        ranking_list = ranking_list + ranking_item_list
        sleep(1.5 + random.random())

    rank_info_list = list()
    for ranking_item in ranking_list:
        ranking_info = dict()
        ranking_info["rank"] = ranking_item.get("rank")
        ranking_info["startAt"] = ranking_item.get("startAt")
        ranking_info["totalScore"] = ranking_item.get("totalScore")
        ranking_json_user = ranking_item.get("user")

        if 'studentUser' in ranking_json_user:
            ranking_info["user"] = ranking_json_user.get("studentUser")
        else:
            continue

        if 'solvingTime' in ranking_item:
            ranking_info["solvingTime"] = ranking_item.get("solvingTime")

        if 'problemScores' in ranking_item:
         ranking_info["problemScores"] = ranking_item.get("problemScores")

         for k in list(ranking_info["problemScores"].keys()):
           if k in label_tuple:
            ranking_info["problemScores"][label_tuple[k]] = ranking_info["problemScores"].pop(k)
            continue
        rank_info_list.append(ranking_info)

    r = redis.StrictRedis(host='127.0.0.1', port=6379, db=0)
    r.set("ranking_json", json.dumps(rank_info_list))
    r.close()
    print("key: ranking_json --->>> success ")


if __name__ == "__main__":

    # 参数校验
    if len(sys.argv) == 1 or len(sys.argv) == 0:
        print("请至少输入PTA_session 和 problem_set_id")
        quit()
    PTA_session = sys.argv[1]
    problem_set_id = sys.argv[2]
    # 爬取页面的题号，方便后台进行数据初始化。

    get_ranking(PTA_session, problem_set_id)
    #get_ranking("4f71e43e-f678-4f9e-87ba-e403a0b7a09f", 1595469283415740416)
