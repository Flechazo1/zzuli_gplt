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
        "Accept-Language": "cn-ZH",
        # "Accept - Encoding": "gzip, deflate, br",
        "Accept": "application/json;charset=UTF-8",
        "User-Agent": "Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0 Mobile/15E148 Safari/604.1"
    }
    session.cookies.update(cookies)
    session.headers.update(headers)

    return session


def get_problem_id(PTA_session, problem_set_id):
    session = get_session(PTA_session)

    # 先获取 exam_id
    # exam_res = session.get(f"https://pintia.cn/api/problem-sets/{problem_set_id}/exams")
    # exam_json = json.loads(exam_res.text)
    # print(exam_json)
    # # exam_id = exam_json.get("exam").get("id")
    # exam_id = "1332310289785073664"

    # 通过 exam_id 获取题目集
    res = session.get(f"https://pintia.cn/api/problem-sets/problems/{problem_set_id}?")
    problem_set_json = json.loads(res.text)
    print(problem_set_json)

    problem_set_problems = problem_set_json.get("problemSetProblems")
    if problem_set_problems is None:
        print("题目集为 null")
        quit()

    problems_info = dict()
    for problem_set_problem in problem_set_problems:
        label = problem_set_problem.get("label")
        score = problem_set_problem.get("score")
        problems_info[label] = score

    # print(problems_info)
    # 将json写入文件中
    print("运行完毕")
    with open("problem_set_label.txt", "w", encoding="UTF-8") as f:
        json.dump(problems_info, f)
    print("文件写入完毕")


if __name__ == "__main__":
    get_problem_id("3a9e6672-5bbe-4297-b84f-1df68935e03f", 1601181376764964864)
    # 参数校验
    if len(sys.argv) == 1 or len(sys.argv) == 0:
        print("请至少输入PTA_session 和 problem_set_id")
        quit()

    PTA_session = sys.argv[1]
    problem_set_id = sys.argv[2]
    # 爬取页面的题号，方便后台进行数据初始化。

    get_problem_id(PTA_session, problem_set_id)
