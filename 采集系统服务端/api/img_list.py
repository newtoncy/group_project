# -*- coding: utf-8 -*-

# @File    : img_list.py
# @Date    : 2019-12-21
# @Author  : 王超逸
# @Brief   : 

from flask import request
from app import app
import json
from db.modelDef import *
from db.connect import Session


@app.route("/img_list", methods=['POST'])
def imgList():
    arg = json.loads(request.data)
    uid = arg["uid"]
    lastOne = arg.get("lastOne", None)
    count = arg["count"]
    session = Session()
    user: User = session.query(User).get(uid)
    query = user.data
    if lastOne is not None:
        query = query.filter(Data.dataID < lastOne)
    data = query.order_by(Data.dataID.desc()).limit(count).all()
    img = [{"id": item.dataID,
            "imgPath": item.imgPath,
            "tag": item.tag,
            "comment": item.comment}
           for item in data]
    out = {"code": "OK", "img": img}
    session.commit()
    return json.dumps(out, ensure_ascii=False)
