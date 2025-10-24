import express, { Application, Request, Response, NextFunction } from 'express';
import bodyParser from 'body-parser';
import dotenv from 'dotenv';
import healthRoute from './routes/health';
import flowRoute from './routes/flow';
import forteRoute from './routes/forte';
import { logger } from './utils/logger';
// Import flowConfig to ensure FCL is configured on startup
import '../utils/flowConfig';

// Load environment variables
dotenv.config();

const app: Application = express();
const PORT = process.env.PORT || 4000;

// Middleware
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

// Routes
app.use('/health', healthRoute);
app.use('/api/flow', flowRoute);
app.use('/api/forte', forteRoute);

// Error handling middleware
app.use((err: Error, _req: Request, res: Response, _next: NextFunction) => {
  logger.error('Unhandled error:', err);
  res.status(500).json({
    error: 'Internal Server Error',
    message: process.env.NODE_ENV === 'development' ? err.message : undefined,
  });
});

// 404 handler
app.use((req: Request, res: Response) => {
  res.status(404).json({
    error: 'Not Found',
    message: `Route ${req.method} ${req.path} not found`,
  });
});

// Start server
app.listen(PORT, () => {
  logger.info(`Server is running on port ${PORT}`);
  logger.info(`Environment: ${process.env.NODE_ENV || 'development'}`);
  logger.info(`Flow Access Node: ${process.env.FLOW_ACCESS_NODE || 'Not configured'}`);
});

export default app;
