package ac.linker.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import ac.linker.dto.JoinDto;
import ac.linker.dto.RoomDto;

import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface ConnectMapper {

    List<Map<String, Object>> getRoomByCode(RoomDto room);

    List<Map<String, Object>> getCodeByName(RoomDto room);

    List<Map<String, Object>> findRoom(RoomDto room);

    void insertRoom(RoomDto room);

    void insertJoin(JoinDto join);

    void updateRoomCode(RoomDto room);

    void updateRoomJoin(JoinDto join);

    void updateRoomNewJoin(JoinDto join);

    void updateRoomLeave(RoomDto room);

    Map<String, Object> getRoomPresent(RoomDto room);
}
