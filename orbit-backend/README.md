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
 - `POST /api/flow/pods` - Create a new pod. NOTE: the create endpoint returns a plaintext `joinCode` in the response (and the on-chain state stores only the hash). Use the returned `joinCode` for "scan-to-join" flows — the server stores only the hashed join code on-chain as metadata.
 
Important: the plaintext join code behavior

- The plaintext `joinCode` is returned only once by `POST /api/flow/pods` when you create a pod. The backend generates the code and returns it to the caller (so you can display it or encode it in a QR). The server does not persist the plaintext join code in pod metadata.
- The on-chain `PodContract` stores only a hash of the join code (`joinHash`), so retrieving pod data from the chain (or via `GET /api/flow/pods` and `GET /api/flow/pods/:podID`) will NOT reveal the plaintext code. If you need to re-issue a join code, call the appropriate server endpoint to generate a new code and update the on-chain hash (this requires contract permissions).

See `API_DOCUMENTATION.md` for full request/response examples and details on the on-chain hashed join-code flow.

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
