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
    if "coverID" in arg:
        data = session.query(Data).get(arg["coverID"])
        data.tag = arg.get("tag", data.tag)
        data.comment = arg.get("comment", data.comment)
    else:
        data = Data(userUID=arg["userID"], tag=arg["tag"], comment=arg["comment"])
        session.add(data)
    # 随机生成一个文件名

    if "imgBase64" not in arg and "coverID" not in arg:
        return {"code": "Fail", "reason": "图片是必须要的"}

    if "imgBase64" in arg:
        path = getNewFilePath()
        with open(path, 'wb') as file:
            foo = base64.b64decode(arg["imgBase64"])
            file.write(foo)
        data.imgPath = path

    session.flush()
    dataID = data.dataID
    session.commit()
    return {"code": "OK", "imgPath": data.imgPath, "id": dataID}


def getNewFilePath():
    while True:
        fName = random.randint(0, 100000000)
        fName = hex(fName)[2:]
        fName += '.jpg'
        path = "static/" + fName
        if not os.path.exists(path):
            break
    return path
