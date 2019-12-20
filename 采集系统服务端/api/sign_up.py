# -*- coding: utf-8 -*-

# @File    : sign_up.py
# @Date    : 2019-12-19
# @Author  : 王超逸
# @Brief   :

from flask import request
from app import app
import json
from db.modelDef import *
from db.connect import Session


@app.route("/sign_up", methods=['POST'])
def signUp():
    arg = json.loads(request.data)
    uid = arg['uid']
    userName = arg["userName"]
    pwd = arg["password"]
    # todo:异常捕获和判断参数合法
    session = Session()
    if session.query(User).get(uid):
        return {"code": 'Fail', 'reason': "此账号已被注册"}
    user = User(uid=uid, userName=userName, password=pwd)
    session.add(user)
    session.commit()
    out = {
        "code": "OK",
        "uid": uid,
        "userName": userName
    }
    return json.dumps(out, ensure_ascii=False)
