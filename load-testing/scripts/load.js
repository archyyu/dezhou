import { sleep } from 'k6';
import * as api from '../libs/api.js';
import * as utils from '../libs/utils.js';

export const options = {
  stages: [
    { duration: '30s', target: 20 }, // ramp up
    { duration: '1m', target: 20 },  // stay
    { duration: '30s', target: 0 },  // ramp down
  ],
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<1000'],
  },
};

export default function () {
  const name = `load_user_${utils.randomString(6)}`;
  const password = 'password';

  const user = api.login(name, password);
  if (user) {
    const roomTypes = api.getRoomTypeList(user.token);
    if (roomTypes && roomTypes.length > 0) {
      const typeId = roomTypes[0].roomtypeid;
      api.getRoomList(user.token, typeId);
      
      // Occasionally create a room
      if (Math.random() < 0.1) {
        api.createRoom(user.token, typeId, `LoadRoom_${utils.randomString(4)}`);
      }
    }
  }
  
  sleep(utils.randomInt(1, 3));
}
