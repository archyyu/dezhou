import http from 'k6/http';
import { check } from 'k6';

export const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export function login(name, password) {
  const url = `${BASE_URL}/api/v1/user/login?name=${name}&password=${password}`;
  const res = http.post(url);
  
  check(res, {
    'login status is 200': (r) => r.status === 200,
  });

  if (res.status === 200 && res.json().data) {
    return {
      token: res.json().data.token,
      uid: res.json().data.user.uid,
    };
  }
  return null;
}

export function getRoomTypeList(token) {
  const url = `${BASE_URL}/api/v1/room/roomTypeList`;
  const params = {
    headers: {
      'Authorization': `Bearer ${token}`,
    },
  };
  const res = http.get(url, params);
  return res.json().data;
}

export function getRoomList(token, roomTypeId) {
  const url = `${BASE_URL}/api/v1/room/${roomTypeId}/list`;
  const params = {
    headers: {
      'Authorization': `Bearer ${token}`,
    },
  };
  const res = http.get(url, params);
  return res.json().data;
}

export function createRoom(token, roomTypeId, roomName) {
  const url = `${BASE_URL}/api/v1/room/create/${roomTypeId}/${roomName}`;
  const params = {
    headers: {
      'Authorization': `Bearer ${token}`,
    },
  };
  const res = http.post(url, {}, params);
  return res.json().data;
}

export function joinRoom(token, roomId) {
  const url = `${BASE_URL}/api/v1/room/${roomId}/join`;
  const params = {
    headers: {
      'Authorization': `Bearer ${token}`,
    },
  };
  return http.post(url, {}, params);
}

export function sitDown(token, roomId, seatId, buyIn) {
  const url = `${BASE_URL}/api/v1/game/${roomId}/action/6`; // CMD_SITDOWN
  const params = {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
  };
  const body = {
    sid: seatId.toString(),
    cb: buyIn.toString(),
  };
  return http.post(url, JSON.stringify(body), params);
}

export function gameAction(token, roomId, cmd, body = {}) {
  const url = `${BASE_URL}/api/v1/game/${roomId}/action/${cmd}`;
  const params = {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
  };
  // Ensure all values in body are strings as the backend uses Map<String, String>
  const stringBody = {};
  for (const key in body) {
    stringBody[key] = body[key].toString();
  }
  return http.post(url, JSON.stringify(stringBody), params);
}

export function getGameState(token, roomId) {
  const url = `${BASE_URL}/api/v1/game/${roomId}/state`;
  const params = {
    headers: {
      'Authorization': `Bearer ${token}`,
    },
  };
  const res = http.get(url, params);
  return res.json().data;
}
