import { Router } from 'express';
import {
  handleCreatePod,
  handleJoinPod,
  handleLeavePod,
  handleGetAllPods,
  handleGetPodDetails,
  handleTransferBetweenPods,
} from '../controllers/flowController';

const router = Router();

// Pod Management Routes
router.post('/pods', handleCreatePod);
router.post('/pods/join', handleJoinPod);
router.post('/pods/leave', handleLeavePod);
router.get('/pods', handleGetAllPods);
router.get('/pods/:podID', handleGetPodDetails);

// Transfer Route
router.post('/transfer', handleTransferBetweenPods);

export default router;
