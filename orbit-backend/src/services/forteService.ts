import axios, { AxiosError } from 'axios';
import dotenv from 'dotenv';
import { logger } from '../utils/logger';

// Load environment variables
dotenv.config();

const FORTE_API_BASE_URL = process.env.FORTE_API_BASE_URL || 'http://localhost:3000/api/forte';
const FORTE_API_KEY = process.env.FORTE_API_KEY || '';

// Configure axios instance with default headers
const forteClient = axios.create({
  baseURL: FORTE_API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${FORTE_API_KEY}`,
  },
  timeout: 10000, // 10 seconds
});

interface ForteResponse {
  success: boolean;
  data?: unknown;
  message: string;
}

/**
 * Create a scheduled payment between two pods
 * @param payerPodID - ID of the payer pod
 * @param receiverPodID - ID of the receiver pod
 * @param amount - Amount to transfer
 * @param intervalDays - Interval in days between payments
 * @returns Scheduled action ID and status
 */
export const createScheduledPayment = async (
  payerPodID: number,
  receiverPodID: number,
  amount: number,
  intervalDays: number
): Promise<ForteResponse> => {
  try {
    logger.info(
      `Creating scheduled payment: Payer=${payerPodID}, Receiver=${receiverPodID}, Amount=${amount}, Interval=${intervalDays} days`
    );

    const response = await forteClient.post('/scheduled-actions', {
      actionType: 'transferBetweenPods',
      metadata: {
        payerPodID,
        receiverPodID,
        amount,
        intervalDays,
      },
      interval: {
        days: intervalDays,
      },
    });

    logger.info(
      `Scheduled payment created successfully. Schedule ID: ${response.data?.scheduleID || response.data?.id}`
    );

    return {
      success: true,
      data: response.data,
      message: 'Scheduled payment created successfully',
    };
  } catch (error) {
    const axiosError = error as AxiosError;
    logger.error('Failed to create scheduled payment', error);

    return {
      success: false,
      data: axiosError.response?.data,
      message: axiosError.message || 'Failed to create scheduled payment',
    };
  }
};

/**
 * Cancel a scheduled payment
 * @param scheduleID - ID of the scheduled payment to cancel
 * @returns Cancellation status
 */
export const cancelScheduledPayment = async (scheduleID: string): Promise<ForteResponse> => {
  try {
    logger.info(`Canceling scheduled payment: ${scheduleID}`);

    const response = await forteClient.delete(`/scheduled-actions/${scheduleID}`);

    logger.info(`Scheduled payment ${scheduleID} canceled successfully`);

    return {
      success: true,
      data: response.data,
      message: 'Scheduled payment canceled successfully',
    };
  } catch (error) {
    const axiosError = error as AxiosError;
    logger.error(`Failed to cancel scheduled payment ${scheduleID}`, error);

    return {
      success: false,
      data: axiosError.response?.data,
      message: axiosError.message || 'Failed to cancel scheduled payment',
    };
  }
};

/**
 * Trigger an immediate payment between two pods
 * @param payerPodID - ID of the payer pod
 * @param receiverPodID - ID of the receiver pod
 * @param amount - Amount to transfer
 * @returns Transaction status and details
 */
export const triggerImmediatePayment = async (
  payerPodID: number,
  receiverPodID: number,
  amount: number
): Promise<ForteResponse> => {
  try {
    logger.info(
      `Triggering immediate payment: Payer=${payerPodID}, Receiver=${receiverPodID}, Amount=${amount}`
    );

    const response = await forteClient.post('/immediate-action', {
      actionType: 'transferBetweenPods',
      metadata: {
        payerPodID,
        receiverPodID,
        amount,
      },
    });

    logger.info(
      `Immediate payment triggered successfully. Transaction ID: ${response.data?.transactionId || response.data?.id}`
    );

    return {
      success: true,
      data: response.data,
      message: 'Immediate payment triggered successfully',
    };
  } catch (error) {
    const axiosError = error as AxiosError;
    logger.error('Failed to trigger immediate payment', error);

    return {
      success: false,
      data: axiosError.response?.data,
      message: axiosError.message || 'Failed to trigger immediate payment',
    };
  }
};

/**
 * List all scheduled payments related to a specific pod
 * @param podID - ID of the pod to query
 * @returns List of scheduled payments
 */
export const listScheduledPayments = async (podID: number): Promise<ForteResponse> => {
  try {
    logger.info(`Listing scheduled payments for pod: ${podID}`);

    const response = await forteClient.get('/scheduled-actions', {
      params: {
        podID,
      },
    });

    logger.info(`Retrieved ${response.data?.length || 0} scheduled payments for pod ${podID}`);

    return {
      success: true,
      data: response.data,
      message: 'Scheduled payments retrieved successfully',
    };
  } catch (error) {
    const axiosError = error as AxiosError;
    logger.error(`Failed to list scheduled payments for pod ${podID}`, error);

    return {
      success: false,
      data: axiosError.response?.data,
      message: axiosError.message || 'Failed to retrieve scheduled payments',
    };
  }
};

/**
 * Get details of a specific scheduled payment
 * @param scheduleID - ID of the scheduled payment
 * @returns Scheduled payment details
 */
export const getScheduledPaymentDetails = async (scheduleID: string): Promise<ForteResponse> => {
  try {
    logger.info(`Getting details for scheduled payment: ${scheduleID}`);

    const response = await forteClient.get(`/scheduled-actions/${scheduleID}`);

    logger.info(`Retrieved details for scheduled payment ${scheduleID}`);

    return {
      success: true,
      data: response.data,
      message: 'Scheduled payment details retrieved successfully',
    };
  } catch (error) {
    const axiosError = error as AxiosError;
    logger.error(`Failed to get details for scheduled payment ${scheduleID}`, error);

    return {
      success: false,
      data: axiosError.response?.data,
      message: axiosError.message || 'Failed to retrieve scheduled payment details',
    };
  }
};

/**
 * Update a scheduled payment
 * @param scheduleID - ID of the scheduled payment to update
 * @param updates - Updated metadata
 * @returns Updated scheduled payment
 */
export const updateScheduledPayment = async (
  scheduleID: string,
  updates: {
    amount?: number;
    intervalDays?: number;
  }
): Promise<ForteResponse> => {
  try {
    logger.info(`Updating scheduled payment: ${scheduleID}`, updates);

    const response = await forteClient.patch(`/scheduled-actions/${scheduleID}`, {
      metadata: updates,
      ...(updates.intervalDays && {
        interval: {
          days: updates.intervalDays,
        },
      }),
    });

    logger.info(`Scheduled payment ${scheduleID} updated successfully`);

    return {
      success: true,
      data: response.data,
      message: 'Scheduled payment updated successfully',
    };
  } catch (error) {
    const axiosError = error as AxiosError;
    logger.error(`Failed to update scheduled payment ${scheduleID}`, error);

    return {
      success: false,
      data: axiosError.response?.data,
      message: axiosError.message || 'Failed to update scheduled payment',
    };
  }
};
