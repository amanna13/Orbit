import { Router } from 'express';
import {
  handleCreateScheduledPayment,
  handleCancelScheduledPayment,
  handleTriggerImmediatePayment,
  handleListScheduledPayments,
  handleGetScheduledPaymentDetails,
  handleUpdateScheduledPayment,
} from '../controllers/forteController';

const router = Router();

// Scheduled Payment Routes
router.post('/scheduled-payments', handleCreateScheduledPayment);
router.get('/scheduled-payments', handleListScheduledPayments);
router.get('/scheduled-payments/:scheduleID', handleGetScheduledPaymentDetails);
router.patch('/scheduled-payments/:scheduleID', handleUpdateScheduledPayment);
router.delete('/scheduled-payments/:scheduleID', handleCancelScheduledPayment);

// Immediate Payment Route
router.post('/immediate-payment', handleTriggerImmediatePayment);

export default router;
