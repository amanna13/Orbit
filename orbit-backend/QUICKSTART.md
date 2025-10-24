# Quick Start Guide

## Initial Setup

1. **Install Dependencies** (Already completed)
   ```bash
   npm install
   ```

2. **Configure Environment**
   - Copy `.env.example` to `.env`
   - Update the Flow configuration values in `.env`

3. **Start Development Server**
   ```bash
   npm run dev
   ```
   The server will start on http://localhost:4000

## Testing the API

### Health Check
```bash
curl http://localhost:4000/health
```

Expected response:
```json
{
  "ok": true,
  "timestamp": "2025-10-24T...",
  "uptime": 123.456
}
```

### Flow Blockchain Integration

Get current block:
```bash
curl http://localhost:4000/api/flow/block
```

Get account info:
```bash
curl http://localhost:4000/api/flow/account/0x01
```

## Available Scripts

- `npm run dev` - Start development server with hot reload
- `npm run build` - Compile TypeScript to JavaScript
- `npm start` - Run production build
- `npm run lint` - Check code with ESLint
- `npm run format` - Format code with Prettier

## Project Features

✅ TypeScript configuration
✅ Express.js server
✅ Flow blockchain integration (@onflow/fcl)
✅ Environment variable management (dotenv)
✅ Code linting (ESLint)
✅ Code formatting (Prettier)
✅ Hot reload in development (ts-node-dev)
✅ Structured folders (controllers, services, routes, utils)
✅ Error handling middleware
✅ Logger utility
✅ Health check endpoint
✅ Sample Flow API endpoints

## Next Steps

1. Update `.env` with your actual Flow configuration
2. Add your custom routes in `src/routes/`
3. Implement business logic in `src/services/`
4. Create controllers in `src/controllers/`
5. Add utility functions in `src/utils/`

Happy coding! 🚀
