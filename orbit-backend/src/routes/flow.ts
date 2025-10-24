import { Router } from 'express';
import { getBlockInfo, getAccountInfo } from '../controllers/flowController';

const router = Router();

// Get current block information
router.get('/block', getBlockInfo);

// Get account information by address
router.get('/account/:address', getAccountInfo);

export default router;
