# -*- coding: utf-8 -*-

# @File    : connect.py
# @Date    : 2019-11-28
# @Author  : 王超逸
# @Brief   : 数据库连接
import json
import os
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, scoped_session
from db.modelDef import Base

this_dir, this_filename = os.path.split(__file__)
config_path = os.path.join(this_dir, "ConnectDB.json")

connectStr = "mysql+%(driver)s://%(username)s:%(password)s@%(host)s:%(port)s/%(dbname)s?charset=%(charset)s"
with open(config_path, 'rt', encoding='utf-8') as file:
    connectStr = connectStr % json.load(file)
engine = create_engine(connectStr, encoding='utf-8')
# 设置会话的工厂函数。采用scoped_session
sessionFactory = sessionmaker(bind=engine)
Session = scoped_session(sessionFactory)

if __name__ == "__main__":
    Base.metadata.create_all(engine)
