const { createServer } = require('vite');
const path = require('path');

async function startDevServer() {
  try {
    const server = await createServer({
      configFile: path.resolve(__dirname, 'vite.config.js'),
      mode: 'development'
    });
    
    await server.listen();
    
    console.log('Server started successfully!');
    console.log(`Local: ${server.config.server.https ? 'https' : 'http'}://${server.config.server.host}:${server.config.server.port}`);
    
  } catch (error) {
    console.error('Failed to start server:', error);
    process.exit(1);
  }
}

startDevServer();