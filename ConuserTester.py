import requests
import threading


url = "http://34.64.85.29:8081/"
headers = {'content-type': 'application/json'}


def requestApi(reqMethod, reqData):
    response = requests.post(url+reqMethod, json=reqData, headers=headers)


class dataBuilder:
    def __init__(self, dataId):
        self.dataId = str(dataId)

    def getHomeLogin(self, sign):
        dataId = self.dataId
        return {
            "authToken": "authToken" + dataId,
            "displayName": "displayName" + dataId,
            "userId": "userId" + dataId,
            "newPlayer": (True if (sign is "register") else False),
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
            "NickName": "displayName"+dataId
        }

    def getConAuthRoom(self):
        return {
            "roomCode": ""
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
        self.thId = thId

    def run(self):
        thDataBuilder = dataBuilder(self.thId)
        requestApi("login", thDataBuilder.getHomeLogin("register"))
        requestApi("login", thDataBuilder.getHomeLogin("login"))
        requestApi("user", thDataBuilder.getHomeUser())
        requestApi("skin", thDataBuilder.getHomeSkin())
        requestApi("cloth", thDataBuilder.getHomeCloth())
        requestApi("auth_room", thDataBuilder.getConAuthRoom())
        requestApi("join", thDataBuilder.getConJoinLeave())
        requestApi("get_token", thDataBuilder.getAgora())
        requestApi("delete_class_master", thDataBuilder.getAgora())
        requestApi("board/list", thDataBuilder.getBoardList())
        requestApi("board/insert", thDataBuilder.getBoardInsert())
        requestApi("timer/list", thDataBuilder.getTimerAdd())
        requestApi("timer/add", thDataBuilder.getTimerList())
        requestApi("leave", thDataBuilder.getConJoinLeave())


if __name__ == "__main__":
    for i in range(100):
        reqThread = ReqThread(i)
        reqThread.start()

    mainThread = threading.currentThread()

    for th in threading.enumerate():
        if th is not mainThread:
            th.join()
