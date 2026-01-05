function fn() {
  var env = karate.env; // get system property 'karate.env'
  karate.log('karate.env system property was:', env);
  
  if (!env) {
    env = 'dev'; // a custom 'intelligent' default
  }
  
  var config = {
    env: env,
    baseUrl: 'http://localhost:8080',
    apiVersion: 'v1',
    
    // Database configuration for test data setup
    db: {
      url: 'jdbc:mysql://localhost:33306/texasholder',
      username: 'root',
      password: 'aida87014999',
      driver: 'com.mysql.cj.jdbc.Driver'
    }
  };
  
  if (env == 'docker') {
    config.baseUrl = 'http://texasholder-app:8080';
    config.db.url = 'jdbc:mysql://mysql:3306/texasholder';
  }
  
  // don't waste time waiting for a connection or element to appear
  // if it's not there after 5 seconds
  karate.configure('connectTimeout', 5000);
  karate.configure('readTimeout', 5000);
  
  return config;
}