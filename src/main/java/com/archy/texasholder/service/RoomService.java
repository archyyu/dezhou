package com.archy.texasholder.service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.archy.texasholder.entity.GameActionDB;
import com.archy.texasholder.entity.GameRoomDB;
import com.archy.texasholder.entity.Player;
import com.archy.texasholder.entity.RoomDB;
import com.archy.texasholder.entity.puker.PukerHelp;
import com.archy.texasholder.entity.room.GameRoom;
import com.archy.texasholder.entity.room.PukerGame;
import com.archy.texasholder.repo.GameActionDBRepository;
import com.archy.texasholder.repo.GameRoomDBRepository;
import com.archy.texasholder.repo.RoomDBRepository;

import jakarta.annotation.Resource;

@Service
public class RoomService{

    @Resource
    private RoomDBRepository roomDBRepository;

	@Resource
	private GameRoomDBRepository gameRoomDBRepository;

	@Resource
	private GameActionDBRepository gameActionDBRepository;

	@Resource
	private PukerHelp pukerHelp;

	@Resource
	private WebSocketService webSocketService;

	private final Logger logger = LoggerFactory.getLogger(getClass());

    private Map<Integer,PukerGame> roomsMap = new ConcurrentHashMap<Integer,PukerGame>();
	
	private Map<Integer, Player> usersMap = new ConcurrentHashMap<Integer, Player>();

	public Optional<RoomDB> getRoomById(int roomId){
        return roomDBRepository.findById(roomId);
    }

	public List<RoomDB> getRoomTypeList() {
		return this.roomDBRepository.findAll();
	}
	
	public PukerGame getRoom(Integer id)
	{
		if (id == null) {
			return null;
		}
		return roomsMap.get(id);
	}
	
	public GameRoom getRoomByName(String roomName)
	{
		for (GameRoom room : this.roomsMap.values()) {
			if (room.getName().equals(roomName)) {
				return room;
			}
		}
		return null;
	}
	
	public GameRoom createGameRoom(String uid, String userName, int roomTypeId, String roomName) {

		RoomDB roomDB = this.roomDBRepository.findById(roomTypeId).orElse(null);

		GameRoomDB gameRoomDB = GameRoomDB.builder().build();
		try {
			BeanUtils.copyProperties(gameRoomDB, roomDB);
		} catch (Exception ex) {
			logger.info("creategameroom", ex);
		}

		gameRoomDB.setAccount(userName);
		gameRoomDB.setCreatetime(System.currentTimeMillis()/1000);
		this.gameRoomDBRepository.save(gameRoomDB);

		PukerGame gameRoom = new PukerGame(gameRoomDB, this.webSocketService, this.pukerHelp);
		gameRoom.setCreator(userName);
		gameRoom.setName(roomName);

		this.roomsMap.put(gameRoom.getRoomid(), gameRoom);

		return gameRoom;
	}

	@Scheduled(fixedRate = 5000)
	public void tickAllRoom() {

		long now = System.currentTimeMillis();

		this.roomsMap.values().forEach( item -> { item.beatHeart(now); });

	}
	
	public void addRoom(PukerGame room)
	{
		roomsMap.put(room.getRoomid(),room);
	}
	
	public List<PukerGame> getRoomList()
	{
		return new ArrayList<PukerGame>(this.roomsMap.values());
	}

	public List<PukerGame> getRoomListByTypeId(int roomTypeId) {
		return this.roomsMap.values().stream().filter(item -> item.getRoomTypeId() == roomTypeId).collect(Collectors.toList());
	}
	
	public int destroyRoom(GameRoom room)
	{
		roomsMap.remove(room.getRoomid());
		return 0;
	}
	
	public void destroyRoom(int roomId)
	{
		roomsMap.remove(roomId);
	}
	
	public Player getUserByUserId(int userId)
	{
		return this.usersMap.get(userId);
	}
	
	public void addUser(Player user)
	{
		this.usersMap.put(user.getUid(),user);
	}
	
	public void removeUser(int userId)
	{
		this.usersMap.remove(userId);
	}

	
	public void UserLogout(int userId)
	{
		this.usersMap.remove(userId);
	}
	
	public Player[] userToArray()
	{
		return this.usersMap.values().toArray(new Player[this.usersMap.size()]);
	}



}