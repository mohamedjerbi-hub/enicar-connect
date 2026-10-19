import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '10s', target: 20 },  // Ramp-up to 20 virtual users
    { duration: '30s', target: 50 },  // Peak load: 50 concurrent students
    { duration: '10s', target: 0 },   // Graceful ramp-down
  ],
  thresholds: {
    // Non-functional SLA requirements (BNF)
    http_req_duration: ['p(95)<500'], // 95% of requests must complete in under 500ms
    http_req_failed: ['rate<0.01'],    // Failure rate under 1%
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8081';

export default function () {
  const loginPayload = JSON.stringify({
    email: 'etudiant@enicar.tn',
    password: 'password123',
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
    },
  };

  // 1. Authentication flow
  const loginRes = http.post(`${BASE_URL}/api/auth/login`, loginPayload, params);
  const loginSuccess = check(loginRes, {
    'login status 200': (r) => r.status === 200,
    'jwt token received': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body && body.token !== undefined;
      } catch (e) {
        return false;
      }
    },
  });

  let token = '';
  if (loginSuccess) {
    try {
      token = JSON.parse(loginRes.body).token;
    } catch (e) {}
  }

  const authParams = {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
    },
  };

  // 2. Fetch Social Feed (Read-heavy workload)
  const feedRes = http.get(`${BASE_URL}/api/posts`, authParams);
  check(feedRes, {
    'feed status 200': (r) => r.status === 200,
    'feed response < 500ms': (r) => r.timings.duration < 500,
  });

  // 3. Mentorship Catalog
  const mentorRes = http.get(`${BASE_URL}/api/mentorship/mentors`, authParams);
  check(mentorRes, {
    'mentors status 200': (r) => r.status === 200,
  });

  // 4. Events Calendar
  const eventsRes = http.get(`${BASE_URL}/api/events`, authParams);
  check(eventsRes, {
    'events status 200': (r) => r.status === 200,
  });

  sleep(1);
}
