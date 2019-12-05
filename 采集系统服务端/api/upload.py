# -*- coding: utf-8 -*-

# @File    : upload.py
# @Date    : 2019-11-28
# @Author  : 王超逸
# @Brief   : 上传图片的接口
import os
import random

from flask import request
from app import app
import json
from db.modelDef import *
from db.connect import Session
import base64


@app.route("/upload", methods=['POST'])
def upload():
    arg = json.loads(request.data)
    session = Session()
    data = Data(userUID=arg["userID"], tag=arg["tag"], comment=arg["comment"])
    # 随机生成一个文件名
    while True:
        fName = random.randint(0, 100000000)
        fName = hex(fName)[2:]
        path = "upload_img/" + fName
        if not os.path.exists(path):
            break
    with open(path, 'wb') as file:
        foo = base64.b64decode(arg["imgBase64"])
        file.write(foo)
    session.add(data)
    session.commit()
    return "ok"
