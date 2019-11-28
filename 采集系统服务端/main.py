# -*- coding: utf-8 -*-

# @File    : main.py
# @Date    : 2019-11-28
# @Author  : 王超逸
# @Brief   : 
from app import app
import api

if __name__ == '__main__':
    app.jinja_env.auto_reload = True
    app.run(host="0.0.0.0", debug=True)
