import time
import requests
import threading

url = "http://34.64.85.29:8081/"
headers = {'content-type': 'application/json'}


def requestApi(reqMethod, reqData):
    try:
        requests.post(url+reqMethod, json=reqData, headers=headers)
    except:
        time.sleep(2)
        requests.post(url+reqMethod, json=reqData, headers=headers)


class dataBuilder:
    def __init__(self, dataId):
        self.dataId = str(dataId)

    def getHomeLogin(self, sign):
        dataId = self.dataId
        return {
            "authToken": "authToken" + dataId,
            "displayName": "displayName" + dataId,
            "userId": "userId" + dataId,
            "newPlayer": (True if (sign == "register") else False),
            "skinRole": "S"
        }

    def getHomeUser(self):
        return {"userId": "userId" + self.dataId}

    def getHomeSkin(self):
        return {
            "userId": "userId"+self.dataId,
            "skinColor": "D8FF7EFF"
        }

    def getHomeCloth(self):
        return {
            "userId": "userId"+self.dataId,
            "skinCloth": "Uniform_red"
        }

    def getConJoinLeave(self):
        dataId = self.dataId
        return {
            "GameId": "conuser_test",
            "UserId": "userId"+dataId,
            "Nickname": "displayName"+dataId
        }

    def getConAuthRoom(self):
        return {
            "joinCode": "code"
        }

    def getConRoomExist(self):
        return {
            "roomName": "conuser_test"
        }

    def getAgora(self):
        return {
            "roomName": "conuser_test",
        }

    def getBoardList(self):
        return {
            "boardRoom": "conuser_test"
        }

    def getBoardInsert(self):
        dataId = self.dataId
        return {
            "boardRoom": "conuser_test",
            "boardWriterId": "userId"+dataId,
            "boardTitle": "boardTitle"+dataId,
            "boardContent": "boardContent"+dataId,
            "boardDeadline": "1998-09-24 00:00:00",
            "boardNotice": True,
            "boardAssginment": False
        }

    def getTimerAdd(self):
        dataId = self.dataId
        return {
            "timerRoom": "conuser_test",
            "timerUser": "userId"+dataId,
            "timerSubject": "Subject"+dataId
        }

    def getTimerList(self):
        return {
            "timerRoom": "conuser_test",
            "timerUser": "userId"+self.dataId
        }


class ReqThread(threading.Thread):
    def __init__(self, thId):
        threading.Thread.__init__(self)
        self.thId = str(thId)

    def run(self):
        print(self.thId+"start")
        thDataBuilder = dataBuilder(self.thId)

        requestApi("login", thDataBuilder.getHomeLogin("register"))
        # print(self.thId+"register")

        requestApi("login", thDataBuilder.getHomeLogin("login"))
        # print(self.thId+"login")

        requestApi("user", thDataBuilder.getHomeUser())
        # print(self.thId+"user")

        requestApi("skin", thDataBuilder.getHomeSkin())
        # print(self.thId+"skin")

        requestApi("cloth", thDataBuilder.getHomeCloth())
        # print(self.thId+"cloth")

        requestApi("auth_room", thDataBuilder.getConAuthRoom())
        # print(self.thId+"authroom")

        requestApi("join", thDataBuilder.getConJoinLeave())
        # print(self.thId+"join")

        requestApi("get_token", thDataBuilder.getAgora())
        # print(self.thId+"gettoken")

        requestApi("delete_class_master", thDataBuilder.getAgora())
        # print(self.thId+"deletecm")

        requestApi("board/list", thDataBuilder.getBoardList())
        # print(self.thId+"boardlist")

        requestApi("board/insert", thDataBuilder.getBoardInsert())
        # print(self.thId+"boardinsert")

        requestApi("timer/add", thDataBuilder.getTimerAdd())
        # print(self.thId+"timeradd")

        requestApi("timer/list", thDataBuilder.getTimerList())
        # print(self.thId+"timerlist")

        requestApi("leave", thDataBuilder.getConJoinLeave())
        # print(self.thId+"leave")
        print(self.thId+"end")


if __name__ == "__main__":
    for i in range(5000):
        reqThread = ReqThread(i)
        reqThread.start()

    mainThread = threading.currentThread()

    for th in threading.enumerate():
        if th is not mainThread:
            th.join()
