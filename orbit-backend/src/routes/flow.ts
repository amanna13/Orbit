import { Router } from 'express';
import {
  handleCreatePod,
  handleJoinPod,
  handleLeavePod,
  handleGetAllPods,
  handleGetPodDetails,
  handleTransferBetweenPods,
  handleGetPodsByAddress,
  handleDepositToPod,
  handleDistributePodFunds,
  handleExecutePodDisbursement,
  handleGetFlowBalance,
} from '../controllers/flowController';

const router = Router();

// Pod Management Routes
router.post('/pods', handleCreatePod);
router.post('/pods/join', handleJoinPod);
router.post('/pods/leave', handleLeavePod);
router.get('/pods', handleGetAllPods);
router.get('/pods/:podID', handleGetPodDetails);
router.get('/pods/by-address/:address', handleGetPodsByAddress);

// Account Routes
router.get('/balance/:address', handleGetFlowBalance);

// Transfer & Deposit Routes
router.post('/transfer', handleTransferBetweenPods);
router.post('/deposit', handleDepositToPod);

// Distribution Routes
router.post('/distribute', handleDistributePodFunds);
router.post('/disburse', handleExecutePodDisbursement);

export default router;
