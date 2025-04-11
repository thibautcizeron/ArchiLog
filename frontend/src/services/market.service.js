// frontend/src/services/market.service.js
import authService from './auth.service';

const API_URL = 'http://localhost:8085/api/market';

class MarketService {
  async getAllListings() {
    try {
      const response = await fetch(API_URL, {
        method: 'GET',
        headers: this._getHeaders(),
      });

      if (!response.ok) {
        throw new Error('Failed to fetch market listings');
      }

      return await response.json();
    } catch (error) {
      console.error('Error fetching market listings:', error);
      throw error;
    }
  }

  async sellCard(cardId, price) {
    const user = authService.getCurrentUser();
    if (!user) {
      throw new Error('User not authenticated');
    }

    try {
      const sellData = {
        cardId: cardId,
        sellerId: user.userId,
        price: price
      };

      const response = await fetch(`${API_URL}/sell`, {
        method: 'POST',
        headers: this._getHeaders(),
        body: JSON.stringify(sellData),
      });

      if (!response.ok) {
        throw new Error('Failed to sell card');
      }

      return true;
    } catch (error) {
      console.error('Error selling card:', error);
      throw error;
    }
  }

  async buyCard(cardId) {
    const user = authService.getCurrentUser();
    if (!user) {
      throw new Error('User not authenticated');
    }

    try {
      const response = await fetch(`${API_URL}/buy/${cardId}?buyerId=${user.userId}`, {
        method: 'POST',
        headers: this._getHeaders(),
      });

      if (!response.ok) {
        throw new Error('Failed to buy card');
      }

      return true;
    } catch (error) {
      console.error('Error buying card:', error);
      throw error;
    }
  }

  async getUserTransactions() {
    const user = authService.getCurrentUser();
    if (!user) {
      throw new Error('User not authenticated');
    }

    try {
      const response = await fetch(`${API_URL}/transactions/${user.userId}`, {
        method: 'GET',
        headers: this._getHeaders(),
      });

      if (!response.ok) {
        throw new Error('Failed to fetch user transactions');
      }

      return await response.json();
    } catch (error) {
      console.error('Error fetching user transactions:', error);
      throw error;
    }
  }

  _getHeaders() {
    const headers = {
      'Content-Type': 'application/json',
    };

    const token = authService.getToken();
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    return headers;
  }
}

export default new MarketService();