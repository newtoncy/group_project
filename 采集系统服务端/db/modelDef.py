# -*- coding: utf-8 -*-

# @File    : modelDef.py
# @Date    : 2019-11-28
# @Author  : 王超逸
# @Brief   : 定义数据库模式

from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import Column, Integer, String, ForeignKey, Text
from sqlalchemy.orm import relationship

Base = declarative_base()


class User(Base):
    __tablename__ = 'users'  # __tablename__是必须的
    uid = Column(String(20), primary_key=True)  # 主键是必须的
    userName = Column(String(20))
    password = Column(String(20))
    data = relationship("Data", back_populates='user', lazy="dynamic")


class Data(Base):
    __tablename__ = 'data'
    dataID = Column(Integer, primary_key=True)
    imgPath = Column(String(20))
    userUID = Column(String(20), ForeignKey('users.uid'))
    tag = Column(String(20))
    comment = Column(Text(10000))
    user = relationship("User", back_populates='data')
