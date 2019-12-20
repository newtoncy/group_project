# -*- coding: utf-8 -*-

# @File    : sign_in.py
# @Date    : 2019-12-19
# @Author  : 王超逸
# @Brief   : 

from flask import request
from app import app
import json
from db.modelDef import *
from db.connect import Session


@app.route("/sign_in", methods=['POST'])
def sign_in():
    arg = json.loads(request.data)
    uid = arg['uid']
    pwd = arg['pwd']
    session = Session()
    user: User = session.query(User).get(uid)
    session.commit()
    if pwd == user.password:
        out = {
            "code": "OK",
            "userName": user.userName,
            "uid": user.uid
        }
        return json.dumps(out, ensure_ascii=False)
    else:
        out = {"code": "Fail","reason":"Validation failed"}
        return json.dumps(out, ensure_ascii=False)
