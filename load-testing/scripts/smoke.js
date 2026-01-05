import { sleep } from 'k6';
import * as api from '../libs/api.js';
import * as utils from '../libs/utils.js';

export const options = {
  vus: 1,
  duration: '10s',
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<500'],
  },
};

export default function () {
  const name = `smoke_user_${utils.randomString(6)}`;
  const password = 'password';

  const user = api.login(name, password);
  if (user) {
    api.getRoomTypeList(user.token);
  }
  
  sleep(1);
}
