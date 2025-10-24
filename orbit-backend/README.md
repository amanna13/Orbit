# Orbit Backend

Node.js + TypeScript backend server with Flow blockchain integration.

## Setup

1. Install dependencies:
```bash
npm install
```

2. Create a `.env` file based on `.env.example`:
```bash
copy .env.example .env
```

3. Update the `.env` file with your Flow configuration.

## Development

Run the development server with hot reload:
```bash
npm run dev
```

## Build

Compile TypeScript to JavaScript:
```bash
npm run build
```

## Production

Start the production server:
```bash
npm start
```

## Scripts

- `npm run dev` - Start development server with hot reload
- `npm run build` - Build TypeScript to JavaScript
- `npm start` - Start production server
- `npm run lint` - Run ESLint
- `npm run format` - Format code with Prettier

## API Endpoints

### Health Check
- `GET /health` - Returns server health status

### Flow Blockchain
- `GET /api/flow/block` - Get current Flow blockchain block information
- `GET /api/flow/account/:address` - Get Flow account information by address

## Project Structure

```
src/
├── controllers/      # Request handlers
│   └── flowController.ts
├── services/         # Business logic
│   └── flowService.ts
├── routes/           # API routes
│   ├── health.ts
│   └── flow.ts
├── utils/            # Utility functions
│   └── logger.ts
└── index.ts          # Entry point
```
