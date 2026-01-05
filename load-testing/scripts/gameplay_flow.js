import { sleep, group } from 'k6';
import * as api from '../libs/api.js';
import * as utils from '../libs/utils.js';

export const options = {
  stages: [
    { duration: '1m', target: 5 },  // Slow ramp up for gameplay
    { duration: '3m', target: 5 },
    { duration: '1m', target: 0 },
  ],
  thresholds: {
    http_req_failed: ['rate<0.05'],
    http_req_duration: ['p(95)<2000'],
  },
};

export default function () {
  const name = `player_${utils.randomString(6)}`;
  const password = 'password';

  group('Full Gameplay Simulation', function () {
    // 1. Auth
    const user = api.login(name, password);
    if (!user) return;

    sleep(1);

    // 2. Discover
    const roomTypes = api.getRoomTypeList(user.token);
    if (!roomTypes || roomTypes.length === 0) return;
    
    // Pick the first type (usually Texas Holdem)
    const typeId = roomTypes[0].roomtypeid || 1;
    const rooms = api.getRoomList(user.token, typeId);
    
    let roomId;
    if (!rooms || rooms.length === 0 || Math.random() < 0.2) {
      const room = api.createRoom(user.token, typeId, `Table_${utils.randomString(4)}`);
      if (room) roomId = room.roomid;
    } else {
      roomId = rooms[0].roomid;
    }

    if (!roomId) return;

    // 3. Join & Sit
    api.joinRoom(user.token, roomId);
    sleep(1);
    
    // Sit at a random seat 1-8
    const seatId = utils.randomInt(1, 8);
    api.sitDown(user.token, roomId, seatId, 1000); 
    
    sleep(2);

    // 4. Game Cycle
    // Since we don't have real-time notification in k6 easily, 
    // we poll the state and take action if it's our turn.
    for (let i = 0; i < 10; i++) {
        const state = api.getGameState(user.token, roomId);
        if (!state) break;

        // In a real load test, we could check if state.currentPlayer.uid == user.uid
        // But for generic pressure, we just try some actions
        
        const rand = Math.random();
        if (rand < 0.3) {
            api.gameAction(user.token, roomId, '1'); // Look card
        } else if (rand < 0.6) {
            api.gameAction(user.token, roomId, '3'); // Follow
        } else if (rand < 0.8) {
            api.gameAction(user.token, roomId, '2', { cb: 20 }); // Raise
        } else if (rand < 0.9) {
            api.gameAction(user.token, roomId, '4'); // Fold
            break;
        }

        sleep(utils.randomInt(3, 7));
    }

    // 5. Cleanup
    api.gameAction(user.token, roomId, '106'); // Leave
  });
  
  sleep(5);
}
