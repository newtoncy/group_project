# -*- coding: utf-8 -*-

# @File    : test.py
# @Date    : 2019-11-28
# @Author  : 王超逸
# @Brief   : 试试水

from app import app

@app.route("/")
def helloWorld():
    return "hello world!"
