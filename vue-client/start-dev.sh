#!/bin/bash

echo "Starting Vue client..."
cd vue-client

# Check if node_modules exist
if [ ! -d "node_modules" ]; then
    echo "Installing dependencies..."
    npm install
fi

# Try to start the dev server
echo "Starting Vite dev server..."
npm run dev -- --port 5173 --host