package ac.linker.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ac.linker.dto.JoinDto;
import ac.linker.dto.RoomDto;
import ac.linker.service.CodeGenerator;
import ac.linker.service.ConnectService;

@RestController
public class ConnectController {
    private Gson gson = new Gson();
    private ConnectService connectService;

    @Autowired
    ConnectController(ConnectService connectService) {
        this.connectService = connectService;
    }

    private String getResponseJson(final int status) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("State", "");
        jsonObject.addProperty("ResultCode", status);
        /*
         * 0 : OK 1 : Duplicated 2 : Over the max length
         */
        return jsonObject.toString();

    }

    @PostMapping(value = "/create", produces = "application/json; charset=utf8")
    public String pathCreate(@RequestBody Map<String, Object> param) {
        final JsonObject requestObject = gson.toJsonTree(param).getAsJsonObject();

        System.out.println("PathCreate : " + requestObject + "\n");
        final String roomName = param.get("GameId").toString();
        final String userId = param.get("UserId").toString();
        final String userName = param.get("Nickname").toString();
        final String reqType = param.get("Type").toString();

        RoomDto roomDto = new RoomDto(roomName);

        System.out.println("reqType : " + reqType);

        if (reqType.equals("Create")) {
            // room insert

            try { // prevent duplicated room name
                connectService.insertRoom(roomDto);
            } catch (DuplicateKeyException e) {
                System.out.println("Warning! Room name " + roomName + " duplicated!(from pathCreate)\n");
                return getResponseJson(1);
            } catch (DataIntegrityViolationException m) {
                System.out.println("Warning! Room name " + roomName + " is over the max length!(from pathCreate)\n");
                return getResponseJson(2);
            }

            while (true) { // create and update room code
                try {
                    roomDto.setCode(CodeGenerator.getCode(roomDto.getNo()));
                    connectService.updateRoomCode(roomDto);
                    break;
                } catch (DuplicateKeyException e) { // prevent duplicated code.
                    System.out.println("Warning! Invite code " + roomDto.getNo() + " duplicated! Regenerate code...\n");
                }
            }

            connectService.insertJoin(new JoinDto(userId, roomName));
            connectService.updateRoomNewJoin(roomDto);
            System.out.println(userId + " :: " + userName + " created and joined " + roomName + "\n");
            // join room
        }

        if (reqType.equals("Load")) {
            System.out.println(userId + " :: " + userName + " recreated and joined " + roomName + "\n");
            connectService.updateRoomJoin(roomDto);
        }

        return getResponseJson(0);
    }

    @PostMapping(value = "/join", produces = "application/json; charset=utf8")
    public String pathJoin(@RequestBody Map<String, Object> param) {
        final JsonObject requestObject = gson.toJsonTree(param).getAsJsonObject();

        System.out.println("PathJoin : " + requestObject + "\n");

        final String roomName = param.get("GameId").toString();
        final String userId = param.get("UserId").toString();
        final String userName = param.get("Nickname").toString();

        // user join
        try {
            connectService.insertJoin(new JoinDto(userId, roomName));
            connectService.updateRoomNewJoin(new RoomDto(roomName));
            System.out.println(userId + " :: " + userName + "joined" + roomName + "\n");
        } catch (DuplicateKeyException e) {
            connectService.updateRoomJoin(new RoomDto(roomName));
            System.out.println("Member " + userId + " :: " + userName + " is already in room " + roomName
                    + "! Duplicated pair is prevented.\n");
        }

        return getResponseJson(0);
    }

    @PostMapping(value = "/leave", produces = "application/json; charset=utf8")
    public String pathLeave(@RequestBody Map<String, Object> param) {
        final JsonObject requestObject = gson.toJsonTree(param).getAsJsonObject();

        final String roomName = param.get("GameId").toString();

        System.out.println("PathLeave : " + requestObject + "\n");

        connectService.updateRoomLeave(new RoomDto(roomName));

        return getResponseJson(0);
    }

    @PostMapping(value = "/close", produces = "application/json; charset=utf8")
    public String pathClose(@RequestBody Map<String, Object> param) {
        final JsonObject requestObject = gson.toJsonTree(param).getAsJsonObject();

        System.out.println("PathClose : " + requestObject + "\n");

        // delete room

        return getResponseJson(0);
    }

    @PostMapping(value = "/event", produces = "application/json; charset=utf8")
    public String pathEvent(@RequestBody Map<String, Object> param) {
        final JsonObject requestObject = gson.toJsonTree(param).getAsJsonObject();

        System.out.println("PathEvent : " + requestObject + "\n");

        return getResponseJson(0);
    }

    @PostMapping(value = "/game_properties", produces = "application/json; charset=utf8")
    public String pathGameProperties(@RequestBody Map<String, Object> param) {
        final JsonObject requestObject = gson.toJsonTree(param).getAsJsonObject();

        System.out.println("PathGameProperites : " + requestObject + "\n");

        return getResponseJson(0);
    }

    @PostMapping(value = "/auth_room", produces = "application/json; charset=utf8")
    public String authRoom(@RequestBody Map<String, Object> param) {
        final Optional<String> optional = Optional.ofNullable(param.get("joinCode").toString());
        final String roomCode = optional.orElse("");
        final String roomName;

        System.out.println("Received roomCode : " + roomCode);

        RoomDto roomDto = new RoomDto();
        roomDto.setCode(roomCode);
        final List<Map<String, Object>> queryResult = connectService.getRoomByCode(roomDto);

        if (!queryResult.isEmpty()) {
            roomName = queryResult.get(0).get("room_name").toString();
        } else {
            roomName = "";
        }
        System.out.println("Response roomName : " + roomName + "\n");

        return roomName;
    }

    @PostMapping(value = "/room_code", produces = "application/json; charset=utf8")
    public String responseRoomCode(@RequestBody Map<String, Object> param) {
        final Optional<String> optional = Optional.ofNullable(param.get("roomName").toString());
        final String roomName = optional.orElse("");
        final String roomCode;

        System.out.println("Received roomName : " + roomName);

        roomCode = connectService.getCodeByName(new RoomDto(roomName)).get(0).get("room_code").toString();
        System.out.println("Response roomCode : " + roomCode + "\n");

        return roomCode;
    }

    @PostMapping(value = "/room_exist", produces = "application/json; charset=utf8")
    public String checkRoomExist(@RequestBody Map<String, Object> param) {
        final Optional<String> optional = Optional.ofNullable(param.get("roomName").toString());
        final String roomName = optional.orElse("");
        final List<Map<String, Object>> queryResult = connectService.findRoom(new RoomDto(roomName));
        final boolean roomExist;

        roomExist = !queryResult.isEmpty();

        if (roomExist) {
            System.out.println("Warning! Room name " + roomName + " duplicated!(from checkRoomExist)\n");
        } else {
            System.out.println("Room name " + roomName + " is admitted!(from checkRoomExist)\n");
        }

        return Boolean.toString(roomExist);
    }
}
