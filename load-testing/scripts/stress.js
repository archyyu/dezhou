import { sleep } from 'k6';
import * as api from '../libs/api.js';
import * as utils from '../libs/utils.js';

export const options = {
  stages: [
    { duration: '1m', target: 50 },  // below normal load
    { duration: '2m', target: 100 }, // normal load
    { duration: '2m', target: 200 }, // around breaking point
    { duration: '2m', target: 300 }, // beyond breaking point
    { duration: '2m', target: 0 },   // ramp down
  ],
  thresholds: {
    http_req_failed: ['rate<0.05'],   // allow more errors during stress
    http_req_duration: ['p(95)<3000'],
  },
};

export default function () {
  const name = `stress_user_${utils.randomString(6)}`;
  const password = 'password';

  const user = api.login(name, password);
  if (user) {
    const roomTypes = api.getRoomTypeList(user.token);
    if (roomTypes && roomTypes.length > 0) {
      const typeId = roomTypes[0].roomtypeid;
      api.getRoomList(user.token, typeId);
      
      // Heavy room creation pressure
      if (Math.random() < 0.3) {
        api.createRoom(user.token, typeId, `StressRoom_${utils.randomString(4)}`);
      }
    }
  }
  
  sleep(1);
}
